package com.dkzy.areaparty.phone.fragment02.lyric;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.adapter.TorrentFile;
import com.dkzy.areaparty.phone.fragment02.subtitle.SubTitle;
import com.dkzy.areaparty.phone.fragment02.ui.BreakTextView;

import java.util.List;

/**
 * Created by zhuyulin on 2017/6/7.
 */

public class LyricAdapter extends RecyclerView.Adapter<LyricAdapter.ViewHolder> {

    private Context mContext;
    private List<Lyric> dataList;
    private int select;

    public int getSelect() {
        return select;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        BreakTextView name;
        RadioButton radioButton;


        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardView);
            name = (BreakTextView) view.findViewById(R.id.name);
            radioButton = (RadioButton) view.findViewById(R.id.radioButton);
        }
    }

    public LyricAdapter(List<Lyric> dataList){
        this.dataList = dataList;

    }

    @Override
    public LyricAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.subtitle_item, parent, false);
        final LyricAdapter.ViewHolder holder = new LyricAdapter.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final LyricAdapter.ViewHolder holder, final int position) {
        final Lyric lyric = dataList.get(position);
        if (lyric.isChecked()){
            holder.radioButton.setChecked(true);
            select = position;
        }else {
            holder.radioButton.setChecked(false);

        }
        String name = lyric.getSong()+"...."+lyric.getSinger();
        holder.name.setText(name);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select < dataList.size()){
                    dataList.get(select).setChecked(false);
                    notifyItemChanged(select);
                }
                lyric.setChecked(true);
                notifyItemChanged(position);
            }
        });

    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
