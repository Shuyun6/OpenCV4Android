package shuyun.opencv4android.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BaseActivity extends AppCompatActivity {

    private RxPermissions rxPermissions;
    private ThreadPoolExecutor pool;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxPermissions = new RxPermissions(this);
    }

    protected RxPermissions getRxPermissions(){
        return rxPermissions;
    }

    protected ThreadPoolExecutor getPool(){
        if (null == pool) {
            pool = new ThreadPoolExecutor(4, 4, 60L,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<>(), (ThreadFactory) Thread::new);
        }
        return pool;
    }

    protected void toast(String content) {
        runOnUiThread(() -> Toast.makeText(this, content, Toast.LENGTH_SHORT).show());
    }

}
