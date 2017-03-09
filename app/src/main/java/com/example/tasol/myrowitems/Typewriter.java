package com.example.tasol.myrowitems;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class Typewriter extends TextView {

    private static final String TAG = Typewriter.class.getSimpleName();
    Handler animationCompleteCallBack;
    private CharSequence mText;
    private int mIndex;
    private long mDelay = 500; // Default 500ms character delay
    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if (mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
            } else {
                if (null != animationCompleteCallBack)
                    animationCompleteCallBack.sendMessage(new Message());
                else
                    Log.i(TAG, "Animation Complete Listener not set...");
            }
        }
    };

    public Typewriter(Context context) {
        super(context);
    }

    public Typewriter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAnimationCompleteListener(Handler animationCompleteCallBack) {
        this.animationCompleteCallBack = animationCompleteCallBack;
    }

    public void animateText(CharSequence text) {
        mText = text;
        mIndex = 0;
        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }
}