package lional.example.com.objectdetect;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.ml.LogisticRegression;
import org.opencv.ml.Ml;

import java.io.File;

/**
 * Created by skysoft on 2018/3/2.
 */

public class LogisticRegressionForDigit {
    private static final String TAG = "LRForDigit";
    private static final String FILE_NAME = "_train_mode.xml";

    private static LogisticRegressionForDigit lr;
    private String modeFile;
    private Context mContext;

    private LogisticRegressionForDigit(Context context) {
        mContext = context;
        modeFile = mContext.getFilesDir() + FILE_NAME;
        Log.d(TAG, "lr file name: " + modeFile);
    }

    public static LogisticRegressionForDigit get(Context context) {
        if (lr == null) {
            lr = new LogisticRegressionForDigit(context);
        }

        return lr;
    }

    public void trainData(Mat data, Mat response) {
            LogisticRegression lr = LogisticRegression.create();
            lr.setLearningRate(0.0001);
            lr.setIterations(500);
            lr.setRegularization(LogisticRegression.REG_L1);
            lr.setTrainMethod(LogisticRegression.MINI_BATCH);
            lr.setMiniBatchSize(500);


            lr.train(data, Ml.ROW_SAMPLE, response);
            lr.save(modeFile);
    }

    public int predictData(Mat data) {
        Mat test = new Mat();
        Mat response = new Mat();
        LogisticRegression predictLr = LogisticRegression.load(modeFile);


        test.push_back(data);
        test.convertTo(test, CvType.CV_32F);

        float result = predictLr.predict(test);
        Log.d(TAG, "predict result: " + result);

        predictLr.predict(test, response, 0);
        for (int i = 0; i < response.rows(); i++) {
            for (int j = 0; j < response.cols(); j++) {
                for (double m: response.get(i, j)) {
                    Log.d(TAG, "predict response: " + m + " at row:" + i + " col: " + j);
                }
            }
        }

        return (int) result;
    }

    public boolean isTrainFileExist() {
        File file = new File(modeFile);
        return file.exists();
    }
}
