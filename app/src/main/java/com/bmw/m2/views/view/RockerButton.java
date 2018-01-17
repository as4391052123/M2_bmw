package com.bmw.m2.views.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.bmw.m2.R;

/**
 * Created by admin on 2017/5/12.
 */

public class RockerButton extends View {

    private String TAG = "rocker";

    private int mDefaultWidth = 300;
    private int mDefaultHeight = 300;

    private Paint mPaint = new Paint();

    private int mBallRadius = 50;
    private int mBallRadiusPress = 56;
    private int mBallRadiusUnPress = 50;

    private int mBallCenterX = -1;
    private int mBallCenterY = -1;

    private int mBallColor = 0x40ff9900;
    private int mBallCenterColor;
    private int mPressBallColor;
    private int mUnPressBallColor;
    private int mPressBallCenterColor;
    private int mUnPressBallCenterColor;
    private Bitmap mBitmap;
    private int mWidth;
    private int mHeight;
    private long lastClickTime;
    private boolean isShowShadow;
    private Drawable mDrawable;
    private Paint mBitmapPaint = new Paint();
//    private Canvas mBitmapCanvas;


    public RockerButton(Context context) {
        super(context);
        initDraw();
    }

    public RockerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.RockerButton);

        Drawable d = typeArray.getDrawable(R.styleable.RockerButton_ballImage);
