LOCAL_PATH := $(call my-dir)

#static decryptor library info
include $(CLEAR_VARS)
LOCAL_MODULE := decryption
LOCAL_SRC_FILES := prebuild/libsbs2emotivdecryptor_android.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_CPP_FEATURES := exceptions rtti
LOCAL_ARM_MODE := arm
include $(PREBUILT_STATIC_LIBRARY)

#wrapper info
include $(CLEAR_VARS)
LOCAL_MODULE    := androidEmotivDecryptor
LOCAL_SRC_FILES := DecryptorWrapper.cpp
LOCAL_STATIC_LIBRARIES := decryption
LOCAL_LDLIBS := -lstdc++ -lm -ldl -lc -landroid -llog
LOCAL_ARM_MODE := arm
include $(BUILD_SHARED_LIBRARY)