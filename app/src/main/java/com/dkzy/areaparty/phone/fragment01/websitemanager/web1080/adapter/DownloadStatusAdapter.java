package com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.callback.OnTorrentFileItemListener;

import java.util.List;

/**
 * Created by zhuyulin on 2017/6/7.
 */

public class DownloadStatusAdapter extends RecyclerView.Adapter<DownloadStatusAdapter.ViewHolder>{
    private Context mContext;
    private List<DownloadStatus> mTorrentList;
    private OnTorrentFileItemListener mListener;

    static class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView torrentFileName;


        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            torrentFileName = (TextView) view.findViewById(R.id.torrent_file_name);

        }
    }

    public DownloadStatusAdapter(List<DownloadStatus> torrentList){
        mTorrentList = torrentList;
    }

    @Override
    public DownloadStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.web1080_pt_torrent_file__item, parent, false);
        final DownloadStatusAdapter.ViewHolder holder = new DownloadStatusAdapter.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final DownloadStatusAdapter.ViewHolder holder,final int position) {
        final DownloadStatus torrent = mTorrentList.get(position);
        holder.torrentFileName.setText(torrent.getTorrentFileName());




    }

    @Override
    public int getItemCount() {
        return mTorrentList.size();
    }

    public void setOnTorrentFileListent(OnTorrentFileItemListener mListener) {
        this.mListener = mListener;
    }

    public void remove(TorrentFile itemModel){
        mTorrentList.remove(itemModel);
    }

    public DownloadStatus getItem(int pos){
        return mTorrentList.get(pos);
    }
}
