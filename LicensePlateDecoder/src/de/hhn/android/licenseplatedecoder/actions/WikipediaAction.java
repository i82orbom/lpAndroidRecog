package de.hhn.android.licenseplatedecoder.actions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import de.hhn.android.licenseplatedecoder.decoder.RegionInformation;

/*
 ** CLASS NAME: 
 **	  WikipediaAction
 **
 ** DESCRIPTION:
 **   This class represents a wikipedia action
 **
 **
 ** DEVELOPED BY:
 **   Mario Orozco Borrego (MOB)
 **
 ** SUPERVISED BY:
 **	  Prof. Dr.-Ing. Permantier, Gerald
 **		
 ** NOTES:
 **   
 **
 */

public class WikipediaAction {

	private RegionInformation regionInfo;
	private Context context;
	
	public WikipediaAction(Context ctx, RegionInformation info){
		this.context = ctx;
		this.regionInfo = info;
	}
	
	public void runAction(){
		Uri uri = Uri.parse("http://en.wikipedia.org/wiki/" + this.regionInfo.getRegionName());
		Intent wikiIntent = new Intent(Intent.ACTION_VIEW,uri);
		wikiIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.context.startActivity(wikiIntent);
	}
	
}
