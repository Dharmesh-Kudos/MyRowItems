package smart.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import smart.framework.SmartApplication;

/**
 * This Class Contains All Method Related To IjoomerCheckBox.
 *
 * @author tasol
 */
public class SmartCheckBox extends AppCompatCheckBox {

    public SmartCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SmartCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SmartCheckBox(Context context) {
        super(context);
        init(context);
    }

    private void init(Context mContext) {
        try {
            if (getTypeface().getStyle() == Typeface.BOLD) {
                if (SmartApplication.REF_SMART_APPLICATION.BOLDFONT != null) {

                    setTypeface(SmartApplication.REF_SMART_APPLICATION.BOLDFONT);
                } else {

                    Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), SmartApplication.REF_SMART_APPLICATION.BOLDFONT_NAME);
                    SmartApplication.REF_SMART_APPLICATION.BOLDFONT = typeface;
                    setTypeface(SmartApplication.REF_SMART_APPLICATION.BOLDFONT);
                }
            } else {
                if (SmartApplication.REF_SMART_APPLICATION.FONT != null) {

                    setTypeface(SmartApplication.REF_SMART_APPLICATION.FONT);
                } else {

                    Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), SmartApplication.REF_SMART_APPLICATION.FONT_NAME);
                    SmartApplication.REF_SMART_APPLICATION.FONT = typeface;
                    setTypeface(SmartApplication.REF_SMART_APPLICATION.FONT);
                }
            }
        } catch (Throwable e) {
        }
    }
}
