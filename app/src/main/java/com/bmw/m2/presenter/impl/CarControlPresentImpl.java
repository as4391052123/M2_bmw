package com.bmw.m2.presenter.impl;

import android.content.Intent;

import com.bmw.m2.Constant;
import com.bmw.m2.VLCApplication;
import com.bmw.m2.model.CarRemoteDeviecInfo;
import com.bmw.m2.presenter.CarControlPresenter;
import com.bmw.m2.utils.InternetUtil;
import com.bmw.m2.utils.UdpSocketUtil;
import com.bmw.m2.views.viewImpl.PreviewImpl;

import static com.bmw.m2.utils.ThreadUtil.sleep;
import static com.bmw.m2.utils.ThrowUtil.log;

/**
 * Created by admin on 2017/6/17.
 */

public class CarControlPresentImpl implements CarControlPresenter {

    private UdpSocketUtil udpSocketUtil;
    private CarRemoteDeviecInfo carRemoteDeviecInfo;
    private PreviewImpl preview;
    private Runnable sendTimerTask;
    private boolean isStopSendThread;
    private long sendThradTime = 120;
    private long currentGetTime;
    private Intent mBroadCastIntent;
    private Intent mIntentUpdateVersion;

    private void initIntentUpdateVersion(){
        mIntentUpdateVersion = new Intent();
        mIntentUpdateVersion.setAction(Constant.BROADCAST_DEVICE_UPDATAE_VERSION_SHOW);
    }

    public CarControlPresentImpl(PreviewImpl preview) {

        initIntentUpdateVersion();
        this.preview = preview;
        carRemoteDeviecInfo = new CarRemoteDeviecInfo();
        initSocket();
        initTimer();
        mBroadCastIntent = new Intent(Constant.BROADCAST_GET_INFO_FROM_FRAME_DEVICE);
    }


    public void release() {
        isStopSendThread = true;
        udpSocketUtil.release();
        udpSocketUtil = null;

    }

    private void initTimer() {
        sendTimerTask = new Runnable() {
            @Override
            public void run() {
                while (!isStopSendThread) {
                    if (udpSocketUtil != null)
                        udpSocketUtil.sendCommands(commands_car);
                    if (System.currentTimeMillis() - currentGetTime >= 15000)
                        preview.carResult(null);

//                log("send car--------"+Integer.toHexString(commands_car[shouxianPosition+2]&0xff));
//                for(int i=0;i<commands_car.length;i++){
//                    log("car send: commands["+i+"] = "+ (commands_car[i]&0x0ff));
//                }
                    sleep(sendThradTime);
                }
            }
        };

        new Thread(sendTimerTask).start();
    }

    private void initSocket() {
        udpSocketUtil = new UdpSocketUtil();
        udpSocketUtil.socketLogin("172.169.10.8", 20108, 48, 20109);
//        udpSocketUtil.socketLogin("192.168.155.1", 69, 48, 20107);
        udpSocketUtil.setOnCommandResultListener(new UdpSocketUtil.OnCommandResultListener() {
            @Override
            public void result(byte[] bytes) {
               /* log("get car--------");
                for(int i=0;i<bytes.length;i++){
                    log("\ncar : byte["+i+"] = "+Integer.toHexString(bytes[i]&0xff));
                }*/
                currentGetTime = System.currentTimeMillis();
                parseCarResultFromSocket(bytes);
            }
        });
    }


