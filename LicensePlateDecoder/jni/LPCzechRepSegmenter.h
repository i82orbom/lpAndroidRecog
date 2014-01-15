//
//  LPCzechRepSegmenter.h
//  License Plate Recognizer
//
//  Created by Mario Orozco Borrego on 23/05/13.
//  Copyright (c) 2013 Mario Orozco Borrego. All rights reserved.
//

#ifndef __License_Plate_Recognizer__LPCzechRepSegmenter__
#define __License_Plate_Recognizer__LPCzechRepSegmenter__

#include <iostream>
#include "LPStandarSegmenter.h"

class LPCzechRepSegmenter : public LPStandarSegmenter {
    
    
public:
    
    LPCzechRepSegmenter(Mat inputV) : LPStandarSegmenter(inputV){
        
    }
    
};

#endif /* defined(__License_Plate_Recognizer__LPCzechRepSegmenter__) */
