package com.androidlearning.boris.familycentralcontroler.fragment06;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidlearning.boris.familycentralcontroler.FileTypeConst;
import com.androidlearning.boris.familycentralcontroler.Login;
import com.androidlearning.boris.familycentralcontroler.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
        initData();
    }

    private void initViews() {
        downloadStateFragmentRefresh = (LinearLayout) getActivity().findViewById(R.id.downloadStateFragmentRefresh);
        downloadFileStateFragmentList = (ListView) getActivity().findViewById(R.id.downloadFileStateFragmentList);
        downloadStateFileFinish = (TextView) getActivity().findViewById(R.id.downloadStateFileFinish);
        downloadFileStateFragmentFileAdapter = new DownloadStateFragmentFileAdapter(getActivity(), downloadFileStateData);
        downloadFileStateFragmentList.setAdapter(downloadFileStateFragmentFileAdapter);
        new Thread(new getProgress()).start();

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
                    new Thread(new getProgress()).start();
                    timer = new Date().getTime();
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
        private List<HashMap<String, Object>> downloadStateFileData;
        public DownloadStateFragmentFileAdapter(Context context, List<HashMap<String, Object>> downloadStateFileData) {
            mInflater = LayoutInflater.from(context);
            this.downloadStateFileData = downloadStateFileData;
        }
        @Override
        public int getCount() {
            return downloadStateFileData.size();
        }

        @Override
        public Object getItem(int position) {
            return downloadStateFileData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final ViewHolderFile holder = new ViewHolderFile();
            convertView = mInflater.inflate(R.layout.tab06_download_manager_stateitem, null);
            holder.downloadStateFileImg = (ImageView) convertView.findViewById(R.id.downloadStateFileImg);
            holder.downloadStateFileFinish = (TextView) convertView.findViewById(R.id.downloadStateFileFinish);
            holder.downloadStateFileName  = (TextView) convertView.findViewById(R.id.downloadStateFileName);
            holder.downloadStateFileSize = (TextView) convertView.findViewById(R.id.downloadStateFileSize);
            holder.downloadStateFileTotalSize = (TextView) convertView.findViewById(R.id.downloadStateFileTotalSize);
            holder.downloadStateFileProgress = (TextView) convertView.findViewById(R.id.downloadStateFileProgress);

            holder.downloadStateFileName.setText((String) downloadStateFileData.get(position).get("downloadStateFileName"));
            holder.downloadStateFileImg.setImageResource((int) downloadStateFileData.get(position).get("downloadStateFileImg"));
            holder.downloadStateFileSize.setText((String) downloadStateFileData.get(position).get("downloadStateFileSize"));
            holder.downloadStateFileTotalSize.setText((String) downloadStateFileData.get(position).get("downloadStateFileTotalSize"));
            holder.downloadStateFileProgress.setText((String) downloadStateFileData.get(position).get("downloadStateFileProgress"));

//            if((int) downloadStateFileData.get(position).get("downloadStateFileState") == DOWNLOADED){
//                holder.downloadStateFileFinish.setVisibility(View.VISIBLE);
//            }

            return convertView;
        }

        class ViewHolderFile {
            ImageView downloadStateFileImg;
            TextView downloadStateFileName;
            TextView downloadStateFileSize;
            TextView downloadStateFileTotalSize;
            TextView downloadStateFileProgress;
            TextView downloadStateFileFinish;
        }
    }
}
