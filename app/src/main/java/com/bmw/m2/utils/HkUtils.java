package com.bmw.m2.utils;

import com.bmw.m2.VLCApplication;
import com.bmw.m2.model.All_id_Info;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_TIME;

import static com.bmw.m2.utils.ThrowUtil.error;

/**
 * Created by admin on 2017/9/4.
 */

public class HkUtils {


    public static NET_DVR_TIME getTime() {
        if(All_id_Info.getInstance().getM_iLogID()<0)
            return null;

        NET_DVR_TIME net_dvr_time = new NET_DVR_TIME();
        HCNetSDK.getInstance().NET_DVR_GetDVRConfig(All_id_Info.getInstance().getM_iLogID(),
                HCNetSDK.getInstance().NET_DVR_GET_TIMECFG,
                All_id_Info.getInstance().getM_iChanNum(),
                net_dvr_time);
        return net_dvr_time;
    }


    public static boolean setTime(int year, int month, int day, int hour, int min, int second) {
        if(All_id_Info.getInstance().getM_iLogID()<0) {
            VLCApplication.toast("未连接主机！");
            return false;
        }

        NET_DVR_TIME net_dvr_time = new NET_DVR_TIME();
        net_dvr_time.dwYear = year;
        net_dvr_time.dwMonth = month;
        net_dvr_time.dwDay = day;
        net_dvr_time.dwHour = hour;
        net_dvr_time.dwMinute = min;
        net_dvr_time.dwSecond = second;
        boolean isSuccess = HCNetSDK.getInstance().NET_DVR_SetDVRConfig(All_id_Info.getInstance().getM_iLogID(),
                HCNetSDK.getInstance().NET_DVR_SET_TIMECFG,
                All_id_Info.getInstance().getM_iChanNum(),
                net_dvr_time);
        if(!isSuccess){
            error("海康时间设置出错，e = "+ HCNetSDK.getInstance().NET_DVR_GetLastError());
            VLCApplication.toast("主机时间设置失败！");
            return isSuccess;
        }

        VLCApplication.toast("主机时间设置成功！");

        return isSuccess;
    }
}
