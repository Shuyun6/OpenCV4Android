package shuyun.opencv4android.module.camera;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import io.reactivex.android.schedulers.AndroidSchedulers;
import shuyun.opencv4android.R;
import shuyun.opencv4android.common.CVBaseActivity;
import shuyun.opencv4android.util.Log;

public class Camera2Activity extends CVBaseActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase cameraBridgeViewBase;
    private Mat mRgba, mGray;
    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity_camera2);
        cameraBridgeViewBase = findViewById(R.id.cameraView);
        cameraBridgeViewBase.setCvCameraViewListener(this);
//        cameraBridgeViewBase.setCameraIndex(0);
        cameraBridgeViewBase.enableFpsMeter();
        getRxPermissions()
                .request(Manifest.permission.CAMERA)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        cameraBridgeViewBase.enableView();
                    } else {
                        toast("NO Permission!");
                    }
                });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null != cameraBridgeViewBase)
            cameraBridgeViewBase.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mGray = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
//        if (mAbsoluteFaceSize == 0) {
//            int height = mGray.rows();
//            if (Math.round(height * mRelativeFaceSize) > 0) {
//                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
//            }
//            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
//        }
//
//        MatOfRect faces = new MatOfRect();
//        if (mDetectorType == JAVA_DETECTOR) {
//            if (mJavaDetector != null)
//                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2,
//                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
//        }
//        else if (mDetectorType == NATIVE_DETECTOR) {
//            if (mNativeDetector != null)
//                mNativeDetector.detect(mGray, faces);
//        }
//        else {
//            Log.e("Detection method is not selected!");
//        }
//
//        Rect[] facesArray = faces.toArray();
//        for (int i = 0; i < facesArray.length; i++)
//            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);

        return mRgba;
    }
}
