package com.bmw.m2.presenter;

/**
 * Created by admin on 2017/6/24.
 */

public interface PtzControlPresenter {

    void zoom_in(boolean isStart);
    void zoom_out(boolean isStart);
    void focus_near(boolean isStart);
    void focus_far(boolean isStart);
}
