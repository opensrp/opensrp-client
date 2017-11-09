/*
 * =========================================================================
 * Copyright (c) 2014 Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 * =========================================================================
 * @file LiveRecognition.java
 */

package layout.face.sidface.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing.FP_MODES;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing.PREVIEW_ROTATION_ANGLE;

import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.face.sidface.utils.CameraSurfacePreview;
import org.ei.opensrp.indonesia.face.sidface.utils.DrawView;

import java.io.ByteArrayOutputStream;

public class LiveRecognition extends Activity implements Camera.PreviewCallback {

	private static final String TAG = LiveRecognition.class.getSimpleName();
	private static final String IMAGE_PICK = "Image Pick";
	private final String PROJECTION_PATH = MediaStore.Images.Media.DATA;
	Camera cameraObj; // Accessing the Android native Camera.
	FrameLayout preview;
	CameraSurfacePreview mPreview;
	private OrientationEventListener orientationListener;
	private FacialProcessing faceObj;
	private int frameWidth;
	private int frameHeight;
	private boolean cameraFacingFront = true;
	private static PREVIEW_ROTATION_ANGLE rotationAngle = PREVIEW_ROTATION_ANGLE.ROT_90;
	private DrawView drawView;
	private ImageView switchCameraButton;
	private ImageView btn_cameraShot;
	private ImageView btn_imageGallery;
	private Vibrator vibrate;
	private boolean shutterButtonClicked = false;

