#include <jni.h>
#include <string>

extern "C"
{
    #include <android/log.h>
    #include "digit/digit_data.h"

    #define TAG "digit_data"
    #define LOGD(...)   __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__);

    JNIEXPORT jdoubleArray JNICALL
    Java_lional_example_com_objectdetect_CvLibrary_getData(JNIEnv *env, jobject instance, jint line) {
        double data[400];
        jdoubleArray array = (*env).NewDoubleArray(400);
        digit_get(line, data);
        (*env).SetDoubleArrayRegion(array, 0, 400, data);

        return array;
    }
}