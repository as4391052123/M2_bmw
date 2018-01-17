package com.bmw.m2.presenter.impl;

import android.content.Context;

import com.bmw.m2.R;
import com.bmw.m2.model.FileInfo;
import com.bmw.m2.presenter.FilePresenter;
import com.bmw.m2.presenter.VideoPlayerPresenter;
import com.bmw.m2.utils.FileComparator;
import com.bmw.m2.utils.FileUtil;
import com.bmw.m2.views.activity.FileShowNewActivity;
import com.bmw.m2.views.adapter.FileListAdapter;
import com.bmw.m2.views.fragment.DialogNormalFragment;
import com.bmw.m2.views.fragment.OnDialogFragmentClickListener;
import com.bmw.m2.views.viewImpl.FileViewImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bmw.m2.utils.FragmentUtil.showDialogFragment;
import static com.bmw.m2.utils.ThrowUtil.log;


/**
 * Created by admin on 2016/9/8.
 */
public class FilePresenterImpl implements FilePresenter {

    private FileListAdapter adapter;
    private boolean isPicture;
    private FileViewImpl fileViewImpl;
    private boolean isChooseDeleteMode;
    private String mPath;
    private Context context;
    private VideoPlayerPresenter videoPlayerPresenter;

    public FilePresenterImpl(FileListAdapter adapter, boolean isPicture, FileViewImpl view, Context context, VideoPlayerPresenter videoPlayerPresenter) {
        this.isPicture = isPicture;
        this.adapter = adapter;
        this.fileViewImpl = view;
        this.context = context;
        this.videoPlayerPresenter = videoPlayerPresenter;
//        initAdapter();
    }

    @Override
    public void initAdapter() {
        adapter.setFiles(initData());
        adapter.setOnClickChangeListener(new FileListAdapter.OnClickChangeListener() {
            @Override
            public void Click(String path) {

                if (isChooseDeleteMode)
                    isChooseDeleteMode = false;
                if (path == null) {
                    mPath = null;
                    setVideoPresentPath();
                    return;
                }
                if (path.equals(mPath)) {
                    fileViewImpl.isBtnDeleteShow(true);
                    return;
                }


                mPath = path;
                setVideoPresentPath();
                fileViewImpl.isBtnDeleteShow(true);
                if (isPicture) {
                    fileViewImpl.pictureClick(path);
                } else {
                    videoPlayerPresenter.startPlayVideo();
                }


            }

            @Override
            public void longClick(int position) {
                fileViewImpl.isBtnDeleteShow(true);
                if (videoPlayerPresenter != null)
                    videoPlayerPresenter.playStop();
                isChooseDeleteMode = true;
                adapter.setChoose(true);
                adapter.addDeleteChooseList(position);
            }

            @Override
            public void cancelLongClick() {
                isChooseDeleteMode = false;
                fileViewImpl.isBtnDeleteShow(false);
            }
        });
    }


    public void setVideoPresentPath() {
        if (videoPlayerPresenter != null)
            videoPlayerPresenter.setPlayPath(mPath);
    }

    private List<File> initData() { //从指定文件夹获取文件列表

        List<File> files = null;
        if (isPicture) {
            files = FileUtil.getAllFiles(FileUtil.getFileSavePath() + FileInfo.picturePath);
            List<File> picFiles = new ArrayList<>();
            if (files != null) {
                for (int i = 0; i < files.size(); i++) {
                    String name = files.get(i).getName();
                    name = name.substring(name.lastIndexOf("."), name.length());
                    if (name.equals(".jpg")) {
                        picFiles.add(files.get(i));
                    } else {
                        boolean isDelete = true;
                        for (File f : files) {
                            String fName = files.get(i).getName();
                            fName = fName.substring(fName.lastIndexOf("/") + 1, fName.lastIndexOf("."));
                            String tName = f.getName();
                            String tNameF = tName.substring(tName.lastIndexOf("/") + 1, tName.lastIndexOf("."));
                            String tNameE = tName.substring(tName.lastIndexOf("."), tName.length());
                            if (tNameF.equals(fName) && tNameE.equals(".jpg")) {
                                isDelete = false;
                            }
                        }
                        if (isDelete) {
                            files.get(i).delete();
                            files.remove(i);
                        }
                    }
                }
                files = picFiles;
            }
        } else {
            files = FileUtil.getAllFiles(FileUtil.getFileSavePath() + FileInfo.videoPath);
        }
        if (files != null) {
            FileComparator fileComparator = new FileComparator();
            Collections.sort(files, fileComparator);
        }

        return files;

    }

