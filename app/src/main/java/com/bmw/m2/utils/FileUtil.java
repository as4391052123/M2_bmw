package com.bmw.m2.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;


import com.bmw.m2.Constant;
import com.bmw.m2.VLCApplication;
import com.bmw.m2.model.FileInfo;
import com.lidroid.xutils.util.LogUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.bmw.m2.utils.ThrowUtil.log;

/**
 * Created by admin on 2017/4/27.
 */

public class FileUtil {

    public static List<Integer> list = new ArrayList<>();

/*
    public static boolean writeByteToFile(String filePath,byte[] bytes,int len){


        File file = new File(filePath);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(file,true);
            BufferedOutputStream bout = new BufferedOutputStream(out);
            log("rtp封装流 "+ len);
            bout.write(bytes,0,len);
            bout.flush();
            bout.close();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean writeIntergeToFile(String filePath,int i){
        File file = new File(filePath);
        String string = String.valueOf(i);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(file,true);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(string,0,string.length());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            fileWriter.flush();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }*/

    public static boolean writeStringToFile(File file,boolean isAddToEnd,String string){
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(file,isAddToEnd);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(string,0,string.length());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            fileWriter.flush();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //得到文件夹下所有文件列表
    public static List<File> getAllFiles(String path) {
        File file = new File(path);
        if (!file.exists() || file.isFile())
            return null;
        File[] files = file.listFiles();
        if (files.length > 0) {
            List<File> list = new ArrayList<File>();
            for (File f : files) {
                if (f.isFile())
                    list.add(f);
            }
            file = null;
            files = null;
            return list;
        } else {
            return null;
        }
    }

    public static String getFileSavePath() {
        List<String> list = getRealExtSDCardPath(VLCApplication.context());
        boolean isSaveToSdcard = VLCApplication.getSharedPreferences().getBoolean(Constant.FILE_IS_SAVE_TO_SDCARD, false);
        if (!isSaveToSdcard) {
            return list.get(0);
        } else {
            if (list.size() > 1)
                return list.get(list.size() - 1);
            VLCApplication.getSharedPreferences().
                    edit().
                    putBoolean(Constant.FILE_IS_SAVE_TO_SDCARD, false).
                    commit();
            return list.get(0);
        }
    }


    /**
     * 路径是否存在，不能存在则创建
     */
    public static void pathIsExist() {
        File file = new File(getFileSavePath() + FileInfo.videoPath);
        if (!file.exists())
            file.mkdirs();

        File file1 = new File(getFileSavePath() + FileInfo.picturePath);
        if (!file1.exists())
            file1.mkdirs();
    }


    public static List<String> getRealExtSDCardPath(Context context) {
        List<String> sdcardList = new ArrayList<>();
        String[] allSdcard = getExtSDCardPath(context);

        String inlaySDcard = getSDPath();
        sdcardList.add(inlaySDcard);

        if (allSdcard.length == 0 || allSdcard.length == 1)
            return sdcardList;

        for (String sdPath : allSdcard) {
            if (sdPath.contains(inlaySDcard))
                continue;
            if (canSdcardWrite(sdPath)) {
                sdcardList.add(sdPath);
            } else {
                File[] sdcardDataLogFiles = getSdcardDataLog(context);
                if (sdcardDataLogFiles.length <= 1)
                    continue;
                String sdcardDataLog = getSDcardDataLog(sdPath, sdcardDataLogFiles);
                if (sdcardDataLog != null)
                    sdcardList.add(sdcardDataLog);
            }

        }

        return sdcardList;
    }

    private static String getSDcardDataLog(String sdPath, File[] sdcardDataLogFiles) {
        for (File file : sdcardDataLogFiles) {
            if (file != null)
                if (file.toString().contains(sdPath) && canSdcardWrite(file.toString()))
                    return file.toString();
        }
        return null;
    }


    private static boolean canSdcardWrite(String sdPath) {
        File file = new File(sdPath + "/a.txt");
        try {
            file.createNewFile();
            file.delete();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String[] getExtSDCardPath(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context
                .STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            return (String[]) invoke;
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getSDPath() {

        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            return Environment.getExternalStorageDirectory().toString();
        } else
            return Environment.getDownloadCacheDirectory().toString();
    }

    private static File[] getSdcardDataLog(Context context) {
        File[] filearray = new File[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            filearray = context.getExternalFilesDirs(null);
        }
        return filearray;
    }

    public static List<Float> getDiskCapacity() {
        String path = getFileSavePath();
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        List<Float> diskSizeList = new ArrayList<>();
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long totalBlockCount = stat.getBlockCount();
        long feeBlockCount = stat.getAvailableBlocks();
        float totleSize = (float) (blockSize * totalBlockCount) / (1024 * 1024 * 1024);
        float freeSize = (float) (blockSize * feeBlockCount) / (1024 * 1024 * 1024);
        float usedSize = totleSize - freeSize;
        totleSize = (float) (Math.round(totleSize * 100)) / 100;
        freeSize = (float) (Math.round(freeSize * 100)) / 100;
        usedSize = (float) (Math.round(usedSize * 100)) / 100;

        diskSizeList.add(totleSize);
        diskSizeList.add(freeSize);
        diskSizeList.add(usedSize);
        return diskSizeList;
    }

}
