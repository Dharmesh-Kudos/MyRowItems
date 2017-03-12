package smart.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout {

    private onTouchUpListener onTouchUpListener;

    public TouchableWrapper(Context context) {
        super(context);
    }

    public TouchableWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchableWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnTouchUpListener(TouchableWrapper.onTouchUpListener onTouchUpListener) {
        this.onTouchUpListener = onTouchUpListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (onTouchUpListener != null) {
                    onTouchUpListener.onTouchUp();
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public interface onTouchUpListener {
        void onTouchUp();
    }
}