#include <jni.h>
#include <string>
#include <android/bitmap.h>
#include <opencv2/opencv.hpp>
#include <android/log.h>

#define LOG_TAG "native-lib_JNI"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))

using namespace cv;

extern "C"
JNIEXPORT jstring JNICALL
Java_shuyun_opencv4android_MainActivity_processImage(
        JNIEnv *env,
        jobject /* this */,
        jobject bitmap) {
    std::string hello = "Hello from C++";

    AndroidBitmapInfo info;
    void *pixels;
    LOGD("start process bitmap");
    LOGE("start process bitmap(ERROR TEST)");
//    int res = AndroidBitmap_getInfo(env, bitmap, &info);

    CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
    CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
              info.format == ANDROID_BITMAP_FORMAT_RGB_565);
    CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
    CV_Assert(pixels);
    if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        Mat temp(info.height, info.width, CV_8UC4, pixels);
        Mat gray;
        cvtColor(temp, gray, COLOR_RGBA2GRAY);
        Canny(gray, gray, 125, 225);
        cvtColor(gray, temp, COLOR_GRAY2RGBA);
    } else {
        Mat temp(info.height, info.width, CV_8UC2, pixels);
        Mat gray;
        cvtColor(temp, gray, COLOR_RGB2GRAY);
        Canny(gray, gray, 125, 225);
        cvtColor(gray, temp, COLOR_GRAY2RGB);
    }
    AndroidBitmap_unlockPixels(env, bitmap);

    return env->NewStringUTF(hello.c_str());
}
