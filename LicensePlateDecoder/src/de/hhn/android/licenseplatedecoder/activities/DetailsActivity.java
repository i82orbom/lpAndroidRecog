package de.hhn.android.licenseplatedecoder.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import de.hhn.android.licenseplatedecoder.R;
import de.hhn.android.licenseplatedecoder.decoder.RegionInformation;
import de.hhn.android.licenseplatedecoder.listeners.DetailsActivityListener;

/*
 ** CLASS NAME: 
 **	  DetailsActivity
 **
 ** DESCRIPTION:
 **   This class represents the activity that shows the regional information after a decoding
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


public class DetailsActivity extends Activity {

	private ImageView capturedImg;
	private TextView regionName;
	private TextView subRegionName;
	private ImageView flagImg;
	
	private Button viewInWikipediaButton;
	private Button viewOnMapButton;
	private Button shareButton;
	
	private RegionInformation currentRegionInfo;


	private DetailsActivityListener listener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		
		/** Get the regional info received from the intent */
		this.currentRegionInfo = getIntent().getExtras().getParcelable("region-info");
		/** Set the listeners attached to this activity */
		this.listener = new DetailsActivityListener(this);
		
		/** Get UI Elements */
		this.regionName = (TextView)findViewById(R.id.regionName);
		this.subRegionName = (TextView)findViewById(R.id.superRegionName);
		this.capturedImg = (ImageView)findViewById(R.id.capturedImg);
		this.flagImg = (ImageView)findViewById(R.id.flagImage);
		
		this.viewInWikipediaButton = (Button)findViewById(R.id.wikipediaButton);
		this.viewOnMapButton = (Button)findViewById(R.id.viewOnMapButton);
		this.shareButton = (Button)findViewById(R.id.shareButton);
		
		this.viewInWikipediaButton.setOnClickListener(this.listener);
		this.viewOnMapButton.setOnClickListener(this.listener);
		this.shareButton.setOnClickListener(this.listener);
		
		/** Fill the UI with the regional info received */
		fillUIWithInfo();
	}
	
	private void fillUIWithInfo(){
		this.regionName.setText(this.currentRegionInfo.getRegionName());
		this.subRegionName.setText(this.currentRegionInfo.getSuperRegionName());
		Uri receivedImg = this.currentRegionInfo.getCapturedImg();
		if (receivedImg != null){
			this.capturedImg.setImageURI(receivedImg);
		}
		Bitmap flagResource = getFlagResource(this.currentRegionInfo.getCountryName());
		if (flagResource != null)
			this.flagImg.setImageBitmap(flagResource);
	}


	private Bitmap getFlagResource(String country){
		if (country.compareTo("Germany") == 0)
			return BitmapFactory.decodeResource(getResources(),R.drawable.f100);
		else if (country.compareTo("Austria") == 0)
			return BitmapFactory.decodeResource(getResources(),R.drawable.f101);
		else if (country.compareTo("France") == 0)
			return BitmapFactory.decodeResource(getResources(),R.drawable.f102);
		else if (country.compareTo("Czech Republic") == 0)
			return BitmapFactory.decodeResource(getResources(),R.drawable.f103);
		else if (country.compareTo("Poland") == 0)
			return BitmapFactory.decodeResource(getResources(),R.drawable.f104);
		else if (country.compareTo("Great Britain") == 0)
			return BitmapFactory.decodeResource(getResources(),R.drawable.f105);
		else if (country.compareTo("Italy") == 0)
			return BitmapFactory.decodeResource(getResources(),R.drawable.f106);
		else if (country.compareTo("Ireland") == 0)
			return BitmapFactory.decodeResource(getResources(),R.drawable.f107);
		else if (country.compareTo("Slovakia") == 0)
			return BitmapFactory.decodeResource(getResources(),R.drawable.f108);
		else if (country.compareTo("Turkey") == 0)
			return BitmapFactory.decodeResource(getResources(),R.drawable.f109);
		else if (country.compareTo("Romania") == 0)
			return BitmapFactory.decodeResource(getResources(),R.drawable.f110);
		else if (country.compareTo("Switzerland") == 0)
			return BitmapFactory.decodeResource(getResources(),R.drawable.f111);
		else if (country.compareTo("Russia") == 0)
			return BitmapFactory.decodeResource(getResources(),R.drawable.f112);

		return null;

	}
	
	public RegionInformation getCurrentRegionInfo() {
		return currentRegionInfo;
	}
	

}
