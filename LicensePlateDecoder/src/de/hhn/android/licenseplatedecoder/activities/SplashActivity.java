package de.hhn.android.licenseplatedecoder.activities;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.droid4you.util.cropimage.CropImage;

import de.hhn.android.licenseplatedecoder.R;
import de.hhn.android.licenseplatedecoder.decoder.DecodingEngine;
import de.hhn.android.licenseplatedecoder.decoder.RegionInformation;
import de.hhn.android.licenseplatedecoder.listeners.SplashActivityListener;

/*
 ** CLASS NAME: 
 **	  SplashActivity
 **
 ** DESCRIPTION:
 **   This class represents the main activity showed in the application
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


public class SplashActivity extends Activity {

	private ImageButton cameraButton;
	private ImageButton galleryButton;
	private SplashActivityListener listener;
	private TextView preferenceStatusLabel;
	
	/** Tags */
	private static final int TAG_GALLERY_ACTION = 1000;
	private static final int TAG_TAKE_PICTURE = 1001;
	private static final int TAG_CROP_ACTION = 1002;
	protected static final String TAG_OPENCV = "cv";

	
	/** OpenCV library loading */
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
			{
				Log.i(TAG_OPENCV, "OpenCV loaded successfully");
			} break;
			default:
			{
				super.onManagerConnected(status);
			} break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		Log.i(TAG_OPENCV, "onCreate");
		Log.i(TAG_OPENCV, "Trying to load OpenCV library");
		if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_5, this, mLoaderCallback))
		{
			Log.e(TAG_OPENCV, "Cannot connect to OpenCV Manager");
		}
		else{ Log.i(TAG_OPENCV, "opencv successfully added"); }
		
		
		/** Initialize variables */
		this.listener = new SplashActivityListener(this);
		
		/** Get UI controls */
		cameraButton = (ImageButton)findViewById(R.id.buttonCameraSplash);
		galleryButton = (ImageButton)findViewById(R.id.buttonGallerySplash);
		
		/** Set listeners */
		cameraButton.setOnClickListener(this.listener);
		galleryButton.setOnClickListener(this.listener);
		
		this.preferenceStatusLabel = (TextView)findViewById(R.id.preferenceStatus);
		
		refreshStatusFromPreferences();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		/** We have to refresh the bottom status when if the preferences were modified */
		refreshStatusFromPreferences();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		/** Either we have captured a picture from the camera or the gallery */
		if (resultCode == RESULT_OK && (requestCode == TAG_GALLERY_ACTION) || (requestCode == TAG_TAKE_PICTURE) ){
			if (data != null) /** On some devices, resultCode from the camera even though the picture was not take is RESULT_OK, 
											by doing this we avoid the application crashing */
				cropImage(data.getData());
		
		}
		else if (resultCode == RESULT_OK && requestCode == TAG_CROP_ACTION){
			/** Process the cropped image */
			Uri capturedImgUri = Uri.parse(getRealPathFromURI(getImageUri(this, (Bitmap) data.getExtras().get("data"))));
			RegionInformation infoGot = null;
			// RECOGNITION AND DB LOOKUP
			Bitmap bmp = (Bitmap)data.getExtras().get("data");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			boolean automaticMode = prefs.getBoolean("isAutomatic", true);
			String countrySelectedStr = prefs.getString("selectedDecodeCountry", "1");

			int countrySelected = Integer.parseInt(countrySelectedStr);
			
			DecodingEngine decodEngine = new DecodingEngine(bmp, automaticMode, countrySelected, this);
			infoGot = decodEngine.getRegionInfo();
			
			
			if (infoGot != null){
				infoGot.setCapturedImg(capturedImgUri);
				updateRecentShots(infoGot);
				Intent intent = new Intent(this,DetailsActivity.class);
				intent.putExtra("region-info", infoGot);
				startActivity(intent);		
			}
			else{
				// RECOG NOT DONE
				String recogNotDoneMsg = getResources().getString(R.string.recognitionNotDoneMessage);
				Toast.makeText(this, recogNotDoneMsg, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if (item.getItemId() == R.id.action_settings){
			Intent intent = new Intent(this, PrefsActivity.class);
			startActivity(intent);
			return true;
		}
		else if (item.getItemId() == R.id.action_recentShots){
			Intent intent = new Intent(this, RecentShotsActivity.class);
			startActivity(intent);
			return true;
		}
		
		return false;
	}
	
	private void cropImage(Uri picture){
		Intent cropIntent = new Intent(this,CropImage.class);
		cropIntent.putExtra("image-path", getRealPathFromURI(picture));
		cropIntent.putExtra("return-data", true);
		startActivityForResult(cropIntent, TAG_CROP_ACTION);
	}
	
	public Uri getImageUri(Context inContext, Bitmap inImage) {
		  ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		  inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		  String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		  return Uri.parse(path);
	}
	

	public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
	
	
	public void updateRecentShots(RegionInformation regInfo){
				
		SharedPreferences keyValues = getSharedPreferences(getResources().getString(R.string.RECENT_SHARED_PREFS), Context.MODE_PRIVATE);
		Map<String,?> mapValues = keyValues.getAll();

		// LOOK UP THE INSERTION POSITION
		int idx = mapValues.size();

		if (idx == 20){
			String []values = new String[19];
			for (int i = 0; i < 19; ++i){
				values[i] = (String)mapValues.get(new String(i+""));
			}
			SharedPreferences.Editor ed = keyValues.edit();
			ed.clear();
			for (int i = 0; i < 19; ++i){
				ed.putString(new String((i+1)+""), values[i]);
			}
			ed.commit();
			idx = 0;
		}
		else{
			String []values = new String[idx];
			for (int i = 0; i < idx; ++i){
				values[i] = (String)mapValues.get(new String(i+""));
			}
			SharedPreferences.Editor ed = keyValues.edit();
			ed.clear();
			for (int i = 0; i < idx; ++i){
				ed.putString(new String((i+1)+""), values[i]);
			}
			ed.commit();
			idx = 0;
		}

		// IN IDX!
		SharedPreferences.Editor ed = keyValues.edit();
		String superRegionName = regInfo.getSuperRegionName();
		
		if (superRegionName.compareTo("") == 0){
			ed.putString(new String(idx+""), new String(regInfo.getRegionName() + "," + regInfo.getCountryName()));
		}
		else{
			ed.putString(new String(idx+""), new String(regInfo.getRegionName() + "," + regInfo.getSuperRegionName() + "," + regInfo.getCountryName()));
		}
		
		ed.commit();	

	}
	
	private void refreshStatusFromPreferences(){
		this.preferenceStatusLabel = (TextView)findViewById(R.id.preferenceStatus);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean automaticMode = prefs.getBoolean("isAutomatic", true);
		String countrySelectedStr = prefs.getString("selectedDecodeCountry", "1");
		String statusStr = "";
		if (automaticMode){
			statusStr = "Automatic mode";
		}
		else{
			int countryCodeInt = Integer.parseInt(countrySelectedStr);
			String countryName = getCountryFromCode(countryCodeInt);
			statusStr = "Manual / " + countryName;
		}
		this.preferenceStatusLabel.setText(statusStr);
	}
	
	
	public String getCountryFromCode(int countryCode){
		if (countryCode == 1){ // Germany
			return "Germany";
		}
		else if (countryCode == 2){ // Austria
			return "Austria";
		}
		else if (countryCode == 3){ // Switzerland
			return "Switzerland";
		}
		else if (countryCode == 4){ // France
			return "France";
		}
		else if (countryCode == 5){ // Chech Republic
			return "Chech Republic";
		}
		else if (countryCode == 6){ // Poland
			return "Poland";
		}
		else if (countryCode == 7){ // Great Britain
			return "Great Britain";
		}
		else if (countryCode == 8){ // Italy
			return "Italy";
		}
		else if (countryCode == 9){ // Ireland
			return "Ireland";
		}
		else if (countryCode == 10){ // Slovakia
			return "Slovakia";
		}
		else if (countryCode == 11){ // Turkey
			return "Turkey";
		}
		else if (countryCode == 12){ // Russia
			return "Russia";
		}
		else if (countryCode == 13){ // Romania
			return "Romania";
		}
		return null;
	}
	
	/** Getters & Setters */
	
	public int getTagTakePicture() {
		return TAG_TAKE_PICTURE;
	}


	public int getTagGalleryAction() {
		return TAG_GALLERY_ACTION;
	}
	
	public int getTagCropAction() {
		return TAG_CROP_ACTION;
	}
	
}
