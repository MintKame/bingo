package com.bingo.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.bingo.android.R;

public class TimeView extends View {
    private Paint mPaint;
    private Bitmap ssBitmap = null;
    private Bitmap sssBitmap = null;
    private Bitmap mmBitmap = null;
    private Bitmap mmmBitmap = null;
    private int ssx;
    private int ssy;
    private int mmx;
    private int mmy;
    private Context mContext;
    private Matrix matrix = null;
    private Matrix mmatrix = null;
    private float angle = 0;//秒针每秒偏移的角度
    private float mangle = 0;//分针每秒偏移的角度
    private int num = 0;

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initBitmap();
    }

    public TimeView(Context context) {
        super(context);
        this.mContext = context;
        initBitmap();
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initBitmap();
    }

    public void rotate(){
        if (angle == 360) {
            angle = 0;
        }
        if (mangle == 360) {
            mangle = 0;
        }
        matrix.setRotate(angle);
        sssBitmap = Bitmap.createBitmap(ssBitmap, 0, 0, ssx, ssy, matrix, true);
        mmatrix.setRotate(mangle);
        mmmBitmap = Bitmap.createBitmap(mmBitmap, 0, 0, mmx, mmy, mmatrix, true);
        angle += 6;
        if (num%5==0) {//控制分针五秒移动一个角度，也可以每秒都让其移动
            mangle += 0.5;
        }
        num++;
        if (num==200){
            num=0;
        }
        postInvalidate();//重新绘制
    }

    private void initBitmap() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        ssBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.timer_second);
        mmBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.timer_minute);

        ssx = ssBitmap.getWidth();//获取bitmap的宽度
        ssy = ssBitmap.getHeight();
        mmx = mmBitmap.getWidth();
        mmy = mmBitmap.getHeight();
        matrix = new Matrix();
        matrix.setRotate(angle);//设置图片旋转角度
        mmatrix = new Matrix();
        matrix.setRotate(mangle);
        sssBitmap = Bitmap.createBitmap(ssBitmap, 0, 0, ssx, ssy, matrix, true);//创建新的bitmap
        mmmBitmap = Bitmap.createBitmap(mmBitmap, 0, 0, mmx, mmy, mmatrix, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mmmBitmap, getWidth() / 2 - mmmBitmap.getWidth() / 2, getHeight() / 2 - mmmBitmap.getHeight() / 2, mPaint);
        canvas.drawBitmap(sssBitmap, getWidth() / 2 - sssBitmap.getWidth() / 2, getHeight() / 2 - sssBitmap.getHeight() / 2, mPaint);
    }
}