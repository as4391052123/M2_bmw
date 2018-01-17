package com.bmw.m2.model;

import java.io.Serializable;

/**
 * Created by admin on 2017/6/8.
 */

public class RemoteDeviceInfo implements Serializable{
    private int ptz_direction_state;

    private int lift_height;
    private float lift_angle;

    private float poseSlant;
    private float poseRoll;

    private float ptz_axial_angle;
    private float ptz_radial_angle;

    private int dynamo_temperature1;
    private int dynamo_temperature2;
    private int dynamo_electricity1;
    private int dynamo_electricity2;
    private int dynamo_rotate_speed1;
    private int dynamo_rotate_speed2;

    private int machine_temperature;
    private int machine_airPressure;
    private float machine_voltage;
    private int machine_electricity;
    private int machine_state;
    private int machine_protect;

    private int lift_temperature;
    private int lift_airPressure;
    private int lift_workElectricity;
    private int lift_machineElectricity;
    private int lift_state;

    private int ptz_temperature;
    private int ptz_airPressure;
    private int ptz_workElectricity;
    private int ptz_machineElectricity1;
    private int ptz_machineElectricity2;
    private int ptz_lightElectricity;
    private int ptz_heaterElectricity;
    private int ptz_state;

    private int light_temperature;
    private int light_airPressure;
    private int light_workElectricity;
    private int light_Electricity;
    private int light_state;

    private int remoteDevice;

    private int updateObj;
    private int updateMode;
    private int updateState;
    private int versionUpdate;
    private int versionNow;

    private int jimiqi_value;
    private int battery;

    private int car_temperature;
    private int car_airPressure;
    private int car_valtage;
    private int car_Electricity;
    private String controlIp;

    public int getPtz_direction_state() {
        return ptz_direction_state;
    }

    public void setPtz_direction_state(int ptz_direction_state) {
        this.ptz_direction_state = ptz_direction_state;
    }

    public int getLift_height() {
        return lift_height;
    }

    public void setLift_height(int lift_height) {
        this.lift_height = lift_height;
    }

    public float getLift_angle() {
        return lift_angle;
    }

    public void setLift_angle(float lift_angle) {
        this.lift_angle = lift_angle;
    }

    public float getPoseSlant() {
        return poseSlant;
    }

    public void setPoseSlant(float poseSlant) {
        this.poseSlant = poseSlant;
    }

    public float getPoseRoll() {
        return poseRoll;
    }

    public void setPoseRoll(float poseRoll) {
        this.poseRoll = poseRoll;
    }

    public float getPtz_axial_angle() {
        return ptz_axial_angle;
    }

    public void setPtz_axial_angle(float ptz_axial_angle) {
        this.ptz_axial_angle = ptz_axial_angle;
    }

    public float getPtz_radial_angle() {
        return ptz_radial_angle;
    }

    public void setPtz_radial_angle(float ptz_radial_angle) {
        this.ptz_radial_angle = ptz_radial_angle;
    }

    public int getDynamo_temperature1() {
        return dynamo_temperature1;
    }

    public void setDynamo_temperature1(int dynamo_temperature1) {
        this.dynamo_temperature1 = dynamo_temperature1;
    }

    public int getDynamo_temperature2() {
        return dynamo_temperature2;
    }

    public void setDynamo_temperature2(int dynamo_temperature2) {
        this.dynamo_temperature2 = dynamo_temperature2;
    }

    public int getDynamo_electricity1() {
        return dynamo_electricity1;
    }

    public void setDynamo_electricity1(int dynamo_electricity1) {
        this.dynamo_electricity1 = dynamo_electricity1;
    }

    public int getDynamo_electricity2() {
        return dynamo_electricity2;
    }

    public void setDynamo_electricity2(int dynamo_electricity2) {
        this.dynamo_electricity2 = dynamo_electricity2;
    }

    public int getDynamo_rotate_speed1() {
        return dynamo_rotate_speed1;
    }

    public void setDynamo_rotate_speed1(int dynamo_rotate_speed1) {
        this.dynamo_rotate_speed1 = dynamo_rotate_speed1;
    }

    public int getDynamo_rotate_speed2() {
        return dynamo_rotate_speed2;
    }

    public void setDynamo_rotate_speed2(int dynamo_rotate_speed2) {
        this.dynamo_rotate_speed2 = dynamo_rotate_speed2;
    }

    public int getMachine_temperature() {
        return machine_temperature;
    }

    public void setMachine_temperature(int machine_temperature) {
        this.machine_temperature = machine_temperature;
    }

    public int getMachine_airPressure() {
        return machine_airPressure;
    }

    public void setMachine_airPressure(int machine_airPressure) {
        this.machine_airPressure = machine_airPressure;
    }

    public float getMachine_voltage() {
        return machine_voltage;
    }

    public void setMachine_voltage(float machine_voltage) {
        this.machine_voltage = machine_voltage;
    }

    public int getMachine_electricity() {
        return machine_electricity;
    }

    public void setMachine_electricity(int machine_electricity) {
        this.machine_electricity = machine_electricity;
    }

    public int getMachine_state() {
        return machine_state;
    }

    public void setMachine_state(int machine_state) {
        this.machine_state = machine_state;
    }

    public int getMachine_protect() {
        return machine_protect;
    }

    public void setMachine_protect(int machine_protect) {
        this.machine_protect = machine_protect;
    }

