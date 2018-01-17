package com.bmw.m2.views.activity;

import android.support.v7.app.AlertDialog;

import com.bmw.m2.jna.SystemTransformByJNA;
import com.bmw.m2.jna.SystemTransformJNAInstance;
import com.bmw.m2.model.All_id_Info;
import com.bmw.m2.presenter.ControlPresenter;
import com.bmw.m2.presenter.HCNetSdkLogin;
import com.bmw.m2.presenter.PtzControlPresenter;
import com.bmw.m2.presenter.VlcPresenter;

import static com.bmw.m2.utils.ThreadUtil.sleep;
import static com.bmw.m2.utils.ThrowUtil.log;

/**
 * Created by admin on 2017/7/5.
 */

public class FatigueTest {
    private ControlPresenter controlPresenter;
    private HCNetSdkLogin hcNetSdkLogin;
    private VlcPresenter vlcPresenter;
    private PtzControlPresenter ptzControlPresenter;
    private boolean isStopFatigue = true;
    private Runnable moveTask;
    private Runnable liftTask;
    private Runnable lightTask;
    private Runnable ptzlrTask;
    private Runnable ptzubTask;
    private Runnable cameraTask;
    private Runnable haikangZoomTask;
    private Runnable fogTask;

    private Runnable moveTopRunnable;
    private Runnable moveBottomRunnable;
    private Runnable moveLeftRunnable;
    private Runnable moveRightRunnable;
    private Runnable liftRiseRunnable;
    private Runnable liftFallRunnable;
    private Runnable ptzTopRunnable;
    private Runnable ptzBottomRunnable;
    private Runnable ptzLeftRunnable;
    private Runnable ptzRightRunnable;


    private int liftTime = 17;
    private int ptzlrLeftTime = 32;
    private int ptzlrRightTime = 33;
    private int ptzubTime = 13;
    private int ptzubHalfTime = 3;
    private boolean isPtzFirst = true;
    private int hkZoomTime = 5;

    private int moveTopBottomTime = 1000 * 60 * 5;
    private int moveLeftRightTime = 1000 * 60 * 2;
    private long cameraChangeTime = 1000 * 60 * 30;
    private long fogTestTime = 1000 * 60 * 5;
    private boolean isRecord;
    private long ptzResetTime = 6200;
    private boolean isUp;
    private Runnable lightPtzTask;
    private boolean isOpenLihe;
    private long lift_wait_time = 60 * 5;

    public boolean isStop() {
        return isStopFatigue;
    }

    public FatigueTest(ControlPresenter controlPresenter, HCNetSdkLogin hcNetSdkLogin, VlcPresenter vlcPresenter, PtzControlPresenter ptzControlPresenter) {
        this.controlPresenter = controlPresenter;
        this.hcNetSdkLogin = hcNetSdkLogin;
        this.vlcPresenter = vlcPresenter;
        this.ptzControlPresenter = ptzControlPresenter;
        initFatigue();
        initFastFatigue();
    }

    public void fastTestTop() {
        setMoveAction("top");
        setPtzAction("top");
        setLiftAction("rise");
    }

    public void fastTestBottom() {
        setMoveAction("bottom");
        setPtzAction("bottom");
        setLiftAction("fall");
    }

    public void fastTestLeft() {
        setMoveAction("left");
        setPtzAction("left");
        setLiftAction("rise");
    }

    public void fastTestRight() {
        setMoveAction("right");
        setPtzAction("right");
        setLiftAction("fall");
    }


    private boolean isMove = true;
    private boolean isLift = true;
    private boolean isLight = true;
    private boolean isPtzMove = true;
    private boolean isPtzFuyangMove = true;
    private boolean isCamera = true;
    private boolean isHKZoom = true;
    private boolean isHeater = false;


