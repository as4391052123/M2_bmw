package com.bmw.m2.presenter.impl;

import android.text.TextUtils;

import com.bmw.m2.jna.SystemTransformByJNA;
import com.bmw.m2.jna.SystemTransformJNAInstance;
import com.bmw.m2.model.All_id_Info;
import com.bmw.m2.model.FileInfo;
import com.bmw.m2.model.Login_info;
import com.bmw.m2.presenter.VideoCutPresenter;
import com.bmw.m2.utils.FileUtil;
import com.bmw.m2.utils.HkUtils;
import com.bmw.m2.utils.TimeUtil;
import com.bmw.m2.utils.WriteInfoUtil;
import com.bmw.m2.views.viewImpl.PreviewImpl;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_TIME;

import org.MediaPlayer.PlayM4.Player;

import java.io.FileOutputStream;

import static com.bmw.m2.utils.ThrowUtil.error;
import static com.bmw.m2.utils.ThrowUtil.log;

/**
 * Created by admin on 2016/9/30.
 */
public class VideoCutPresentImpl implements VideoCutPresenter {

    private PreviewImpl preview;
    private boolean isRecord;
    private All_id_Info all_id_info;
    private String lastRecordPath;
    private SystemTransformByJNA.SYS_TRANS_PARA sys_trans_para;
    private boolean mIsAddKanban;

    public VideoCutPresentImpl(PreviewImpl preview) {
        this.preview = preview;
        all_id_info = All_id_Info.getInstance();
        FileUtil.pathIsExist();
        initStructure();
    }


