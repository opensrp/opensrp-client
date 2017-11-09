package layout.face.fr.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing.FP_MODES;

import org.ei.opensrp.indonesia.BidanHomeActivity;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.face.fr.CameraSurfaceView;
import org.ei.opensrp.indonesia.face.fr.DrawView;
import org.ei.opensrp.indonesia.lib.FlurryFacade;

import java.util.HashMap;

/**
 * Created by wildan on 12/21/16.
 */
public class PhotoLive extends Activity implements Camera.PreviewCallback {

    private FacialProcessing faceObj;
//    static FacialProcessing faceObj = KIDetailActivity.faceObj;
    private static final String IMAGE_PICK = "Image Pick";
    private static final String TAG = PhotoLive.class.getSimpleName();
    ImageView btn_cameraShot;
    ImageView btn_cameraSwitch;
    ImageView btn_imageGallery;
    public final int confidence_value = 58;
    public static boolean activityStartedOnce = false;
    public static final String ALBUM_NAME = "serialize_deserialize";
    public static final String HASH_NAME = "HashMap";
    HashMap<String, String> hash;
    Vibrator vibrate;
    private OrientationEventListener orientationListener;
    private boolean cameraFacingFront = true;

    private Camera cameraObj;
    private FrameLayout preview;
    private CameraSurfaceView mPreview;

    private int frameWidth;
    private int frameHeight;
    private static FacialProcessing.PREVIEW_ROTATION_ANGLE rotationAngle = FacialProcessing.PREVIEW_ROTATION_ANGLE.ROT_0;
    private DrawView drawView;
    private boolean shutterButtonClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_recognition);

//        faceObj = (FacialProcessing) FacialProcessing.getInstance();
        faceObj = BidanHomeActivity.faceObj;
//        initSingleRun();

        initGui();

        initActionListener();

        Bundle extras = getIntent().getExtras();
        boolean recognitionMode = extras.getBoolean("IdentifyPerson");
        String entityId = extras.getString("org.sid.sidface.ImageConfirmation.id");

        Log.e(TAG, "onCreate: "+entityId );

        if(recognitionMode){
            btn_cameraShot.setVisibility(View.GONE);
        }
        vibrate = (Vibrator) PhotoLive.this.getSystemService(Context.VIBRATOR_SERVICE);
        orientationListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
            }
        };

    }

    private void initSingleRun() {

        if (!activityStartedOnce) {
            activityStartedOnce = true;
            // Check if FacialActivity Recognition feature is supported, else give alert.
            boolean isSupported = FacialProcessing.isFeatureSupported(FacialProcessing.FEATURE_LIST.FEATURE_FACIAL_RECOGNITION);
            if (isSupported) {
//                Log.d(TAG, "Feature FacialActivity Recognition is supported");
                FlurryFacade.logEvent("Awesome!, Feature FacialActivity Recognition is supported");

                faceObj = FacialProcessing.getInstance();
                loadAlbum(); // De-serialize a previously stored album.
                if (faceObj != null) {
                    faceObj.setRecognitionConfidence(confidence_value);
                    faceObj.setProcessingMode(FacialProcessing.FP_MODES.FP_MODE_STILL);
                }
            } else {
//                Log.e(TAG, "Feature FacialActivity Recognition is NOT supported");
                FlurryFacade.logEvent("Sorry, FacialActivity Recognition Feature is NOT supported!");
                AlertDialog.Builder builder= new AlertDialog.Builder(this);

                builder.setTitle("Incompatible Hardware!");
                builder.setMessage("Your Smartphone doesn't support Qualcomm's FacialActivity Recognition feature.");
                builder.setNegativeButton("OK",
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                PhotoLive.this.finish();
                            }});
                builder.show();

            }
        }
    }

    private void initGui() {
        btn_cameraShot = (ImageView) findViewById(R.id.camera_shot);
        btn_cameraSwitch = (ImageView) findViewById(R.id.camera_facing);
        btn_imageGallery = (ImageView) findViewById(R.id.iv_gallery);
    }

    private void initActionListener() {
        btn_cameraShot.setOnClickListener(camListener);
        btn_cameraSwitch.setOnClickListener(camListener);
        btn_imageGallery.setOnClickListener(camListener);

    }

    private OnClickListener camListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.camera_shot:
                    take_picture();
                    break;
                case R.id.camera_facing:
                    switch_camera();
                    break;
                case R.id.iv_gallery:
                    get_photo();
                    break;
            }
        }

    };

    private void take_picture() {
        vibrate.vibrate(80);

        shutterButtonClicked = true;

    }

    private void switch_camera() {
        if(cameraFacingFront){
            btn_cameraSwitch.setImageResource(R.drawable.ic_camera_rear_white_24dp);
        } else {
            btn_cameraSwitch.setImageResource(R.drawable.ic_camera_front_white_24dp);
        }

        stopCamera();
        startCamera();
    }

    private void stopCamera() {
        if(cameraObj!=null){
            cameraObj.stopPreview();
            cameraObj.setPreviewCallback(null);
            preview.removeView(mPreview);
            cameraObj.release();
        }

        cameraObj = null;
    }

    private void startCamera() {

        int FRONT_CAMERA_INDEX = 1;
        int REAR_CAMERA_INDEX = 0;
        cameraObj = (cameraFacingFront)? Camera.open(FRONT_CAMERA_INDEX): Camera.open(REAR_CAMERA_INDEX);

//        Surface
        mPreview = new CameraSurfaceView(PhotoLive.this, cameraObj, orientationListener);
        preview = (FrameLayout) findViewById(R.id.cameraPreview2);
        preview.addView(mPreview);
        cameraObj.setPreviewCallback(PhotoLive.this);

        frameWidth = cameraObj.getParameters().getPreviewSize().width;
        frameHeight = cameraObj.getParameters().getPreviewSize().height;
        Log.e(TAG, "startCamera: widthxheight = "+frameWidth+"x"+frameHeight );
//        Samsung s2 wxh 1920x1080
    }

    private void get_photo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, IMAGE_PICK), 0
        );

    }

    public void loadAlbum() {
        Log.e(TAG, "loadAlbum: "+"Start" );
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
//            Log.e(TAG, "De-Serialized my album");
        }
    }

