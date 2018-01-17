package com.bmw.m2.presenter.impl;

import android.content.Intent;

import com.bmw.m2.Constant;
import com.bmw.m2.VLCApplication;
import com.bmw.m2.model.CarRemoteDeviecInfo;
import com.bmw.m2.model.RemoteDeviceInfo;
import com.bmw.m2.presenter.ControlPresenter;
import com.bmw.m2.utils.InternetUtil;
import com.bmw.m2.utils.NumberUtil;
import com.bmw.m2.utils.UdpSocketUtil;
import com.bmw.m2.views.viewImpl.PreviewImpl;
import com.lidroid.xutils.util.LogUtils;

import static com.bmw.m2.utils.NumberUtil.getDecimalDit;
import static com.bmw.m2.utils.ThreadUtil.sleep;
import static com.bmw.m2.utils.ThrowUtil.log;
import static com.bmw.m2.utils.ThrowUtil.throwException;

/**
 * Created by admin on 2017/5/16.
 */

public class ControlPresentImpl implements ControlPresenter {

    private UdpSocketUtil socketUtil;
    private Runnable sendTimerTask;
    private RemoteDeviceInfo remoteDeviceInfo;
    private CarRemoteDeviecInfo carRemoteDeviecInfo;
    private PreviewImpl previewImpl;
    private boolean isFirstChangeSendTime;
    private boolean isStopSendThread;
    private long sendSleepTime = 100;
    private long currentGetTime;
    private Intent mBroadCastIntent;
    private boolean canSendStop;
    private Intent mIntentUpdateVersion;

    private void initIntentUpdateVersion(){
        mIntentUpdateVersion = new Intent();
        mIntentUpdateVersion.setAction(Constant.BROADCAST_DEVICE_UPDATAE_VERSION_SHOW);
    }

    private void carMove() {

    }

    private void carStop() {

    }


    @Override
    public void updateObject(int object) {
        operator(updatePosition + 1, 0xf0, object);

//        operator(updatePosition + 1, 0x0f, 0x10);
        log("updateModel object = " + object + " command = " + Integer.toHexString(commands[updatePosition + 1] & 0xff));
    }

    @Override
    public void updateModel(boolean ready) {
        if (ready) {
            operator(updatePosition + 1, 0x0f, 0x10);
        } else {
            operator(updatePosition + 1, 0x0f, 0x00);
        }

        log("updateModel = " + ready + " command = " + Integer.toHexString(commands[updatePosition + 1] & 0xff));
    }

    public void changeIp(String ip, int port) {
        socketUtil.changeIp(ip, port);
    }

    public ControlPresentImpl(PreviewImpl previewImpl) {
        initIntentUpdateVersion();
        this.previewImpl = previewImpl;
        initSocket();
        initTimer();
        remoteDeviceInfo = new RemoteDeviceInfo();
        carRemoteDeviecInfo = new CarRemoteDeviecInfo();
        mBroadCastIntent = new Intent(Constant.BROADCAST_GET_INFO_FROM_FRAME_DEVICE);
    }

    public void release() {
        isStopSendThread = true;
        socketUtil.release();
        socketUtil = null;

    }

    private void initTimer() {

        sendTimerTask = new Runnable() {
            @Override
            public void run() {

                while (!isStopSendThread) {
//                log("control liftControl "+ Integer.toHexString(commands[liftMovePosition +1]&0xff)
//                +" light front: "+Integer.toHexString(commands[lfPosition+1]&0xff)
//                +" lazer: "+Integer.toBinaryString(commands[ptzPosition+2]&0xff));
                    socketUtil.sendCommands(commands);
                    if (System.currentTimeMillis() - currentGetTime >= 15000)
                        previewImpl.result(null);
//                for(int i=0;i<commands.length;i++){
//                    log("car send: commands["+i+"] = "+ (commands[i]&0x0ff));
//                }
//                    log("byte [28] = "+Integer.toHexString(commands[28]&0xff));
                    sleep(sendSleepTime);
                }
            }
        };

        new Thread(sendTimerTask).start();

    }

