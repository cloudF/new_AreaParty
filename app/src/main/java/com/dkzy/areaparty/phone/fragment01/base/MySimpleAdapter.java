package com.dkzy.areaparty.phone.fragment01.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ProcessFormat;

import java.util.List;

/**
 * Project Name： recyclerViewTest
 * Description:
 * Author: boris
 * Time: 2016/12/23 16:30
 */
public class MySimpleAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<ProcessFormat> processes;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ProcessFormat process);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, ProcessFormat process);
    }



    public MySimpleAdapter(Context context, List<ProcessFormat> processes) {
        this.mContext = context;
        this.processes = processes;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.tab01_computermonitor_process_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        String ID = processes.get(position).getId() + "";
        String name = processes.get(position).getName() + "";
        holder.processID_textView.setText(ID);
        holder.processName_textView.setText(name);

        if(mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(holder.itemView, processes.get(position));
                }
            });
        }

        if(mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnItemLongClickListener.onItemLongClick(holder.itemView, processes.get(position));
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return processes.size();
    }

    public void addData(List<ProcessFormat> increasedProcesses) {
        for(ProcessFormat process:increasedProcesses) {
            processes.add(processes.size(), process);
            notifyItemInserted(processes.size());
        }
    }

    public void deleteData(List<Integer> decreasedProcesses) {
        for(int processId:decreasedProcesses) {
            for(int i = 0; i < processes.size(); i++) {
                if(processes.get(i).getId() == processId) {
                    processes.remove(i);
                    notifyItemRemoved(i);
                }
            }
        }
    }

    public void deleteItem(int processId) {
        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i).getId() == processId) {
                processes.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {
    TextView processID_textView;
    TextView processName_textView;


    public MyViewHolder(View arg0) {
        super(arg0);
        processID_textView = (TextView) arg0.findViewById(R.id.processId_tv);
        processName_textView = (TextView) arg0.findViewById(R.id.processName_tv);

    }
}