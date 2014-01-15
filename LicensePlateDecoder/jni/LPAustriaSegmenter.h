//
//  LPAustriaSegmenter.h
//  License Plate Recognizer
//
//  Created by Mario Orozco Borrego on 23/05/13.
//  Copyright (c) 2013 Mario Orozco Borrego. All rights reserved.
//

#ifndef __License_Plate_Recognizer__LPAustriaSegmenter__
#define __License_Plate_Recognizer__LPAustriaSegmenter__

#include <iostream>
#include "LPStandarSegmenter.h"

class LPAustriaSegmenter : public LPStandarSegmenter {
    
    
public:
    
    LPAustriaSegmenter(Mat inputV) : LPStandarSegmenter(inputV){
        
    }
    
};


#endif /* defined(__License_Plate_Recognizer__LPAustriaSegmenter__) */
