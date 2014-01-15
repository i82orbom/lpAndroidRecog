LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

include /Users/psylock/OpenCV-2.4.5-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE  := LicensePlateDecoder

define all-cpp-files-under
$(patsubst ./%, %, \
  $(shell cd $(LOCAL_PATH) ; \
          find $(1) -name "*.cpp" -and -not -name ".*" -and -not -name "leptonica/" -and -not -name "tesseract/") \
 )
endef


LOCAL_SRC_FILES := $(call all-cpp-files-under, .)
LOCAL_LDLIBS +=  -llog -ldl 
LOCAL_EXPORT_LDLIBS := $(call host-path,$(LOCAL_PATH)/libs/$(TARGET_ARCH_ABI)/libsupc++.a)
CXXFLAGS += -Wno-psabi
LOCAL_CFLAGS += -Wno-psabi -g -Wall 


include $(BUILD_SHARED_LIBRARY)
