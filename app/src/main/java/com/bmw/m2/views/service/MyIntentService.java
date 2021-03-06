package com.bmw.m2.views.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.util.Log;

import com.bmw.m2.utils.WifiAdmin;
import com.bmw.m2.utils.WifiUtil;

import java.util.List;


public class MyIntentService extends IntentService {
    private String TAG = "MyIntentService";

    private static final String ACTION_WIFI = "example.bmw.wifiservice.MyIntentService.wifiConnect";
    private static final String PARAM_WIFI_SSID = "example.bmw.wifiservice.MyIntentService.wifiConnect_params_ssid";
    private static final String PARAM_WIFI_PADDWORD = "example.bmw.wifiservice.MyIntentService.wifiConnect_params_password";
    private static final String PARAM_WIFI_TESTIP = "example.bmw.wifiservice.MyIntentService.wifiConnect_params_testIp";

    private int test;
    private boolean isFinish;


    public MyIntentService() {
        super("MyIntentService");
    }

    public static void startActionWifiConnect(Context context, String ssid, String password, String ip) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_WIFI);
        intent.putExtra(PARAM_WIFI_SSID, ssid);
        intent.putExtra(PARAM_WIFI_PADDWORD, password);
        intent.putExtra(PARAM_WIFI_TESTIP, ip);
        context.startService(intent);
    }

    public static void stopIntentService(Context context) {
        Intent intent = new Intent(context, MyIntentService.class);
        context.stopService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION_WIFI)) {
                String ssid = intent.getStringExtra(PARAM_WIFI_SSID);
                String password = intent.getStringExtra(PARAM_WIFI_PADDWORD);
                String ip = intent.getStringExtra(PARAM_WIFI_TESTIP);
                WifiAdmin wifiAdmin = new WifiAdmin(this);
                if (!isWifiAlreadyConnect(wifiAdmin, ssid, ip)) {
                    connectWifi(wifiAdmin, ssid, password, ip);
                }
            }
        }
    }

    private void connectWifi(WifiAdmin wifiAdmin, String ssid, String password, String ip) {
        wifiAdmin.openWifi();
        List<ScanResult> list = null;
        Log.d(TAG, "connectWifi:ssid = " + ssid + " password = " + password);
        while (!WifiUtil.isWifiConnect(wifiAdmin, ssid, ip) && !isFinish) {
            Log.d(TAG, "connectWifi: wifi is Scanning!!!  " + ssid);

            wifiAdmin.disconnectWifi(wifiAdmin.getNetworkId());
            wifiAdmin.startScan();
            list = wifiAdmin.getWifiList();
            Log.d(TAG, "connectWifi: just scanREsult!" + (list == null ? list : list.size()));

            if (list != null && list.size() > 0) {
                for (ScanResult scanResult : list) {
                    if (scanResult.SSID.equals(ssid)) {
                        Log.d(TAG, "connectWifi: find ssid :" + ssid);
                        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, password, 3));
                        sleep(10000);
                        Log.d(TAG, "connectWifi: wifi is connected!!!  " + ssid);
                        break;
                    }
                }
            }else{

//                wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, password, 3));
//                sleep(10000);
            }
            sleep(1000);
        }
        if (isFinish) {
            Log.d(TAG, "connectWifi: wifiThread is finish!!!");
        } else
            Log.d(TAG, "connectWifi: wifi already connected!!!");
    }


    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isWifiAlreadyConnect(WifiAdmin wifiAdmin, String ssid, String ip) {
        return WifiUtil.isWifiConnect(wifiAdmin, ssid, ip);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        test = 100;
        Log.d(TAG, "onCreate: " + test);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart: " + test);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFinish = true;
        Log.d(TAG, "onDestroy: " + test);
    }
}
