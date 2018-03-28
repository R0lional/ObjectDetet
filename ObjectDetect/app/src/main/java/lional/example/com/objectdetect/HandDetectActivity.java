package lional.example.com.objectdetect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorKNN;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

import java.util.ArrayList;

/**
 * Created by lional on 2018/2/8.
 */

public class HandDetectActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "HandDetectActivity";
    private static final Scalar ORANGE_MIN = new Scalar(10, 80, 80);
    private static final Scalar ORANGE_MAX = new Scalar(15, 150,255);
    private static final int MESSAGE_DEVICE_READY = 0;

    private CameraBridgeViewBase cameraBridgeViewBase;

    private Mat mask;
    private Mat erodeElement, dilateElement;
    private Mat show;
    private Mat hMat, sMat, vMat;
    private ArrayList<Mat> hsvList;
    private boolean isPreviewReady = false;
    private BackgroundSubtractorMOG2 mog2;
    private DetectHandle mHandler;

    private ArrayList<MatOfPoint> contourList = new ArrayList<MatOfPoint>();
    private Mat hierarchy = new Mat();

    @Override
    public void onCameraViewStarted(int width, int height) {
        mog2 = Video.createBackgroundSubtractorMOG2();
        show = new Mat();
        mask = new Mat();
        hMat = new Mat();
        sMat = new Mat();
        vMat = new Mat();
        hsvList = new ArrayList<Mat>();
        erodeElement = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(3,3));
        dilateElement = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(5, 5));
        show.setTo(new Scalar(0, 0, 0));
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        show = inputFrame.rgba();

        Imgproc.cvtColor(show, mask, Imgproc.COLOR_RGB2HSV);
        Core.split(mask, hsvList);
        Imgproc.threshold(hsvList.get(0), mask, 15, 255, Imgproc.THRESH_BINARY_INV);
        Imgproc.GaussianBlur(mask, mask, new Size(5, 5), 1);
        //Imgproc.threshold(hsvList.get(1), sMat, 70, 255, Imgproc.THRESH_BINARY);
        //Imgproc.threshold(hsvList.get(2), vMat, 190, 255, Imgproc.THRESH_BINARY_INV);
        //Core.bitwise_and(hMat, sMat, mask);
        //Core.bitwise_and(mask, vMat, mask);
        //Core.inRange(mask, ORANGE_MIN, ORANGE_MAX, mask);
        /*inputFrame.rgba().copyTo(show, mask);

        Imgproc.findContours(mask, contourList, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        double maxArea = 0;
        int maxIndex = 0;
        MatOfPoint convex;
        Log.d(TAG, "contourList size: " + contourList.size());
        for (int i = 0; i < contourList.size(); i++) {
            double temp = Imgproc.contourArea(contourList.get(i));
            if (maxArea < temp) {
                maxArea = temp;
                maxIndex = i;
            }
        }

        //Imgproc.convexHull(contourList.get(maxIndex), mask);
        Imgproc.drawContours(show, contourList, maxIndex, new Scalar(0,255,0));*/
        return mask;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handdetect);

        mHandler = new DetectHandle();
        cameraBridgeViewBase = findViewById(R.id.opencv_camera);
        cameraBridgeViewBase.setMaxFrameSize(720, 480);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

        cameraBridgeViewBase.enableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraBridgeViewBase.disableView();
    }

    protected class DetectHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DEVICE_READY:
                    Log.d(TAG, "MESSAGE_DEVICE_READY");
                    Toast.makeText(getApplicationContext(), "Preview ready~!", Toast.LENGTH_LONG).show();
                    isPreviewReady = true;
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
}
