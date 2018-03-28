package lional.example.com.objectdetect;

import android.content.Context;
import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.ml.ANN_MLP;
import org.opencv.ml.Ml;

import java.io.File;

/**
 * Created by skysoft on 2018/3/7.
 */

public class ANNDetectForDigit {
    private static final String TAG = "ANNDetectForDigit";
    private static final String FILE_NAME = "_ANN_train_mode.xml";

    private static ANNDetectForDigit annDetect;
    private ANN_MLP ann;
    private double[] layerSize = new double[]{400, 25, 10};
    private Mat layerSizeMat;
    private TermCriteria termCriteria;
    private Context mContext;
    private String fileName;

    private ANNDetectForDigit(Context context) {
        mContext = context;
        fileName = context.getFilesDir() + FILE_NAME;
        Log.d(TAG, "ann file name: " + fileName);

        create();
    }

    private void create() {
        layerSizeMat = new Mat(1, 3, CvType.CV_32FC1);
        layerSizeMat.put(0, 0, 400);
        layerSizeMat.put(0, 1, 25);
        layerSizeMat.put(0, 2, 10);

        Log.d(TAG, "layerSizeMat row: " + layerSizeMat.rows() + ", cols: " + layerSizeMat.cols());

        termCriteria = new TermCriteria(TermCriteria.MAX_ITER | TermCriteria.EPS,
                1000, 0.001);

        ann = ANN_MLP.create();
        ann.setLayerSizes(layerSizeMat);
        ann.setActivationFunction(ANN_MLP.SIGMOID_SYM, 1, 1);
        ann.setTrainMethod(ANN_MLP.BACKPROP);
        ann.setTermCriteria(termCriteria);
        ann.setBackpropWeightScale(0.1);
        ann.setBackpropMomentumScale(0.1);
    }

    public static ANNDetectForDigit get(Context context) {
        if (annDetect == null) {
            annDetect = new ANNDetectForDigit(context);
        }

        return annDetect;
    }

    public void trainData(Mat data, Mat response) {
        File file = new File(fileName);
        if (!file.exists()) {
            ann.train(data, Ml.ROW_SAMPLE, response);
            ann.save(fileName);
        }
    }

    public int predictData(Mat data) {
        Mat result = new Mat();
        double max_pr = 0;
        int value = 0;
        ANN_MLP annPredict = ANN_MLP.load(fileName);

        data.convertTo(data, CvType.CV_32F);
        annPredict.predict(data, result, 0);

        for (int i = 0; i < result.cols(); i++) {
            for (double m: result.get(0, i)) {
                if (m > max_pr) {
                    max_pr = m;
                    value = i;
                }
            }
        }

        return value;
    }

    public boolean isTrainFileExist() {
        File file = new File(fileName);
        return file.exists();
    }
}