    private void initSocket() {
        socketUtil = new UdpSocketUtil();
        socketUtil.socketLogin("172.169.10.7", 20108, 104, 20108);
        listenSocketResult();
    }

    private void listenSocketResult() {
        socketUtil.setOnCommandResultListener(new UdpSocketUtil.OnCommandResultListener() {
            @Override
            public void result(final byte[] bytes) {
                currentGetTime = System.currentTimeMillis();
//                log("get control**********");
//                for(int i=0;i<bytes.length;i++){
//                    log("\npaxingqi: byte["+i+"] = "+Integer.toHexString(bytes[i]&0xff));
//                }
                parseResultFromSocket(bytes);


            }
        });

    }


    private void parseResultFromSocket(byte[] bytes) {

       /* int ptzArgs = bytes[ptzPosition + 1];
        int ptzSetState = ptzArgs & 0xff;
        ptzSetState = ptzSetState >> 4;
        if (ptzSetState != 0) {
            isPtzSet = true;
        }

        if (isPtzSet && ptzSetState == 0) {
            operator(ptzPosition + 1, 0x0f, 0x00);
            isPtzSet = false;
        }*/


        /**
         云台置位状态： 0000-无， 0001-环绕一周， 0010-置上， 0011-置左， 0100-置右
         */
        int ptz_direction_state = bytes[ptzPosition + 1] & 0xff;
        ptz_direction_state = ptz_direction_state >> 4;
        remoteDeviceInfo.setPtz_direction_state(ptz_direction_state);

        /**
         高度值： 分辨率 1mm/bit 偏移量 0 错误值： 254 不可用： 255
         角度值： 分辨率 0.5deg/bit 偏移量 0 错误值： 254 不可用： 255
         */
        int lift_height = bytes[liftHeightPosition + 1] & 0xff;
        int lift_value2 = bytes[liftHeightPosition + 2] & 0xff;
        if (lift_value2 != 254 && lift_value2 != 255) {
            float lift_angle = lift_value2 / 2;
            lift_angle = NumberUtil.getDecimalDit(lift_angle, 10);
            remoteDeviceInfo.setLift_angle(lift_angle);
        }
        if (lift_height != 254 && lift_height != 255)
            remoteDeviceInfo.setLift_height(lift_height);


        /**
         倾角/横滚值： 分辨率(1/128)deg/bit 偏移量-200 错误值： 0xFE00~0xFEFF  65024~65279 ,不可用： 0xFF00~0xFFFF  65280~65535
         */
        float poseSlant = 0;
        float poseRoll = 0;
        int slan_low = bytes[posePosition + 1] & 0xff;
        int slan_high = bytes[posePosition + 2] & 0xff;
        int roll_low = bytes[posePosition + 3] & 0xff;
        int roll_high = bytes[posePosition + 4] & 0xff;
        poseSlant = getHexFromHighAndLow(slan_low, slan_high);
        poseRoll = getHexFromHighAndLow(roll_low, roll_high);
        if (poseSlant < 65024) {
            poseSlant = poseSlant / (float) 128 - 200;
            poseSlant = NumberUtil.getDecimalDit(poseSlant, 10);
            remoteDeviceInfo.setPoseSlant(poseSlant);
        }
        if (poseRoll < 65024) {
            poseRoll = poseRoll / (float) 128 - 200;
            poseRoll = NumberUtil.getDecimalDit(poseRoll, 10);
            remoteDeviceInfo.setPoseRoll(poseRoll);
        }

        /**
         轴向/径向角： 分辨率(1/128)deg/bit 偏移量 0 错误值： 0xFE00~0xFEFF 不可用： 0xFF00~0xFFFF
         */
        float ptz_axial_angle = 0;
        float ptz_radial_angle = 0;
        int axial_low = bytes[ptzAnglePosition + 1] & 0xff;
        int axial_high = bytes[ptzAnglePosition + 2] & 0xff;
        int radial_low = bytes[ptzAnglePosition + 3] & 0xff;
        int radial_high = bytes[ptzAnglePosition + 4] & 0xff;
        ptz_axial_angle = getHexFromHighAndLow(axial_low, axial_high);
        ptz_radial_angle = getHexFromHighAndLow(radial_low, radial_high);
        if (ptz_axial_angle < 65024) {
            ptz_axial_angle = ptz_axial_angle / (float) 128;
            ptz_axial_angle = NumberUtil.getDecimalDit(ptz_axial_angle, 10);
            remoteDeviceInfo.setPtz_axial_angle(ptz_axial_angle);
        }
        if (ptz_radial_angle < 65024) {
            ptz_radial_angle = ptz_radial_angle / (float) 128;
            ptz_radial_angle = NumberUtil.getDecimalDit(ptz_radial_angle, 10);
            remoteDeviceInfo.setPtz_radial_angle(ptz_radial_angle);
        }

        /**
         温度值： 分辨率 1℃/bit 偏移量-40 错误值： 254 不可用： 255
         电流值： 分辨率 100mA/bit 偏移量 0 错误值： 254 不可用： 255
         转速值： 分辨率 1rps/bit 偏移量 0 错误值： 254 不可用： 255
         */
        int dynamo_temperature1 = bytes[dynamoStatePosition + 1] & 0xff;
        int dynamo_temperature2 = bytes[dynamoStatePosition + 2] & 0xff;
        int dynamo_electricity1 = bytes[dynamoStatePosition + 5] & 0xff;
        int dynamo_electricity2 = bytes[dynamoStatePosition + 6] & 0xff;
        int dynamo_rotate_speed1 = bytes[dynamoStatePosition + 3] & 0xff;
        int dynamo_rotate_speed2 = bytes[dynamoStatePosition + 4] & 0xff;
        if (dynamo_temperature1 < 254)
            remoteDeviceInfo.setDynamo_temperature1(dynamo_temperature1 - 40);
        if (dynamo_temperature2 < 254)
            remoteDeviceInfo.setDynamo_temperature2(dynamo_temperature2 - 40);
        if (dynamo_electricity1 < 254) {
            remoteDeviceInfo.setDynamo_electricity1(dynamo_electricity1 * 10);
        }
        if (dynamo_electricity2 < 254) {
            remoteDeviceInfo.setDynamo_electricity2(dynamo_electricity2 * 10);
        }
        if (dynamo_rotate_speed1 < 254)
            remoteDeviceInfo.setDynamo_rotate_speed1(dynamo_rotate_speed1 * 120);
        if (dynamo_rotate_speed2 < 254)
            remoteDeviceInfo.setDynamo_rotate_speed2(dynamo_rotate_speed2 * 120);

//        log("rotate_speed = "+Integer.toHexString(dynamo_rotate_speed1));

        /*
        状态： 00-离线， 01-在线
        温度值： 分辨率 1℃/bit 偏移量-40 错误值： 254 不可用： 255
        气压值： 分辨率 1PSI/bit 偏移量 0 错误值： 254 不可用： 255
        电压值： 分辨率 0.5V/bit 偏移量 0 错误值： 254 不可用： 255
        电流值： 分辨率 10mA/bit 偏移量 0 错误值： 254 不可用： 255
        * */
        int machine_temperature = bytes[machineStatePosition + 1] & 0xff;
        int machine_airPressure = bytes[machineStatePosition + 2] & 0xff;
        float machine_voltage = bytes[machineStatePosition + 3] & 0xff;
        int machine_electricity = bytes[machineStatePosition + 4] & 0xff;
        int machine_state = bytes[machineStatePosition + 5] & 0xff;
        int machine_protect = bytes[machineStatePosition + 6] & 0xff;
        if (machine_temperature < 254)
            remoteDeviceInfo.setMachine_temperature(machine_temperature - 40);
        if (machine_airPressure < 254)
            remoteDeviceInfo.setMachine_airPressure(machine_airPressure);
        if (machine_voltage < 254) {
            remoteDeviceInfo.setMachine_voltage(NumberUtil.getDecimalDit(machine_voltage / (float) 2, 10));
        }
        if (machine_electricity < 254) {
            remoteDeviceInfo.setMachine_electricity(machine_electricity * 10);
        }
        remoteDeviceInfo.setMachine_state(machine_state);
        remoteDeviceInfo.setMachine_protect(machine_protect);
//        log("电机保护："+machine_protect);

        /**
         状态： 00-离线， 01-在线
         温度值： 分辨率 1℃/bit 偏移量-40 错误值： 254 不可用： 255
         气压值： 分辨率 1PSI/bit 偏移量 0 错误值： 254 不可用： 255
         电流值： 分辨率 10mA/bit 偏移量 0 错误值： 254 不可用： 255
         */
        int lift_temperature = bytes[liftStatePosition + 1] & 0xff;
        int lift_airPressure = bytes[liftStatePosition + 2] & 0xff;
        int lift_workElectricity = bytes[liftStatePosition + 3] & 0xff;
        int lift_machineElectricity = bytes[liftStatePosition + 4] & 0xff;
        int lift_state = bytes[liftStatePosition + 5] & 0xff;
        if (lift_temperature < 254)
            remoteDeviceInfo.setLift_temperature(lift_temperature - 40);
        if (lift_airPressure < 254)
            remoteDeviceInfo.setLift_airPressure(lift_airPressure);
        if (lift_workElectricity < 254) {
            remoteDeviceInfo.setLift_workElectricity(lift_workElectricity * 10);
        }
        if (lift_machineElectricity < 254) {
            remoteDeviceInfo.setLift_machineElectricity(lift_machineElectricity * 10);
        }
        remoteDeviceInfo.setLift_state(lift_state);


        /**
         状态： 00-离线， 01-在线
         温度值： 分辨率 1℃/bit 偏移量-40 错误值： 254 不可用： 255
         气压值： 分辨率 1PSI/bit 偏移量 0 错误值： 254 不可用： 255
         电流值： 分辨率 10mA/bit 偏移量 0 错误值： 254 不可用： 255

         状态： 0-离线， 1-在线
         机芯类型： 000-海康， 001-雄迈， 010-迪威泰 33,011-迪威泰 22
         置位状态： 0000-无， 0101-自水平， 0110-环绕一周， 0111-置上， 1000-置左， 1001-置右
         （当返回为相对应的状态值的时候， 表示该动作正在进行。 若返回为 0000 则表示该动作已经完成或
         者可以执行以上动作）
         */
        int ptz_temperature = bytes[ptzStatePosition + 1] & 0xff;
        int ptz_airPressure = bytes[ptzStatePosition + 2] & 0xff;
        int ptz_workElectricity = bytes[ptzStatePosition + 3] & 0xff;
        int ptz_machineElectricity1 = bytes[ptzStatePosition + 4] & 0xff;
        int ptz_machineElectricity2 = bytes[ptzStatePosition + 5] & 0xff;
        int ptz_lightElectricity = bytes[ptzStatePosition + 6] & 0xff;
        int ptz_heaterElectricity = bytes[ptzStatePosition + 7] & 0xff;
        int ptz_state = bytes[ptzStatePosition + 8] & 0xff;
        if (ptz_temperature < 254)
            remoteDeviceInfo.setPtz_temperature(ptz_temperature - 40);
        if (ptz_airPressure < 254)
            remoteDeviceInfo.setPtz_airPressure(ptz_airPressure);
        if (ptz_workElectricity < 254) {
            remoteDeviceInfo.setPtz_workElectricity(ptz_workElectricity * 10);
        }
        if (ptz_machineElectricity1 < 254) {
            remoteDeviceInfo.setPtz_machineElectricity1(ptz_machineElectricity1 * 10);
        }
        if (ptz_machineElectricity2 < 254) {
            remoteDeviceInfo.setPtz_machineElectricity2(ptz_machineElectricity2 * 10);
        }
        if (ptz_lightElectricity < 254) {
            remoteDeviceInfo.setPtz_lightElectricity(ptz_lightElectricity * 10);
        }
        if (ptz_heaterElectricity < 254) {
            remoteDeviceInfo.setPtz_heaterElectricity(ptz_heaterElectricity * 10);
        }

        int zaixian = ptz_state & 0x01;
        remoteDeviceInfo.setPtz_state(zaixian);

        int jiXin = (ptz_state >> 1) & 0x07;

        int zhiWei = ptz_state >> 4;
//        log("canSendStop zhiwei = "+ zhiWei+"  state = "+ Integer.toHexString(ptz_state));


        if (canSendStop) {
//            log("canSendStop true");
            if (zhiWei == 0) {
//                log("canSendStop send stop ptz");
                operator(ptzPosition + 1, 0xf0, 0x00);
            }
        } else {
//            log("canSendStop false");
            if (zhiWei != 0) {
//                log("set cansendStop true");
                canSendStop = true;
            }
        }


        /**
         状态： 00-离线， 01-在线
         温度值： 分辨率 1℃/bit 偏移量-40 错误值： 254 不可用： 255
         气压值： 分辨率 1PSI/bit 偏移量 0 错误值： 254 不可用： 255
         电流值： 分辨率 10mA/bit 偏移量 0 错误值： 254 不可用： 255
         */
        int light_temperature = bytes[lightStatePosition + 1] & 0xff;
        int light_airPressure = bytes[lightStatePosition + 2] & 0xff;
        int light_workElectricity = bytes[lightStatePosition + 3] & 0xff;
        int light_Electricity = bytes[lightStatePosition + 4] & 0xff;
        int light_state = bytes[lightStatePosition + 5] & 0xff;
        if (light_temperature < 254)
            remoteDeviceInfo.setLight_temperature(light_temperature - 40);
        if (light_airPressure < 254)
            remoteDeviceInfo.setLight_airPressure(light_airPressure);
        if (light_workElectricity < 254) {
            remoteDeviceInfo.setLight_workElectricity(light_workElectricity * 10);
        }
        if (light_Electricity < 254) {
            remoteDeviceInfo.setLight_Electricity(light_Electricity * 10);
        }
        remoteDeviceInfo.setLight_state(light_state);

        /*
        设备类型： 00-平板（PC） ， 01-手机
        * */
        int remoteDevice = bytes[remoteDevicePosition + 2] & 0xff;
        remoteDeviceInfo.setRemoteDevice(remoteDevice);
        StringBuilder builder = new StringBuilder();
        int addr1 = bytes[remoteDevicePosition + 3] & 0xff;
        int addr2 = bytes[remoteDevicePosition + 4] & 0xff;
        int addr3 = bytes[remoteDevicePosition + 5] & 0xff;
        int addr4 = bytes[remoteDevicePosition + 6] & 0xff;
        builder.append(addr1).append(".").append(addr2).append(".").append(addr3).append(".").append(addr4);
        remoteDeviceInfo.setControlIp(builder.toString());

        if (builder.toString().equals(InternetUtil.getWifiIp(VLCApplication.context()))) {
            sendSleepTime = 100;
        } else {
            sendSleepTime = 2560;
        }


        /**
         对象： 0000-无， 0001-EthToCAN 模块， 0010-爬行器， 0011-云台， 0100-升降架， 0101-大灯，
         0110-线缆车
         模式： 0000-无更新操作， 0001-固件更新操作
         状态： 0000-正常工作模式， 0001-进入更新， 0010-更新完成
         版本号： 产品型号 设备类型 日期 标识
         */
        int updateValue1 = bytes[updatePosition + 1] & 0xff;
        int updateObj = updateValue1 & 0x0f;
        int updateMode = updateValue1 >> 4;
        int updateState = bytes[updatePosition + 2] & 0xff;
        int versionUpdate_low = bytes[updatePosition + 3] & 0xff;
        int versionUpdate_high = bytes[updatePosition + 4] & 0xff;
        int versionNow_low = bytes[updatePosition + 5] & 0xff;
        int versionNow_high = bytes[updatePosition + 6] & 0xff;
        int versionUpdate = getHexFromHighAndLow(versionUpdate_low, versionUpdate_high);
        int versionNow = getHexFromHighAndLow(versionNow_low, versionNow_high);
        remoteDeviceInfo.setUpdateObj(updateObj);
        remoteDeviceInfo.setUpdateState(updateState);
        remoteDeviceInfo.setUpdateMode(updateMode);
        remoteDeviceInfo.setVersionNow(versionNow);
        remoteDeviceInfo.setVersionUpdate(versionUpdate);

        mIntentUpdateVersion.putExtra(Constant.KEY_MYDEVICE_UPDATE_VERSION_MAINFRAME_SHOW,versionNow);
        VLCApplication.context().sendBroadcast(mIntentUpdateVersion);
        mIntentUpdateVersion.putExtra(Constant.KEY_MYDEVICE_UPDATE_VERSION_MAINFRAME_SHOW,-1);


/*

        */
/**
 计米器值： 分辨率 1mm 偏移量 0 错误值： 0xFE00~0xFEFF 不可用： 0xFF00~0xFFFF
 电 量 值： 分辨率 1% 偏移量 0 错误值： 254 不可用： 255
 *//*


        int jimiqi_value = 0;
        int jimiqi_value_low = bytes[jimiqiPosition + 2] & 0xff;
        int jimiqi_value_high = bytes[jimiqiPosition + 3] & 0xff;
        int battery = bytes[jimiqiPosition + 4] & 0xff;
        jimiqi_value = getHexFromHighAndLow(jimiqi_value_low, jimiqi_value_high);
        if(jimiqi_value<65024)
        remoteDeviceInfo.setJimiqi_value(jimiqi_value);
        if(battery<254)
        remoteDeviceInfo.setBattery(battery);
*/
/*

        */
/**
 温度值： 分辨率 1℃/bit 偏移量-40 错误值： 254 不可用： 255
 气压值： 分辨率 1PSI/bit 偏移量 0 错误值： 254 不可用： 255
 电压值： 分辨率 0.5V/bit 偏移量 0 错误值： 254 不可用： 255
 电流值： 分辨率 10mA/bit 偏移量 0 错误值： 254 不可用： 255
 *//*

        int car_temperature = bytes[carStatePosition + 1] & 0xff;
        int car_airPressure = bytes[carStatePosition + 2] & 0xff;
        int car_valtage = bytes[carStatePosition + 3] & 0xff;
        int car_Electricity = bytes[carStatePosition + 4] & 0xff;
        if(car_temperature<254)
        remoteDeviceInfo.setCar_temperature(car_temperature-40);
        if(car_airPressure<254)
        remoteDeviceInfo.setCar_airPressure(car_airPressure);
        if(car_valtage<254)
        remoteDeviceInfo.setCar_valtage(car_valtage/2);
        if(car_Electricity<254)
        remoteDeviceInfo.setCar_Electricity(car_Electricity*10);
*/

        previewImpl.result(remoteDeviceInfo);
        mBroadCastIntent.putExtra(Constant.KEY_RESULT_INFO_PAXINGQI, remoteDeviceInfo);
        VLCApplication.context().sendBroadcast(mBroadCastIntent);

    }

