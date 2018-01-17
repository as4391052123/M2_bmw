package com.bmw.m2.views.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.bmw.m2.Constant;
import com.bmw.m2.R;
import com.bmw.m2.VLCApplication;
import com.bmw.m2.utils.FileUtil;
import com.bmw.m2.utils.FragmentUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/4/7.
 */

public class AdvanceSetFragment extends Fragment {

    @Bind(R.id.switch_advance_yingjiema)
    Switch switchYingjiema;
    @Bind(R.id.tv_advance_fileSavePath)
    Switch switchSavePath;
    @Bind(R.id.switch_advance_autoWifi)
    Switch switchAutoWifi;
    private View root;



    @OnClick({R.id.tv_update_device})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_update_device:
                openUpdateDialog();
                break;
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_advance_setting, container, false);
        ButterKnife.bind(this, root);

        initYingJieMa();
        initFileSavePath();
        initSwitchAutoWifi();
        return root;
    }


    public void openUpdateDialog(){
        DeviceUpdateDialogFg updateDialogFg = new DeviceUpdateDialogFg();
        FragmentUtil.showDialogFragment(getActivity().getSupportFragmentManager(),updateDialogFg,"DeviceUpdateDialogFg");
    }

    private void initSwitchAutoWifi() {
        boolean isConnectWifiAuto = VLCApplication.getSharedPreferences().getBoolean(Constant.IS_CONNECT_WIFI_AUTO, false);
        switchAutoWifi.setChecked(isConnectWifiAuto);
        switchAutoWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                VLCApplication.getSharedPreferences().edit().putBoolean(Constant.IS_CONNECT_WIFI_AUTO, b).commit();
                if (b) {

                }
            }
        });
    }


    private void initFileSavePath() {
        boolean isSaveFileToSdcard = VLCApplication.getSharedPreferences().getBoolean(Constant.FILE_IS_SAVE_TO_SDCARD, false);
        switchSavePath.setChecked(isSaveFileToSdcard);
        switchSavePath.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    sureCheckSDcard();
                else
                    VLCApplication.getSharedPreferences().edit().putBoolean(Constant.FILE_IS_SAVE_TO_SDCARD, b).commit();
            }
        });
    }

    private void sureCheckSDcard() {
        List<String> sdcardList = FileUtil.getRealExtSDCardPath(this.getActivity());
        if (sdcardList.size() <= 1) {
            onleInlaySdcardExist();
            return;
        }
        if (!sdcardList.get(sdcardList.size() - 1).contains("Android/data")) {
            VLCApplication.getSharedPreferences().edit().putBoolean(Constant.FILE_IS_SAVE_TO_SDCARD, true).commit();
            FileUtil.pathIsExist();
            return;
        }

        DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance(getString(R.string.alarm), getString(R.string.fileSaveDangerousIn4) + "\n\n  " + getString(R.string.fileSaveTo) + "\n" +
                sdcardList.get(1) + getString(R.string.canMove) + " \n\n " + getString(R.string.fileDeleteAfterAppDelete) + "\n\n " + getString(R.string.sureSaveToSdcard), null, null, true);
        dialogNormalFragment.isNeedDismissCancel(true);
        dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
            @Override
            public void sure() {
                VLCApplication.getSharedPreferences().edit().putBoolean(Constant.FILE_IS_SAVE_TO_SDCARD, true).commit();
                FileUtil.pathIsExist();
            }

            @Override
            public void cancel() {
                boolean isSaveFileToSdcard = VLCApplication.getSharedPreferences().getBoolean(Constant.FILE_IS_SAVE_TO_SDCARD, false);
                if (!isSaveFileToSdcard)
                    switchSavePath.setChecked(false);
            }
        });
        showDialogFragment(dialogNormalFragment, "DialogNormalFragment");
    }


    private void showDialogFragment(DialogFragment dialogFragment, String tag) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null)
            fragmentTransaction.remove(fragment);

        dialogFragment.show(fragmentTransaction, tag);
    }

    private void onleInlaySdcardExist() {
        VLCApplication.toast(getString(R.string.noOutSdcard));
        switchSavePath.setChecked(false);
    }

    private void initYingJieMa() {
        boolean isYingJieMaFirst = VLCApplication.getSharedPreferences().getBoolean(Constant.IS_YINGJIEMA_FIRST, false);
        switchYingjiema.setChecked(isYingJieMaFirst);
        switchYingjiema.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                VLCApplication.getSharedPreferences().edit().putBoolean(Constant.IS_YINGJIEMA_FIRST, b).commit();
                reLoginHK();
            }
        });
    }

    private void reLoginHK() {
        Intent intent = new Intent();
        intent.setAction("data.receiver");
        intent.putExtra("isReLoginHK", true);
        this.getActivity().sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(root);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }



}
