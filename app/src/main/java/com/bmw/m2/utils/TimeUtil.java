package com.bmw.m2.utils;

import java.text.SimpleDateFormat;

/**
 * Created by admin on 2017/6/3.
 */

public class TimeUtil {

    public static String formatTime(Long ms,boolean isShowHour) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day).append(":");
        }
        if (hour >= 0) {
            if(isShowHour || hour!=0) {
                if (hour < 10) {
                    sb.append("0").append(hour).append(":");
                } else
                    sb.append(hour).append(":");
            }
        }
        if (minute >= 0) {
            if (minute < 10) {
                sb.append("0").append(minute).append(":");
            } else
                sb.append(minute).append(":");
        }
        if (second >= 0) {
            if (second < 10) {
                sb.append("0").append(second);
            } else
                sb.append(second);
        }
        /*if(milliSecond > 0) {
            sb.append(milliSecond).append(":");
        }*/
        return sb.toString();
    }

    public static String getTimeNum() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyyMMddHHmmss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    public static String getTimeHanZi(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy年MM月dd日HH时mm分ss秒SSS");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }
}
