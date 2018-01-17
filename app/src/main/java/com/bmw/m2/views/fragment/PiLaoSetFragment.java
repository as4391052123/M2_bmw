package com.bmw.m2.views.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.bmw.m2.R;
import com.bmw.m2.views.activity.FatigueTest;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/6/19.
 */

public class PiLaoSetFragment extends DialogFragment implements CompoundButton.OnCheckedChangeListener {


    @Bind(R.id.sw_move)
    Switch swMove;
    @Bind(R.id.edt_move)
    EditText edtMove;
    @Bind(R.id.sw_light)
    Switch swLight;
    @Bind(R.id.edt_light)
    EditText edtLight;
    @Bind(R.id.sw_lift)
    Switch swLift;
    @Bind(R.id.edt_lift)
    EditText edtLift;
    @Bind(R.id.sw_ptz_move)
    Switch swPtzMove;
    @Bind(R.id.edt_ptz_move)
    EditText edtPtzMove;
    @Bind(R.id.sw_ptz_fuyang)
    Switch swPtzFuyang;
    @Bind(R.id.edt_ptz_fuyang)
    EditText edtPtzFuyang;
    @Bind(R.id.sw_camera)
    Switch swCamera;
    @Bind(R.id.edt_camera)
    EditText edtCamera;
    @Bind(R.id.sw_heater)
    Switch swHeater;
    @Bind(R.id.edt_heater)
    EditText edtHeater;
    @Bind(R.id.sw_zoom)
    Switch swZoom;
    @Bind(R.id.edt_zoom)
    EditText edtZoom;
    @Bind(R.id.edt_lift_waitTime)
    EditText edt_lift_wait;
    private View mView;

    private boolean isMove = true;
    private boolean isLift = true;
    private boolean isLight = true;
    private boolean isPtzMove = true;
    private boolean isPtzFuyangMove = true;
    private boolean isCamera = true;
    private boolean isHKZoom = true;
    private boolean isHeater = false;

    private int liftTime = 17;
    private int ptzlrmoveTime = 32;
    private int ptzubTime = 13;
    private int hkZoomTime = 5;
    private long cameraChangeTime = 1000 * 60 * 30;
    private long fogTestTime = 1000 * 60 * 5;
    private long liftWaitTime = 60*5;


    private FatigueTest fatigueTest;


    public PiLaoSetFragment() {
    }

    public void setFatigueTest(FatigueTest fatigueTest) {
        this.fatigueTest = fatigueTest;
        initData();

    }


    private void initData() {

        isMove = fatigueTest.isMove();
        isLift = fatigueTest.isLift();
        isLight = fatigueTest.isLight();
        isPtzMove = fatigueTest.isPtzMove();
        isPtzFuyangMove = fatigueTest.isPtzFuyangMove();
        isCamera = fatigueTest.isCamera();
        isHKZoom = fatigueTest.isHKZoom();
        isHeater = fatigueTest.isHeater();

        liftTime = fatigueTest.getLiftTime();
        liftWaitTime = fatigueTest.getLift_wait_time();
        ptzlrmoveTime = fatigueTest.getPtzlrLeftTime();
        ptzubTime = fatigueTest.getPtzubTime();
        hkZoomTime = fatigueTest.getHkZoomTime();

        cameraChangeTime = fatigueTest.getCameraChangeTime() / 60 / 1000;
        fogTestTime = fatigueTest.getFogTestTime() / 60 / 1000;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_pilao_set, null);
        ButterKnife.bind(this, mView);

        setData();

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(mView).create();
        setWindowStyle(dialog);
        return dialog;
    }

    private void setData() {
        edtMove.setText("");
        edtLift.setText("" + liftTime);
        edt_lift_wait.setText(""+liftWaitTime);
        edtLight.setText("");
        edtPtzMove.setText("" + ptzlrmoveTime);
        edtPtzFuyang.setText("" + ptzubTime);
        edtCamera.setText("" + cameraChangeTime);
        edtHeater.setText("" + fogTestTime);
        edtZoom.setText("" + hkZoomTime);

        swCamera.setChecked(isCamera);
        swHeater.setChecked(isHeater);
        swLift.setChecked(isLift);
        swLight.setChecked(isLight);
        swMove.setChecked(isMove);
        swPtzFuyang.setChecked(isPtzFuyangMove);
        swPtzMove.setChecked(isPtzMove);
        swZoom.setChecked(isHKZoom);


        swCamera.setOnCheckedChangeListener(this);
        swHeater.setOnCheckedChangeListener(this);
        swLift.setOnCheckedChangeListener(this);
        swLight.setOnCheckedChangeListener(this);
        swMove.setOnCheckedChangeListener(this);
        swPtzFuyang.setOnCheckedChangeListener(this);
        swPtzMove.setOnCheckedChangeListener(this);
        swZoom.setOnCheckedChangeListener(this);

    }

    private void setWindowStyle(Dialog dialog) {
        Window window = dialog.getWindow();
//        window.setWindowAnimations(R.style.dialog_anim);
        WindowManager manager = (WindowManager) getActivity().
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = window.getAttributes();  //获取对话框当前的参数值

        p.height = (int) (dm.heightPixels);   //高度设置为屏幕的0.3
        p.width = (int) (dm.widthPixels * 0.5);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);
//        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(mView);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick(R.id.btn_sure)
    public void onClick() {
        fatigueTest.setMove(isMove);
        fatigueTest.setLift(isLift);
        fatigueTest.setLight(isLight);
        fatigueTest.setPtzMove(isPtzMove);
        fatigueTest.setPtzFuyangMove(isPtzFuyangMove);
        fatigueTest.setCamera(isCamera);
        fatigueTest.setHKZoom(isHKZoom);
        fatigueTest.setHeater(isHeater);

        initEdit();

        fatigueTest.setLiftTime(liftTime);
        fatigueTest.setLift_wait_time(liftWaitTime);
        fatigueTest.setPtzlrLeftTime(ptzlrmoveTime);
        fatigueTest.setPtzlrRightTime(ptzlrmoveTime);
        fatigueTest.setPtzubTime(ptzubTime);
        fatigueTest.setHkZoomTime(hkZoomTime );
        fatigueTest.setCameraChangeTime(cameraChangeTime * 1000 * 60);
        fatigueTest.setFogTestTime(fogTestTime* 1000 * 60);


        dismiss();
    }

    private void initEdit() {
        liftTime = Integer.valueOf(edtLift.getText().toString());
        liftWaitTime = Integer.valueOf(edt_lift_wait.getText().toString());
        ptzlrmoveTime = Integer.valueOf(edtPtzMove.getText().toString());
        ptzubTime = Integer.valueOf(edtPtzFuyang.getText().toString());
        hkZoomTime = Integer.valueOf(edtZoom.getText().toString());
        cameraChangeTime = Integer.valueOf(edtCamera.getText().toString());
        fogTestTime = Integer.valueOf(edtHeater.getText().toString());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_camera:
                isCamera = isChecked;
                break;
            case R.id.sw_move:
                isMove = isChecked;
                break;
            case R.id.sw_lift:
                isLift = isChecked;
                break;
            case R.id.sw_light:
                isLight = isChecked;
                break;
            case R.id.sw_ptz_move:
                isPtzMove = isChecked;
                break;
            case R.id.sw_ptz_fuyang:
                isPtzFuyangMove = isChecked;
                break;
            case R.id.sw_zoom:
                isHKZoom = isChecked;
                break;
            case R.id.sw_heater:
                isHeater = isChecked;
                break;
        }
    }


}