	private int displayAngle;
	private String userName;
	private int personId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_recognition);
		
		faceObj = SidFaceActivity.faceObj;

		Bundle extras = getIntent().getExtras();
		boolean recognitionMode = extras.getBoolean("IdentifyPerson");

		btn_cameraShot = (ImageView) findViewById(R.id.camera_shot);
		switchCameraButton = (ImageView) findViewById(R.id.camera_facing);
		btn_imageGallery = (ImageView) findViewById(R.id.iv_gallery);

		if(recognitionMode){
			btn_cameraShot.setVisibility(View.GONE);
		}


		vibrate = (Vibrator) LiveRecognition.this.getSystemService(Context.VIBRATOR_SERVICE);
		
		orientationListener = new OrientationEventListener(this) {
			@Override
			public void onOrientationChanged(int orientation) {
			}
		};

		btnCameraShotActionListener();
		btnCameraSwitchActionListener();
		btnGalleryActionListener();

	}

	private void btnGalleryActionListener(){
		btn_imageGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(
						Intent.ACTION_PICK,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				);
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, IMAGE_PICK), 0
				);
			}
		});

	}

	private void btnCameraShotActionListener() {
		btn_cameraShot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrate.vibrate(80);

				shutterButtonClicked = true;

			}
		});
	}

	private void btnCameraSwitchActionListener() {
		switchCameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrate.vibrate(80);
				if (cameraFacingFront) {
					switchCameraButton
							.setImageResource(R.drawable.ic_camera_rear_white_24dp);
					cameraFacingFront = false;
				} else {
					switchCameraButton
							.setImageResource(R.drawable.ic_camera_front_white_24dp);
					cameraFacingFront = true;
				}
				stopCamera();
				startCamera();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.live_recognition, menu);
		return true;
	}
	
	protected void onPause() {
		super.onPause();
		stopCamera();
	}
	
	protected void onDestroy() {
		super.onDestroy();
	}
	
	protected void onResume() {
		super.onResume();
		if (cameraObj != null) {
			stopCamera();
		}
		startCamera();
	}
	
	/*
	 * Stops the camera preview. Releases the camera. Make the objects null.
	 */
	private void stopCamera() {
		
		if (cameraObj != null) {
			cameraObj.stopPreview();
			cameraObj.setPreviewCallback(null);
			preview.removeView(mPreview);
			cameraObj.release();
		}
		cameraObj = null;
	}
	
	/*
	 * Method that handles initialization and starting of camera.
	 */
	long t_startCamera = 0;
	private void startCamera() {

		t_startCamera = System.nanoTime();

		if (cameraFacingFront) {
			int FRONT_CAMERA_INDEX = 1;
			cameraObj = Camera.open(FRONT_CAMERA_INDEX); // Open the Front camera
		} else {
			int BACK_CAMERA_INDEX = 0;
			cameraObj = Camera.open(BACK_CAMERA_INDEX); // Open the back camera
		}
		// Create a new surface on which Camera will be displayed.
		mPreview = new CameraSurfacePreview(LiveRecognition.this, cameraObj,
				orientationListener);
		preview = (FrameLayout) findViewById(R.id.cameraPreview2);
		preview.addView(mPreview);
		cameraObj.setPreviewCallback(LiveRecognition.this);
		frameWidth = cameraObj.getParameters().getPreviewSize().width;
		frameHeight = cameraObj.getParameters().getPreviewSize().height;
	}
	
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		boolean result;
		faceObj.setProcessingMode(FP_MODES.FP_MODE_VIDEO);
		if (cameraFacingFront) {
			result = faceObj.setFrame(data, frameWidth, frameHeight, true,
					rotationAngle);
		} else {
			result = faceObj.setFrame(data, frameWidth, frameHeight, false,
					rotationAngle);
		}
		if (result) {
			int numFaces = faceObj.getNumFaces();
			if (numFaces == 0) {
				Log.d("TAG", "No Face Detected");
				if (drawView != null) {
					preview.removeView(drawView);
					drawView = new DrawView(this, null, false);
					preview.addView(drawView);
				}
			} else {
				Log.e(TAG, "onPreviewFrame: " );
				FaceData[] faceDataArray = faceObj.getFaceData();
				if (faceDataArray == null) {
					Log.e("TAG", "Face array is null");
				} else {
//					Action for Found Similar Face
					int surfaceWidth = mPreview.getWidth();
					int surfaceHeight = mPreview.getHeight();
					faceObj.normalizeCoordinates(surfaceWidth, surfaceHeight);

//					Remove the previously created view to avoid unnecessary stacking of Views.
					preview.removeView(drawView);
					drawView = new DrawView(this, faceDataArray, true);
					Log.e(TAG, "onPreviewFrame: "+faceDataArray[0].getPersonId() );
					Log.e(TAG, "onPreviewFrame: "+(System.nanoTime() - t_startCamera)/1000000000.0D );
					preview.addView(drawView);

//					Activate Camera Shutter Button
					if(shutterButtonClicked){
						shutterButtonClicked = false;
						camera.takePicture(shutterCallback, rawCallback, jpegCallback);
					}
				}
			}
		}
	}


	Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
		public void onShutter() {
			Log.d("TAG", "onShutter'd");
		}
	};

	Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d("TAG", "onPictureTaken - raw");
		}
	};

	Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			usePicture(data);
		}

	};

	private void usePicture(byte[] data) {
		Intent intent = new Intent(this, ImageConfirmation.class);
//		Intent intent = new Intent(this, ConfirmImage.class);
		if (data != null) {

			intent.putExtra("org.sid.sidface.ImageConfirmation", data);
		}
		intent.putExtra(
				"org.sid.sidface.ImageConfirmation.switchCamera",
				cameraFacingFront);
		intent.putExtra(
				"org.sid.sidface.ImageConfirmation.orientation",
				displayAngle);
		intent.putExtra("Username", userName);
		intent.putExtra("PersonId", personId);
		boolean updatePerson = false;
		intent.putExtra("UpdatePerson", updatePerson);
		boolean identifyPerson = false;
		intent.putExtra("IdentifyPerson", identifyPerson);
		startActivityForResult(intent, 1);
	}


	protected void onActivityResult(int requestCode, int finalResultCode,
									Intent returnedImage) {
		super.onActivityResult(requestCode, finalResultCode, returnedImage);

		switch (requestCode) {
			case 0:
				if (finalResultCode == RESULT_OK) {
					ContentResolver resolver = getContentResolver();
					Uri userSelectedImage = returnedImage.getData();
					String[] projection = { PROJECTION_PATH };
					Cursor csr = resolver.query(userSelectedImage, projection,
							null, null, null);
					csr.moveToFirst();
					int selectedIndex = 0;
					String path = csr.getString(selectedIndex);
					csr.close();
					Bitmap bitmap = BitmapFactory.decodeFile(path);

					// Convert to byte array
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					if (bitmap != null) {
						Log.e(TAG, "Bitmap is not NULL");
						bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
						byte[] byteArray = stream.toByteArray();
						Intent in1 = new Intent(LiveRecognition.this,
								ImageAnalyzer.class);
//							ImageConfirmation.class);
						in1.putExtra(
								"org.sid.sidface.ImageConfirmation",
								byteArray);
						in1.putExtra(
								"org.sid.sidface.ImageConfirmation.switchCamera",
								true);
						in1.putExtra(
								"org.sid.sidface.ImageConfirmation.through.gallery",
								true);
						in1.putExtra(
								"org.sid.sidface.ImageConfirmation.orientation",
								0);
						in1.putExtra("Username", userName);
						in1.putExtra("PersonId", personId);
//						in1.putExtra("UpdatePerson", updatePerson);
//						in1.putExtra("IdentifyPerson", identifyPerson);
						startActivityForResult(in1, 1);
					} else {
						Log.e(TAG, "Bitmap is NULL");
					}
				}
		}
	}


}
