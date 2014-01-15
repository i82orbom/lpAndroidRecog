//
//  LPIrelandSegmenter.h
//  License Plate Recognizer
//
//  Created by Mario Orozco Borrego on 23/05/13.
//  Copyright (c) 2013 Mario Orozco Borrego. All rights reserved.
//

#ifndef __License_Plate_Recognizer__LPIrelandSegmenter__
#define __License_Plate_Recognizer__LPIrelandSegmenter__

#include <iostream>

#include "LPStandarSegmenter.h"


class LPIrelandSegmenter : public LPStandarSegmenter {
    
    
public:
    
    LPIrelandSegmenter(Mat inputV) : LPStandarSegmenter(inputV){
        
    }
    
};

#endif /* defined(__License_Plate_Recognizer__LPIrelandSegmenter__) */
