package shuyun.opencv4android.util;

/**
 * A customized Log class for this project
 * @Author shuyun
 * @Create at 2018/11/13 0013 22:29
 * @Update at 2018/11/13 0013 22:29
*/
public class Log {

    private static final boolean DEBUG = true;
    private static final String TAG = "OpenCV4Android";

    public static void e(String content) {
        if (DEBUG) {
            android.util.Log.e(TAG, content);
        }
    }

    public static void i(String content) {
        if (DEBUG) {
            android.util.Log.i(TAG, content);
        }
    }

    public static void w(String content) {
        if (DEBUG) {
            android.util.Log.w(TAG, content);
        }
    }

}