    public void startFatigue() {
        isStopFatigue = false;
        if (isMove)
            new Thread(moveTask).start();
        if (isLift)
            new Thread(liftTask).start();
        if (isLight)
            new Thread(lightTask).start();
//        new Thread(ptzubTask).start();
        if (isCamera)
            new Thread(cameraTask).start();
        if (isHKZoom)
            new Thread(haikangZoomTask).start();
        if (isHeater)
            new Thread(fogTask).start();
        if (isPtzMove||isPtzFuyangMove)
            new Thread(ptzlrTask).start();
    }


    public void startLightFatigue() {
        isStopFatigue = false;
        new Thread(lightPtzTask).start();
        new Thread(ptzubTask).start();
    }


    public void stopFatigue() {


        isStopFatigue = true;

        controlPresenter.move_stop();
        controlPresenter.ptz_stop();
        controlPresenter.lift_stop();
        controlPresenter.light_back_off();
        controlPresenter.light_back_LX(0);
        controlPresenter.light_front_off();
        controlPresenter.light_front_LX(0);
        controlPresenter.ptz_light_LX(0);
        controlPresenter.lightBack_L1(0);
        controlPresenter.light_front_off_to_back(0);
        controlPresenter.ptz_heat_off();
        controlPresenter.ptz_lazer_off();
        if (All_id_Info.getInstance().getM_iLogID() >= 0)
            hcNetSdkLogin.stopRecord();


    }

    public void release() {
        if (!isStopFatigue)
            stopFatigue();
    }

    public void setMoveAction(String action) {
        switch (action) {
            case "top":
                new Thread(moveTopRunnable).start();
                break;
            case "bottom":
                new Thread(moveBottomRunnable).start();
                break;
            case "left":
                new Thread(moveLeftRunnable).start();
                break;
            case "right":
                new Thread(moveRightRunnable).start();
                break;
        }
    }

    public void setLiftAction(String action) {
        switch (action) {
            case "rise":
                new Thread(liftRiseRunnable).start();
                break;
            case "fall":
                new Thread(liftFallRunnable).start();
                break;

        }
    }

    public void setPtzAction(String action) {
        switch (action) {
            case "top":
                new Thread(ptzTopRunnable).start();
                break;
            case "bottom":
                new Thread(ptzBottomRunnable).start();
                break;
            case "left":
                new Thread(ptzLeftRunnable).start();
                break;
            case "right":
                new Thread(ptzRightRunnable).start();
                break;
        }
    }

    private void initFastFatigue() {
        moveTopRunnable = new Runnable() {
            @Override
            public void run() {
                controlPresenter.move_top();
                sleep(1000);
                controlPresenter.move_bottom();
                sleep(1000);
                controlPresenter.move_stop();
            }
        };
        moveBottomRunnable = new Runnable() {
            @Override
            public void run() {
                controlPresenter.move_bottom();
                sleep(1000);
                controlPresenter.move_top();
                sleep(1000);
                controlPresenter.move_stop();
            }
        };
        moveLeftRunnable = new Runnable() {
            @Override
            public void run() {
                controlPresenter.move_left();
                sleep(1000);
                controlPresenter.move_right();
                sleep(1000);
                controlPresenter.move_stop();
            }
        };
        moveRightRunnable = new Runnable() {
            @Override
            public void run() {
                controlPresenter.move_right();
                sleep(1000);
                controlPresenter.move_left();
            }
        };
        liftRiseRunnable = new Runnable() {
            @Override
            public void run() {
                controlPresenter.lift_rise();
                sleep(1000);
                controlPresenter.lift_fall();
                sleep(1000);
                controlPresenter.lift_stop();
            }
        };
        liftFallRunnable = new Runnable() {
            @Override
            public void run() {
                controlPresenter.lift_fall();
                sleep(1000);
                controlPresenter.lift_rise();
                sleep(1000);
                controlPresenter.lift_stop();
            }
        };
        ptzTopRunnable = new Runnable() {
            @Override
            public void run() {
                controlPresenter.ptz_top();
                sleep(1000);
                controlPresenter.ptz_bottom();
                sleep(1000);
                controlPresenter.ptz_stop();
            }
        };
        ptzBottomRunnable = new Runnable() {
            @Override
            public void run() {
                controlPresenter.ptz_bottom();
                sleep(1000);
                controlPresenter.ptz_top();
                sleep(1000);
                controlPresenter.ptz_stop();
            }
        };
        ptzLeftRunnable = new Runnable() {
            @Override
            public void run() {
                controlPresenter.ptz_left();
                sleep(1000);
                controlPresenter.ptz_right();
                sleep(1000);
                controlPresenter.ptz_stop();
            }
        };
        ptzRightRunnable = new Runnable() {
            @Override
            public void run() {
                controlPresenter.ptz_right();
                sleep(1000);
                controlPresenter.ptz_left();
                sleep(1000);
                controlPresenter.ptz_stop();
            }
        };
    }

