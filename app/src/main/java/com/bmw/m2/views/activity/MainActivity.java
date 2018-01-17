package com.bmw.m2.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.bmw.m2.Constant;
import com.bmw.m2.R;
import com.bmw.m2.VLCApplication;
import com.bmw.m2.jna.SystemTransformByJNA;
import com.bmw.m2.jna.SystemTransformJNAInstance;
import com.bmw.m2.model.All_id_Info;
import com.bmw.m2.model.CarRemoteDeviecInfo;
import com.bmw.m2.model.FileInfo;
import com.bmw.m2.model.Login_info;
import com.bmw.m2.model.RecordTaskInfo;
import com.bmw.m2.model.RemoteDeviceInfo;
import com.bmw.m2.presenter.CarControlPresenter;
import com.bmw.m2.presenter.CharacterOverLapingPresenter;
import com.bmw.m2.presenter.ControlPresenter;
import com.bmw.m2.presenter.HCNetSdkLogin;
import com.bmw.m2.presenter.PtzControlPresenter;
import com.bmw.m2.presenter.VideoCutPresenter;
import com.bmw.m2.presenter.VlcPresenter;
import com.bmw.m2.presenter.impl.CarControlPresentImpl;
import com.bmw.m2.presenter.impl.CharacterOverLapingImpl;
import com.bmw.m2.presenter.impl.ControlPresentImpl;
import com.bmw.m2.presenter.impl.HCNetSdkLoginImpl;
import com.bmw.m2.presenter.impl.PtzControlPresentImpl;
import com.bmw.m2.presenter.impl.VideoCutPresentImpl;
import com.bmw.m2.presenter.impl.VlcPresentImpl;
import com.bmw.m2.utils.ActivityUtil;
import com.bmw.m2.utils.FileUtil;
import com.bmw.m2.utils.FragmentUtil;
import com.bmw.m2.utils.HkUtils;
import com.bmw.m2.utils.InternetUtil;
import com.bmw.m2.utils.MyViewUtil;
import com.bmw.m2.utils.NumberUtil;
import com.bmw.m2.utils.ThreadUtil;
import com.bmw.m2.utils.ThrowUtil;
import com.bmw.m2.utils.TimeUtil;
import com.bmw.m2.views.dialog.Capture_tishi_Dialog;
import com.bmw.m2.views.fragment.BiaodingFragment;
import com.bmw.m2.views.fragment.ChooseWifiFragment;
import com.bmw.m2.views.fragment.DialogBiaojiMutiFragment;
import com.bmw.m2.views.fragment.DialogNormalFragment;
import com.bmw.m2.views.fragment.MyPopupWindow;
import com.bmw.m2.views.fragment.OnDialogFragmentClickListener;
import com.bmw.m2.views.fragment.PiLaoSetFragment;
import com.bmw.m2.views.fragment.RiZhiSetFragment;
import com.bmw.m2.views.service.MyIntentService;
import com.bmw.m2.views.view.CompositeImageText;
import com.bmw.m2.views.view.DirectionButton;
import com.bmw.m2.views.view.RollButton;
import com.bmw.m2.views.viewImpl.PreviewImpl;
import com.hikvision.netsdk.NET_DVR_TIME;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static com.bmw.m2.utils.FragmentUtil.showDialogFragment;

public class MainActivity extends BaseActivity implements PreviewImpl {

    @Bind(R.id.preview_right_menu_liftControl)
    RollButton mLiftControl;
    @Bind(R.id.preview_right_menu_moveControl)
    DirectionButton mMoveControl;
    @Bind(R.id.preview_ptzControl)
    DirectionButton mPtzControl;
    @Bind(R.id.preview_lihe)
    FrameLayout liheBtn;
    @Bind(R.id.preview_jiguang)
    ImageView jiguangBtn;
    @Bind(R.id.iv_lihe_inhibit)
    ImageView liheInhibitBtn;
    @Bind(R.id.preview_resultInfo)
    TextView mResultInfo;
    @Bind(R.id.preview_resultInfo_car)
    TextView mResultInfo_car;
    @Bind(R.id.surface_main)
    SurfaceView mSurfaceView;
    @Bind(R.id.surface_vlc)
    SurfaceView mSurfaceView_vlc;
    @Bind(R.id.preview_Scrollview)
    ScrollView scrollView;
    //    @Bind(R.id.shouxian_auto)
//    Switch shouxian_auto_sw;
//    @Bind(R.id.fangxian_auto)
//    Switch fangxian_auto_sw;
    @Bind(R.id.preview_surface_container)
    FrameLayout surfaceContainer;
    @Bind(R.id.iv_speed)
    ImageView iv_Speed;
    @Bind(R.id.iv_fog)
    ImageView iv_fog;
    @Bind(R.id.iv_move_auto)
    ImageView iv_move_auto;
    @Bind(R.id.iv_shouxian)
    ImageView iv_shouxian;
    @Bind(R.id.iv_test)
    ImageView iv_test;

    @Bind(R.id.cit_record)
    CompositeImageText cit_record;
    @Bind(R.id.iv_jimiqi)
    ImageView iv_jimiqi;
    @Bind(R.id.tv_jimiqi)
    TextView tv_jimiqi;
    @Bind(R.id.cit_quanxian)
    CompositeImageText cit_quanxian;

    @Bind(R.id.tv_battery_device)
    TextView tv_battery_device;
    @Bind(R.id.tv_battery_terminal)
    TextView tv_battery_terminal;
    @Bind(R.id.cit_protected)
    CompositeImageText cit_protectDevice;


    private int jimi_last;

    private ControlPresenter controlPresenter;
    private CarControlPresenter carControlPresenter;
    private boolean isPtzModeDirection;
    private boolean isLiheOn = false;
    private boolean isJiguangOn;
    private boolean isLiheOn_shouxian;
    private boolean isDianresiOn;
    private HCNetSdkLogin hcnetSdkLoginPresenter;
    private VlcPresenter vlcPresenter;
    private boolean mIsOpenLiftBiaoding;
    private boolean mIsOpenPtzBiaoding;
    private int mShouxianSpeed = 2;
    private boolean isOpenMoveAuto;
    private PtzControlPresenter ptzControlPresenter;
    private int lastMoveAction;
    private boolean isBackCameraNow = false;
    private boolean mIsOpenAutoModel;
    private int mMovePressCount;
    private FatigueTest fatigueTest;
    private boolean isOpenFatigueTest;
    private MyPopupWindow mLightPopupWindow;
    private MyPopupWindow mSpeedPopupWindow;
    private MyPopupWindow mShouxianPopupWindow;
    private LinearLayout shouxianBtn, fangxianBtn;
    private MyPopupWindow mTestPopupWindow;
    private MyPopupWindow mPtzSetPlacePopupWindow;
    private MyPopupWindow mZitaiLeftPopupWindow;
    private MyPopupWindow mZitaiRightPopupWindow;

    private ImageView mIv_zitai_roll;
    private ImageView mIv_zitai_picth;
    private TextView mTv_lift_info;
    private TextView mTv_ptz_info;
    private TextView mTv_zitai_roll;
    private TextView mTv_zitai_picth;

    private boolean isWriteRiZhi;
    private long riZhiWaitTime = 0;

    //版头任务名称、监测位置
    private String mTaskName, mTaskPlace;
    private Timer recordTimer;
    private TimerTask recordTimerTask;
    private long recordTime;
    private long recordLastTime;
    private boolean isAddNewRecordKanBan;

    private boolean isResetRanging;
    private String mStartWell;
    private String mEndWell;
    //    private boolean mIsRecordHasData;
    private boolean isRecordOpen;
    private VideoCutPresenter videoCutPresenter;
    private CharacterOverLapingPresenter mCharOverPresent;
    private String[] mBiaojiTexts;
    private String m_Machine_protect;


