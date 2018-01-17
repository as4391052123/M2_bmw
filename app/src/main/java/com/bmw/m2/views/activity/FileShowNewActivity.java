package com.bmw.m2.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bmw.m2.Constant;
import com.bmw.m2.R;
import com.bmw.m2.VLCApplication;
import com.bmw.m2.presenter.VideoPlayerPresenter;
import com.bmw.m2.presenter.impl.FilePresenterImpl;
import com.bmw.m2.presenter.impl.VideoPlayerPresentImpl;
import com.bmw.m2.utils.FileUtil;
import com.bmw.m2.utils.ImageLoader;
import com.bmw.m2.utils.MyViewUtil;
import com.bmw.m2.utils.PullXmlParseUtil;
import com.bmw.m2.views.adapter.FileListAdapter;
import com.bmw.m2.views.dialog.Capture_tishi_Dialog;
import com.bmw.m2.views.fragment.DialogNormalFragment;
import com.bmw.m2.views.fragment.OnDialogFragmentClickListener;
import com.bmw.m2.views.view.CompositeImageText;
import com.bmw.m2.views.viewImpl.FileViewImpl;

import java.io.File;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;

import static com.bmw.m2.utils.FragmentUtil.showDialogFragment;


public class FileShowNewActivity extends BaseActivity implements FileViewImpl {

    @Bind(R.id.photoView_pic)
    PhotoView photoViewPic;
    @Bind(R.id.recyclerView_pic)
    RecyclerView recyclerViewPic;
    @Bind(R.id.pic_show_guandaoId)
    TextView mGuandaoId;
    @Bind(R.id.pic_show_style)
    TextView mStyle;
    @Bind(R.id.pic_show_name)
    TextView mName;
    @Bind(R.id.pic_show_grade)
    TextView mGrade;
    @Bind(R.id.pic_show_distance)
    TextView mDistance;
    @Bind(R.id.pic_show_clock)
    TextView mClock;
    @Bind(R.id.pic_show_length)
    TextView mLength;
    @Bind(R.id.pic_show_detail)
    TextView mDetail;
    @Bind(R.id.pic_show_bottom_container)
    LinearLayout picShowBottomContainer;
    @Bind(R.id.search_edit)
    EditText searchEdit;
    TextView batteryNum_system;
    @Bind(R.id.container_picturePlayer)
    RelativeLayout containerPicturePlayer;
    @Bind(R.id.container_video_player)
    RelativeLayout containerVideoPlayer;
    @Bind(R.id.player_surface)
    VideoView playerSurface;
    @Bind(R.id.player_seekbar)
    SeekBar playerSeekbar;
    @Bind(R.id.tv_video_allTime)
    TextView tvVideoAllTime;
    @Bind(R.id.tv_video_currentTime)
    TextView tvVideoCurrentTime;
    @Bind(R.id.tv_video_playSpeed)
    TextView tvVideoPlaySpeed;
    @Bind(R.id.compositeImgTv_pic_delete)
    CompositeImageText compositeImgTvPicDelete;
    @Bind(R.id.play_start)
    ImageView playStart;
    @Bind(R.id.tv_fileActivty_diskSize)
    TextView tvDiskSize;
    @Bind(R.id.tv_file_show_title)
    TextView tv_title;


    @Bind(R.id.tv_battery_device)
    TextView tv_battery_device;
    @Bind(R.id.tv_battery_terminal)
    TextView tv_battery_terminal;

