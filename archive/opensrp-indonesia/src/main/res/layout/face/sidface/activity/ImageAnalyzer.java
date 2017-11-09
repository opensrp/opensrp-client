package layout.face.sidface.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.qualcomm.snapdragon.sdk.face.FacialProcessing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

//import com.ldoublem.loadingviewlib.LVLineWithText;

public class ImageAnalyzer extends Activity
{
    private static FacialProcessing faceObj = FacialProcessing.getInstance();
    final String TAG = ImageAnalyzer.class.getSimpleName();
    private Bitmap bitmap;
    private File dir;
    private SidFaceActivity facialRecognitionActivity;
    private Handler mHandle = new Handler()
    {
        public void handleMessage(Message paramAnonymousMessage)
        {
            super.handleMessage(paramAnonymousMessage);
            if (paramAnonymousMessage.what == 2)
//                ImageAnalyzer.this.mLVLineWithText.setValue(paramAnonymousMessage.arg1);
            while (paramAnonymousMessage.what != 1)
                return;
        }
    };
//    LVLineWithText mLVLineWithText;
    public Timer mTimerLVLineWithText = new Timer();
    int mValueLVLineWithText = 0;
    private String m_chosenDir = "";
    private ByteArrayOutputStream stream = new ByteArrayOutputStream();
    private Uri uri;

    private void face_processing(int paramInt, Bitmap paramBitmap, String paramString)
    {
        paramBitmap.compress(Bitmap.CompressFormat.JPEG, 25, this.stream);
//        paramBitmap = stream.toByteArray();
//        paramBitmap = BitmapFactory.decodeByteArray(paramBitmap, 0, paramBitmap.length, null);
//        if (faceObj.setBitmap(paramBitmap))
//        {
//            paramBitmap = faceObj.getFaceData();
//            faceObj.addPerson(paramInt);
//            Log.e("Face Datas", Arrays.toString(paramBitmap));
//            Log.e("Face Num face", String.valueOf(faceObj.getNumFaces()));
//            paramBitmap = faceObj.serializeRecogntionAlbum();
//            paramString = getSharedPreferences("serialize_deserialize", 0).edit();
//            paramString.putString("albumArray", Arrays.toString(paramBitmap));
//            paramString.apply();
//            return;
//        }
        Log.e("Face", "No face Detected");
    }

    private void imgProc()
    {
        Log.e("doBackground ", this.m_chosenDir);
        if (this.m_chosenDir.isEmpty());
        String[] arrayOfString;
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "SID");
//        for (File localFile : localFile = new File(this.m_chosenDir));
//        {
//            arrayOfString = localFile.list();
//            if (localFile.list().length != 0)
//                break;
//            return;
//        }
//        int i = 0;
//        while (i < localFile.list().length) {
//            Log.e("File name ", arrayOfString[i]);
//            Object localObject = Uri.withAppendedPath(Uri.parse("file://" + String.valueOf(localFile)), arrayOfString[i]);
//            try
//            {
//                localObject = BitmapFactory.decodeStream(getContentResolver().openInputStream((Uri)localObject));
//                if ((localObject != null) && (Bitmap.createScaledBitmap((Bitmap)localObject, 150, 150, true) != null))
//                    face_processing(i, (Bitmap)localObject, arrayOfString[i]);
//                i += 1;
//            }
//            catch (IOException localIOException)
//            {
//                while (true)
//                    Log.e("Er:", localIOException.toString());
//            }
//        }
    }

    private void readFiles()
    {
        int i = 0;
        while (i < 1000) {
//            this.mLVLineWithText.setValue(i);
            i += 1;
        }
    }

    private void startLVLineWithTextAnim()
    {
        this.mValueLVLineWithText = 0;
        if (this.mTimerLVLineWithText != null)
            this.mTimerLVLineWithText.cancel();
        this.mTimerLVLineWithText = new Timer();
        timerTaskLVLineWithText();
    }

    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
//        setContentView(2130968609);
        this.facialRecognitionActivity = new SidFaceActivity();
        this.facialRecognitionActivity.retrieveHash(getApplicationContext());
//        paramBundle = (Button)findViewById(2131558571);
//        this.mLVLineWithText = ((LVLineWithText)findViewById(2131558579));
        this.dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "SID");
        this.dir.list();
//        paramBundle.setOnClickListener(new View.OnClickListener()
//        {
//            public void onClick(View paramAnonymousView)
//            {
//                ImageAnalyzer.this.imgProc();
//            }
//        });
    }

    public void onStart()
    {
        super.onStart();
    }

    public void onStop()
    {
        super.onStop();
    }

    public void startAnim(View paramView)
    {
//        if ((paramView instanceof LVLineWithText))
//            readFiles();
    }

    public void timerTaskLVLineWithText()
    {
        this.mTimerLVLineWithText.schedule(
                new TimerTask(){
                    public void run(){
                        if (ImageAnalyzer.this.mValueLVLineWithText < 1000) {
                            Log.e("TAG", "timerTaskLVLineWithText: " + ImageAnalyzer.this.mValueLVLineWithText);
                            Object localObject = ImageAnalyzer.this;
                            ((ImageAnalyzer)localObject).mValueLVLineWithText += 1;
                            localObject = ImageAnalyzer.this.mHandle.obtainMessage(2);
                            ((Message)localObject).arg1 = ImageAnalyzer.this.mValueLVLineWithText;
                            ImageAnalyzer.this.mHandle.sendMessage((Message)localObject);
                            return;
                        }
                        ImageAnalyzer.this.mTimerLVLineWithText.cancel();
                    }
                }, 0L, 50L);
    }
}