package layout.face.fr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.SurfaceView;

import com.qualcomm.snapdragon.sdk.face.FaceData;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by wildan on 12/22/16.
 */
public class DrawView extends SurfaceView {

    private static String TAG = DrawView.class.getSimpleName();
    private Paint paintForTextBackground = new Paint();
    private Paint paintForText = new Paint();
    private FaceData[] mFaceArray;
    private boolean _isFaceExist;
    private HashMap<String, String> hash;
    private SharedPreferencesHelper faceRecog;
//    private BidanHomeActivity faceRecog;

    public DrawView(Context context, FaceData[] faceArray, boolean inFrame) {
        super(context);
        // Init tor Draw
        setWillNotDraw(false);

        mFaceArray = faceArray;
        _isFaceExist = inFrame;
//        faceRecog = new BidanHomeActivity();
        faceRecog = new SharedPreferencesHelper();

//        if(faceRecog == null){
//            Log.e(TAG, "DrawView: "+"Face Null");
//        }else{
//            Log.e(TAG, "DrawView: "+"Face Not Null");
//        }

        hash = faceRecog.retrieveHash(getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (_isFaceExist) {
            for (int i = 0; i < mFaceArray.length; i++) {

                Log.e(TAG, "onDraw: "+hash.toString() );

                String selectedPersonId = Integer.toString(mFaceArray[i].getPersonId());
                String personName = "";
                Iterator<HashMap.Entry<String, String>> iter = hash.entrySet().iterator();

                while (iter.hasNext()) {
                    HashMap.Entry<String, String> entry = iter.next();
                    if (entry.getValue().equals(selectedPersonId)) {
                        personName = entry.getKey();

                        display_name(i, personName, canvas);
                    }
                }
            }
        } else {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
    }

    private void display_name(int i, String personName, Canvas canvas) {
        Rect rect = mFaceArray[i].rect;
        float pixelDensity = getResources().getDisplayMetrics().density;
        int textSize = (int) (rect.width() / 25 * pixelDensity);

        paintForText.setColor(Color.WHITE);
        paintForText.setTextSize(textSize);
        Typeface tp = Typeface.SERIF;
        Rect backgroundRect = new Rect(rect.left, rect.bottom, rect.right, (rect.bottom + textSize));

        paintForTextBackground.setStyle(Paint.Style.FILL);
        paintForTextBackground.setColor(Color.BLACK);
        paintForText.setTypeface(tp);
        paintForTextBackground.setAlpha(80);
        if (personName != null) {
            canvas.drawRect(backgroundRect, paintForTextBackground);
            canvas.drawText(personName, rect.left, rect.bottom
                    + (textSize), paintForText);
        }

    }

}