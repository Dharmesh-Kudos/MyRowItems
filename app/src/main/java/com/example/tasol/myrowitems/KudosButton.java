package com.example.tasol.myrowitems;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by tasol on 7/4/17.
 */

public class KudosButton extends Button {
    public KudosButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public KudosButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KudosButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Ubuntu-L.ttf");
            setTypeface(tf);
        }
    }

}
