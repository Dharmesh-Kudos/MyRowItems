package smart.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * This Class Contains All Method Related To IjoomerListView.
 *
 * @author tasol
 */
public class SmartListView extends ListView {

    @SuppressWarnings("unused")
    private static final int SWIPE_MIN_DISTANCE = 50;
    @SuppressWarnings("unused")
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    @SuppressWarnings("unused")
    private GestureDetector gDetector;
    @SuppressWarnings("unused")
    private boolean isFling;

    private float mDiffX;
    private float mDiffY;
    private float mLastX;
    private float mLastY;

    public SmartListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SmartListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SmartListView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Overrides method
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // reset difference values
                mDiffX = 0;
                mDiffY = 0;

                mLastX = ev.getX();
                mLastY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                mDiffX += Math.abs(curX - mLastX);
                mDiffY += Math.abs(curY - mLastY);
                mLastX = curX;
                mLastY = curY;

                // don't intercept event, when user tries to scroll vertically
                if (mDiffX > mDiffY) {
                    return false; // do not react to horizontal touch events, these
                    // events will be passed to your list item view
                }
        }

        return super.onInterceptTouchEvent(ev);
    }

    private void init(Context mContext) {
    }
}
