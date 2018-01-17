package com.dkzy.areaparty.phone.fragment06;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.FileTypeConst;
import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.ui.DeleteDialog;
import com.dkzy.areaparty.phone.fragment01.utils.prepareDataForFragment;
import com.dkzy.areaparty.phone.fragment06.zhuyulin.DownloadBean;
import com.dkzy.areaparty.phone.fragment06.zhuyulin.DownloadProcess;
import com.dkzy.areaparty.phone.fragment06.zhuyulin.ReceiveData;
import com.dkzy.areaparty.phone.fragment06.zhuyulin.ReceiveDataFormat;
import com.dkzy.areaparty.phone.fragment06.zhuyulin.ReceiveDownloadProcessFormat;
import com.dkzy.areaparty.phone.fragment06.zhuyulin.ReceivedDownloadListFormat;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.JsonUitl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import protocol.Msg.GetDownloadFileInfo;
import protocol.ProtoHead;
import server.NetworkPacket;



/**
 * Created by SnowMonkey on 2017/5/31.
 */

public class DownloadStateFragment extends Fragment {
    public static final int PAUSE = 1;//暂停
    public static final int DOWNLOADING = 2;//正在下载
    public static  final int TORRENT = 3; //种子文件
    public static  final int DOWNFILE = 4; //普通下载文件
    public static final int DOWNLOADED = 5;//下载完成
    private LinearLayout downloadStateFragmentRefresh;
    private ListView downloadFileStateFragmentList = null;
    private TextView downloadStateFileFinish = null;
    public static List<HashMap<String, Object>> downloadFileStateData = null;
    private DownloadStateFragmentFileAdapter downloadFileStateFragmentFileAdapter;
    private long timer = 0;
    public static Handler mHandler;