    private void initFatigue() {
        moveTask = new Runnable() {
            @Override
            public void run() {
                while (!isStopFatigue) {
                    log("run moveTask");
//                    controlPresenter.move_speed(6);
                    controlPresenter.move_top();
                    sleep(moveTopBottomTime);
                    if (isStopFatigue)
                        return;
                    controlPresenter.move_stop();
                    sleep(1000 * 5);
                    if (isStopFatigue)
                        return;

                    controlPresenter.move_bottom();
                    sleep(moveTopBottomTime);
                    if (isStopFatigue)
                        return;
                    controlPresenter.move_stop();
                    sleep(1000 * 5);
                    if (isStopFatigue)
                        return;

                    controlPresenter.move_left();
                    sleep(moveLeftRightTime);
                    if (isStopFatigue)
                        return;
                    controlPresenter.move_stop();
                    sleep(1000 * 5);
                    if (isStopFatigue)
                        return;

                    controlPresenter.move_right();
                    sleep(moveLeftRightTime);
                    if (isStopFatigue)
                        return;
                    controlPresenter.move_stop();
                    sleep(1000 * 5);
                    if (isStopFatigue)
                        return;

                    if (isOpenLihe) {
                        controlPresenter.move_on();
                        isOpenLihe = false;
                    } else {
                        controlPresenter.move_off();
                        isOpenLihe = true;
                    }
                }
            }
        };
        liftTask = new Runnable() {
            @Override
            public void run() {

                while (!isStopFatigue) {
                    log("run liftTask");
                    sleep(800);
                    controlPresenter.lift_fall();
                    log("lift_fall as");
                    sleep(liftTime * 1000);
                    if (isStopFatigue)
                        return;
                    controlPresenter.lift_stop();
                    log("lift_stop as");
                    sleep(lift_wait_time * 1000);
                    if (isStopFatigue)
                        return;

                    controlPresenter.lift_rise();
                    sleep(liftTime * 1000);
                    if (isStopFatigue)
                        return;
                    controlPresenter.lift_stop();
                    sleep(lift_wait_time * 1000);
                    if (isStopFatigue)
                        return;
                }
            }

        };

        lightTask = new Runnable() {
            @Override
            public void run() {
                while (!isStopFatigue) {
                    log("run lightTask");
                    for (int i = 0; i < 10; i++) {
                        controlPresenter.light_front_LX(10);
                        controlPresenter.light_front_on();
                        controlPresenter.light_back_LX(10);
                        controlPresenter.light_back_on();
                        controlPresenter.ptz_light_LX(10);
                        controlPresenter.ptz_lazer_on();
                        controlPresenter.light_front_off_to_back(10);
                        controlPresenter.lightBack_L1(10);
                        sleep(100);
//                        ptzTurnBug();
                        sleep(1000 * 5);
                        if (isStopFatigue)
                            break;

                        controlPresenter.light_front_off();
                        controlPresenter.light_back_off();
                        controlPresenter.ptz_light_LX(0);
                        controlPresenter.ptz_lazer_off();
                        controlPresenter.light_front_off_to_back(0);
                        controlPresenter.lightBack_L1(0);

                        sleep(100);
//                        ptzTurnBug();
                        sleep(1000 * 3);
                        if (isStopFatigue)
                            break;
                    }
                    if (isStopFatigue)
                        return;

                    for (int a = 0; a < 10; a++) {
                        for (int i = 1; i < 11; i++) {
                            controlPresenter.light_front_LX(i);
                            controlPresenter.light_front_on();
                            controlPresenter.light_back_LX(i);
                            controlPresenter.light_back_on();
                            controlPresenter.ptz_light_LX(i);
                            controlPresenter.light_front_off_to_back(i);
                            controlPresenter.lightBack_L1(i);

                            sleep(100);
//                            ptzTurnBug();
                            sleep(1000 * 1);
                            if (isStopFatigue)
                                break;
                        }
                        sleep(1000 * 5);
                        if (isStopFatigue)
                            break;
                        for (int i = 9; i >= 0; i--) {
                            controlPresenter.light_front_LX(i);
                            controlPresenter.light_front_on();
                            controlPresenter.light_back_LX(i);
                            controlPresenter.light_back_on();
                            controlPresenter.ptz_light_LX(i);
                            controlPresenter.light_front_off_to_back(i);
                            controlPresenter.lightBack_L1(i);

                            sleep(100);
//                            ptzTurnBug();
                            sleep(1000 * 1);
                            if (isStopFatigue)
                                break;
                        }
                        sleep(1000 * 5);
                        if (isStopFatigue)
                            break;
                    }
                    if (isStopFatigue)
                        return;
                }
            }
        };
        lightPtzTask = new Runnable() {
            @Override
            public void run() {
                while (!isStopFatigue) {
                    log("run lightTask");
                    sleep(150);
                    for (int i = 0; i < 10; i++) {
//                        controlPresenter.light_front_LX(10);
//                        controlPresenter.light_front_on();
//                        controlPresenter.light_back_LX(10);
//                        controlPresenter.light_back_on();
                        controlPresenter.ptz_light_LX(10);
                        controlPresenter.ptz_lazer_on();
                        sleep(100);
//                        ptzTurnBug();
                        sleep(1000 * 5);
                        if (isStopFatigue)
                            break;

//                        controlPresenter.light_front_off();
//                        controlPresenter.light_back_off();
                        controlPresenter.ptz_light_LX(0);
                        controlPresenter.ptz_lazer_off();

                        sleep(100);
//                        ptzTurnBug();
                        sleep(1000 * 3);
                        if (isStopFatigue)
                            break;
                    }
                    if (isStopFatigue)
                        return;

                    for (int a = 0; a < 10; a++) {
                        for (int i = 1; i < 11; i++) {
//                            controlPresenter.light_front_LX(i);
//                            controlPresenter.light_front_on();
//                            controlPresenter.light_back_LX(i);
//                            controlPresenter.light_back_on();
                            controlPresenter.ptz_light_LX(i);

                            sleep(100);
//                            ptzTurnBug();
                            sleep(1000 * 1);
                            if (isStopFatigue)
                                break;
                        }
                        sleep(1000 * 5);
                        if (isStopFatigue)
                            break;
                        for (int i = 9; i >= 0; i--) {
//                            controlPresenter.light_front_LX(i);
//                            controlPresenter.light_front_on();
//                            controlPresenter.light_back_LX(i);
//                            controlPresenter.light_back_on();
                            controlPresenter.ptz_light_LX(i);

                            sleep(100);
//                            ptzTurnBug();
                            sleep(1000 * 1);
                            if (isStopFatigue)
                                break;
                        }
                        sleep(1000 * 5);
                        if (isStopFatigue)
                            break;
                    }
                    if (isStopFatigue)
                        return;
                }
            }
        };

        ptzlrTask = new Runnable() {
            @Override
            public void run() {
                while (!isStopFatigue) {
                    log("run ptzlrTask");
                    if (isPtzMove)
                        controlPresenter.ptz_left();
                    sleep(150);
                    ptzMoveUpBottom();
                    for (int i = 1; i <= ptzlrLeftTime; i++) {

                        if (i % (ptzubTime + 2) == 0) {
                            ptzMoveUpBottom();
                        }
                        sleep(1000);
                        if (isStopFatigue)
                            break;
                    }
                    if (isStopFatigue)
                        return;
                    controlPresenter.ptz_stop();
                    sleep(1000 * 2);
                    if (isStopFatigue)
                        return;

                    if (isPtzMove)
                        controlPresenter.ptz_right();
                    sleep(150);
                    ptzMoveUpBottom();
                    for (int i = 1; i <= ptzlrRightTime; i++) {
                        if (i % (ptzubTime + 2) == 0) {
                            ptzMoveUpBottom();
                        }
                        sleep(1000);
                        if (isStopFatigue)
                            break;
                    }
                    if (isStopFatigue)
                        return;
                    controlPresenter.ptz_stop();
                    sleep(1000 * 2);
                    if (isStopFatigue)
                        return;


                    if (isPtzMove)
                        controlPresenter.ptz_reset();
                    sleep(ptzResetTime);
                    if (isStopFatigue)
                        return;
                    isPtzFirst = true;
                }
            }
        };

        ptzubTask = new Runnable() {
            @Override
            public void run() {
                while (!isStopFatigue) {
                    log("run ptzubTask");
                    ptzMoveUpBottom();
                    sleep(ptzubTime * 1000);
                    if (isStopFatigue)
                        break;

                }

            }
        };

        cameraTask = new Runnable() {
            @Override
            public void run() {
                while (!isStopFatigue) {
                    log("run cameraTask");
                    if (listener != null)
                        listener.changeCamera();

                    record();
                    sleep(cameraChangeTime);
                    if (isStopFatigue)
                        return;

                    if (listener != null)
                        listener.changeCamera();
                    sleep(cameraChangeTime);
                    if (isStopFatigue)
                        return;
                }
            }
        };

        haikangZoomTask = new Runnable() {
            @Override
            public void run() {
                while (!isStopFatigue) {
                    log("run haikangZoomTask");
                    ptzControlPresenter.zoom_out(true);
                    sleep(hkZoomTime * 1000);
                    if (isStopFatigue)
                        return;

                    ptzControlPresenter.zoom_in(true);
                    sleep(hkZoomTime * 1000);
                    if (isStopFatigue)
                        return;
                }
            }
        };

        fogTask = new Runnable() {
            @Override
            public void run() {
                while (!isStopFatigue) {
                    log("run fogTask");
                    controlPresenter.ptz_heat_on();
                    sleep(100);
//                    ptzTurnBug();
                    sleep(1000 * 60 * 1);
                    if (isStopFatigue)
                        return;

                    controlPresenter.ptz_heat_off();
                    sleep(100);
//                    ptzTurnBug();
                    sleep(fogTestTime);
                    if (isStopFatigue)
                        return;
                }
            }
        };

    }

