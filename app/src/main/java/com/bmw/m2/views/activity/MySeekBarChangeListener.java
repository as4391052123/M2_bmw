package com.bmw.m2.views.activity;

import android.widget.SeekBar;

/**
 * Created by admin on 2017/7/10.
 */

public class MySeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
    private MyOnSeekBarChangeListener myOnSeekBarChangeListener;
    private long lastChangeTime;
    private static final int DEFAULT_TIME = 00;

    public MySeekBarChangeListener(MyOnSeekBarChangeListener myOnSeekBarChangeListener) {
        this.myOnSeekBarChangeListener = myOnSeekBarChangeListener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (System.currentTimeMillis() - lastChangeTime >= DEFAULT_TIME &&
                myOnSeekBarChangeListener != null) {
            myOnSeekBarChangeListener.change(seekBar,progress);
        }
        lastChangeTime = System.currentTimeMillis();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    interface MyOnSeekBarChangeListener {
        void change(SeekBar seekBar, int progress);
    }
}
