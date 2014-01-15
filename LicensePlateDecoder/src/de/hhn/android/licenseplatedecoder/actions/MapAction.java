package de.hhn.android.licenseplatedecoder.actions;

/*
 ** CLASS NAME: 
 **	  MapAction
 **
 ** DESCRIPTION:
 **   This class represents a map action
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import de.hhn.android.licenseplatedecoder.R;
import de.hhn.android.licenseplatedecoder.activities.OSMActivity;
import de.hhn.android.licenseplatedecoder.decoder.RegionInformation;

public class MapAction {


	private RegionInformation regionInfo;
	private Context context;
	
	/**
	 * Constructor
	 * @param ctx context
	 * @param info regional information to be used
	 */
	public MapAction(Context ctx, RegionInformation info){
		this.context = ctx;
		this.regionInfo = info;
	}
	
	/**
	 * Runs map action
	 */
	public void runAction(){
	
		AlertDialog.Builder mapProviderSelector = new AlertDialog.Builder(this.context);
		mapProviderSelector.setTitle("Select map provider:");
        
        final CharSequence[] opsChars = {this.context.getResources().getString(R.string.gMaps),  
        		this.context.getResources().getString(R.string.openStreeMaps)};
        
        mapProviderSelector.setItems(opsChars, new android.content.DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){ // GMAPS
                	Uri uri = Uri.parse("geo:0,0?q="+ regionInfo.getRegionName() + "+" + regionInfo.getCountryName());
            		Intent mapIntent = new Intent(Intent.ACTION_VIEW,uri);
            		mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            		context.startActivity(mapIntent);
            		
                }
                else if (which == 1){ // OSM
                	Intent mapIntent = new Intent(context, OSMActivity.class);
                	mapIntent.putExtra("region-info",regionInfo);
                	mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            		context.startActivity(mapIntent);
                }
                
                dialog.dismiss();
            }

			
        });
        /**
         * Shows the selector, an therefore the action is going to be executed
         */
        mapProviderSelector.show();
	}
	
}
