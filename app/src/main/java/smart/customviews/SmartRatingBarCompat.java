package smart.customviews;

import android.content.Context;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.AttributeSet;

/**
 * Created by tasol on 25/7/15.
 */
public class SmartRatingBarCompat extends AppCompatRatingBar {
    public SmartRatingBarCompat(Context context) {
        super(context);
    }

    public SmartRatingBarCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartRatingBarCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
