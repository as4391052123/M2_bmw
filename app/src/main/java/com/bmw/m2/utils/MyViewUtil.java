package com.bmw.m2.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.bmw.m2.Constant;
import com.bmw.m2.R;
import com.bmw.m2.model.RemoteDeviceInfo;

/**
 * Created by admin on 2017/7/22.
 */

public class MyViewUtil {

    public static int getBatteryId(int batery){
        if(batery == 0){
            return R.drawable.ic_battery_0;
        }else if(batery<=10){
            return R.drawable.ic_battery_1;
        }else if(batery<=20){
            return R.drawable.ic_battery_2;
        }else if(batery<=30){
            return R.drawable.ic_battery_3;
        }else if(batery<=40){
            return R.drawable.ic_battery_4;
        }else if(batery<=50){
            return R.drawable.ic_battery_5;
        }else if(batery<=60){
            return R.drawable.ic_battery_6;
        }else if(batery<=70){
            return R.drawable.ic_battery_7;
        }else if(batery<=80){
            return R.drawable.ic_battery_8;
        }else if(batery<=90){
            return R.drawable.ic_battery_9;
        }else {
            return R.drawable.ic_battery_10;
        }
    }

    public static int getLiftImgId(int value){
        if(value<=5){
            return R.mipmap.crawler_0;
        }else if(value<=13){
            return R.mipmap.crawler_1;
        }else if(value<=26){
            return R.mipmap.crawler_2;
        }else if(value<=39){
            return R.mipmap.crawler_3;
        }else if(value<=52){
            return R.mipmap.crawler_4;
        }else if(value<=65){
            return R.mipmap.crawler_5;
        }else if(value<=78){
            return R.mipmap.crawler_6;
        }else if(value<=91){
            return R.mipmap.crawler_7;
        }else if(value<=104){
            return R.mipmap.crawler_8;
        }else if(value<=117){
            return R.mipmap.crawler_9;
        }else{
            return R.mipmap.crawler_10;
        }
    }

    public static void setSystemBattery(Context context, Intent intent, final TextView textView){
        if(textView == null)
            return;
        int rowLevel = intent.getIntExtra("level", -1);
        int scale = intent.getIntExtra("scale", -1);

        if (rowLevel != -1 && scale != -1) {
            final float sysBattery = ((float) rowLevel / scale) * 100;
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setBackgroundResource(getBatteryId((int) sysBattery));
                    textView.setText((int)sysBattery+"%");
                }
            });
//            setSystemBattery((int) sysBattery);
        }
    }

    public static void setDeviceBattery(Intent intent, final TextView textView){
        final RemoteDeviceInfo remoteDeviceInfo = (RemoteDeviceInfo) intent.getSerializableExtra(Constant.KEY_RESULT_INFO_PAXINGQI);
        if(remoteDeviceInfo!=null && textView!=null){
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setBackgroundResource(getBatteryId(remoteDeviceInfo.getBattery()));
                    textView.setText(remoteDeviceInfo.getBattery()+"%");
                }
            });
        }
    }
}
