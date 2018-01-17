package com.bmw.m2.utils;

import com.bmw.m2.jna.SystemTransformByJNA;
import com.bmw.m2.jna.SystemTransformJNAInstance;
import com.bmw.m2.model.FileInfo;
import com.hikvision.netsdk.HCNetSDK;
import com.lidroid.xutils.util.LogUtils;
import com.sun.jna.CallbackReference;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bmw.m2.utils.ThrowUtil.error;
import static com.bmw.m2.utils.ThrowUtil.log;

/**
 * Created by admin on 2017/9/21.
 */

public class FileTurnStreamTest {


    public IntByReference handle = new IntByReference();

    public IntByReference file_handle1 = new IntByReference();


    public FileTurnStreamTest() {
//        recordStart();
    }

    public void parseFile(String path, String desPath) {
        parseFileStart(path, desPath);
    }

    private void parseFileStart(String path, String desPath) {
        log("start file parse");
        SystemTransformByJNA.SYS_TRANS_PARA sys_trans_para = initTransformParas(0x04, 7, 0x0100);
        int create_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Create(file_handle1, sys_trans_para);
        if (create_state != 0) {
            error("SYSTRANS_Create error！" + Integer.toHexString(create_state));
            return;
        }

//         SystemTransformByJNA.OutputDataCallBack outputDataCallBack = new OutPutDataCallBack();
        Pointer p = Pointer.NULL;

       int callback_state = SystemTransformJNAInstance.getInstance().SYSTRANS_RegisterOutputDataCallBack(file_handle1.getValue(), new OutPutDataCallBack(), p);
        if (callback_state != 0) {
            error("SYSTRANS_RegisterOutputDataCallBack error！" + Integer.toHexString(callback_state));
            return;
        }

        int start_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Start(file_handle1.getValue(), path, null);
        if (start_state != 0) {
            error("SYSTRANS_Start error " + Integer.toHexString(start_state));
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isGo = true;
                while (isGo) {
                    IntByReference percent = getFileParsePercent(file_handle1.getValue());

//                    log("file parse percent"+(percent==null?null:percent.getValue()));
                    if (percent.getValue() == 100) {
                        log("file parse finish！");
                        stop(file_handle1.getValue());
                        stop(handle.getValue());
                        break;
                    }
                }
            }
        }).start();


    }


    private IntByReference getFileParsePercent(int value) {
        IntByReference pc = new IntByReference();
        int percent = -1;
        int percent_state = SystemTransformJNAInstance.getInstance().SYSTRANS_GetTransPercent(value, pc);
        if (percent_state != 0) {
            error("getFileParsePercent error " + Integer.toHexString(percent_state));
            return null;
        }
        return pc;
    }


    public static class OutPutDataCallBack implements SystemTransformByJNA.OutputDataCallBack {
        @Override
        public void invoke(SystemTransformByJNA.OUTPUTDATA_INFO outputdata_info, Pointer pUser) {
            log("ouputdata is == "+outputdata_info);
        }

        /*@Override
        public void callback(SystemTransformByJNA._DETAIL_DATA_INFO_ outputData_info, Pointer pUser) {
            log("ouputdata is == "+outputData_info);
        }*/
    }


    private void recordStart() {
        StringBuilder recordName = new StringBuilder();
        recordName.
                append(FileUtil.getSDPath()).
                append(FileInfo.videoPath).
                append("iamnewfile").
                append(".avi");


        String desPath = recordName.toString();


        SystemTransformByJNA.SYS_TRANS_PARA sys_trans_para = initTransformParas(0x07, 4, 0x0100);

        int create_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Create(handle, sys_trans_para);
        if (create_state != 0) {
            error("record SYSTRANS_Create error " + Integer.toHexString(create_state));
            return;
        }

        int start_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Start(handle.getValue(), null, desPath);
        if (start_state != 0) {
            error("record SYSTRANS_Start error " + Integer.toHexString(start_state));
            return;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder recordName = new StringBuilder();
                recordName.
                        append(FileUtil.getSDPath()).
                        append(FileInfo.m2Path).
                        append("裸码流");
                StringBuilder recordName_index = new StringBuilder();
                recordName_index.
                        append(FileUtil.getSDPath()).
                        append(FileInfo.m2Path).
                        append("裸码流_index");
                File file_index = new File(recordName_index.toString());

                File file = new File(recordName.toString());
                try {
                    FileInputStream inputStream = new FileInputStream(file);
                    BufferedInputStream bin = new BufferedInputStream(inputStream);

                    FileReader read_index = new FileReader(file_index);
                    BufferedReader bReader = new BufferedReader(read_index);

                    List<Integer> list = null;
//                    list = FileUtil.list;

                    list = new ArrayList<Integer>();
                    String str_index = null;
                    while ((str_index = bReader.readLine()) != null) {
                        list.add(Integer.valueOf(str_index));
                    }

                    for (int i = 0; i < list.size(); i++) {
                        int a = list.get(i);
                        byte[] bytes = new byte[a];
                        bin.read(bytes);
                        int state = SystemTransformJNAInstance.
                                getInstance().
                                SYSTRANS_InputData(
                                        handle.getValue(),
                                        0,
                                        bytes,
                                        bytes.length
                                );
                        log("inputdata : " + Integer.toHexString(state) + "  " + bytes.length);
                    }

                    stop(handle.getValue());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public SystemTransformByJNA.SYS_TRANS_PARA initTransformParas(int enTgtType, int system_format, int video_format) {

        SystemTransformByJNA.SYS_TRANS_PARA sys_trans_para = new SystemTransformByJNA.SYS_TRANS_PARA();
        sys_trans_para.dwSrcInfoLen = 40;
        sys_trans_para.dwTgtPackSize = 8 * 1024;
        sys_trans_para.dwSrcDemuxSize = 0;
        sys_trans_para.enTgtType = enTgtType;

        SystemTransformByJNA.PLAYSDK_MEDIA_INFO.ByReference media_info = SystemTransformByJNA.media_info;

        media_info.media_fourcc = 0x484B4D49;
        media_info.media_version = 0x0101;
        media_info.device_id = 0;
        media_info.audio_bits_per_sample = 16;                 //16bit
        media_info.audio_bitrate = 8000;
        //封装对应值(10进制):0-raw, 1-hik, 2-ps, 3-ts, 4-rtp, 5-mp4, 6-asf, 7-avi, 8-GB_ps, 9-hls_ts, 10-flv, 11-mp4_front, 13-rtmp
        media_info.system_format = (short) system_format;
        //视频编码类型: 0x0001-hik264, 0x0002-mpeg2, 0x0003-mpeg4, 0x0004-mjpeg, 0x0005-h265, 0x0006-svac, 0x0100-h264
        media_info.video_format = (short) video_format;
        media_info.audio_format = 0x1000;
        media_info.audio_channels = 0;      //single channel
        media_info.audio_samplesrate = 0;
        media_info.write();
        sys_trans_para.pSrcInfo = media_info;
        sys_trans_para.write();

        return sys_trans_para;
    }


    public void stop(int handle_value) {
        int stop_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Stop(handle_value);
        int release_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Release(handle_value);
        if (stop_state != 0 || release_state != 0) {
            error("stop parse error！" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
    }
}
