#include <jni.h>
#include "de_hhn_android_licenseplatedecoder_decoder_CountryExtractor.h"
#include <opencv2/opencv.hpp>
#include <android/log.h>
#include "LPEuroCountryExtract.h"

#define APPNAME "lprecognizer"

using namespace cv;

/*
 * Class:     com_example_lprecognizer_ocr_CountryExtractor
 * Method:    process
 * Signature: ()[J
 */
JNIEXPORT jlongArray JNICALL Java_de_hhn_android_licenseplatedecoder_decoder_CountryExtractor_process
  (JNIEnv *env, jobject obj){

	/** Get input address mat from java class */
	jclass diz = env->GetObjectClass(obj);
	jfieldID inputAddrFieldID = env->GetFieldID(diz,"nativeInputAddr","J");
	jlong inputAddr = env->GetLongField(obj, inputAddrFieldID);

	jfieldID inputWithoutStripFieldID = env->GetFieldID(diz,"withoutStripInputAddr","J");
	jlong withoutStripAddr = env->GetLongField(obj,inputWithoutStripFieldID);
	/****/

	/** Create native mat object for input */
	Mat *inputImage = (Mat*)inputAddr;
	__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Got a [%d x %d] input", inputImage->rows, inputImage->cols);

	/** Process it */
	LPEuroCountryExtract countryExtract = LPEuroCountryExtract(inputImage);
	vector<Mat> countryChars = countryExtract.getCharacters();

	Mat *withoutStrip = (Mat*)withoutStripAddr;
	Mat *gotWithoutStrip = countryExtract.getCroppedWithoutStrip();
	gotWithoutStrip->copyTo(*withoutStrip);

	int amountOfChars = countryChars.size();

	if(amountOfChars == 0){
		__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Got 0 chars!, returning NULL");
		return NULL;
	}

	__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Got %d chars!",amountOfChars);

	/** Prepare return values */
	jsize returnArraySize = amountOfChars;
	jlong *tempArray = (jlong*)malloc(sizeof(jlong)*returnArraySize);

	for (int i = 0; i < amountOfChars; ++i){
		Mat *tempMat = new Mat();
		*tempMat = countryChars[i];
		tempArray[i] = (long)(tempMat);
	}

	jlongArray retArray = env->NewLongArray(amountOfChars);
	env->SetLongArrayRegion(retArray,0,amountOfChars,tempArray);

	return retArray;
}

