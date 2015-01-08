package com.example.jhihanlin.dramotmediaplayer.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.SeekBar;


/**
 * Created by jhihanlin on 12/22/14.
 */
public class MySeekBar extends SeekBar {

    private Context mContext;
    private Paint paint;

    public MySeekBar(Context context) {
        super(context);
        mContext = context;
        setup();


    }

    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setup();

    }

    public MySeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();

    }

    protected void setup() {
        paint = new Paint();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // First draw the regular progress bar, then custom draw our text
        super.onDraw(canvas);
//        paint.setColor(R.color.white);
//        paint.setStyle(Paint.Style.STROKE);
//        RectF r = new RectF(0, 0, getWidth() - 1, getHeight() - 1);
//        canvas.drawRoundRect(r, getHeight() / 2, getHeight() / 2, paint);
//        canvas.drawPaint(paint);

    }

}