    private void ptzMoveUpBottom() {
        if (!isPtzFuyangMove)
            return;

        if (isUp) {
            controlPresenter.ptz_bottom();
            isUp = false;
        } else {
            controlPresenter.ptz_top();
            isUp = true;
        }
    }

    private void ptzTurnBug() {
        /*if (isPtzTurning) {
            if (turnDirection == 0)
                controlPresenter.ptz_left();
            else
                controlPresenter.ptz_right();
        }*/
    }

    private boolean isRecordPause;

    private void record() {
        if (!isRecord) {
            while (All_id_Info.getInstance().getM_iLogID() < 0) {
                sleep(1);
                if (isStopFatigue)
                    break;
            }
            if (isStopFatigue)
                return;
            sleep(2000);
            hcNetSdkLogin.record();
            isRecord = true;
        } else {
            if (All_id_Info.getInstance().isRecordPause()) {
                All_id_Info.getInstance().setRecordPause(false);
            } else {

                All_id_Info.getInstance().setRecordPause(true);
            }
        }
    }

    public boolean isMove() {
        return isMove;
    }

    public void setMove(boolean move) {
        isMove = move;
    }

    public boolean isLift() {
        return isLift;
    }

    public void setLift(boolean lift) {
        isLift = lift;
    }

    public boolean isLight() {
        return isLight;
    }

