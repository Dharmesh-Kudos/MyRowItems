package smart.common;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class IjoomerSpannable extends ClickableSpan {

    private int color = -1;
    private boolean isUnderline = true;

    /**
     * Constructor
     */
    public IjoomerSpannable(int color, boolean isUnderline) {
        this.isUnderline = isUnderline;
        this.color = color;
    }

    /**
     * Overrides methods
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        if (color != -1) {
            ds.setColor(color);
        }
        ds.setUnderlineText(isUnderline);
    }

    @Override
    public void onClick(View widget) {

    }
}