
package layout.face.sidface.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.widget.GridView;
import android.widget.Toast;

import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing.FEATURE_LIST;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing.FP_MODES;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

public class SidFaceActivity extends Activity {
	
	private GridView gridView;
	public static FacialProcessing faceObj;
	public final String TAG = "SidFaceActivity";
	public final int confidence_value = 58;
	public static boolean activityStartedOnce = false;
	public static final String ALBUM_NAME = "serialize_deserialize";
	public static final String HASH_NAME = "HashMap";
	HashMap<String, String> hash;
	Vibrator vibrate;
	public int numId;
	public int numUid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_facial_recognition);

//		initFirebase();

//		initRecords();

		initSingleRun();

		initVibrate();

//		initGui();
	}

	private void initVibrate() {
		vibrate = (Vibrator) SidFaceActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
	}

	private void initSingleRun() {

		if (!activityStartedOnce) {
			activityStartedOnce = true;
			// Check if Facial Recognition feature is supported in the device
			boolean isSupported = FacialProcessing
					.isFeatureSupported(FEATURE_LIST.FEATURE_FACIAL_RECOGNITION);
			if (isSupported) {
				Log.d(TAG, "Feature Facial Recognition is supported");
				faceObj = (FacialProcessing) FacialProcessing.getInstance();
				loadAlbum(); // De-serialize a previously stored album.
				if (faceObj != null) {
					faceObj.setRecognitionConfidence(confidence_value);
					faceObj.setProcessingMode(FP_MODES.FP_MODE_STILL);
				}
			} else // If Facial recognition feature is not supported then
			// display an alert box.
			{
				Log.e(TAG, "Feature Facial Recognition is NOT supported");
				new AlertDialog.Builder(this)
						.setMessage(
								"Your device does NOT support Qualcomm's Facial Recognition feature. ")
						.setCancelable(false)
						.setNegativeButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int id) {
										SidFaceActivity.this.finish();
									}
								}).show();
			}
		}
	}

	private void prosesFoto(int paramInt, byte[] paramArrayOfByte, String paramString) {
		Bitmap storedBitmap = BitmapFactory.decodeByteArray(paramArrayOfByte, 0, paramArrayOfByte.length, null);
		if (this.faceObj.setBitmap(storedBitmap))
		{
			FaceData[] faceDataArray = this.faceObj.getFaceData();
			if (faceDataArray != null)
			{
				paramInt = 0;
				while (paramInt < faceDataArray.length)
				{
					int i = this.faceObj.addPerson(paramInt);
					hash.put(paramString, Integer.toString(i));
					SidFaceActivity faceRecog = null;
					faceRecog.saveHash(hash, getApplicationContext());
					Log.e("LoadImages", "prosesFoto: " + paramString + " faceDataArray length " + faceDataArray.length + " Add Person " + i);
					paramInt += 1;
				}
			}
			Log.e("LoadImages", "prosesFoto: No Face Detected");
			return;
		}
		Log.e("LoadImages", "prosesFoto: Set Bitmap Failed");
	}

	private void addNewPerson() {
//		Intent intent = new Intent(this, AddPhoto.class);
		Intent intent = new Intent(this, LiveRecognition.class);
		intent.putExtra("Username", "null");
		intent.putExtra("PersonId", -1);
		intent.putExtra("UpdatePerson", false);
		intent.putExtra("IdentifyPerson", false);
		startActivity(intent);
	}


	private void liveRecognition() {
		Intent intent = new Intent(this, LiveRecognition.class);
		intent.putExtra("IdentifyPerson", true);
		startActivity(intent);
	}
	
	private void resetAlbum() {

		AlertDialog.Builder builder= new AlertDialog.Builder(this);

		builder.setTitle("Are you Sure?");
		builder.setMessage("All photos and media will lose!");
		builder.setNegativeButton("CANCEL", null);
		builder.setPositiveButton("ERASE", doEmpty);
		builder.show();
	}

	private DialogInterface.OnClickListener doEmpty = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
			boolean result = faceObj.resetAlbum();
			if (result) {
				HashMap<String, String> hashMap = retrieveHash(getApplicationContext());
				hashMap.clear();
				saveHash(hashMap, getApplicationContext());
				saveAlbum();
				Toast.makeText(getApplicationContext(),
						"Album Reset Successful.",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(
						getApplicationContext(),
						"Internal Error. Reset album failed",
						Toast.LENGTH_LONG).show();
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.facial_recognition, menu);
		return true;
	}
	
	protected void onPause() {
		super.onPause();
	}
	
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "Destroyed");
		if (faceObj != null) // If FacialProcessing object is not released, then
								// release it and set it to null
		{
			faceObj.release();
			faceObj = null;
			Log.d(TAG, "Face Recog Obj released");
		} else {
			Log.d(TAG, "In Destroy - Face Recog Obj = NULL");
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			SidFaceActivity.this.finishAffinity();
		}
		activityStartedOnce = false;
	}

	protected void saveHash(HashMap<String, String> hashMap, Context context) {
		SharedPreferences settings = context.getSharedPreferences(HASH_NAME, 0);
		
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		Log.e(TAG, "Hash Save Size = " + hashMap.size());
		for (String s : hashMap.keySet()) {
			editor.putString(s, hashMap.get(s));
		}
		editor.apply();
	}

	public HashMap<String, String> retrieveHash(Context context) {
		SharedPreferences settings = context.getSharedPreferences(HASH_NAME, 0);
		HashMap<String, String> hash = new HashMap<String, String>();
		hash.putAll((Map<? extends String, ? extends String>) settings.getAll());
		return hash;
	}

	public void saveAlbum() {
		byte[] albumBuffer = faceObj.serializeRecogntionAlbum();
		SharedPreferences settings = getSharedPreferences(ALBUM_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("albumArray", Arrays.toString(albumBuffer));
		editor.apply();
	}

	public void loadAlbum() {
		SharedPreferences settings = getSharedPreferences(ALBUM_NAME, 0);
		String arrayOfString = settings.getString("albumArray", null);

		byte[] albumArray = null;
		if (arrayOfString != null) {
			String[] splitStringArray = arrayOfString.substring(1,
					arrayOfString.length() - 1).split(", ");

			albumArray = new byte[splitStringArray.length];
			for (int i = 0; i < splitStringArray.length; i++) {
				albumArray[i] = Byte.parseByte(splitStringArray[i]);
			}
			faceObj.deserializeRecognitionAlbum(albumArray);
			Log.e("TAG", "De-Serialized my album");
		}
	}

}