    private FilePresenterImpl filePresenter;
    //    private Bitmap mBitmap;
    private boolean isStop;
    private boolean mSeekbarStartTouch;
    private VideoPlayerPresenter videoPlayerPresenter;
    private ImageLoader imageLoader;
    private int speedfast = 1;
    private int speedSlow = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_show_new);
        ButterKnife.bind(this);
        imageLoader = new ImageLoader();

        boolean isPicture = getIntent().getBooleanExtra(Constant.IS_PICTURE_ACTIVITY, true); //判断图片还是视频文件
        initView(isPicture);

        initBroadcastReceiver();
        FileListAdapter adapter = initRecyclerView(isPicture);

        initSearch();

        if (!isPicture) {
//            SurfaceHolder surfaceHolder = playerSurface.getHolder();
//            surfaceHolder.addCallback(callback);
//            surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
//            playerSurface.setZOrderOnTop(true);
//            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            videoPlayerPresenter = new VideoPlayerPresentImpl(this, null, playerSurface,playerSeekbar);
            setSeekbarMove();


        }

        filePresenter = new FilePresenterImpl(adapter, isPicture, this, this, videoPlayerPresenter); //初始化presenter
        initAdapter();

    }

    private void initView(boolean isPicture) {
        if (isPicture) {
            containerPicturePlayer.setVisibility(View.VISIBLE);
            containerVideoPlayer.setVisibility(View.GONE);
        } else {
            containerPicturePlayer.setVisibility(View.GONE);
            containerVideoPlayer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isStop = true;
        ButterKnife.unbind(this);
        unregisterReceiver(system_battery_receiver);
//        recyleBitmap();
        imageLoader.clearBitmapCache();
        if (videoPlayerPresenter != null)
            videoPlayerPresenter.release();
    }


    //设置返回控制
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK

            removeActivity();
            return true;
        }
        if (keyCode == event.KEYCODE_HOME) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.play_start,
            R.id.play_stop,
            R.id.play_last,
            R.id.play_next,
            R.id.play_slow,
            R.id.play_fast,
            R.id.compositeImgTv_pic_goBack,
            R.id.compositeImgTv_pic_delete,
            R.id.cit_close_app,
            R.id.play_cut
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.compositeImgTv_pic_goBack:
                removeActivity();
                break;
            case R.id.compositeImgTv_pic_delete:
                filePresenter.deleteFile();
                break;
            case R.id.cit_close_app:
                btnClickCloseApp();
                break;
            case R.id.play_start:
                videoPlayerPresenter.playStart();
                break;
            case R.id.play_stop:
                videoPlayerPresenter.playStop();
                speedfast = 1;
                speedSlow = 1;
                break;
            case R.id.play_last:
                filePresenter.lastItem();
                break;
            case R.id.play_next:
                filePresenter.nextItem();
                break;
            case R.id.play_slow:
                if (speedfast != 1) {
                    playerSurface.pause();
                    speedfast = speedfast / 2;
                    setSpeedTv(speedfast);
                    if (speedfast == 1){
                        playerSurface.start();
                    }
                } else {
                    playerSurface.start();
                    if (speedSlow < 16) {
                        speedSlow = speedSlow * 2;
                    } else {
                        speedSlow = 16;
                    }
                    setSpeedTv(-speedSlow);
                }
//                videoPlayerPresenter.playSlow();
                break;
            case R.id.play_fast:
                if (speedSlow != 1) {
                    speedSlow = speedSlow / 2;
                    setSpeedTv(-speedSlow);
                    if (speedSlow == 1)
                        playerSurface.start();
                } else {
                    playerSurface.pause();
                    if (speedfast < 16) {
                        speedfast = speedfast * 2;
                    } else {
                        speedfast = 16;
                    }
                    setSpeedTv(speedfast);
                }
