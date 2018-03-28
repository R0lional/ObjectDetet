package lional.example.com.objectdetect;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Random;

/**
 * Created by lional on 2018/2/23.
 */

public class DigitDetectActivity extends AppCompatActivity {
    private static final String TAG = "DigitDetectActivity";
    private static final int TRAIN_DIGIT_DATA = 1;
    private static final int PREDICT_DIGIT_DATA = 2;
    private static final int TRAIN_DATA_INIT = 3;
    private static final int INIT_COMPLETE = 4;
    private static int SCALE = 4;

    private ImageView digitView;
    private TextView textInfo;
    private DigitDetectHandle mHandler;
    private Mat digitMat;
    private Mat showMat;
    private int dataOfLine;

    public DigitOfLineData mDigitOfLineData;
    public DigitDataLab mLRDataLab;
    public DigitDataLab mANNDataLab;
    public Bitmap showImage;
    public Random random = new Random();
    public LoadingDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digitdetect);
        digitView = findViewById(R.id.digit_view);
        textInfo = findViewById(R.id.digit_info);

        mHandler = new DigitDetectHandle();
        mDigitOfLineData = new DigitOfLineData();
        DigitTrainThread  dataInitthread = new DigitTrainThread(TRAIN_DATA_INIT);
        dataInitthread.start();

        dialog = new LoadingDialog(this);
        dialog.show();
    }

    private void fillDigitData() {
        dataOfLine = random.nextInt(5000);
        mDigitOfLineData.selectLineData(dataOfLine);
        digitMat = mDigitOfLineData.translateToMat();
    }

    private void setImageShow() {
        Size dSize = new Size(digitMat.width() * SCALE, digitMat.height() * SCALE);
        Imgproc.resize(digitMat, showMat, dSize);
        Utils.matToBitmap(showMat, showImage);
        digitView.setImageBitmap(showImage);
        digitView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.digit_detect_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.digit_update:
                fillDigitData();
                setImageShow();

                DigitTrainThread thread = new DigitTrainThread(PREDICT_DIGIT_DATA);
                thread.start();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public class DigitTrainThread extends Thread {
        private LogisticRegressionForDigit LRTrain;
        private ANNDetectForDigit annTrain;
        private int type = 0;

        public DigitTrainThread(int msgType) {
            LRTrain = LogisticRegressionForDigit.get(getApplicationContext());
            annTrain = ANNDetectForDigit.get(getApplicationContext());
            type = msgType;
        }

        private void loadData() {
            Message msg = new Message();

            msg.what = TRAIN_DATA_INIT;
            mHandler.sendMessage(msg);
            if (!LRTrain.isTrainFileExist()) {
                mLRDataLab = DigitDataLab.get(DigitDataLab.LOGISTIC_REGRESSION_ALGORITHM);
            }

            if (!annTrain.isTrainFileExist()) {
                mANNDataLab = DigitDataLab.get(DigitDataLab.ANN_ALGORITHM);
            }
        }

        private void trainingData() {
            Message msg = new Message();

            mHandler.removeMessages(msg.what);
            msg.what = TRAIN_DIGIT_DATA;
            mHandler.sendMessage(msg);
            if (!LRTrain.isTrainFileExist()) {
                LRTrain.trainData(mLRDataLab.testData(), mLRDataLab.testResponse());
            }

            if (!annTrain.isTrainFileExist()) {
                annTrain.trainData(mANNDataLab.testData(), mANNDataLab.testResponse());
            }
        }

        private void predictData() {
            int LRresult, ANNResult;
            Message msg = new Message();

            LRresult = LRTrain.predictData(mDigitOfLineData.getLineMatData());
            ANNResult = annTrain.predictData(mDigitOfLineData.getLineMatData());
            msg.what = PREDICT_DIGIT_DATA;
            msg.arg1 = LRresult;
            msg.arg2 = ANNResult;
            mHandler.sendMessage(msg);
        }

        @Override
        public void run() {
            if (type == TRAIN_DIGIT_DATA) {
                trainingData();
            } else if (type == PREDICT_DIGIT_DATA){
                predictData();
            } else if (type == TRAIN_DATA_INIT) {
                loadData();
                trainingData();

                Message msg = new Message();
                msg.what = INIT_COMPLETE;
                mHandler.sendMessage(msg);
            }
        }
    }

    public class DigitDetectHandle extends Handler {
        private String message;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TRAIN_DATA_INIT:
                    message = "In Data Loading...";
                    break;
                case TRAIN_DIGIT_DATA:
                    message = "Data in Training...";
                    break;
                case PREDICT_DIGIT_DATA:
                    int l_info = msg.arg1;
                    int ann_info = msg.arg2;

                    message = "The Number from Logistic Regression is:" + l_info +
                                "\n" + "The Number from ANN is:" + ann_info;
                    break;
                case INIT_COMPLETE:
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    Log.d(TAG,"show digit view");
                    fillDigitData();
                    showMat = new Mat(/*digitMat.rows() * SCALE, digitMat.cols() * SCALE, CvType.CV_8UC1*/);
                    showImage = Bitmap.createBitmap(digitMat.width() * SCALE,
                            digitMat.height() * SCALE, Bitmap.Config.RGB_565);

                    setImageShow();

                    DigitTrainThread thread = new DigitTrainThread(PREDICT_DIGIT_DATA);
                    thread.start();
                    break;
                default:
                    message = "invaild operation!";
                    super.handleMessage(msg);
                    break;
            }

            if (dialog.isShowing()) {
                dialog.setDialogMessage(message);
            } else {
                setTextInfo(message);
            }
        }
    }

    public void setTextInfo(String info) {
        textInfo.setText(info);
    }
}
