package com.droid4you.util.cropimage;

import java.io.File;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private static final int PICK_FROM_CAMERA = 1;
	private Uri mImageCaptureUri;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button b = (Button)findViewById(R.id.button);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTakePhotoAction();

			}
		});
	}


	private void doTakePhotoAction() {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
				"tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

		try {
			intent.putExtra("return-data", false);
			startActivityForResult(intent, PICK_FROM_CAMERA);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case PICK_FROM_CAMERA:
			Intent intent = new Intent(this, CropImage.class);
			intent.putExtra("image-path", mImageCaptureUri.getPath());
			intent.putExtra("scale", true);
			startActivity(intent);
			break;
		}
	}
}