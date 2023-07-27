package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;
import org.telegram.messenger.AndroidUtilities;
public class DotDividerSpan extends ReplacementSpan {
    int color;
    Paint p = new Paint(1);
    private int size = 3;
    int topPadding;

    @Override
    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        return AndroidUtilities.dp(this.size);
    }

    @Override
    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        if (this.color != paint.getColor()) {
            this.p.setColor(paint.getColor());
        }
        canvas.drawCircle(f + (AndroidUtilities.dpf2(this.size) / 2.0f), ((i5 - i3) / 2) + this.topPadding, AndroidUtilities.dpf2(3.0f) / 2.0f, this.p);
    }

    public void setTopPadding(int i) {
        this.topPadding = i;
    }

    public void setSize(int i) {
        this.size = i;
    }
}
