package shuyun.opencv4android.constant;

import android.app.Application;

import org.opencv.android.OpenCVLoader;

import shuyun.opencv4android.util.Log;

public class App extends Application {

    public static boolean mIsInitOpenCVSuccess = false;

    @Override
    public void onCreate() {
        super.onCreate();
        initLoadOpenCV();
    }

    protected void initLoadOpenCV(){
        mIsInitOpenCVSuccess = OpenCVLoader.initDebug();
        if (mIsInitOpenCVSuccess) {
            Log.e("init opencv success");
        } else {
            Log.e("init opencv failed");
        }
    }

}
