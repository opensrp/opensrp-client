package layout.face.sidface.utils;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wildan on 12/18/16.
 */
public class LogReport {
    Date date = new Date() ;
    SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy-hhmmss") ;
    String path = Environment.getExternalStorageDirectory().toString();
    File dirLog = new File(path + "/ReportFR");
    File fileLog = new File(dirLog, "FaceData"+dateFormat.format(date)+".csv");
    BufferedWriter bufw;

    public void saveLog(String name, int[] intArray) {
        try {
            bufw = new BufferedWriter(new FileWriter(fileLog, true));
            bufw.write(name);
            for (int i: intArray) {
                bufw.write(44);
                bufw.write(String.valueOf(i));
            }

            bufw.write(10);
            bufw.close();
            bufw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void initLogReport() {

        String[] header = new String[]{
                "getByteCount()",
                "getWidth()",
                "getHeight()",
                "getRecognitionConfidence()",
                "getPersonId()",
                "chin.center.x",
                "chin.center.y",
                "rect.left",
                "rect.right",
                "leftEye.x",
                "leftEye.y",
                "rightEye.x",
                "rightEye.y",
                "mouth.x",
                "mouth.y",
                "mouthObj.left.x",
                "mouthObj.left.y",
                "mouthObj.right.y",
                "mouthObj.right.y",
                "mouthObj.upperLipBottom.x",
                "mouthObj.upperLipBottom.y",
                "mouthObj.upperLipTop.x",
                "mouthObj.upperLipTop.y",
                "mouthObj.lowerLipBottom.x",
                "mouthObj.lowerLipBottom.y",
                "mouthObj.lowerLipTop.x",
                "mouthObj.lowerLipTop.y",
                "mouthObj.left.x",
                "mouthObj.left.y",
                "mouthObj.right.x",
                "mouthObj.right.y",
                "nose.noseBridge.x",
                "nose.noseBridge.y",
                "nose.noseCenter.x",
                "nose.noseCenter.y",
                "nose.noseUpperLeft.x",
                "nose.noseUpperLeft.y",
                "nose.noseUpperRight.x",
                "nose.noseLowerLeft.y",
                "nose.noseLowerRight.x",
                "nose.noseLowerRight.y",
                "nose.noseMiddleLeft.x",
                "nose.noseMiddleLeft.y",
                "nose.noseMiddleRight.x",
                "nose.noseMiddleRight.y",
                "nose.noseTip.x",
                "nose.noseTip.y",
                "rightEar.bottom.x",
                "rightEar.bottom.y",
                "rightEar.top.x",
                "rightEar.top.y",
                "rightEyebrow.bottom.x",
                "rightEyebrow.bottom.y",
                "rightEyebrow.top.x",
                "rightEyebrow.top.y",
                "rightEyebrow.left.x",
                "rightEyebrow.left.y",
                "rightEyebrow.right.x",
                "rightEyebrow.right.y",
                "rightEyeObj.bottom.x",
                "rightEyeObj.bottom.y",
                "rightEyeObj.top.x",
                "rightEyeObj.top.y",
                "rightEyeObj.left.x",
                "rightEyeObj.left.y",
                "rightEyeObj.right.x",
                "rightEyeObj.right.y",
                "rightEyeObj.centerPupil.x",
                "rightEyeObj.centerPupil.y",
                "leftEar.bottom.x",
                "leftEar.bottom.y",
                "leftEar.top.x",
                "leftEar.top.y",
                "leftEyebrow.bottom.x",
                "leftEyebrow.bottom.y",
                "leftEyebrow.top.x",
                "leftEyebrow.top.y",
                "leftEyebrow.left.x",
                "leftEyebrow.left.y",
                "leftEyebrow.right.x",
                "leftEyebrow.right.y",
                "leftEyeObj.bottom.x",
                "leftEyeObj.bottom.y",
                "leftEyeObj.top.x",
                "leftEyeObj.top.y",
                "leftEyeObj.left.x",
                "leftEyeObj.left.y",
                "leftEyeObj.right.x",
                "leftEyeObj.right.y",
                "leftEyeObj.centerPupil.x",
                "leftEyeObj.centerPupil.y",
                "rect.top",
                "rect.bottom",
                "getYaw()",
                "getRoll()",
                "getPitch()",
                "getEyeVerticalGazeAngle()",
                "getEyeHorizontalGazeAngle()",
                "getSmileValue()",
                "getLeftEyeBlink()",
                "getRightEyeBlink()",
                "tBitmapFactory",
                "tCompressBitmap",
                "tSetBitmap",
                "tFaceData",
                "tDbEnd",
        };

        try {
            if (!dirLog.exists()) dirLog.mkdir();
            if (!fileLog.exists()) fileLog.createNewFile();

            bufw = new BufferedWriter(new FileWriter(fileLog, true));
            bufw.write("FILE_NAME");
            for (String s: header) {
                bufw.write(44);
                bufw.write(s);
            }
            bufw.write(10);
            bufw.close();
            bufw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }    }
}
