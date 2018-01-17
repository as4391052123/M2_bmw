package com.bmw.m2.views.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bmw.m2.R;

/**
 * Created by admin on 2017/5/10.
 */

public class RollButton extends View {


    private int mDefaultWidth = 300;
    private int mDefaultHeight = 100;

    private Paint mPaintStroke = new Paint();
    private Paint mPaintBack = new Paint();
    private Paint mPaintBall = new Paint();
    private int mBorderColor = Color.YELLOW;
    private int mBackColor;
    private int mBorderWidth = 5;
    private int mBallCenterColor = -1;
    private int mBallMarginColor = -1;

    private int mCoordsX = -1;
    private int mCoordsY = -1;
    private int width;
    private int height;
    private int mMaxProgress = 100;
    private int mProgress;
    private int mDirection = -1;
    private long lastMoveTime;
    private boolean isShadowShow;
    private boolean isVertical;
    private String mTextOne;
    private String mTextTwo;
    private int mTextSize;
    private Paint mTextPaint;
    private Rect mTextBound;

    public RollButton(Context context) {
        super(context);
        initDraw();
    }

    public RollButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.RollButton);
        mBorderWidth = typeArray.getDimensionPixelSize(R.styleable.RollButton_borderWidth, 5);
        mBorderColor = typeArray.getColor(R.styleable.RollButton_borderColor, Color.YELLOW);
        mBackColor = typeArray.getColor(R.styleable.RollButton_backColor, 0);
        mBallCenterColor = typeArray.getColor(R.styleable.RollButton_ballCenterColor, -1);
        mBallMarginColor = typeArray.getColor(R.styleable.RollButton_ballMarginColor, -1);
        if (mBallMarginColor == -1)
            mBallMarginColor = mBorderColor;
        isShadowShow = typeArray.getBoolean(R.styleable.RollButton_isShadowShow, false);
        isVertical = typeArray.getBoolean(R.styleable.RollButton_isVertical, false);
        mTextOne = typeArray.getString(R.styleable.RollButton_textOne);
        mTextTwo = typeArray.getString(R.styleable.RollButton_textTwo);
        mTextSize = typeArray.getDimensionPixelSize(R.styleable.RollButton_rollTextSize,15);
        typeArray.recycle();
        initDraw();
    }

    public RollButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDraw();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //set for macth_parent and wrap_content
        //1.get current specMode
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        //2.get current specSize
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        //3.set specSize by specMode:AT_MOST mean wrap_content
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            //unit:px
            setMeasuredDimension(mDefaultWidth, mDefaultHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDefaultWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mDefaultHeight);
        }


    }


    private void initDraw() {

        mPaintStroke.setColor(mBorderColor);
        mPaintStroke.setStrokeWidth((float) mBorderWidth);
        mPaintStroke.setAntiAlias(true);
        mPaintStroke.setStyle(Paint.Style.STROKE);

        mPaintBack.setStrokeWidth((float) mBorderWidth);
        mPaintBack.setAntiAlias(true);
        mPaintBack.setStyle(Paint.Style.FILL);
        mPaintBack.setColor(mBackColor);

        mPaintBall.setStrokeWidth((float) mBorderWidth);
        mPaintBall.setAntiAlias(true);
        mPaintBall.setStyle(Paint.Style.FILL);
        mPaintBall.setColor(mBallMarginColor);

        if (isShadowShow) {
            setLayerType(LAYER_TYPE_SOFTWARE, mPaintStroke);
            mPaintStroke.setShadowLayer(1, 1, 1, Color.GRAY);
            setLayerType(LAYER_TYPE_SOFTWARE, mPaintBall);
            mPaintBall.setShadowLayer(2, 2, 3, Color.GRAY);
        }
        mTextPaint = new Paint();
        mTextPaint.setStrokeWidth((float) mBorderWidth);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mBorderColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        height = getHeight();
        width = getWidth();

        int padLeft = getPaddingLeft();
        int padRight = getPaddingRight();
        int padTop = getPaddingTop();
        int padBottom = getPaddingBottom();
        width = width - padLeft - padRight;
        height = height - padTop - padBottom;
        int radiusOut = isVertical ? width / 2 : height / 2;

        if (mTextOne != null && mTextTwo != null && mTextOne.length() == mTextTwo.length()) {
            if (mTextBound == null){
                mTextBound = new Rect();
            }
            mTextPaint.getTextBounds(mTextOne, 0, mTextOne.length(), mTextBound);
            if (isVertical) {
                canvas.drawText(
                        mTextTwo,
                        padLeft + width / 2,
                        padTop + mBorderWidth  + radiusOut,
                        mTextPaint
                );
                canvas.drawText(
                        mTextOne,
                        padLeft + width / 2,
                        padTop + height - mBorderWidth - radiusOut+mTextBound.height(),
                        mTextPaint
                );
            } else {
                canvas.drawText(
                        mTextOne,
                        padLeft + mBorderWidth + radiusOut-mTextBound.width()/2,
                        padTop + height / 2 + mTextBound.height() / 2,
                        mTextPaint
                );
                canvas.drawText(
                        mTextTwo,
                        padLeft + width - mBorderWidth - radiusOut+mTextBound.width()/2,
                        padTop + height / 2 + mTextBound.height() / 2,
                        mTextPaint
                );
            }
        }




        if (isVertical) {
            drawVerticalRounRect(canvas
                    , getPaddingLeft() + radiusOut
                    , getPaddingTop() + radiusOut
                    , getPaddingLeft() + radiusOut
                    , getHeight() - getPaddingBottom() - radiusOut
                    , radiusOut
                    , 0
                    , mPaintStroke);
        } else {
            drawHorizontalRoundRect(canvas
                    , padLeft
                    , padTop
                    , width + padLeft
                    , height + padTop
                    , radiusOut
                    , mPaintStroke);
        }

        if (mBackColor != 0) {
            if (isVertical) {
                drawVerticalRounRect(canvas
                        , getPaddingLeft() + radiusOut
                        , getPaddingTop() + radiusOut
                        , getPaddingLeft() + radiusOut
                        , getHeight() - getPaddingBottom() - radiusOut
                        , radiusOut - mBorderWidth
                        , mBackColor
                        , mPaintBack);
            } else {
                drawHorizontalRoundRect(canvas
                        , padLeft + mBorderWidth / 2
                        , mBorderWidth / 2 + padTop
                        , width - 0 + padLeft - mBorderWidth / 2
                        , height - mBorderWidth / 2 + padTop
                        , radiusOut
                        , mPaintBack);
            }
        }

        if (mCoordsX == -1) {
            mCoordsX = padLeft + width / 2;
        }

        if (mCoordsY == -1) {
            mCoordsY = padTop + height / 2;
        }


        if (mBallCenterColor != -1) {
            RadialGradient radialGradient = new RadialGradient(mCoordsX
                    , mCoordsY
                    , radiusOut - mBorderWidth / 2
                    , mBallCenterColor
                    , mBallMarginColor
                    , RadialGradient.TileMode.CLAMP
            );
            mPaintBall.setShader(radialGradient);
        }

        canvas.drawCircle(mCoordsX, mCoordsY, radiusOut - mBorderWidth / 2, mPaintBall);


    }

    private void drawVerticalRounRect(Canvas canvas, int topX, int topY, int bottomX, int bottomY, int radius, int backColor, Paint paint) {
        RectF rectFTop = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + radius * 2, getPaddingTop() + radius * 2);
        canvas.drawArc(rectFTop, 0, -180, false, paint);

        if (backColor == 0) {
            canvas.drawLine(getPaddingLeft()
                    , getPaddingTop() + radius - 2
                    , getPaddingLeft()
                    , getHeight() - getPaddingBottom() - radius + 2
                    , paint);
            canvas.drawLine(getPaddingLeft() + radius * 2
                    , getPaddingTop() + radius - 2
                    , getPaddingLeft() + radius * 2
                    , getHeight() - getPaddingBottom() - radius + 2
                    , paint);
        } else {
            canvas.drawRect(getPaddingLeft() + mBorderWidth
                    , getPaddingTop() + radius + mBorderWidth
                    , getPaddingLeft() + mBorderWidth + radius * 2
                    , getHeight() - getPaddingBottom() - mBorderWidth - radius
                    , paint);
        }

        RectF rectFBottom = new RectF(getPaddingLeft(), getHeight() - getPaddingBottom() - radius * 2, getPaddingLeft() + radius * 2, getHeight() - getPaddingBottom());
        canvas.drawArc(rectFBottom, 0, 180, false, paint);


    }

    private void drawHorizontalRoundRect(Canvas canvas, int left, int top, int right, int bottom, int radius, Paint paint) {
        RectF rectFout = new RectF(left
                , top
                , right
                , bottom);
        canvas.drawRoundRect(rectFout, radius, radius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isVertical) {
                    touchEventVertical(event);
                } else {
                    touchEventHorizontal(event);
                }
                if (listener != null) {
                    listener.onTouchChange(mDirection, mProgress);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isVertical) {
                    touchEventVertical(event);
                } else {
                    touchEventHorizontal(event);
                }
                if (System.currentTimeMillis() - lastMoveTime > 250)
                    if (listener != null) {
                        lastMoveTime = System.currentTimeMillis();
                        listener.onTouchChange(mDirection, mProgress);
                    }
                break;
            case MotionEvent.ACTION_UP:
                stopTouch();
                if (listener != null) {
                    listener.stopTouch();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                stopTouch();
                if (listener != null) {
                    listener.onTouchChange(mDirection, mProgress);
                }
                break;
            default:

                break;
        }
        return true;
    }

    private void touchEventVertical(MotionEvent event) {
        float y = event.getY();

        int topBorder = getPaddingTop();
        int bottomBorder = height + getPaddingTop();
        if (y < topBorder || y > bottomBorder)
            return;
        int topRadius = topBorder + width / 2;
        int bottomRadius = bottomBorder - width / 2;
        if (y >= topRadius && y <= bottomRadius) {
            mCoordsY = (int) y;

            int subLength = height / 2 + topBorder;
            int dividend = subLength - topBorder;
            int currentLength = (int) Math.abs(y - subLength);
            float bi = currentLength / (float) dividend;
            mProgress = (int) (mMaxProgress * bi);
            if (y > subLength) {
                mDirection = 0;
            }
            if (y < subLength) {
                mDirection = 1;
            }
        } else {
            mProgress = mMaxProgress;
            if (y <= topRadius) {
                mCoordsY = topRadius;
                mDirection = 1;
            } else {
                mCoordsY = bottomRadius;
                mDirection = 0;
            }
        }
        invalidate();
    }

    private void touchEventHorizontal(MotionEvent event) {
        float x = event.getX();

        int leftBorder = getPaddingLeft();
        int rightBorder = width + getPaddingLeft();
        int leftRadius = leftBorder + height / 2;
        int rightRadius = rightBorder - height / 2;
        if (x < leftBorder || x > rightBorder)
            return;

        if (x >= leftRadius && x <= rightRadius) {
            mCoordsX = (int) x;

            int subLength = width / 2 + leftBorder;
            int dividend = subLength - leftRadius;
            int currentLength = (int) Math.abs(x - subLength);
            float bi = currentLength / (float) dividend;
            mProgress = (int) (mMaxProgress * bi);
            if (x < subLength) {
                mDirection = 0;
            } else {
                mDirection = 1;
            }

        } else if (x >= leftBorder && x <= rightBorder) {
            mProgress = mMaxProgress;
            if (x <= leftRadius) {
                mCoordsX = leftRadius;
                mDirection = 0;
            } else {
                mCoordsX = rightRadius;
                mDirection = 1;
            }
        }
        invalidate();
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    private void stopTouch() {
        mCoordsX = -1;
        mCoordsY = -1;
        mProgress = 0;
        mDirection = -1;
        invalidate();
    }


    public interface OnRollBtnChangeListener {
        void onTouchChange(int direction, int progress);

        void stopTouch();
    }

    private OnRollBtnChangeListener listener;

    public void setOnRollBtnChangeListener(OnRollBtnChangeListener listener) {
        this.listener = listener;
    }
}
