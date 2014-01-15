//
//  Rectangle.cpp
//  License Plate Recognizer
//
//  Created by Mario Orozco Borrego on 03/04/13.
//  Copyright (c) 2013 Mario Orozco Borrego. All rights reserved.
//

#include "Rectangle.h"
#include <opencv2/opencv.hpp>


using namespace cv;


    
Rectangle::Rectangle(Rect& r, Point& c){
        _rect = r;
        _center = c;
    }
    
void Rectangle::setRect(const Rect& v){
        _rect = v;
    }
    
void Rectangle::setCenter(const Point& v){
        _center = v;
    }
    
Rect& Rectangle:: getRect(void){
        return _rect;
    }
    
Point& Rectangle:: getCenter(void){
        return _center;
    }
