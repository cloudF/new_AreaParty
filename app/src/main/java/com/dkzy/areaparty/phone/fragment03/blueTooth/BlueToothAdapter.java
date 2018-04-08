package com.dkzy.areaparty.phone.fragment03.blueTooth;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment02.subtitle.SubTitle;
import com.dkzy.areaparty.phone.fragment02.ui.BreakTextView;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;

import java.util.List;

/**
 * Created by zhuyulin on 2017/6/7.
 */

public class BlueToothAdapter extends RecyclerView.Adapter<BlueToothAdapter.ViewHolder> {
    private Context mContext;
    private List<BlueDevice> dataList;
    private Handler handler;

    static class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        BreakTextView name;
        BreakTextView status;
        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardView);
            name = (BreakTextView) view.findViewById(R.id.name);
            status = (BreakTextView) view.findViewById(R.id.status);
        }
    }

    public BlueToothAdapter(List<BlueDevice> dataList,Handler handler){
        this.dataList = dataList;
        this.handler = handler;
    }

    @Override
    public BlueToothAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.bluetooth_item, parent, false);
        final BlueToothAdapter.ViewHolder holder = new BlueToothAdapter.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final BlueToothAdapter.ViewHolder holder, final int position) {
        final BlueDevice blueDevice = dataList.get(position);
        holder.name.setText(blueDevice.getName());
        if (blueDevice.isConnected()){
            holder.status.setText("已连接");
        }else if (blueDevice.getStatus().equals("已配对")){
            holder.status.setText("已配对");
        }else {
            holder.status.setText("未配对");
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!blueDevice.getStatus().equals("已配对")){
                    TVAppHelper.vedioPlayControlConnectBlueTooth(blueDevice.getAddress());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            blueDevice.setStatus("已配对");
                            notifyItemChanged(position);
                        }
                    },1000);

                }else {
                    TVAppHelper.vedioPlayControlUnpairBlueTooth(blueDevice.getAddress());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            blueDevice.setStatus("未配对");
                            notifyItemChanged(position);
                        }
                    },1000);
                }

            }
        });
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
