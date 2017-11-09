/* ======================================================================
 *  Copyright � 2014 Qualcomm Technologies, Inc. All Rights Reserved.
 *  QTI Proprietary and Confidential.
 *  =====================================================================
 *  
 * @file:   ImageConfirmation.java
 *
 */
package layout.face.camera;

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
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.anc.NativeKIANCSmartRegisterActivity;
import org.ei.opensrp.indonesia.child.NativeKIAnakSmartRegisterActivity;
import org.ei.opensrp.indonesia.face.camera.SmartShutterActivity;
import org.ei.opensrp.indonesia.face.camera.util.FaceConstants;
import org.ei.opensrp.indonesia.face.camera.util.Tools;
import org.ei.opensrp.indonesia.fragment.NativeKBSmartRegisterFragment;
import org.ei.opensrp.indonesia.fragment.NativeKIANCSmartRegisterFragment;
import org.ei.opensrp.indonesia.fragment.NativeKIAnakSmartRegisterFragment;
import org.ei.opensrp.indonesia.fragment.NativeKIPNCSmartRegisterFragment;
import org.ei.opensrp.indonesia.fragment.NativeKISmartRegisterFragment;
import org.ei.opensrp.indonesia.kartu_ibu.KIDetailActivity;
import org.ei.opensrp.indonesia.kartu_ibu.NativeKISmartRegisterActivity;
import org.ei.opensrp.indonesia.kb.NativeKBSmartRegisterActivity;
import org.ei.opensrp.indonesia.pnc.NativeKIPNCSmartRegisterActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class ImageConfirmation extends Activity {

    private static String TAG = org.ei.opensrp.indonesia.face.camera.ImageConfirmation.class.getSimpleName();
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
    HashMap<String, String> hash;
    private String selectedPersonName = "";
    private Parcelable[] kiclient;

    String str_origin_class;

    byte[] data;
    int angle;
    boolean switchCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_face_confirmation);

        init_gui();

        init_extras();

        storedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);
        objFace = SmartShutterActivity.faceProc;
//        if (objFace == null) {
//            objFace = FacialProcessing.getInstance();
//        }

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
        hash = SmartShutterActivity.retrieveHash(getApplicationContext());

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

        if(result){
//            Log.e(TAG, "onCreate: SetBitmap objFace "+"Success" );
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
                        Iterator<HashMap.Entry<String, String>> iter = hash.entrySet().iterator();
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
                        if(faceDatas[i].getPersonId() < 0){

                            arrayPossition = i;

                            int res = objFace.addPerson(arrayPossition);
                            hash.put(entityId, Integer.toString(res));
                            saveHash(hash, getApplicationContext());
                            saveAlbum();
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
                } // end for count faces
            } else {
                Log.e(TAG, "onCreate: faceDatas "+"Null" );
                Toast.makeText(org.ei.opensrp.indonesia.face.camera.ImageConfirmation.this, "No Face Detected", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                setResult(RESULT_CANCELED, resultIntent);
                org.ei.opensrp.indonesia.face.camera.ImageConfirmation.this.finish();
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

        Intent intent = new Intent(org.ei.opensrp.indonesia.face.camera.ImageConfirmation.this, origin_class);
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
                if (!identifyPerson) {
                    saveAndClose(entityId);
                } else {
//                    SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
//                    Cursor cursor = getApplicationContext().
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
                    confirmButton.setImageResource(R.drawable.confirm_highlighted);
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    confirmButton.setImageResource(R.drawable.confirm);
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
                org.ei.opensrp.indonesia.face.camera.ImageConfirmation.this.finish();
            }

        });

        trashButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    trashButton.setImageResource(R.drawable.trash_highlighted);
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    trashButton.setImageResource(R.drawable.trash);
                }

                return false;
            }
        });

    }

    /*
     * Function to save image and get back to the camera preview.
     */
    private void saveAndClose(String entityId) {
        Log.e(TAG, "saveAndClose: "+arrayPossition );
        int res = objFace.addPerson(arrayPossition);
        Log.e(TAG, "saveAndClose: "+res );
        Log.e(TAG, "saveAndClose: "+ Arrays.toString(objFace.serializeRecogntionAlbum()));
//        SmartShutterActivity.WritePictureToFile(ImageConfirmation.this, storedBitmap);
        saveAlbum();
        Tools.WritePictureToFile(org.ei.opensrp.indonesia.face.camera.ImageConfirmation.this, storedBitmap, entityId);
//        Tools.SavePictureToFile(ImageConfirmation.this, storedBitmap, entityId);
//        resultIntent.putExtra("com.qualcomm.sdk.smartshutterappgit .SmartShutterActivity.thumbnail", thumbnail);
        org.ei.opensrp.indonesia.face.camera.ImageConfirmation.this.finish();
        Intent resultIntent = new Intent(this, KIDetailActivity.class);
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
        editor.putString("albumArray", Arrays.toString(albumBuffer));
        editor.apply();
    }



}