    @Override
    public void recordOnHk(String name, String place, String startWell, String endWell, boolean isAddKanban) {
        int m_iPlayID = all_id_info.getM_iPlayID();
        if (!isRecord) {
            String desPath = getDesPath(name, place, startWell, endWell, true);


            if (!HCNetSDK.getInstance().NET_DVR_SaveRealData(m_iPlayID, desPath)) {
                log("海康：抓拍：开始录制失败：" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                if (preview != null) {
                    preview.record(0, false);
                }
                return;
            } else {
                log("海康：抓拍：开始录制成功！");
                if (preview != null) {
                    preview.record(0, true);
                }
            }
            isRecord = true;
        } else {
            if (!HCNetSDK.getInstance().NET_DVR_StopSaveRealData(m_iPlayID)) {
                log("海康：抓拍：结束录制失败！" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                if (preview != null) {

                    preview.record(1, false);
                }
            } else {
                log("海康：抓拍：结束录制成功！");
                if (preview != null) {
                    preview.record(1, true);
                }
            }
            isRecord = false;
        }
    }

    @Override
    public void record(String name, String place, String startWell, String endWell, boolean isAddKanban) {
        mIsAddKanban = isAddKanban;
        int m_iPlayID = all_id_info.getM_iPlayID();
        if (!isRecord) {
            /*if (!NetUtil.getInstance().isVideoConnect()) {
                if (preview != null) {
                    preview.record(0, false);
                }
                return;
            }*/

            if (isAddKanban) {
                //开始看板录制
                //...
                iniKanBanName();
//                Login_info.isAddKanban = true;
            } else {
                String desPath = getDesPath(name, place, startWell, endWell, true);
                startRecord(desPath);
            }

        } else {
            if (isAddKanban) {
                //结束看板录制
                //...
                WriteInfoUtil.getInstance().release();
//                Login_info.isAddKanban = false;
                endRecordBack(true);
            } else
                endRecord();
        }
    }

    private void iniKanBanName() {
        StringBuilder sb = new StringBuilder();

    }


    private void endRecord() {
//        Login_info.isPause = true;
        all_id_info.setRecordPause(true);
        int stop_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Stop(SystemTransformByJNA.handle.getValue());
        int release_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Release(SystemTransformByJNA.handle.getValue());
        if (stop_state != 0 || release_state != 0) {
            //结束录制失败
            endRecordBack(false);
        } else {
            endRecordBack(true);
        }
    }

    private void endRecordBack(boolean b) {
        if (preview != null) {
            preview.record(1, b);
        }
        isRecord = false;
    }


    private void startRecord(String desPath) {
        int create_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Create(SystemTransformByJNA.handle, sys_trans_para);
        if (create_state != 0) {
            startFailed();
            return;
        }

        int start_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Start(SystemTransformByJNA.handle.getValue(), null, desPath);
        if (start_state != 0) {
            startFailed();
            return;
        }
        //开始录制
//        Login_info.isPause = false;
        all_id_info.setRecordPause(false);
        if (preview != null) {
            preview.record(0, true);
        }
        isRecord = true;
    }

    private String getDesPath(String name, String place, String startWell, String endWell, boolean isRecord) {
        StringBuilder recordName = new StringBuilder();
        recordName.append(FileUtil.getFileSavePath());
        if (isRecord)
            recordName.append(FileInfo.videoPath);
        else
            recordName.append(FileInfo.picturePath);
        if (name != null && !TextUtils.isEmpty(name))
            recordName.append(name).append("_");
        if (place != null && !TextUtils.isEmpty(place))
            recordName.append(place).append("_");
        if (startWell != null && !TextUtils.isEmpty(startWell))
            recordName.append(startWell).append("_");
        if (endWell != null && !TextUtils.isEmpty(endWell))
            recordName.append(endWell).append("_");


        NET_DVR_TIME net_dvr_time = HkUtils.getTime();
        if (net_dvr_time != null) {
            recordName.append(net_dvr_time.dwYear)
                    .append(net_dvr_time.dwMonth < 10 ? 0 : "")
                    .append(net_dvr_time.dwMonth)
                    .append(net_dvr_time.dwDay < 10 ? 0 : "")
                    .append(net_dvr_time.dwDay)
                    .append(net_dvr_time.dwHour < 10 ? 0 : "")
                    .append(net_dvr_time.dwHour)
                    .append(net_dvr_time.dwMinute < 10 ? 0 : "")
                    .append(net_dvr_time.dwMinute)
                    .append(net_dvr_time.dwSecond < 10 ? 0 : "")
                    .append(net_dvr_time.dwSecond);
        } else {
            recordName.append(TimeUtil.getTimeNum());
        }
        lastRecordPath = recordName.toString();
        if (isRecord)
            recordName.append(".avi");
        else
            recordName.append(".jpg");


        return recordName.toString();
    }


    private void startFailed() {
        error("海康：抓拍：开始录制失败："+ HCNetSDK.getInstance().NET_DVR_GetLastError());
        if (preview != null) {
            preview.record(0, false);
        }
    }


//
//    public void reRecord(int count) {
//        int m_iPlayID = all_id_info.getM_iPlayID();
//        HCNetSDK.getInstance().NET_DVR_StopSaveRealData(m_iPlayID);
//        int clibrary = HCNetSDKJNAInstance.getInstance().NET_DVR_SaveRealData_V30(m_iPlayID,
//                2
//                , lastRecordPath + "_" + count + ".mp4");
//    }

    @Override
    public void capture(String name, String place, String startWell, String endWell) {

        int m_iPort = all_id_info.getM_iPort();
        if (m_iPort < 0) {
            error("海康：抓拍：截图失败，未登录！");
//            preview.iToast("截图失败！");
            return;
        }
        Player.MPInteger stWidth = new Player.MPInteger();
        Player.MPInteger stHeight = new Player.MPInteger();
        if (!Player.getInstance().getPictureSize(m_iPort, stWidth,
                stHeight)) {
            error("海康：抓拍：截图失败，getPictureSize failed with error code:"
                    + Player.getInstance().getLastError(m_iPort));
            return;
        }
        int nSize = 5 * stWidth.value * stHeight.value;
        final byte[] picBuf = new byte[nSize];
        final Player.MPInteger stSize = new Player.MPInteger();
        if (!Player.getInstance()
                .getJPEG(m_iPort, picBuf, nSize, stSize)) {
            error("海康：抓拍：截图失败，未登录！getBMP failed with error code:"
                    + Player.getInstance().getLastError(m_iPort));
            return;
        }


        final String path = getDesPath(name, place, startWell, endWell, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream file = new FileOutputStream(path);
                    file.write(picBuf, 0, stSize.value);
                    file.close();
                    preview.capture(path);
//                    preview.iToast("截图已保存");
                } catch (Exception err) {
                    error("海康：抓拍：截图失败:"+ err.toString());
                }
            }
        }).start();


    }

    @Override
    public void release() {
        SystemTransformJNAInstance.getInstance().SYSTRANS_Stop(SystemTransformByJNA.handle.getValue());
        SystemTransformJNAInstance.getInstance().SYSTRANS_Release(SystemTransformByJNA.handle.getValue());
        if (isRecord) {
            log("海康：抓拍：视图退出，停止录像");
//            preview = null;
            record(null, null, null, null, mIsAddKanban);
        }
    }


    public void initStructure() {

        sys_trans_para = new SystemTransformByJNA.SYS_TRANS_PARA();
        sys_trans_para.dwSrcInfoLen = 40;
        sys_trans_para.dwTgtPackSize = 8 * 1024;
        sys_trans_para.dwSrcDemuxSize = 0;
        sys_trans_para.enTgtType = 0x07;

        SystemTransformByJNA.PLAYSDK_MEDIA_INFO.ByReference media_info = SystemTransformByJNA.media_info;

        media_info.media_fourcc = 0x484B4D49;
        media_info.media_version = 0x0101;
        media_info.device_id = 0;
        media_info.audio_bits_per_sample = 16;                 //16bit
        media_info.audio_bitrate = 8000;
        media_info.system_format = 4;
        media_info.video_format = 0x0100;
        media_info.audio_format = 0x1000;
        media_info.audio_channels = 0;      //single channel
        media_info.audio_samplesrate = 0;
        media_info.write();
        sys_trans_para.pSrcInfo = media_info;
        sys_trans_para.write();


    }


}
