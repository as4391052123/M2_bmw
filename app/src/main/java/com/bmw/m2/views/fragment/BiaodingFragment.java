package com.bmw.m2.views.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.bmw.m2.R;
import com.bmw.m2.presenter.ControlPresenter;
import com.bmw.m2.views.activity.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTouch;

/**
 * Created by admin on 2017/6/19.
 */

public class BiaodingFragment extends DialogFragment {


    @Bind(R.id.lift_biaoding_switch)
    Switch liftBiaodingSwitch;
    @Bind(R.id.ptz_biaoding_switch)
    Switch ptzBiaodingSwitch;
    private View mView;
    private static BiaodingFragment biaodingFragment;
    private ControlPresenter controlPresenter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        controlPresenter = ((MainActivity) activity).getContropresenter();
    }

    public static BiaodingFragment getInstance(boolean isOpenLiftBiaoding, boolean isOpenPtzBiaoding) {
        if (biaodingFragment == null) {
            biaodingFragment = new BiaodingFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("isOpenLiftBiaoding", isOpenLiftBiaoding);
        bundle.putBoolean("isOpenPtzBiaoding", isOpenPtzBiaoding);
        biaodingFragment.setArguments(bundle);
        return biaodingFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.biaoding_fragment, null);
        ButterKnife.bind(this, mView);
        boolean isOpenLiftBiaoding = getArguments().getBoolean("isOpenLiftBiaoding");
        boolean isOpenPtzBiaoding = getArguments().getBoolean("isOpenPtzBiaoding");

        liftBiaodingSwitch.setChecked(isOpenLiftBiaoding);
        ptzBiaodingSwitch.setChecked(isOpenPtzBiaoding);
        liftBiaodingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener != null)
                    listener.liftBiaoding_control(isChecked);
            }
        });
        ptzBiaodingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener != null)
                    listener.ptzBiaoding_control(isChecked);
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(mView).create();
        setWindowStyle(dialog);
        return dialog;
    }


    @OnTouch({R.id.lift_biaoding_max
            , R.id.lift_biaoding_min
            , R.id.ptz_rotate_zero
            , R.id.ptz_pitch_zero
            , R.id.ptz_pitch_max
            , R.id.ptz_pitch_min
    })
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchDown(view.getId());
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            touchUp(view.getId());
            dismiss();
        }
        return false;
    }


    private void touchDown(int viewId) {
        if (controlPresenter != null)
            switch (viewId) {
                case R.id.lift_biaoding_max:
//                    listener.lift_max();
                    controlPresenter.lift_Max();
                    break;
                case R.id.lift_biaoding_min:
//                    listener.lift_min();
                    controlPresenter.lift_min();
                    break;
                case R.id.ptz_rotate_zero:
//                    listener.ptz_rotate_zero();
                    controlPresenter.ptz_rotate_zero();
                    break;
                case R.id.ptz_pitch_zero:
//                    listener.ptz_pitch_zero();
                    controlPresenter.ptz_pitch_zero();
                    break;
                case R.id.ptz_pitch_max:
//                    listener.ptz_pitch_max();
                    controlPresenter.ptz_pitch_max();
                    break;
                case R.id.ptz_pitch_min:
//                    listener.ptz_pitch_min();
                    controlPresenter.ptz_pitch_min();
                    break;
            }
    }

    private void touchUp(int viewId) {
        if (controlPresenter != null)
            switch (viewId) {
                case R.id.lift_biaoding_max:
//                    listener.lift_biaoding_off();
                    controlPresenter.lift_default();
                    break;
                case R.id.lift_biaoding_min:
//                    listener.lift_biaoding_off();
                    controlPresenter.lift_default();
                    break;
                case R.id.ptz_rotate_zero:
//                    listener.ptz_biaoding_off();
                    controlPresenter.ptz_default();
                    break;
                case R.id.ptz_pitch_zero:
//                    listener.ptz_biaoding_off();
                    controlPresenter.ptz_default();
                    break;
                case R.id.ptz_pitch_max:
//                    listener.ptz_biaoding_off();
                    controlPresenter.ptz_default();
                    break;
                case R.id.ptz_pitch_min:
//                    listener.ptz_biaoding_off();
                    controlPresenter.ptz_default();
                    break;
            }
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


    public interface OnBiaodingChangedListener {
        void liftBiaoding_control(boolean isOpen);

        void ptzBiaoding_control(boolean isOpen);
    }

    private OnBiaodingChangedListener listener;

    public void setOnBiaodingChangedListener(OnBiaodingChangedListener listener) {
        this.listener = listener;
    }
}
