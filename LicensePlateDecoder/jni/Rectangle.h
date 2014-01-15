//
//  Rectangle.h
//  License Plate Recognizer
//
//  Created by Mario Orozco Borrego on 03/04/13.
//  Copyright (c) 2013 Mario Orozco Borrego. All rights reserved.
//

#ifndef __License_Plate_Recognizer__Rectangle__
#define __License_Plate_Recognizer__Rectangle__

#include <iostream>
#include <opencv2/opencv.hpp>

using namespace cv;

class Rectangle {
    
private:
    Rect _rect;
    Point _center;
    
public:
    
    Rectangle(Rect& r, Point& c);
    
    void setRect(const Rect& v);
    
    void setCenter(const Point& v);
    
    Rect& getRect(void);
    
    Point& getCenter(void);
};


bool squareCompare (Rectangle i, Rectangle j);
#endif /* defined(__License_Plate_Recognizer__Rectangle__) */
