package com.dkzy.areaparty.phone.fragment02.subtitle;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.adapter.TorrentFile;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.callback.OnTorrentFileItemListener;
import com.dkzy.areaparty.phone.fragment02.ui.BreakTextView;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuyulin on 2017/6/7.
 */

public class SubTitleAdapter extends RecyclerView.Adapter<SubTitleAdapter.ViewHolder> {

    private Context mContext;
    private List<SubTitle> dataList;
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

    public SubTitleAdapter(List<SubTitle> dataList){
        this.dataList = dataList;

    }

    @Override
    public SubTitleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.subtitle_item, parent, false);
        final SubTitleAdapter.ViewHolder holder = new SubTitleAdapter.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final SubTitleAdapter.ViewHolder holder, final int position) {
        final SubTitle subTitle = dataList.get(position);
        if (subTitle.isChecked()){
            holder.radioButton.setChecked(true);
            select = position;
        }else {
            holder.radioButton.setChecked(false);

        }
        holder.name.setText(subTitle.getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select < dataList.size()){
                    dataList.get(select).setChecked(false);
                    notifyItemChanged(select);
                }
                subTitle.setChecked(true);
                notifyItemChanged(position);
            }
        });

    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void remove(TorrentFile itemModel){
        dataList.remove(itemModel);
    }
}
