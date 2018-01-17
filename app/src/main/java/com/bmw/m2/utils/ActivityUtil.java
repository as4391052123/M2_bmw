package com.bmw.m2.utils;

import android.content.Context;
import android.content.Intent;

import com.bmw.m2.Constant;
import com.bmw.m2.views.activity.FileShowNewActivity;
import com.bmw.m2.views.activity.SettingActivity;

/**
 * Created by admin on 2017/7/20.
 */

public class ActivityUtil {
    public static void goToFileShowActivity(Context context, boolean isPictureActivity) {
        Intent intent = new Intent(context, FileShowNewActivity.class);
        intent.putExtra(Constant.IS_PICTURE_ACTIVITY, isPictureActivity);
        context.startActivity(intent);
    }

    public static void goToSettingActivity(Context context){
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }
}
