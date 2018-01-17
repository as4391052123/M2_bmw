package com.bmw.m2.presenter;

/**
 * Created by admin on 2016/9/8.
 */
public interface FilePresenter {
    void initAdapter();
    void searching(String str);
    void deleteFile();
    void lastItem();
    void nextItem();
    void chooseAll();
    void cancelAll();
}
