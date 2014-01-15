//
//  LPFranceSegmenter.h
//  License Plate Recognizer
//
//  Created by Mario Orozco Borrego on 23/05/13.
//  Copyright (c) 2013 Mario Orozco Borrego. All rights reserved.
//

#ifndef __License_Plate_Recognizer__LPFranceSegmenter__
#define __License_Plate_Recognizer__LPFranceSegmenter__

#include <iostream>
#include "LPStandarSegmenter.h"
#include "LPFranceSegmenterImpl.h"

class LPFranceSegmenter : public LPStandarSegmenter {
    
   
protected:
    // Possible own implementation if the process it's really different from the base class
    LPFranceSegmenterImpl ownImpl;
    
public:
    
    LPFranceSegmenter(Mat inputV) : LPStandarSegmenter(inputV){
        ownImpl = LPFranceSegmenterImpl();
    }
    
    void run(){
        ownImpl.run(input);
    }

    // If overriding is not done, it will return the result from the standard implementation
    inline vector<Mat> getResult(){
    	vector<Mat> result =  ownImpl.getResult();
    	/*for (int i = 0; i < result.size(); ++i){
    		cvtColor(result[i], result[i], CV_BGR2GRAY);

    		Mat new_image = Mat::zeros(result[i].size(), result[i].type());

    		Mat sub_mat = Mat::ones(result[i].size(), result[i].type())*255;

    		subtract(sub_mat, result[i], new_image);
    		result[i] = new_image;
    	}*/
    	return result;
    }

    
    
};

#endif /* defined(__License_Plate_Recognizer__LPFranceSegmenter__) */
