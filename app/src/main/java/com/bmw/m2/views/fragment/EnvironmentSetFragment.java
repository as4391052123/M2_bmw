package com.bmw.m2.views.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bmw.m2.Constant;
import com.bmw.m2.R;
import com.bmw.m2.VLCApplication;
import com.bmw.m2.model.CarRemoteDeviecInfo;
import com.bmw.m2.model.Environment;
import com.bmw.m2.model.RemoteDeviceInfo;
import com.bmw.m2.utils.ListUtil;
import com.bmw.m2.views.adapter.EnvironmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/7.
 */

public class EnvironmentSetFragment extends Fragment {
    @Bind(R.id.environment_recyclerview)
    RecyclerView eRecyclerview;
    private View root;
    private EnvironmentAdapter adapter;
    private float environmentNum;
    private List<Environment> list;


    private BroadcastReceiver environmentDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CarRemoteDeviecInfo carRemoteDeviecInfo = (CarRemoteDeviecInfo) intent.getSerializableExtra(Constant.KEY_RESULT_INFO_XIANLANCHE);

            RemoteDeviceInfo remoteDeviceInfo = (RemoteDeviceInfo) intent.getSerializableExtra(Constant.KEY_RESULT_INFO_PAXINGQI);
            if (list == null) {
                list = new ArrayList<>();
                String[] strings = getResources().getStringArray(R.array.environment_name);
                String[] unitStrs = getResources().getStringArray(R.array.environment_unit);
                for (int i=0;i<strings.length;i++) {
                    Environment environment = new Environment();
                    environment.setName(strings[i]);
                    environment.setCurrent_num(0);
                    environment.setMax_num(VLCApplication.getSharedPreferences().getFloat(strings[i]+"_max",0));
                    environment.setMin_num(VLCApplication.getSharedPreferences().getFloat(strings[i]+"_min",0));
                    environment.setUnit(unitStrs[i]);
                    list.add(environment);
                }
            }

            list = ListUtil.getEnvironmentList(list, carRemoteDeviecInfo, remoteDeviceInfo);
            if (adapter != null) {
                if(System.currentTimeMillis() - updateTime>=1000) {
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                    updateTime = System.currentTimeMillis();
                }

            }


        }
    };

    private long updateTime;

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(Constant.BROADCAST_GET_INFO_FROM_FRAME_DEVICE);
        getActivity().registerReceiver(environmentDataReceiver, intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_environment, container, false);
        ButterKnife.bind(this, root);
        initBroadcastReceiver();

        initRecyclerView();
        return root;
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eRecyclerview.setLayoutManager(layoutManager);
        adapter = new EnvironmentAdapter(getActivity());
        eRecyclerview.setAdapter(adapter);
        adapter.setAdapterDateChangeListener(new EnvironmentAdapter.AdapterDateChangeListener() {
            @Override
            public void resetDate() {
//                setEnvironment(environmentNum);
                for (int i=0;i<list.size();i++) {
                    Environment environment = list.get(i);
                    environment.setMax_num(VLCApplication.getSharedPreferences().getFloat(environment.getName()+"_max",0));
                    environment.setMin_num(VLCApplication.getSharedPreferences().getFloat(environment.getName()+"_min",0));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    public void setEnvironment(float num) {
//        environmentNum = num;
//        List<Environment> list = new ArrayList<>();
//        Environment[] environment = {null};
//        environment[0] = getEnvironmentDate("气压", num);
//        if (list != null) {
//            if (list.size() != 0)
//                list.clear();
//            list.add(environment[0]);
//            if (adapter != null)
//                adapter.setList(list);
//        }
    }


    private Environment getEnvironmentDate(String name, float num) {
        Environment environment = new Environment();
        environment.setCurrent_num(num);
        environment.setName(name);
        switch (name) {
            case "气压":
                environment.setMin_num(VLCApplication.getSharedPreferences().getFloat(Environment.QIYA_MIN, (float) 0));
                environment.setMax_num(VLCApplication.getSharedPreferences().getFloat(Environment.QIYA_MAX, (float) 16.48));
                break;
        }
        return environment;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        getActivity().unregisterReceiver(environmentDataReceiver);
    }


}
