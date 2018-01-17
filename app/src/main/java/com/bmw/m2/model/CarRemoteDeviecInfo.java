package com.bmw.m2.model;

import java.io.Serializable;

/**
 * Created by admin on 2017/6/17.
 */

public class CarRemoteDeviecInfo implements Serializable{
    private long jimiqi_value;
    private int battery;

    private float dynamo_voltage;
    private int dynamo_electricity;
    private int dynamo_rotate_speed;
    private int dynamo_temperature;
    private int dynamo_alarm;

    private int car_temperature;
    private int car_airPressure;
    private float car_valtage;
    private int car_Electricity;


    private int updateObj;
    private int updateMode;
    private int updateState;
    private int versionUpdate;
    private int versionNow;

    private int remoteDevice;
    private String controlIp;

    public long getJimiqi_value() {
        return jimiqi_value;
    }

    public void setJimiqi_value(long jimiqi_value) {
        this.jimiqi_value = jimiqi_value;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public float getDynamo_voltage() {
        return dynamo_voltage;
    }

    public void setDynamo_voltage(float dynamo_voltage) {
        this.dynamo_voltage = dynamo_voltage;
    }

    public int getDynamo_electricity() {
        return dynamo_electricity;
    }

    public void setDynamo_electricity(int dynamo_electricity) {
        this.dynamo_electricity = dynamo_electricity;
    }

    public int getDynamo_rotate_speed() {
        return dynamo_rotate_speed;
    }

    public void setDynamo_rotate_speed(int dynamo_rotate_speed) {
        this.dynamo_rotate_speed = dynamo_rotate_speed;
    }

    public int getDynamo_temperature() {
        return dynamo_temperature;
    }

    public void setDynamo_temperature(int dynamo_temperature) {
        this.dynamo_temperature = dynamo_temperature;
    }

    public int getDynamo_alarm() {
        return dynamo_alarm;
    }

    public void setDynamo_alarm(int dynamo_alarm) {
        this.dynamo_alarm = dynamo_alarm;
    }


    public int getCar_airPressure() {
        return car_airPressure;
    }

    public void setCar_airPressure(int car_airPressure) {
        this.car_airPressure = car_airPressure;
    }

    public int getCar_temperature() {
        return car_temperature;
    }

    public void setCar_temperature(int car_temperature) {
        this.car_temperature = car_temperature;
    }

    public float getCar_valtage() {
        return car_valtage;
    }

    public void setCar_valtage(float car_valtage) {
        this.car_valtage = car_valtage;
    }

    public int getCar_Electricity() {
        return car_Electricity;
    }

    public void setCar_Electricity(int car_Electricity) {
        this.car_Electricity = car_Electricity;
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

    public int getRemoteDevice() {
        return remoteDevice;
    }

    public void setRemoteDevice(int remoteDevice) {
        this.remoteDevice = remoteDevice;
    }

    public String getControlIp() {
        return controlIp;
    }

    public void setControlIp(String controlIp) {
        this.controlIp = controlIp;
    }
}
