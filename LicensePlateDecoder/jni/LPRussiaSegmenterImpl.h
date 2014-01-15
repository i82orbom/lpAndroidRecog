//
//  LPRussiaSegmenterImpl.h
//  License Plate Recognizer
//
//  Created by Mario Orozco Borrego on 09/06/13.
//  Copyright (c) 2013 Mario Orozco Borrego. All rights reserved.
//

#ifndef __License_Plate_Recognizer__LPRussiaSegmenterImpl__
#define __License_Plate_Recognizer__LPRussiaSegmenterImpl__

#include <iostream>
#include <opencv2/opencv.hpp>
#include "LPStandarSegmenterImpl.h"


using namespace cv;

class LPRussiaSegmenterImpl : public LPStandarSegmenterImpl{
    // THRESHOLDS
    int THRESHOLD_BINARIZE;
    double SQUARE_RATIO;
    double MIN_SQUARE_HEIGHT_PERCENTAGE;
    double MIN_SQUARE_WIDTH_PERCENTAGE;
    int MAX_ALLOWED_HEIGHT_VARIATION;

    void initializeThresholds();
    void controlAndExpandRectangle(Rect *rect);

    Mat inputCopy;
    vector<vector<Point> > _contours;
    
protected:
    vector<Mat> result;
    
public:
    
    LPRussiaSegmenterImpl(){
        initializeThresholds();
    }
    
    void thresholdM(Mat *input);
    
    void processForResult(Mat *input);
    
    inline vector<Mat> getResult(){
        return result;
    }
    
    inline void setInputCopy(Mat *copy){
        inputCopy = *copy;
    }
    
    inline void setContours(vector<vector<Point> >* cont){
        _contours = *cont;
    }
    
    inline void setResult(vector<Mat> res){
        result = res;
    }
 
    
};

#endif /* defined(__License_Plate_Recognizer__LPRussiaSegmenterImpl__) */
