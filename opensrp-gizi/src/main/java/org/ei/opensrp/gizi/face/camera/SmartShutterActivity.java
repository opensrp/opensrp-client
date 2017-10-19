package org.ei.opensrp.gizi.face.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing.PREVIEW_ROTATION_ANGLE;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.gizi.R;
import org.ei.opensrp.gizi.face.camera.util.FaceConstants;
import org.ei.opensrp.gizi.face.camera.util.Tools;
import org.ei.opensrp.gizi.fragment.GiziIbuSmartRegisterFragment;
import org.ei.opensrp.gizi.fragment.GiziSmartRegisterFragment;
import org.ei.opensrp.gizi.gizi.GiziSmartRegisterActivity;
import org.ei.opensrp.gizi.giziIbu.IbuSmartRegisterActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SmartShutterActivity extends Activity implements Camera.PreviewCallback {

    private static final String TAG = SmartShutterActivity.class.getSimpleName();
    public static CommonPersonObjectClient kidetail;

    Camera cameraObj;
    FrameLayout preview;
    CameraPreview mPreview;
    public static FacialProcessing faceProc;
    PaintFaceView drawView;
    FaceData[] faceArray;
    private ImageView cameraButton;
    private ImageView settingsButton;
    private ImageView switchCameraButton;
    private ImageView chooseCameraButton;
    private ImageView menu;
    private ImageView faceEyesMouthBtn;
    private ImageView perfectPhotoButton;
    private ImageView galleryButton;
    private ImageView flashButton;
    Display display;

    Animation animationFadeOut;

    AnimationDrawable frameAnimation;
    CheckBox smile;
    CheckBox gazeAngle;
    CheckBox eyeBlink;

    private int FRONT_CAMERA_INDEX = 1;
    private int BACK_CAMERA_INDEX = 0;
    public static boolean isDevCompat;
    private static boolean switchCamera = false;
    private static boolean settingsButtonPress;
    private static boolean faceEyesMouthDetectionPressed;
    private static boolean perfectModeButtonPress;
    private static boolean cameraButtonPress;
    private static boolean animationPress;
    private static String flashButtonPress;
    private int displayAngle;
    private boolean smileFlag;
    private boolean blinkFlag;
    private boolean horizontalGazeAngleFlag;
    private boolean verticalGazeAngleFlag;
    private static boolean activityStartedOnce;
    private int numFaces;
    private static String pathName;
    private static String entityId;
    private boolean identifyPerson = false;

    private ImageView clientListButton;
    HashMap<String, String> hash;
    private String selectedPersonName;
    long t_startCamera = 0;
    double t_stopCamera = 0;
    String str_origin_class;
    private boolean updated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        t_startCamera = System.nanoTime();
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_fr_main_face);

        Bundle extras = getIntent().getExtras();
        updated = extras.getBoolean("org.sid.sidface.SmartShutterActivity.updated");
        entityId = extras.getString("org.sid.sidface.ImageConfirmation.id");
        identifyPerson = extras.getBoolean("org.sid.sidface.ImageConfirmation.identify");
        kidetail = extras.getParcelable("org.sid.sidface.ImageConfirmation.kidetail");
        str_origin_class = extras.getString("org.sid.sidface.ImageConfirmation.origin");

        Log.e(TAG, "onCreate: " + kidetail);

        initializeFlags();

        initializeCheckBoxes();

        animationFadeOut = AnimationUtils.loadAnimation(SmartShutterActivity.this, R.anim.fadeout);

        initializeImageButtons();

        hash = SmartShutterActivity.retrieveHash(getApplicationContext());

        settingsButtonPress = false;


        chooseCameraActionListener();
