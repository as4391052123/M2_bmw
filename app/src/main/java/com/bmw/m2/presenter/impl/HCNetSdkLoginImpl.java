package com.bmw.m2.presenter.impl;

import android.content.Context;
import android.view.SurfaceView;
import android.widget.Toast;

import com.bmw.m2.model.All_id_Info;
import com.bmw.m2.model.Login_info;
import com.bmw.m2.presenter.HCNetSdkLogin;
import com.bmw.m2.presenter.PreviewPresenter;
import com.bmw.m2.presenter.VideoCutPresenter1;
import com.bmw.m2.views.viewImpl.PreviewImpl;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.bmw.m2.utils.ThrowUtil.error;
import static com.bmw.m2.utils.ThrowUtil.log;

/**
 * Created by admin on 2016/9/28.
 */
public class HCNetSdkLoginImpl implements HCNetSdkLogin {

    private final Toast mToast;
    private All_id_Info all_id_info;
    private PreviewPresenter previewPresenter;
    private boolean isStop;
    private ExecutorService cachedThreadPool;
    private boolean isRecord;
    private boolean mCanReConnect;
    private VideoCutPresenter1 videoCutPresenter;
    private PreviewImpl preview;

    public HCNetSdkLoginImpl(Context context, SurfaceView surfaceView, PreviewImpl preview) {
        mCanReConnect = true;
        initSDK();
        all_id_info = All_id_Info.getInstance();
        cachedThreadPool = Executors.newCachedThreadPool();
        mToast = Toast.makeText(context, "连接失败！请检查wifi是否连接！", Toast.LENGTH_SHORT);
        previewPresenter = new PreviewPresentImpl(context, surfaceView, new PreviewPresentImpl.OnPlayFailedListener() {
            @Override
            public void playFailed() {
                if (mCanReConnect)
                    reLoginHaiKang();
            }
        });
//        previewPresenter = new PreviewPresentImpl(context,surfaceView,null);
        videoCutPresenter = new VideoCutPresentImpl1(preview);
        this.preview = preview;
//        previewPresenter.surfaceAddCallback();

    }

    public void reLoginHaiKang() {

        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (All_id_Info.getInstance().getM_iLogID() >= 0) {
                    logout();
                    sleep(3000);
                }
                connectDeviceOperator();
            }
        });
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initSDK() {
        // init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            error("海康：HCNetSDK init is failed!");
//            viewImpl.initHCNetSdkFaild();
            return;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/",
                true);
        log("海康：HCNetSDK init is success!");
    }


    public boolean login() {
        int m_iLogID = loginNormalDevice();
        if (m_iLogID < 0) {
//            viewImpl.iToast("连接失败！请检查wifi是否连接！");
//            mToast.show();
            return false;
        }
        mToast.cancel();
        log("海康：登录成功！");
        all_id_info.setM_iLogID(m_iLogID);
        if (preview != null)
            preview.loginSuccessful();


        return true;

    }

    @Override
    public void logout() {
        videoCutPresenter.release();
        previewPresenter.stopSingle();
        if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(all_id_info.getM_iLogID())) {
            error("海康： 退出登录失败!");
            return;
        }
        log("海康：退出登录成功!");
        all_id_info.setM_iLogID(-1);
        all_id_info.resetData();
    }

    @Override
    public void connectDevice() {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                connectDeviceOperator();
            }
        });

    }

    private void connectDeviceOperator() {
        while (All_id_Info.getInstance().getM_iLogID() < 0 && !isStop && mCanReConnect) {
            synchronized (this) {
                if (All_id_Info.getInstance().getM_iLogID() >= 0)
                    break;
                if (login()) {
                    previewPresenter.startSingle();
                }

                try {
                    Thread.sleep(1000 * 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        log("海康：预览完成！");
    }

    @Override
    public void release() {
        // release net SDK resource
        if (cachedThreadPool != null)
            cachedThreadPool.shutdownNow();
        mToast.cancel();
        isStop = true;
        logout();
        log("海康：release");
        HCNetSDK.getInstance().NET_DVR_Cleanup();
    }

    @Override
    public void record() {
        videoCutPresenter.record();
    }

    @Override
    public void capture() {
        videoCutPresenter.capture();
    }

    @Override
    public void stopRecord() {
        videoCutPresenter.release();
    }

    @Override
    public void setCanReConnect(boolean canReConnect) {
        mCanReConnect = canReConnect;
        if (!isStop)
            if (canReConnect) {
                connectDevice();
                previewPresenter.surfaceAddCallback();
            } else {
                logout();
                previewPresenter.removeSurfaceCallback();
            }
    }

    private int loginNormalDevice() {

        NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30) {
            error("海康：HKNetDvrDeviceInfoV30对象创建失败!");
            return -1;
        }

        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(Login_info.getInstance().getHk_ip(), Login_info.getInstance().getHk_port(),
                Login_info.getInstance().getHk_account(), Login_info.getInstance().getHk_password(), m_oNetDvrDeviceInfoV30);
        if (iLogID < 0) {
            error("海康：登录失败!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError() + " " + iLogID);
            return -1;
        }

        all_id_info.setM_oNetDvrDeviceInfoV30(m_oNetDvrDeviceInfoV30);
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            all_id_info.setM_iStartChan(m_oNetDvrDeviceInfoV30.byStartChan);
            all_id_info.setM_iChanNum(m_oNetDvrDeviceInfoV30.byChanNum);
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            all_id_info.setM_iStartChan(m_oNetDvrDeviceInfoV30.byStartDChan);
            all_id_info.setM_iChanNum(m_oNetDvrDeviceInfoV30.byIPChanNum
                    + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256);
        }

//        if(HCNetSDK.getInstance().NET_DVR_MakeKeyFrameSub(iLogID,all_id_info.getM_iChanNum())){
//            viewImpl.ilog("子码流动态产生一个关键帧!");
//        }

        return iLogID;
    }
}
