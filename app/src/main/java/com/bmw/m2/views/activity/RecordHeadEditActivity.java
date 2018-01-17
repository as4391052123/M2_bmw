package com.bmw.m2.views.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bmw.m2.Constant;
import com.bmw.m2.R;
import com.bmw.m2.VLCApplication;
import com.bmw.m2.model.RecordTaskInfo;
import com.bmw.m2.utils.DbHelper;
import com.bmw.m2.utils.FragmentUtil;
import com.bmw.m2.views.adapter.SpinnerAdapter;
import com.bmw.m2.views.adapter.TaskListAdapter;
import com.bmw.m2.views.fragment.DialogNormalFragment;
import com.bmw.m2.views.fragment.OnDialogFragmentClickListener;
import com.bmw.m2.views.view.MySpinner;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordHeadEditActivity extends BaseActivity {

    @Bind(R.id.record_recylerView)
    RecyclerView mTaskListView;
    @Bind(R.id.record_task_name_edt)
    EditText mNameEdt;
    @Bind(R.id.record_task_start_edt)
    EditText mStartEdt;
    @Bind(R.id.record_task_end_edt)
    EditText mEndEdt;
    @Bind(R.id.record_task_id_tv)
    TextView mIdTv;
    @Bind(R.id.record_task_id_edt)
    EditText mIdEdt;
    @Bind(R.id.record_task_direction_sp)
    MySpinner mDirectionSp;
    @Bind(R.id.record_task_sort_sp)
    MySpinner mSortSp;
    @Bind(R.id.record_task_guancai_sp)
    MySpinner mGuancaiSp;
    @Bind(R.id.record_task_conputer_edt)
    EditText mComputerEdt;
    @Bind(R.id.record_task_people_edt)
    EditText mPeopleEdt;
    @Bind(R.id.record_task_diameter_edt)
    EditText mDiameterEdt;
    @Bind(R.id.record_task_place_edt)
    EditText mPlaceEdt;
    @Bind(R.id.dialog_record_title)
    TextView title;
    @Bind(R.id.record_start_record_btn)
    TextView commitBtn;
    @Bind(R.id.iv_goback)
    ImageView ivGoBack;


    private DbUtils dbUtils;
    private TaskListAdapter adapter;
    private List<RecordTaskInfo> list;
    private Handler handler;
    private int position = -1;
    private int mId;

    private PopupWindow mEditPopupWindow;
    private EditText mEditorCustom;
    private int mPopupDismissWho;
    private LinearLayout.LayoutParams mEditLayoutParams;


    private Intent mIntent;
    private int[] locations;
    private boolean isEditModel;

    private SpinnerAdapter sortAdapter, directionAdapter, guanCaiAdapter, soilAdapter, regionalAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record_head);
        ButterKnife.bind(this);
        title.setText(getString(R.string.record_title));
        ivGoBack.setVisibility(View.GONE);
        isEditModel = getIntent().getBooleanExtra(Constant.RECORD_IS_EDIT_MODEL,false);
        if(isEditModel){
            title.setText(getString(R.string.record_title_edit));
            commitBtn.setVisibility(View.GONE);
            ivGoBack.setVisibility(View.VISIBLE);
        }
        dbUtils = DbHelper.getDbUtils();
        mIntent = new Intent();
        initEditorCustom();

    }


    @Override
    protected void onResume() {
        super.onResume();

        initSpinner();
        initHandler();
        initListView();
        getData(0);
        initData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mIntent = null;
    }


    private void initEditorCustom() {
        locations = new int[2];
        mEditorCustom = new EditText(this);
        mEditorCustom.setTextColor(getResources().getColor(R.color.colorBase));
        mEditorCustom.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.record_dialog_edit));
        mEditLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        mEditorCustom.setSingleLine(true);
        mEditorCustom.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        mEditorCustom.setLayoutParams(mEditLayoutParams);
        mEditorCustom.setGravity(Gravity.TOP);
        mEditorCustom.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mEditorCustom.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mEditPopupWindow.dismiss();
                    mEditorCustom.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null) {
                        imm.hideSoftInputFromWindow(mEditorCustom.getWindowToken(), 0);
                        imm.hideSoftInputFromWindow(mIdEdt.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });
        mEditPopupWindow = new PopupWindow(mEditorCustom, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mEditPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mEditPopupWindow.setFocusable(true);
        mEditPopupWindow.setOutsideTouchable(true);
        mEditPopupWindow.setOutsideTouchable(true);
        mEditPopupWindow.setTouchable(true);
        mEditPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindwo_bg));
        mEditPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mEditPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                String str = mEditorCustom.getText().toString();
                switch (mPopupDismissWho) {
                    case 0:
                        mSortSp.setText(str);
                        break;
                    case 1:
                        mDirectionSp.setText(str);
                        break;
                    case 2:
                        mGuancaiSp.setText(str);
                        break;
                }


            }
        });

    }


    private void initData() {
        if (position < 0)
            return;
        RecordTaskInfo recordTaskInfo = list.get(position);
        if (recordTaskInfo == null)
            return;
        mNameEdt.setText(recordTaskInfo.getTask_name());
        mPeopleEdt.setText(recordTaskInfo.getTask_people());
//        mDirectionEdt.setText(recordTaskInfo.getTask_direction());
//        mSortEdt.setText(recordTaskInfo.getTask_sort());
        mComputerEdt.setText(recordTaskInfo.getTask_computer());
        mPlaceEdt.setText(recordTaskInfo.getTask_place());
        mStartEdt.setText(recordTaskInfo.getTask_start());
//        mGuancaiSp.setText(recordTaskInfo.getTask_guancai());
        mEndEdt.setText(recordTaskInfo.getTask_end());
        mDiameterEdt.setText(recordTaskInfo.getTask_diameter());


//        if (isChangeInfo) {
        mIdEdt.setText(recordTaskInfo.getTask_id());
//            mIdEdt.setEnabled(false);
//            mIdEdt.setTextColor(context.getResources().getColor(R.color.gray_10));
//            mIdTv.setTextColor(context.getResources().getColor(R.color.gray_10));
//        }

        mSortSp.setText(recordTaskInfo.getTask_sort());

        mDirectionSp.setText(recordTaskInfo.getTask_direction());

        mGuancaiSp.setText(recordTaskInfo.getTask_guancai());


    }


    private void initNull() {
        mNameEdt.setText("");
        mPeopleEdt.setText("");
        mComputerEdt.setText("");
        mPlaceEdt.setText("");
        mStartEdt.setText("");
        mEndEdt.setText("");
        mDiameterEdt.setText("");
        mIdEdt.setText("");
        mSortSp.setText("");
        mDirectionSp.setText("");
        mGuancaiSp.setText("");
    }


    private void getData(final int i) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list = dbUtils.findAll(RecordTaskInfo.class);
                    if (list != null)
                        Collections.sort(list);
                    handler.sendEmptyMessage(i);
                    position = -1;

                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void initListView() {
        mTaskListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskListAdapter(this);
        mTaskListView.setAdapter(adapter);
        adapter.setOnDataChooseListener(new TaskListAdapter.OnDataChooseListener() {
            @Override
            public void setData(int id) {
                position = id;
                handler.sendEmptyMessage(3);
            }
        });

    }


    @OnClick({R.id.record_start_page_btn,
            R.id.record_last_page_btn,
            R.id.record_next_page_btn,
            R.id.record_end_page_btn,
            R.id.record_add_page_btn,
            R.id.record_change_page_btn,
            R.id.record_delete_page_btn,
            R.id.record_start_record_btn,
            R.id.record_delete_all_page_btn,
            R.id.iv_goback
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.record_start_page_btn:
                adapter.chooseFirstItem();
                break;
            case R.id.record_last_page_btn:
                adapter.chooseLastItem();
                break;
            case R.id.record_next_page_btn:
                adapter.chooseNextItem();
                break;
            case R.id.record_end_page_btn:
                adapter.chooseEndItem();
                break;
            case R.id.record_add_page_btn:
                saveData(false);
                break;
            case R.id.record_change_page_btn:
                if (position != -1) {
                    mId = list.get(position).getId();
                    saveData(true);
                } else
                    handler.sendEmptyMessage(4);
                break;
            case R.id.record_delete_page_btn:
                if (position != -1)
                    try {
                        dbUtils.delete(list.get(position));
                        getData(1);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                else
                    handler.sendEmptyMessage(4);
                break;
            case R.id.record_start_record_btn:
                startRecord();
                break;
            case R.id.iv_goback:
                startRecord();
                break;
            case R.id.record_delete_all_page_btn:
                try {
                    dbUtils.deleteAll(RecordTaskInfo.class);
                    getData(0);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void startRecord(){

        if (position < 0) {
            showNormalFragment("数据未保存！", "是否添加至列表选项？", new Runnable() {
                @Override
                public void run() {
                    saveData(false);
                    setCommit();
                }
            }, new Runnable() {
                @Override
                public void run() {
                    setCommit();
                }
            });

        } else {
            if (isDataChange()) {
                showNormalFragment("数据已更改！", "是否在列表选项中更新？", new Runnable() {
                    @Override
                    public void run() {
                        saveData(true);
                        setCommit();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        setCommit();
                    }
                });
            } else {
                setCommit();
            }
        }
    }

    private boolean isDataChange() {
        if (position < 0)
            return false;
        RecordTaskInfo recordTaskInfo = list.get(position);
        if (recordTaskInfo == null)
            return false;
        boolean isNotChange = mIdEdt.getText().toString().equals(recordTaskInfo.getTask_id()) &&
                mNameEdt.getText().toString().equals(recordTaskInfo.getTask_name()) &&
                mPlaceEdt.getText().toString().equals(recordTaskInfo.getTask_place()) &&
                mStartEdt.getText().toString().equals(recordTaskInfo.getTask_start()) &&
                mEndEdt.getText().toString().equals(recordTaskInfo.getTask_end()) &&
                mDirectionSp.getText().toString().equals(recordTaskInfo.getTask_direction()) &&
                mSortSp.getText().toString().equals(recordTaskInfo.getTask_sort()) &&
                mGuancaiSp.getText().toString().equals(recordTaskInfo.getTask_guancai()) &&
                mDiameterEdt.getText().toString().equals(recordTaskInfo.getTask_diameter()) &&
                mComputerEdt.getText().toString().equals(recordTaskInfo.getTask_computer()) &&
                mPeopleEdt.getText().toString().equals(recordTaskInfo.getTask_people()) ;
        if (!isNotChange)
            return true;


        return false;
    }

    private void showNormalFragment(String title, String msg, final Runnable sureRunnable, final Runnable cancelRunnable) {
        DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance(title, msg, "是", "否", false);
        dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
            @Override
            public void sure() {
                sureRunnable.run();
            }

            @Override
            public void cancel() {
                cancelRunnable.run();
            }
        });
        FragmentUtil.showDialogFragment(getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");
    }

    private void setCommit() {
//        if(position != -1){
        RecordTaskInfo recordTaskInfo = new RecordTaskInfo(mId, mIdEdt.getText().toString(), mNameEdt.getText().toString(),
                mPlaceEdt.getText().toString(), mStartEdt.getText().toString(),
                mEndEdt.getText().toString(), mDirectionSp.getText().toString(),
                mSortSp.getText().toString(), mGuancaiSp.getText().toString(),
                mDiameterEdt.getText().toString(), mComputerEdt.getText().toString(),
                mPeopleEdt.getText().toString());
        mIntent.putExtra("record_head", recordTaskInfo);
        setResult(1, mIntent);
        finish();
//        }
    }

    //设置返回控制
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK
            if (position != -1) {
                setCommit();
            } else
                setResult(1);
            finish();


            return true;
        }
        if (keyCode == event.KEYCODE_HOME) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        adapter.setList(list);
                        break;
                    case 1:
                        if (list == null)
                            break;
                        adapter.setList(list);
                        adapter.setChooseByPosition(list.size() - 1);
                        break;
                    case 2:
                        adapter.setList(list);
                        adapter.setChooseByTaskId(mId);
                        break;

                    case 3:
                        if (position != -1) {
                            mId = list.get(position).getId();
                            mTaskListView.smoothScrollToPosition(position);
                            initData();
                        } else {
                            initNull();
                        }
                        break;
                    case 4:
                        VLCApplication.toast("请先选中一个列表选项!");
                        break;

                }

            }
        };
    }


    private void saveData(final boolean isChangeInfo) {

        new Thread(new Runnable() {
            @Override
            public void run() {
               /* if (TextUtils.isEmpty(mIdEdt.getText().toString())) {
                    handler.sendEmptyMessage(1);
                    return;
                }*/
                RecordTaskInfo recordTaskInfo = new RecordTaskInfo(mId, mIdEdt.getText().toString(), mNameEdt.getText().toString(),
                        mPlaceEdt.getText().toString(), mStartEdt.getText().toString(),
                        mEndEdt.getText().toString(), mDirectionSp.getText().toString(),
                        mSortSp.getText().toString(), mGuancaiSp.getText().toString(),
                        mDiameterEdt.getText().toString(), mComputerEdt.getText().toString(),
                        mPeopleEdt.getText().toString());

                try {
                    if (!isChangeInfo) {

                        dbUtils.save(recordTaskInfo);
                        getData(1);
                    } else {
                        recordTaskInfo.setId(mId);
                        dbUtils.update(recordTaskInfo);
                        getData(2);
                    }
//                    if (listener != null)
//                        listener.finish(recordTaskInfo.getId());
//                    dismiss();
                } catch (DbException e) {
                    error("数据库保存失败：" + e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initAdapter() {
        sortAdapter = new SpinnerAdapter(this);
        directionAdapter = new SpinnerAdapter(this);
        guanCaiAdapter = new SpinnerAdapter(this);
        soilAdapter = new SpinnerAdapter(this);
        regionalAdapter = new SpinnerAdapter(this);

        sortAdapter.setStrings(getResources().getStringArray(R.array.record_task_sort));
        directionAdapter.setStrings(getResources().getStringArray(R.array.record_task_direction));
        guanCaiAdapter.setStrings(getResources().getStringArray(R.array.record_task_guancai));
        soilAdapter.setStrings(getResources().getStringArray(R.array.record_task_soil_effect));
        regionalAdapter.setStrings(getResources().getStringArray(R.array.record_task_regional_important));
    }

    private void initSpinner() {
        initAdapter();

        mSortSp.setAdapter(sortAdapter);
        mDirectionSp.setAdapter(directionAdapter);
        mGuancaiSp.setAdapter(guanCaiAdapter);


        mSortSp.setText(sortAdapter.getString(1));
        mDirectionSp.setText(directionAdapter.getString(1));
        mGuancaiSp.setText(guanCaiAdapter.getString(1));

        mSortSp.setOnItemSelectedListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String sortStr = sortAdapter.getString(position);
                    mSortSp.setText(sortStr);
                    mSortSp.dismissPop();
                } else {
                    mSortSp.dismissPop();
                    mSortSp.getLocationOnScreen(locations);
                    mEditorCustom.setText(mSortSp.getText().toString());
                    mEditLayoutParams.width = mSortSp.getWidth();
                    mEditorCustom.setLayoutParams(mEditLayoutParams);
                    mEditorCustom.setMinimumWidth(mSortSp.getWidth());
                    mEditPopupWindow.showAtLocation(mSortSp,Gravity.NO_GRAVITY,locations[0],locations[1]);
                    mPopupDismissWho = 0;
                }
            }
        });
        mDirectionSp.setOnItemSelectedListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String directStr = directionAdapter.getString(position);
                    mDirectionSp.setText(directStr);
                    mDirectionSp.dismissPop();
                } else {
                    mDirectionSp.dismissPop();
                    mDirectionSp.getLocationOnScreen(locations);
                    mEditorCustom.setText(mDirectionSp.getText().toString());
                    mEditLayoutParams.width = mDirectionSp.getWidth();
                    mEditorCustom.setLayoutParams(mEditLayoutParams);
                    mEditorCustom.setMinimumWidth(mSortSp.getWidth());
                    mEditPopupWindow.showAtLocation(mDirectionSp,Gravity.NO_GRAVITY,locations[0],locations[1]);
                    mPopupDismissWho = 1;
                }
            }
        });
        mGuancaiSp.setOnItemSelectedListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String guancarStr = guanCaiAdapter.getString(position);
                    mGuancaiSp.setText(guancarStr);
                    mGuancaiSp.dismissPop();
                } else {
                    mGuancaiSp.dismissPop();
                    mGuancaiSp.getLocationOnScreen(locations);
                    mEditorCustom.setText(mGuancaiSp.getText().toString());
                    mEditLayoutParams.width = mGuancaiSp.getWidth();
                    mEditorCustom.setLayoutParams(mEditLayoutParams);
                    mEditorCustom.setMinimumWidth(mGuancaiSp.getWidth());
                    mEditPopupWindow.showAtLocation(mGuancaiSp,Gravity.NO_GRAVITY,locations[0],locations[1]);
                    mPopupDismissWho = 2;
                }
            }
        });



    }




    private List<String> getDirectiongList() {
        List<String> list = new ArrayList<>();
        String[] strings = getResources().getStringArray(R.array.record_task_direction);
        for (String s : strings) {
            list.add(s);
        }
        return list;
    }

}


