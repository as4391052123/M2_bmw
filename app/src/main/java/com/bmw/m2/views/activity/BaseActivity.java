package com.bmw.m2.views.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.bmw.m2.VLCApplication;
import com.bmw.m2.utils.ThrowUtil;


public class BaseActivity extends AppCompatActivity {

    public String TAG = ThrowUtil.TAG;
    private VLCApplication application;
    private BaseActivity oContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (application == null) {
            // 得到Application对象
            application = (VLCApplication) getApplication();
        }
        oContext = this;// 把当前的上下文对象赋值给BaseActivity
        addActivity();// 调用添加方法
    }

    public Context context() {
        return oContext;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity();
    }

    Toast mToast;

    public void toast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }

    public void toast(int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), resId,
                    Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
        }
        mToast.show();
    }

    public  void log(String msg) {
        Log.i(TAG, msg);
    }

    public  void error(String msg) {
        Log.e(TAG, msg);
    }

    //添加Activity方法
    public void addActivity() {
        application.addActivity_(oContext);// 调用myApplication的添加Activity方法
    }

    //销毁当个Activity方法
    public void removeActivity() {
        application.removeActivity_(oContext);// 调用myApplication的销毁单个Activity方法
    }

    //销毁所有Activity方法
    public void removeALLActivity() {
        application.removeALLActivity_();// 调用myApplication的销毁所有Activity方法
        System.exit(0);
    }

}
