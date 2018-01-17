package com.bmw.m2.presenter.impl;


import android.app.KeyguardManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.bmw.m2.VLCApplication;
import com.bmw.m2.presenter.VlcPresenter;
import com.bmw.m2.views.activity.MainActivity;

import org.videolan.libvlc.AWindow;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.AndroidUtil;

import java.io.File;
import java.util.ArrayList;

import static com.bmw.m2.utils.ThrowUtil.error;
import static com.bmw.m2.utils.ThrowUtil.log;

/**
 * Created by admin on 2017/5/22.
 */

public class VlcPresentImpl implements VlcPresenter, IVLCVout.Callback, IVLCVout.OnNewVideoLayoutListener {

    private MediaPlayer mMediaPlayer;
    //    public static final String uri = "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear2/prog_index.m3u8";
//    public static final String uri = "rtsp://admin:bmwadmin@192.168.2.13:554/media/video1";
    public static final String uri = "rtsp://admin:bmwadmin@172.169.10.88:554/media/video1";
//    private static final String uri = "rtsp://admin:bmwadmin@192.168.2.13:554/media/video1";
//    public static final int type =6;

    SurfaceView mSurfaceView;

    private MySurfaceCallBack mySurfaceCallBack;
//    private IVLCVout.OnNewVideoLayoutListener newVideoLayoutListener;
//    private int sw, sh;


    private LibVLC sLibVLC;

    public VlcPresentImpl(SurfaceView mSurfaceView) {
        this.mSurfaceView = mSurfaceView;
        mySurfaceCallBack = new MySurfaceCallBack();
//        this.newVideoLayoutListener = newVideoLayoutListener;
//        this.sw = sw;
//        this.sh = sh;
    }

    public void start() {
        ArrayList<String> options = new ArrayList<String>(50);
//        options.add("--no-audio-time-stretch");
        options.add("--stats");
        options.add("--network-caching=" + 300);

//        options.add("--avcodec-skip-frame");
//        options.add(false ? "2" : "0");
//        options.add("--avcodec-skip-idct");
//        options.add(false ? "2" : "0");
        sLibVLC = new LibVLC(VLCApplication.getAppContext(), options);
        init();
    }


    public void release() {

        isStartPlay = false;
        if (isStartPlay)
            mMediaPlayer.stop();
        if (mMediaPlayer != null) {
            mMediaPlayer.getVLCVout().detachViews();
            mMediaPlayer.getMedia().clearSlaves();
            mMediaPlayer.getMedia().release();
            mMediaPlayer.setEventListener(null);
            this.onSurfacesDestroyed(mMediaPlayer.getVLCVout());
            sLibVLC.release();
            mMediaPlayer.release();
            mSurfaceView.getHolder().removeCallback(mySurfaceCallBack);
            mSurfaceView.destroyDrawingCache();
        }
    }


    private void init() {

//        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mMediaPlayer = newMediaPlayer();
//        mMediaPlayer.setEqualizer(VLCOptions.getEqualizer(this));

      /*  if (!VLCInstance.testCompatibleCPU(this)) {
            finish();
            return;
        }*/
        playIndex();

    }


    private MediaPlayer newMediaPlayer() {
        final MediaPlayer mp = new MediaPlayer(sLibVLC);
//        final String aout = VLCOptions.getAout(mSettings);
//        if (aout != null)
//            mp.setAudioOutput(aout);
//        mp.getVLCVout().addCallback(this);

        return mp;
    }

    public void playIndex() {
        final Media media = new Media(sLibVLC, Uri.parse(uri));
        media.setHWDecoderEnabled(false, false);
//        media.addOption(":no-mediacodec-dr");
//        media.addOption(":no-omxil-dr");
//        VLCApplication.runBackground(new Runnable() {
//            @Override
//            public void run() {
//                mMediaPlayer.addSlave(type, Uri.parse(uri), false);
//            }
//        });

        mMediaPlayer.setMedia(media);
        media.release();
        log("playIndex: play0");
        mMediaPlayer.setEventListener(mMediaPlayerListener);
        mMediaPlayer.play();


    }


