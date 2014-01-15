//
//  LPStandarSegmenterAbstraction.h
//  License Plate Recognizer
//
//  Created by Mario Orozco Borrego on 22/05/13.
//  Copyright (c) 2013 Mario Orozco Borrego. All rights reserved.
//

#ifndef __License_Plate_Recognizer__LPStandarSegmenter__
#define __License_Plate_Recognizer__LPStandarSegmenter__

#include <iostream>
#include "LPStandarSegmenterImpl.h"

#include <opencv2/opencv.hpp>

using namespace cv;

class LPStandarSegmenter {
 
protected:
    LPStandarSegmenterImpl oImpl;
    Mat input;
    
public:
    
    LPStandarSegmenter(Mat inputV){
        oImpl = LPStandarSegmenterImpl();
        input = inputV;
    }
    
    inline void run(){
        oImpl.run(input);
    }
    
    inline vector<Mat> getResult(){
        return oImpl.getResult();
    }
    
  
    inline void preprocess(Mat *input){
        oImpl.preprocess(input);
    }
    inline void filter(Mat *input){
        oImpl.filter(input);
    }
    inline void thresholdM(Mat *input){
        oImpl.thresholdM(input);
    }
    inline void calculateContours(Mat *input){
        oImpl.calculateContours(input);
    }
    inline void processForResult(Mat *input){
        oImpl.processForResult(input);
    }

    
};

#endif /* defined(__License_Plate_Recognizer__LPStandarSegmenter__) */
