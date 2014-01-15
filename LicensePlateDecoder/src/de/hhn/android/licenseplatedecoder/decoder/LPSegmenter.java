package de.hhn.android.licenseplatedecoder.decoder;

import java.util.ArrayList;


import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

/*
 ** CLASS NAME: 
 **	  LPSegmenter
 **
 ** DESCRIPTION:
 **   This class represents the JNI wrapper for the implemented C++ license plate segmenter
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
public class LPSegmenter {

	private long nativeInputAddr;
	private int countryCode;
	private String result;
	private TessBaseAPI baseApi;

	/**
	 * Returns the result of the decoding
	 * @return the result
	 */
	public String getResult(){
		return this.result;
	}
	
	/**
	 * Returns the dataset to be used and whitelist by the ocr engine, depending on the country code
	 * @param countryCode input country code
	 * @return the dataset in position 0 and whitelist in position 1
	 */
	public String[] getDatasetAndWhiteList(int countryCode){
		String[] result = new String[2]; // 0: dataset, 1: whitelist
		if (countryCode == 1){ // Germany
			result[0] = "de";
			result[1] = "ABCDEFGHIJKLMNOPRSTUVWXYZÄÖÜ";
		}
		else if(countryCode == 2){ // Austria
			result[0] = "eng";
			result[1] = "ABCDEFGHIJKLMNOPRSTUVWXYZ";
		}
		else if (countryCode == 3){ // Switzerland
			result[0] = "eng";
			result[1] = "ABCDEFGHIJKLMNOPRSTUVWXYZ";
		}
		else if (countryCode == 4){ // France
			result[0] = "eng";
			result[1] = "0123456789";
		}
		else if (countryCode == 5){ // Chech Republic
			result[0] = "eng";
			result[1] = "ABCDEFGHIJKLMNOPRSTUVWXYZ";
		}
		else if (countryCode == 6){ // Poland
			result[0] = "eng";
			result[1] = "ABCDEFGHIJKLMNOPRSTUVWXYZ";
		}
		else if (countryCode == 7){ // Great Britain
			result[0] = "eng";
			result[1] = "ABCDEFGHIJKLMNOPRSTUVWXYZ";
		}
		else if (countryCode == 8){ // Italy
			result[0] = "eng";
			result[1] = "ABCDEFGHIJKLMNOPRSTUVWXYZ";
		}
		else if (countryCode == 9){ // Ireland
			result[0] = "eng";
			result[1] = "ABCDEFGHIJKLMNOPRSTUVWXYZ";
		}
		else if (countryCode == 10){ // Slovakia
			result[0] = "eng";
			result[1] = "ABCDEFGHIJKLMNOPRSTUVWXYZ";
		}
		else if (countryCode == 11){ // Turkey
			result[0] = "eng";
			result[1] = "0123456789";
		}
		else if (countryCode == 12){ // Russia
			result[0] = "eng";
			result[1] = "0123456789";
		}
		else if (countryCode == 13){ // Romania
			result[0] = "eng";
			result[1] = "ABCDEFGHIJKLMNOPRSTUVWXYZ";
		}
		else
			return null;
		
		return result;
	}
	
	/**
	 * Constructor
	 * @param inputImageAddr input image pointer address
	 * @param countryCode input country code
	 */
	public LPSegmenter(long inputImageAddr, int countryCode){
		this.nativeInputAddr = inputImageAddr;
		this.countryCode = countryCode;
		Log.d("Segmenter", "Country Code: " + countryCode);
		
		/** OCR ENGINE INIT */
		this.baseApi = new TessBaseAPI();
		// INITIALIZATION DEPENDING OF THE COUNTRY CODE
		String languageDataset;
		String whitelist = null;
		String []datasetWhitelist = getDatasetAndWhiteList(countryCode);
		languageDataset = datasetWhitelist[0];
		whitelist = datasetWhitelist[1];
		
		this.baseApi.init("/sdcard/", languageDataset); // myDir + "/tessdata/eng.traineddata" must be present
		this.baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_CHAR);
		this.baseApi.setVariable("tessedit_char_whitelist", whitelist);
		
		ArrayList<Mat> segChars = getCharacters();

	    StringBuffer strb = new StringBuffer();
		for (Mat elem : segChars){
	        Imgproc.cvtColor(elem, elem, Imgproc.COLOR_BGR2GRAY);

			Bitmap pass = Bitmap.createBitmap(elem.cols(), elem.rows(), Bitmap.Config.ARGB_8888);
	        Utils.matToBitmap(elem, pass,true);
	   
	    	baseApi.setImage(pass);
			String recognizedText = baseApi.getUTF8Text(); // Log or otherwise display this string...
			strb.append(recognizedText);
	    	baseApi.clear();
	    	pass.recycle();
		}
		this.result = strb.toString();
		baseApi.end();
		baseApi = null;
		segChars = null;
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
