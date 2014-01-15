//
//  gabor.h
//  License Plate Recognizer
//
//  Created by Mario Orozco Borrego on 01/04/13.
//  Copyright (c) 2013 Mario Orozco Borrego. All rights reserved.
//

#include <opencv2/opencv.hpp>


#ifndef License_Plate_Recognizer_gabor_h
#define License_Plate_Recognizer_gabor_h

cv::Mat mkKernel(int ks, double sig, double th, double lm, double ps);
cv::Mat processGabor(const cv::Mat src_f, cv::Mat kernel, int kernel_size);


#endif
