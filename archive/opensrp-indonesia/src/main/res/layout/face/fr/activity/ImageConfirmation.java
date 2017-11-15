package layout.face.fr.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;

import org.ei.opensrp.indonesia.BidanHomeActivity;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.face.fr.SharedPreferencesHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by wildan on 12/22/16.
 */
public class ImageConfirmation extends Activity {

    private ImageView trashButton; // ImageView for discarding the presently loaded image
    private ImageView confirmButton; // ImageView to confirm the presently loaded image
    private ImageView homeButton; // ImageView to go to Home Screen
    // (SidFaceActivity)
    private ImageView confirmationView; // ImageView to display the selected image
    public SharedPreferencesHelper faceRecog;
    private static Bitmap storedBitmap;
    private Bitmap mutableBitmap; // Temporary mutable bitmap
    private static FacialProcessing faceObj;
    private static Rect[] arrayOfRects; // A temporary array that will store the
    // face rects.
    private static HashMap<String, String> hash;
    private FaceData[] faceDataArray;
    private Vibrator vibrate;
    private int arrayPosition;
    private int personId;
    private boolean identifyPerson = false;
    private boolean inputNameFlag = true;
    private boolean updatePerson = false;
    private static boolean faceFoundFlag = false;
    private String userName;
    private final String TAG = "ImageConfirmation.java";
    private static final String ALBUM_NAME = "serialize_deserialize";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_confirmation);
        Bundle extras = getIntent().getExtras();

//      Init var passed
        byte[] data = getIntent().getByteArrayExtra("org.sid.sidface.ImageConfirmation");
        int angle = extras.getInt("org.sid.sidface.ImageConfirmation.orientation");
        boolean cameraFacingFront = extras.getBoolean("org.sid.sidface.ImageConfirmation.switchCamera");
        boolean throughGallery = extras.getBoolean("org.sid.sidface.ImageConfirmation.through.gallery");

        updatePerson = extras.getBoolean("UpdatePerson");
        personId = extras.getInt("PersonId");
        userName = extras.getString("Username");
        identifyPerson = extras.getBoolean("IdentifyPerson");

        // New view on which the image will be displayed
        confirmationView = (ImageView) findViewById(R.id.iv_confirmationView);
        faceObj = BidanHomeActivity.faceObj;

        storedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);

        vibrate = (Vibrator) ImageConfirmation.this.getSystemService(Context.VIBRATOR_SERVICE);
        faceRecog = new SharedPreferencesHelper();
        hash = faceRecog.retrieveHash(getApplicationContext());

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inMutable = true;
        Matrix mat = new Matrix();

        Log.e(TAG, "onCreate: "+"Image Confirm" );
//      Rotate the bitmap image based on the deviceorientation
        if (cameraFacingFront) {
            Log.e(TAG, "onCreate: "+"Fronth Camera" );
            if (throughGallery) {
                mat.postRotate(angle == 90 ? 90 : (angle == 180 ? 180 : 0));
                storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0,
                        storedBitmap.getWidth(), storedBitmap.getHeight(), mat,
                        true);
            } else {
                mat.postRotate(angle == 0 ? 270 : angle == 270 ? 180
                        : (angle == 180 ? 180 : 0));
                mat.postScale(-1, 1);
                storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0,
                        storedBitmap.getWidth(), storedBitmap.getHeight(), mat,
                        true);
            }
        } else {
            Log.e(TAG, "onCreate: "+"Rear Camera" );
            if (throughGallery) {
                mat.postRotate(angle == 90 ? 90 : (angle == 180 ? 180 : 0));
            } else {
                mat.postRotate(angle == 0 ? 90 : angle == 270 ? 180
                        : (angle == 180 ? 180 : 0));
            }
            storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0,
                    storedBitmap.getWidth(), storedBitmap.getHeight(), mat,
                    true);
        }
        Bitmap tempBitmap = Bitmap.createScaledBitmap(storedBitmap,
                (storedBitmap.getWidth() / 2), (storedBitmap.getHeight() / 2),
                false);
        confirmationView.setImageBitmap(tempBitmap);
