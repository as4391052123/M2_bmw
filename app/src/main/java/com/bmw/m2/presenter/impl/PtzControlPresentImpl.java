package com.bmw.m2.presenter.impl;

import com.bmw.m2.model.All_id_Info;
import com.bmw.m2.presenter.PtzControlPresenter;
import com.hikvision.netsdk.HCNetSDK;

/**
 * Created by admin on 2017/6/24.
 */

public class PtzControlPresentImpl implements PtzControlPresenter {

    public PtzControlPresentImpl() {

    }

    @Override
    public void zoom_in(final boolean isStart) {
        final int iPlay_id = All_id_Info.getInstance().getM_iPlayID();
        if (iPlay_id < 0)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isStart) {
                    HCNetSDK.getInstance().NET_DVR_PTZControl(iPlay_id, 12, 0);
                } else {
                    HCNetSDK.getInstance().NET_DVR_PTZControl(iPlay_id, 12, 1);
                }
            }
        }).start();
    }

    @Override
    public void zoom_out(final boolean isStart) {
        final int iPlay_id = All_id_Info.getInstance().getM_iPlayID();
        if (iPlay_id < 0)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isStart) {
                    HCNetSDK.getInstance().NET_DVR_PTZControl(iPlay_id, 11, 0);
                } else {
                    HCNetSDK.getInstance().NET_DVR_PTZControl(iPlay_id, 11, 1);
                }
            }
        }).start();
    }

    @Override
    public void focus_near(final boolean isStart) {
        final int iPlay_id = All_id_Info.getInstance().getM_iPlayID();
        if (iPlay_id < 0)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isStart) {
                    HCNetSDK.getInstance().NET_DVR_PTZControl(iPlay_id, 14, 0);
                } else {
                    HCNetSDK.getInstance().NET_DVR_PTZControl(iPlay_id, 14, 1);
                }
            }
        }).start();
    }

    @Override
    public void focus_far(final boolean isStart) {
        final int iPlay_id = All_id_Info.getInstance().getM_iPlayID();
        if (iPlay_id < 0)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isStart) {
                    HCNetSDK.getInstance().NET_DVR_PTZControl(iPlay_id, 13, 0);
                } else {
                    HCNetSDK.getInstance().NET_DVR_PTZControl(iPlay_id, 13, 1);
                }
            }
        }).start();

    }
}
