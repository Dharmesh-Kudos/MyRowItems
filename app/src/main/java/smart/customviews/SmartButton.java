package smart.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import smart.framework.SmartApplication;


/**
 * This Class Contains All Method Related To IjoomerButton.
 *
 * @author tasol
 */
public class SmartButton extends AppCompatButton {

    public SmartButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SmartButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SmartButton(Context context) {
        super(context);
        init(context);
    }

    private void init(Context mContext) {
        try {
            if (SmartApplication.REF_SMART_APPLICATION.FONT != null) {

                setTypeface(SmartApplication.REF_SMART_APPLICATION.FONT);
            } else {

                Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), SmartApplication.REF_SMART_APPLICATION.FONT_NAME);
                SmartApplication.REF_SMART_APPLICATION.FONT = typeface;
                setTypeface(SmartApplication.REF_SMART_APPLICATION.FONT);
            }
        } catch (Throwable e) {
        }
    }
}
