//
//  LPTurkeySegmenter.h
//  License Plate Recognizer
//
//  Created by Mario Orozco Borrego on 23/05/13.
//  Copyright (c) 2013 Mario Orozco Borrego. All rights reserved.
//

#ifndef __License_Plate_Recognizer__LPTurkeySegmenter__
#define __License_Plate_Recognizer__LPTurkeySegmenter__

#include <iostream>

#include "LPStandarSegmenter.h"


class LPTurkeySegmenter : public LPStandarSegmenter {
    
    
public:
    
    LPTurkeySegmenter(Mat inputV) : LPStandarSegmenter(inputV){
        
    }
    
};

#endif /* defined(__License_Plate_Recognizer__LPTurkeySegmenter__) */