    public void setLight(boolean light) {
        isLight = light;
    }

    public boolean isPtzMove() {
        return isPtzMove;
    }

    public void setPtzMove(boolean ptzMove) {
        isPtzMove = ptzMove;
    }

    public boolean isCamera() {
        return isCamera;
    }

    public void setCamera(boolean camera) {
        isCamera = camera;
    }

    public boolean isHKZoom() {
        return isHKZoom;
    }

    public void setHKZoom(boolean HKZoom) {
        isHKZoom = HKZoom;
    }

    public boolean isHeater() {
        return isHeater;
    }

    public void setHeater(boolean heater) {
        isHeater = heater;
    }

    public int getLiftTime() {
        return liftTime;
    }

    public void setLiftTime(int liftTime) {
        this.liftTime = liftTime;
    }

    public int getPtzlrLeftTime() {
        return ptzlrLeftTime;
    }

    public void setPtzlrLeftTime(int ptzlrLeftTime) {
        this.ptzlrLeftTime = ptzlrLeftTime;
    }

    public int getPtzlrRightTime() {
        return ptzlrRightTime;
    }

    public void setPtzlrRightTime(int ptzlrRightTime) {
        this.ptzlrRightTime = ptzlrRightTime;
    }

    public int getHkZoomTime() {
        return hkZoomTime;
    }