    public int getLift_temperature() {
        return lift_temperature;
    }

    public void setLift_temperature(int lift_temperature) {
        this.lift_temperature = lift_temperature;
    }

    public int getLift_airPressure() {
        return lift_airPressure;
    }

    public void setLift_airPressure(int lift_airPressure) {
        this.lift_airPressure = lift_airPressure;
    }

    public int getLift_workElectricity() {
        return lift_workElectricity;
    }

    public void setLift_workElectricity(int lift_workElectricity) {
        this.lift_workElectricity = lift_workElectricity;
    }

    public int getLift_machineElectricity() {
        return lift_machineElectricity;
    }

    public void setLift_machineElectricity(int lift_machineElectricity) {
        this.lift_machineElectricity = lift_machineElectricity;
    }

    public int getLift_state() {
        return lift_state;
    }

    public void setLift_state(int lift_state) {
        this.lift_state = lift_state;
    }

    public int getPtz_temperature() {
        return ptz_temperature;
    }

    public void setPtz_temperature(int ptz_temperature) {
        this.ptz_temperature = ptz_temperature;
    }

    public int getPtz_airPressure() {
        return ptz_airPressure;
    }

    public void setPtz_airPressure(int ptz_airPressure) {
        this.ptz_airPressure = ptz_airPressure;
    }

    public int getPtz_workElectricity() {
        return ptz_workElectricity;
    }

    public void setPtz_workElectricity(int ptz_workElectricity) {
        this.ptz_workElectricity = ptz_workElectricity;
    }

    public int getPtz_machineElectricity1() {
        return ptz_machineElectricity1;
    }

    public void setPtz_machineElectricity1(int ptz_machineElectricity1) {
        this.ptz_machineElectricity1 = ptz_machineElectricity1;
    }

    public int getPtz_machineElectricity2() {
        return ptz_machineElectricity2;
    }

    public void setPtz_machineElectricity2(int ptz_machineElectricity2) {
        this.ptz_machineElectricity2 = ptz_machineElectricity2;
    }

    public int getPtz_lightElectricity() {
        return ptz_lightElectricity;
    }

    public void setPtz_lightElectricity(int ptz_lightElectricity) {
        this.ptz_lightElectricity = ptz_lightElectricity;
    }

    public int getPtz_heaterElectricity() {
        return ptz_heaterElectricity;
    }

    public void setPtz_heaterElectricity(int ptz_heaterElectricity) {
        this.ptz_heaterElectricity = ptz_heaterElectricity;
    }

    public int getPtz_state() {
        return ptz_state;
    }

    public void setPtz_state(int ptz_state) {
        this.ptz_state = ptz_state;
    }

    public int getLight_temperature() {
        return light_temperature;
    }

    public void setLight_temperature(int light_temperature) {
        this.light_temperature = light_temperature;
    }

    public int getLight_airPressure() {
        return light_airPressure;
    }

    public void setLight_airPressure(int light_airPressure) {
        this.light_airPressure = light_airPressure;
    }

    public int getLight_workElectricity() {
        return light_workElectricity;
    }

    public void setLight_workElectricity(int light_workElectricity) {
        this.light_workElectricity = light_workElectricity;
    }

    public int getLight_Electricity() {
        return light_Electricity;
    }

    public void setLight_Electricity(int light_Electricity) {
        this.light_Electricity = light_Electricity;
    }

    public int getLight_state() {
        return light_state;
    }

    public void setLight_state(int light_state) {
        this.light_state = light_state;
    }

    public int getRemoteDevice() {
        return remoteDevice;
    }

    public int getUpdateObj() {
        return updateObj;
    }

    public void setUpdateObj(int updateObj) {
        this.updateObj = updateObj;
    }

    public int getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(int updateMode) {
        this.updateMode = updateMode;
    }

    public int getUpdateState() {
        return updateState;
    }

    public void setUpdateState(int updateState) {
        this.updateState = updateState;
    }

    public int getVersionUpdate() {
        return versionUpdate;
    }

    public void setVersionUpdate(int versionUpdate) {
        this.versionUpdate = versionUpdate;
    }

    public int getVersionNow() {
        return versionNow;
    }

    public void setVersionNow(int versionNow) {
        this.versionNow = versionNow;
    }

    public void setRemoteDevice(int remoteDevice) {
        this.remoteDevice = remoteDevice;
    }

    public int getJimiqi_value() {
        return jimiqi_value;
    }

    public void setJimiqi_value(int jimiqi_value) {
        this.jimiqi_value = jimiqi_value;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getCar_temperature() {
        return car_temperature;
    }

    public void setCar_temperature(int car_temperature) {
        this.car_temperature = car_temperature;
    }

    public int getCar_airPressure() {
        return car_airPressure;
    }

    public void setCar_airPressure(int car_airPressure) {
        this.car_airPressure = car_airPressure;
    }

    public int getCar_valtage() {
        return car_valtage;
    }

    public void setCar_valtage(int car_valtage) {
        this.car_valtage = car_valtage;
    }

    public int getCar_Electricity() {
        return car_Electricity;
    }

    public void setCar_Electricity(int car_Electricity) {
        this.car_Electricity = car_Electricity;
    }

    public String getControlIp() {
        return controlIp;
    }

    public void setControlIp(String controlIp) {
        this.controlIp = controlIp;
    }
}
