//
//  LPFranceSegmenterImpl.cpp
//  License Plate Recognizer
//
//  Created by Mario Orozco Borrego on 08/06/13.
//  Copyright (c) 2013 Mario Orozco Borrego. All rights reserved.
//

#include "LPFranceSegmenterImpl.h"
#include "gabor.h"
#include "Rectangle.h"


void LPFranceSegmenterImpl::run(Mat input){
    initializeThresholds();
    
    _input = new Mat();
    input.copyTo(*_input);
    
    Mat *_hsvImage = NULL;
    if (_hsvImage == NULL)
        _hsvImage = new Mat();
    cvtColor(input, *_hsvImage, CV_BGR2HSV);
    
    
    
    Mat *blueAreaMask = new Mat(_hsvImage->rows, _hsvImage->cols, _hsvImage->type());
    
    inRange(*_hsvImage, LOW_BLUETHRES,  HIGH_BLUETHRES, *blueAreaMask);
    
    bool _bluePresent = false;
    
    if (getPercentageOfBlue(blueAreaMask) >= MINBLUEPERCENTAGE)
        _bluePresent = true;
    
    if (_bluePresent){
        cropBlueStrip(blueAreaMask);
                
        cvtColor(*_croppedBlueStrip, *_croppedBlueStrip, CV_HSV2BGR);
        
        cvtColor(*_croppedBlueStrip, *_croppedBlueStrip, CV_BGR2GRAY);
        
        Mat kernel = mkKernel(7,1,95,0.69,21);
        *_croppedBlueStrip = processGabor(*_croppedBlueStrip, kernel, 21);
        
        (*_croppedBlueStrip).convertTo(*_croppedBlueStrip, CV_8UC1);
        
        dilate(*_croppedBlueStrip,*_croppedBlueStrip,NULL);
        dilate(*_croppedBlueStrip,*_croppedBlueStrip,NULL);
        dilate(*_croppedBlueStrip,*_croppedBlueStrip,NULL);


        if ( (_input->rows/1.4) < _croppedBlueStrip->rows){ // MOST PROBABLY THE CROPPED IT'S THE FULL STRIP
            // CROP HALF BOTTOM

            Rect bottomRect = Rect(0, _croppedBlueStrip->rows/2, _croppedBlueStrip->cols, _croppedBlueStrip->rows/2);

               Mat halfCropped = (*_croppedBlueStrip)(bottomRect);
              halfCropped.copyTo(*_croppedBlueStrip);
                *_originalCroppedStrip = (*_originalCroppedStrip)(bottomRect);

        }

        threshold(*_croppedBlueStrip, *_croppedBlueStrip, BINARIZE_THRESH, 255, CV_THRESH_BINARY);
        
        
        extractCharacters();     
    }    

}

void LPFranceSegmenterImpl::initializeThresholds(){
    LOW_BLUETHRES = Scalar(89,138,51);
    HIGH_BLUETHRES = Scalar(121,255,255);
    MINBLUEPERCENTAGE = 1.75;
    BINARIZE_THRESH = 92; /** Old value 92 */
    MAX_CHARACTERS = 3;
    SQUARE_RATIO = 3.2;
}

void LPFranceSegmenterImpl::cropBlueStrip(Mat *blueZoneMasking){
    vector<vector<Point> > contours;
    vector<Vec4i> contourHierarchy;
    

    /** Contours find */
    findContours(*blueZoneMasking, contours, contourHierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_NONE, Point(0,0));
    
    /** Sorting **/
    std::sort(contours.begin(), contours.end(), descendingCompare);
    
    std:sort(contours.begin(),contours.begin()+2,horizontalCompare);
    
    std::vector<std::vector<cv::Point> > contours_poly(1);
    
    /** Find the largest contour */
    approxPolyDP( cv::Mat(contours[0]), contours_poly[0], 3, true );
    
    /** Get the square which surround the largest contour */
    Rect blueSquare = boundingRect(contours_poly[0]);
    
    /** Crop from original image */
    _croppedBlueStrip = new Mat();
    *_croppedBlueStrip = (*_input)(blueSquare);
    
    
    /** This image will be used for cropping the original characters from it */
    _originalCroppedStrip = new Mat();
    _croppedBlueStrip->copyTo(*_originalCroppedStrip);


}
bool LPFranceSegmenterImpl::descendingCompare (vector<Point> i, vector<Point> j){
    return (i.size()>j.size());

}
bool LPFranceSegmenterImpl::horizontalCompare(vector<Point>i, vector<Point>j){
    Moments mntI = moments(i);
    Moments mntJ = moments(j);
    
    double xI = mntI.m10/mntI.m00;
    double xJ = mntJ.m10/mntJ.m00;
    return (xI > xJ);
}

bool LPFranceSegmenterImpl::horizontalCompareLeftRight(Rectangle i, Rectangle j){
    return (i.getCenter().x < j.getCenter().x);
}


