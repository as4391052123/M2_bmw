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
import android.widget.EditText;

import com.bmw.m2.R;
import com.bmw.m2.views.service.MyIntentService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/6/8.
 */

public class ChooseWifiFragment extends DialogFragment {
    private View mView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.choose_wifi_fragemnt, null);
        ButterKnife.bind(this, mView);

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(mView).create();
        setWindowStyle(dialog);
        return dialog;
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


    @OnClick({R.id.Dolphin_Test_1, R.id.Dolphin_Test_2, R.id.Dolphin_Test_3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Dolphin_Test_1:
                MyIntentService.stopIntentService(getActivity());
                MyIntentService.startActionWifiConnect(getActivity(),"Dolphin2_AP_Test1","bmwdolphin2","172.169.10.8");
                break;
            case R.id.Dolphin_Test_2:
                MyIntentService.stopIntentService(getActivity());
                MyIntentService.startActionWifiConnect(getActivity(),"Dolphin2_AP_Test2","bmwdolphin2","172.169.10.8");
                break;
            case R.id.Dolphin_Test_3:
                MyIntentService.stopIntentService(getActivity());
                MyIntentService.startActionWifiConnect(getActivity(),"Dolphin2_AP_Test3","bmwdolphin2","172.169.10.8");
                break;
        }
        dismiss();
    }
}
