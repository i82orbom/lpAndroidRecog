package de.hhn.android.licenseplatedecoder.decoder;

import java.util.ArrayList;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

/*
 ** CLASS NAME: 
 **	  CountryExtractor
 **
 ** DESCRIPTION:
 **   This class represents the JNI wrapper for the implemented C++ country extractor
 **
 **
 ** DEVELOPED BY:
 **   Mario Orozco Borrego (MOB)
 **
 ** SUPERVISED BY:
 **	  Prof. Dr.-Ing. Permantier, Gerald
 **
 **		
 ** NOTES:
 **   
 **
 */


@SuppressLint("SdCardPath")
public class CountryExtractor {

	private long nativeInputAddr;
	private long withoutStripInputAddr;
	private TessBaseAPI baseApi;
	private Mat withoutBlueStrip;
	private String result;
	
	/**
	 * Result getter
	 * @return the result
	 */
	public String getResult(){
		return this.result;
	}
	
	/**
	 * Constructor
	 * @param nativeAddress input image pointer address
	 * @param withoutStripInputAddr image pointer address in order to store the cropped image without the blue strip
	 */
	public CountryExtractor(long nativeAddress, long withoutStripInputAddr){
		this.nativeInputAddr = nativeAddress;	
		this.withoutStripInputAddr = withoutStripInputAddr;
		this.withoutBlueStrip = new Mat();
		
		/** OCR ENGINE INIT */
		this.baseApi = new TessBaseAPI();
		this.baseApi.setDebug(true);
		this.baseApi.init("/sdcard/", "blueBand"); // myDir + "/tessdata/eng.traineddata" must be present
		this.baseApi.setVariable("tessedit_char_whitelist", "ABCDFGHIKLOPRSTZ");
		this.baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_CHAR);
		
		ArrayList<Mat> countryCode  = getCharacters();

		StringBuffer strb = new StringBuffer();
		for (Mat elem : countryCode){

			Bitmap pass = Bitmap.createBitmap(elem.cols(), elem.rows(), Bitmap.Config.ARGB_8888);
	        Utils.matToBitmap(elem, pass,true);
	        
	    	baseApi.setImage(pass);
			String recognizedText = baseApi.getUTF8Text(); 
			strb.append(recognizedText);
			baseApi.clear();
	    	pass.recycle();
		}
		this.result = strb.toString();
		baseApi.end();
		baseApi = null;
		countryCode = null;
	}
	
	/**
	 * Native method which carries out the algorithm
	 * @return
	 */
	private native long[] process();
	
	/**
	 * Gets the characters from the input image calling process method
	 * @return the list of cropped images
	 */
	private ArrayList<Mat> getCharacters(){
		ArrayList<Mat> result = new ArrayList<Mat>();
		long[] address = this.process();
		if (address == null)
			return new ArrayList<Mat>();
		for(long elem:address){
			result.add(new Mat(elem));
		}
		return result;
	}
}
