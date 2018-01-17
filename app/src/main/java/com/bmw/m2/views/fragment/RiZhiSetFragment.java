package com.bmw.m2.views.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.bmw.m2.R;
import com.bmw.m2.views.service.MyIntentService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/6/8.
 */

public class RiZhiSetFragment extends DialogFragment {

    @Bind(R.id.sw_rizhi_f)
    Switch sw_rizhi;
    @Bind(R.id.edt_rizhi_f)
    EditText edt_rizhi;
    private View mView;
    private static RiZhiSetFragment fragment;
    private static final String KEY_ISWRITERIZHI = "KEY_ISWRITERIZHI";
    private static final String KEY_RIZHI_TIME = "KEY_RIZHI_TIME";


    public static RiZhiSetFragment getInstance(boolean isWrite,long time){
        if(fragment == null){
            fragment = new RiZhiSetFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_ISWRITERIZHI,isWrite);
        bundle.putLong(KEY_RIZHI_TIME,time);
        fragment.setArguments(bundle);
        return fragment;
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
        p.width = (int) (dm.widthPixels * 0.4);    //宽度设置为全屏
        //设置生效
        getDialog().getWindow().setAttributes(p);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.rizhi_set, null);
        ButterKnife.bind(this, mView);

        sw_rizhi.setChecked(getArguments().getBoolean(KEY_ISWRITERIZHI));
        edt_rizhi.setText(""+getArguments().getLong(KEY_RIZHI_TIME));

        sw_rizhi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(listener != null){
                    listener.setIsWrite(isChecked);
                }
            }
        });

        edt_rizhi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(listener!=null && !TextUtils.isEmpty(s.toString()))
                    listener.setTime(Integer.valueOf(s.toString()));
            }
        });


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


    private OnRiZhiValueChangeListener listener;

    public void setListener(OnRiZhiValueChangeListener listener) {
        this.listener = listener;
    }

    public interface  OnRiZhiValueChangeListener{
        void setIsWrite(boolean isWrite);
        void setTime(long time);
    }

}