    private void parseCarResultFromSocket(byte[] bytes) {

/**
 计米器值： 分辨率 1mm 偏移量 0 错误值： 0xFE00~0xFEFF 不可用： 0xFF00~0xFFFF
 电 量 值： 分辨率 1% 偏移量 0 错误值： 254 不可用： 255
 */

        long jimiqi_value = 0;
        int jimiqi_value1 = bytes[jimiqiPosition + 3] & 0xff;
        int jimiqi_value2 = bytes[jimiqiPosition + 4] & 0xff;
        int jimiqi_value3 = bytes[jimiqiPosition + 5] & 0xff;
        int jimiqi_value4 = bytes[jimiqiPosition + 6] & 0xff;

        jimiqi_value = jimiqi_value1 | (jimiqi_value2 << 8) | (jimiqi_value3 << 16) | (jimiqi_value4 << 24);
        int battery = bytes[jimiqiPosition + 2] & 0xff;
//        if (jimiqi_value < 65024)
        carRemoteDeviecInfo.setJimiqi_value(jimiqi_value);
        if (battery < 254)
            carRemoteDeviecInfo.setBattery(battery);
/*

        */
/**
 温度值： 分辨率 1℃/bit 偏移量-40 错误值： 254 不可用： 255
 气压值： 分辨率 1PSI/bit 偏移量 0 错误值： 254 不可用： 255
 电压值： 分辨率 0.5V/bit 偏移量 0 错误值： 254 不可用： 255
 电流值： 分辨率 10mA/bit 偏移量 0 错误值： 254 不可用： 255
 */

        int car_temperature = bytes[carStatePosition + 1] & 0xff;
        int car_airPressure = bytes[carStatePosition + 2] & 0xff;
        int car_valtage = bytes[carStatePosition + 3] & 0xff;
        int car_Electricity = bytes[carStatePosition + 4] & 0xff;
        if (car_temperature < 254)
            carRemoteDeviecInfo.setCar_temperature(car_temperature - 40);
        if (car_airPressure < 254)
            carRemoteDeviecInfo.setCar_airPressure(car_airPressure);
        if (car_valtage < 254)
            carRemoteDeviecInfo.setCar_valtage(car_valtage / (float) 2);
        if (car_Electricity < 254)
            carRemoteDeviecInfo.setCar_Electricity(car_Electricity * 10);


        int dynamo_voltage = bytes[carDynamoStatePosition + 1] & 0xff;
        int dynamo_electricity = bytes[carDynamoStatePosition + 2] & 0xff;
        int dynamo_rotate_speed = bytes[carDynamoStatePosition + 3] & 0xff;
        int dynamo_temperature = bytes[carDynamoStatePosition + 4] & 0xff;
        int dynamo_alarm = bytes[carDynamoStatePosition + 5] & 0xff;

        float dynamo_voltage_float = dynamo_voltage / (float) 2;
        carRemoteDeviecInfo.setDynamo_voltage(dynamo_voltage_float);
        carRemoteDeviecInfo.setDynamo_electricity(dynamo_electricity * 10);
        carRemoteDeviecInfo.setDynamo_rotate_speed(dynamo_rotate_speed);
        carRemoteDeviecInfo.setDynamo_temperature(dynamo_temperature);
        carRemoteDeviecInfo.setDynamo_alarm(dynamo_alarm);


        /*
        设备类型： 00-平板（PC） ， 01-手机
        * */
        int remoteDevice = bytes[carRemoteDevicePosition + 2] & 0xff;
        carRemoteDeviecInfo.setRemoteDevice(remoteDevice);
        StringBuilder builder = new StringBuilder();
        int addr1 = bytes[carRemoteDevicePosition + 3] & 0xff;
        int addr2 = bytes[carRemoteDevicePosition + 4] & 0xff;
        int addr3 = bytes[carRemoteDevicePosition + 5] & 0xff;
        int addr4 = bytes[carRemoteDevicePosition + 6] & 0xff;
        builder.append(addr1).append(".").append(addr2).append(".").append(addr3).append(".").append(addr4);
        carRemoteDeviecInfo.setControlIp(builder.toString());

        if (builder.toString().equals(InternetUtil.getWifiIp(VLCApplication.context()))) {
            sendThradTime = 120;
        } else {
            sendThradTime = 2660;
        }
        /**
         对象： 0000-无， 0001-EthToCAN 模块， 0010-爬行器， 0011-云台， 0100-升降架， 0101-大灯，
         0110-线缆车
         模式： 0000-无更新操作， 0001-固件更新操作
         状态： 0000-正常工作模式， 0001-进入更新， 0010-更新完成
         版本号： 产品型号 设备类型 日期 标识
         */
        int updateValue1 = bytes[carUpdatePosition + 1] & 0xff;
        int updateObj = updateValue1 & 0x0f;
        int updateMode = updateValue1 >> 4;
        int updateState = bytes[carUpdatePosition + 2] & 0xff;
        int versionUpdate_low = bytes[carUpdatePosition + 3] & 0xff;
        int versionUpdate_high = bytes[carUpdatePosition + 4] & 0xff;
        int versionNow_low = bytes[carUpdatePosition + 5] & 0xff;
        int versionNow_high = bytes[carUpdatePosition + 6] & 0xff;
        int versionUpdate = getHexFromHighAndLow(versionUpdate_low, versionUpdate_high);
        int versionNow = getHexFromHighAndLow(versionNow_low, versionNow_high);
        carRemoteDeviecInfo.setUpdateObj(updateObj);
        carRemoteDeviecInfo.setUpdateState(updateState);
        carRemoteDeviecInfo.setUpdateMode(updateMode);
        carRemoteDeviecInfo.setVersionNow(versionNow);
        carRemoteDeviecInfo.setVersionUpdate(versionUpdate);

        mIntentUpdateVersion.putExtra(Constant.KEY_MYDEVICE_UPDATE_VERSION_CAR_SHOW,versionNow);
        VLCApplication.context().sendBroadcast(mIntentUpdateVersion);
        mIntentUpdateVersion.putExtra(Constant.KEY_MYDEVICE_UPDATE_VERSION_CAR_SHOW,-1);

        preview.carResult(carRemoteDeviecInfo);
        mBroadCastIntent.putExtra(Constant.KEY_RESULT_INFO_XIANLANCHE, carRemoteDeviecInfo);
        VLCApplication.context().sendBroadcast(mBroadCastIntent);

    }


