package com.dkzy.areaparty.phone.fragment01.utorrent.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.utorrent.customView.AutoSplitTextView;
import com.dkzy.areaparty.phone.fragment01.utorrent.customView.CustomProgress;
import com.dkzy.areaparty.phone.fragment01.utorrent.listener.OnRecycleViewClickListener;
import com.dkzy.areaparty.phone.fragment01.utorrent.model.Torrent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhuyulin on 2017/10/20.
 */

public class UTorrentAdapter extends RecyclerView.Adapter<UTorrentAdapter.ViewHolder> {
    private Context context;
    private List<Torrent> torrentList;
    private OnRecycleViewClickListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cardView)
        CardView cardView;
        @BindView(R.id.progress)
        CustomProgress progressBar;
        @BindView(R.id.name)
        AutoSplitTextView name;
        @BindView(R.id.downloadspeed)
        TextView downloadSpeed;
        @BindView(R.id.size)
        TextView size;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.eta)
        TextView eta;
        @BindView(R.id.startOrPause)
        Button startOrPause;
        @BindView(R.id.stop)
        Button stop;
        @BindView(R.id.remove)
        Button remove;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

//            cardView = (CardView) view;
//            progressBar =(CustomProgress) view.findViewById(R.id.progress);
//            name = (AutoSplitTextView) view.findViewById(R.id.name);
//            downloadSpeed =(TextView) view.findViewById(R.id.downloadspeed);
//            size = (TextView) view.findViewById(R.id.size);
//            status = (TextView) view.findViewById(R.id.status);
//            eta = (TextView) view.findViewById(R.id.eta);
//            startOrPause = (Button) view.findViewById(R.id.startOrPause);
//            stop = (Button) view.findViewById(R.id.stop);
//            remove = (Button) view.findViewById(R.id.remove);
        }
    }

    public UTorrentAdapter(List<Torrent> torrentList) {
        this.torrentList = torrentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.utorrent_torrent_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Torrent torrent = torrentList.get(position);
        holder.progressBar.setProgress(torrent.getPgress());
        holder.downloadSpeed.setText(torrent.getDownloadSpeed());
        holder.name.setText(torrent.getName());
        holder.status.setText(translateStatus(torrent.getStatus()));
        holder.size.setText(torrent.getSize());
        holder.eta.setText(torrent.getETA());
        holder.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.stop(position);
            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.remove(position);
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getFileList(position);
            }
        });
        switch (torrent.getStatus()) {
            case "Downloading":
            case "Seeding":
                holder.stop.setVisibility(View.VISIBLE);
                holder.startOrPause.setText("暂停");
                holder.startOrPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.pause(position);
                    }
                });
                break;
            case "Stopped":
            case "Finished":
                holder.stop.setVisibility(View.GONE);
                holder.startOrPause.setText("开始");
                holder.startOrPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.start(position);
                    }
                });
                break;
            default:
                holder.stop.setVisibility(View.VISIBLE);
                holder.startOrPause.setText("开始");
                holder.startOrPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.start(position);
                    }
                });
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return torrentList.size();
    }

    public void setListener(OnRecycleViewClickListener listener) {
        this.listener = listener;
    }

    private String translateStatus(String status){
        String newStatus = "";
        switch (status){
            case "Downloading":
                newStatus = "下载中";
                break;
            case "Seeding":
                newStatus = "做种中";
                break;
            case "Stopped":
                newStatus = "已停止";
                break;
            case "Finished":
                newStatus = "已完成";
                break;
            case "Queued Seed":
                newStatus = "排队做种";
                break;
            case "Queued":
                newStatus = "排队中";
                break;
            case "Paused":
                newStatus = "已暂停";
                break;
            case "Checked":
                newStatus = "检查中";
                break;
            case "Connecting to peers":
                newStatus = "连接种源";
                break;
            case "Finding peers":
                newStatus = "寻找种源";
                break;
            case "Deleting":
                newStatus = "删除中";
                break;
            default:
                newStatus = status;
                break;

        }

        if (!TextUtils.isEmpty(newStatus)){
            return newStatus;
        }else {
            return status;
        }

    }

}
