package com.bmw.m2.views.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmw.m2.R;

/**
 * Created by admin on 2017/7/19.
 */

public class CompositeLlImgTv extends FrameLayout {

    private ImageView iv_show;
    private TextView tv_show;

    private int imgId;
    private String textStr;
    private int imgWidth;
    private int imgHeight;
    private int imgPadding;
    private int textColor;
    private int textSize;

    public CompositeLlImgTv(Context context) {
        super(context);
        initView(context);

    }

    public CompositeLlImgTv(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        initView(context);
    }

    public CompositeLlImgTv(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initView(context);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CompositeLlImgTv);
        imgId = typedArray.getResourceId(R.styleable.CompositeLlImgTv_CompositeLlImgTv_imgId, R.mipmap.ic_launcher);
        imgWidth = typedArray.getDimensionPixelSize(R.styleable.CompositeLlImgTv_CompositeLlImgTv_imgWidth, 30);
        imgHeight = typedArray.getDimensionPixelSize(R.styleable.CompositeLlImgTv_CompositeLlImgTv_imgWidth, 30);
        imgPadding = typedArray.getDimensionPixelSize(R.styleable.CompositeLlImgTv_CompositeLlImgTv_imgPadding, 5);
        textStr = typedArray.getString(R.styleable.CompositeLlImgTv_CompositeLlImgTv_text);
        textSize = (int) typedArray.getDimension(R.styleable.CompositeLlImgTv_CompositeLlImgTv_textSize, 12);
        textColor = typedArray.getColor(R.styleable.CompositeLlImgTv_CompositeLlImgTv_textColor, Color.BLACK);
        typedArray.recycle();
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_layout_ll_tv_img, this, true);
        iv_show = (ImageView) findViewById(R.id.iv_custom);
        tv_show = (TextView) findViewById(R.id.tv_custom);

        ViewGroup.LayoutParams lp = iv_show.getLayoutParams();
        lp.width = imgWidth;
        lp.height = imgHeight;
        iv_show.setImageResource(imgId);
        iv_show.setLayoutParams(lp);
        iv_show.setPadding(imgPadding, imgPadding, imgPadding, imgPadding);

        if (textStr != null)
            tv_show.setText(textStr);
        tv_show.setTextColor(textColor);
        tv_show.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }
}
