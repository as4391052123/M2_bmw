package com.bmw.m2.views.fragment;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by admin on 2017/7/18.
 */

public class MyPopupWindow {

    private PopupWindow mPopupWindow;
    private int loc_x;
    private int loc_y;
    private String tag;

    public MyPopupWindow(View view, int width, int height, boolean isOutSideTouchable,int loc_x,int loc_y) {

        mPopupWindow = new PopupWindow(view,width,height);
        mPopupWindow.setWidth(width);
        mPopupWindow.setHeight(height);
        if(isOutSideTouchable) {
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(isOutSideTouchable);
            mPopupWindow.setOutsideTouchable(isOutSideTouchable);
            mPopupWindow.setTouchable(true);
        }
        this.loc_x = loc_x;
        this.loc_y = loc_y;
    }


    public void show(View view){
        mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY,loc_x,loc_y);
    }

    public void dismiss(){
        mPopupWindow.dismiss();
    }

    public String getTag(){
        return tag;
    }

    public void setTag(String str){
        this.tag = str;
    }

    public PopupWindow getmPopupWindow(){
        return mPopupWindow;
    }
}
