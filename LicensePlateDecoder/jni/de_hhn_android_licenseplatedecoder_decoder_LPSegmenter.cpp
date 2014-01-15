#include <jni.h>
#include "de_hhn_android_licenseplatedecoder_decoder_LPSegmenter.h"
#include <opencv2/opencv.hpp>
#include <android/log.h>

#include "LPAustriaSegmenter.h"
#include "LPCzechRepSegmenter.h"
#include "LPFranceSegmenter.h"
#include "LPGermanySegmenter.h"
#include "LPIrelandSegmenter.h"
#include "LPItalySegmenter.h"
#include "LPPolandSegmenter.h"
#include "LPRomaniaSegmenter.h"
#include "LPSlovakiaSegmenter.h"
#include "LPRussiaSegmenter.h"
#include "LPSwitzerlandSegmenter.h"
#include "LPTurkeySegmenter.h"
#include "LPUkSegmenter.h"

#define APPNAME "lprecognizer"

using namespace cv;

/*
 * Class:     de_hhn_android_licenseplatedecoder_decoder_LPSegmenter
 * Method:    process
 * Signature: ()[J
 */
JNIEXPORT jlongArray JNICALL Java_de_hhn_android_licenseplatedecoder_decoder_LPSegmenter_process
  (JNIEnv *env, jobject obj){

	/** Get input address mat from java class */
	jclass diz = env->GetObjectClass(obj);
	jfieldID inputAddrFieldID = env->GetFieldID(diz,"nativeInputAddr","J");
	jlong inputAddr = env->GetLongField(obj, inputAddrFieldID);

	jfieldID countryCodeFieldID = env->GetFieldID(diz,"countryCode","I");
	jint countryCode = env->GetIntField(obj,countryCodeFieldID);
	/****/

	Mat *inputImage = (Mat*)inputAddr;
	vector<Mat> result;
	__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Country Code got: " + countryCode);

	switch(countryCode){

	case 1:{ /** GERMANY, COUNTRY CODE: D */
		LPGermanySegmenter germany = LPGermanySegmenter(*inputImage);
		germany.run();
		result = germany.getResult();
		break;
	}
	case 2:{ /** AUSTRIA, COUNTRY CODE: A */
		LPAustriaSegmenter austria = LPAustriaSegmenter(*inputImage);
		austria.run();
		result = austria.getResult();
		break;
	}
	case 4:{ /** FRANCE, COUNTRY CODE: F */
		LPFranceSegmenter france = LPFranceSegmenter(*inputImage);
		france.run();
		result = france.getResult();
		break;
	}
	case 5:{ /** CZECH REPUBLIC, COUNTRY CODE: CZ */
		LPCzechRepSegmenter czech = LPCzechRepSegmenter(*inputImage);
		czech.run();
		result = czech.getResult();
		break;
	}
	case 6:{ /** POLAND, COUNTRY CODE: PL */
		LPPolandSegmenter poland = LPPolandSegmenter(*inputImage);
		poland.run();
		result = poland.getResult();
		break;
	}
	case 7:{ /** UK, COUNTRY CODE: GB/UK */
		LPUkSegmenter uk = LPUkSegmenter(*inputImage);
		uk.run();
		result = uk.getResult();
		break;
	}
	case 8:{ /** ITALY, COUNTRY CODE: I */
		LPItalySegmenter italy = LPItalySegmenter(*inputImage);
		italy.run();
		result = italy.getResult();
		break;
	}
	case 9:{ /** IRELAND, COUNTRY CODE: IRL */
		LPIrelandSegmenter ireland = LPIrelandSegmenter(*inputImage);
		ireland.run();
		result = ireland.getResult();
		break;
	}
	case 10:{ /** SLOVAKIA, COUNTRY CODE: SK */
		LPSlovakiaSegmenter slovakia = LPSlovakiaSegmenter(*inputImage);
		slovakia.run();
		result = slovakia.getResult();
		break;
	}
	case 11:{ /** TURKEY, COUNTRY CODE: TR */
		LPTurkeySegmenter turkey = LPTurkeySegmenter(*inputImage);
		turkey.run();
		result = turkey.getResult();
		break;
	}
	case 13:{ /** ROMANIA, COUNTRY CODE: RO */
		LPRomaniaSegmenter romania = LPRomaniaSegmenter(*inputImage);
		romania.run();
		result = romania.getResult();
		break;
	}
	case 3:{ /** SWITZERLAND, COUNTRY CODE: CH */
		LPSwitzerlandSegmenter switzerland = LPSwitzerlandSegmenter(*inputImage);
		switzerland.run();
		result = switzerland.getResult();
		break;
	}
	case 12:{
		LPRussiaSegmenter russia = LPRussiaSegmenter(*inputImage);
		russia.run();
		result = russia.getResult();
		break;
	}
	default: /** DEFAULT CASE */

		break;
	}

	int amountOfChars = result.size();

	if (amountOfChars == 0){
		__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Got 0 chars!, returning NULL");
		return NULL;
	}


	__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Got %d chars!",amountOfChars);

	/** Prepare return values */
	jsize returnArraySize = amountOfChars;
	jlong *tempArray = (jlong*)malloc(sizeof(jlong)*returnArraySize);

	for (int i = 0; i < amountOfChars; ++i){
		Mat *tempMat = new Mat();
		*tempMat = result[i];
		tempArray[i] = (long)(tempMat);
	}

	jlongArray retArray = env->NewLongArray(amountOfChars);
	env->SetLongArrayRegion(retArray,0,amountOfChars,tempArray);

	return retArray;
}

