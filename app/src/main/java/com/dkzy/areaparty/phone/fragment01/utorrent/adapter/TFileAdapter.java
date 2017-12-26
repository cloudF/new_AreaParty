package com.dkzy.areaparty.phone.fragment01.utorrent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.utorrent.listener.OnCheckBoxChangeListener;
import com.dkzy.areaparty.phone.fragment01.utorrent.model.TFile;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by zhuyulin on 2017/10/24.
 */

public class TFileAdapter extends RecyclerView.Adapter<TFileAdapter.ViewHolder> {
    private Context context;
    private List<TFile> fileList;
    private OnCheckBoxChangeListener listener;

    public TFileAdapter(List<TFile> fileList) {
        this.fileList = fileList;
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.size)
        TextView size;
        @BindView(R.id.downloaded)
        TextView downloaded;
        @BindView(R.id.progress)
        TextView progress;
        @BindView(R.id.checkBox)
        CheckBox checkBox;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.utorrent_file_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        TFile file = fileList.get(position);
        holder.name.setText(file.getName());
        holder.size.setText(file.getSize());
        holder.progress.setText(file.getProgress());
        holder.downloaded.setText(file.getDownloaded());
        if (file.getPriority() == 0){
            holder.checkBox.setChecked(false);
        }else {
            holder.checkBox.setChecked(true);
        }
//        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.w("TFileAdapter",""+position);
////                if (isChecked){
////                    listener.checked(position);
////                }else {
////                    listener.unchecked(position);
////                }
//            }
//        });
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()){
                    listener.checked(position);
                }else {
                    listener.unchecked(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void setListener(OnCheckBoxChangeListener listener) {
        this.listener = listener;
    }
}
