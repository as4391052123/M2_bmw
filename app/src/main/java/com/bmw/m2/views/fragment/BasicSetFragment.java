package com.bmw.m2.views.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.bmw.m2.R;
import com.bmw.m2.utils.FragmentUtil;
import com.bmw.m2.views.dialog.ScreenLightDialog;
import com.bmw.m2.views.dialog.SystemMsgDialog;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/4/7.
 */

public class BasicSetFragment extends Fragment {
    private View root;
    private boolean isInitBmob;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_basic_setting, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(!isInitBmob)
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                isInitBmob = true;
//                //默认初始化
//                Bmob.initialize(getActivity(), "2733df1cda91841bbac921e164dc70d4");
//            }
//        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(root);
    }

    private void setScreenLight() {
        //取得当前亮度
        int normal = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 255);
//        AdvancedSetDialog dialog = new AdvancedSetDialog(this,normal);
//        ScreenPopupWindow dialog = new ScreenPopupWindow(this,normal);
        ScreenLightDialog dialog = new ScreenLightDialog(getActivity(), normal);
//        dialog.showPopupWindow(main);
        dialog.setDialogSeeekbarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //取得当前进度
                int tmpInt = seekBar.getProgress();

                //当进度小于80时，设置成40，防止太黑看不见的后果。
                if (tmpInt < 40) {
                    tmpInt = 40;
                }

                //根据当前进度改变亮度
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, tmpInt);
                tmpInt = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, -1);
                WindowManager.LayoutParams wl = getActivity().getWindow()
                        .getAttributes();

                float tmpFloat = (float) tmpInt / 255;
                if (tmpFloat > 0 && tmpFloat <= 1) {
                    wl.screenBrightness = tmpFloat;
                }
                getActivity().getWindow().setAttributes(wl);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
            }
        });
    }


    public void updateApk(String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), name)),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({ R.id.light_set, R.id.sys_stat, R.id.sys_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.light_set:
                setScreenLight();
                break;
            case R.id.sys_stat:
                new SystemMsgDialog(this.getActivity(), getVersion());
                break;
            case R.id.sys_update:
                DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance(
                        getString(R.string.isUpdateApp),
                        getString(R.string.updateAppAndClosePreview),
                        getString(R.string.sure),
                        getString(R.string.cancel), true);
                dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
                    @Override
                    public void sure() {
//                        startActivity(new Intent(BasicSetFragment.this.getActivity(), UpdateActivity.class));
                    }

                    @Override
                    public void cancel() {

                    }
                });
                FragmentUtil.showDialogFragment(getActivity().getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");

                break;
        }
    }


    // 获取当前版本的版本号
    private String getVersion() {
        try {
            PackageManager packageManager = getActivity().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getActivity().getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return getString(R.string.noVersion);
        }
    }


}
