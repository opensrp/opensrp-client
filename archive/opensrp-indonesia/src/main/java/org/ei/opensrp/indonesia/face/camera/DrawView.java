/* ======================================================================
 *  Copyright � 2014 Qualcomm Technologies, Inc. All Rights Reserved.
 *  QTI Proprietary and Confidential.
 *  =====================================================================
 *  
 * @file:   DrawView.java
 *
 */


package org.ei.opensrp.indonesia.face.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.view.SurfaceView;

import com.qualcomm.snapdragon.sdk.face.FaceData;

public class DrawView extends SurfaceView {
	
	private Paint leftEyeBrush = new Paint();
	private Paint rightEyeBrush = new Paint();
	private Paint mouthBrush = new Paint();
	private Paint rectBrush = new Paint();
	public Point leftEye, rightEye, mouth;
	Rect mFaceRect;
	public FaceData []mFaceArray;	
	boolean _inFrame;			// Boolean to see if there is any faces in the frame
	int mSurfaceWidth;
	int mSurfaceHeight;
	int cameraPreviewWidth;
	int cameraPreviewHeight;
	boolean mLandScapeMode;
	float scaleX=1.0f;
	float scaleY=1.0f;


	public DrawView(Context context, FaceData []faceArray, boolean inFrame) {
		super(context);
        setWillNotDraw(false);
        mFaceArray = faceArray;
        _inFrame= inFrame;
	}
	

	@Override
	protected void onDraw(Canvas canvas){
		
		if(_inFrame)				// If the face detected is in frame. 
		{					
			for(int i = 0; i < mFaceArray.length; i++)
			{
				leftEyeBrush.setColor(Color.RED);
				canvas.drawCircle(mFaceArray[i].leftEye.x, mFaceArray[i].leftEye.y, 5f, leftEyeBrush);

				rightEyeBrush.setColor(Color.GREEN);
				canvas.drawCircle(mFaceArray[i].rightEye.x, mFaceArray[i].rightEye.y, 5f, rightEyeBrush);

				mouthBrush.setColor(Color.WHITE);
				canvas.drawCircle(mFaceArray[i].mouth.x, mFaceArray[i].mouth.y, 5f, mouthBrush);	
				
				setRectColor(mFaceArray[i], rectBrush);	// changing color w.r.t. smile				
				
				rectBrush.setStrokeWidth(2);
				rectBrush.setStyle(Paint.Style.STROKE);
				canvas.drawRect(mFaceArray[i].rect.left, mFaceArray[i].rect.top, mFaceArray[i].rect.right, mFaceArray[i].rect.bottom, rectBrush);
			}			
		}
		else
		{
			canvas.drawColor(0, Mode.CLEAR);
		}
	}
	
	/*
	 * Function to set the color of the stroke based on smile value
	 */
	private void setRectColor(FaceData faceData, Paint rectBrush) {
		if(faceData.getSmileValue()<40) {

			rectBrush.setColor(Color.RED);

		} else if(faceData.getSmileValue()<55) {

			rectBrush.setColor(Color.parseColor("#FE642E"));		// Red-Orange

		} else if(faceData.getSmileValue()<70) {

			rectBrush.setColor(Color.parseColor("#D7DF01"));		// Orange-Yellow

		} else if(faceData.getSmileValue()<85) {

			rectBrush.setColor(Color.parseColor("#86B404"));		// Yellow-Green

		} else {

			rectBrush.setColor(Color.parseColor("#5FB404"));		// Green
		}		
		
	}
	

	

}
