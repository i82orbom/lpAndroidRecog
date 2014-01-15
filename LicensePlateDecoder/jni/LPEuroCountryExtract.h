#ifndef __License_Plate_Recognizer__LPEuroCountryExtract__
#define __License_Plate_Recognizer__LPEuroCountryExtract__

#include <iostream>
#include <opencv2/opencv.hpp>

using namespace cv;


/**
 * \mainpage LPEuroCountryExtract Class
 * \author Mario Orozco Borrego
 * \date 18/Apr/2013
 * \brief Carries out the blue strip detection for recognize the country
         from an european license plate, the license plate image should only contain the croped rectangular license plate.
          The more quality and horizontal the picture is, the better results
 */

class LPEuroCountryExtract {
    
    bool debug;
    
    /* \brief _input Image input for processing (could be modified) */
    Mat* _input;
    
    /* \brief _hsvImage HSV colorspace image for blue detecting */
    Mat* _hsvImage;
    
    /* \brief _croppedBlueStrip cropped blue strip from original image (could be modified during the processing) */
    Mat* _croppedBlueStrip;
    
    /* \brief _croppedWithoutStrip original image without the blue strip */
    Mat* _croppedWithoutStrip;
    
    /* \brief _originalCroppedStrip original blue strip image (for further use, since _croppedBlueStrip could be modified) */
    Mat* _originalCroppedStrip;
    
    /* \brief _blueStripPresent wether if the blue strip is present in the license plate */
    bool _blueStripPresent;
    
    /* \brief _listOfCharacters vector with the character images */
    vector<Mat> _listOfCharacters;
    
    /* \brief LOW_BLUETHRES min blue threshold */
    Scalar LOW_BLUETHRES;
    /* \brief HIGH_BLUETHRES max blue threshold */
    Scalar HIGH_BLUETHRES;
    
    /* \brief MINBLUEPERCENTAGE min blue percentage that should be present in the image */
    float MINBLUEPERCENTAGE;
    
    /* \brief BINARIZE_THRESH binarize threhold for binarizing */
    int BINARIZE_THRESH;
    
    /* \brief MAX_CHARACTERS max number of characters to detect */
    int MAX_CHARACTERS;
    
    /* \brief SQUARE_RATIO square ratio for filtering false contours squares */
    float SQUARE_RATIO;
    
    /**
        \brief Initializes all parameters
            
            Initializes all parameters, note that all values are experimental by testing with real license plate pictures
     **/
    inline void initializeThresholds(void){
        LOW_BLUETHRES = Scalar(89,138,51);
        HIGH_BLUETHRES = Scalar(121,255,255);
        MINBLUEPERCENTAGE = 1.75;
        BINARIZE_THRESH = 92; /** Old value 92 */
        MAX_CHARACTERS = 2;
        SQUARE_RATIO = 3.2;
    }
    
    
    /**
        \brief Converts to HSV
     
        Converts to HSV colorspace _input and stores it in _hsvImage
     **/
    void convertToHsv(void);
    
    /**
     \brief Creates a mask for the blue area
     
        From _hsvImage, creates a mask for identifying the blue area of the license plate
     
     \returns A mask for detecting the blue area in a further step
     **/
    Mat* createBlueAreaMask(void);
    
    
    /**
     \brief Calculates the percentage of white
     
        Calculate the percentage of white of an input image
     \param v the input image
     \returns The percentage value (0-100)
     \note TO-IMPROVE
     **/
    float getPercentageOfBlue(Mat*v); // A 1.75% is enough to consider the blue strip present
    
    /**
     \brief By using the blue masking image, crops the blue strip
     
        From _input image, by using createBlueAreaMask, crops the blue strip and store two copies in _croppedBlueStrip and _originalCroppedStrip
     
     \param blueZoneMasking The mask previously computed
     **/
    void cropBlueStrip(Mat* blueZoneMasking);

    
    /**
     \brief Returns the cropped  blue strip
     
     \returns Image without blue strip
     **/
    Mat* getCroppedBlueStrip(void);
    
    /**
     \brief Extract characters from the blue strip
     **/
    void extractCharacters(void);
    
    /**
     \brief Expands a rectangle
        Expands a given rectangle by keeping its boundaries inside _input image
     \param rect Rect to expand
     **/
    void controlAndExpandRectangle(Rect *rect);
    
    /**
     \brief Get the rectangle from a contour
        Calculate the rectangle from a given contour and stores in center its centroid
     \param contour input contour
     \param center centroid of contour bounding rectangle
     **/
    Rect* getRectCharFromContour(vector<Point> contour, Point *center);
    
    /**
     \brief Ensures that the first character and consecutive characters are in the same horizontal line
       
        Ensures that the first character and consecutive characters are in the same horizontal line,
        it takes the first character center, traces a horizontal line to the edge, and test if the 
        consecutive centers are in a suitable distance from that line
     
     \param centers list of centers
     \param size center list's size
     \return suitable characters found
     **/
    int testCharacters(Point** centers, int size);
    
    /**
     \brief Runs the algorithm
        
        Runs the algorithm, the rest of the functions are not supossed to be called, this method is only called from the
        constructor, the steps would be:
     
            1. Convert _input to HSV
            2. Get blue mask from _hsvImage
            3. If there is enough amount of blue
                3a. Crop the blue strip
                3b. Filter and binarize
                3c. Extract characters
            4. Image characters are stored in _listOfCharacters
     **/
    void run(void);

    static bool descendingCompare (vector<Point> i, vector<Point> j);
    
public:
    
    /**
        \brief Void constructor
     */
    LPEuroCountryExtract(void);
    
    /**
     \brief Parametrized constructor
        \param input Input image to be processed (BGR)
     
        This constructor calls the execution routine and carries out all the detection process 
     */
    LPEuroCountryExtract(Mat* input);
    
    /**
     \brief Returns wether the blue strip if present
     \return true if present, false otherwise
     */
    bool isBlueStripPresent(void){ return _blueStripPresent; }
    
    /** 
     \brief Returns all image detected characters
        If no bluestrip is present, then the list would be empty 
     \return list of Mat with character images
     */
    vector<Mat> getCharacters(void){
         return _listOfCharacters;        
    }
    
    
    /**
     \brief Returns the cropped image without the blue strip
     
     \returns Image without blue strip
     **/
    Mat* getCroppedWithoutStrip(void);

};

#endif /* defined(__License_Plate_Recognizer__LPEuroCountryExtract__) */
