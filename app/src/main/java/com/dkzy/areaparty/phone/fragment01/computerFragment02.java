package com.dkzy.areaparty.phone.fragment01;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.base.MySimpleAdapter;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ProcessFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedActionMessageFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boris on 2016/11/29.
 *
 */

public class computerFragment02 extends Fragment {

    private LayoutInflater layoutInflater;

    private RecyclerView mRecyclerView;
    private List<ProcessFormat> initProcesses;
    private MySimpleAdapter mAdapter;

    private static Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab01_computermonitor_tab2fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initProcesses = new ArrayList<>();
        layoutInflater = LayoutInflater.from(getActivity());
        initViews();
        initEvent();
    }

    private void initEvent() {
        mAdapter = new MySimpleAdapter(getActivity(), initProcesses);
        mAdapter.setOnItemClickListener(new MySimpleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ProcessFormat process) {
                ProcessFormat temp = prepareDataForFragment.getProcessById(process.getId());
                process = (temp != null) ? temp : process;
                processDialog(process);
            }
        });
        mAdapter.setOnItemLongClickListener(new MySimpleAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, ProcessFormat process) {
                deleteProcessDialog(process);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case OrderConst.actionFail_order:
                        Toast.makeText(getActivity(), "操作失败，失败信息：" + (msg.obj), Toast.LENGTH_SHORT).show();
                        break;
                    case OrderConst.actionSuccess_order:
                        mAdapter.deleteItem(msg.arg1);
                        Toast.makeText(getActivity(), "操作成功", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.id_recyclerView);
    }

    /**
     * <summary>
     *  根据给定进程生成进程信息对话框
     * </summary>
     * <param name="process">进程</param>
     */
    private void processDialog(ProcessFormat process) {
        String name      = process.getName() + "";
        String path      = process.getPath() + "";
        String cpu       = process.getCpu() + "";
        String memory    = process.getMemory() + "";
        View titleView   = layoutInflater.inflate(R.layout.tab01_computermonitor_processinfor_dialogtitle, null);
        View contentView = layoutInflater.inflate(R.layout.tab01_computermonitor_processinfor_dialogcontent, null);

        ((TextView) titleView.findViewById(R.id.processName_dlTv)).setText(name);
        ((TextView) contentView.findViewById(R.id.processPath_dlTv)).setText(path);
        ((TextView) contentView.findViewById(R.id.processCpu_dlTv)).setText(cpu);
        ((TextView) contentView.findViewById(R.id.processMemory_dlTv)).setText(memory);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCustomTitle(titleView);
        builder.setView(contentView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    /**
     * <summary>
     *  根据指定进程生成进程操作对话框
     * </summary>
     * <param name="process">进程</param>
     */
    private void deleteProcessDialog(final ProcessFormat process) {
        View processTitleView = layoutInflater.inflate(R.layout.tab01_computermonitor_noticetitle, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCustomTitle(processTitleView);
        builder.setMessage("尝试从主机系统中关闭进程" + process.getName());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Thread() {
                    @Override
                    public void run() {
                        ReceivedActionMessageFormat message = prepareDataForFragment.getActionStateData(
                                OrderConst.processAction_name,
                                OrderConst.process_closeById_command,
                                process.getId() + "");
                        if(message.getStatus() == 404) {
                            Message msg = new Message();
                            msg.what = OrderConst.actionFail_order;
                            msg.obj = message.getMessage();
                            handler.sendMessage(msg);
                        }
                        if(message.getStatus() == 200) {
                            Message msg = new Message();
                            msg.what = OrderConst.actionSuccess_order;
                            msg.arg1 = process.getId();
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setCancelable(false);
        builder.show();
    }



    /**
     * <summary>
     *  更新list中的item
     * </summary>
     * <param name="increasedProcesses">增加的进程</param>
     * <param name="decreasedProcesses">减少的进程</param>
     */
    public void refreshItems(List<ProcessFormat> increasedProcesses, List<Integer> decreasedProcesses) {
        if(!increasedProcesses.isEmpty()) {
            mAdapter.addData(increasedProcesses);
        }
        if(!decreasedProcesses.isEmpty()) {
            mAdapter.deleteData(decreasedProcesses);
        }
    }
}
