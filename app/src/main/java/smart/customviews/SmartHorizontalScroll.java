package smart.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * This Class Contains All Method Related To IjoomerHorizontalScroll.
 *
 * @author tasol
 */
public class SmartHorizontalScroll extends HorizontalScrollView {


    public SmartHorizontalScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SmartHorizontalScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SmartHorizontalScroll(Context context) {
        super(context);
        init(context);
    }

    /**
     * Overrides methods
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    void init(Context context) {
        setHorizontalFadingEdgeEnabled(false);

        setVerticalFadingEdgeEnabled(false);

        setVerticalScrollBarEnabled(false);

        setHorizontalScrollBarEnabled(false);

    }

}
