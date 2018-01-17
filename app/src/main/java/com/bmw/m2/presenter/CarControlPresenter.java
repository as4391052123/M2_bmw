package com.bmw.m2.presenter;

/**
 * Created by admin on 2017/6/17.
 */

public interface CarControlPresenter {

    byte[] commands_car = new byte[]{
            (byte) 0xa5, 0x5a, 0x2e, 0, 0x06, 0
            , 4, 7, 0, 0    //7
            , 8, 0x10, 0, 0, 0, 0, 0, 0   //11
            , 8, 0x11, 0, 0, 0, 0, 0, 0   //19
            , 6, 0x12, 0, 0, 0, 0   //27
            , 8, 0x1c, 0, 0, 0, 0, 0, 0  //33
            , 8, 0x21, 0, 0, 0, 0, 0, 0  //41
    };

    int shouxianPosition = 7;
    int jimiqiPosition = 11;
    int carDynamoStatePosition = 19;
    int carStatePosition = 27;
    int carRemoteDevicePosition = 33;
    int carUpdatePosition = 41;

    void update(boolean ready);
    void updateModel(boolean isReady);
    void car_lihe_on();
    void car_lihe_off();
    void car_shouxian_speed(int speed);
    void car_fangxian_liju(int num);
    void car_shouxian_fang();
    void car_shouxian_shou();
    void car_shouxian_stop();
    void car_jimi(int num);

}