    private int getHexFromHighAndLow(int low, int high) {
        return high << 8 | low;
    }


    private void operator(int position, int arg1, int arg2) {
        int arg = commands[position] & 0xff;
        commands[position] = (byte) (arg & arg1 | arg2);
//        socketUtil.sendCommands(commands_car);
    }

    @Override
    public void move_on() {
        log("move_on");
        operator(movePosition + 2, 0x0f, 0x10);

    }

    @Override
    public void move_off() {
        log("move_off");
        operator(movePosition + 2, 0x0f, 0x00);
    }

    @Override
    public void move_top() { //0001
        log("move_top");
        operator(movePosition + 2, 0xf0, 0x01);
    }

    @Override
    public void move_bottom() {//0010
        log("move_bottom");
        operator(movePosition + 2, 0xf0, 0x02);
    }

    @Override
    public void move_left() {//0011
        log("move_left");
        operator(movePosition + 2, 0xf0, 0x03);
    }

    @Override
    public void move_right() {//0100
        log("move_right");
        operator(movePosition + 2, 0xf0, 0x04);
    }

    @Override
    public void move_stop() {
        log("move_stop");
        operator(movePosition + 2, 0xf0, 0x00);
    }


    @Override
    public void move_speed(int speed) {
        if (speed > 10 || speed < 0)
            throwException("move speed out of limited");

        log("move_speed =  " + speed);
//        commands_car[movePosition + 1] = (byte) speed;
        operator(movePosition + 1, 0x00, speed);

    }


