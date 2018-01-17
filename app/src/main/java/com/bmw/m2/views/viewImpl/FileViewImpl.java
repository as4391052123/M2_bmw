package com.bmw.m2.views.viewImpl;

/**
 * Created by admin on 2016/9/8.
 */
public interface FileViewImpl {
    void isPlaying(boolean isPause);
    void setSeekbarProgress(int progress);
    void showPlayFailed();
    void showAllPlayTime(String time);
    void resetPlayView();
    void capturePicture(String path);
    void setSpeedTv(int speed);
    void pictureClick(String path);
    void isBtnDeleteShow(boolean isShow);
    void setEmptyPicture();
//    void showDialogFragment(DialogFragment dialogFragment,String tag);
}