void LPFranceSegmenterImpl::extractCharacters(void){
    vector<vector<Point> > contoursBlue;
    vector<Vec4i> contourHierarchyBlue;
    
    /* At this point _croppedBlueStrip should be a mask of the blue strip */
    findContours(*_croppedBlueStrip, contoursBlue, contourHierarchyBlue, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);
    
    sort(contoursBlue.begin(), contoursBlue.end(), descendingCompare);
    
    
    /** Make it in form of loop, by defining a global constant for the # of chars to detect
     when the first char is found, if the second doesn't match, stop looking
     */
    
    Point **centerList = (Point**)malloc(sizeof(Point*)*MAX_CHARACTERS);
    Rect **squareCharList = (Rect**)malloc(sizeof(Rect*)*MAX_CHARACTERS);
    
    for (int i = 0 ; i < MAX_CHARACTERS; ++i){
        squareCharList[i] = NULL;
        centerList[i] = new Point();
        
    }
    
    // assert(MAX_CHARACTERS >= contoursBlue.size());
    
    /** Get rectangles from contours, please take note that some of them are filtered */
    int charsGot = 0;
    for (int i = 0 ; i < contoursBlue.size() && charsGot < MAX_CHARACTERS; ++i ){
        
        squareCharList[charsGot] = getRectCharFromContour(contoursBlue[i], centerList[charsGot]);
        if (squareCharList[charsGot] != NULL)
            charsGot++;
        
    }
    
    // SORT SQUARES!!


    int validChars = testCharacters(centerList, charsGot);
    vector<Rectangle> squareListVectorized;
    
    for (int i = 0; i <= validChars; ++i){
        Rect square = *squareCharList[i];
        Rectangle elem = Rectangle(square, *centerList[i]);
        squareListVectorized.push_back(elem);
    }

    std::sort(squareListVectorized.begin(),squareListVectorized.end(),horizontalCompareLeftRight);

    /** Fill the image list */
    for (int i = 0; i <= validChars; ++i){
        Mat temp = (*_originalCroppedStrip)(squareListVectorized[i].getRect());
        Mat toPut = Mat(temp.rows, temp.cols, temp.type());
        temp.copyTo(toPut);
        result.push_back(toPut);
    }

}

Rect* LPFranceSegmenterImpl::getRectCharFromContour(vector<Point> contour, Point * center){
    std::vector<std::vector<cv::Point> > contours_poly(1);
    
    // Determine an approximate polygon for v[0] which is the largest contour
    approxPolyDP( cv::Mat(contour), contours_poly[0], 3, true );
    
    /** Get center */
    Moments mnt = moments(contours_poly[0]);
    
    double xCentroid = mnt.m10/mnt.m00;
    double yCentroid = mnt.m01/mnt.m00;
    
    // Store center
    (*center).x = xCentroid; (*center).y = yCentroid;
    
    Rect *square = new Rect();
    *square = boundingRect(contours_poly[0]);
    
    // Adapt rectangle
    controlAndExpandRectangle(square);
    
    double f1,f2;
    
    /** We need in f1 the biggest, for avoiding problems by comparing with the RATIO THRESHOLD */
    if (square->height > square->width){
        f1 = square->height;
        f2 = square->width;
    }
    else{
        f1 = square->width;
        f2 = square->height;
    }
    
    
    //   cout << "Square\t\n Height:"  << square->height << "\n\t Width:" << square->width << "\n\t Ratio:" << f1/f2 << endl;
    
    // If the ratio is adequate return the square, NULL otherwise
    if ((f1/f2) < SQUARE_RATIO)
        return square;
    else
        return NULL;

}
int LPFranceSegmenterImpl::testCharacters(Point** centers, int size){
    
    if (size <= 0) return -1;
    
    // First of all get the edge point for 'draw' the supporting line
    
    Point *firstPoint = centers[0];
    Point *lastPoint = new Point();
    lastPoint->y = firstPoint->y;
    lastPoint->x = _originalCroppedStrip->rows;
    
    /** Rect coefficients in form of Ax + Bx + C = 0 */
    double A = lastPoint->y - firstPoint->y;
    double B = - ( lastPoint->x - firstPoint->x);
    double C = firstPoint->y*(lastPoint->x-firstPoint->x) - firstPoint->x*(lastPoint->y-firstPoint->y);
    
    double sqrtpowAB = sqrt(pow(A, 2) + pow(B,2));
    int validChars = 0;
    int i = 1; bool done = false;
    
    while(i < size && done == false ){
        double dist = (A*centers[i]->x + B*centers[i]->y + C)/sqrtpowAB;
        
        if (abs(dist) < 7)
            validChars++;
        else
            done = true;
        
        ++i;
    }
    
    return validChars;

    
}
void LPFranceSegmenterImpl::controlAndExpandRectangle(Rect *rect){
    
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
    if ( (rect->x + rect->width ) > _originalCroppedStrip->cols)
        rect->width = _originalCroppedStrip->cols - rect->x;
    
    if ( (rect->y + rect->height) > _originalCroppedStrip->rows)
        rect->height = _originalCroppedStrip->rows - rect->y;

}

float LPFranceSegmenterImpl::getPercentageOfBlue(Mat*v){
    float totalPix = v->rows*v->cols;
    int totalLow = 0;
    int totalHigh = 0;
    
    for (int i = 0; i < v->rows*v->cols; ++i){
        uchar data = v->data[i];
        if (data > 127.5)
            totalHigh++;
        else
            totalLow++;
    }
    return (totalHigh/totalPix)*100;
}




