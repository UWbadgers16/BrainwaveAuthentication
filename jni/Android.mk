LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := decryptor
LOCAL_SRC_FILES := ../libs/libsbs2emotivdecryptor_android.a
include $(PREBUILT_STATIC_LIBRARY)