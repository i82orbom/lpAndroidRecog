package de.hhn.android.licenseplatedecoder.decoder;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import de.hhn.android.licenseplatedecoder.db.DBHelper;

/*
 ** CLASS NAME: 
 **	  DecodingEngine
 **
 ** DESCRIPTION:
 **   This class wraps all the decoding functionality
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

public class DecodingEngine {

	private Bitmap inputImg;
	private boolean automaticMode;
	private int countryCodeNonAutomatic;
	private DBHelper dbHelper;
	private Context context;
	
	/**
	 * Constructor
	 * @param input input image
	 * @param automatic wheter we are using automatic country detection or not
	 * @param countryCodeNonAutomatic in case we are using manual mode we need to specify the country to be recognized
	 * @param ctx context
	 */
	public DecodingEngine(Bitmap input, boolean automatic, int countryCodeNonAutomatic, Context ctx){
		this.inputImg = input;
		this.automaticMode = automatic;
		this.countryCodeNonAutomatic = countryCodeNonAutomatic;
		this.context = ctx;
		this.dbHelper = new DBHelper(this.context);
		this.dbHelper.openDataBase();
		/** Loads the native library */
		System.loadLibrary("LicensePlateDecoder");
	}
	
	/**
	 * Returns the regional info using the input provided in the constructor
	 * @return the regional info got
	 */
	public RegionInformation getRegionInfo(){
		RegionInformation regionInfoGot = null;
		
		if (automaticMode){
			String countryCode = getCountryCode();
			int countryCodeInt = this.dbHelper.getCountryCode(countryCode);
			if (countryCodeInt != -1){
				String licensePlate = getLicensePlate();
				regionInfoGot = dbRegionLookup(countryCodeInt, licensePlate);
			}
			else
				return null;
		}
		else{
			String licensePlate = getLicensePlate();
			regionInfoGot = dbRegionLookup(countryCodeNonAutomatic, licensePlate);
		}
		
		
		return regionInfoGot;
	}
	
	/**
	 * Carries out the db lookup
	 * @param countryCode the country code
	 * @param licensePlate the license plate recognized
	 * @return the regional info from the db
	 */
	private RegionInformation dbRegionLookup(int countryCode, String licensePlate){
		RegionInformation regionInfo = null;
		Log.d("LC","License plate: " + licensePlate);

		if (countryCode == 1){ // Germany
			if (licensePlate.length() >= 3){
				RegionInformation tempReg = null;
				RegionInformation lastReg = null;
				int idx = 0;
				while ( (tempReg = this.dbHelper.getRegionInfo(countryCode, licensePlate.substring(0, idx))) != null && idx <= 3 ){
					lastReg = tempReg;
					idx++;
				}
				regionInfo = lastReg;
			}
		}
		else if (countryCode == 2){ // Austria
			if (licensePlate.length() >= 3)
				regionInfo = this.dbHelper.getRegionInfo(countryCode, licensePlate.substring(0, 3));
		}
		else if (countryCode == 3){ // Switzerland
			if (licensePlate.length() >= 2)
				regionInfo = this.dbHelper.getRegionInfo(countryCode, licensePlate.substring(0, 1));
		}
		else if (countryCode == 4){ // France
			regionInfo = this.dbHelper.getRegionInfo(countryCode, licensePlate);
		}
		else if (countryCode == 5){ // Chech Republic
			if (licensePlate.length() >= 1)
				regionInfo = this.dbHelper.getRegionInfo(countryCode, licensePlate.substring(0, 0));
		}
		else if (countryCode == 6){ // Poland
			if (licensePlate.length() >= 3)
				regionInfo = this.dbHelper.getRegionInfo(countryCode, licensePlate.substring(0, 3));
		}
		else if (countryCode == 7){ // Great Britain
			if (licensePlate.length() >= 2)
				regionInfo = this.dbHelper.getRegionInfo(countryCode, licensePlate.substring(0, 1));
		}
		else if (countryCode == 8){ // Italy
			if (licensePlate.length() >= 2)
				regionInfo = this.dbHelper.getRegionInfo(countryCode, licensePlate.substring(0, 2));
		}
		else if (countryCode == 9){ // Ireland
			if (licensePlate.length() >= 2){
				RegionInformation tempReg = null;
				RegionInformation lastReg = null;
				int idx = 0;
				while ( (tempReg = this.dbHelper.getRegionInfo(countryCode, licensePlate.substring(0, idx))) != null && idx <= 2 ){
					lastReg = tempReg;
					idx++;
				}
				regionInfo = lastReg;
			}
		}
		else if (countryCode == 10){ // Slovakia
			if (licensePlate.length() >= 2)
				regionInfo = this.dbHelper.getRegionInfo(countryCode, licensePlate.substring(0, 2));
		}
		else if (countryCode == 11){ // Turkey
			if (licensePlate.length() >= 2)
				regionInfo = this.dbHelper.getRegionInfo(countryCode, licensePlate.substring(0, 2));
		}
		else if (countryCode == 12){ // Russia
			if (licensePlate.length() >= 3){
				String licensePlateTrim = licensePlate.substring(licensePlate.length()-3, licensePlate.length());
			
				RegionInformation tempReg = null;
				RegionInformation lastReg = null;
				int idx = licensePlateTrim.length() - 1;
				
				while ( (tempReg = this.dbHelper.getRegionInfo(countryCode, licensePlateTrim.substring(idx, licensePlateTrim.length()))) != null && idx > 0 ){
					lastReg = tempReg;
					idx--;
				}
				regionInfo = lastReg;
			}
		}
		else if (countryCode == 13){ // Romania
			if (licensePlate.length() >= 2)
				regionInfo = this.dbHelper.getRegionInfo(countryCode, licensePlate.substring(0, 2));
		}
		
	
		return regionInfo;
	}
	
	/**
	 * Get the country code using CountryExtractor
	 * @return the country code
	 */
	public String getCountryCode(){
		Mat nativeOpencv = new Mat();
		org.opencv.android.Utils.bitmapToMat(inputImg, nativeOpencv);
		Mat res = new Mat(nativeOpencv.rows(),nativeOpencv.cols(),nativeOpencv.type());
		org.opencv.imgproc.Imgproc.cvtColor(nativeOpencv, res, Imgproc.COLOR_RGBA2BGR);
		nativeOpencv = null;
		Mat withoutStrip = new Mat();
		
		CountryExtractor ce = new CountryExtractor(res.nativeObj,withoutStrip.nativeObj);
		return ce.getResult();
	}
	
	/**
	 * Get the license plate using LPSegmenter
	 * @return the license plate
	 */
	public String getLicensePlate(){
		Mat nativeOpencv = new Mat();
		org.opencv.android.Utils.bitmapToMat(inputImg, nativeOpencv);
		Mat res = new Mat(nativeOpencv.rows(),nativeOpencv.cols(),nativeOpencv.type());
		org.opencv.imgproc.Imgproc.cvtColor(nativeOpencv, res, Imgproc.COLOR_RGBA2BGR);
		nativeOpencv = null;
		
		LPSegmenter lp = new LPSegmenter(res.nativeObj, this.countryCodeNonAutomatic);
		return lp.getResult();
	}
}
