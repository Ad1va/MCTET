#include <jni.h>
#include <string.h>
#include <stdlib.h>
#include "include/easytier_ffi.h"

static jstring getJniErrorMsg(JNIEnv *env) {
    const char *msg = NULL;
    get_error_msg(&msg);
    if (msg == NULL) {
        return (*env)->NewStringUTF(env, "unknown error");
    }
    jstring result = (*env)->NewStringUTF(env, msg);
    free_string(msg);
    return result;
}

JNIEXPORT jint JNICALL
Java_com_easytier_EasyTier_nativeParseConfig(
    JNIEnv *env, jclass clazz, jstring cfgStr) {
    const char *cfg = (*env)->GetStringUTFChars(env, cfgStr, NULL);
    int ret = parse_config(cfg);
    (*env)->ReleaseStringUTFChars(env, cfgStr, cfg);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_easytier_EasyTier_nativeRunNetworkInstance(
    JNIEnv *env, jclass clazz, jstring cfgStr) {
    const char *cfg = (*env)->GetStringUTFChars(env, cfgStr, NULL);
    int ret = run_network_instance(cfg);
    (*env)->ReleaseStringUTFChars(env, cfgStr, cfg);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_easytier_EasyTier_nativeRetainNetworkInstance(
    JNIEnv *env, jclass clazz, jobjectArray instNames) {
    jsize length = (*env)->GetArrayLength(env, instNames);
    if (length == 0) {
        return retain_network_instance(NULL, 0);
    }
    const char **names = (const char **)malloc(sizeof(const char *) * length);
    jstring *jstrs = (jstring *)malloc(sizeof(jstring) * length);
    for (jsize i = 0; i < length; i++) {
        jstrs[i] = (jstring)(*env)->GetObjectArrayElement(env, instNames, i);
        names[i] = (*env)->GetStringUTFChars(env, jstrs[i], NULL);
    }
    int ret = retain_network_instance(names, (size_t)length);
    for (jsize i = 0; i < length; i++) {
        (*env)->ReleaseStringUTFChars(env, jstrs[i], names[i]);
    }
    free(names);
    free(jstrs);
    return ret;
}

JNIEXPORT jobjectArray JNICALL
Java_com_easytier_EasyTier_nativeCollectNetworkInfos(
    JNIEnv *env, jclass clazz) {
    /* First call with count=0 to get number of instances */
    int count = collect_network_infos(NULL, 0);
    if (count <= 0) {
        /* Return empty array */
        jclass strArrClass = (*env)->FindClass(env, "[Ljava/lang/String;");
        return (*env)->NewObjectArray(env, 0, strArrClass, NULL);
    }
    /* Allocate space for KeyValuePair array */
    KeyValuePair *infos = (KeyValuePair *)malloc(sizeof(KeyValuePair) * count);
    int actual = collect_network_infos(infos, (size_t)count);
    if (actual < 0) {
        free(infos);
        jclass strArrClass = (*env)->FindClass(env, "[Ljava/lang/String;");
        return (*env)->NewObjectArray(env, 0, strArrClass, NULL);
    }
    /* Build String[][actual], each row is [key, value] */
    jclass strArrClass = (*env)->FindClass(env, "[Ljava/lang/String;");
    jclass strClass = (*env)->FindClass(env, "java/lang/String");
    jobjectArray result = (*env)->NewObjectArray(env, actual, strArrClass, NULL);
    for (int i = 0; i < actual; i++) {
        jobjectArray row = (*env)->NewObjectArray(env, 2, strClass, NULL);
        (*env)->SetObjectArrayElement(env, row, 0,
            (*env)->NewStringUTF(env, infos[i].key ? infos[i].key : ""));
        (*env)->SetObjectArrayElement(env, row, 1,
            (*env)->NewStringUTF(env, infos[i].value ? infos[i].value : ""));
        (*env)->SetObjectArrayElement(env, result, i, row);
        if (infos[i].key) free_string(infos[i].key);
        if (infos[i].value) free_string(infos[i].value);
    }
    free(infos);
    return result;
}

JNIEXPORT jint JNICALL
Java_com_easytier_EasyTier_nativeSetTunFd(
    JNIEnv *env, jclass clazz, jstring instName, jint fd) {
    const char *name = (*env)->GetStringUTFChars(env, instName, NULL);
    int ret = set_tun_fd(name, (int)fd);
    (*env)->ReleaseStringUTFChars(env, instName, name);
    return ret;
}

JNIEXPORT jstring JNICALL
Java_com_easytier_EasyTier_nativeGetErrorMsg(
    JNIEnv *env, jclass clazz) {
    return getJniErrorMsg(env);
}
