package com.bmw.m2.utils;

import com.bmw.m2.model.CarRemoteDeviecInfo;
import com.bmw.m2.model.Environment;
import com.bmw.m2.model.RemoteDeviceInfo;

import java.util.List;

/**
 * Created by admin on 2017/7/22.
 */

public class ListUtil {

    public static List<Environment> getEnvironmentList(List<Environment> list,
                                                       CarRemoteDeviecInfo carRemoteDeviecInfo,
                                                       RemoteDeviceInfo remoteDeviceInfo){
        if(remoteDeviceInfo != null) {
            list.get(0).setCurrent_num(remoteDeviceInfo.getDynamo_temperature1());
            list.get(1).setCurrent_num(remoteDeviceInfo.getDynamo_temperature2());
            list.get(2).setCurrent_num(remoteDeviceInfo.getDynamo_electricity1());
            list.get(3).setCurrent_num(remoteDeviceInfo.getDynamo_electricity2());
            list.get(4).setCurrent_num(remoteDeviceInfo.getDynamo_rotate_speed1());
            list.get(5).setCurrent_num(remoteDeviceInfo.getDynamo_rotate_speed2());

            list.get(0).setStat(1);
            list.get(1).setStat(1);
            list.get(2).setStat(1);
            list.get(3).setStat(1);
            list.get(4).setStat(1);
            list.get(5).setStat(1);


            list.get(6).setCurrent_num(remoteDeviceInfo.getMachine_temperature());
            list.get(7).setCurrent_num(remoteDeviceInfo.getMachine_airPressure());
            list.get(8).setCurrent_num(remoteDeviceInfo.getMachine_voltage());
            list.get(9).setCurrent_num(remoteDeviceInfo.getMachine_electricity());

            list.get(6).setStat(remoteDeviceInfo.getMachine_state());
            list.get(7).setStat(remoteDeviceInfo.getMachine_state());
            list.get(8).setStat(remoteDeviceInfo.getMachine_state());
            list.get(9).setStat(remoteDeviceInfo.getMachine_state());

            list.get(10).setCurrent_num(remoteDeviceInfo.getLift_temperature());
            list.get(11).setCurrent_num(remoteDeviceInfo.getLift_airPressure());
            list.get(12).setCurrent_num(remoteDeviceInfo.getLift_workElectricity());
            list.get(13).setCurrent_num(remoteDeviceInfo.getLift_machineElectricity());

            list.get(10).setStat(remoteDeviceInfo.getLift_state());
            list.get(11).setStat(remoteDeviceInfo.getLift_state());
            list.get(12).setStat(remoteDeviceInfo.getLift_state());
            list.get(13).setStat(remoteDeviceInfo.getLift_state());

            list.get(14).setCurrent_num(remoteDeviceInfo.getPtz_temperature());
            list.get(15).setCurrent_num(remoteDeviceInfo.getPtz_airPressure());
            list.get(16).setCurrent_num(remoteDeviceInfo.getPtz_workElectricity());
            list.get(17).setCurrent_num(remoteDeviceInfo.getPtz_machineElectricity1());
            list.get(18).setCurrent_num(remoteDeviceInfo.getPtz_machineElectricity2());
            list.get(19).setCurrent_num(remoteDeviceInfo.getPtz_lightElectricity());
            list.get(20).setCurrent_num(remoteDeviceInfo.getPtz_heaterElectricity());

            list.get(14).setStat(remoteDeviceInfo.getPtz_state());
            list.get(15).setStat(remoteDeviceInfo.getPtz_state());
            list.get(16).setStat(remoteDeviceInfo.getPtz_state());
            list.get(17).setStat(remoteDeviceInfo.getPtz_state());
            list.get(18).setStat(remoteDeviceInfo.getPtz_state());
            list.get(19).setStat(remoteDeviceInfo.getPtz_state());
            list.get(20).setStat(remoteDeviceInfo.getPtz_state());


            list.get(21).setCurrent_num(remoteDeviceInfo.getLight_temperature());
            list.get(22).setCurrent_num(remoteDeviceInfo.getLight_airPressure());
            list.get(23).setCurrent_num(remoteDeviceInfo.getLight_workElectricity());
            list.get(24).setCurrent_num(remoteDeviceInfo.getLight_Electricity());


            list.get(21).setStat(remoteDeviceInfo.getLight_state());
            list.get(22).setStat(remoteDeviceInfo.getLight_state());
            list.get(23).setStat(remoteDeviceInfo.getLight_state());
            list.get(24).setStat(remoteDeviceInfo.getLight_state());
        }

        if(carRemoteDeviecInfo!= null) {
            list.get(25).setCurrent_num(carRemoteDeviecInfo.getDynamo_voltage());
            list.get(26).setCurrent_num(carRemoteDeviecInfo.getDynamo_electricity());
            list.get(27).setCurrent_num(carRemoteDeviecInfo.getDynamo_rotate_speed());
            list.get(28).setCurrent_num(carRemoteDeviecInfo.getDynamo_temperature());


            list.get(25).setStat(1);
            list.get(26).setStat(1);
            list.get(27).setStat(1);
            list.get(28).setStat(1);

            list.get(29).setCurrent_num(carRemoteDeviecInfo.getCar_temperature());
            list.get(30).setCurrent_num(carRemoteDeviecInfo.getCar_airPressure());
            list.get(31).setCurrent_num(carRemoteDeviecInfo.getCar_valtage());
            list.get(32).setCurrent_num(carRemoteDeviecInfo.getCar_Electricity());


            list.get(29).setStat(1);
            list.get(30).setStat(1);
            list.get(31).setStat(1);
            list.get(32).setStat(1);
        }

        return list;
    }


}
