package org.smartregister.indonesia.face.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.indonesia.R;
import org.smartregister.indonesia.anc.NativeKIANCSmartRegisterActivity;
import org.smartregister.indonesia.child.NativeKIAnakSmartRegisterActivity;
import org.smartregister.indonesia.face.camera.utils.FaceConstants;
import org.smartregister.indonesia.face.camera.utils.Tools;
import org.smartregister.indonesia.fragment.NativeKBSmartRegisterFragment;
import org.smartregister.indonesia.fragment.NativeKIANCSmartRegisterFragment;
import org.smartregister.indonesia.fragment.NativeKIAnakSmartRegisterFragment;
import org.smartregister.indonesia.fragment.NativeKIPNCSmartRegisterFragment;
import org.smartregister.indonesia.fragment.NativeKISmartRegisterFragment;
import org.smartregister.indonesia.kartu_ibu.KIDetailActivity;
import org.smartregister.indonesia.kartu_ibu.NativeKISmartRegisterActivity;
import org.smartregister.indonesia.kb.NativeKBSmartRegisterActivity;
import org.smartregister.indonesia.pnc.NativeKIPNCSmartRegisterActivity;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


public class ImageConfirmation extends Activity {

    private static String TAG = ImageConfirmation.class.getSimpleName();
    private Bitmap storedBitmap;
    private Bitmap workingBitmap;
    private Bitmap mutableBitmap;
    ImageView confirmationView;
    ImageView confirmButton;
    ImageView trashButton;
    private String entityId;
    private Rect[] rects;
    private boolean faceFlag = false;
    private boolean identifyPerson = false;
    private FacialProcessing objFace;
    private FaceData[] faceDatas;
    private int arrayPossition;
    Tools tools;
    HashMap<String, String> clientList;
    private String selectedPersonName = "";
    private Parcelable[] kiclient;

    String str_origin_class;

    byte[] data;
    int angle;
    boolean switchCamera;
    private byte[] faceVector;
    private boolean updated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fr_image_face_confirmation);

        init_gui();

        init_extras();

        process_img();

        buttonJob();
    }

    private void init_gui() {
        // Display New Photo
        confirmationView = (ImageView) findViewById(R.id.iv_confirmationView);
        trashButton = (ImageView) findViewById(R.id.iv_cancel);
        confirmButton = (ImageView) findViewById(R.id.iv_approve);
    }

    /**
     * Method to get Info from previous Intent
     */
    private void init_extras() {
        Bundle extras = getIntent().getExtras();
        data = getIntent().getByteArrayExtra("org.sid.sidface.ImageConfirmation");
        angle = extras.getInt("org.sid.sidface.ImageConfirmation.orientation");
        switchCamera = extras.getBoolean("org.sid.sidface.ImageConfirmation.switchCamera");
        entityId = extras.getString("org.sid.sidface.ImageConfirmation.id");
        identifyPerson = extras.getBoolean("org.sid.sidface.ImageConfirmation.identify");
        kiclient = extras.getParcelableArray("org.sid.sidface.ImageConfirmation.kiclient");
        str_origin_class = extras.getString("org.sid.sidface.ImageConfirmation.origin");
        updated = extras.getBoolean("org.sid.sidface.ImageConfirmation.updated");
        Log.e(TAG, "init_extras: updated "+updated );

    }

    private void process_img() {

        storedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);
//        Log.e(TAG, "process_img: storedBitmap "+ storedBitmap ); // 720 x 1280
//        Log.e(TAG, "process_img: storedBitmap h "+ storedBitmap.getHeight() );
//        Log.e(TAG, "process_img: storedBitmap w "+ storedBitmap.getWidth() );
        objFace = SmartShutterActivity.faceProc;

        Matrix mat = new Matrix();
        if (!switchCamera) {
            mat.postRotate(angle == 90 ? 270 : (angle == 180 ? 180 : 0));
            mat.postScale(-1, 1);
            storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0, storedBitmap.getWidth(), storedBitmap.getHeight(), mat, true);
        } else {
            mat.postRotate(angle == 90 ? 90 : (angle == 180 ? 180 : 0));
            storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0, storedBitmap.getWidth(), storedBitmap.getHeight(), mat, true);
        }
//        TODO : Image from gallery

//        Retrieve data from Local Storage
        clientList = SmartShutterActivity.retrieveHash(getApplicationContext());

        boolean setBitmapResult = objFace.setBitmap(storedBitmap);
        faceDatas = objFace.getFaceData();

//        Log.e(TAG, "process_img: w "+ tempBitmap.getWidth() );
//        Log.e(TAG, "process_img: h "+ tempBitmap.getHeight() );
        Log.e(TAG, "process_img: w "+confirmationView.getWidth() ); //w 1536
        Log.e(TAG, "process_img: h "+confirmationView.getHeight() ); //h 1872

        /**
         * Set Height and Width
         */
        int imageViewSurfaceWidth = storedBitmap.getWidth();
        int imageViewSurfaceHeight = storedBitmap.getHeight();