    public void setHkZoomTime(int hkZoomTime) {
        this.hkZoomTime = hkZoomTime;
    }

    public int getMoveTopBottomTime() {
        return moveTopBottomTime;
    }

    public void setMoveTopBottomTime(int moveTopBottomTime) {
        this.moveTopBottomTime = moveTopBottomTime;
    }

    public int getMoveLeftRightTime() {
        return moveLeftRightTime;
    }

    public void setMoveLeftRightTime(int moveLeftRightTime) {
        this.moveLeftRightTime = moveLeftRightTime;
    }

    public long getCameraChangeTime() {
        return cameraChangeTime;
    }

    public void setCameraChangeTime(long cameraChangeTime) {
        this.cameraChangeTime = cameraChangeTime;
    }

    public long getPtzResetTime() {
        return ptzResetTime;
    }

    public void setPtzResetTime(long ptzResetTime) {
        this.ptzResetTime = ptzResetTime;
    }

    public boolean isPtzFuyangMove() {
        return isPtzFuyangMove;
    }

    public void setPtzFuyangMove(boolean ptzFuyangMove) {
        isPtzFuyangMove = ptzFuyangMove;
    }

    public int getPtzubTime() {
        return ptzubTime;
    }

    public void setPtzubTime(int ptzubTime) {
        this.ptzubTime = ptzubTime;
    }

    public long getFogTestTime() {
        return fogTestTime;
    }

    public void setFogTestTime(long fogTestTime) {
        this.fogTestTime = fogTestTime;
    }

    public long getLift_wait_time() {
        return lift_wait_time;
    }

    public void setLift_wait_time(long lift_wait_time) {
        this.lift_wait_time = lift_wait_time;
    }

    public interface OnChangeCameraListener {
        void changeCamera();
    }

    private OnChangeCameraListener listener;

    public void setOnChangeCameraListener(OnChangeCameraListener listener) {
        this.listener = listener;
    }

}
