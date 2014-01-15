package de.hhn.android.licenseplatedecoder.decoder;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/*
 ** CLASS NAME: 
 **	  LPSegmenter
 **
 ** DESCRIPTION:
 **   This class represents the information of a region as result of decoding a license 
 **   plate or from the recent shots activity.
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

public class RegionInformation implements Parcelable{
	
	private String regionName;
	private String superRegionName;
	private String countryName;
	private Uri capturedImg; /** Present if it's the result of decoding a license plate */
	
	/**
	 * Public constructor
	 * @param regionN region name
	 * @param subRegionN subregion name
	 * @param countryN country name
	 * @param pic picture (just in case it's from the license plate recognition)
	 */
	public RegionInformation(String regionN, String subRegionN, String countryN, Uri pic){
		this.regionName = regionN;
		this.superRegionName = subRegionN;
		this.countryName = countryN;
		this.capturedImg = pic;
	}
	
	
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public String getSuperRegionName() {
		return superRegionName;
	}
	public void setSuperRegionName(String subRegionName) {
		this.superRegionName = subRegionName;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public Uri getCapturedImg() {
		return capturedImg;
	}
	public void setCapturedImg(Uri capturedImg) {
		this.capturedImg = capturedImg;
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (this.capturedImg != null)
			dest.writeStringArray(new String[] {this.regionName,
                                             this.superRegionName,
                                             this.countryName,
                                             this.capturedImg.getPath()});
		else{
			dest.writeStringArray(new String[] {this.regionName,
                    this.superRegionName,
                    this.countryName,
                    ""});
		}
		
     }
	
	public RegionInformation(Parcel in){
        String[] data = new String[4];
        in.readStringArray(data);
        
        this.regionName = data[0];
        this.superRegionName = data[1];
        this.countryName = data[2];
        if (data[3].compareTo("") != 0)
        	this.capturedImg = Uri.parse(data[3]);
        else
        	this.capturedImg = null;
        
    }
	
	public static final Parcelable.Creator<RegionInformation> CREATOR = new Parcelable.Creator<RegionInformation>() {
        public RegionInformation createFromParcel(Parcel in) {
            return new RegionInformation(in); 
        }

        public RegionInformation[] newArray(int size) {
            return new RegionInformation[size];
        }
    };
	
	
}
