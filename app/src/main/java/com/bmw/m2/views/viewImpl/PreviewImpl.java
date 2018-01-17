package com.bmw.m2.views.viewImpl;

import com.bmw.m2.model.CarRemoteDeviecInfo;
import com.bmw.m2.model.RemoteDeviceInfo;

/**
 * Created by admin on 2017/6/8.
 */

public interface PreviewImpl {
    void result(RemoteDeviceInfo remoteDeviceInfo);
    void carResult(CarRemoteDeviecInfo carRemoteDeviecInfo);
    void record(int startOrEnd,boolean isSuccess);
    void capture(String path);
    void loginSuccessful();
}
