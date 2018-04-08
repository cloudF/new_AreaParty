package com.dkzy.areaparty.phone.fragment03.blueTooth;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.utorrent.adapter.WrapContentLinearLayoutManager;
import com.dkzy.areaparty.phone.fragment01.utorrent.customView.MyItemDecoration;
import com.dkzy.areaparty.phone.fragment02.subtitle.SubTitle;
import com.dkzy.areaparty.phone.fragment02.subtitle.SubTitleAdapter;

import java.util.List;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/3/6 13:00
 */

public class ActionDialog_blueTooth extends Dialog {
    private TextView title;
    private Button positiveButton;
    private Button negativeButton;
    private Context context;
    private RecyclerView recyclerView;
    private BlueToothAdapter adapter;
    private List<BlueDevice> blueList;
    private Handler handler;
    public ActionDialog_blueTooth(Context context, List<BlueDevice> blueList, Handler handler) {
        super(context, R.style.CustomDialog);
        this.context = context;
        this.blueList = blueList;
        this.handler = handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new BlueToothAdapter(blueList, handler);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_subtitle, null);
        title = (TextView) mView.findViewById(R.id.exittitle);
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        positiveButton = (Button) mView.findViewById(R.id.exitpositiveButton);
        positiveButton.setVisibility(View.GONE);
        negativeButton = (Button) mView.findViewById(R.id.exitnegativeButton);
        negativeButton.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        recyclerView.addItemDecoration(new MyItemDecoration());
        recyclerView.getItemAnimator().setAddDuration(0);
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.getItemAnimator().setMoveDuration(0);
        recyclerView.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(adapter);


        super.setContentView(mView);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth() - 100; //设置dialog的宽度为当前手机屏幕的宽度-100
        //p.y = 200;
        getWindow().setAttributes(p);
    }


    public void setOnPositiveListener(View.OnClickListener listener) {
        positiveButton.setOnClickListener(listener);
    }
    public void setOnNegativeListener(View.OnClickListener listener) {
        negativeButton.setOnClickListener(listener);
    }
    public void setTitleText(String text) {
        this.title.setText(text);
    }
    public void setPositiveButtonText(String text) {
        this.positiveButton.setText(text);
    }
    public void setNegativeButtonText(String text) {
        this.negativeButton.setText(text);
    }
}