    @Override
    public void ptz_left() {
        log("ptz_left ");
        canSendStop = false;
        operator(ptzPosition + 1, 0xf0, 0x01);

    }

    @Override
    public void ptz_right() {
        log("ptz_right ");
        canSendStop = false;
        operator(ptzPosition + 1, 0xf0, 0x02);

    }

    @Override
    public void ptz_top() {
        log("ptz_top ");
        canSendStop = false;
        operator(ptzPosition + 1, 0xf0, 0x03);

    }


    @Override
    public void ptz_bottom() {
        log("ptz_bottom ");
        canSendStop = false;
        operator(ptzPosition + 1, 0xf0, 0x04);

    }


    @Override
    public void ptz_reset() {
        log("ptz_reset ");
        canSendStop = false;
        operator(ptzPosition + 1, 0xf0, 0x05);
//        operator(ptzPosition + 1, 0x0f, 0x50);

    }

    @Override
    public void ptz_set_round() {
        log("ptz_set_round ");
        canSendStop = false;
        operator(ptzPosition + 1, 0xf0, 0x06);
//        operator(ptzPosition + 1, 0x0f, 0x60);
    }

    @Override
    public void ptz_set_top() {
        log("ptz_set_top ");
        canSendStop = false;
        operator(ptzPosition + 1, 0xf0, 0x07);
//        operator(ptzPosition + 1, 0x0f, 0x70);
    }

