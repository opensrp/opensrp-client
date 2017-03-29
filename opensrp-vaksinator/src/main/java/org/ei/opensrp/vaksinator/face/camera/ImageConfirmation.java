/* ======================================================================
 *  Copyright � 2014 Qualcomm Technologies, Inc. All Rights Reserved.
 *  QTI Proprietary and Confidential.
 *  =====================================================================
 *  
 * @file:   ImageConfirmation.java
 *
 */
package org.ei.opensrp.vaksinator.face.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
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

import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.vaksinator.R;

import org.ei.opensrp.vaksinator.face.camera.util.FaceConstants;
import org.ei.opensrp.vaksinator.face.camera.util.Tools;
import org.ei.opensrp.vaksinator.fragment.TTSmartRegisterFragment;
import org.ei.opensrp.vaksinator.fragment.VaksinatorSmartRegisterFragment;
import org.ei.opensrp.vaksinator.imunisasiTT.TTSmartRegisterActivity;
import org.ei.opensrp.vaksinator.vaksinator.VaksinatorDetailActivity;
import org.ei.opensrp.vaksinator.vaksinator.VaksinatorSmartRegisterActivity;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fr_image_face_confirmation);

        init_gui();

        init_extras();

        storedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);
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

        boolean result = objFace.setBitmap(storedBitmap);
        faceDatas = objFace.getFaceData();

        int imageViewSurfaceWidth = storedBitmap.getWidth();
        int imageViewSurfaceHeight = storedBitmap.getHeight();
//        int imageViewSurfaceWidth = confirmationView.getWidth();
//        int imageViewSurfaceHeight = confirmationView.getHeight();

        workingBitmap = Bitmap.createScaledBitmap(storedBitmap,
                imageViewSurfaceWidth, imageViewSurfaceHeight, false);
