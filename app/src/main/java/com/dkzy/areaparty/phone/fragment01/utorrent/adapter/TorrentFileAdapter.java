package com.dkzy.areaparty.phone.fragment01.utorrent.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.utorrent.model.TorrentFile;

import java.util.List;

/**
 * Created by zhuyulin on 2017/6/7.
 */

public class TorrentFileAdapter extends RecyclerView.Adapter<TorrentFileAdapter.ViewHolder> {

    private Context mContext;
    private List<TorrentFile> mTorrentList;

    public List<TorrentFile> getmTorrentList() {
        return mTorrentList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView torrentFileName;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
           torrentFileName = (TextView) view.findViewById(R.id.name);

        }
    }

    public TorrentFileAdapter(List<TorrentFile> torrentList){
        mTorrentList = torrentList;
    }

    @Override
    public TorrentFileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.utorrent_selecttorrent_item, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final TorrentFileAdapter.ViewHolder holder,final int position) {
        final TorrentFile torrent = mTorrentList.get(position);

        holder.torrentFileName.setText(torrent.getTorrentFileName());

    }

    @Override
    public int getItemCount() {
        return mTorrentList.size();
    }


    public void remove(TorrentFile itemModel){
        mTorrentList.remove(itemModel);
    }

    public TorrentFile getItem(int pos){
        return mTorrentList.get(pos);
    }
}
