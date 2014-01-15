#include "LPEuroCountryExtract.h"
#include "gabor.h"
#include <iostream>

using namespace std;

LPEuroCountryExtract::LPEuroCountryExtract(void){
    _input = NULL;
}


LPEuroCountryExtract::LPEuroCountryExtract(Mat* v){
    debug = false;
    _input = v;
    _hsvImage = NULL;
    run();
}

void LPEuroCountryExtract::convertToHsv(void){
    if (_hsvImage == NULL)
        _hsvImage = new Mat();
    _blueStripPresent = false;
    cvtColor(*_input, *_hsvImage, CV_BGR2HSV);
    
    initializeThresholds();
}



Mat* LPEuroCountryExtract::createBlueAreaMask(void){
    Mat* blueZoneMasking = new Mat(_hsvImage->rows, _hsvImage->cols, _hsvImage->type());
    
    inRange(*_hsvImage, LOW_BLUETHRES, HIGH_BLUETHRES, *blueZoneMasking);
    
    if (getPercentageOfBlue(blueZoneMasking) >= MINBLUEPERCENTAGE)
        _blueStripPresent = true;
   
    return blueZoneMasking;
}



float LPEuroCountryExtract::getPercentageOfBlue(Mat*v){
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

/**
  \brief Comparator used for sorting contours 
 */
bool LPEuroCountryExtract::descendingCompare (vector<Point> i, vector<Point> j)
{
    return (i.size()>j.size());
}

void LPEuroCountryExtract::cropBlueStrip(Mat *blueZoneMasking){
    
    vector<vector<Point> > contours;
    vector<Vec4i> contourHierarchy;
    
    /** Contours find */
    findContours(*blueZoneMasking, contours, contourHierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_NONE, Point(0,0));
    
    /** Sorting **/
    std::sort(contours.begin(), contours.end(), descendingCompare);
    
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
    
    
    /** Remove the blue strip from the original image */
    Rect restSquare;
    restSquare.x = blueSquare.x + blueSquare.width;
    restSquare.y = 0;
    restSquare.width = _input->cols - restSquare.x;
    restSquare.height = _input->rows;
    
    _croppedWithoutStrip = new Mat();
    Mat temp = (*_input)(restSquare);
    temp.copyTo(*_croppedWithoutStrip);
    
    
}

Mat* LPEuroCountryExtract::getCroppedBlueStrip(void){
    return _originalCroppedStrip;
}

Mat* LPEuroCountryExtract::getCroppedWithoutStrip(void){
    return _croppedWithoutStrip;
}


void LPEuroCountryExtract::run(void){
    convertToHsv();
    Mat *blueAreaMask = createBlueAreaMask();
 
    
    if (_blueStripPresent){
        if (debug) cout << "Blue strip present" << endl;
        
        cropBlueStrip(blueAreaMask);
        
        if (debug){ cout << "Blue strip cropped" << endl;
            namedWindow("Cropped");
            namedWindow("WithoutStrip");

            imshow("Cropped", *_croppedBlueStrip);
            imshow("WithoutStrip", *_croppedWithoutStrip);

            waitKey(0);
            
            destroyWindow("Cropped");
            destroyWindow("WithoutStrip");
        }

    
        /** The cropped blue strip is in _croppedBlueStrip */
   
        /** _croppedBlueStrip it's not actually HSV, but by doing this conversion we got
            a mask */
        cvtColor(*_croppedBlueStrip, *_croppedBlueStrip, CV_HSV2BGR);
        cvtColor(*_croppedBlueStrip, *_croppedBlueStrip, CV_BGR2GRAY);
        
        Mat kernel = mkKernel(7, 1, 95, 0.69, 21);
        *_croppedBlueStrip = processGabor(*_croppedBlueStrip,kernel,21);
        
        (*_croppedBlueStrip).convertTo(*_croppedBlueStrip, CV_8UC1);
        
        dilate(*_croppedBlueStrip, *_croppedBlueStrip, NULL);
        dilate(*_croppedBlueStrip, *_croppedBlueStrip, NULL);
        dilate(*_croppedBlueStrip, *_croppedBlueStrip, NULL);
        
        if (debug){
            cout << "Showing blue strip after filtering" << endl;
            namedWindow("BlueStripProcessed");
            
            imshow("BlueStripProcessed", *_croppedBlueStrip);
            
            waitKey(0);
            
            destroyWindow("BlueStripProcessed");

        }

        
        threshold(*_croppedBlueStrip, *_croppedBlueStrip, BINARIZE_THRESH, 255, CV_THRESH_BINARY);

        if (debug){
            cout << "Showing blue strip thresholded" << endl;
            namedWindow("BlueStripThreshold");
            
            imshow("BlueStripThreshold", *_croppedBlueStrip);
            
            waitKey(0);
            
            destroyWindow("BlueStripThreshold");
            
            cout << "Running extraction" << endl;

        }
        
        /** Run the extraction */
        extractCharacters();
    }

}

void LPEuroCountryExtract::extractCharacters(void){
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
    
    int validChars = testCharacters(centerList, charsGot);

    /** Fill the image list */
    for (int i = 0; i <= validChars; ++i){
        Rect square = *squareCharList[i];
        Mat temp = (*_originalCroppedStrip)(square);
        Mat toPut = Mat(temp.rows, temp.cols, temp.type());
        temp.copyTo(toPut);
        _listOfCharacters.push_back(toPut);
    }
      
}

int LPEuroCountryExtract::testCharacters(Point** centers, int size){
    
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
        
        if (abs(dist) < 5)
            validChars++;
        else
            done = true;
        
        ++i;
    }

    return validChars;

}

void LPEuroCountryExtract::controlAndExpandRectangle(Rect *rect){
    
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

Rect* LPEuroCountryExtract::getRectCharFromContour(vector<Point> contour, Point * center){
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

