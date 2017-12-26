package com.dkzy.areaparty.phone.fragment01;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.base.BaseFragment;
import com.dkzy.areaparty.phone.fragment01.base.DownloadingAdapter;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetBroadcastReceiver;
import com.lzy.okgo.db.DownloadManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.task.XExecutor;

import java.util.List;

/**
 * Created by borispaul on 17-4-27.
 */

public class downloadTab01Fragment extends BaseFragment implements
        XExecutor.OnAllTaskEndListener, NetBroadcastReceiver.netEventHandler {
    private List<Progress> allTask;
    private OkDownload okDownload;

    private Context context;
    private RecyclerView recyclerView;
    private ImageView downloadingNothingIV;
    private DownloadingAdapter adapter;


    @Override
    protected void initData() {

    }

    @Override
    public void onNetChange() {
        Log.e("test", "tab01");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab04_downloading_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.downloadingFileRV);
        downloadingNothingIV = (ImageView) view.findViewById(R.id.downloadingNothingIV);

        okDownload = OkDownload.getInstance();
        allTask = DownloadManager.getInstance().getAll();
        Log.e("circle1", "ResumeAcTAB01" + allTask.size());

        adapter = new DownloadingAdapter(context, this);
        adapter.updateData(DownloadingAdapter.TYPE_ING);



        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        okDownload.addOnAllTaskEndListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NetBroadcastReceiver.mListeners.add(this);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setAdapter(adapter);

        if(DownloadManager.getInstance().getDownloading().size() == 0)
            downloadingNothingIV.setVisibility(View.VISIBLE);
        else downloadingNothingIV.setVisibility(View.GONE);
    }

    public void changeView() {
        if(DownloadManager.getInstance().getDownloading().size() == 0)
            downloadingNothingIV.setVisibility(View.VISIBLE);
        else downloadingNothingIV.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        changeView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        okDownload.removeOnAllTaskEndListener(this);
        adapter.unRegister();
    }

    @Override
    public void onAllTaskEnd() {
        for(Progress downloadInfo : allTask) {
            if(downloadInfo.status != Progress.FINISH) {
                Toast.makeText(context, "所有下载线程结束，部分下载未完成", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(context, "所有下载任务完成", Toast.LENGTH_SHORT).show();
    }


    public void removeAllTasks() {
        okDownload.removeAll();
        adapter.updateData(DownloadingAdapter.TYPE_ING);
        adapter.notifyDataSetChanged();
    }

    public void pauseAllTasks() {
        okDownload.pauseAll();
    }

    public void startAll(View view) {
        okDownload.startAll();
    }
}
