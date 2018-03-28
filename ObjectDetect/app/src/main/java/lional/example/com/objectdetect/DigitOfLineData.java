package lional.example.com.objectdetect;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by lional on 18-2-26.
 */

public class DigitOfLineData {
    private static final String TAG = "DigitOfLineData";

    private int mLine;
    private double[] lineofPixData;
    Mat lineMatData;
    private double maxPix = 0.0;

    public void selectLineData(int line) {
        CvLibrary cvLibrary = new CvLibrary();
        lineofPixData = cvLibrary.getData(line);
        mLine = line;

        findLineMaxValue();
        translateToLineMat();
    }

    private void findLineMaxValue() {
        DecimalFormat decimalFormat = new DecimalFormat("#.0000");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);

        for (int i = 0; i < lineofPixData.length; i++) {
            lineofPixData[i] = Math.abs(Double.parseDouble(decimalFormat.format(lineofPixData[i])));
            if (lineofPixData[i] > maxPix) {
                maxPix = lineofPixData[i];
            }
        }
    }


    public void translateToLineMat() {
        lineMatData = new Mat(1, lineofPixData.length, CvType.CV_8UC1);
        DecimalFormat pecent = new DecimalFormat("#.000");
        pecent.setRoundingMode(RoundingMode.HALF_UP);
        double temp;

        for (int i = 0; i < lineofPixData.length; i++) {
            temp = lineofPixData[i] / maxPix;

            temp = temp * 255.0;
            temp = Double.parseDouble(pecent.format(temp));
            lineMatData.put(0, i, temp);
        }
    }

    public Mat getLineMatData() {
        return  lineMatData;
    }

    public Mat translateToMat() {
        Mat resultMat = new Mat(20, 20, CvType.CV_8UC1);

        int elements = 0;
        for (int i = 0; i < resultMat.cols(); i++) {
            for (int j = 0; j < resultMat.rows(); j++) {
                resultMat.put(j, i, lineMatData.get(0, elements));
                elements++;
            }
        }

        return resultMat;
    }
}