    @Override
    public void ptz_set_left() {
        log("ptz_set_left ");
        canSendStop = false;
        operator(ptzPosition + 1, 0xf0, 0x08);
//        operator(ptzPosition + 1, 0x0f, 0x80);
    }

    @Override
    public void ptz_set_right() {
        log("ptz_set_right ");
        canSendStop = false;
        operator(ptzPosition + 1, 0xf0, 0x09);
//        operator(ptzPosition + 1, 0x0f, 0x90);

    }

    @Override
    public void ptz_stop() {
        log("ptz_stop ");
        operator(ptzPosition + 1, 0xf0, 0x00);

    }

    @Override
    public void ptz_light_LX(int lx) {
        log("ptz_light_LX  lx =  " + lx);
        if (lx > 10 || lx < 0)
            throwException("ptz light out of limited!");
        operator(ptzPosition + 2, 0xf0, lx);
    }

    @Override
    public void ptz_heat_on() {
        log("ptz_heat_on ");
        operator(ptzPosition + 2, 0xcf, 0x10);
    }

    @Override
    public void ptz_heat_off() {
        log("ptz_heat_off ");
        operator(ptzPosition + 2, 0xcf, 0x00);

    }


    @Override
    public void ptz_lazer_on() {
        log("ptz_lazer_on ");
        operator(ptzPosition + 2, 0x3f, 0x40);

    }

