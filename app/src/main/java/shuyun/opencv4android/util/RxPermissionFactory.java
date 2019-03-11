package shuyun.opencv4android.util;

import android.support.v4.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.ref.WeakReference;

public class RxPermissionFactory {

    private static volatile WeakReference<FragmentActivity> weakContext;
    private static volatile RxPermissions rxPermissions;

    public static RxPermissions from(FragmentActivity context) {
        if (null == weakContext) {
            synchronized (RxPermissions.class) {
                if(null == weakContext){
                    weakContext = new WeakReference<>(context);
                }
            }
        }
        if (null == rxPermissions) {
            synchronized (RxPermissions.class) {
                if (null == rxPermissions) {
                    rxPermissions = new RxPermissions(weakContext.get());
                }
            }
        }
        return rxPermissions;
    }

}
