package layout.face.sidface.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.ei.opensrp.indonesia.R;

/**
 * Created by wildan on 12/30/16.
 */
public class ConfirmImage extends Activity {

    private static final String TAG = ConfirmImage.class.getSimpleName();

    private ImageView confirmationView; // ImageView to display the selected image
    private static Bitmap storedBitmap;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_confirmation);

//      Init var passed
        Bundle extras = getIntent().getExtras();

        byte[] data = getIntent().getByteArrayExtra("org.sid.sidface.ImageConfirmation");
        int angle = extras.getInt("org.sid.sidface.ImageConfirmation.orientation");
        boolean cameraFacingFront = extras.getBoolean("org.sid.sidface.ImageConfirmation.switchCamera");
        boolean throughGallery = extras.getBoolean("org.sid.sidface.ImageConfirmation.through.gallery");
        confirmationView = (ImageView) findViewById(R.id.iv_confirmationView);

        storedBitmap = BitmapFactory.decodeByteArray(data,0,data.length, null);

        confirmationView.setImageBitmap(storedBitmap);

        Log.e(TAG, "onCreate: "+"Confirm Image" );
    }
}
