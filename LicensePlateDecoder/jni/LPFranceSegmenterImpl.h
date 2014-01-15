//
//  LPFranceSegmenterImpl.h
//  License Plate Recognizer
//
//  Created by Mario Orozco Borrego on 08/06/13.
//  Copyright (c) 2013 Mario Orozco Borrego. All rights reserved.
//

#ifndef __License_Plate_Recognizer__LPFranceSegmenterImpl__
#define __License_Plate_Recognizer__LPFranceSegmenterImpl__

#include <iostream>
#include "LPStandarSegmenterImpl.h"
#include "Rectangle.h"

class LPFranceSegmenterImpl : public LPStandarSegmenterImpl {
    
    Scalar LOW_BLUETHRES;
    Scalar HIGH_BLUETHRES;
    float MINBLUEPERCENTAGE;
    int BINARIZE_THRESH;
    int MAX_CHARACTERS;
    float SQUARE_RATIO;

    Mat *_input;
    Mat *_croppedBlueStrip;
    Mat *_originalCroppedStrip;
    
    
public:
    LPFranceSegmenterImpl(){
        
    }
    
    void run(Mat input);

    void initializeThresholds();
    void cropBlueStrip(Mat *blueZoneMasking);
    static bool descendingCompare (vector<Point> i, vector<Point> j);
    static bool horizontalCompare(vector<Point>i, vector<Point>j);
    static bool horizontalCompareLeftRight(Rectangle i, Rectangle j);
    void extractCharacters(void);
    Rect* getRectCharFromContour(vector<Point> contour, Point * center);
    int testCharacters(Point** centers, int size);
    void controlAndExpandRectangle(Rect *rect);
    float getPercentageOfBlue(Mat*v);


};

#endif /* defined(__License_Plate_Recognizer__LPFranceSegmenterImpl__) */
