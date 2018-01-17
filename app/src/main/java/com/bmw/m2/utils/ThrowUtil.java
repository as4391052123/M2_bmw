package com.bmw.m2.utils;

import android.util.Log;

/**
 * Created by admin on 2017/5/16.
 */

public class ThrowUtil {
    public static final String TAG = "M2_debug";

    public static void throwException(String msg){
        try {
            throw new Exception(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static  void log(String msg){
        Log.i(TAG, msg);
    }

    public static void error(String msg){
        Log.e(TAG, msg );
    }
}
