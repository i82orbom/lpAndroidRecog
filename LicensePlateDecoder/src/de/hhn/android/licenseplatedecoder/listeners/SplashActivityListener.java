package de.hhn.android.licenseplatedecoder.listeners;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import de.hhn.android.licenseplatedecoder.R;
import de.hhn.android.licenseplatedecoder.activities.SplashActivity;

/*
 ** CLASS NAME: 
 **	  SplashActivityListener
 **
 ** DESCRIPTION:
 **   This class represents the SplashActivity listener actions
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

public class SplashActivityListener implements OnClickListener{

	private SplashActivity context;
	
	public SplashActivityListener(Context ctx){
		if (ctx instanceof SplashActivity)
			this.context = (SplashActivity)ctx;
	}
	
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonCameraSplash){
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			this.context.startActivityForResult(takePictureIntent, this.context.getTagTakePicture());
		}
		else if(v.getId() == R.id.buttonGallerySplash){
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			this.context.startActivityForResult(Intent.createChooser(intent,
					this.context.getResources().getString(R.string.openGallery)), this.context.getTagGalleryAction());
		}
		
	}

}