//
//    @Override
//    public void onStop() {
//        super.onStop();
////        faceObj.release();
//
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (cameraObj != null) {
            stopCamera();
        }
        startCamera();

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        boolean result;

//        if(faceObj == null){
//            Toast.makeText(PhotoLive.this, "FaceObject is Null", Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "onPreviewFrame: "+"Null FaceObj" );
//        } else {
//            Log.e(TAG, "onPreviewFrame: "+"Exist FaceObj" );
//        }

        faceObj.setProcessingMode(FP_MODES.FP_MODE_VIDEO);

        if (cameraFacingFront) {
            result = faceObj.setFrame(data, frameWidth, frameHeight, true, rotationAngle);
        } else {
            result = faceObj.setFrame(data, frameWidth, frameHeight, false, rotationAngle);
        }

        if (result) {
//            Checking if Face Detected
            int numFaces = faceObj.getNumFaces();
            if (numFaces == 0) {
                Log.d(TAG, "No Face Detected");
                if (drawView != null) {
                    preview.removeView(drawView);
                    drawView = new DrawView(this, null, false);
                    preview.addView(drawView);
                }
            } else {
                Log.e(TAG, "onPreviewFrame: " );
                FaceData[] faceDataArray = faceObj.getFaceData();
//				Checking Similar Face found
                if (faceDataArray == null) {
                    Log.e(TAG, "Face array is null");
                } else {
                    int surfaceWidth = mPreview.getWidth();
                    int surfaceHeight = mPreview.getHeight();
                    faceObj.normalizeCoordinates(surfaceWidth, surfaceHeight);

//					Remove the previously created view to avoid unnecessary stacking of Views.
                    preview.removeView(drawView);
                    drawView = new DrawView(this, faceDataArray, true);
//                    Log.e(TAG, "onPreviewFrame: "+faceDataArray[0].getPersonId() );
//                    Log.e(TAG, "onPreviewFrame: "+(System.nanoTime() - t_startCamera)/1000000000.0D );
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
            Log.d(TAG, "onJeprt");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "RAW onPictureTaken");
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            usePicture(data);
        }

    };

    private void usePicture(byte[] data) {
//        Intent intent = new Intent(this, ImageConfirmation.class);
        Intent intent = new Intent(this, ConfirmImage.class);
        if (data != null) {
            intent.putExtra("org.sid.sidface.ImageConfirmation", data);
        }
        intent.putExtra("org.sid.sidface.ImageConfirmation.switchCamera", cameraFacingFront);

        int displayAngle = 90;
        String userName = "";
        int personId = 0;

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

}
