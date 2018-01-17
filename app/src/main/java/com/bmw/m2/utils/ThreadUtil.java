package com.bmw.m2.utils;

/**
 * Created by admin on 2017/5/17.
 */

public class ThreadUtil {
    public static void sleep(long time ){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
