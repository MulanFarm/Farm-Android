package com.xcc.mylibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.xcc.mylibrary.R;

/**
 * Created by Administrator on 2016/11/21.
 */
public class LineView extends View {
    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        init(context);
    }

    public LineView(Context context) {
        super(context);
        init(context);
    }

    public LineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;
        init(context);
    }

    private AttributeSet attrs;

    private void init(Context context) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LineView);
            fillLeft = a.getDimension(R.styleable.LineView_fillLeft, 0);
            fillRight = a.getDimension(R.styleable.LineView_fillRight, 0);
            fillColor = a.getColor(R.styleable.LineView_fillColor, 0);
            attrs = null;
        }
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(fillColor);
    }

    private float fillLeft, fillRight;
    private int fillColor;

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
        paint.setColor(fillColor);
    }

    public void setFillLeft(float fillLeft) {
        this.fillLeft = fillLeft;
    }

    public void setFillRight(float fillRight) {
        this.fillRight = fillRight;
    }

    private Paint paint;

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0)
            return;
        if (fillLeft > 0) {
            if (fillLeft > width) fillLeft = width;
            canvas.drawRect(0, 0, fillLeft, height, paint);
        }
        if (fillRight > 0) {
            if (fillRight > width) fillRight = width;
            canvas.drawRect(width - fillRight, 0, width, height, paint);
        }
    }
}