    private boolean isStartPlay;
    private final MediaPlayer.EventListener mMediaPlayerListener = new MediaPlayer.EventListener() {
        KeyguardManager keyguardManager = (KeyguardManager) VLCApplication.getAppContext().getSystemService(Context.KEYGUARD_SERVICE);

        @Override
        public void onEvent(MediaPlayer.Event event) {
//            log("mMediaPlayerListener onEvent: " + Integer.toHexString(event.type));
            switch (event.type) {
                case MediaPlayer.Event.Playing:

                    break;
                case MediaPlayer.Event.Paused:

                    break;
                case MediaPlayer.Event.Stopped:
                    isStartPlay = false;
                    break;
                case MediaPlayer.Event.EndReached:

                    break;
                case MediaPlayer.Event.EncounteredError:
                    break;
                case MediaPlayer.Event.TimeChanged:
                    break;
                case MediaPlayer.Event.PositionChanged:
                    break;
                case MediaPlayer.Event.Vout:
                    break;
                case MediaPlayer.Event.ESAdded:

                    if (!isStartPlay) {
                        startPlay();
                        isStartPlay = true;
                    }
                    break;
                case MediaPlayer.Event.ESDeleted:
                    break;
                case MediaPlayer.Event.PausableChanged:
                    break;
                case MediaPlayer.Event.SeekableChanged:
                    break;
                case MediaPlayer.Event.MediaChanged:
            }

        }
    };

    private void startPlay() {
        mMediaPlayer.getVLCVout().setVideoView(mSurfaceView);
        mMediaPlayer.getVLCVout().addCallback(this);
        mMediaPlayer.getVLCVout().attachViews(this);
        mSurfaceView.getHolder().addCallback(mySurfaceCallBack);

        log("sw = " + mSurfaceView.getWidth() + " sh = " + mSurfaceView.getHeight());
        mMediaPlayer.getVLCVout().setWindowSize(mSurfaceView.getWidth(), mSurfaceView.getHeight());
//        mMediaPlayer.setAspectRatio(null);
//        mMediaPlayer.setAspectRatio(mSurfaceView.getWidth()+":"+mSurfaceView.getHeight());
//        mMediaPlayer.setScale(0);
//        mMediaPlayer.setAspectRatio(""+(mSurfaceView.getWidth())+":"+mSurfaceView.getHeight());

//        ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
//        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
//        mSurfaceView.setLayoutParams(lp);

        /*lp = surfaceFrame.getLayoutParams();
        lp.width  = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        surfaceFrame.setLayoutParams(lp);*/

        fillScreen(mSurfaceView.getWidth(), mSurfaceView.getHeight());
    }

    private void fillScreen(int displayW, int displayH) {
        Media.VideoTrack vtrack = mMediaPlayer.getCurrentVideoTrack();
        if (vtrack == null) {
            log("vtrack == null");
            return;
        }
        final boolean videoSwapped = vtrack.orientation == Media.VideoTrack.Orientation.LeftBottom
                || vtrack.orientation == Media.VideoTrack.Orientation.RightTop;

        mMediaPlayer.setScale(0);
        log("displayW = " + displayW + " displayH = " + displayH);
        mMediaPlayer.setAspectRatio("" + displayW + ":" + displayH);
//        mMediaPlayer.
        mSurfaceView.invalidate();
    }


    public void onSurfacesCreated(IVLCVout vlcVout) {
        log("IVlCV onSurfacesCreated");
    }

    public void onSurfacesDestroyed(IVLCVout vlcVout) {
        log("IVlCV onSurfacesDestroyed");
    }

    @Override
    public void onNewVideoLayout(IVLCVout vlcVout, final int width, final int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {

//        final int dw = getWindow().getDecorView().getWidth();
//        final int dh = getWindow().getDecorView().getHeight();

        mSurfaceView.post(new Runnable() {
            @Override
            public void run() {
                final int dw = mSurfaceView.getWidth();
                final int dh = mSurfaceView.getHeight();
                mSurfaceView.getHolder().setFixedSize(width, height);
                ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
                lp.width = dw;
                lp.height = dh;
                mSurfaceView.setLayoutParams(lp);
                mSurfaceView.invalidate();
            }
        });
    }

    public class MySurfaceCallBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            log("vlc surfaceCallback oncreate");
//            start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, final int width, final int height) {
            log("vlc surfaceCallback surfaceChanged");
            fillScreen(mSurfaceView.getWidth(), mSurfaceView.getHeight());
            mMediaPlayer.getVLCVout().setWindowSize(mSurfaceView.getWidth(), mSurfaceView.getHeight());

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            log("vlc surfaceCallback surfaceDestroyed");
            isStartPlay = false;
            release();
        }
    }
}
