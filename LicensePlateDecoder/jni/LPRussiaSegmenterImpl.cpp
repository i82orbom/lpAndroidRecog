//
//  LPRussiaSegmenterImpl.cpp
//  License Plate Recognizer
//
//  Created by Mario Orozco Borrego on 09/06/13.
//  Copyright (c) 2013 Mario Orozco Borrego. All rights reserved.
//

#include "LPRussiaSegmenterImpl.h"


void LPRussiaSegmenterImpl::thresholdM(Mat *input){
    threshold(*input, *input, THRESHOLD_BINARIZE, 255, CV_THRESH_BINARY);
}

void LPRussiaSegmenterImpl::processForResult(Mat *input){
    
    /**********************************/
    /******* PROCESS CONTOURS *********/
    /**********************************/
    
    vector<Rectangle> listOfSquares;
    double maxHeight = 0;
    int maxXcoordinate = 0;
    
    /** First pass filter */
    for (int i = 0 ; i < _contours.size(); ++i){
        Rect square = boundingRect(_contours[i]);
        // FILTER SOMEHOW
        double height, width;
        height = square.height;
        width = square.width;
        
        double f1,f2;
        /** We need in f1 the biggest, for avoiding problems by comparing with the RATIO THRESHOLD */
        if (height > width){
            f1 = height;
            f2 = width;
        }
        else{
            f1 = width;
            f2 = height;
        }

        /**
         *  Three constraints, the ratio of the square must be less than the max allowed square ratio
         *                   , the height must represent a certain percentage of the whole heigth 14.4%
         *                   , the width must represent a certain percentage of the whole width 5%
         */
      
         if( (f1/f2) < SQUARE_RATIO && ((height*100)/input->rows)> MIN_SQUARE_HEIGHT_PERCENTAGE && ((width*100)/input->cols) > MIN_SQUARE_WIDTH_PERCENTAGE){
             
             if (maxHeight < height){
                 maxHeight = height;
                 maxXcoordinate = square.y;
             }
             
             
             Moments mnt = moments(_contours[i]);
             double xCentroid = mnt.m10/mnt.m00;
             double yCentroid = mnt.m01/mnt.m00;
             Point center; center.x = xCentroid; center.y = yCentroid;
             
             Rectangle rectangle = Rectangle(square,center);
             listOfSquares.push_back(rectangle);
         }
    }
    
    /** Sort them */
    std::sort(listOfSquares.begin(),listOfSquares.end(),squareCompare);
    
    /** Second pass filter and result storing*/
    vector<Mat> resultToReturn;
    int count = 0;
    
    for (int i = 0; i < listOfSquares.size(); ++i){
        
        Rect squareGot = listOfSquares[i].getRect();
        
        if (squareGot.height >= (maxHeight)/2){
            
            controlAndExpandRectangle(&squareGot);
            
            Mat temp = inputCopy(squareGot);
            
            resultToReturn.push_back(temp);
            count++;
            
        }
    }
    
    // Store the result in result
    setResult(resultToReturn);

}

void LPRussiaSegmenterImpl::initializeThresholds(){
    // THRESHOLDS
    THRESHOLD_BINARIZE = 92;
    SQUARE_RATIO = 2.4;
    MIN_SQUARE_HEIGHT_PERCENTAGE = 18.18;
    MIN_SQUARE_WIDTH_PERCENTAGE = 4;
}

void LPRussiaSegmenterImpl::controlAndExpandRectangle(Rect *rect){
    
    // Expand
    rect->x -= 3;
    if (rect->x < 0)
        rect->x = 0;
    rect->y -= 3;
    if (rect->y < 0)
        rect->y = 0;
    rect->width += 6;
    rect->height += 6;
    
    
    // Control
    if ( (rect->x + rect->width ) > inputCopy.cols)
        rect->width = inputCopy.cols - rect->x;
    
    if ( (rect->y + rect->height) > inputCopy.rows)
        rect->height = inputCopy.rows - rect->y;
    
}

