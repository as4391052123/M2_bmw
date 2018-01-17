package com.bmw.m2.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bmw.m2.Constant;
import com.bmw.m2.R;
import com.bmw.m2.utils.MyViewUtil;
import com.bmw.m2.views.fragment.AdvanceSetFragment;
import com.bmw.m2.views.fragment.BasicSetFragment;
import com.bmw.m2.views.fragment.DialogNormalFragment;
import com.bmw.m2.views.fragment.EnvironmentSetFragment;
import com.bmw.m2.views.fragment.OnDialogFragmentClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bmw.m2.utils.FragmentUtil.showDialogFragment;

public class SettingActivity extends BaseActivity {


    TextView batteryNum_system;
    @Bind(R.id.setting_containerRg)
    RadioGroup containerRg;
    @Bind(R.id.setting_fragment_contain)
    FrameLayout fragmentContain;


    @Bind(R.id.tv_battery_device)
    TextView tv_battery_device;
    @Bind(R.id.tv_battery_terminal)
    TextView tv_battery_terminal;

    private EnvironmentSetFragment environmentSetFragment;
    private AdvanceSetFragment advanceSetFragment;
    private BasicSetFragment basicSetFragment;
    private FragmentManager fragmentManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initBroadcastReceiver();


    }

    @Override
    protected void onResume() {
        super.onResume();
        initFragment();
        initRadioGroup();
    }


    private void initFragment() {
        basicSetFragment = new BasicSetFragment();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.setting_fragment_contain, basicSetFragment);
        transaction.commit();


    }

    private void initRadioGroup() {
        containerRg.check(R.id.basic_setingRd);
        containerRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                FragmentTransaction transaction1 = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentById(R.id.setting_fragment_contain);
                switch (i) {
                    case R.id.basic_setingRd:
                        transaction1.replace(R.id.setting_fragment_contain, basicSetFragment);
                        transaction1.remove(fragment);
                        break;
                    case R.id.advance_settingRd:
                        if (advanceSetFragment == null) {
                            advanceSetFragment = new AdvanceSetFragment();
                        }
                        transaction1.replace(R.id.setting_fragment_contain, advanceSetFragment);
                        transaction1.remove(fragment);
                        break;
                    case R.id.environment_settingRd:
                        if (environmentSetFragment == null)
                            environmentSetFragment = new EnvironmentSetFragment();
                        transaction1.replace(R.id.setting_fragment_contain, environmentSetFragment);
                        transaction1.remove(fragment);
                        break;
                }
                transaction1.commit();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        unregisterReceiver(system_battery_receiver);
    }

    @OnClick({
            R.id.setting_goback
            ,R.id.cit_close_app
    })
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.preview_closeApp:
//                btnClickCloseApp();
//                break;
            case R.id.setting_goback:
                removeActivity();
                break;
            case R.id.cit_close_app:
                removeALLActivity();
                break;
        }
    }

    private void btnClickCloseApp() {

        DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance(getString(R.string.exitingApp), getString(R.string.exitAppSure), null, null, true);
        dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
            @Override
            public void sure() {
                removeALLActivity();
            }

            @Override
            public void cancel() {

            }
        });
        showDialogFragment(getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");
    }


    //设置返回控制
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK

            removeActivity();
            return true;
        }
        if (keyCode == event.KEYCODE_HOME) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private BroadcastReceiver system_battery_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyViewUtil.setSystemBattery(context(),intent,tv_battery_terminal);
            MyViewUtil.setDeviceBattery(intent,tv_battery_device);
        }
    };

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(Constant.BROADCAST_GET_INFO_FROM_FRAME_DEVICE);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(system_battery_receiver, intentFilter);
    }

}
