package de.hhn.android.licenseplatedecoder.activities;

/*
 ** CLASS NAME: 
 **	  RecentShotsActivity
 **
 ** DESCRIPTION:
 **   This class represents the recents shots activity
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import de.hhn.android.licenseplatedecoder.R;
import de.hhn.android.licenseplatedecoder.actions.MapAction;
import de.hhn.android.licenseplatedecoder.actions.ShareAction;
import de.hhn.android.licenseplatedecoder.actions.WikipediaAction;
import de.hhn.android.licenseplatedecoder.decoder.RegionInformation;


public class RecentShotsActivity extends ListActivity{

	private RecentShotsActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recent_shots);
		String title = getResources().getString(R.string.recentShotsTitle);
		setTitle(title);
		this.instance = this;
		
		final ListView listview = getListView();
		
		// Get values from sharedpreferences!
	    SharedPreferences keyValues = getSharedPreferences(getResources().getString(R.string.RECENT_SHARED_PREFS), Context.MODE_PRIVATE);
	    
	    Map<String,?> mapValues = keyValues.getAll();
	    String[] values = new String[mapValues.size()];
	    if (mapValues.size() == 0){
	    	values = new String[1];
	    	String emptyLabel = getResources().getString(R.string.emptyListLabel);
	    	values[0] = new String(emptyLabel);
	    }
	    else{
	    	for (int i = 0; i < mapValues.size(); ++i){
	    		values[i] = (String)mapValues.get(new String(""+i));
	    	}
	    }
	    
	
		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < values.length; ++i) {
			list.add(values[i]);
		}
		final StableArrayAdapter adapter = new StableArrayAdapter(this,
				android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final String item = (String) parent.getItemAtPosition(position);
				actionItemSelected(item);
			}

		});
	}
	
	private void actionItemSelected(final String item){
		
		String emptyMsg = getResources().getString(R.string.emptyListLabel);
		
		if (item.compareTo(emptyMsg) == 0)
			return;
		
		// Fill a RegionInformation Object
		String countryName = "";
		String regionName = "";
		String superRegionName = "";
		
		StringTokenizer strtk = new StringTokenizer(item,",");
		int count = 0;
		while(strtk.hasMoreTokens()){
			if (count == 0){
				regionName = strtk.nextToken();
			}
			else if (count == 1){
				superRegionName = strtk.nextToken();
			}
			else if (count == 2){
				countryName = strtk.nextToken();
			}
			count++;
		}
		if (count == 2){
			countryName = new String(superRegionName);
			superRegionName = "";
		}
		
		final RegionInformation regInfo = new RegionInformation(regionName, superRegionName, countryName, null);
		
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		String wikipediaLabel = getResources().getString(R.string.viewInWikipedia);
		String shareLabel = getResources().getString(R.string.share);
		String mapLabel = getResources().getString(R.string.viewInMap);
		
		/** 
		 * Attach the actions when a item is selected 
		 */
		final CharSequence[] items = {wikipediaLabel,shareLabel,mapLabel};
		dialog.setTitle("Choose action");
		dialog.setItems(items, new DialogInterface.OnClickListener() {
			
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (which == 0){
					WikipediaAction wikiAction = new WikipediaAction(instance, regInfo);
					wikiAction.runAction();
				}
				else if (which == 1){
					ShareAction shareAction = new ShareAction(instance, regInfo);
					shareAction.runAction();

				}
				else if (which == 2){
					MapAction mapAction = new MapAction(instance, regInfo);
					mapAction.runAction();
				}
			}
		});
		dialog.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recent_shots_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		if (item.getItemId() == R.id.action_shots_clear){
			/** Clears the sharedpreferences file */
		    SharedPreferences keyValues = getSharedPreferences(getResources().getString(R.string.RECENT_SHARED_PREFS), Context.MODE_PRIVATE);
		    SharedPreferences.Editor ed = keyValues.edit();
		    ed.clear();
		    /** Save changes */
		    ed.commit();
		    /** Shows 'cleared' message */
		    String clearedMessage = getResources().getString(R.string.clearedMessage);
		    Toast.makeText(this, clearedMessage, Toast.LENGTH_SHORT).show();
		    finish();
		    
		    return true;
		}
	
		return false;
	}
	
	
	/** 
	 * Class which holds the logic of item storage
	 *
	 */
	private class StableArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}



}
