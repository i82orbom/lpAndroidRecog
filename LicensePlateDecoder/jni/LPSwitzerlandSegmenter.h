//
//  LPSwitzerlandSegmenter.h
//  License Plate Recognizer
//
//  Created by Mario Orozco Borrego on 23/05/13.
//  Copyright (c) 2013 Mario Orozco Borrego. All rights reserved.
//

#ifndef __License_Plate_Recognizer__LPSwitzerlandSegmenter__
#define __License_Plate_Recognizer__LPSwitzerlandSegmenter__

#include <iostream>
#include "LPStandarSegmenter.h"
#include "LPRussiaSegmenterImpl.h"

class LPSwitzerlandSegmenter : public LPStandarSegmenter {
    
    LPRussiaSegmenterImpl ownImpl; /** The russian segmenter it's valid for switzerland license plates */

public:
    
    LPSwitzerlandSegmenter(Mat inputV) : LPStandarSegmenter(inputV){
        ownImpl = LPRussiaSegmenterImpl();

    }
    
    inline void run(){
        oImpl.preprocess(&input);
        oImpl.filter(&input);
        ownImpl.thresholdM(&input);
        oImpl.calculateContours(&input);
        ownImpl.setInputCopy(oImpl.getInputCopy());
        ownImpl.setContours(oImpl.getContours());
        ownImpl.processForResult(&input);
        
    }
    
    inline vector<Mat> getResult(){
        return ownImpl.getResult();
    }

    
    
};


#endif /* defined(__License_Plate_Recognizer__LPSwitzerlandSegmenter__) */
