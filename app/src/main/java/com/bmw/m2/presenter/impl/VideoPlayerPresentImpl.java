package com.bmw.m2.presenter.impl;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.SurfaceHolder;
import android.widget.SeekBar;
import android.widget.VideoView;


import com.bmw.m2.VLCApplication;
import com.bmw.m2.model.FileInfo;
import com.bmw.m2.model.Login_info;
import com.bmw.m2.presenter.VideoPlayerPresenter;
import com.bmw.m2.utils.BitmapUtils;
import com.bmw.m2.utils.FileUtil;
import com.bmw.m2.views.viewImpl.FileViewImpl;

import org.MediaPlayer.PlayM4.Player;
import org.MediaPlayer.PlayM4.PlayerCallBack;

import java.io.File;
import java.io.IOException;

import static com.bmw.m2.utils.ThrowUtil.error;
import static com.bmw.m2.utils.ThrowUtil.log;
import static com.bmw.m2.utils.TimeUtil.formatTime;


/**
 * Created by admin on 2017/5/2.
 */

public class VideoPlayerPresentImpl implements VideoPlayerPresenter {
    private final int iPort;
    private boolean isStartPlay;
    private boolean isPlaying;
    private FileViewImpl fileView;
    private String mPath;
    private SeekBar seekBar;

    private VideoView videoView;
    private long allVideoTime;


    public VideoPlayerPresentImpl(FileViewImpl fileView, String mPath, VideoView videoView, SeekBar seekBar) {
        iPort = Player.getInstance().getPort();
        this.fileView = fileView;
        this.mPath = mPath;
        this.videoView = videoView;
        this.seekBar = seekBar;
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                long time = mp.getDuration();
                allVideoTime = time;
                VideoPlayerPresentImpl.this.fileView.showAllPlayTime(formatTime(time,false));
                VideoPlayerPresentImpl.this.seekBar.setMax((int)allVideoTime);
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playStop();
            }
        });

    }

    @Override
    public void setPlayPath(String path) {
        mPath = path;
    }

    @Override
    public void playStart() {
        if (isStartPlay)
            if (!isPlaying) {
                videoView.start();
                isPlaying = true;
                fileView.isPlaying(true);
            } else {
                videoView.pause();
                isPlaying = false;
                fileView.isPlaying(false);
            }
        else {
            if (mPath != null) {
                fileView.setSeekbarProgress(0);
                startPlayVideo();
            }
        }
    }


    public void startPlayVideo() {

        if (mPath == null)
            return;

        if (isStartPlay) {
            videoView.stopPlayback();
        }

        videoView.setVideoURI(Uri.parse(mPath));


        videoView.start();

//        setFileAllTime();
        isStartPlay = true;
        fileView.isPlaying(true);
        isPlaying = true;
    }


   /* private void setFileAllTime() {
        long allTime = videoView.getDuration();
        LogUtil.log(" time = "+ allTime);

    }*/


    @Override
    public void playStop() {
        if (isStartPlay) {
            videoView.stopPlayback();
            fileView.resetPlayView();
            isStartPlay = false;
            isPlaying = false;
        }
    }

    @Override
    public String getPlayedTimeEx() {
        long time = videoView.getCurrentPosition();
        return formatTime(time,false);
    }

    public long getAllTime(){
        return allVideoTime;
    }


    public void playCutPicture() {
        if (isStartPlay) {
            playStart();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MediaMetadataRetriever rev = new MediaMetadataRetriever();

                    rev.setDataSource(VLCApplication.context(), Uri.parse(mPath)); //这里第一个参数需要Context，传this指针

                    Bitmap saveBitmap= rev.getFrameAtTime(videoView.getCurrentPosition() * 1000,
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

                    StringBuilder saveNameBuilder = new StringBuilder();
                    if(mPath!= null && mPath.contains(".")){
                        String nameFromPaht = mPath.substring(mPath.lastIndexOf("/")+1,mPath.length());
//                        String taskName = nameFromPaht.substring(0,nameFromPaht.indexOf("_")+1);
//                        String taskPlace = nameFromPaht.substring(nameFromPaht.indexOf("_")+1,nameFromPaht.lastIndexOf("_")+1);
//                        saveNameBuilder.append(taskName).append(taskPlace);
                        saveNameBuilder.append(nameFromPaht.substring(0,nameFromPaht.lastIndexOf(".")));

                    }else {
                        error("截图路径错误！");
                        return;
                    }

//                    saveNameBuilder.append(UrlUtil.getFileName()).append(".jpg");

                    final String savePath = FileUtil.getFileSavePath() + FileInfo.picturePath;

                    File file = new File(savePath+saveNameBuilder.toString()+".jpg");
                    if(file.exists()) {
                        int num = 0;
                        while (file.exists()) {
                            num++;
                            file = new File(savePath + saveNameBuilder.toString() + "-" + num + ".jpg");
                        }
                        saveNameBuilder.append("-").append(num);
                    }
                    saveNameBuilder.append(".jpg");
                    final String saveName = saveNameBuilder.toString();
                    try {
                        BitmapUtils.save(savePath, saveName, saveBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (saveBitmap != null)
                            saveBitmap.recycle();

                        saveBitmap = null;
                    }
                    fileView.capturePicture(savePath + saveName);
                }
            }).start();

        }
    }




    public void setPlayPlace(float place){
        int currentTime = (int)(place);
        log(" seekto = "+ currentTime);
        videoView.seekTo(currentTime);
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public boolean isStartPlay() {
        return isStartPlay;
    }

    public void release(){
        isStartPlay = false;
        isPlaying = false;
        videoView.destroyDrawingCache();
    }
}
