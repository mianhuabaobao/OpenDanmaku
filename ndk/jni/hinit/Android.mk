LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := hinit
LOCAL_SRC_FILES := hinit.c
LOCAL_CFLAGS += -fPIE -pie
LOCAL_LDFLAGS += -fPIE -pie
#include $(BUILD_SHARED_LIBRARY)
include $(BUILD_EXECUTABLE)