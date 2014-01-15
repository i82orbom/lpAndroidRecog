//
//  LPStandarSegmenterImpl.cpp
//  License Plate Recognizer
//
//  Created by Mario Orozco Borrego on 22/05/13.
//  Copyright (c) 2013 Mario Orozco Borrego. All rights reserved.
//

#include "LPStandarSegmenterImpl.h"
#include "gabor.h"
#include "Rectangle.h"


void LPStandarSegmenterImpl :: run(Mat input){
    preprocess(&input);
    filter(&input);
    thresholdM(&input);
    calculateContours(&input);
    processForResult(&input);
}


void LPStandarSegmenterImpl::setResult(vector<Mat> v){
    result = v;
}


void LPStandarSegmenterImpl::initializeThresholds(){
    // THRESHOLDS
    THRESHOLD_BINARIZE = 151;
    SQUARE_RATIO = 3.2;
    MIN_SQUARE_HEIGHT_PERCENTAGE = 14.4;
    MIN_SQUARE_WIDTH_PERCENTAGE = 5;
    MAX_ALLOWED_HEIGHT_VARIATION = 7;
}




void LPStandarSegmenterImpl::preprocess(Mat *input){
    input->copyTo(inputCopy);
    cvtColor(*input, *input, CV_BGR2GRAY);
}
void LPStandarSegmenterImpl::filter(Mat *input){
    Mat kernel = mkKernel(21,1,95,0.69,21);
    *input = processGabor(*input,kernel,21);
}
void LPStandarSegmenterImpl::thresholdM(Mat *input){
    threshold(*input, *input, THRESHOLD_BINARIZE, 255, CV_THRESH_BINARY);
}
void LPStandarSegmenterImpl::calculateContours(Mat *input){
    Mat contourTemp; input->convertTo(contourTemp,CV_8UC1);
    findContours(contourTemp, _contours, _contourHierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_TC89_KCOS);
}

void LPStandarSegmenterImpl::processForResult(Mat *input){
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
    if ((f1/f2) < SQUARE_RATIO && ((height*100)/inputCopy.rows) > MIN_SQUARE_HEIGHT_PERCENTAGE && ((width*100)/inputCopy.cols) > MIN_SQUARE_WIDTH_PERCENTAGE){
            
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
    std::sort(listOfSquares.begin(),listOfSquares.end(), squareCompare);
    
    /** Second pass filter and result storing*/
    vector<Mat> resultToReturn;
    for (int i = 0; i < listOfSquares.size(); ++i){
        
        Rect squareGot = listOfSquares[i].getRect();
        
        /** Post filtering!!!!, the height of the square must be greather than the half of the tallest square found */
        /* This case occurs when we have Ä Ö Ü */
        // MAX_ALLOWED_HEIGHT_VARIATION = 7
        if (squareGot.height >= (maxHeight)/2){
            
            
            if (abs(maxHeight-squareGot.height) > MAX_ALLOWED_HEIGHT_VARIATION){
                // EXPAND SQUARE
                int idxToGet;
                if (i < listOfSquares.size() - 1){
                    // I can get the next one
                    idxToGet = i + 1;
                }
                else if (i == 0){
                    // Get the second one
                    idxToGet = 1;
                }
                else{
                    // Otherwise get the previous square
                    idxToGet = i - 1;
                }
                
                squareGot.y = listOfSquares[idxToGet].getRect().y - 4;
                squareGot.height = listOfSquares[idxToGet].getRect().height + 4;
            }
            
            Mat temp = inputCopy(squareGot);
            resultToReturn.push_back(temp);
        }
    }
    
    
    // Store the result in result
    setResult(resultToReturn);

}


bool LPStandarSegmenterImpl::squareCompare (Rectangle i, Rectangle j){
    return (i.getCenter().x < j.getCenter().x);
}

bool LPStandarSegmenterImpl::descendingCompare(vector<Point> i, vector<Point> j){
    return i.size() > j.size();
}