    private int getHexFromHighAndLow(int low, int high) {
        return high << 8 | low;
    }


    private void operator(int position, int arg1, int arg2) {
        int arg = commands_car[position] & 0xff;
        commands_car[position] = (byte) (arg & arg1 | arg2);
//        socketUtil.sendCommands(commands_car);
    }

    @Override
    public void update(boolean ready) {
        if(ready){
            operator(carUpdatePosition+1,0xf0,0x06);
        }else {
            operator(carUpdatePosition+1,0x00,0x00);
        }

        log("updateModel carReady = " + ready + " command = " + Integer.toHexString(commands_car[carUpdatePosition + 1] & 0xff));
    }

    @Override
    public void updateModel(boolean isReady) {
        if(isReady){
            operator(carUpdatePosition+1,0x0f,0x10);
        }else {
            operator(carUpdatePosition+1,0x0f,0x00);
        }
        log("updateModel carReady = " + isReady + " command = " + Integer.toHexString(commands_car[carUpdatePosition + 1] & 0xff));
    }

    @Override
    public void car_lihe_on() {
        operator(shouxianPosition + 1, 0xf0, 0x01);
    }

    @Override
    public void car_lihe_off() {
        operator(shouxianPosition + 1, 0xf0, 0);
    }


    public void car_shouxian_speed(int num) {
        operator(shouxianPosition + 2, 0xf0, num);
    }

    @Override
    public void car_fangxian_liju(int num) {
        operator(shouxianPosition + 2, 0x0f, num << 4);
    }

    @Override
    public void car_shouxian_fang() {
        operator(shouxianPosition + 1, 0x0f, 0x20);
    }

    @Override
    public void car_shouxian_shou() {
        operator(shouxianPosition + 1, 0x0f, 0x10);

    }

    @Override
    public void car_shouxian_stop() {
        operator(shouxianPosition + 1, 0x0f, 0x00);
    }

    @Override
    public void car_jimi(int num) {
        operator(jimiqiPosition + 1, 0x00, num);
    }
}
