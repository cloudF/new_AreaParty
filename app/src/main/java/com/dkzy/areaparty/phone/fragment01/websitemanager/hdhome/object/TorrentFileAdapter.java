package com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome.object;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.callback.OnTorrentFileItemListener;

import java.util.List;

/**
 * Created by zhuyulin on 2017/6/7.
 */

public class TorrentFileAdapter extends RecyclerView.Adapter<TorrentFileAdapter.ViewHolder> {

    private Context mContext;
    private List<TorrentFile> mTorrentList;
    private OnTorrentFileItemListener mListener;

    static class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView torrentFileName;
        Button downloadBtn, deleteBtn;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            torrentFileName = (TextView) view.findViewById(R.id.torrent_file_name);
            downloadBtn = (Button) view.findViewById(R.id.download);
            deleteBtn = (Button) view.findViewById(R.id.delete);
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.web1080_torrent_file_item, parent, false);
        final TorrentFileAdapter.ViewHolder holder = new TorrentFileAdapter.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final TorrentFileAdapter.ViewHolder holder, final int position) {
        final TorrentFile torrent = mTorrentList.get(position);

        holder.torrentFileName.setText(torrent.getTorrentFileName());
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.delete(position);
            }
        });
        holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.download(position);
            }
        });
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
}
