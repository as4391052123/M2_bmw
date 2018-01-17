package com.bmw.m2.presenter;

/**
 * Created by admin on 2016/9/30.
 */
public interface VideoCutPresenter {
    void record(String name, String place, String startWell, String endWell, boolean isAddKanban);
    void capture(String name, String place, String startWell, String endWell);

    void recordOnHk(String name, String place, String startWell, String endWell, boolean isAddKanban);
//    void reRecord(int count);
    void release();
}
