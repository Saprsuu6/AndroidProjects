package step.learning.basics.Game2048;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class OnSwipeListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector;

    public OnSwipeListener(Context context) {
        this.gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int MIN_SWIPE_DISTANCE = 100;
        private static final int MIN_SWIPE_VELOCITY = 100;

        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            float dx = e2.getX() - e1.getX();
            float dy = e2.getY() - e1.getY();
            if (Math.abs(dx) > Math.abs(dy)) {
                if (Math.abs(dx) >= MIN_SWIPE_DISTANCE && Math.abs(velocityX) >= MIN_SWIPE_VELOCITY) {
                    if (dx > 0) OnSwipeRight();
                    else OnSwipeLeft();
                    return true;
                }
            } else {
                if (Math.abs(dy) >= MIN_SWIPE_DISTANCE && Math.abs(velocityY) >= MIN_SWIPE_VELOCITY) {
                    if (dy > 0) OnSwipeBottom();
                    else OnSwipeTop();
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true;
        }
    }

    public void OnSwipeRight() {

    }

    public void OnSwipeLeft() {

    }

    public void OnSwipeTop() {

    }

    public void OnSwipeBottom() {

    }
}
