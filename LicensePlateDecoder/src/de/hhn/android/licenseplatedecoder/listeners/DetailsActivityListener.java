package de.hhn.android.licenseplatedecoder.listeners;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import de.hhn.android.licenseplatedecoder.R;
import de.hhn.android.licenseplatedecoder.actions.MapAction;
import de.hhn.android.licenseplatedecoder.actions.ShareAction;
import de.hhn.android.licenseplatedecoder.actions.WikipediaAction;
import de.hhn.android.licenseplatedecoder.activities.DetailsActivity;

/*
 ** CLASS NAME: 
 **	  DetailsActivityListener
 **
 ** DESCRIPTION:
 **   This class represents the listener for the details activity actions
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
public class DetailsActivityListener implements OnClickListener{

	private DetailsActivity context;
	
	public DetailsActivityListener(Context ctx){
		if (ctx instanceof DetailsActivity)
			this.context = (DetailsActivity)ctx;
	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.wikipediaButton){
			WikipediaAction wikiAction = new WikipediaAction(this.context, this.context.getCurrentRegionInfo());
			wikiAction.runAction();
		}
		else if (v.getId() == R.id.shareButton){
			ShareAction shareAction = new ShareAction(this.context, this.context.getCurrentRegionInfo());
			shareAction.runAction();
		}
		else if (v.getId() == R.id.viewOnMapButton){
			MapAction mapAction = new MapAction(this.context, this.context.getCurrentRegionInfo());
			mapAction.runAction();
		}
		
	}
	
}
