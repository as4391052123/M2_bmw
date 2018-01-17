package com.bmw.m2.views.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.bmw.m2.Constant;
import com.bmw.m2.R;
import com.bmw.m2.VLCApplication;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by admin on 2017/5/18.
 */

public class DialogBiaojiMutiFragment extends DialogFragment {

    @Bind(R.id.tiEdt_row1)
    TextInputEditText tiEdtRow1;
    @Bind(R.id.tiEdt_row2)
    TextInputEditText tiEdtRow2;
    @Bind(R.id.tiEdt_row3)
    TextInputEditText tiEdtRow3;
    @Bind(R.id.tiEdt_row4)
    TextInputEditText tiEdtRow4;
    @Bind(R.id.tl_row3)
    TextInputLayout tl_row3;
    @Bind(R.id.tl_row4)
    TextInputLayout tl_row4;
    private View mView;

    public static DialogBiaojiMutiFragment getInstance(
            String biaojiText1
            , String biaojiText2
            ,String biaojiText3
            ,String biaojiText4
    ) {
        DialogBiaojiMutiFragment fragment = new DialogBiaojiMutiFragment();
        Bundle bundle = new Bundle();
        bundle.putString("biaojiText1", biaojiText1);
        bundle.putString("biaojiText2", biaojiText2);
        bundle.putString("biaojiText3", biaojiText3);
        bundle.putString("biaojiText4", biaojiText4);
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
        p.width = (int) (dm.widthPixels * 0.5);    //宽度设置为全屏
        //设置生效
        getDialog().getWindow().setAttributes(p);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_biaoji_four, null);
        ButterKnife.bind(this, mView);
        if(VLCApplication.getSharedPreferences().getBoolean(Constant.KEY_IS_RECORDHEADER_ALWAYS_SHOW, false)){
            tl_row3.setVisibility(View.GONE);
            tl_row4.setVisibility(View.GONE);
        }

        tiEdtRow1.setText(getArguments().getString("biaojiText1"));
        tiEdtRow2.setText(getArguments().getString("biaojiText2"));
        tiEdtRow3.setText(getArguments().getString("biaojiText3"));
        tiEdtRow4.setText(getArguments().getString("biaojiText4"));

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setView(mView).create();
        setWindowStyle(alertDialog);
        return alertDialog;
    }


    private void setWindowStyle(Dialog dialog) {
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager manager = (WindowManager) getActivity().
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = window.getAttributes();  //获取对话框当前的参数值

        p.height = (int) (dm.heightPixels);   //高度设置为屏幕的0.3
        p.width = (int) (dm.widthPixels * 0.6);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);
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

    @OnClick({R.id.biaoji_sure, R.id.biaoji_cancel, R.id.biaoji_clear})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.biaoji_sure:
                dismiss();
                if (listener != null)
                    listener.finish(tiEdtRow1.getText().toString()
                            , tiEdtRow2.getText().toString()
                            ,tiEdtRow3.getText().toString()
                            ,tiEdtRow4.getText().toString()
                    );
                break;
            case R.id.biaoji_cancel:
                dismiss();
                break;
            case R.id.biaoji_clear:
                tiEdtRow1.post(new Runnable() {
                    @Override
                    public void run() {
                        tiEdtRow1.setText("");
                        tiEdtRow2.setText("");
                        tiEdtRow3.setText("");
                        tiEdtRow4.setText("");
                    }
                });
                break;
        }
    }

    private OnMutlSureClickListener listener;

    public void setOnMutlSureClickListener(OnMutlSureClickListener listener) {
        this.listener = listener;
    }

    public interface OnMutlSureClickListener {
        void finish(
                String value1
                , String value2
                , String value3
                , String value4
        );
    }
}
