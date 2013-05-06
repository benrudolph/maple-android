package com.example.custom_views;

import java.util.Iterator;

import com.twotoasters.android.horizontalimagescroller.widget.TextStyle;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;

public class MapleTextView extends ImageView implements OnTouchListener {

	private Bitmap mCurrAd;
	private String mText;
	private PointF mTextPos;
	private Paint mPaint;
    private static final float DEFAULT_TEXT_SIZE = 14;
	private PointF mPrevTouch;
	private TextStyle mTextStyle;
	
	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;
	private Builder mAddTextDialogBuilder;
	private AlertDialog mAddTextDialog;
	private GestureDetector mGestureDetector;

	public MapleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mCurrAd = null;
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		mGestureDetector = new GestureDetector(context, new GestureListener());

		mText = "";
		mPaint = new Paint();
		mPaint.setTextSize(DEFAULT_TEXT_SIZE);
		mTextPos = new PointF(100, 100);
		
		/****** Dialog for user to enter text ****************/
		mAddTextDialogBuilder = new AlertDialog.Builder(context);
		mAddTextDialogBuilder.setTitle("Add Text");
		mAddTextDialogBuilder.setMessage("Enter the text you would like to include in your ad");

		final EditText input = new EditText(context);
		mAddTextDialogBuilder.setView(input);
		mAddTextDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				mText = value;
				invalidate();
			}
		});

		mAddTextDialogBuilder.setNegativeButton("Cancel",
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					mAddTextDialog.dismiss();
				}
	
		});
		
		mAddTextDialog = mAddTextDialogBuilder.create();
		
		
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (mText != null && mText != "") {
			Iterator<Paint> iter = mTextStyle.iterator();
			
			while (iter.hasNext()) {
				canvas.drawText(mText, mTextPos.x, mTextPos.y, iter.next());
			}
		}
	}
	
	private void moveText(float deltaX, float deltaY) {
		mTextPos.set(mTextPos.x + deltaX, mTextPos.y + deltaY);
		invalidate();
	}
	
	public void setStyle(TextStyle textStyle) {
		mTextStyle = textStyle;
		invalidate();
	}
	/**
	 * Sets the ad. Also takes a ratio so that we can scale the logo to the same size later.
	 * @param ad
	 * @param ratio
	 */
	public void setAd(Bitmap ad) {
		setImageBitmap(ad);
		mCurrAd = ad;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		return false;
	}
	
	/**
	 * When it detects a scaling gesture, it will scale. Moves the logo if there is only
	 * one pointer and it's on the logo.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
	    // Let the ScaleGestureDetector inspect all events.
	    mScaleDetector.onTouchEvent(ev);
	    mGestureDetector.onTouchEvent(ev);
	    
	    if (ev.getPointerCount() == 1) {
		    switch (ev.getAction()) {
			    case MotionEvent.ACTION_DOWN:
			    	if (mPrevTouch == null) {
			    		mPrevTouch = new PointF(ev.getX(), ev.getY());
			    	} else {
			    		mPrevTouch.set(ev.getX(), ev.getY());	
		    		}
			    	break;
				case MotionEvent.ACTION_MOVE:
					moveText(ev.getX() - mPrevTouch.x, ev.getY() - mPrevTouch.y);
		    		mPrevTouch.set(ev.getX(), ev.getY());
					break;
				case MotionEvent.ACTION_CANCEL:
					break;
				case MotionEvent.ACTION_UP:
					break;
				default:
					break;
		    }
	    }
	    return true;
	}
	
	public Bitmap addText() {
		Bitmap newAd = Bitmap.createBitmap(mCurrAd.getWidth(), mCurrAd.getHeight(), mCurrAd.getConfig());
        Canvas canvas = new Canvas(newAd);
        canvas.drawBitmap(mCurrAd, new Matrix(), null);
        if (mText != null && mText != "") {
			Iterator<Paint> iter = mTextStyle.iterator();
			
			while (iter.hasNext()) {
				canvas.drawText(mText, mTextPos.x, mTextPos.y, iter.next());
			}
		}
        return newAd;
        
	}

	/**
	 * Android has built in scale listener.
	 * 
	 *
	 */
	private class ScaleListener 
        extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
	    public boolean onScale(ScaleGestureDetector detector) {
	        mScaleFactor *= detector.getScaleFactor();
	
	        // Don't let the object get too small or too large.
	        mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
	        mPaint.setTextSize(DEFAULT_TEXT_SIZE * mScaleFactor);
	        invalidate();
	        return true;
	    }
	}
	
	private class GestureListener
		extends GestureDetector.SimpleOnGestureListener {
		
		@Override
		public void onLongPress(MotionEvent e) {
			mAddTextDialog.show();
		}
	}

}