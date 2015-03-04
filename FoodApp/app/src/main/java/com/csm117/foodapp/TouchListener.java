package com.csm117.foodapp;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Jose Vega on 2/28/2015.
 */
public class TouchListener implements View.OnTouchListener {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    GestureDetector gestureDet;
    Context context;

    TouchListener(Context ctxt, ImageViewTracker ivt){
        context = ctxt;
        gestureDet = new GestureDetector(context, new GestureListener(ivt));
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent event) {
        gestureDet.onTouchEvent(event);
        return true;
    }
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        ImageViewTracker ivt;
        GestureListener (ImageViewTracker imageViewTracker){
            ivt = imageViewTracker;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Right to left
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Left to right
            }

            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                ivt.imageDown();
                return false; // Swipes down
            }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                ivt.imageUp();
                return false; // Swipes up
            }
            return false;
        }
    }
}