//        int imageViewSurfaceWidth = confirmationView.getWidth();
//        int imageViewSurfaceHeight = confirmationView.getHeight();

        // Face Confirmation view purpose
        workingBitmap = Bitmap.createScaledBitmap(storedBitmap, imageViewSurfaceWidth, imageViewSurfaceHeight, false);

        mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

//        mutableBitmap = storedBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        Bitmap tempBitmap = Bitmap.createScaledBitmap(storedBitmap,
//                (storedBitmap.getWidth() / 2), (storedBitmap.getHeight() / 2),
//                false);
        confirmationView.setImageBitmap(mutableBitmap); // Setting the view with the bitmap image that came in.


        objFace.normalizeCoordinates(imageViewSurfaceWidth, imageViewSurfaceHeight);

        // Set Bitmap Success
        if(setBitmapResult){

            // Face Data Exist
            if(faceDatas != null){
//                Log.e(TAG, "onCreate: faceDatas "+faceDatas.length );
                rects = new Rect[faceDatas.length];

                for (int i = 0; i < faceDatas.length; i++) {
                    Rect rect = faceDatas[i].rect;
                    rects[i] = rect;

                    int matchRate = faceDatas[i].getRecognitionConfidence();

                    float pixelDensity = getResources().getDisplayMetrics().density; // 2.0

//                    Identify or new record
                    if (identifyPerson) {
                        String selectedPersonId = Integer.toString(faceDatas[i].getPersonId());
                        Iterator<HashMap.Entry<String, String>> iter = clientList.entrySet().iterator();
                        // Default name is the person is unknown
                        selectedPersonName = "Not Identified";
                        while (iter.hasNext()) {
                            Log.e(TAG, "process_img: Identified" );
                            HashMap.Entry<String, String> entry = iter.next();
                            if (entry.getValue().equals(selectedPersonId)) {
                                selectedPersonName = entry.getKey();
                            }
                        }

                        Toast.makeText(getApplicationContext(), selectedPersonName, Toast.LENGTH_SHORT).show();

//                        Draw Info on Image
                        Tools.drawInfo(rect, mutableBitmap, pixelDensity, selectedPersonName);

                        showDetailUser(selectedPersonName);

                    } else {

                        // Not Identifiying, do new record.
//                        Draw Info on Image
                        Log.e(TAG, "process_img: rect "+ rect.toString() ); // Rect(125, 409 - 847, 951)
                        Tools.drawRectFace(rect, mutableBitmap, pixelDensity);

                        Log.e(TAG, "onCreate: PersonId "+faceDatas[i].getPersonId() );

                        // Check Detected existing face
                        if(faceDatas[i].getPersonId() < 0){

                            arrayPossition = i;

                        } else {

                            showPersonInfo(matchRate);

                        }

//                        TODO: asign selectedPersonName to search
                        // Applied Image that came in to the view.
                        // Face only
//                        confirmationView.setImageBitmap(storedBitmap);

                        // Face and Rect
//                        confirmationView.setImageBitmap(mutableBitmap);
                        Drawable drawable = confirmationView.getDrawable();
//you should call after the bitmap drawn
                        Rect bounds = drawable.getBounds();
                        int width = bounds.width();
                        int height = bounds.height();
                        int bitmapWidth = drawable.getIntrinsicWidth(); //this is the bitmap's width
                        int bitmapHeight = drawable.getIntrinsicHeight(); //this is the bitmap's height

                        Log.e(TAG, "process_img: w "+ width );
                        Log.e(TAG, "process_img: h "+ height );
                        Log.e(TAG, "process_img: bw "+ bitmapWidth );
                        Log.e(TAG, "process_img: bh "+ bitmapHeight );
                    } // end if-else mode Identify {True or False}

                } // end for count ic_faces

            } else {

                Log.e(TAG, "onCreate: faceDatas "+"Null" );
                Toast.makeText(ImageConfirmation.this, "No Face Detected", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                setResult(RESULT_CANCELED, resultIntent);
                ImageConfirmation.this.finish();
            }

        } else {

            Log.e(TAG, "onCreate: SetBitmap objFace"+"Failed" );

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_confirmation, menu);
        return true;
    }

    public void showDetailUser(String selectedPersonName) {

        Log.e(TAG, "showDetailUser: " );
        Class<?> origin_class = this.getClass();

        if(str_origin_class.equals(NativeKISmartRegisterFragment.class.getSimpleName())){
            origin_class = NativeKISmartRegisterActivity.class;
        } else if(str_origin_class.equals(NativeKBSmartRegisterFragment.class.getSimpleName())){
            origin_class = NativeKBSmartRegisterActivity.class;
        } else if(str_origin_class.equals(NativeKIAnakSmartRegisterFragment.class.getSimpleName())){
            origin_class = NativeKIAnakSmartRegisterActivity.class;
        } else if(str_origin_class.equals(NativeKIANCSmartRegisterFragment.class.getSimpleName())){
            origin_class = NativeKIANCSmartRegisterActivity.class;
        } else if(str_origin_class.equals(NativeKIPNCSmartRegisterFragment.class.getSimpleName())){
            origin_class = NativeKIPNCSmartRegisterActivity.class;
        }

        Intent intent = new Intent(ImageConfirmation.this, origin_class);
        intent.putExtra("org.smartregister.indonesia.face.face_mode", true);
        intent.putExtra("org.smartregister.indonesia.face.base_id", selectedPersonName);

        startActivity(intent);

    }

    private void showPersonInfo(int recognitionConfidence) {
        Log.e(TAG, "showPersonInfo: Similar face found " +
                Integer.toString(recognitionConfidence));

        AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setTitle("Are you Sure?");
        builder.setMessage("Similar Face Found! : Confidence "+recognitionConfidence);
        builder.setNegativeButton("CANCEL", null);
        builder.show();
        confirmButton.setVisibility(View.INVISIBLE);

    }

    /**
     *
     */
    private void buttonJob() {
        // If approved then save the image and close.
        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (!identifyPerson) {

                    Log.e(TAG, "onClick: class origin "+str_origin_class );

                    Tools.saveAndClose(getApplicationContext(), entityId, updated, objFace, arrayPossition, storedBitmap, str_origin_class);

                    // Back To Detail Activity
                    Intent i = new Intent();
                    setResult(2, i);
                    finish();

                } else {
                    Log.e(TAG, "onClick: not identify ");
                    // TODO: detect origin class
                    KIDetailActivity.kiclient = (CommonPersonObjectClient) arg0.getTag();
                    Log.e(TAG, "onClick: " + KIDetailActivity.kiclient);
//                    Intent intent = new Intent(ImageConfirmation.this,KIDetailActivity.class);
                    Log.e(TAG, "onClick: " + selectedPersonName);
//                    startActivity(intent);
                }
            }

        });

        confirmButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    confirmButton.setImageResource(R.drawable.ic_confirm_highlighted_24dp);
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    confirmButton.setImageResource(R.drawable.ic_confirm_white_24dp);
                }

                return false;
            }
        });

        // Trash the image and return back to the camera preview.
        trashButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent resultIntent = new Intent();
                setResult(RESULT_CANCELED, resultIntent);
                ImageConfirmation.this.finish();
            }

        });

        trashButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    trashButton.setImageResource(R.drawable.ic_trash_delete_green);
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    trashButton.setImageResource(R.drawable.ic_trash_delete);
                }

                return false;
            }
        });

    }

    /*
    Save File and DB
     */
    private void saveAndClose(String entityId) {

        Log.e(TAG, "saveAndClose: updated "+ updated );

        faceVector = objFace.serializeRecogntionAlbum();

        Log.e(TAG, "saveAndClose: " + Arrays.toString(faceVector));

        int result = objFace.addPerson(arrayPossition);
        clientList.put(entityId, Integer.toString(result));

        byte[] albumBuffer = SmartShutterActivity.faceProc.serializeRecogntionAlbum();

        SmartShutterActivity.faceProc.resetAlbum();

//        Tools.WritePictureToFile(ImageConfirmation.this, storedBitmap, entityId, albumBuffer, updated);
        // TODO : change album buffer to String[]
//        Tools.WritePictureToFile(storedBitmap, entityId, albumBuffer, updated);

        ImageConfirmation.this.finish();

        Intent resultIntent = new Intent(this, KIDetailActivity.class);
        setResult(RESULT_OK, resultIntent);
        startActivityForResult(resultIntent, 1);

        Log.e(TAG, "saveAndClose: "+"end" );
    }

    public void saveHash(HashMap<String, String> hashMap, android.content.Context context) {
        SharedPreferences settings = context.getSharedPreferences(FaceConstants.HASH_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        Log.e(TAG, "Hash Save Size = " + hashMap.size());
        for (String s : hashMap.keySet()) {
            editor.putString(s, hashMap.get(s));
        }
        editor.apply();
    }

    public void saveAlbum() {
        byte[] albumBuffer = SmartShutterActivity.faceProc.serializeRecogntionAlbum();
//		saveCloud(albumBuffer);
        Log.e(TAG, "Size of byte Array =" + albumBuffer.length);
        SharedPreferences settings = getSharedPreferences(FaceConstants.ALBUM_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(FaceConstants.ALBUM_ARRAY, Arrays.toString(albumBuffer));
        editor.apply();
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
//        DatabaseReference ref = FirebaseDatabase.getInstance()
//                .getReference(AllConstantsINA.FIREBASE_OPENSRP_INA)
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .child(mRestaurant.getPushId())
//                .child("imageUrl");
//        ref.setValue(imageEncoded);
    }


}