    private List<DownloadBean> beanList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab06_download_manager_statefragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            getData();
            initViews();
            initEvents();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initData(){
        downloadFileStateData = (downloadFileStateData==null)? new ArrayList<HashMap<String, Object>>():downloadFileStateData;
    }
    private void getData(){
        //initData();
        beanList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ReceivedDownloadListFormat receive = (ReceivedDownloadListFormat) prepareDataForFragment.getFileActionStateData(
                            OrderConst.fileAction_name,
                            OrderConst.GETDOWNLOADSTATE,
                            "");
                    if (receive.getStatus() == 200){
                        ReceiveDataFormat receiveDataFormat = receive.getData();
                        if (receiveDataFormat.getDownloading_files().size() > 0 ){
                            for (ReceiveData data : receiveDataFormat.getDownloading_files()){
                                beanList.add(new DownloadBean(data,"下载中"));
                            }
                        }
                        if (receiveDataFormat.getPause_files().size() > 0){
                            for (ReceiveData data : receiveDataFormat.getPause_files()){
                                beanList.add(new DownloadBean(data,"终止"));
                            }
                        }
                    }
                    if (beanList.size() > 0){
                        mHandler.sendEmptyMessage(4);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void initViews() {
        downloadStateFragmentRefresh = (LinearLayout) getActivity().findViewById(R.id.downloadStateFragmentRefresh);
        downloadFileStateFragmentList = (ListView) getActivity().findViewById(R.id.downloadFileStateFragmentList);
        //downloadStateFileFinish = (TextView) getActivity().findViewById(R.id.downloadStateFileFinish);
        downloadFileStateFragmentFileAdapter = new DownloadStateFragmentFileAdapter(getActivity(), beanList);
        downloadFileStateFragmentList.setAdapter(downloadFileStateFragmentFileAdapter);
        //new Thread(new getProgress()).start();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        Toast.makeText(getActivity(), "请确保电脑端程序已连接上远程服务器",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getActivity(), "正在获取，请稍后",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        try {
                            downloadFileStateData.clear();
                            String fileListStr = (String) msg.obj;
                            System.out.println(fileListStr);
                            Gson gson = new Gson();
                            List<ProgressObj> fileList = gson.fromJson(fileListStr, new TypeToken<List<ProgressObj>>(){}.getType());
                            for(ProgressObj progress : fileList){
                                String fileName = progress.getFileName();
                                String fileSize = progress.getFileSize();
                                String fileTotalSize = progress.getFileTotalSize();
                                String fileProgress = progress.getProgress();
                                int fileState = progress.getState();
                                if(fileState == 0)
                                    addToList(fileSize, fileTotalSize, fileIndexToImgId.toImgId(FileTypeConst.determineFileType(fileName)), fileProgress, DOWNLOADING, DOWNFILE, fileName);
                                else if(fileState == 1)
                                    addToList(fileSize, fileTotalSize, fileIndexToImgId.toImgId(FileTypeConst.determineFileType(fileName)), fileProgress, DOWNLOADED, DOWNFILE, fileName);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;


                    case 4:
                        downloadFileStateFragmentFileAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        };
    }
    private void initEvents(){
        downloadStateFragmentRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新向服务器请求下载状态 刷新列表
                if(new Date().getTime() - timer > 5000) {
                    getData();
                }else{
                    Toast.makeText(getActivity(), "点击过于频繁，5秒内请勿连续点击",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class getProgress implements Runnable{
        //获取下载进度
        @Override
        public void run() {
            GetDownloadFileInfo.GetDownloadFileInfoReq.Builder builder = GetDownloadFileInfo.GetDownloadFileInfoReq.newBuilder();
            builder.setUserId(Login.userId);
            byte[] byteArray = new byte[0];
            try {
                byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.GET_DOWNLOAD_FILE_INFO_REQ.getNumber(), builder.build().toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Login.base.writeToServer(Login.outputStream, byteArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void torrentFileStart(Message msg){
    }

    public void torrentFileContinue(Message msg){
    }

    public void torrentFilePause(Message msg){
    }

    public void downloadFileContinue(Message msg){
        initData();
        Iterator<HashMap<String,Object>> it = downloadFileStateData.iterator();
        while(it.hasNext()){
            HashMap<String,Object> hm = it.next();
            if((int)hm.get("downloadStateFileId") == (int)msg.obj){
                hm.put("downloadStateFileState",DOWNLOADING);
                if(downloadFileStateFragmentList != null)
                    downloadFileStateFragmentFileAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public void downloadFilePause(Message msg){
        initData();
        Iterator<HashMap<String,Object>> it = downloadFileStateData.iterator();
        while(it.hasNext()){
            HashMap<String,Object> hm = it.next();
            if((int)hm.get("downloadStateFileId") == (int)msg.obj){
                hm.put("downloadStateFileState",PAUSE);
                if(downloadFileStateFragmentList != null)
                    downloadFileStateFragmentFileAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public void downloadFileStart(Message msg){
        initData();
        HashMap<String, Object> item = (HashMap<String, Object>) msg.obj;
        downloadFileStateData.add(item);
        if(downloadFileStateFragmentList != null)
            downloadFileStateFragmentFileAdapter.notifyDataSetChanged();
    }

    public void addToList(String fileSize, String fileTotalSize, int fileImg, String fileProcess, int fileState, int fileStyle, String fileName){
        HashMap<String, Object> item = new HashMap<>();
        item.put("downloadStateFileName", fileName);
        item.put("downloadStateFileSize",Long.valueOf(fileSize)/1024+"M");
        item.put("downloadStateFileTotalSize", "/"+Long.valueOf(fileTotalSize)/1024+"M");
        item.put("downloadStateFileImg",fileImg);
        item.put("downloadStateFileProgress", fileProcess);
        item.put("downloadStateFileState",fileState);
        item.put("downloadStateFileStyle",fileStyle);
        if(fileStyle == DOWNFILE) {
            item.put("downloadStateFileId", downloadFileStateData.size());
            if(fileState == DOWNLOADED)
                downloadFileStateData.add(0,item);
            else if(fileState == DOWNLOADING)
                downloadFileStateData.add(item);
            if (downloadFileStateFragmentList != null)
                downloadFileStateFragmentFileAdapter.notifyDataSetChanged();
        }
    }
    public void agreeDownload(Message msg){
        initData();
        Log.e("downFolder","agreeDownload-downloadFolder");
        fileObj file = (fileObj) msg.obj;
        HashMap<String, Object> item = new HashMap<>();
        item.put("downloadStateFileName",file.getFileName());
        item.put("downloadStateFileSize","0M");
        item.put("downloadStateFileTotalSize", "/"+file.getFileSize()/1024+"M");
        item.put("downloadStateFileImg", fileIndexToImgId.toImgId(FileTypeConst.determineFileType(file.getFileName())));
        item.put("downloadStateFileProgress","0.0%");
        item.put("downloadStateFileState",DOWNLOADING);
        item.put("downloadStateFileStyle",DOWNFILE);
        item.put("downloadStateFileId",downloadFileStateData.size());
        downloadFileStateData.add(item);
        if(downloadFileStateFragmentList != null)
            downloadFileStateFragmentFileAdapter.notifyDataSetChanged();
    }

    private class DownloadStateFragmentFileAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<DownloadBean> beanList;
        public DownloadStateFragmentFileAdapter(Context context, List<DownloadBean> beanList) {
            mInflater = LayoutInflater.from(context);
            this.beanList = beanList;
        }
        @Override
        public int getCount() {
            return beanList.size();
        }

        @Override
        public Object getItem(int position) {
            return beanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final ViewHolderFile holder = new ViewHolderFile();
            final DownloadBean bean = beanList.get(position);

                convertView = mInflater.inflate(R.layout.tab06_download_manager_stateitem, null);
                holder.rootView = (SwipeMenuLayout) convertView;
                holder.iv_expand = (ImageView) convertView.findViewById(R.id.iv_expand);
                holder.tv_fileName = (TextView) convertView.findViewById(R.id.tv_fileName);
                holder.tv_downloadState  = (TextView) convertView.findViewById(R.id.tv_downloadState);
                holder.tv_downloadProgress = (TextView) convertView.findViewById(R.id.tv_downloadProgress);
                holder.tv_downloadSpeed = (TextView)  convertView.findViewById(R.id.tv_downloadSpeed);
                holder.tv_stopTime = (TextView) convertView.findViewById(R.id.tv_stopTime) ;
                holder.tv_stop = (Button) convertView.findViewById(R.id.tv_stop);
                holder.tv_delete = (Button) convertView.findViewById(R.id.tv_delete);
                holder.info = (RelativeLayout) convertView.findViewById(R.id.info);
                holder.item = (LinearLayout) convertView.findViewById(R.id.item);


            holder.rootView.setSwipeEnable(true);
            holder.info.setVisibility(View.GONE);
            holder.tv_fileName.setText(bean.getName());
            holder.tv_downloadState.setText(bean.getState());
            holder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*prepareDataForFragment.getFileActionStateData(
                            OrderConst.fileAction_name,
                            OrderConst.GETDOWNLOADProcess,
                            bean.getId()
                    );*/
                    deleteDialog(bean);
                }
            });
            if (bean.getState().equals("下载中")){
                holder.tv_stop.setText("暂停");
                holder.tv_stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (MyApplication.isSelectedPCOnline()){
                                    prepareDataForFragment.getFileActionStateData(
                                            OrderConst.fileAction_name,
                                            OrderConst.STOPDOWNLOAD,
                                            bean.getId()
                                    );
                                    getData();
                                }else {
                                    Toasty.warning(getContext(),"与电脑断开连接").show();
                                }

                            }
                        }).start();
                    }
                });
            }else {
                holder.tv_stop.setText("恢复");
                holder.tv_stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toasty.info(getContext(),"请到好友分享重新请求下载该文件",Toast.LENGTH_LONG).show();
                        /*new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (MyApplication.isSelectedPCOnline()){
                                    prepareDataForFragment.getFileActionStateData(
                                            OrderConst.fileAction_name,
                                            OrderConst.RECOVERDOWNLOAD,
                                            bean.getId()
                                    );
                                    getData();
                                }else {
                                    Toasty.warning(getContext(),"与电脑断开连接").show();

                                }

                            }
                        }).start();*/
                    }
                });
            }


            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.info.getVisibility() == View.GONE){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (MyApplication.isSelectedPCOnline()){
                                        ReceiveDownloadProcessFormat receive = (ReceiveDownloadProcessFormat) prepareDataForFragment.getFileActionStateData(
                                                OrderConst.fileAction_name,
                                                OrderConst.GETDOWNLOADProcess,
                                                bean.getPath()
                                        );
                                        if (receive.getStatus() == 200 && receive.getData() != null){
                                            final DownloadProcess process = receive.getData();
                                            downloadFileStateFragmentList.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    holder.iv_expand.setImageResource(R.drawable.ic_collapse);
                                                    holder.tv_downloadProgress.setText(process.getPercent());
                                                    holder.info.setVisibility(View.VISIBLE);
                                                    if (bean.getState().equals("下载中")){
                                                        holder.tv_stopTime.setText("下载速度");
                                                        holder.tv_downloadSpeed.setText(process.getDownloadSpeed());
                                                        holder.tv_stopTime.setVisibility(View.GONE);
                                                        holder.tv_downloadSpeed.setVisibility(View.GONE);
                                                    }else {
                                                        holder.tv_stopTime.setText("终止时间");
                                                        holder.tv_downloadSpeed.setText(process.getLastChangeTime());
                                                        holder.tv_stopTime.setVisibility(View.GONE);
                                                        holder.tv_downloadSpeed.setVisibility(View.GONE);
                                                    }
                                                }
                                            });
                                        }
                                    }else {
                                        Toasty.warning(getContext(),"与电脑断开连接").show();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }


                            }
                        }).start();
                    }
                    else if (holder.info.getVisibility() == View.VISIBLE){
                        holder.info.setVisibility(View.GONE);
                        holder.iv_expand.setImageResource(R.drawable.ic_expand);
                    }
                }
            });
            holder.item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    holder.rootView.setLeftSwipe(true);
                    return true;
                }
            });
//            if((int) downloadStateFileData.get(position).get("downloadStateFileState") == DOWNLOADED){
//                holder.downloadStateFileFinish.setVisibility(View.VISIBLE);
//            }

            return convertView;
        }

        class ViewHolderFile {
            SwipeMenuLayout rootView;
            ImageView iv_expand;
            TextView tv_fileName;
            TextView tv_downloadState;
            TextView tv_downloadProgress;
            TextView tv_downloadSpeed;
            TextView tv_stopTime;
            Button tv_stop;
            Button tv_delete;
            LinearLayout item;
            RelativeLayout info;
        }
    }

    private void deleteDialog(final DownloadBean bean) {
        final DeleteDialog deleteDialog = new DeleteDialog(getContext());
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.show();
        deleteDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (MyApplication.isSelectedPCOnline()){
                            prepareDataForFragment.getFileActionStateData(
                                    OrderConst.fileAction_name,
                                    OrderConst.DELETEDOWNLOAD,
                                    JsonUitl.objectToString(bean.getReceiveData())
                            );
                            getData();
                        }else {
                            Toasty.warning(getContext(),"与电脑断开连接").show();

                        }

                    }
                }).start();
            }
        });
        deleteDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });
    }

}
