package com.dkzy.areaparty.phone.fragment02.subtitle;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.utorrent.adapter.WrapContentLinearLayoutManager;
import com.dkzy.areaparty.phone.fragment01.utorrent.customView.MyItemDecoration;
import com.dkzy.areaparty.phone.model_comman.MyAdapter;

import java.util.List;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/3/6 13:00
 */

public class ActionDialog_subTitle extends Dialog {
    private TextView title;
    private Button positiveButton;
    private Button negativeButton;
    private Context context;
    private RecyclerView recyclerView;
    private SubTitleAdapter adapter;
    private List<SubTitle> subTitleList;
    private boolean submit;
    public ActionDialog_subTitle(Context context, List<SubTitle> subTitleList) {
        super(context, R.style.CustomDialog);
        this.context = context;
        this.subTitleList = subTitleList;
        submit = false;
    }

    public boolean isSubmit() {
        return submit;
    }

    public void setSubmit(boolean submit) {
        this.submit = submit;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SubTitleAdapter(subTitleList);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_subtitle, null);
        title = (TextView) mView.findViewById(R.id.exittitle);
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        positiveButton = (Button) mView.findViewById(R.id.exitpositiveButton);
        negativeButton = (Button) mView.findViewById(R.id.exitnegativeButton);
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
    public String getSelected(){
        return subTitleList.get(adapter.getSelect()).getId();
    };
    public String getSelectedName(){
        return subTitleList.get(adapter.getSelect()).getName();
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
    public void setData(List<SubTitle> subTitleList){
        this.subTitleList.clear();
        this.subTitleList.addAll(subTitleList);
        adapter.notifyDataSetChanged();
    }
}