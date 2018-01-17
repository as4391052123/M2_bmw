package com.bmw.m2.utils;

import java.io.Closeable;
import java.io.IOException;

import static com.bmw.m2.utils.ThrowUtil.error;

/**
 * Created by admin on 2017/5/17.
 */

public class StreamUtil {
    public static void close(Closeable closeable){
        if(closeable != null)
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
                error("关闭流出错："+e);
            }
    }
}