    @Override
    public void searching(String msg) {
        if (msg.equals("")) {
//            fileViewImpl.mToast("请先输入搜索内容！");
            adapter.setFiles(initData());
            return;
        }
        List<File> searFiles = new ArrayList<>();
        if (initData() != null)
            for (File f : initData()) {
                String name = f.getName();
                if (name.contains(msg)) {
                    searFiles.add(f);
                }
            }
//        if (searFiles.size() != 0) {
        adapter.setFiles(searFiles);
//        if (fileViewImpl != null)
//            fileViewImpl.showToast("搜索到" + searFiles.size() + "个文件");
//        } else {
//            adapter.setFiles(searFiles);
//        }
//            fileViewImpl.mToast("未搜索到相关文件！");
    }

    @Override
    public void deleteFile() {
        log("isChooseDeleteMode = " + isChooseDeleteMode);
        if (isChooseDeleteMode) {
            deleteAllChooseFile();
        } else
            deleteClickChooseFile();
    }

    @Override
    public void lastItem() {
        adapter.lastItem();
    }

    @Override
    public void nextItem() {
        adapter.nextItem();
    }


    private void deleteAllChooseFile() {
        DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance(
                context.getResources().getString(R.string.isDeleteAllFile),
                context.getResources().getString(R.string.isdeleteChooseFile),
                context.getResources().getString(R.string.yes),
                context.getResources().getString(R.string.no), true);
        dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
            @Override
            public void sure() {
                adapter.deleteFile();
                adapter.setChoose(false);
                mPath = null;
                setVideoPresentPath();
                initAdapter();
                isChooseDeleteMode = false;
                fileViewImpl.setEmptyPicture();
            }

            @Override
            public void cancel() {

            }
        });
        showDialogFragment(((FileShowNewActivity) context).getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");

    }


    private void deleteClickChooseFile() {
        if (mPath != null) {
            String pathName = mPath.substring(mPath.lastIndexOf("/") + 1, mPath.length());
            if (videoPlayerPresenter != null && videoPlayerPresenter.isPlaying())
                videoPlayerPresenter.playStart();
            DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance(context.getResources().getString(R.string.isdelete),
                    context.getResources().getString(R.string.isdeleteFile) + pathName,
                    context.getResources().getString(R.string.yes),
                    context.getResources().getString(R.string.no), true);
            dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
                @Override
                public void sure() {
                    if (videoPlayerPresenter != null)
                        videoPlayerPresenter.playStop();
                    adapter.deleteOneChoose();
                    deleteXmlFile();
//                            filePresenter.initAdapter();
                }

                @Override
                public void cancel() {
                    if (videoPlayerPresenter != null && videoPlayerPresenter.isStartPlay() && !videoPlayerPresenter.isPlaying())
                        videoPlayerPresenter.playStart();
                }
            });
            showDialogFragment(((FileShowNewActivity) context).getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");

            pathName = null;
        }
    }

    private void deleteXmlFile() {
        if (isPicture && mPath != null) {
            String pathName = mPath.substring(0, mPath.lastIndexOf("."));
            File xmlFile = new File(pathName + ".xml");
            if (xmlFile.exists())
                xmlFile.delete();
        }
    }

    @Override
    public void chooseAll() {
        adapter.chooseAll();
    }

    @Override
    public void cancelAll() {
        adapter.cancelAll();
        adapter.setChoose(false);
    }


}
