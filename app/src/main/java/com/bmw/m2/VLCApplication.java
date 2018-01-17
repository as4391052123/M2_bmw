/*****************************************************************************
 * VLCApplication.java
 *****************************************************************************
 * Copyright © 2010-2013 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/
package com.bmw.m2;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bmw.m2.utils.DbHelper;
import com.bmw.m2.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VLCApplication extends Application {
    public final static String TAG = "main/VLC/VLCApplication";
    private static VLCApplication instance;

    public final static String SLEEP_INTENT = "org.videolan.vlc.SleepIntent";

    static Context mContext;
    static Resources mResources;
    private static final String PREF_M1S1 = "bmw.M2_PREF";
    private static long last_duration_time;
    private static String last_toast_msg;
    private List<Activity> oList;
    private static ImageLoader imageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        DbHelper.init(getApplicationContext());

        // Are we using advanced debugging - locale?
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String p = pref.getString("set_locale", "");
        if (p != null && !p.equals("")) {
            Locale locale;
            // workaround due to region code
            if(p.equals("zh-TW")) {
                locale = Locale.TRADITIONAL_CHINESE;
            } else if(p.startsWith("zh")) {
                locale = Locale.CHINA;
            } else if(p.equals("pt-BR")) {
                locale = new Locale("pt", "BR");
            } else if(p.equals("bn-IN") || p.startsWith("bn")) {
                locale = new Locale("bn", "IN");
            } else {
                /**
                 * Avoid a crash of
                 * java.lang.AssertionError: couldn't initialize LocaleData for locale
                 * if the user enters nonsensical region codes.
                 */
                if(p.contains("-"))
                    p = p.substring(0, p.indexOf('-'));
                locale = new Locale(p);
            }
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

        instance = this;

        // Initialize the database soon enough to avoid any race condition and crash
//        MediaDatabase.getInstance(this);
        // Prepare cache folder constants
//        AudioUtil.prepareCacheFolder(this);

        mContext = getApplicationContext();
        mResources = mContext.getResources();
        oList = new ArrayList<Activity>();
        imageLoader = new ImageLoader();
    }

    /**
     * Called when the overall system is running low on memory
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "System is running low on memory");

//        BitmapCache.getInstance().clear();
    }

    /**
     * @return the main context of the Application
     */
    public static Context getAppContext()
    {
        return instance;
    }

    /**
     * @return the main resources from the Application
     */
    public static Resources getAppResources()
    {
        if(instance == null) return null;
        return instance.getResources();
    }

    public synchronized static VLCApplication context(){
        return (VLCApplication) mContext;
    }

    public static Resources resources(){
        return mResources;
    }

    public static SharedPreferences getSharedPreferences(){
        return context().getSharedPreferences(PREF_M1S1,Context.MODE_PRIVATE);
    }

    public static void toast(String msg){
        toast(msg, Toast.LENGTH_SHORT);
    }

    public static void toast(String msg,int duration){
        if(msg != null && !msg.equalsIgnoreCase("")){
            long current_time = System.currentTimeMillis();
            if( !msg.equalsIgnoreCase(last_toast_msg) || current_time - last_duration_time>2000){
                View view = LayoutInflater.from(context()).inflate(R.layout.toast_view,null);
                TextView textView = (TextView) view.findViewById(R.id.toast_tv);
                textView.setText(msg);
                Toast toast = new Toast(context());
                toast.setView(view);
                toast.setDuration(duration);
                toast.show();

                last_duration_time = System.currentTimeMillis();
                last_toast_msg = msg;
            }
        }
    }


    /**
     * 添加Activity
     */
    public void addActivity_(Activity activity) {
// 判断当前集合中不存在该Activity
        if (!oList.contains(activity)) {
            oList.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(Activity activity) {
//判断当前集合中存在该Activity
        if (oList.contains(activity)) {
            oList.remove(activity);//从集合中移除
            activity.finish();//销毁当前Activity
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity_() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : oList) {
            activity.finish();
        }
    }

    public static void putBitmap(Bitmap bitmap,String key){
        imageLoader.putBitmap(bitmap,key);
    }
    public static Bitmap getBitmap(String key){
       return imageLoader.getBitmap(key);
    }
    public static void removeBitmap(String key){
        imageLoader.removeBitmapCache(key);
    }
    public static void clearBitmapCache(){
        imageLoader.clearBitmapCache();
    }



}
