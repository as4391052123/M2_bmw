package com.bmw.m2.presenter;

/**
 * Created by admin on 2016/9/28.
 */
public interface HCNetSdkLogin {

    void reLoginHaiKang();
    void connectDevice();
    void logout();
    void release();
    void record();
    void capture();
    void stopRecord();
    void setCanReConnect(boolean canReConnect);
}