//                videoPlayerPresenter.playFast();
                break;
            case R.id.play_cut:
                videoPlayerPresenter.playCutPicture();
                break;
        }
    }

    private void btnClickCloseApp() {

        DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance(getString(R.string.exitingApp),
                getString(R.string.exitAppSure), null, null, true);
        dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
            @Override
            public void sure() {
                removeALLActivity();
            }

            @Override
            public void cancel() {

            }
        });
        showDialogFragment(getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");
    }


    private void initAdapter() {

        filePresenter.initAdapter();
        initDiskSize();

    }

    private void initDiskSize() {
        StringBuilder stringBuilder = new StringBuilder();
        List<Float> diskSizeList = FileUtil.getDiskCapacity();
        boolean isSaveToSdcard = VLCApplication.getSharedPreferences().getBoolean(Constant.FILE_IS_SAVE_TO_SDCARD, false);
        stringBuilder.append(isSaveToSdcard ?
                getResources().getString(R.string.outStorage) : getResources().getString(R.string.inStorage)).
                append("\n").append(diskSizeList.get(2)).append("G").append("/").append(diskSizeList.get(0)).append("G");
        tvDiskSize.setText(stringBuilder.toString());
    }

    private void setEmptyBitmap() {
//        recyleBitmap();
        Bitmap mBitmap = imageLoader.getBitmap("empty");
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(1280, 720, Bitmap.Config.ARGB_8888);
            imageLoader.putBitmap(mBitmap, "empty");
        }
        photoViewPic.setImageBitmap(mBitmap);
    }


    public void capturePicture(final String path) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Capture_tishi_Dialog dialog = new Capture_tishi_Dialog(FileShowNewActivity.this, getResources().getString(R.string.isEditPicture), path);

                dialog.setSureOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(FileShowNewActivity.this, PictureEditActivity.class);
                        intent.putExtra("path", path);
                        startActivityForResult(intent, 0);
                        dialog.dismiss();
                    }
                });

                dialog.setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });


    }


    public void setSpeedTv(int playSpeed) {
        switch (playSpeed) {
            case -16:
                tvVideoPlaySpeed.setText("1/16X");
                break;
            case -8:
                tvVideoPlaySpeed.setText("1/8X");
                break;
            case -4:
                tvVideoPlaySpeed.setText("1/4X");
                break;
            case -2:
                tvVideoPlaySpeed.setText("1/2X");
                break;
            case 1:
                tvVideoPlaySpeed.setText("1X");
                break;
            case 2:
                tvVideoPlaySpeed.setText("2X");
                break;
            case 4:
                tvVideoPlaySpeed.setText("4X");
                break;
            case 8:
                tvVideoPlaySpeed.setText("8X");
                break;
            case 16:
                tvVideoPlaySpeed.setText("16X");
                break;

        }
    }


    public void isBtnDeleteShow(boolean isShow) {
        if (compositeImgTvPicDelete == null)
            return;
        compositeImgTvPicDelete.setVisibility(isShow ? View.VISIBLE : View.GONE);

    }

    @Override
    public void setEmptyPicture() {
        if (compositeImgTvPicDelete != null)
            compositeImgTvPicDelete.post(new Runnable() {
                @Override
                public void run() {
                    compositeImgTvPicDelete.setVisibility(View.GONE);
                    setEmptyBitmap();
                    getDataFromXml(null);
                }
            });
    }
/*
    @Override
    public void showDialogFragment(DialogFragment dialogFragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment != null)
            fragmentTransaction.remove(fragment);

        dialogFragment.show(fragmentTransaction,tag);
    }*/


    private void initSearch() {
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String msg = searchEdit.getText().toString();
                filePresenter.searching(msg);
            }
        });
    }

    private FileListAdapter initRecyclerView(boolean isPicture) {
        GridLayoutManager gManager = new GridLayoutManager(this, 3);
        recyclerViewPic.setLayoutManager(gManager);
        FileListAdapter adapter = new FileListAdapter(this, isPicture);
        recyclerViewPic.setAdapter(adapter);
        return adapter;

    }


    public void pictureClick(final String path) {
//        recyleBitmap();
        if (path != null) {
           /* Observable.create(new Observable.OnSubscribe<Bitmap>() {
                @Override
                public void call(Subscriber<? super Bitmap> subscriber) {
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    subscriber.onNext(bitmap);
                }
            }).subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<Bitmap>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onNext(Bitmap bitmap) {
                    photoViewPic.setImageBitmap(bitmap);
                    mBitmap = bitmap;
                }
            });*/

            Bitmap mBitmap = imageLoader.getBitmap(path);
            if (mBitmap == null) {
                mBitmap = BitmapFactory.decodeFile(path);
                imageLoader.putBitmap(mBitmap, path);
            }
            photoViewPic.setImageBitmap(mBitmap);
            getDataFromXml(path);
        } else {
            photoViewPic.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.background)));
        }
    }
