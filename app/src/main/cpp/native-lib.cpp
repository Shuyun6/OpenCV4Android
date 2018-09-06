#include <jni.h>
#include <string>

using namespace cv;

extern "C"
JNIEXPORT jstring JNICALL
Java_shuyun_opencv4android_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    Mat img;


    return env->NewStringUTF(hello.c_str());
}
