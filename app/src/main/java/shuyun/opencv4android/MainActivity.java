package shuyun.opencv4android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    private ImageView ivTips;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivTips = findViewById(R.id.ivTips);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        stringFromJNI(bitmap);
//        ivTips.setImageBitmap(bitmap);
        //Before using OpenCV java code, must init OpenCV like this method
        //then invoke OpenCV relative methods
        initLoadOpenCV();
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGRA2GRAY);
        Utils.matToBitmap(dst, bitmap);
        ivTips.setImageBitmap(bitmap);
        src.release();
        dst.release();
    }

    private void initLoadOpenCV(){
        boolean success = OpenCVLoader.initDebug();
        if (success) {
            Log.e("test", "init opencv success");
        } else {
            Log.e("test", "init opencv failed");
        }
    }

    /**
     * invoke native method to process bitmap
     * @param bitmap
     * @return
     */
    public native String processImage(Bitmap bitmap);


}
