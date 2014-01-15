package de.hhn.android.licenseplatedecoder.activities;

/*
 ** CLASS NAME: 
 **	  PrefsActivity
 **
 ** DESCRIPTION:
 **   This class represents the preferences view
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

import de.hhn.android.licenseplatedecoder.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PrefsActivity extends PreferenceActivity{

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.prefs);
	}

}