    @Override
    public void ptz_lazer_off() {
        log("ptz_lazer_off ");
        operator(ptzPosition + 2, 0x3f, 0x00);

    }


    @Override
    public void lift_rise() {
        log("lift_rise ");
        operator(liftMovePosition + 1, 0x00, 0x01);
    }

    @Override
    public void lift_fall() {
        log("lift_fall ");
        operator(liftMovePosition + 1, 0x00, 0x02);

    }

    @Override
    public void lift_stop() {
        log("lift_stop ");
        operator(liftMovePosition + 1, 0x00, 0x00);
    }

    @Override
    public void lift_Max() {
        log("lift_Max ");
        operator(liftMovePosition + 2, 0xfc, 0x02);
    }

    @Override
    public void lift_min() {
        log("lift_min ");
        operator(liftMovePosition + 2, 0xfc, 0x01);
    }

    @Override
    public void lift_default() {
        log("lift_default ");
        operator(liftMovePosition + 2, 0xfc, 0x00);
    }

    @Override
    public void lift_biaoding_on() {
        log("lift_biaoding_on ");
        operator(liftMovePosition + 2, 0xfb, 0x04);
    }

    @Override
    public void lift_biaoding_off() {
        log("lift_biaoding_off ");
        operator(liftMovePosition + 2, 0xfb, 0x00);

    }

