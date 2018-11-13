package shuyun.opencv4android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;

import shuyun.opencv4android.util.Log;

/**
 * A base activity for OpenCV init
 * @Author shuyun
 * @Create at 2018/11/13 0013 22:30
 * @Update at 2018/11/13 0013 22:30
*/
public class CVBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLoadOpenCV();
    }

    protected void initLoadOpenCV(){
        boolean success = OpenCVLoader.initDebug();
        if (success) {
            Log.e("init opencv success");
        } else {
            Log.e("init opencv failed");
        }
    }

}
