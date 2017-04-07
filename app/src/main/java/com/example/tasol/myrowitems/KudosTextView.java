package com.example.tasol.myrowitems;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by tasol on 7/4/17.
 */

public class KudosTextView extends TextView {
    public KudosTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public KudosTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KudosTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tfB = Typeface.createFromAsset(getContext().getAssets(), "fonts/Ubuntu-B.ttf");
            Typeface tfL = Typeface.createFromAsset(getContext().getAssets(), "fonts/Ubuntu-L.ttf");
            if (getTypeface().getStyle() == Typeface.BOLD) {
                setTypeface(tfB);
            } else if (getTypeface().getStyle() == Typeface.NORMAL) {
                setTypeface(tfL);
            } else if (getTypeface().getStyle() == Typeface.ITALIC || getTypeface().getStyle() == Typeface.BOLD_ITALIC) {
                setTypeface(tfL);
            } else {
                setTypeface(tfL);
            }
        }
    }

}
