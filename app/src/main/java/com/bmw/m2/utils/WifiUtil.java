package com.bmw.m2.utils;

import android.util.Log;

import java.io.IOException;

import static com.bmw.m2.utils.InternetUtil.pingHost;

/**
 * Created by admin on 2017/6/30.
 */

public class WifiUtil {

    private static String TAG = "MyIntentService";

    public static boolean isWifiConnect(WifiAdmin wifiAdmin, String ssid, String testIp) {
        if (!wifiAdmin.getSSID().contains(ssid)) {
            wifiAdmin.removeConfig(ssid);
            return false;
        }
        if (wifiAdmin.getIPAddress() == 0) {
            return false;
        }

        if(testIp == null)
            return true;

        if (pingHost(testIp) == 0) {
            return true;
        }
        return false;
    }


}
