package com.example.tasol.myrowitems;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by tasol on 7/4/17.
 */

public class KudosEditText extends EditText {
    public KudosEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public KudosEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KudosEditText(Context context) {
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
