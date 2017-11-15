package layout.face.fr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing.FP_MODES;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing.PREVIEW_ROTATION_ANGLE;

import org.ei.opensrp.indonesia.R;

import java.util.HashMap;
import java.util.Map;

public class LiveRecognitionActivity extends Activity implements Camera.PreviewCallback {

    public final String TAG = LiveRecognitionActivity.class.getSimpleName();
    Camera objCamera;
    FrameLayout preview;
    CameraSurfaceView mPreview;
    private int FRONT_CAMERA_INDEX = 1;
    private int BACK_CAMERA_INDEX = 0;
    private OrientationEventListener orientationListener;
    private FacialProcessing faceObj;
    private int frameWidth;
    private int frameHeight;
    private boolean cameraFacingFront = true;
    private static PREVIEW_ROTATION_ANGLE rotationAngle = PREVIEW_ROTATION_ANGLE.ROT_90;
    private DrawView drawView;
    private FaceData[] faceArray; // Array in which all the face data values will be returned for each face detected.
    private ImageView switchCameraButton;
    private Vibrator vibrate;
    private GridView gridView;
    public final int confidence_value = 58;
    public static boolean activityStartedOnce = false;
    public static final String ALBUM_NAME = "serialize_deserialize";
    public static final String HASH_NAME = "HashMap";
    HashMap<String, String> hash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_recognition);

        // Retrieve the previously saved Hash Map.
        hash = retrieveHash(getApplicationContext()); 

        // Check to make sure FacialProcessing object is not created multiple times.
        if (!activityStartedOnce)
        {
            activityStartedOnce = true;
            // Check if FacialActivity Recognition feature is supported in the device
            // boolean isSupported = FacialProcessing.isFeatureSupported(FEATURE_LIST.FEATURE_FACIAL_RECOGNITION);
            boolean isSupported = true;
            if (isSupported) {
                Log.d(TAG, "Feature FacialActivity Recognition is supported");
                faceObj = FacialProcessing.getInstance();

                loadAlbum();

                if (faceObj != null) {
                    faceObj.setRecognitionConfidence(confidence_value);
                    faceObj.setProcessingMode(FP_MODES.FP_MODE_STILL);
                }
            } else {
                Log.e(TAG, "Feature FacialActivity Recognition is NOT supported");
                new AlertDialog.Builder(this)
                        .setMessage(
                                "Your device does NOT support Qualcomm's FacialActivity Recognition feature. ")
                        .setCancelable(false)
                        .setNegativeButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // super.finish();
                                    }
                                }).show();
            }
        }


        // faceObj = FacialRecognitionActivity.faceObj;
        switchCameraButton = (ImageView) findViewById(R.id.camera_facing);
        vibrate = (Vibrator) LiveRecognitionActivity.this
                .getSystemService(Context.VIBRATOR_SERVICE);

        orientationListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {

            }
        };

        switchCameraButton.setOnClickListener(new OnClickListener() {

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
//        getMenuInflater().inflate(R.menu.live_recognition, menu);
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
        if (objCamera != null) {
            stopCamera();
        }
        startCamera();
    }

    /*
     * Stops the camera preview. Releases the camera. Make the objects null.
     */
    private void stopCamera() {

        if (objCamera != null) {
            objCamera.stopPreview();
            objCamera.setPreviewCallback(null);
            preview.removeView(mPreview);
            objCamera.release();
        }
        objCamera = null;
    }

    /*
     * Method that handles initialization and starting of camera.
     */
    private void startCamera() {
        if (cameraFacingFront) {
            objCamera = Camera.open(FRONT_CAMERA_INDEX); // Open the Front camera
        } else {
            objCamera = Camera.open(BACK_CAMERA_INDEX); // Open the back camera
        }
        mPreview = new CameraSurfaceView(LiveRecognitionActivity.this, objCamera,
                orientationListener); // Create a new surface on which Camera will be displayed.
        preview = (FrameLayout) findViewById(R.id.cameraPreview2);
        preview.addView(mPreview);
        objCamera.setPreviewCallback(LiveRecognitionActivity.this);
        frameWidth = objCamera.getParameters().getPreviewSize().width;
        frameHeight = objCamera.getParameters().getPreviewSize().height;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        boolean result = false;
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
                Log.d(TAG, "No Face Detected");
                if (drawView != null) {
                    preview.removeView(drawView);
                    drawView = new DrawView(this, null, false);
                    preview.addView(drawView);
                }
            } else {
                faceArray = faceObj.getFaceData();
                if (faceArray == null) {
                    Log.e(TAG, "Face array is null");
                } else {
                    int surfaceWidth = mPreview.getWidth();
                    int surfaceHeight = mPreview.getHeight();
                    faceObj.normalizeCoordinates(surfaceWidth, surfaceHeight);
                    preview.removeView(drawView); // Remove the previously created view to avoid unnecessary stacking of
                    // Views.
                    drawView = new DrawView(this, faceArray, true);
                    preview.addView(drawView);

                    //System.out.println("FACE FOUND 1: " + faceArray[0].toString());
                    // Return to main window, sent the server that you have found the guy!
                    this.finish();
                }
            }
        }
    }

    public void loadAlbum() {
//        String arrayOfString = new String("[-92, 3, 0, 0, 76, 65, -68, -20, 77, 116, 46, 83, 105, 110, 97, 105, 6, 0, 0, 0, -24, 3, 0, 0, 10, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, -59, -77, 127, 38, 63, 49, 49, 79, 12, -23, -56, -31, -85, -5, 4, -3, -21, 95, 27, -49, -71, 17, 51, 19, 19, -44, -16, -40, 2, -38, -22, -10, 42, -2, -15, -39, -60, 13, 26, -61, -25, 12, -37, 45, -17, 5, -15, -38, 28, 8, -47, -2, 4, -23, 26, 54, -20, 10, -5, -23, 21, 43, -20, 51, 24, 1, 6, 58, -13, 16, -8, 16, 22, 14, -65, -4, -3, -23, -28, -7, 56, -46, -9, 31, -13, -6, 27, 37, 14, 1, -23, -4, -16, -10, 40, 17, -45, -36, 64, -41, -1, 2, 44, -17, 60, 11, -5, 52, -18, -19, -11, 14, -25, -42, 37, -10, -11, -16, -22, -9, -4, -23, -21, 30, -25, 49, 10, -47, 2, -30, 15, -6, 1, -10, -33, 2, -39, -15, -8, -24, -24, 22, 26, -9, 1, 0, 0, 0, 0, 0, 0, 0, -59, -77, 127, 38, 63, 49, 49, 79, 12, -23, -56, -31, -85, -5, 4, -3, -21, 95, 27, -49, -71, 17, 51, 19, 19, -44, -16, -40, 2, -38, -22, -10, 42, -2, -15, -39, -60, 13, 26, -61, -25, 12, -37, 45, -17, 5, -15, -38, 28, 8, -47, -2, 4, -23, 26, 54, -20, 10, -5, -23, 21, 43, -20, 51, 24, 1, 6, 58, -13, 16, -8, 16, 22, 14, -65, -4, -3, -23, -28, -7, 56, -46, -9, 31, -13, -6, 27, 37, 14, 1, -23, -4, -16, -10, 40, 17, -45, -36, 64, -41, -1, 2, 44, -17, 60, 11, -5, 52, -18, -19, -11, 14, -25, -42, 37, -10, -11, -16, -22, -9, -4, -23, -21, 30, -25, 49, 10, -47, 2, -30, 15, -6, 1, -10, -33, 2, -39, -15, -8, -24, -24, 22, 26, -9, 1, 0, 0, 0, -22, -9, 79, 3, 39, 101, 43, 32, -7, -16, -61, 28, -15, 41, -5, 96, 5, -17, -25, 28, -20, -33, -14, -46, -39, 65, -85, -28, -35, 58, -30, -25, -12, 59, 58, -21, -24, 34, -6, -8, 53, -5, -21, 66, 56, 81, -2, -15, 8, 3, 3, 0, 45, 69, 17, 11, 44, -43, 16, 29, -7, -35, -25, 38, -18, -28, -3, -28, 18, 33, -3, -44, -19, 6, -13, 0, 14, -21, 20, 1, -28, 14, 2, -16, -16, 48, -5, 11, -71, -16, -26, 65, 13, 81, -16, -14, -4, -23, -47, -2, 17, -85, 15, -50, -25, -34, 5, 1, 6, 6, 14, 29, -2, 19, 44, -37, 13, -4, -3, -39, -28, -34, 39, 23, -2, -4, -19, 31, 0, 33, 37, -69, 33, -4, 3, 22, -26, -13, -43, -14, 3, 4, -5, -35, 1, 0, 0, 0, 0, 0, 0, 0, -22, -9, 79, 3, 39, 101, 43, 32, -7, -16, -61, 28, -15, 41, -5, 96, 5, -17, -25, 28, -20, -33, -14, -46, -39, 65, -85, -28, -35, 58, -30, -25, -12, 59, 58, -21, -24, 34, -6, -8, 53, -5, -21, 66, 56, 81, -2, -15, 8, 3, 3, 0, 45, 69, 17, 11, 44, -43, 16, 29, -7, -35, -25, 38, -18, -28, -3, -28, 18, 33, -3, -44, -19, 6, -13, 0, 14, -21, 20, 1, -28, 14, 2, -16, -16, 48, -5, 11, -71, -16, -26, 65, 13, 81, -16, -14, -4, -23, -47, -2, 17, -85, 15, -50, -25, -34, 5, 1, 6, 6, 14, 29, -2, 19, 44, -37, 13, -4, -3, -39, -28, -34, 39, 23, -2, -4, -19, 31, 0, 33, 37, -69, 33, -4, 3, 22, -26, -13, -43, -14, 3, 4, -5, -35, 2, 0, 0, 0, 2, -40, 12, -12, -18, 7, 33, 11, 2, -101, 8, -62, 39, -24, 39, -32, 33, 39, 23, -22, -33, -31, 21, -13, -10, -54, -40, -21, 31, 17, -37, 21, 26, 9, -48, -34, -35, 12, -39, -41, 25, -66, -16, 31, -27, 57, 12, 0, 28, 15, 0, 4, -38, -60, -53, 5, -36, 6, -10, -33, 53, 44, -3, 16, -89, -10, 31, 3, -30, -42, -48, 76, -21, 54, 29, 0, -30, 83, -62, -23, 10, 1, 18, 30, 4, -20, 16, 27, 5, -16, 27, -9, 26, -61, -18, -9, -30, 51, -28, -22, 32, -27, -1, -15, -21, -36, 23, -75, -59, 8, -6, 49, -36, 34, 6, 48, -22, 11, -23, 5, -12, 10, -64, 13, -37, 9, 25, -24, -25, 5, -37, -9, 32, 13, 31, -80, -58, -23, 46, 12, -11, 2, 49, -58, 1, 0, 0, 0, 0, 0, 0, 0, 2, -40, 12, -12, -18, 7, 33, 11, 2, -101, 8, -62, 39, -24, 39, -32, 33, 39, 23, -22, -33, -31, 21, -13, -10, -54, -40, -21, 31, 17, -37, 21, 26, 9, -48, -34, -35, 12, -39, -41, 25, -66, -16, 31, -27, 57, 12, 0, 28, 15, 0, 4, -38, -60, -53, 5, -36, 6, -10, -33, 53, 44, -3, 16, -89, -10, 31, 3, -30, -42, -48, 76, -21, 54, 29, 0, -30, 83, -62, -23, 10, 1, 18, 30, 4, -20, 16, 27, 5, -16, 27, -9, 26, -61, -18, -9, -30, 51, -28, -22, 32, -27, -1, -15, -21, -36, 23, -75, -59, 8, -6, 49, -36, 34, 6, 48, -22, 11, -23, 5, -12, 10, -64, 13, -37, 9, 25, -24, -25, 5, -37, -9, 32, 13, 31, -80, -58, -23, 46, 12, -11, 2, 49, -58]");

        String arrayOfString = new String("[]");
        byte[] albumArray;
        if (arrayOfString != null) {
            String[] splitStringArray = arrayOfString.substring(1,
                    arrayOfString.length() - 1).split(", ");

            albumArray = new byte[splitStringArray.length];
            for (int i = 0; i < splitStringArray.length; i++) {
                albumArray[i] = Byte.parseByte(splitStringArray[i]);
            }
            faceObj.deserializeRecognitionAlbum(albumArray);
            Log.e(TAG, "De-Serialized my album");
        }
    }

    /*
     * Function to retrieve a HashMap from the Shared preferences.
     * @return
     */
    protected HashMap<String, String> retrieveHash(Context context) {
        SharedPreferences settings = context.getSharedPreferences(HASH_NAME, 0);
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.putAll((Map<? extends String, ? extends String>) settings.getAll());
        return hash;
    }

    /*
     * Function to store a HashMap to shared preferences.
     * @param hash
     */
    protected void saveHash(HashMap<String, String> hashMap, Context context) {
        SharedPreferences settings = context.getSharedPreferences(HASH_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        Log.e(TAG, "HashSize = " + hashMap.size());
        for (String s : hashMap.keySet()) {
            editor.putString(s, hashMap.get(s));
        }
        editor.commit();
    }


}