    public ControlPresenter getContropresenter() {
        return controlPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ThrowUtil.log("开始启动程序！");

        VLCApplication.getSharedPreferences().edit().putBoolean(Constant.KEY_MYDEVICE_UPDATE_IS_OPEN, false).commit();
        VLCApplication.getSharedPreferences().edit().putInt(Constant.KEY_MYDEVICE_UPDATE_OBJECT_WHO, 0).commit();

        mBiaojiTexts = new String[4];
        String[] strings = getResources().getStringArray(R.array.environment_name);

       /* mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ThrowUtil.log("X = "+mSurfaceView.getX()+" Y = "+mSurfaceView.getY());
                log("getPivotX = "+mSurfaceView.getPivotX()+" getPivotY = "+mSurfaceView.getPivotY());
                log("getRotationX = "+mSurfaceView.getRotationX()+" getRotationY = "+mSurfaceView.getRotationY());
                log("getScrollX = "+mSurfaceView.getScrollX()+" getScrollY = "+mSurfaceView.getScrollY());

                return true;
            }
        });*/
        controlPresenter = new ControlPresentImpl(this);
        carControlPresenter = new CarControlPresentImpl(this);
        ptzControlPresenter = new PtzControlPresentImpl();
        videoCutPresenter = new VideoCutPresentImpl(this);
        mCharOverPresent = new CharacterOverLapingImpl();

        loginHK();

        loginVLC();

        initPiLaoTest();

        initControl();

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            boolean isFillScreen;
            long lastClicTime = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (System.currentTimeMillis() - lastClicTime <= 500) {
                        isFillScreen = surfaceFillScreen(isFillScreen);
                        lastClicTime = 0;
                    } else
                        lastClicTime = System.currentTimeMillis();
                }
                return true;
            }
        });

        initBroadcastReceiver();

    }


    private void initRecordTimer() {
        recordTimer = new Timer();
        recordTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (!All_id_Info.getInstance().isRecordPause()) {

                    //断线情况下，中断录像计时
//                    if (mIsRecordHasData && (System.currentTimeMillis() - recordLastTime > 1500)) {
//                        mIsRecordHasData = false;
//                        log("录像计时中断！");
//                    }

                    if (cit_record != null) {
                        cit_record.post(new Runnable() {
                            @Override
                            public void run() {
                                cit_record.setText(TimeUtil.formatTime(recordTime * 1000, true));
                            }
                        });
                    }

//                    if (mIsRecordHasData)
                    recordTime++;

                    if (isAddNewRecordKanBan && recordTime >= 30) {
                        stopRecord();
                    }
                }
            }
        };
        recordTimer.schedule(recordTimerTask, 0, 1000);
    }

    private boolean surfaceFillScreen(boolean isFillScreen) {
        if (!isFillScreen) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            surfaceContainer.setLayoutParams(layoutParams);
            isFillScreen = true;
        } else {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.BELOW, R.id.preview_top_menu_container);
            layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.preview_right_menu_container);
            layoutParams.addRule(RelativeLayout.ABOVE, R.id.preview_bottom_menu_container);
            layoutParams.setMargins(0, 0, 5, 5);
            surfaceContainer.setLayoutParams(layoutParams);
            isFillScreen = false;
        }

        return isFillScreen;
    }

    private void initPiLaoTest() {
        fatigueTest = new FatigueTest(controlPresenter, hcnetSdkLoginPresenter, vlcPresenter, ptzControlPresenter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        log(" onresume");
        if (isBackCameraNow && vlcPresenter != null) {
            vlcPresenter.start();
        }

//        FileTurnStreamTest fileHeBing = new FileTurnStreamTest();
//        fileHeBing.parseFile(FileUtil.getSDPath()+FileInfo.videoPath+"001.avi",FileUtil.getSDPath()+FileInfo.videoPath+"new123.avi");

    }


    private void loginVLC() {
        mSurfaceView_vlc.setVisibility(View.VISIBLE);
        vlcPresenter = new VlcPresentImpl(mSurfaceView_vlc);
//        vlcPresenter.start();
    }

    private void loginHK() {
        mSurfaceView.setVisibility(View.VISIBLE);
        hcnetSdkLoginPresenter = new HCNetSdkLoginImpl(this, mSurfaceView, this);
        hcnetSdkLoginPresenter.setCanReConnect(true);
    }

    @Override
    protected void onDestroy() {

        SystemTransformJNAInstance.getInstance().SYSTRANS_Stop(SystemTransformByJNA.handle.getValue());
        SystemTransformJNAInstance.getInstance().SYSTRANS_Release(SystemTransformByJNA.handle.getValue());
        super.onDestroy();
        ButterKnife.unbind(this);
        unregisterReceiver(system_battery_receiver);
        MyIntentService.stopIntentService(this);
        if (hcnetSdkLoginPresenter != null) {
            hcnetSdkLoginPresenter.setCanReConnect(false);
            hcnetSdkLoginPresenter.release();
        }
        if (vlcPresenter != null)
            vlcPresenter.release();
        controlPresenter.release();
        VLCApplication.clearBitmapCache();
        removeALLActivity();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.KEYCODE_BACK || event.getAction() == KeyEvent.KEYCODE_HOME)
            return true;

        return false;
    }

    private void initControl() {
        mLiftControl.setOnRollBtnChangeListener(new RollButton.OnRollBtnChangeListener() {
            @Override
            public void onTouchChange(int direction, int progress) {
                if (direction == 0) {
                    controlPresenter.lift_fall();
                }
                if (direction == 1) {
                    controlPresenter.lift_rise();
                }
            }

            @Override
            public void stopTouch() {
                controlPresenter.lift_stop();
            }
        });

        mMoveControl.setOnItemClickListener(new DirectionButton.OnItemClickListener() {
            @Override
            public void topClick() {
                if (isOpenFatigueTest) {
                    fatigueTest.fastTestTop();
                    return;
                }

                firstMoveAutoPress(0);
                controlPresenter.move_top();
                lastMoveAction = 0;
                if (mIsOpenAutoModel) {
                    carControlPresenter.car_shouxian_fang();
                }
            }

            @Override
            public void bottomClick() {
                if (isOpenFatigueTest) {
                    fatigueTest.fastTestBottom();
                    return;
                }
                firstMoveAutoPress(1);
                controlPresenter.move_bottom();
                lastMoveAction = 1;
                if (mIsOpenAutoModel) {
                    carControlPresenter.car_shouxian_shou();
                }
            }

            @Override
            public void leftClick() {
                if (isOpenFatigueTest) {
                    fatigueTest.fastTestLeft();
                    return;
                }
                firstMoveAutoPress(2);
                controlPresenter.move_left();
                lastMoveAction = 2;
                if (mIsOpenAutoModel) {
                    carControlPresenter.car_shouxian_stop();
                    lastMoveAction = -1;
                }
            }

            @Override
            public void rightClick() {
                if (isOpenFatigueTest) {
                    fatigueTest.fastTestRight();
                    return;
                }
                firstMoveAutoPress(3);
                controlPresenter.move_right();
                lastMoveAction = 3;
                if (mIsOpenAutoModel) {
                    carControlPresenter.car_shouxian_stop();
                    lastMoveAction = -1;
                }
            }

            @Override
            public void stopTouch() {
                if (isOpenFatigueTest) {
                    return;
                }
//                log("stop touch direction!");
                if (!isOpenMoveAuto) {
                    stopMove();
                } else {
                    if (lastMoveAction == 0) {
                        controlPresenter.move_top();
                        if (mMovePressCount == 2) {
                            stopMove();
                            mMovePressCount = 1;
                        }
                    } else if (lastMoveAction == 1) {
                        controlPresenter.move_bottom();
                        if (mMovePressCount == 2) {
                            stopMove();
                            mMovePressCount = 1;
                        }
                    } else {
                        stopMove();
                        mMovePressCount = 0;
                    }
                }
            }
        });

        mPtzControl.setOnItemClickListener(new DirectionButton.OnItemClickListener() {
            @Override
            public void topClick() {
                if (isPtzModeDirection)
                    controlPresenter.ptz_set_top();
                else
                    controlPresenter.ptz_top();
            }

            @Override
            public void bottomClick() {
                if (isPtzModeDirection)
                    controlPresenter.ptz_set_round();
                else
                    controlPresenter.ptz_bottom();
            }

            @Override
            public void leftClick() {
                if (isPtzModeDirection)
                    controlPresenter.ptz_set_left();
                else
                    controlPresenter.ptz_left();
            }

            @Override
            public void rightClick() {
                if (isPtzModeDirection)
                    controlPresenter.ptz_set_right();
                else
                    controlPresenter.ptz_right();
            }

            @Override
            public void stopTouch() {
                log("stop touch direction!");
                controlPresenter.ptz_stop();
            }
        });


        if (controlPresenter != null)
            controlPresenter.move_speed(5);

        carControlPresenter.car_fangxian_liju(mShouxianSpeed);
        carControlPresenter.car_shouxian_speed(mShouxianSpeed);
        controlPresenter.move_speed(5);
    }

    private void stopMove() {
        controlPresenter.move_stop();
        lastMoveAction = -1;
        if (mIsOpenAutoModel) {
            carControlPresenter.car_shouxian_stop();
        }
    }

    private void pressCount(int who) {
        if (!isOpenMoveAuto)
            return;
        if (lastMoveAction == who)
            mMovePressCount++;
        else
            mMovePressCount = 1;
    }

    private void firstMoveAutoPress(int who) {
        if (isOpenMoveAuto) {
            if (mMovePressCount == 0)
                mMovePressCount = 1;
            else
                pressCount(who);
        }
    }

    @Override
    public void loginSuccessful() {

        if (All_id_Info.getInstance().getM_iLogID() >= 0) {
            if (mCharOverPresent != null) {
                mBiaojiTexts[0] = "";
                mBiaojiTexts[1] = "";
                mBiaojiTexts[2] = "";
                mBiaojiTexts[3] = "";
                mCharOverPresent.setSingleSign(mBiaojiTexts);
                mCharOverPresent.setTongDaoName("", true);
                mCharOverPresent.setGpsAndRang("00.00", 0, 0);
            }
        }
    }


    @OnClick({
            R.id.preview_changeCamera
            , R.id.preview_ptz_reset
            , R.id.preview_lihe
            , R.id.preview_jiguang
            , R.id.iv_speed
            , R.id.iv_light
            , R.id.iv_shouxian
            , R.id.iv_fog
            , R.id.iv_move_auto
            , R.id.iv_test
            , R.id.cit_record
            , R.id.cit_capture
            , R.id.ll_jimiqi
            , R.id.iv_wifi
            , R.id.iv_biaoding
            , R.id.cit_pictures
            , R.id.cit_play_back
            , R.id.cit_setting
            , R.id.iv_yuntai_set_place
            , R.id.iv_zitai
            , R.id.cit_close_app
            , R.id.iv_rizhi
            , R.id.cit_biaozhu
            , R.id.cit_protected
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cit_protected:

                showMachineResetDialog(m_Machine_protect);
                break;
            case R.id.cit_biaozhu:
                btnClickBiaoji();
                break;

            case R.id.iv_rizhi:
                RiZhiSetFragment riZhiSetFragment = RiZhiSetFragment.getInstance(isWriteRiZhi, riZhiWaitTime);
                riZhiSetFragment.setListener(new RiZhiSetFragment.OnRiZhiValueChangeListener() {


                    @Override
                    public void setIsWrite(boolean isWrite) {
                        isWriteRiZhi = isWrite;
                    }

                    @Override
                    public void setTime(long time) {
                        riZhiWaitTime = time;
                    }
                });
                FragmentUtil.showDialogFragment(getSupportFragmentManager(), riZhiSetFragment, "RiZhiSetFragment");

                break;

            case R.id.cit_close_app:
                removeALLActivity();
                break;
            case R.id.iv_zitai:
                if (mZitaiLeftPopupWindow == null || mZitaiRightPopupWindow == null) {
                    initZitaiLeftPopupWindow();
                    initZitaiRightPopupWindow();
                }
//                mResultInfo_car.setVisibility(View.VISIBLE);
//                mResultInfo.setVisibility(View.VISIBLE);
                showOnePopupWindow(mZitaiLeftPopupWindow, new MyPopupWindow[0]);
                showOnePopupWindow(mZitaiRightPopupWindow, new MyPopupWindow[0]);

                break;
            case R.id.iv_yuntai_set_place:
                if (mPtzSetPlacePopupWindow == null) {
                    initPtzSetPlacePopupWindow();
                }
                showOnePopupWindow(mPtzSetPlacePopupWindow, mShouxianPopupWindow);

                break;
            case R.id.cit_setting:
                ActivityUtil.goToSettingActivity(this);
                break;
            case R.id.cit_pictures:
                ActivityUtil.goToFileShowActivity(this, true);
                break;
            case R.id.cit_play_back:
                ActivityUtil.goToFileShowActivity(this, false);
                break;
            case R.id.iv_biaoding:
                setBiaoDing();
                break;
            case R.id.iv_wifi:
                ChooseWifiFragment chooseWifi = new ChooseWifiFragment();
                showDialogFragment(getSupportFragmentManager(), chooseWifi, "ChooseWifiFragment");
                break;
            case R.id.ll_jimiqi:

//                FileTurnStreamTest fileHeBing = new FileTurnStreamTest();

                iv_jimiqi.setImageResource(R.drawable.circle_red);
                if (jimi_last == 0) {
                    carControlPresenter.car_jimi(1);
                    jimi_last = 1;
                } else {
                    carControlPresenter.car_jimi(0);
                    jimi_last = 0;
                }
                break;

            case R.id.cit_capture:
//                hcnetSdkLoginPresenter.capture();
                videoCutPresenter.capture(mTaskName, mTaskPlace, mStartWell, mEndWell);
                break;

            case R.id.cit_record:
                btnClickRecords();
//                hcnetSdkLoginPresenter.record();
                break;

            case R.id.iv_test:

//                showMachineResetDialog("电机保护复位");
                if (mTestPopupWindow == null) {
                    initTestPopupWindow();
                }
                showOnePopupWindow(mTestPopupWindow, mSpeedPopupWindow, mLightPopupWindow);
                break;

            case R.id.iv_move_auto:
                if (isOpenMoveAuto) {
                    isOpenMoveAuto = false;
                    iv_move_auto.setImageResource(R.drawable.ic_file_upload_gray_24dp);
                    stopMove();
                    mMovePressCount = 0;
                } else {
                    isOpenMoveAuto = true;
                    iv_move_auto.setImageResource(R.drawable.ic_file_upload_yellow_24dp);
                }
                break;
            case R.id.iv_fog:
                if (isDianresiOn) {
                    isDianresiOn = false;
                    iv_fog.setImageResource(R.drawable.ic_fog_close_64dp);
                    controlPresenter.ptz_heat_off();
                } else {
                    isDianresiOn = true;
                    iv_fog.setImageResource(R.drawable.ic_fog_open_64dp);
                    controlPresenter.ptz_heat_on();
                }
                break;
            case R.id.iv_shouxian:
                if (mShouxianPopupWindow == null) {
                    initShouxianPopupWindow();
                }
                showOnePopupWindow(mShouxianPopupWindow, mPtzSetPlacePopupWindow);
                break;

            case R.id.iv_light:
                if (mLightPopupWindow == null) {
                    initLightPopupWindow();
                }
                showOnePopupWindow(mLightPopupWindow, mSpeedPopupWindow, mTestPopupWindow);
                break;

            case R.id.iv_speed:
                if (mSpeedPopupWindow == null) {
                    initSpeedPopupWindow();
                }
                showOnePopupWindow(mSpeedPopupWindow, mLightPopupWindow, mTestPopupWindow);
                break;
            case R.id.preview_changeCamera:

                changeCamera();
                break;
            case R.id.preview_ptz_reset:
                controlPresenter.ptz_reset();
                break;

            case R.id.preview_lihe:
                if (isLiheOn) {
                    isLiheOn = false;
                    liheInhibitBtn.setVisibility(View.GONE);
                    controlPresenter.move_on();
                } else {
                    isLiheOn = true;
                    controlPresenter.move_off();
                    liheInhibitBtn.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.preview_jiguang:
                if (isJiguangOn) {
                    isJiguangOn = false;
                    jiguangBtn.setImageResource(R.drawable.ic_lazer_close_64dp);
                    controlPresenter.ptz_lazer_off();
                } else {
                    isJiguangOn = true;
                    jiguangBtn.setImageResource(R.drawable.ic_lazer_open_64dp);
                    controlPresenter.ptz_lazer_on();
                }
                break;

        }
    }


    private void btnClickBiaoji() {

        log("标记按钮！");
        DialogBiaojiMutiFragment dialogBiaojiFragment = DialogBiaojiMutiFragment.getInstance(
                mBiaojiTexts[0]
                , mBiaojiTexts[1]
                , mBiaojiTexts[2]
                , mBiaojiTexts[3]
        );
        dialogBiaojiFragment.setOnMutlSureClickListener(new DialogBiaojiMutiFragment.OnMutlSureClickListener() {
            @Override
            public void finish(
                    String value1
                    , String value2
                    , String value3
                    , String value4
            ) {
                mBiaojiTexts[0] = value1;
                mBiaojiTexts[1] = value2;
                mBiaojiTexts[2] = value3;
                mBiaojiTexts[3] = value4;
                mCharOverPresent.setSingleSign(mBiaojiTexts);
            }
        });
        showDialogFragment(getSupportFragmentManager(), dialogBiaojiFragment, "DialogBiaojiMutiFragment");
    }

    private void btnClickRecords() {
        log("录像按钮！");
        if (!isRecordOpen) {
            boolean isOpenRecordKanBan = VLCApplication.getSharedPreferences().getBoolean(Constant.KEY_IS_OPEN_RECORD_KANBAN, false);
            if (isOpenRecordKanBan) {
//                openRecordKanBan();
            } else {
                addRecordBanTou();
            }

        } else {
            boolean canPause = VLCApplication.getSharedPreferences().getBoolean(Constant.RECORD_CAN_PAUSE, false);
            if (canPause) {
                if (!All_id_Info.getInstance().isRecordPause()) {
//                    showRecordPauseDialog();
                } else {
//                    Login_info.isPause = false;
                    All_id_Info.getInstance().setRecordPause(false);
                    toast(getString(R.string.record_continue));
                }
            } else {
                stopRecord();
            }

        }
    }


    private void stopRecord() {
        videoCutPresenter.record(null, null, null, null, isAddNewRecordKanBan);
        mTaskName = null;
        mTaskPlace = null;
    }

//
//    private void showRecordPauseDialog() {
//        DialogRecordPauseFragment fragment = new DialogRecordPauseFragment();
//        fragment.setOnFragmentItemClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Login_info.isPause = true;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        recordComposite.setText(getString(R.string.alreadyPause));
//                        toast(getString(R.string.record_pause));
//                    }
//                });
//            }
//        }, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                videoCutPresenter.record(null, null, null, null,isAddNewRecordKanBan);
//                mTaskName = null;
//                mTaskPlace = null;
//            }
//        });
//        FragmentUtil.showDialogFragment(getSupportFragmentManager(), fragment, "DialogRecordPauseFragment");
//    }


    private void addRecordBanTou() {
        DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance("录像提示", "是否添加版头？", "是", "否", false);

        dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
            @Override
            public void sure() {
                startActivityForResult(new Intent(context(), RecordHeadEditActivity.class), 1);
            }

            @Override
            public void cancel() {
                videoCutPresenter.record(null, null, null, null, isAddNewRecordKanBan);
            }
        });
        showDialogFragment(getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");
    }

    private void initPtzSetPlacePopupWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.ptz_set_place_layout, null);
        CompositeImageText cit_ptz_set_up = (CompositeImageText) view.findViewById(R.id.cit_ptz_set_up);
        CompositeImageText cit_ptz_set_round = (CompositeImageText) view.findViewById(R.id.cit_ptz_set_circle);
        CompositeImageText cit_ptz_set_left = (CompositeImageText) view.findViewById(R.id.cit_ptz_set_left);
        CompositeImageText cit_ptz_set_right = (CompositeImageText) view.findViewById(R.id.cit_ptz_set_right);
        cit_ptz_set_up.setOnClickListener(ptzSetPlaceClickListerner);
        cit_ptz_set_round.setOnClickListener(ptzSetPlaceClickListerner);
        cit_ptz_set_left.setOnClickListener(ptzSetPlaceClickListerner);
        cit_ptz_set_right.setOnClickListener(ptzSetPlaceClickListerner);

        mPtzSetPlacePopupWindow = initLeftBottomPopupWidow(view);

    }

    private void initZitaiRightPopupWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.zitai_right_layout, null);
        mIv_zitai_picth = (ImageView) view.findViewById(R.id.iv_zitai_qingjiao);
        mTv_zitai_picth = (TextView) view.findViewById(R.id.tv_zitai_qingjiao);
        mTv_lift_info = (TextView) view.findViewById(R.id.tv_lift_info);
        mZitaiRightPopupWindow = initLeftBottomPopupWidow(view);
        int width = NumberUtil.dipId2px(this, R.dimen.zitai_width);
        int heigh = NumberUtil.dipId2px(this, R.dimen.zitai_height);
        int lx = getLocation(mSurfaceView)[0] + mSurfaceView.getWidth() - width;
        int ly = getLocation(mSurfaceView)[1];
        mZitaiRightPopupWindow = new MyPopupWindow(view, width, heigh, false, lx, ly);
    }

    private void initZitaiLeftPopupWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.zitai_left_layout, null);
        mIv_zitai_roll = (ImageView) view.findViewById(R.id.iv_zitai_roll);
        mTv_zitai_roll = (TextView) view.findViewById(R.id.tv_zitai_roll);
        mTv_ptz_info = (TextView) view.findViewById(R.id.tv_ptz_info);
        int width = NumberUtil.dipId2px(this, R.dimen.zitai_width);
        int heigh = NumberUtil.dipId2px(this, R.dimen.zitai_height);
        int lx = getLocation(mSurfaceView)[0];
        int ly = getLocation(mSurfaceView)[1];
        mZitaiLeftPopupWindow = new MyPopupWindow(view, width, heigh, false, lx, ly);
       /* mZitaiLeftPopupWindow.getmPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mResultInfo_car.setVisibility(View.INVISIBLE);
                mResultInfo.setVisibility(View.INVISIBLE);
            }
        });*/
    }


    private void initShouxianPopupWindow() {
        View shouxianView = LayoutInflater.from(this).inflate(R.layout.shouxian_layout, null);
        SeekBar sb_shouxian = (SeekBar) shouxianView.findViewById(R.id.sb_shouxian_speed);
        SeekBar sb_fangxian = (SeekBar) shouxianView.findViewById(R.id.sb_shouxian_liju);
        sb_shouxian.setMax(10);
        sb_shouxian.setProgress(mShouxianSpeed);
        sb_fangxian.setProgress(mShouxianSpeed);
        sb_shouxian.setOnSeekBarChangeListener(seekBarChangeListener);
        sb_fangxian.setOnSeekBarChangeListener(seekBarChangeListener);
        ((Switch) shouxianView.findViewById(R.id.sw_auto_model)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                mIsOpenAutoModel = isChecked;

                if (shouxianBtn != null && fangxianBtn != null) {
                    shouxianBtn.post(new Runnable() {
                        @Override
                        public void run() {
                            shouxianBtn.setEnabled(!isChecked);
                            fangxianBtn.setEnabled(!isChecked);
                            if (isChecked) {
                                iv_shouxian.setImageResource(R.drawable.ic_shouxian_yellow);
                            } else {
                                iv_shouxian.setImageResource(R.drawable.ic_shouxian_gray);
                            }
                        }
                    });
                }
            }
        });
        shouxianBtn = (LinearLayout) shouxianView.findViewById(R.id.ll_shouxian);
        fangxianBtn = (LinearLayout) shouxianView.findViewById(R.id.ll_fangxian);
        shouxianBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchDown(view.getId());
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    touchUp(view.getId());
                }
                return false;
            }
        });
        fangxianBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchDown(view.getId());
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    touchUp(view.getId());
                }
                return false;
            }
        });

        mShouxianPopupWindow = initLeftBottomPopupWidow(shouxianView);
        mShouxianPopupWindow.setTag("shouxian");
    }

    private MyPopupWindow initLeftBottomPopupWidow(View view) {
        int width = NumberUtil.dipId2px(this, R.dimen.shouxian_width);
        int heigh = NumberUtil.dipId2px(this, R.dimen.shouxian_height);
        int lx = getLocation(mSurfaceView)[0];
        int ly = getLocation(mSurfaceView)[1] + mSurfaceView.getHeight() - heigh;

        return new MyPopupWindow(view, width, heigh, false, lx, ly);
    }

    private void initTestPopupWindow() {
        View testView = LayoutInflater.from(this).inflate(R.layout.test_layout, null);
        Switch sw_auto = (Switch) testView.findViewById(R.id.sw_auto_test);
        Switch sw_noAuto = (Switch) testView.findViewById(R.id.sw_noAutoModel);
//        Switch sw_rizhi = (Switch) testView.findViewById(R.id.sw_rizi);
        ImageView iv_test_set = (ImageView) testView.findViewById(R.id.iv_test_set);
        ImageView iv_test_showAlarm = (ImageView) testView.findViewById(R.id.iv_test_showAlarm);

//        sw_rizhi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                isWriteRiZhi = isChecked;
//            }
//        });
        sw_auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fatigueTest.startFatigue();
                } else {
                    fatigueTest.stopFatigue();
                }
                if (isOpenFatigueTest || isChecked) {
                    iv_test.setImageResource(R.drawable.ic_test_yellow);
                } else {
                    iv_test.setImageResource(R.drawable.ic_test_gray);
                }
            }
        });
        sw_noAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isOpenFatigueTest = isChecked;
                if (isOpenFatigueTest || isChecked) {
                    iv_test.setImageResource(R.drawable.ic_test_yellow);
                } else {
                    iv_test.setImageResource(R.drawable.ic_test_gray);
                }
            }
        });
        iv_test_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PiLaoSetFragment fragment = new PiLaoSetFragment();
                fragment.setFatigueTest(fatigueTest);

                FragmentUtil.showDialogFragment(getSupportFragmentManager(), fragment, "PiLaoSetFragment");
            }
        });

        iv_test_showAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(context()).
                        setTitle(getString(R.string.piLao_Testing)).
                        setMessage(getString(R.string.piLao_alarm)).
                        show();
                alertDialog.setCanceledOnTouchOutside(false);

            }
        });
        mTestPopupWindow = initRightBottomPopupWindow(testView);
    }

    private void initSpeedPopupWindow() {
        View speedView = LayoutInflater.from(this).inflate(R.layout.speed_layout, null);
        SeekBar sb_speed = (SeekBar) speedView.findViewById(R.id.sb_speed_move);
        SeekBar sb_ptz_speed = (SeekBar) speedView.findViewById(R.id.sb_ptz_move);
        sb_ptz_speed.setProgress(10);
        sb_ptz_speed.setOnSeekBarChangeListener(seekBarChangeListener);
        sb_speed.setProgress(5);
        sb_speed.setOnSeekBarChangeListener(seekBarChangeListener);
        mSpeedPopupWindow = initRightBottomPopupWindow(speedView);
        mSpeedPopupWindow.setTag("speed");
    }

    private void initLightPopupWindow() {
        View lightView = LayoutInflater.from(this).inflate(R.layout.preview_light, null);
        SeekBar ptz_light_sb = (SeekBar) lightView.findViewById(R.id.sb_ptz_light);
        SeekBar front_light_sb = (SeekBar) lightView.findViewById(R.id.sb_front_light);
        SeekBar back_light_sb = (SeekBar) lightView.findViewById(R.id.sb_back_light);
        ptz_light_sb.setOnSeekBarChangeListener(seekBarChangeListener);
        front_light_sb.setOnSeekBarChangeListener(seekBarChangeListener);
        back_light_sb.setOnSeekBarChangeListener(seekBarChangeListener);

        mLightPopupWindow = initRightBottomPopupWindow(lightView);
        mLightPopupWindow.setTag("light");
    }

    private MyPopupWindow initRightBottomPopupWindow(View view) {
        int width = NumberUtil.dipId2px(this, R.dimen.light_width);
        int heigh = NumberUtil.dipId2px(this, R.dimen.light_height);
        int lx = getLocation(mSurfaceView)[0] + mSurfaceView.getWidth() - width;
        int ly = getLocation(mSurfaceView)[1] + mSurfaceView.getHeight() - heigh;

        return new MyPopupWindow(view, width, heigh, false, lx, ly);
    }

    private int[] getLocation(View view) {
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        return locations;
    }

    private void showOnePopupWindow(MyPopupWindow popupWindow, MyPopupWindow... popupWindows) {
        if (popupWindows != null)
            for (MyPopupWindow mp : popupWindows) {
                if (mp != null && mp.getmPopupWindow().isShowing())
                    mp.dismiss();
            }

        if (popupWindow.getmPopupWindow().isShowing())
            popupWindow.dismiss();
        else {
            popupWindow.show(mSurfaceView);
        }
    }

    private void changeCamera() {
        if (!isBackCameraNow) {
//            controlPresenter.machine_reset();
            isBackCameraNow = true;
//                showVlcCarmera();
            mSurfaceView_vlc.setVisibility(View.VISIBLE);
            mSurfaceView.setVisibility(View.VISIBLE);
            vlcPresenter.start();


        } else {
            isBackCameraNow = false;
//                showHKCamera();
//                mSurfaceView_vlc.setVisibility(View.GONE);
//                vlcPresenter.release();
            mSurfaceView_vlc.setVisibility(View.INVISIBLE);
            mSurfaceView.setVisibility(View.VISIBLE);
            if (All_id_Info.getInstance().getM_iLogID() < 0) {
                showHKCamera();

            }
        }
    }

    private void showHKCamera() {

//        mSurfaceView.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                vlcPresenter.release();
                hcnetSdkLoginPresenter.setCanReConnect(true);
            }
        }).start();
    }

    private void showVlcCarmera() {
//        mSurfaceView.setVisibility(View.INVISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                hcnetSdkLoginPresenter.setCanReConnect(false);
                vlcPresenter.start();
            }
        }).start();
    }

    private void setBiaoDing() {

        BiaodingFragment biaodingFragment = BiaodingFragment.getInstance(mIsOpenLiftBiaoding, mIsOpenPtzBiaoding);
        biaodingFragment.setOnBiaodingChangedListener(new BiaodingFragment.OnBiaodingChangedListener() {

            @Override
            public void liftBiaoding_control(boolean isOpen) {
                log("isopen");
                mIsOpenLiftBiaoding = isOpen;
                if (isOpen) {
                    controlPresenter.lift_biaoding_on();
                } else {
                    controlPresenter.lift_biaoding_off();
                }
            }

            @Override
            public void ptzBiaoding_control(boolean isOpen) {
                log("isopen");
                mIsOpenPtzBiaoding = isOpen;
                if (isOpen) {
                    controlPresenter.ptz_biaoding_on();
                } else {
                    controlPresenter.ptz_biaoding_off();
                }
            }
        });

        showDialogFragment(getSupportFragmentManager(), biaodingFragment, "BiaodingFragment");
    }

    @OnTouch({R.id.zoom_add
            , R.id.focus_add
            , R.id.focus_sub
            , R.id.zoom_sub
    })
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchDown(view.getId());
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            touchUp(view.getId());
        }
        return false;
    }

    private void touchDown(int viewId) {
        switch (viewId) {
            case R.id.zoom_add:
//                controlPresenter.machine_reset();
                ptzControlPresenter.zoom_out(true);
                break;
            case R.id.focus_add:
                ptzControlPresenter.focus_far(true);
                break;
            case R.id.focus_sub:
                ptzControlPresenter.focus_near(true);
                break;
            case R.id.zoom_sub:
                ptzControlPresenter.zoom_in(true);
                break;
            case R.id.ll_shouxian:
                carControlPresenter.car_shouxian_shou();
                break;
            case R.id.ll_fangxian:
                carControlPresenter.car_shouxian_fang();
                break;
        }
    }

    private void touchUp(int viewId) {
        switch (viewId) {
            case R.id.zoom_add:
//                controlPresenter.machine_reset_default();
                ptzControlPresenter.zoom_out(false);
                break;
            case R.id.focus_add:
                ptzControlPresenter.focus_far(false);
                break;
            case R.id.focus_sub:
                ptzControlPresenter.focus_near(false);
                break;
            case R.id.zoom_sub:
                ptzControlPresenter.zoom_in(false);
                break;
            case R.id.ll_shouxian:
                carControlPresenter.car_shouxian_stop();
                break;
            case R.id.ll_fangxian:
                carControlPresenter.car_shouxian_stop();
                break;
        }
    }

    private MySeekBarChangeListener seekBarChangeListener = new MySeekBarChangeListener(new MySeekBarChangeListener.MyOnSeekBarChangeListener() {
        @Override
        public void change(SeekBar seekBar, int progress) {
            log("progress = " + progress);
            seekBarProgressOpreator(seekBar, progress);
        }
    });

    private View.OnClickListener ptzSetPlaceClickListerner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cit_ptz_set_up:
                    controlPresenter.ptz_set_top();
                    break;
                case R.id.cit_ptz_set_left:
                    controlPresenter.ptz_set_left();
                    break;
                case R.id.cit_ptz_set_right:
                    controlPresenter.ptz_set_right();
                    break;
                case R.id.cit_ptz_set_circle:
                    controlPresenter.ptz_set_round();
                    break;
            }
        }
    };

    private void seekBarProgressOpreator(SeekBar seekBar, int progress) {
        switch (seekBar.getId()) {
            case R.id.sb_front_light:
                if (progress == 0) {
                    controlPresenter.light_front_LX(0);
                    controlPresenter.light_front_off();
                    break;
                }
                controlPresenter.light_front_on();
                controlPresenter.light_front_LX(progress);
                break;
            case R.id.sb_back_light:
                if (progress == 0) {
//                    controlPresenter.light_back_LX(0);
//                    controlPresenter.light_back_off();
                    controlPresenter.light_front_off_to_back(0);
                    controlPresenter.lightBack_L1(0);
                    break;
                }
//                controlPresenter.light_back_on();
//                controlPresenter.light_back_LX(progress);
                controlPresenter.light_front_off_to_back(progress);
                controlPresenter.lightBack_L1(progress);
                break;

            case R.id.sb_ptz_light:
                if (progress == 0) {
                    controlPresenter.ptz_light_LX(0);
                    break;
                }
                controlPresenter.ptz_light_LX(progress);
                break;

            case R.id.sb_speed_move:
                controlPresenter.move_speed(progress);
                break;

            case R.id.sb_ptz_move:
                controlPresenter.ptz_speed_set(progress);
                break;

            case R.id.sb_shouxian_speed:
                carControlPresenter.car_shouxian_speed(progress);
                break;

            case R.id.sb_shouxian_liju:
                carControlPresenter.car_fangxian_liju(progress);
                break;

        }
    }

    private long count;

    @Override
    public void result(final RemoteDeviceInfo remoteDeviceInfo) {
        if (remoteDeviceInfo == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mResultInfo.setText("");
                }
            });
            return;
        }


        m_Machine_protect = getMachine_protectName(remoteDeviceInfo.getMachine_protect());

        if (remoteDeviceInfo.getMachine_protect() != 0) {
            cit_protectDevice.setVisibility(View.VISIBLE);
        } else {
            cit_protectDevice.setVisibility(View.GONE);
        }


        final StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.
                append("\n电机温度1：").append(remoteDeviceInfo.getDynamo_temperature1()).append(" \t-电机温度2：").append(remoteDeviceInfo.getDynamo_temperature2()).
                append("\t电机电流1：").append(remoteDeviceInfo.getDynamo_electricity1()).append(" \t电机电流2：").append(remoteDeviceInfo.getDynamo_electricity2()).
                append("\t电机转速1：").append(remoteDeviceInfo.getDynamo_rotate_speed1()).append(" \t电机转速2：").append(remoteDeviceInfo.getDynamo_rotate_speed2()).
                append("\t  保护状态：").append(remoteDeviceInfo.getMachine_protect()).
                append("\n爬行器温度：").append(remoteDeviceInfo.getMachine_temperature())
                .append(" \t-气压：").append(remoteDeviceInfo.getMachine_airPressure()).
                append("\t-电压：").append(remoteDeviceInfo.getMachine_voltage())
                .append(" \t-电流：").append(remoteDeviceInfo.getMachine_electricity())
                .append(" \t-状态：").append(remoteDeviceInfo.getMachine_state())
                .append("\n升降架温度:").append(remoteDeviceInfo.getLift_temperature())
                .append(" \t-气压：").append(remoteDeviceInfo.getLift_airPressure()).
                append("\t-工作电流：").append(remoteDeviceInfo.getLift_workElectricity())
                .append(" \t-电机电流：").append(remoteDeviceInfo.getLift_machineElectricity())
                .append(" \t-状态：").append(remoteDeviceInfo.getLift_state()).
                append("\n云台温度：").append(remoteDeviceInfo.getPtz_temperature()).append(" \t气压：").append(remoteDeviceInfo.getPtz_airPressure()).
                append("\t-工作电流：").append(remoteDeviceInfo.getPtz_workElectricity())
                .append(" \t-电机电流1：").append(remoteDeviceInfo.getPtz_machineElectricity1())
                .append(" \t-电机电流2：").append(remoteDeviceInfo.getPtz_machineElectricity2()).
                append("\t-灯电流：").append(remoteDeviceInfo.getPtz_lightElectricity()).append(" \t-电热丝电流：").append(remoteDeviceInfo.getPtz_heaterElectricity()).
                append("-状态：").append(remoteDeviceInfo.getPtz_state()).
                append("\n大灯温度：").append(remoteDeviceInfo.getLight_temperature()).append(" \t-气压：").append(remoteDeviceInfo.getLight_airPressure()).
                append("\t-工作电流：").append(remoteDeviceInfo.getLight_workElectricity())
                .append(" \t-灯光电流：").append(remoteDeviceInfo.getLight_Electricity())
                .append(" \t-状态：").append(remoteDeviceInfo.getLight_state());


        if (isWriteRiZhi && System.currentTimeMillis() - count >= riZhiWaitTime) {
            count = System.currentTimeMillis();
            File file = new File(FileUtil.getSDPath(), FileInfo.m2Path + "paxingqiLog.txt");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    StringBuilder nameBuilder = new StringBuilder();
                    String[] strings = getResources().getStringArray(R.array.result_robot);
                    for (int i = 0; i < strings.length; i++) {
                        nameBuilder.append(strings[i]);
                        if (i == 0) {
                            nameBuilder.append("\t\t\t\t\t\t\t");
                        } else {
                            nameBuilder.append("\t");
                        }
                    }
                    FileUtil.writeStringToFile(file, true, nameBuilder.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(TimeUtil.getTimeHanZi()).append("\t\t").
                    append(remoteDeviceInfo.getDynamo_temperature1()).append("\t\t\t").
                    append(remoteDeviceInfo.getDynamo_temperature2()).append("\t\t\t").
                    append(remoteDeviceInfo.getDynamo_electricity1()).append("\t\t\t").
                    append(remoteDeviceInfo.getDynamo_electricity2()).append("\t\t\t").
                    append(remoteDeviceInfo.getDynamo_rotate_speed1()).append("\t\t\t").
                    append(remoteDeviceInfo.getDynamo_rotate_speed2()).append("\t\t").
                    append(remoteDeviceInfo.getMachine_temperature()).append("\t\t").
                    append(remoteDeviceInfo.getMachine_airPressure()).append("\t\t").
                    append(remoteDeviceInfo.getMachine_voltage()).append("\t").
                    append(remoteDeviceInfo.getMachine_electricity()).append("\t\t").
                    append(remoteDeviceInfo.getMachine_state()).append("\t\t\t").
                    append(remoteDeviceInfo.getLift_temperature()).append("\t\t\t").
                    append(remoteDeviceInfo.getLift_airPressure()).append("\t\t\t").
                    append(remoteDeviceInfo.getLift_workElectricity()).append("\t\t\t\t").
                    append(remoteDeviceInfo.getLift_machineElectricity()).append("\t\t\t\t").
                    append(remoteDeviceInfo.getLift_state()).append("\t\t\t").
                    append(remoteDeviceInfo.getPtz_temperature()).append("\t\t\t").
                    append(remoteDeviceInfo.getPtz_airPressure()).append("\t\t\t").
                    append(remoteDeviceInfo.getPtz_workElectricity()).append("\t\t\t\t").
                    append(remoteDeviceInfo.getPtz_machineElectricity1()).append("\t\t\t\t").
                    append(remoteDeviceInfo.getPtz_machineElectricity2()).append("\t\t\t\t").
                    append(remoteDeviceInfo.getPtz_lightElectricity()).append("\t\t\t\t").
                    append(remoteDeviceInfo.getPtz_heaterElectricity()).append("\t\t\t\t").
                    append(remoteDeviceInfo.getPtz_state()).append("\t\t\t").
                    append(remoteDeviceInfo.getLight_temperature()).append("\t\t\t").
                    append(remoteDeviceInfo.getLight_airPressure()).append("\t\t\t").
                    append(remoteDeviceInfo.getLight_workElectricity()).append("\t\t\t\t").
                    append(remoteDeviceInfo.getLight_Electricity()).append("\t\t\t\t").
                    append(remoteDeviceInfo.getLight_state()).append("\t\t\t\t")
                    .append(remoteDeviceInfo.getMachine_protect());
            String str = stringBuilder.toString();
            FileUtil.writeStringToFile(file, true, str);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mResultInfo.setText(resultBuilder.toString());
                if (mIv_zitai_roll != null) {
                    mIv_zitai_picth.setImageResource(MyViewUtil.getLiftImgId(remoteDeviceInfo.getLift_height()));
                    mTv_lift_info.setText(getString(R.string.str_lift_height).
                            replace("{height}", remoteDeviceInfo.getLift_height() + "").
                            replace("{angle}", remoteDeviceInfo.getLift_angle() + ""));
                    mTv_ptz_info.setText(getString(R.string.str_ptz_angle).
                            replace("{jing}", remoteDeviceInfo.getPtz_radial_angle() + "").
                            replace("{zhou}", remoteDeviceInfo.getPtz_axial_angle() + ""));
                    mTv_zitai_roll.setText(getString(R.string.str_zitai_gunjiao).
                            replace("{roll}", remoteDeviceInfo.getPoseRoll() + ""));
                    mTv_zitai_picth.setText(getString(R.string.str_zitai_qingjiao).
                            replace("{qing}", remoteDeviceInfo.getPoseSlant() + ""));
                    float roll_angle = remoteDeviceInfo.getPoseRoll();
                    RotateAnimation ra_roll = new RotateAnimation(roll_angle, roll_angle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    ra_roll.setDuration(10);
                    ra_roll.setFillAfter(true);
                    mIv_zitai_roll.setAnimation(ra_roll);
                    ra_roll.startNow();
                    float pitch_angle = remoteDeviceInfo.getPoseSlant();
                    RotateAnimation ra_picth = new RotateAnimation(pitch_angle, pitch_angle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    ra_picth.setDuration(10);
                    ra_picth.setFillAfter(true);
                    mIv_zitai_picth.setAnimation(ra_picth);
                    ra_picth.setFillAfter(true);
                    ra_picth.startNow();

                }
                if (cit_quanxian != null) {
                    if (remoteDeviceInfo.getControlIp().equals(InternetUtil.getWifiIp(context()))) {

                        cit_quanxian.setImage(R.drawable.ic_verified_user_black_24dp);
                        cit_quanxian.setTextColor(android.R.color.holo_green_dark);
                        cit_quanxian.setText(getString(R.string.str_quanxian_yes));

                    } else {

                        cit_quanxian.setImage(R.drawable.ic_verified_user_red_24dp);
                        cit_quanxian.setTextColor(R.color.stop_red);
                        cit_quanxian.setText(getString(R.string.str_quanxian_no));
                    }
                }
//                log(" ip : "+remoteDeviceInfo.getControlIp()+ " mIp = "+InternetUtil.getWifiIp(context()));

            }
        });
    }

    private AlertDialog mMachineProtectResetFragment;
    private boolean mIsMachineResetProtect;

    private void showMachineResetDialog(String machine_protect) {
        if (mMachineProtectResetFragment == null) {
            mMachineProtectResetFragment = new AlertDialog.Builder(this).setTitle(machine_protect).setMessage("是否进行电机复位操作？").
                    setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mIsMachineResetProtect = true;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ThreadUtil.sleep(200);
                                    mIsMachineResetProtect = false;
                                }
                            }).start();
                            mMachineProtectResetFragment.dismiss();

                        }
                    }).setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            controlPresenter.machine_reset();
                            mIsMachineResetProtect = true;
                            ThreadUtil.sleep(2500);
                            controlPresenter.machine_reset_default();
                            ThreadUtil.sleep(3000);
                            mIsMachineResetProtect = false;
                        }
                    }).start();

                }
            }).create();
        }


        if (!mIsMachineResetProtect && !mMachineProtectResetFragment.isShowing()) {
            mMachineProtectResetFragment.show();
        }
    }


    private String getMachine_protectName(int machine_protect) {
        switch (machine_protect) {
            case 0x00:
                return "无保护";
            case 0x01:
                return "左电机保护";
            case 0x10:
                return "右电机保护";
            case 0x11:
                return "左右电机保护";
        }
        return null;
    }

    private long carCount;

    @Override
    public void carResult(final CarRemoteDeviecInfo carRemoteDeviecInfo) {
        if (carRemoteDeviecInfo == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mResultInfo_car.setText("");
                }
            });
            return;
        }

        final StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("\n")
                .append("\n计米器值-电量：").append(carRemoteDeviecInfo.getJimiqi_value()).append(" - ").append(carRemoteDeviecInfo.getBattery()).
                append("\n线缆车温度-气压：").append(carRemoteDeviecInfo.getCar_temperature()).append(" - ").append(carRemoteDeviecInfo.getCar_airPressure()).
                append("\n线缆车电流-电压：").append(carRemoteDeviecInfo.getCar_Electricity()).append(" - ").append(carRemoteDeviecInfo.getCar_valtage()).
                append("\n线缆车电机-电压：").append(carRemoteDeviecInfo.getDynamo_voltage()).append(" -电流：").append(carRemoteDeviecInfo.getDynamo_electricity()).
                append(" -速度：").append(carRemoteDeviecInfo.getDynamo_rotate_speed()).append(" -温度：").append(carRemoteDeviecInfo.getDynamo_temperature()).
                append(" -警报：").append(carRemoteDeviecInfo.getDynamo_alarm());


        if (isWriteRiZhi && System.currentTimeMillis() - carCount >= riZhiWaitTime) {
            carCount = System.currentTimeMillis();
            File file = new File(FileUtil.getSDPath(), FileInfo.m2Path + "xianlancheLog.txt");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    StringBuilder nameBuilder = new StringBuilder();
                    String[] strings = getResources().getStringArray(R.array.result_car);
                    for (int i = 0; i < strings.length; i++) {
                        nameBuilder.append(strings[i]);
                        if (i == 0) {
                            nameBuilder.append("\t\t\t\t\t\t\t");
                        } else {
                            nameBuilder.append("\t");
                        }
                    }
                    FileUtil.writeStringToFile(file, true, nameBuilder.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(TimeUtil.getTimeHanZi()).append("\t").
                    append(carRemoteDeviecInfo.getCar_temperature()).append("\t\t").
                    append(carRemoteDeviecInfo.getCar_airPressure()).append("\t\t").
                    append(carRemoteDeviecInfo.getCar_Electricity()).append("\t\t").
                    append(carRemoteDeviecInfo.getCar_valtage()).append("\t\t").
                    append(carRemoteDeviecInfo.getDynamo_voltage()).append("\t\t\t").
                    append(carRemoteDeviecInfo.getDynamo_electricity()).append("\t\t\t").
                    append(carRemoteDeviecInfo.getDynamo_rotate_speed()).append("\t\t\t").
                    append(carRemoteDeviecInfo.getDynamo_temperature()).append("\t\t\t").
                    append(carRemoteDeviecInfo.getDynamo_alarm());
            FileUtil.writeStringToFile(file, true, stringBuilder.toString());

        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mResultInfo_car.setText(resultBuilder.toString());
//                mResultInfo_car.setText(resultBuilder.toString());
                tv_battery_device.setBackgroundResource(MyViewUtil.getBatteryId(carRemoteDeviecInfo.getBattery()));
                tv_battery_device.setText(carRemoteDeviecInfo.getBattery() + "%");
                float jimiqi_value = carRemoteDeviecInfo.getJimiqi_value() / (float) 1000;
                tv_jimiqi.setText(NumberUtil.getDecimalDit(jimiqi_value, 100) + "m");
//                mCharOverPresent.setGpsAndRang(String.valueOf(NumberUtil.getDecimalDit(jimiqi_value, 100)),0,0);

                if (cit_quanxian != null) {
                    if (carRemoteDeviecInfo.getControlIp().equals(InternetUtil.getWifiIp(context()))) {

                        cit_quanxian.setImage(R.drawable.ic_verified_user_black_24dp);
                        cit_quanxian.setTextColor(android.R.color.holo_green_dark);
                        cit_quanxian.setText(getString(R.string.str_quanxian_yes));

                    } else {

                        cit_quanxian.setImage(R.drawable.ic_verified_user_red_24dp);
                        cit_quanxian.setTextColor(R.color.stop_red);
                        cit_quanxian.setText(getString(R.string.str_quanxian_no));
                    }
                }
//                log(" ip : "+carRemoteDeviecInfo.getControlIp()+ " mIp = "+InternetUtil.getWifiIp(context()));

            }
        });
    }

    @Override
    public void record(final int startOrEnd, final boolean isSuccess) {
        if (cit_record != null)
            cit_record.post(new Runnable() {
                @Override
                public void run() {
                    if (isSuccess) {
                        if (startOrEnd == 0) {
                            isRecordOpen = true;
                            cit_record.setImage(R.drawable.ic_recording);
                            initRecordTimer();
                        } else {
                            isRecordOpen = false;
                            cit_record.setImage(R.drawable.ic_record);
                            if (recordTimerTask != null)
                                recordTimerTask.cancel();
                            if (recordTimer != null)
                                recordTimer.cancel();
                            cit_record.setText(getResources().getString(R.string.recording));
                            recordTime = 0;

                            mCharOverPresent.setNoRecordHead();
                            mCharOverPresent.recordFinish();
                        }
                    } else {
                        if (startOrEnd == 0) {
                            toast(getString(R.string.str_start_record_fail));

                            mCharOverPresent.setNoRecordHead();
                            mTaskName = null;
                            mTaskPlace = null;
                        } else {
                            toast(getString(R.string.str_stop_record_fail));
                        }
                    }
                }
            });
    }

    @Override
    public void capture(final String path) {
        if (path != null) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Capture_tishi_Dialog dialog = new Capture_tishi_Dialog(MainActivity.this, getResources().getString(R.string.isEditPicture), path);

                    dialog.setSureOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(MainActivity.this, PictureEditActivity.class);
                            intent.putExtra("path", path);
//                            intent.putExtra("batteryNum", mBatteryNum);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });

                    dialog.setCancelClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        } else
            toast("截图失败！");
    }

    private BroadcastReceiver system_battery_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyViewUtil.setSystemBattery(context(), intent, tv_battery_terminal);
            justIsConnectWifi(intent);
            senCommandForDeviceUpdate(intent);
        }
    };

    private void senCommandForDeviceUpdate(Intent intent) {
        int updateObject = intent.getIntExtra(Constant.KEY_DEVICE_UPDATE_OBJECT_CHOOSE, -1);
        log("brocast updateObject = " + updateObject);
        if (updateObject != -1 && controlPresenter != null && carControlPresenter != null) {
            if (updateObject != 6)
                controlPresenter.updateObject(updateObject);
            else if (updateObject == 6) {
                controlPresenter.updateObject(0);
                carControlPresenter.update(true);
            } else {
                controlPresenter.updateObject(0);
                carControlPresenter.update(false);

            }
        }

        int updateModel = intent.getIntExtra(Constant.KEY_DEVICE_UPDATE_MODEL_CHOOSE, -1);
        log("brocaset updateModel = " + updateModel);
        if (updateModel != -1 && controlPresenter != null && carControlPresenter != null) {
            if (updateModel == 1) {
                carControlPresenter.updateModel(false);
                carControlPresenter.update(false);

                controlPresenter.updateModel(true);
            } else if (updateModel == 2) {
                controlPresenter.updateModel(false);

                carControlPresenter.updateModel(true);
            } else if (updateModel == 3) {
                carControlPresenter.update(false);
            } else {
                controlPresenter.updateModel(false);
                carControlPresenter.updateModel(false);
            }
        }


    }

    private void justIsConnectWifi(Intent intent) {
        boolean isConnect = intent.getBooleanExtra(Constant.KEY_IS_CONNECTED_WIFI, true);
        if (!isConnect) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (cit_quanxian != null) {
                        cit_quanxian.setText(getString(R.string.disconnect));
                        cit_quanxian.setTextColor(R.color.stop_red);
                        cit_quanxian.setImage(R.mipmap.disconnect);
                    }
                }
            });
        }
    }

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Constant.BROADCAST_JUST_IS_CONNECTING);
        intentFilter.addAction(Constant.BROADCAST_DEVICE_UPDATAE_SEND_COMMAND);
        registerReceiver(system_battery_receiver, intentFilter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
//                cPresenter.isGetEnvironment(false);
                break;
            case 1:
                if (data != null) {
                    RecordTaskInfo recordTaskInfo = (RecordTaskInfo) data.getSerializableExtra("record_head");
                    setZiFuDieJia(recordTaskInfo);
                    mTaskName = recordTaskInfo.getTask_name();
                    mTaskPlace = recordTaskInfo.getTask_place();
                    mStartWell = recordTaskInfo.getTask_start();
                    mEndWell = recordTaskInfo.getTask_end();
                }
                videoCutPresenter.record(mTaskName, mTaskPlace, mStartWell, mEndWell, isAddNewRecordKanBan);
                break;
        }
    }

    private void setZiFuDieJia(RecordTaskInfo recordTaskInfo) {
        if (recordTaskInfo != null) {
            StringBuilder taskNameAId = new StringBuilder();
            if (!TextUtils.isEmpty(recordTaskInfo.getTask_name())) {
                if (Login_info.getInstance().isShowFirstName())
                    taskNameAId.append(getResources().getString(R.string.task_name_id));
                taskNameAId.append(recordTaskInfo.getTask_name()).
                        append("/").
                        append(recordTaskInfo.getTask_id());
            }

            StringBuilder taskPlace = new StringBuilder();
            if (!TextUtils.isEmpty(recordTaskInfo.getTask_place())) {
                if (Login_info.getInstance().isShowFirstName())
                    taskPlace.append(getResources().getString(R.string.record_task_place_e));
                taskPlace.append(recordTaskInfo.getTask_place()).
                        append(" ");
            }

            NET_DVR_TIME net_dvr_time = HkUtils.getTime();
            if (net_dvr_time != null) {
                StringBuilder timeBuilder = new StringBuilder();
                timeBuilder.append(net_dvr_time.dwYear).append("年")
                        .append(net_dvr_time.dwMonth).append("月")
                        .append(net_dvr_time.dwDay).append("日");
                if (Login_info.getInstance().isShowFirstName()) {
                    taskPlace.append(getResources().getString(R.string.task_date));
                }
                taskPlace.append(timeBuilder.toString());
            }

            StringBuilder taskWellStar2End = new StringBuilder();
            if (!TextUtils.isEmpty(recordTaskInfo.getTask_start()) || !TextUtils.isEmpty(recordTaskInfo.getTask_end())) {
                if (Login_info.getInstance().isShowFirstName())
                    taskWellStar2End.append(getResources().getString(R.string.task_well_start_end));
                taskWellStar2End.append(recordTaskInfo.getTask_start()).
                        append("-").
                        append(recordTaskInfo.getTask_end()).append(" ");
            }

            StringBuilder taskDirectAGuancai = new StringBuilder();
            if (Login_info.getInstance().isShowFirstName())
                taskDirectAGuancai.append(getResources().getString(R.string.record_task_direction_e));
            taskDirectAGuancai.append(recordTaskInfo.getTask_direction()).
                    append(" ");
            if (Login_info.getInstance().isShowFirstName())
                taskDirectAGuancai.append(getResources().getString(R.string.record_task_guancai_e));
            taskDirectAGuancai.append(recordTaskInfo.getTask_guancai()).
                    append(" ");
            StringBuilder taskSortADiameter = new StringBuilder();
            if (Login_info.getInstance().isShowFirstName())
                taskSortADiameter.append(getResources().getString(R.string.record_task_sort_e));
            taskSortADiameter.append(recordTaskInfo.getTask_sort()).
                    append(" ");
            if (!TextUtils.isEmpty(recordTaskInfo.getTask_diameter())) {
                if (Login_info.getInstance().isShowFirstName())
                    taskSortADiameter.append(getResources().getString(R.string.task_diameter));
                taskSortADiameter.append(recordTaskInfo.getTask_diameter()).
                        append("mm");
            }

            StringBuilder taskPlaceAPeople = new StringBuilder();

            if (!TextUtils.isEmpty(recordTaskInfo.getTask_computer())) {
                if (Login_info.getInstance().isShowFirstName())
                    taskPlaceAPeople.append(getResources().getString(R.string.record_task_computer_e));
                taskPlaceAPeople.append(recordTaskInfo.getTask_computer()).
                        append(" ");
            }
            if (!TextUtils.isEmpty(recordTaskInfo.getTask_people())) {
                if (Login_info.getInstance().isShowFirstName())
                    taskPlaceAPeople.append(getResources().getString(R.string.record_task_people_e));
                taskPlaceAPeople.append(recordTaskInfo.getTask_people());
            }


            String[] mulSignStrs = new String[]{
                    taskNameAId.toString(),
                    taskPlace.toString(),
                    taskWellStar2End.toString(),
                    taskDirectAGuancai.toString(),
                    taskSortADiameter.toString(),
                    taskPlaceAPeople.toString()
            };
            String[] sendStrs = new String[6];
            int i = 0;
            for (String str : mulSignStrs) {
                if (!TextUtils.isEmpty(str)) {
                    sendStrs[i] = str;
                    i++;
                }
            }


            mCharOverPresent.setMultSign(sendStrs);
            mCharOverPresent.setAlwaysShowInfo(recordTaskInfo.getTask_name()
                    , recordTaskInfo.getTask_id()
                    , recordTaskInfo.getTask_start()
                    , recordTaskInfo.getTask_end()
                    , recordTaskInfo.getTask_guancai()
                    , recordTaskInfo.getTask_diameter()
            );
        }
    }

}