//        int soureId = typeArray.getResourceId(R.styleable.RockerButton_ballImage,-1);
        int radius = typeArray.getDimensionPixelSize(R.styleable.RockerButton_ballRadius, 50);
        mBallRadiusUnPress = radius;
        mBallRadiusPress = radius + 6;
        mBallRadius = mBallRadiusUnPress;
       /* if(soureId != -1){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            mBitmap = BitmapFactory.decodeResource(context.getResources(),soureId,options);
        }*/

        if (d != null) {
            setImageDrawable(d);
        }
        mPressBallColor = typeArray.getColor(R.styleable.RockerButton_ballPressColor, 0);
        mUnPressBallColor = typeArray.getColor(R.styleable.RockerButton_ballUnPressColor, 0);
        mPressBallCenterColor = typeArray.getColor(R.styleable.RockerButton_ballPressCenterColor, 0);
        mUnPressBallCenterColor = typeArray.getColor(R.styleable.RockerButton_ballUnPressCenterColor, 0);
        isShowShadow = typeArray.getBoolean(R.styleable.RockerButton_isShowShadow,false);
        typeArray.recycle();
        initDraw();

    }


    public RockerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDraw();
    }


    private void setImageDrawable(Drawable d) {
        if (mDrawable != d) {
            mDrawable = d;
            if (mBitmap != null) {
                mBitmap.recycle();
                mBitmap = null;
            }

//            int w = mDrawable.getIntrinsicWidth();
//            int h = mDrawable.getIntrinsicHeight();
//
//            Bitmap.Config config = mDrawable.getOpacity() == PixelFormat.OPAQUE? Bitmap.Config.ARGB_8888: Bitmap.Config.RGB_565;
//
//            mBitmap = Bitmap.createBitmap(w,h,config);
//            mBitmapCanvas = new Canvas(mBitmap);
//            mDrawable.setBounds(0,0,w,h);
//            mDrawable.draw(mBitmapCanvas);


            mBitmap = ((BitmapDrawable) mDrawable).getBitmap();


        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDefaultWidth, mDefaultHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDefaultWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mDefaultHeight);
        }

    }

    private void initDraw() {
        if (mUnPressBallColor != 0)
            mBallColor = mUnPressBallColor;
        if (mUnPressBallCenterColor != 0)
            mBallCenterColor = mUnPressBallCenterColor;
        mPaint.setColor(mBallColor);
        mPaint.setAntiAlias(true);
        if(isShowShadow){
            setLayerType(LAYER_TYPE_SOFTWARE,mPaint);
            mPaint.setShadowLayer(5,3,2, Color.GRAY);
        }

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setFilterBitmap(true);
        mBitmapPaint.setDither(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getWidth();
        mHeight = getHeight();

        int padLeft = getPaddingLeft();
        int padRight = getPaddingRight();
        int padTop = getPaddingTop();
        int padBottom = getPaddingBottom();

        mWidth = mWidth - padLeft - padRight;
        mHeight = mHeight - padTop - padBottom;

        if (mBallRadius > mWidth / 2)
            mBallRadius = mWidth / 2;


        if (mBallCenterX == -1 || mBallCenterY == -1) {
            mBallCenterX = mWidth / 2 + padLeft;
            mBallCenterY = mHeight / 2 + padTop;
        }

        if (mBitmap == null) {
            if (mBallCenterColor != 0) {
                RadialGradient radialGradient = new RadialGradient(mBallCenterX
                        , mBallCenterY
                        , mBallRadius
                        , mBallCenterColor
                        , mBallColor
                        , RadialGradient.TileMode.CLAMP);
                mPaint.setShader(radialGradient);
            }

            mPaint.setColor(mBallColor);
            canvas.drawCircle(mBallCenterX, mBallCenterY, mBallRadius, mPaint);
        } else {


            Rect rect = new Rect(mBallCenterX - mBallRadius
                    , mBallCenterY - mBallRadius
                    , mBallCenterX + mBallRadius
                    , mBallCenterY + mBallRadius);
            Rect rectBitmap = new Rect(0,0,mBitmap.getWidth(),mBitmap.getHeight());
            canvas.drawBitmap(mBitmap, rectBitmap, rect, mBitmapPaint);

        }
    }
    public void releaseBitmap() {
        Log.d(TAG, "releaseBitmap: ");
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
            mDrawable = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchMove(event);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                touchStop();
                break;
        }

        return true;
    }

    private void touchMove(MotionEvent event) {
        if (mPressBallColor != 0)
            mBallColor = mPressBallColor;
        if (mPressBallCenterColor != 0)
            mBallCenterColor = mPressBallCenterColor;
        mBallRadius = mBallRadiusPress;

        float currentX = event.getX();
        float currentY = event.getY();
        int ballX = mWidth / 2 + getPaddingLeft();
        int ballY = mHeight / 2 + getPaddingTop();
        double len = Math.sqrt(Math.pow((currentX - ballX), 2) + Math.pow((currentY - ballY), 2));
        int maxRadius = mWidth >= mHeight ? mWidth / 2 : mHeight / 2;
        int reRadius = maxRadius - mBallRadius;
        double angle = Math.atan2(currentY - ballY, currentX - ballX);

        if (len > reRadius) {
            mBallCenterX = (int) (reRadius * Math.cos(angle) + ballX);
            mBallCenterY = (int) (reRadius * Math.sin(angle) + ballY);
            len = reRadius;

        } else {
            mBallCenterX = (int) currentX;
            mBallCenterY = (int) currentY;
        }
        int angleNum = (int) (angle * 180 / Math.PI);
        if (angleNum <= 0)
            angleNum = angleNum + 360;
        double bate = len / reRadius;

        if (System.currentTimeMillis() - lastClickTime > 200) {
            if (directionClickListener != null) {
                swithDirection(bate, angleNum);
            }
            if (rockerRollTouchListerner != null) {
                rockerRollTouchListerner.rolling(bate, angleNum);
            }
            lastClickTime = System.currentTimeMillis();
        }

        invalidate();

    }

    private void swithDirection(double bate, int angle) {
        if (angle >= 45 && angle < 135) {
            directionClickListener.bottomClick(bate);
        } else if (angle >= 135 && angle < 225) {
            directionClickListener.leftClick(bate);
        } else if (angle >= 225 && angle < 315) {
            directionClickListener.topClick(bate);
        } else {
            directionClickListener.rightClick(bate);
        }
    }


    private void touchStop() {
        if(directionClickListener != null){
            directionClickListener.stopTouch();
        }
        if(rockerRollTouchListerner != null){
            rockerRollTouchListerner.stopRoll();
        }

        mBallRadius = mBallRadiusUnPress;
        if (mUnPressBallColor != 0)
            mBallColor = mUnPressBallColor;
        if (mUnPressBallCenterColor != 0)
            mBallCenterColor = mUnPressBallCenterColor;
        mBallCenterX = mWidth / 2+getPaddingLeft();
        mBallCenterY = mHeight / 2+getPaddingTop();
        invalidate();
    }

    interface OnDirectionClickListener {
        void topClick(double bate);

        void bottomClick(double bate);

        void leftClick(double bate);

        void rightClick(double bate);

        void stopTouch();
    }

    interface OnRockerRollTouchListerner {
        void rolling(double bate, int angle);

        void stopRoll();
    }

    private OnDirectionClickListener directionClickListener;
    private OnRockerRollTouchListerner rockerRollTouchListerner;

    public void setOnDirectionClickListener(OnDirectionClickListener directionClickListener) {
        this.directionClickListener = directionClickListener;
    }

    public void setOnRockerRollTouchListerner(OnRockerRollTouchListerner rockerRollTouchListerner) {
        this.rockerRollTouchListerner = rockerRollTouchListerner;
    }

}
