package com.bmw.m2.views.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bmw.m2.Constant;
import com.bmw.m2.R;
import com.bmw.m2.VLCApplication;
import com.bmw.m2.utils.ThreadUtil;
import com.bmw.m2.utils.ThrowUtil;
import com.lidroid.xutils.util.LogUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by admin on 2017/5/17.
 */

public class DeviceUpdateDialogFg extends DialogFragment {


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tv_update_version)
    TextView tvUpdateVersion;
    @Bind(R.id.sp_update_object)
    Spinner spUpdateObject;
    @Bind(R.id.tv_update_sure)
    TextView tvUpdateSure;
    private View mView;
    private int mPosition;
    private Intent mIntent;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.update_device_application, null);
        ButterKnife.bind(this, mView);
        initBroadcastReceiver();

        initData();

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(mView).create();
        dialog.setCanceledOnTouchOutside(false);
        setWindowStyle(dialog);

        return dialog;
    }

    private void initData() {

        int who = VLCApplication.getSharedPreferences().getInt(Constant.KEY_MYDEVICE_UPDATE_OBJECT_WHO, 0);

        mPosition = who;
        spUpdateObject.setSelection(who);
        if (mPosition != 0) {
            tvUpdateSure.setEnabled(true);
        }


        spUpdateObject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(R.color.colorBase));    //设置颜色

                float size = getResources().getDimension(R.dimen.text_size_m);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);    //设置大小

//                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);

                int updateObject = 0;
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        updateObject = 2;
                        break;
                    case 2:
                        updateObject = 3;
                        break;
                    case 3:
                        updateObject = 4;
                        break;
                    case 4:
                        updateObject = 5;
                        break;
                    case 5:
                        updateObject = 6;
                        break;
                    case 6:
                        updateObject = 1;
                }

                mPosition = position;
                VLCApplication.getSharedPreferences().edit().putInt(Constant.KEY_MYDEVICE_UPDATE_OBJECT_WHO, position).commit();

                if (mPosition == 0) {
                    tvUpdateSure.setEnabled(false);
                } else {
                    tvUpdateSure.setEnabled(true);
                }
                if (mPosition != 5) {
                    mIntent.putExtra(Constant.KEY_DEVICE_UPDATE_MODEL_CHOOSE, 3);
                    mIntent.putExtra(Constant.KEY_DEVICE_UPDATE_OBJECT_CHOOSE, updateObject);
                    getActivity().sendBroadcast(mIntent);
                    mIntent.putExtra(Constant.KEY_DEVICE_UPDATE_OBJECT_CHOOSE, -1);
                    mIntent.putExtra(Constant.KEY_DEVICE_UPDATE_MODEL_CHOOSE, -1);
                } else {
                    mIntent.putExtra(Constant.KEY_DEVICE_UPDATE_MODEL_CHOOSE, -1);
                    mIntent.putExtra(Constant.KEY_DEVICE_UPDATE_OBJECT_CHOOSE, updateObject);
                    getActivity().sendBroadcast(mIntent);
                    mIntent.putExtra(Constant.KEY_DEVICE_UPDATE_OBJECT_CHOOSE, -1);
                    mIntent.putExtra(Constant.KEY_DEVICE_UPDATE_MODEL_CHOOSE, -1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        WindowManager manager = (WindowManager) getActivity().
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();  //获取对话框当前的参数值

//        p.height = (int) (dm.heightPixels);   //高度设置为屏幕的0.3
        p.width = (int) (dm.widthPixels * 0.6);    //宽度设置为全屏
        //设置生效
        getDialog().getWindow().setAttributes(p);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {

        return super.show(transaction, tag);
    }

    private void setWindowStyle(Dialog dialog) {
        Window window = dialog.getWindow();

        window.setWindowAnimations(R.style.dialog_anim);
        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        getActivity().unregisterReceiver(Device_version_BR);

    }


    @OnClick({R.id.tv_update_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_update_cancel:
                dismiss();
                break;
        }
    }

    @OnTouch({R.id.tv_update_sure
    })
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mPosition != 5) {
                mIntent.putExtra(Constant.KEY_DEVICE_UPDATE_MODEL_CHOOSE, 1);
                getActivity().sendBroadcast(mIntent);
                mIntent.putExtra(Constant.KEY_DEVICE_UPDATE_MODEL_CHOOSE, -1);
            } else {
                mIntent.putExtra(Constant.KEY_DEVICE_UPDATE_MODEL_CHOOSE, 2);
//                mIntent.putExtra(Constant.KEY_DEVICE_UPDATE_OBJECT_CHOOSE, 6);
                getActivity().sendBroadcast(mIntent);
                mIntent.putExtra(Constant.KEY_DEVICE_UPDATE_OBJECT_CHOOSE, -1);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mIntent.putExtra(Constant.KEY_DEVICE_UPDATE_MODEL_CHOOSE, 0);
            getActivity().sendBroadcast(mIntent);
            mIntent.putExtra(Constant.KEY_DEVICE_UPDATE_MODEL_CHOOSE, -1);
//            VLCApplication.getSharedPreferences().edit().putInt(Constant.KEY_MYDEVICE_UPDATE_OBJECT_WHO, 0).commit();
            dismiss();
        }
        return false;

    }


    private BroadcastReceiver Device_version_BR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int versionMainframe = intent.getIntExtra(Constant.KEY_MYDEVICE_UPDATE_VERSION_MAINFRAME_SHOW, -1);
            int versionCar = intent.getIntExtra(Constant.KEY_MYDEVICE_UPDATE_VERSION_CAR_SHOW, -1);

            if (mPosition == 6 && tvUpdateVersion != null && versionCar != -1) {
                tvUpdateVersion.setText(versionCar + "");
            } else if (versionMainframe != -1 && tvUpdateVersion != null) {
                tvUpdateVersion.setText(versionMainframe + "");
            }
        }
    };

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(Constant.BROADCAST_DEVICE_UPDATAE_VERSION_SHOW);
        getActivity().registerReceiver(Device_version_BR, intentFilter);
        initBroadcastSender();
    }


    private void initBroadcastSender() {
        mIntent = new Intent();
        mIntent.setAction(Constant.BROADCAST_DEVICE_UPDATAE_SEND_COMMAND);
    }
}
