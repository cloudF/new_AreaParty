package com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;


import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.callback.OnTorrentFileItemListener;
import com.dkzy.areaparty.phone.fragment02.ui.BreakTextView;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuyulin on 2017/6/7.
 */

public class TorrentFileAdapter extends RecyclerView.Adapter<TorrentFileAdapter.ViewHolder> {

    private Context mContext;
    private List<TorrentFile> mTorrentList;
    private OnTorrentFileItemListener mListener;
    private boolean isAppContent;

    static class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        BreakTextView torrentFileName;
        Button downloadBtn, deleteBtn , reNameBtn;
        CheckBox checkBox;
        SwipeMenuLayout rootView;

        public ViewHolder(View view){
            super(view);
            rootView = (SwipeMenuLayout) view;
            cardView = (CardView) view.findViewById(R.id.cardView);
            torrentFileName = (BreakTextView) view.findViewById(R.id.torrent_file_name);
            downloadBtn = (Button) view.findViewById(R.id.btnDownload);
            deleteBtn = (Button) view.findViewById(R.id.btnDelete);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            reNameBtn = (Button) view.findViewById(R.id.reName);
        }
    }

    public TorrentFileAdapter(List<TorrentFile> torrentList, boolean isAppContent){
        mTorrentList = torrentList;
        this.isAppContent = isAppContent;
    }

    @Override
    public TorrentFileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.web1080_torrent_file_item, parent, false);
        final TorrentFileAdapter.ViewHolder holder = new TorrentFileAdapter.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final TorrentFileAdapter.ViewHolder holder,final int position) {
        final TorrentFile torrent = mTorrentList.get(position);
        if (isAppContent){
            holder.reNameBtn.setVisibility(View.VISIBLE);

        }else holder.reNameBtn.setVisibility(View.GONE);
        if (torrent.isShow()){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setClickable(false);
            holder.rootView.setSwipeEnable(false);
            holder.checkBox.setChecked(torrent.isChecked());
        }else {
            holder.checkBox.setVisibility(View.GONE);
            holder.rootView.setSwipeEnable(true);
        }
        holder.torrentFileName.setText(torrent.getTorrentFileName());
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.delete(position);
                holder.rootView.quickClose();
            }
        });
        holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.download(position);
                holder.rootView.quickClose();
            }
        });
        holder.reNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.reName(position);
                holder.rootView.quickClose();
            }
        });
        if (!torrent.isShow() && holder.checkBox.getVisibility() == View.GONE){//长按进入选择模式
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    for (TorrentFile f : mTorrentList){
                        f.setShow(true);
                        f.setChecked(false);
                    }
                    torrent.setChecked(true);
                    notifyDataSetChanged();
                    mListener.selectModel();
                    return true;
                }
            });
        }
        if (torrent.isShow() && holder.checkBox.getVisibility() == View.VISIBLE){
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(torrent.isChecked()){
                        torrent.setChecked(false);
                        holder.checkBox.setChecked(false);
                    }else {
                        torrent.setChecked(true);
                        holder.checkBox.setChecked(true);
                    }
                }
            });
        }


//        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                torrent.isSelect = holder.cbSelect.isChecked();
//                mOnItemListener.checkBoxClick(position);
//            }
//        });
//
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mOnItemListener.onItemClick(view,position);
//            }
//        });
//
//        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                mOnItemListener.onItemLongClick(view,position);
//                return true;
//            }
//        });
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

    public TorrentFile getItem(int pos){
        return mTorrentList.get(pos);
    }

    private List<TorrentFile> getSelectedTorrent(){
        List<TorrentFile> selectList = new ArrayList<>();
        for (TorrentFile f : mTorrentList){
            if (f.isShow()&&f.isChecked()){
                selectList.add(f);
            }
        }
        return selectList;
    }
}
