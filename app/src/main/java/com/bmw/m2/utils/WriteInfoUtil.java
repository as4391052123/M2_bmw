package com.bmw.m2.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import static com.bmw.m2.utils.ThrowUtil.log;

/**
 * Created by admin on 2017/9/29.
 */

public class WriteInfoUtil {

    FileOutputStream out = null;
    BufferedOutputStream bout = null;
    FileWriter fileWriter = null;
    BufferedWriter bufferedWriter = null;
    private static WriteInfoUtil writeInfoUtil;

    public static WriteInfoUtil getInstance() {
        if (writeInfoUtil == null) {
            synchronized (WriteInfoUtil.class) {
                writeInfoUtil = new WriteInfoUtil();
            }
        }
        return writeInfoUtil;
    }

    private WriteInfoUtil() {

    }

    public void init(String filePath, String indexPath) {
        initFile(filePath);
        initIndexFile(indexPath);
    }

    public void release() {
        try {

            if (bout != null)
                bout.close();
            if (out != null)
                out.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
            if (fileWriter != null)
                fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initIndexFile(String indexPath) {
        File file = new File(indexPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fileWriter = new FileWriter(file, true);
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            out = new FileOutputStream(file, true);
            bout = new BufferedOutputStream(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writeByteToFile(byte[] bytes, int len) throws IOException {
        log("rtp封装流 " + len);
        bout.write(bytes, 0, len);
        bout.flush();
    }

    public void writeIntergeToFile(int i) throws IOException {

        String string = String.valueOf(i);
        bufferedWriter.write(string, 0, string.length());
        bufferedWriter.newLine();
        bufferedWriter.flush();
        fileWriter.flush();

    }
}