    @Override
    public void lightBack_L1(int lx) {
        operator(liftMovePosition + 2, 0x0f, lx << 4);
    }


    @Override
    public void zoom_add() {
        log("zoom_add ");
        operator(zfPosition + 1, 0xf0, 0x01);
    }

    @Override
    public void zoom_sub() {
        log("zoom_sub ");
        operator(zfPosition + 1, 0xf0, 0x02);

    }

    @Override
    public void zoom_stop() {
        log("zoom_stop ");
        operator(zfPosition + 1, 0xf0, 0x00);

    }

    @Override
    public void focus_add() {
        log("focus_add ");
        operator(zfPosition + 1, 0x0f, 0x10);

    }

    @Override
    public void focus_sub() {
        log("focus_sub ");
        operator(zfPosition + 1, 0x0f, 0x20);

    }

    @Override
    public void focus_stop() {
        log("focus_stop ");
        operator(zfPosition + 1, 0x0f, 0x00);

    }

    @Override
    public void ptz_rotate_zero() {
        log("ptz_rotate_zero ");
        operator(zfPosition + 2, 0xf0, 0x01);
    }

    @Override
    public void ptz_pitch_zero() {
        log("ptz_pitch_zero ");
        operator(zfPosition + 2, 0xf0, 0x02);
    }

