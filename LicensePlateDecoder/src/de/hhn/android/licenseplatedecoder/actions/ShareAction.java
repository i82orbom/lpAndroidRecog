package de.hhn.android.licenseplatedecoder.actions;

/*
 ** CLASS NAME: 
 **	  ShareAction
 **
 ** DESCRIPTION:
 **   This class represents a share action
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


import android.content.Context;
import android.content.Intent;
import de.hhn.android.licenseplatedecoder.decoder.RegionInformation;

public class ShareAction extends Intent{

	private RegionInformation infoToShare;
	private Context context;
	
	public ShareAction(Context ctx, RegionInformation info){
		this.context = ctx;
		this.infoToShare = info;
	}
	
	public void runAction(){
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String shareBody = null;
		if (this.infoToShare.getSuperRegionName().compareTo("") == 0)
			shareBody = "Hey there!, I've just seen a license plate from: " + this.infoToShare.getRegionName() + "," + this.infoToShare.getCountryName();
		else
			shareBody = "Hey there!, I've just seen a license plate from: " + this.infoToShare.getRegionName() + ", " + this.infoToShare.getSuperRegionName() + "," + this.infoToShare.getCountryName();

		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "License plate seen");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		this.context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}
	
}