//        switchCameraActionListener();
        galleryActionListener();
        cameraActionListener();
        settingsActionListener();
        faceDetectionActionListener();
        perfectPhotoActionListener();
        flashActionListener();

        clientListActionListener();

        initCamera();

        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        Tools.loadAlbum(getApplicationContext());


    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraObj != null) {
            stopCamera();
        }
        initCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        setFlagsTrue();
        int dRotation = display.getRotation();
        PREVIEW_ROTATION_ANGLE angleEnum = PREVIEW_ROTATION_ANGLE.ROT_0;

        switch (dRotation) {
            case 0:  // Device is not rotated
                displayAngle = 90;
                angleEnum = PREVIEW_ROTATION_ANGLE.ROT_90;
                break;

            case 1:    // Landscape left
                displayAngle = 0;
                angleEnum = PREVIEW_ROTATION_ANGLE.ROT_0;
                break;

            case 2:  // Device upside down
                displayAngle = 270;
                angleEnum = PREVIEW_ROTATION_ANGLE.ROT_270;
                break;

            case 3:    // Landscape right
                displayAngle = 180;
                angleEnum = PREVIEW_ROTATION_ANGLE.ROT_180;
                break;
        }

        cameraObj.setDisplayOrientation(displayAngle);

        if (isDevCompat) {

//            loadAlbum();
            if (faceProc == null) {
                faceProc = FacialProcessing.getInstance();
            }
//            byte[] dataFace = faceProc.serializeRecogntionAlbum();

//            Log.e(TAG, "onCreate: "+ dataFace.length );

//            faceProc.setProcessingMode(FacialProcessing.FP_MODES.FP_MODE_STILL); // Static Image
            faceProc.setProcessingMode(FacialProcessing.FP_MODES.FP_MODE_VIDEO);

            Parameters params = cameraObj.getParameters();
            Size previewSize = params.getPreviewSize();
//            params.set("iso", 400);

//            Log purpose only
//            int previewWidth = params.getPreviewSize().width;
//            int previewHeight = params.getPreviewSize().height;
//            Log.e(TAG, "Preview Size = " + previewWidth + " x " + previewHeight);

            // View Mode : Landscape - Portrait
            // Landscape mode camera : Front , Back
            if (this.getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE && !switchCamera) {
                faceProc.setFrame(data, previewSize.width, previewSize.height, true, angleEnum);
            } else if (this.getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE && switchCamera) {
                faceProc.setFrame(data, previewSize.width, previewSize.height, false, angleEnum);
            }

            // Portrait mode camera : Front
            else if (this.getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_PORTRAIT && !switchCamera) {
                faceProc.setFrame(data, previewSize.width, previewSize.height, true, angleEnum);
            } else {
                faceProc.setFrame(data, previewSize.width, previewSize.height, false, angleEnum);
            }

            // Number of Face in the frame.
            numFaces = faceProc.getNumFaces();

            if (numFaces == 0) {
//                Log.e(TAG, "No Face Detected");
                smile.setChecked(false);
                eyeBlink.setChecked(false);
                gazeAngle.setChecked(false);

                if (drawView != null) {
                    preview.removeView(drawView);
                    drawView = new PaintFaceView(this, null, false);
                    preview.addView(drawView);
                }

            } else {
//                Log.e(TAG, "Face Detected");
                faceArray = faceProc.getFaceData();

                if (faceArray == null) {

                    Log.e(TAG, "onPreviewFrame: "+ "No Face value" );

                } else {
                    int surfaceWidth = mPreview.getWidth();
                    int surfaceHeight = mPreview.getHeight();

                    faceProc.normalizeCoordinates(surfaceWidth, surfaceHeight);

                    Log.e(TAG, "onPreviewFrame: personId" + faceArray[0].getPersonId());

                    if (identifyPerson && faceArray[0].getPersonId() != -111) {
                        String selectedPersonId = Integer.toString(faceArray[0].getPersonId());
                        Iterator<HashMap.Entry<String, String>> iter = hash.entrySet().iterator();
                        // Default name is the person is unknown
                        selectedPersonName = "Not Identified";
                        while (iter.hasNext()) {
                            Log.e(TAG, "onPreviewFrame: "+"check Hash" );
                            HashMap.Entry<String, String> entry = iter.next();
                            if (entry.getValue().equals(selectedPersonId)) {
                                selectedPersonName = entry.getKey();
                                t_stopCamera = (System.nanoTime() - t_startCamera) / 1000000000.0D;
                            }
                        }

//                        Log.e(TAG, "onPreviewFrame: t_start"+t_startCamera );

                        Class<?> origin_class = this.getClass();

                        Log.e(TAG, "onPreviewFrame: init" + origin_class.getSimpleName());
                        Log.e(TAG, "onPreviewFrame: origin" + str_origin_class);

                        if (str_origin_class.equals(GiziSmartRegisterFragment.class.getSimpleName())) {
                            origin_class = GiziSmartRegisterActivity.class;
                        } else if (str_origin_class.equals(GiziIbuSmartRegisterFragment.class.getSimpleName())) {
                            origin_class = IbuSmartRegisterActivity.class;
                        }

                        Log.e(TAG, "onPreviewFrame: " + origin_class.getSimpleName());
                        Intent intent = new Intent(SmartShutterActivity.this, origin_class);
                        intent.putExtra("org.ei.opensrp.indonesia.face.face_mode", true);
                        intent.putExtra("org.ei.opensrp.indonesia.face.base_id", selectedPersonName);
                        intent.putExtra("org.ei.opensrp.indonesia.face.proc_time", t_stopCamera);

                        startActivity(intent);

                    }

//                    Options
                    if (faceEyesMouthDetectionPressed) {
                        // Remove the previously created view to avoid unnecessary stacking of Views.
                        preview.removeView(drawView);
                        drawView = new PaintFaceView(this, faceArray, true);
                        Log.e(TAG, "onPreviewFrame: " + faceArray[0].getPersonId());
                        preview.addView(drawView);

                    } else {

                        preview.removeView(drawView);
                        drawView = new PaintFaceView(this, null, false);
                        preview.addView(drawView);

                    }

                    if (perfectModeButtonPress) {
                        for (int i = 0; i < numFaces; i++) {
                            if (faceArray[i].getSmileValue() < 75) {
                                smileFlag = false;
                                smile.setChecked(false);
                            } else {
                                smile.setChecked(true);
                            }

                            if (faceArray[i].getLeftEyeBlink() > 50 && faceArray[i].getRightEyeBlink() > 50) {
                                blinkFlag = false;
                                eyeBlink.setChecked(false);
                            } else {
                                eyeBlink.setChecked(true);
                            }

                            if (faceArray[i].getEyeHorizontalGazeAngle() < -8 || faceArray[i].getEyeHorizontalGazeAngle() > 8) {
                                horizontalGazeAngleFlag = false;
                                gazeAngle.setChecked(false);
                            } else if (faceArray[i].getEyeVerticalGazeAngle() < -8 || faceArray[i].getEyeVerticalGazeAngle() > 8) {
                                verticalGazeAngleFlag = false;
                                gazeAngle.setChecked(false);
                            } else {
                                gazeAngle.setChecked(true);
                            }

                        }
                        if (smileFlag && blinkFlag && horizontalGazeAngleFlag && verticalGazeAngleFlag && cameraButtonPress) {
                            try {
                                cameraObj.takePicture(shutterCallback, rawCallback, jpegCallback);
                            } catch (Exception e) {

                            }

                            frameAnimation.stop();
                            cameraButton.setBackgroundResource(R.drawable.ic_camera_alt_white_24dp);
                            cameraButton.invalidate();
                            cameraButtonPress = false;
                            animationPress = false;
                            smile.setVisibility(View.INVISIBLE);
                            gazeAngle.setVisibility(View.INVISIBLE);
                            eyeBlink.setVisibility(View.INVISIBLE);
                            smile.setChecked(false);
                            eyeBlink.setChecked(false);
                            gazeAngle.setChecked(false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    if (requestCode == 0) {
                        Uri selectedImageUri = data.getData();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(selectedImageUri);
                        startActivity(intent);
                    }
                }
                break;
            // For the rest don't do anything.
        }
    }

    /**
     *
     */
    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onShutter'd");
        }
    };

    /**
     *
     */
    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken - raw");
        }
    };

    /**
     *
     */
    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            savePicture(data);
        }

    };

    /**
     *
     */
    private void initializeFlags() {
        isDevCompat = false;
        settingsButtonPress = false;
        faceEyesMouthDetectionPressed = false;
        perfectModeButtonPress = false;
        cameraButtonPress = false;
        animationPress = false;
        flashButtonPress = "FLASH_MODE_OFF";
        smileFlag = true;
        blinkFlag = true;
        horizontalGazeAngleFlag = true;
        verticalGazeAngleFlag = true;
        activityStartedOnce = false;
    }

    // Stop the camera preview. release the camera. Release the FacialActivity Processing object. Make the objects null.
    private void stopCamera() {

        if (cameraObj != null) {
            cameraObj.stopPreview();
            cameraObj.setPreviewCallback(null);
            preview.removeView(mPreview);
            cameraObj.release();
//            if (isDevCompat) {
//                faceProc.release();
//                faceProc = null;
//            }
        }
        cameraObj = null;
    }

    // Start with the camera preview. Open the Camera. See if the feature is supported. Initialize the facial processing instance.

    /**
     *
     */
    private void initCamera() {

        // Check to see if the FacialProc feature is supported in the device or no.
        isDevCompat = FacialProcessing.isFeatureSupported(FacialProcessing.FEATURE_LIST.FEATURE_FACIAL_PROCESSING);

        if (isDevCompat && faceProc == null) {
            Log.e(TAG, "Feature is supported");
            // Calling the FacialActivity Processing Constructor.
            faceProc = FacialProcessing.getInstance();
            faceProc.setRecognitionConfidence(Tools.CONFIDENCE_VALUE);

//            Tools tools = new Tools();
//            Tools.loadAlbum(getApplicationContext());

        } else if (!isDevCompat && !activityStartedOnce) {
            Log.e(TAG, "Feature is NOT supported");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SmartShutterActivity.this);

            // set title
            alertDialogBuilder.setTitle("Not Supported");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Your device does not support Qualcomm's FacialActivity Processing Feature. Continue with the normal camera.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            activityStartedOnce = true;
        }

        if (!switchCamera) {
            // Open the Front camera
            cameraObj = Camera.open(FRONT_CAMERA_INDEX);
        } else {
            // Open the back camera
            cameraObj = Camera.open(BACK_CAMERA_INDEX);
        }

        // Create a new surface on which Camera will be displayed.
        mPreview = new CameraPreview(SmartShutterActivity.this, cameraObj);
        preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
        cameraObj.setPreviewCallback(SmartShutterActivity.this);

    }

    /*
     * Function to Initialize all the image buttons that are there in the view and sets its visibility and image resources here.
     */
    private void initializeImageButtons() {
        cameraButton = (ImageView) findViewById(R.id.cameraButton);     // Camera Shutter Button

        galleryButton = (ImageView) findViewById(R.id.gallery);
        galleryButton.setImageResource(R.drawable.ic_collections_white_24dp);
        galleryButton.setVisibility(View.INVISIBLE);

        settingsButton = (ImageView) findViewById(R.id.settings);
        settingsButton.setVisibility(View.INVISIBLE);

        chooseCameraButton = (ImageView) findViewById(R.id.chooseCamera);
        chooseCameraButton.setImageResource(R.drawable.ic_camera_rear_white_24dp);
        chooseCameraButton.setVisibility(View.VISIBLE);

//        Settings Option Menu

        menu = (ImageView) findViewById(R.id.menu);
        menu.setVisibility(View.INVISIBLE);                    // Initially make menu invisible. Make it visible only when the settings button is pressed.

        switchCameraButton = (ImageView) findViewById(R.id.switchCamera);
        switchCameraButton.setImageResource(R.drawable.ic_camera_front_white_24dp);
        switchCameraButton.setVisibility(View.INVISIBLE);                    // Initially make switchCamera invisible. Make it visible only when the settings button is pressed.

        perfectPhotoButton = (ImageView) findViewById(R.id.perfectMode);
        perfectPhotoButton.setVisibility(View.INVISIBLE);                    // Initially make perfectMode invisible. Make it visible only when the settings button is pressed.
        perfectPhotoButton.setImageResource(R.drawable.ic_perfect_mode_off);

        flashButton = (ImageView) findViewById(R.id.flash);
        flashButton.setVisibility(View.INVISIBLE);                            // Initially make flashButton invisible. Make it visible only when the settings button is pressed.

        clientListButton = (ImageView) findViewById(R.id.clientList);
        clientListButton.setVisibility(View.INVISIBLE);
        clientListButton.setImageResource(R.drawable.ic_faces);

        // Change the flash image depending on the button that is being pressed.
        if (flashButtonPress == "FLASH_MODE_OFF") {
            flashButton.setImageResource(R.drawable.ic_flash_off);
        } else {
            flashButton.setImageResource(R.drawable.ic_flash_green);
        }

        // Detect Eyes and Mouth.
        if (!faceEyesMouthDetectionPressed) {
            faceEyesMouthBtn = (ImageView) findViewById(R.id.faceDetection);
            faceEyesMouthBtn.setImageResource(R.drawable.fr_face_detection);
        } else {
            faceEyesMouthBtn = (ImageView) findViewById(R.id.faceDetection);
            faceEyesMouthBtn.setImageResource(R.drawable.fr_face_detection_on);
        }
        faceEyesMouthBtn.setVisibility(View.INVISIBLE);
    }

    /*
     * Initialize the Check Box Buttons. Initially it will be invisible. Will be visible only when the photo is to be taken.
     */
    private void initializeCheckBoxes() {
        smile = (CheckBox) findViewById(R.id.smileCheckBox);
        smile.setVisibility(View.GONE);
        smile.setTextColor(Color.YELLOW);
        gazeAngle = (CheckBox) findViewById(R.id.gazeAngleCheckBox);
        gazeAngle.setVisibility(View.GONE);
        gazeAngle.setTextColor(Color.YELLOW);
        eyeBlink = (CheckBox) findViewById(R.id.blinkCheckBox);
        eyeBlink.setVisibility(View.GONE);
        eyeBlink.setTextColor(Color.YELLOW);
    }

    private void clientListActionListener() {
        clientListButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SmartShutterActivity.this, ClientsList.class);

                startActivity(intent);
            }
        });

    }

    private void chooseCameraActionListener() {
        chooseCameraButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!switchCamera) {
                    stopCamera();
                    chooseCameraButton.setImageResource(R.drawable.ic_camera_front_white_24dp);
                    switchCamera = true;
                    initCamera();
                } else {
                    stopCamera();
                    chooseCameraButton.setImageResource(R.drawable.ic_camera_rear_white_24dp);
                    switchCamera = false;
                    initCamera();
                }
            }
        });
    }

    /*
     * Function to detect the on click listener for the switch camera button.
     */
    private void switchCameraActionListener() {
        switchCameraButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (!switchCamera)        // Flag to check if the camera is switched to front or back.
                {
                    switchCameraButton.setImageResource(R.drawable.ic_camera_rear_white_24dp);
                    stopCamera();
                    switchCamera = true;
                    settingsButtonPress = false;
                    initCamera();
                    fadeOutAnimation();
                } else {
                    switchCameraButton.setImageResource(R.drawable.ic_camera_front_white_24dp);
                    stopCamera();
                    switchCamera = false;
                    settingsButtonPress = false;
                    initCamera();
                    fadeOutAnimation();
                }
            }
        });
    }

    /*
     * Function to detect the on click listener for the GALLERY button.
     */
    private void galleryActionListener() {
        galleryButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
            }
        });
    }

    /*
     * Function to detect the on click listener for the camera shutter button.
     */
    private void cameraActionListener() {
        cameraButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (numFaces != 0) {
                    if (!perfectModeButtonPress) {
                        cameraObj.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {

                                cameraObj.takePicture(shutterCallback, rawCallback, jpegCallback);

                            }
                        });

                    } else {
                        // Play animation
                        cameraButton.setBackgroundResource(R.drawable.fr_spin_animation);
                        frameAnimation = (AnimationDrawable) cameraButton.getBackground();

                        checkBoxVisiblity(true);    // As soon as the shutter button is pressed, make the check boxes visible.

                        // Start the animation (looped playback by default).
                        if (!animationPress) {
                            frameAnimation.start();
                            animationPress = true;
                            cameraButtonPress = true;
                        } else {
                            // If the shutter button is stopped then make the check boxes invisible
                            checkBoxVisiblity(false);
                            // and un-check them.
                            textBoxChecked(false);
                            frameAnimation.stop();
                            cameraButton.setBackgroundResource(R.drawable.ic_camera_alt_white_24dp);
                            animationPress = false;
                            cameraButtonPress = false;
                        }
                    }
                }
            }
        });
    }

    /*
     * Function to detect the on click listener for the switch camera button.
     */
    private void settingsActionListener() {
        settingsButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!settingsButtonPress) {
                    // Disable the buttons if the facial processing feature is not supported.
                    if (isDevCompat) {
                        faceEyesMouthBtn.setVisibility(View.VISIBLE);
                        perfectPhotoButton.setVisibility(View.VISIBLE);
                        clientListButton.setVisibility(View.VISIBLE);

                    }
                    menu.setVisibility(View.VISIBLE);
                    switchCameraButton.setVisibility(View.VISIBLE);
                    if (switchCamera)// If facing back camera then only make it visible or else dont.
                        flashButton.setVisibility(View.VISIBLE);
                    settingsButtonPress = true;
                } else {
                    faceEyesMouthBtn.setVisibility(View.INVISIBLE);
                    menu.setVisibility(View.INVISIBLE);
                    switchCameraButton.setVisibility(View.INVISIBLE);
                    perfectPhotoButton.setVisibility(View.INVISIBLE);
                    flashButton.setVisibility(View.INVISIBLE);
                    settingsButtonPress = false;
                    clientListButton.setVisibility(View.INVISIBLE);
                }
            }

        });

        // On touch listener for the settings button to make it highlighted when pressed
        settingsButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    settingsButton.setImageResource(R.drawable.ic_settings_green_24dp);
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    settingsButton.setImageResource(R.drawable.ic_settings_white_24dp);
                }
                return false;
            }
        });
    }

    /*
     * Interactive Draw of Eyes and Mouth position.
     */
    private void faceDetectionActionListener() {
        faceEyesMouthBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!faceEyesMouthDetectionPressed) {
                    faceEyesMouthBtn.setImageResource(R.drawable.fr_face_detection_on);
                    fadeOutAnimation();
                    faceEyesMouthDetectionPressed = true;
                    settingsButtonPress = false;
                } else {
                    faceEyesMouthBtn.setImageResource(R.drawable.fr_face_detection);
                    fadeOutAnimation();
                    faceEyesMouthDetectionPressed = false;
                    settingsButtonPress = false;
                }
            }
        });

    }

    /*
    Control Flash Mode
     */
    private void flashActionListener() {
        flashButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Parameters params = cameraObj.getParameters();
                String flashMode = params.getFlashMode();
                if (flashMode == null)
                    return;
                else {
                    // On-Off Flash
                    if (flashButtonPress == "FLASH_MODE_OFF") {
                        params.setFlashMode(Parameters.FLASH_MODE_ON);
                        flashButton.setImageResource(R.drawable.ic_flash_green);
                        cameraObj.setParameters(params);
                        fadeOutAnimation();
                        flashButtonPress = "FLASH_MODE_ON";
                        settingsButtonPress = false;
                        return;
                    } else {
                        params.setFlashMode(Parameters.FLASH_MODE_OFF);
                        flashButton.setImageResource(R.drawable.ic_flash_off);
                        cameraObj.setParameters(params);
                        fadeOutAnimation();
                        flashButtonPress = "FLASH_MODE_OFF";
                        settingsButtonPress = false;
                        return;
                    }
                }
            }
        });

    }

    /*
     * Function to detect the on click listener for the perfect photo mode button.
     */
    private void perfectPhotoActionListener() {
        perfectPhotoButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (perfectModeButtonPress) {
                    perfectPhotoButton.setImageResource(R.drawable.ic_perfect_mode_off);
                    fadeOutAnimation();
                    settingsButtonPress = false;
                    perfectModeButtonPress = false;
                } else {
                    perfectPhotoButton.setImageResource(R.drawable.ic_perfect_mode_on);
                    fadeOutAnimation();
                    settingsButtonPress = false;
                    perfectModeButtonPress = true;
                }
            }
        });
    }

    /* Animation menu display
     *
     */
    private void fadeOutAnimation() {

        // Activated features only Supported Device
        if (isDevCompat) {
            faceEyesMouthBtn.startAnimation(animationFadeOut);
            perfectPhotoButton.startAnimation(animationFadeOut);
        }
        menu.startAnimation(animationFadeOut);
        switchCameraButton.startAnimation(animationFadeOut);
        clientListButton.startAnimation(animationFadeOut);

        if (switchCamera) {
            flashButton.startAnimation(animationFadeOut);
        }
        faceEyesMouthBtn.setVisibility(View.GONE);
        menu.setVisibility(View.GONE);
        switchCameraButton.setVisibility(View.GONE);
        perfectPhotoButton.setVisibility(View.GONE);
        flashButton.setVisibility(View.GONE);

    }

    /*
     * Function to write an image to the file system for future viewing.
     */
    public static boolean WritePictureToFile(Context context, Bitmap bitmap) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.e(TAG, "Error creating media file, check storage permissions ");
            return false;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.e(TAG, "Wrote image to " + pictureFile);

            MediaScannerConnection.scanFile(context, new String[]{pictureFile.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
            pathName = pictureFile.toString();
            Log.e(TAG, "Path Name = " + pathName);
            return true;

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return false;
    }

    /**
     * Create a File for saving an image or video
     */
    @SuppressLint("SimpleDateFormat")
    private static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "OPENSRP_SID");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            Log.e(TAG, "failed to find directory " + mediaStorageDir.getAbsolutePath());
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create directory " + mediaStorageDir.getAbsolutePath());
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String filename = entity);
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + entityId + ".jpg");
        return mediaFile;
    }

    /*
     * Function to take the raw YUV byte array and do the necessary conversions to save it.
     */
    private void savePicture(byte[] data) {
        Intent intent = new Intent(this, ImageConfirmation.class);
        // This is when smart shutter feature is not ON. Take the photo generally.
        if (data != null) {
            intent.putExtra("org.sid.sidface.ImageConfirmation", data);
        }
        intent.putExtra("org.sid.sidface.ImageConfirmation.switchCamera", switchCamera);
        intent.putExtra("org.sid.sidface.ImageConfirmation.orientation", displayAngle);
        intent.putExtra("org.sid.sidface.ImageConfirmation.id", entityId);
        intent.putExtra("org.sid.sidface.ImageConfirmation.identify", identifyPerson);
        intent.putExtra("org.sid.sidface.ImageConfirmation.kidetail", (Parcelable) kidetail);
        intent.putExtra("org.sid.sidface.ImageConfirmation.origin", str_origin_class);
        intent.putExtra("org.sid.sidface.ImageConfirmation.updated", updated);

        startActivityForResult(intent, 1);
    }

    private void setFlagsTrue() {
        smileFlag = true;
        blinkFlag = true;
        horizontalGazeAngleFlag = true;
        verticalGazeAngleFlag = true;
    }

    /*
     * A helper function to handle the visibility of the check boxes.
     */
    private void checkBoxVisiblity(boolean visible) {

        if (visible) {
            smile.setVisibility(View.VISIBLE);
            gazeAngle.setVisibility(View.VISIBLE);
            eyeBlink.setVisibility(View.VISIBLE);
        } else {
            smile.setVisibility(View.INVISIBLE);
            gazeAngle.setVisibility(View.INVISIBLE);
            eyeBlink.setVisibility(View.INVISIBLE);
        }

    }

    /*
     *  A helper function to handle the CHECK-MARK of the Check-Text Boxes.
     */
    private void textBoxChecked(boolean check) {
        if (check) {
            smile.setChecked(true);
            eyeBlink.setChecked(true);
            gazeAngle.setChecked(true);
        } else {
            smile.setChecked(false);
            eyeBlink.setChecked(false);
            gazeAngle.setChecked(false);
        }

    }

    public void loadAlbum() {
//        Toast.makeText(this, "Load FacialActivity Album", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "loadAlbum: start");
        SharedPreferences settings = getSharedPreferences(FaceConstants.ALBUM_NAME, 0);
        String arrayOfString = settings.getString(FaceConstants.ALBUM_ARRAY, null);

        Log.e(TAG, "loadAlbum: " + arrayOfString);
        byte[] albumArray;
        if (arrayOfString != null) {
            String[] splitStringArray = arrayOfString.substring(1,
                    arrayOfString.length() - 1).split(", ");

            albumArray = new byte[splitStringArray.length];
            for (int i = 0; i < splitStringArray.length; i++) {
                albumArray[i] = Byte.parseByte(splitStringArray[i]);
            }
            // Boolean
            SmartShutterActivity.faceProc.deserializeRecognitionAlbum(albumArray);
            Log.e(TAG, "De-Serialized Album Success! " + albumArray.toString());
        }
    }


    /**
     * Get Client List
     * @param context
     * @return
     */
    public static HashMap<String, String> retrieveHash(Context context) {
        SharedPreferences settings = context.getSharedPreferences(FaceConstants.HASH_NAME, 0);
        HashMap<String, String> hash = new HashMap<>();
        hash.putAll((Map<? extends String, ? extends String>) settings.getAll());
        return hash;
    }


    protected void saveHash(HashMap<String, String> hashMap, Context context) {
        SharedPreferences settings = context.getSharedPreferences(FaceConstants.HASH_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        Log.e(TAG, "Hash Save Size Clients List= " + hashMap.size());
        for (String s : hashMap.keySet()) {
            editor.putString(s, hashMap.get(s));
        }
        editor.apply();
    }


}
