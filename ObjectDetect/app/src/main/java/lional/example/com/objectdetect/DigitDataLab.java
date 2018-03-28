package lional.example.com.objectdetect;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by lional on 2018/3/5.
 */

public class DigitDataLab {
    private static final String TAG = "DigitDataLab";
    private static final int TRAIN_COUNTS = 5000;

    public static final int LOGISTIC_REGRESSION_ALGORITHM = 0;
    public static final int ANN_ALGORITHM = 1;

    private static ArrayList<DigitDataLab> digitDataLab = new ArrayList<DigitDataLab>();

    private DigitOfLineData mDigitOfLineData;
    private Mat data = new Mat();
    private Mat simple;
    private Mat response;
    private int mType;

    private DigitDataLab(int type) {
        mDigitOfLineData = new DigitOfLineData();
        mType = type;
        init(type);
    }

    public static  DigitDataLab get(int type) {
        if (digitDataLab.size() == 0) {
            digitDataLab.add(new DigitDataLab(LOGISTIC_REGRESSION_ALGORITHM));
            digitDataLab.add(new DigitDataLab(ANN_ALGORITHM));
        }

        return  digitDataLab.get(type);
    }

    private void init(int type) {
        int line, result;

        if (ANN_ALGORITHM == type) {
            response = new Mat(TRAIN_COUNTS, 10, CvType.CV_32FC1);
        } else if (LOGISTIC_REGRESSION_ALGORITHM == type){
            response = new Mat(TRAIN_COUNTS, 1, CvType.CV_32FC1);
        }

        Random random = new Random();
        for (int i = 0; i < TRAIN_COUNTS; i++) {
            if (TRAIN_COUNTS < 5000) {
                line = random.nextInt(5000);
            } else {
                line = i + 1;
            }

            mDigitOfLineData.selectLineData(line);
            simple = mDigitOfLineData.getLineMatData();
            data.push_back(simple.reshape(0, 1));

            result = line / 500;
            result = ((line % 500) == 0 ? (result - 1) : result);
            if (ANN_ALGORITHM == type) {
                for (int j = 0; j < 10; j++) {
                    if (j == result) {
                        response.put(i, j, 1);
                    } else {
                        response.put(i, j, 0);
                    }
                }
            } else if (LOGISTIC_REGRESSION_ALGORITHM == type) {
                response.put(i, 0, result);
            }
            simple.release();
        }
        data.convertTo(data, CvType.CV_32F);
    }

    public Mat testData() {
        return data;
    }

    public Mat testResponse() {
        return response;
    }
}
