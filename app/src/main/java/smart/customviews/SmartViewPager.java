package smart.customviews;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * This Class Contains All Method Related To IjoomerViewPager.
 *
 * @author tasol
 */
public class SmartViewPager extends ViewPager {

    private boolean scrollable = true;

    public SmartViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartViewPager(Context context) {
        super(context);
    }

    /**
     * Overrides methods
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.scrollable) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.scrollable) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    /**
     * This method used to check is scrollable.
     *
     * @return represented {@link Boolean}
     */
    public boolean isScrollable() {
        return scrollable;
    }

    /**
     * This method used to set is scrollable.
     *
     * @param scrollable represented scrollable
     */
    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }


}