/*
    private void recyleBitmap() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }*/

    private void getDataFromXml(String xmlPath) {
        mClock.setText("");
        mDetail.setText("");
        mDistance.setText("");
        mGrade.setText("");
        mGuandaoId.setText("");
        mLength.setText("");
        mName.setText("");
        mStyle.setText("");
        if (xmlPath != null) {
            xmlPath = xmlPath.substring(0, xmlPath.lastIndexOf(".")) + ".xml";

            File xmlFile = new File(xmlPath);
            if (xmlFile.exists()) {
                Map<String, String> map = PullXmlParseUtil.parsePicXml(xmlFile);
                mClock.setText(map.get(PullXmlParseUtil.ClockExpression));
                mDetail.setText(map.get(PullXmlParseUtil.DefectDescription));
                mDistance.setText(map.get(PullXmlParseUtil.Distance));
                mGrade.setText(map.get(PullXmlParseUtil.DefectLevel));
                mGuandaoId.setText(map.get(PullXmlParseUtil.PipeSection));
                mLength.setText(map.get(PullXmlParseUtil.DefectLength));
                mName.setText(map.get(PullXmlParseUtil.DefectCode));
                mStyle.setText(map.get(PullXmlParseUtil.DefectType));
            }
        }
    }


    @Override
    public void isPlaying(boolean isPlaying) {
        if (playStart == null)
            return;
        if (!isPlaying)
            playStart.setImageResource(R.mipmap.play_start);
        else
            playStart.setImageResource(R.mipmap.play_pause);
    }

    @Override
    public void setSeekbarProgress(int progress) {
        playerSeekbar.setProgress(progress);
    }

    public void showPlayFailed() {

        new AlertDialog.Builder(FileShowNewActivity.this).setTitle(getResources().getString(R.string.playVideoFalse)).
                setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();

    }

    @Override
    public void showAllPlayTime(String time) {
        tvVideoAllTime.setText(time);
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setSeekbarMove() {
        playerSeekbar.setOnSeekBarChangeListener(change);
        playerSeekbar.setMax(1000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop) {


                    if (!mSeekbarStartTouch) {
//                        final int currentPlace = videoPlayerPresenter.getCurrentPlayPlace();
                        final int currentPlace =playerSurface.getCurrentPosition();

                        if (speedfast != 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    sleep(500);
                                    playerSurface.pause();
                                    int currentP =playerSurface.getCurrentPosition();
                                    setPlayPlace(currentP+speedfast/2*600);
                                    playerSeekbar.setProgress(currentP+speedfast/2*1100);

                                }
                            });
                        } else if (speedSlow != 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    playerSurface.pause();
                                }
                            });
                            sleep(speedSlow * 15);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    playerSurface.start();
                                    playerSeekbar.setProgress(currentPlace);
                                }
                            });
                        }

                        if (speedfast == 1 && speedSlow == 1)
                            playerSeekbar.setProgress(currentPlace);
//                        if (currentPlace >= 991)
//                            playerSeekbar.setProgress(1000);
                        tvVideoCurrentTime.post(new Runnable() {
                            @Override
                            public void run() {
                                if (tvVideoCurrentTime != null)
                                    tvVideoCurrentTime.setText(videoPlayerPresenter.getPlayedTimeEx());
                            }
                        });
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void resetPlayView() {
        if (tvVideoAllTime == null)
            return;
        speedfast = 1;
        speedSlow = 1;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                tvVideoAllTime.setText("00:00");
                tvVideoCurrentTime.setText("00:00");
//                playerSeekbar.setProgress(0);
                tvVideoPlaySpeed.setText("1X");
                playStart.setImageResource(R.mipmap.play_start);
            }
        });
    }

/*
    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        // SurfaceHolder被修改的时候回调
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            videoPlayerPresenter.surfaceDestroy();
            resetPlayView();


        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            log("视频播放：SurfaceHolder 被创建");

            videoPlayerPresenter.startPlayVideo();

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            log("视频播放：SurfaceHolder 大小被改变");

        }

    };*/


    private SeekBar.OnSeekBarChangeListener change = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 当进度条停止修改的时候触发
            // 取得当前进度条的刻度
            int progress = seekBar.getProgress();

            setPlayPlace(progress);
            log("stopTouch seekbar!");
//            Player.getInstance().pause(iPort,0);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
//            Player.getInstance().pause(iPort,1);

            mSeekbarStartTouch = true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }
    };

    private void setPlayPlace(int progress) {
        videoPlayerPresenter.setPlayPlace(progress);
        getCurrentTime();

        mSeekbarStartTouch = false;
    }

    private void getCurrentTime() {
        tvVideoCurrentTime.setText(videoPlayerPresenter.getPlayedTimeEx());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isPlay) {
                    try {
                        Thread.sleep(10);
                        if (isStop)
                            break;
                        if (isPlay) {
                            Thread.sleep(50);
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (tvVideoCurrentTime != null) {
                    tvVideoCurrentTime.post(new Runnable() {
                        @Override
                        public void run() {
                            setPlayPlace(mPlayPosition);
                            mPlayPosition = 0;
                        }
                    });
                }
            }
        }).start();*/


    }


    private BroadcastReceiver system_battery_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyViewUtil.setSystemBattery(context(), intent, tv_battery_terminal);
            MyViewUtil.setDeviceBattery(intent, tv_battery_device);
        }
    };

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(Constant.BROADCAST_GET_INFO_FROM_FRAME_DEVICE);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(system_battery_receiver, intentFilter);
    }
}