    @Override
    public void ptz_pitch_max() {
        log("ptz_pitch_max ");
        operator(zfPosition + 2, 0xf0, 0x04);
    }

    @Override
    public void ptz_pitch_min() {
        log("ptz_pitch_min ");
        operator(zfPosition + 2, 0xf0, 0x08);
    }

    @Override
    public void ptz_default() {
        log("ptz_default ");
        operator(zfPosition + 2, 0xf0, 0x00);
    }

    @Override
    public void ptz_biaoding_on() {
        log("ptz_biaoding_on ");
        operator(zfPosition + 2, 0xef, 0x10);
    }

    @Override
    public void ptz_biaoding_off() {
        log("ptz_biaoding_off ");
        operator(zfPosition + 2, 0xef, 0x00);
    }

    @Override
    public void ptz_speed_set(int speed) {
        log("ptz_speed = " + speed);
        operator(zfPosition + 1, 0xf0, speed);
    }

    @Override
    public void light_front_on() {
        log("light_front_on ");
//        operator(lfPosition + 1, 0x0f, 0x10);
    }

    @Override
    public void light_front_off() {
        log("light_front_off ");
//        operator(lfPosition + 1, 0x0f, 0x00);

    }

    @Override
    public void light_front_off_to_back(int power) {
        log("light_front_off_to_back ");

        operator(lfPosition + 1, 0x0f, power << 4);

    }

    @Override
    public void light_front_LX(int lx) {
        log("light_front_LX lx =  " + lx);
        if (lx > 10 || lx < 0)
            throwException("light front out of limited!");
        operator(lfPosition + 1, 0xf0, lx);

    }

    @Override
    public void light_back_LX(int lx) {
        log("light_back_LX lx =  " + lx);
        operator(lbPosition + 1, 0xf0, lx);

    }

    @Override
    public void light_back_on() {
        log("light_back_on ");
        operator(lbPosition + 1, 0xcf, 0x10);
    }

    @Override
    public void light_back_off() {
        log("light_back_off ");
        operator(lbPosition + 1, 0xcf, 0x00);

    }


    @Override
    public void machine_reset_default() {
        log("machine_reset_default ");
        operator(lbPosition + 1, 0x3f, 0x00);
    }

    @Override
    public void machine_reset() {
        log("machine_reset ");
        operator(lbPosition + 1, 0x3f, 0x40);
    }


/*
    @Override
    public void jimiqi_zero() {
        operator(jimiqiPosition + 1, 0x01, 0);
    }*/


}
