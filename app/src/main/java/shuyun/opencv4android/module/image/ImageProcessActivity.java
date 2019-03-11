package shuyun.opencv4android.module.image;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import shuyun.opencv4android.R;
import shuyun.opencv4android.common.CVBaseActivity;
import shuyun.opencv4android.util.Log;

/**
 * A simple image processing activity
 * @Author shuyun
 * @Create at 2018/11/13 0013 22:31
 * @Update at 2018/11/13 0013 22:31
*/
public class ImageProcessActivity extends CVBaseActivity {

    private Bitmap bmpLena;
    private ImageView ivLena;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity_imageprocess);
        ivLena = findViewById(R.id.ivLena);
        bmpLena = BitmapFactory.decodeResource(getResources(), R.mipmap.img_lena);

        res2File(R.mipmap.img_lena)
                .observeOn(Schedulers.from(getPool()))
                .map(file -> Imgcodecs.imread(file.getPath()))
                .map(mat -> {
                    int width = mat.cols();
                    int height = mat.rows();
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Mat dst = new Mat();
                    Imgproc.cvtColor(mat, dst, Imgproc.COLOR_BGR2GRAY);
                    Utils.matToBitmap(dst, bitmap);
                    return bitmap;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> ivLena.setImageBitmap(bitmap), throwable -> Log.e(throwable.getMessage()));



    }

    @SuppressLint("CheckResult")
    private Observable<File> res2File(int resId){
        return getRxPermissions()
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .flatMap(aBoolean -> {
                    if (aBoolean) {
                        return Observable.just(resId);
                    } else {
                        toast("Failed to get permission");
                        return Observable.empty();
                    }
                })
                .map(integer -> BitmapFactory.decodeResource(getResources(), integer))
                .map(bitmap -> {
                    File file = new File(Environment.getExternalStorageDirectory()+"/img.jpg");
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                    bitmap.recycle();
                    return file;
                })
                .subscribeOn(Schedulers.from(getPool()));
    }




}