//        mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mutableBitmap = storedBitmap.copy(Bitmap.Config.ARGB_8888, true);

        objFace.normalizeCoordinates(imageViewSurfaceWidth, imageViewSurfaceHeight);

        // Set Bitmap Success
        if(result){
//            Log.e(TAG, "onCreate: SetBitmap objFace "+"Success" );

            // Face Data Exist
            if(faceDatas != null){
//                Log.e(TAG, "onCreate: faceDatas "+"NotNull" );
                rects = new Rect[faceDatas.length];

                for (int i = 0; i < faceDatas.length; i++) {
                    Rect rect = faceDatas[i].rect;
                    rects[i] = rect;

                    float pixelDensity = getResources().getDisplayMetrics().density;

//                    Identify or new record
                    if (identifyPerson) {
                        String selectedPersonId = Integer.toString(faceDatas[i].getPersonId());
                        Iterator<HashMap.Entry<String, String>> iter = clientList.entrySet().iterator();
                        // Default name is the person is unknown
                        selectedPersonName = "Not Identified";
                        while (iter.hasNext()) {
                            Log.e(TAG, "In");
                            HashMap.Entry<String, String> entry = iter.next();
                            if (entry.getValue().equals(selectedPersonId)) {
                                selectedPersonName = entry.getKey();
                            }
                        }

                        Toast.makeText(getApplicationContext(), selectedPersonName, Toast.LENGTH_SHORT).show();

//                        Draw Info on Image
//                        Tools.drawInfo(rect, mutableBitmap, pixelDensity, selectedPersonName);

                        showDetailUser(selectedPersonName);

                    } else {

                        Tools.drawRectFace(rect, mutableBitmap, pixelDensity);
                        Log.e(TAG, "onCreate: PersonId "+faceDatas[i].getPersonId() );

                        // Check Detected existing face
                        if(faceDatas[i].getPersonId() < 0){

                            arrayPossition = i;

//                            TODO
//                            int res = objFace.addPerson(arrayPossition);
//                            clientList.put(entityId, Integer.toString(res));
//                            saveHash(clientList, getApplicationContext());
//                            saveAlbum();

                        } else {
                            Log.e(TAG, "onCreate: Similar face found " +
                                    Integer.toString(faceDatas[i].getRecognitionConfidence()));

                            AlertDialog.Builder builder= new AlertDialog.Builder(this);

                            builder.setTitle("Are you Sure?");
                            builder.setMessage("Similar Face Found! : Confidence "+faceDatas[i].getRecognitionConfidence());
                            builder.setNegativeButton("CANCEL", null);
                            builder.show();
                            confirmButton.setVisibility(View.INVISIBLE);

                        }

//                        TODO: asign selectedPersonName to search

                        confirmationView.setImageBitmap(mutableBitmap);            // Setting the view with the bitmap image that came in.

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

//        confirmationView.setImageBitmap(storedBitmap);            // Setting the view with the bitmap image that came in.
//        confirmationView.setImageBitmap(mutableBitmap);            // Setting the view with the bitmap image that came in.

        buttonJob();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_confirmation, menu);
        return true;
    }

    /**
     * Method to get Info from previous Intent
     */
    private void init_extras() {
        Bundle extras = getIntent().getExtras();
        data = getIntent().getByteArrayExtra("com.qualcomm.sdk.smartshutterapp.ImageConfirmation");
        angle = extras.getInt("com.qualcomm.sdk.smartshutterapp.ImageConfirmation.orientation");
        switchCamera = extras.getBoolean("com.qualcomm.sdk.smartshutterapp.ImageConfirmation.switchCamera");
        entityId = extras.getString("org.sid.sidface.ImageConfirmation.id");
        identifyPerson = extras.getBoolean("org.sid.sidface.ImageConfirmation.identify");
        kiclient = extras.getParcelableArray("org.sid.sidface.ImageConfirmation.kiclient");
        str_origin_class = extras.getString("org.sid.sidface.ImageConfirmation.origin");

    }


    private void init_gui() {
        confirmationView = (ImageView) findViewById(R.id.iv_confirmationView);  // Display New Photo
        trashButton = (ImageView) findViewById(R.id.iv_cancel);
        confirmButton = (ImageView) findViewById(R.id.iv_approve);
    }

    public void showDetailUser(String selectedPersonName) {

        AllCommonsRepository ibuRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        CommonPersonObject kiclient = ibuRepository.findByCaseID(selectedPersonName);

//        Log.e(TAG, "onCreate: IbuRepo "+ibuRepository );
//        Log.e(TAG, "onCreate: Id "+selectedPersonName );
//        Log.e(TAG, "onCreate: KiClient "+kiclient.getCaseId() );

//      CommonRepository commonrepository = new CommonRepository("ibu", new String[]{"ibu.isClosed", "ibu.ancDate", "ibu.ancKe", "kartu_ibu.namalengkap", "kartu_ibu.umur", "kartu_ibu.namaSuami"}););
//        CommonRepository commonrepository = new CommonRepository("ec_kartu_ibu",new String []{"ec_kartu_ibu.is_closed", "ec_kartu_ibu.namalengkap", "ec_kartu_ibu.umur","ec_kartu_ibu.namaSuami"});
//        Log.e(TAG, "onCreate: CommonRespository "+commonrepository );
//        CommonPersonObject personinlist = commonrepository.findByCaseID(selectedPersonName);
//        CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), personinlist.getDetails().get("ec_kartu_ibu.namalengkap"));
//        KIDetailActivity.kiclient = pClient;
//        Intent intent = new Intent(ImageConfirmation.this,KIDetailActivity.class);

        Class<?> origin_class = this.getClass();

        Log.e(TAG, "onPreviewFrame: init"+origin_class.getSimpleName() );
        Log.e(TAG, "onPreviewFrame: origin" + str_origin_class);

        if(str_origin_class.equals(VaksinatorSmartRegisterFragment.class.getSimpleName())){
            origin_class = VaksinatorSmartRegisterActivity.class;
        } else if(str_origin_class.equals(TTSmartRegisterFragment.class.getSimpleName())){
            origin_class = TTSmartRegisterActivity.class;
        }

        Intent intent = new Intent(ImageConfirmation.this, origin_class);
        intent.putExtra("org.ei.opensrp.indonesia.face.face_mode", true);
        intent.putExtra("org.ei.opensrp.indonesia.face.base_id", selectedPersonName);

        startActivity(intent);

    }

    /**
     *
     */
    private void buttonJob() {
        // If approved then save the image and close.
        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.e(TAG, "onClick: " + identifyPerson);
                Log.e(TAG, "onClick: " + entityId);
                if (!identifyPerson) {
                    saveAndClose(entityId);
                } else {
//                    SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
//                    Cursor cursor = getApplicationContext().
//                    TODO
//                    KIDetailActivity.kiclient = (CommonPersonObjectClient) arg0.getTag();
//                    Log.e(TAG, "onClick: " + KIDetailActivity.kiclient);
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
        Log.e(TAG, "saveAndClose: position"+ arrayPossition );
        int res = objFace.addPerson(arrayPossition);
        Log.e(TAG, "saveAndClose: " + res);
        Log.e(TAG, "saveAndClose: " + Arrays.toString(objFace.serializeRecogntionAlbum()));

//        SmartShutterActivity.WritePictureToFile(ImageConfirmation.this, storedBitmap);
        saveAlbum();
        int result = objFace.addPerson(arrayPossition);
        clientList.put(entityId, Integer.toString(result));
        saveHash(clientList, getApplicationContext());

        Tools.WritePictureToFile(ImageConfirmation.this, storedBitmap, entityId);
//        Tools.SavePictureToFile(ImageConfirmation.this, storedBitmap, entityId);
//        resultIntent.putExtra("com.qualcomm.sdk.smartshutterappgit .SmartShutterActivity.thumbnail", thumbnail);
        ImageConfirmation.this.finish();
        Intent resultIntent = new Intent(this, VaksinatorDetailActivity.class);
        setResult(RESULT_OK, resultIntent);
        startActivityForResult(resultIntent, 1);
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
