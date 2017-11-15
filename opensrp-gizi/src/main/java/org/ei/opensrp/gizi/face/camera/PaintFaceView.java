package org.ei.opensrp.gizi.face.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.view.SurfaceView;

import com.qualcomm.snapdragon.sdk.face.FaceData;

import org.ei.opensrp.gizi.face.camera.util.FaceConstants;


public class PaintFaceView extends SurfaceView {

    public FaceData[]mFaceArray;
    boolean existFrame;
	private Paint leftEyeBrush = new Paint();
	private Paint rightEyeBrush = new Paint();
	private Paint mouthBrush = new Paint();
	private Paint rectBrush = new Paint();
	public Point leftEye, rightEye, mouth;
	Rect mFaceRect;
	int mSurfaceWidth;
	int mSurfaceHeight;
	int cameraPreviewWidth;
	int cameraPreviewHeight;
	boolean mLandScapeMode;
	float scaleX=1.0f;
	float scaleY=1.0f;

	public PaintFaceView(Context context, FaceData[] faceArray, boolean inFrame) {
		super(context);
        setWillNotDraw(false);
        mFaceArray = faceArray;
        existFrame = inFrame;
	}
	

	@Override
	protected void onDraw(Canvas canvas){

		// Check exist face in frame.
		if(existFrame) {
			for (FaceData aMFaceArray : mFaceArray) {
				leftEyeBrush.setColor(Color.RED);
				canvas.drawCircle(aMFaceArray.leftEye.x, aMFaceArray.leftEye.y, 5f, leftEyeBrush);

				rightEyeBrush.setColor(Color.GREEN);
				canvas.drawCircle(aMFaceArray.rightEye.x, aMFaceArray.rightEye.y, 5f, rightEyeBrush);

				mouthBrush.setColor(Color.WHITE);
				canvas.drawCircle(aMFaceArray.mouth.x, aMFaceArray.mouth.y, 5f, mouthBrush);

				setRectColor(aMFaceArray, rectBrush);    // changing color w.r.t. smile

				rectBrush.setStrokeWidth(2);
				rectBrush.setStyle(Paint.Style.STROKE);
				canvas.drawRect(aMFaceArray.rect.left, aMFaceArray.rect.top, aMFaceArray.rect.right, aMFaceArray.rect.bottom, rectBrush);
			}

		} else {
			canvas.drawColor(0, Mode.CLEAR);
		}
	}

    /*
    Method Coloring face by Smile Value
     */
	private void setRectColor(FaceData faceData, Paint rectBrush) {
		if(faceData.getSmileValue() < 40) {

			rectBrush.setColor(Color.RED);

		} else if(faceData.getSmileValue() < 55) {

			rectBrush.setColor(Color.parseColor(FaceConstants.RED_ORANGE));

		} else if(faceData.getSmileValue() < 70) {

			rectBrush.setColor(Color.parseColor(FaceConstants.ORANGE_YELLOW));

		} else if(faceData.getSmileValue() < 85) {

			rectBrush.setColor(Color.parseColor(FaceConstants.YELLOW_GREEN));

		} else {

			rectBrush.setColor(Color.parseColor(FaceConstants.GREEN));
		}
		
	}
	

	

}