//      Choose display Landscape or Portrait
        if (storedBitmap.getWidth() > storedBitmap.getHeight()) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        homeButtonOnClickListener();
        confirmButtonOnClickListener();
        trashButtonOnClickListener();
        confirmationViewOnTouchListener();
    }

    /*
     * Action Listener method for taking the touch events on the image/face and
     * performing appropriate action on it
     */
    private void confirmationViewOnTouchListener() {
        confirmationView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int xCoord = (int) event.getX();
                    int yCoord = (int) event.getY();

                    faceDataArray = faceObj.getFaceData();

                    if (faceDataArray != null) {
                        for (int i = 0; i < arrayOfRects.length; i++) {
                            if (arrayOfRects[i].contains(xCoord, yCoord)) {
                                // If update person flag is true then update the
                                // person with its
                                // respective face index.
                                if (updatePerson) {
                                    int result = faceObj.updatePerson(personId,
                                            i);
                                    if (result == 0) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "'"
                                                        + userName
                                                        + "' updated successfully ",
                                                Toast.LENGTH_SHORT).show();
                                        confirmationView
                                                .setOnClickListener(null);
                                    } else {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Maximum face limit for " + "'"
                                                        + userName
                                                        + "' reached.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    saveAlbum();
                                    saveMediaFile(userName);

                                } else if (identifyPerson)
                                // Toast the name of the person
                                {
                                    String selectedPersonId = Integer
                                            .toString(faceDataArray[i]
                                                    .getPersonId());
                                    Iterator<HashMap.Entry<String, String>> iter = hash
                                            .entrySet().iterator();
                                    // Default name is the person is unknown
                                    String selectedPersonName = "Not Identified";
                                    while (iter.hasNext()) {
                                        Log.e(TAG, "In");
                                        HashMap.Entry<String, String> entry = iter
                                                .next();
                                        if (entry.getValue().equals(
                                                selectedPersonId)) {
                                            selectedPersonName = entry.getKey();
                                        }
                                    }
                                    Toast.makeText(getApplicationContext(),
                                            selectedPersonName,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    if (faceDataArray[i].getPersonId() < 0) {
                                        // Check the array position
                                        // corresponding the rect and add that
                                        // index
                                        arrayPosition = i;
                                        createAlert(); // Alert Box for getting
                                        // the user name
                                    } else {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Similar face already exists. Try updating that person. Confidence= +"
                                                        + Integer
                                                        .toString(faceDataArray[i]
                                                                .getRecognitionConfidence()),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                                faceFoundFlag = true;
                            }
                        }
                        if (!faceFoundFlag) {
                            Toast.makeText(getApplicationContext(),
                                    "No face found", Toast.LENGTH_SHORT).show();
                        } else {
                            faceFoundFlag = false;
                        }
                    }

                }

                return true;
            }
        });

    }

    /*
     * Action listener button for the action listener on the screen
     */
    private void trashButtonOnClickListener() {
        // Trash the image and return back to the camera preview.
        trashButton = (ImageView) findViewById(R.id.iv_cancel);
        trashButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                vibrate.vibrate(85);
                Intent resultIntent = new Intent();
                setResult(RESULT_CANCELED, resultIntent);
                ImageConfirmation.this.finish();
            }

        });

        trashButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    trashButton.setImageResource(R.drawable.ic_delete_highlighted_white_24dp);
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    trashButton.setImageResource(R.drawable.ic_delete_white_24dp);
                }

                return false;
            }
        });

    }

    /*
     * Action listener button for the trash button on the screen
     */
    private void confirmButtonOnClickListener() {
        confirmButton = (ImageView) findViewById(R.id.iv_approve);
        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                vibrate.vibrate(85);
                int imageViewSurfaceWidth = confirmationView.getWidth();
                int imageViewSurfaceHeight = confirmationView.getHeight();

                Bitmap workingBitmap = Bitmap.createScaledBitmap(storedBitmap,
                        imageViewSurfaceWidth, imageViewSurfaceHeight, false);
                mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888,
                        true);

                boolean result = faceObj.setBitmap(storedBitmap);
                // Normalize the face data coordinates based on the image that
                // is fed in.
                faceObj.normalizeCoordinates(imageViewSurfaceWidth,
                        imageViewSurfaceHeight);
                if (result) // If setBitmap was successful
                {
                    faceDataArray = faceObj.getFaceData();
                    // If one or more face is detected
                    if (faceDataArray != null) {
                        // Creating a temporary rect to store the faceRects
                        // returned from the faceData array
                        arrayOfRects = new Rect[faceDataArray.length];
                        Canvas canvas = null;
                        for (int i = 0; i < faceDataArray.length; i++) {
                            Rect rect = faceDataArray[i].rect;
                            // Extra padding around the faeRects
                            rect.set(rect.left -= 20, rect.top -= 20,
                                    rect.right += 20, rect.bottom += 20);
                            canvas = new Canvas(mutableBitmap);
                            Paint paintForRectFill = new Paint(); // Draw rect
                            // fill
                            paintForRectFill.setStyle(Paint.Style.FILL);
                            paintForRectFill.setColor(Color.WHITE);
                            paintForRectFill.setAlpha(80);
                            // Draw rect strokes
                            Paint paintForRectStroke = new Paint();
                            paintForRectStroke.setStyle(Paint.Style.STROKE);
                            paintForRectStroke.setColor(Color.GREEN);
                            paintForRectStroke.setStrokeWidth(5);
                            canvas.drawRect(rect, paintForRectFill);
                            canvas.drawRect(rect, paintForRectStroke);

                            // Update the temporary rect array with the given
                            // face rect
                            // so that we can use this in the future to get the
                            // corresponsing
                            // faceIndex of the faces.
                            arrayOfRects[i] = rect;
                            if (identifyPerson) {
                                String selectedPersonId = Integer
                                        .toString(faceDataArray[i]
                                                .getPersonId());
                                String personName = null;
                                Iterator<HashMap.Entry<String, String>> iter = hash
                                        .entrySet().iterator();
                                while (iter.hasNext()) {
                                    HashMap.Entry<String, String> entry = iter
                                            .next();
                                    if (entry.getValue().equals(
                                            selectedPersonId)) {
                                        personName = entry.getKey();
                                    }
                                }
                                float pixelDensity = getResources()
                                        .getDisplayMetrics().density;
                                int textSize = (int) (rect.width() / 25 * pixelDensity);
                                Paint paintForText = new Paint();
                                paintForText.setColor(Color.WHITE);
                                paintForText.setTextSize(textSize);
                                Typeface tp = Typeface.SERIF;
                                Rect backgroundRect = new Rect(rect.left,
                                        rect.bottom, rect.right,
                                        (rect.bottom + textSize));
                                Paint paintForTextBackground = new Paint();
                                paintForTextBackground
                                        .setStyle(Paint.Style.FILL);
                                paintForTextBackground.setColor(Color.BLACK);
                                ;
                                paintForText.setTypeface(tp);
                                paintForTextBackground.setAlpha(80);
                                if (personName != null) {
                                    canvas.drawRect(backgroundRect,
                                            paintForTextBackground);
                                    canvas.drawText(personName, rect.left,
                                            rect.bottom + (textSize),
                                            paintForText);
                                } else {
                                    canvas.drawRect(backgroundRect,
                                            paintForTextBackground);
                                    canvas.drawText("Not identified",
                                            rect.left,
                                            rect.bottom + (textSize),
                                            paintForText);
                                }
                            }
                        }
                        confirmButton.setVisibility(View.GONE);
                        homeButton.setVisibility(View.VISIBLE);
                        // Setting the view with the bitmap image that came in
                        confirmationView.setImageBitmap(mutableBitmap);

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "No Faces detected", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Set Bitmap failed");
                }
            }
        });

        confirmButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    confirmButton
                            .setImageResource(R.drawable.ic_confirm_highlighted_24dp);
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    confirmButton.setImageResource(R.drawable.ic_confirm_white_24dp);
                }

                return false;
            }
        });

    }

    /*
     * Action listener method for the Home button on the screen
     */
    private void homeButtonOnClickListener() {

        homeButton = (ImageView) findViewById(R.id.iv_home);
        homeButton.setVisibility(View.GONE);
        homeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                vibrate.vibrate(85);
                Intent intent = new Intent(ImageConfirmation.this,
                        PhotoLive.class);
                startActivity(intent);
            }
        });

        homeButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    homeButton
                            .setImageResource(R.drawable.ic_home_highlighted_24dp);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    homeButton.setImageResource(R.drawable.ic_home_white_24dp);
                }
                return false;
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.image_confirmation, menu);
        return true;
    }

    /*
     * Method to pop - up an alert box when a face is clicked to be added
     */
    private boolean createAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ImageConfirmation.this);
        builder.setMessage("Enter Person Name");
        final EditText input = new EditText(ImageConfirmation.this);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String inputName = input.getText().toString();
                if (inputName != null && isNumber(inputName) && inputName.trim().length() == 8) {
                    if (!hash.containsKey(inputName)) {
                        int result = faceObj.addPerson(arrayPosition);
                        hash.put(inputName, Integer.toString(result));
                        faceRecog.saveHash(hash, getApplicationContext());
                        saveAlbum();

                        saveMediaFile(inputName);

                        Toast.makeText(
                                getApplicationContext(),
                                input.getText().toString()
                                        + " added successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "User ID '" + inputName + "' already exist",
                                Toast.LENGTH_SHORT).show();
                        createAlert();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "User ID cannot be empty", Toast.LENGTH_SHORT)
                            .show();
                    createAlert();
                }
            }
        });
        builder.show();
        return inputNameFlag;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e(TAG, "Serializing");
    }

    /*
     * Function to retrieve the byte array from the Shared Preferences.
     */
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

    /*
     * Method to save the recognition album to a permanent device memory
     */
    public void saveAlbum() {
        byte[] albumBuffer = faceObj.serializeRecogntionAlbum();
        saveCloud(albumBuffer);
        Log.e(TAG, "Size of byte Array =" + albumBuffer.length);
        SharedPreferences settings = getSharedPreferences(ALBUM_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("albumArray", Arrays.toString(albumBuffer));
        editor.commit();
    }

    private void saveCloud(byte[] bytes) {
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//		String taskId = "-KWJ2-l5YxJwrdryDN8A";
        String colId = "SidQFace";
//        FirebaseDatabase.
//                getInstance().
//                getReference().
//                child(telephonyManager.getDeviceId()).
//                child(colId).
//				push().
//				setValue(Base64.encodeToString(bytes, Base64.DEFAULT));
//        setValue(new FaceUId("a", "b", bytes, hash));
        Log.e(TAG, "saveCloud: "+"FINISHED" );

    }

    private void saveMediaFile(String var1) {
        SharedPreferencesHelper.StoredAsFile(this, storedBitmap, var1);
    }

    private boolean isNumber(String var1) {
        try {
            Integer.parseInt(var1);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }
}