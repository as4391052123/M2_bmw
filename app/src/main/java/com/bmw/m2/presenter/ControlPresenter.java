package com.bmw.m2.presenter;

/**
 * Created by admin on 2017/5/16.
 */

public interface ControlPresenter {


    /**
     * int liftHeightPosition = 31;
     * int posePosition = 35;
     * int ptzAnglePosition = 41;
     * int dynamoStatePosition = 47;
     * int machineStatePosition = 55;
     * int liftStatePosition = 63;
     * int ptzStatePosition = 71;
     * int lightStatePosition = 81;
     */


    byte[] commands = new byte[]{
            (byte) 0xa5, 0x5a, 0x66, 0, 0x10, 0
            , 4, 1, 0, 0x10    //7
            , 4, 2, 0, 0    //11
            , 4, 3, 0, 0    //15
            , 4, 4, 0, 0    //19
            , 4, 5, 0, 0    //23
            , 4, 6, 0, 0    //27
            , 4, 0x13, 0, 0 //31
            , 6, 0x14, 0, 0, 0, 0   //35
            , 6, 0x15, 0, 0, 0, 0   //41
            , 8, 0x16, 0, 0, 0, 0, 0, 0 //47
            , 8, 0x17, 0, 0, 0, 0, 0, 0 //55
            , 8, 0x18, 0, 0, 0, 0, 0, 0 //63
            , 0x0a, 0x19, 0, 0, 0, 0, 0, 0, 0, 0    //71
            , 8, 0x1a, 0, 0, 0, 0, 0, 0 //81
            , 8, 0x1b, 0, 0, 0, 0, 0, 0 //89
            , 8, 0x20, 0, 0, 0, 0, 0, 0 //97
    };


    int movePosition = 7;
    int ptzPosition = 11;
    int liftMovePosition = 15;
    int zfPosition = 19;
    int lfPosition = 23;
    int lbPosition = 27;


    int liftHeightPosition = 31;
    int posePosition = 35;
    int ptzAnglePosition = 41;
    int dynamoStatePosition = 47;
    int machineStatePosition = 55;
    int liftStatePosition = 63;
    int ptzStatePosition = 71;
    int lightStatePosition = 81;
    int remoteDevicePosition = 89;
    int updatePosition = 97;

    void updateObject(int object);
    void updateModel(boolean ready);

    void changeIp(String ip, int port);

    void release();

    void move_on();

    void move_off();

    void move_top();

    void move_bottom();

    void move_left();

    void move_right();

    void move_stop();

    void move_speed(int speed);


    void ptz_left();

    void ptz_top();

    void ptz_right();

    void ptz_bottom();

    void ptz_set_left();

    void ptz_set_top();

    void ptz_set_right();

    void ptz_set_round();

    void ptz_reset();

    void ptz_stop();

    void ptz_light_LX(int lx);

    void ptz_lazer_on();

    void ptz_lazer_off();

    void ptz_heat_on();

    void ptz_heat_off();

    void lift_rise();

    void lift_fall();

    void lift_stop();

    void lift_Max();

    void lift_min();

    void lift_default();

    void lift_biaoding_on();
    void lift_biaoding_off();

    void lightBack_L1(int lx);


    void zoom_add();

    void zoom_sub();

    void zoom_stop();

    void focus_add();

    void focus_sub();

    void focus_stop();

    void ptz_rotate_zero();
    void ptz_pitch_zero();
    void ptz_pitch_max();
    void ptz_pitch_min();
    void ptz_default();
    void ptz_biaoding_on();
    void ptz_biaoding_off();

    void ptz_speed_set(int speed);



    void light_front_on();

    void light_front_off();

    void light_front_off_to_back(int power);

    void light_front_LX(int lx);


    void light_back_on();

    void light_back_off();

    void light_back_LX(int lx);

    void machine_reset_default();

    void machine_reset();


//    void jimiqi_zero();

}
