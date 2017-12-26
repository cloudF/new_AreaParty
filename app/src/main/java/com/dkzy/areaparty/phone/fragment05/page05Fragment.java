package com.dkzy.areaparty.phone.fragment05;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dkzy.areaparty.phone.R;

/**
 * Created by boris on 2016/11/29.
 * TAB05---VIP的Fragment
 */

public class page05Fragment extends Fragment implements View.OnClickListener {
    private Context context;
    private LinearLayout accessPhoneVIPAppLL;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.tab05, container, false);

        initView(fragment);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView(View fragment) {
        accessPhoneVIPAppLL = (LinearLayout) fragment.findViewById(R.id.accessPhoneVIPAppLL);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.accessPhoneVIPAppLL:
                startActivity(new Intent(context, phoneVIPAppActivity.class));
                break;
        }
    }

    private void initEvent() {
        accessPhoneVIPAppLL.setOnClickListener(this);
    }
}
