package com.androidlearning.boris.familycentralcontroler.fragment06;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidlearning.boris.familycentralcontroler.FileTypeConst;
import com.androidlearning.boris.familycentralcontroler.Login;
import com.androidlearning.boris.familycentralcontroler.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import protocol.Msg.DeleteFileMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

/**
 * Created by SnowMonkey on 2017/5/31.
 */

public class DownloadFolderFragment extends Fragment {
    private final int SUCCESS = 0;
    private final int DOWNLOADING = 1;
    private final int PAUSE = 2;
    private final int DOWNLOADAGAIN = 3;

    private ListView downloadFolderFragmentList = null;
    private DownloadFolderFragmentFileAdapter downloadFolderFragmentFileAdapter = null;
    public static List<HashMap<String, Object>> downloadFileData = null;

    public static Handler mHandler;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab06_download_manager_downfolderfragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        initViews();
        initEvents();
    }

    private void initData(){
        downloadFileData = (downloadFileData==null)?new ArrayList<HashMap<String, Object>>():downloadFileData;
    }
    private void getData(){
        initData();
    }
    private void initViews() {
        downloadFolderFragmentList = (ListView) getActivity().findViewById(R.id.downloadFolderFragmentList);
        downloadFolderFragmentFileAdapter = new DownloadFolderFragmentFileAdapter(getActivity(),downloadFileData);
        downloadFolderFragmentList.setAdapter(downloadFolderFragmentFileAdapter);
    }
    private void initEvents(){
//        new Thread(){
//            @Override
//            public void run() {
//                try {
//                    downloadFileData.clear();
//                    ReceivedFileManagerMessageFormat fileManagerMessage = (ReceivedFileManagerMessageFormat)
//                            prepareDataForFragment.getFileActionStateData(OrderConst.folderAction_name,
//                                    OrderConst.folderAction_openInComputer_command, "E:\\");
//                    if(fileManagerMessage.getStatus() == OrderConst.success) {
//                        NodeFormat nodeFormat = fileManagerMessage.getData();
//                        List<FileInforFormat> files = nodeFormat.getFiles();
//                        for(FileInforFormat file : files){
//                            HashMap<String, Object> hs = new HashMap<>();
//                            hs.put("folderFileName", file.getName());
//                            hs.put("folderFileInfo", file.getSize() + "KB" + "  " + file.getLastChangeTime());
//                            downloadFileData.add(hs);
//                        }
//                        if(downloadFolderFragmentList!=null){
//                            downloadFolderFragmentFileAdapter.notifyDataSetChanged();
//                        }
//                    } else {
//                        downloadFileData.clear();
//                    }
//                } catch(Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        String fileName = (String)msg.obj;
                        Toast.makeText(getActivity(), fileName+"删除失败，请重试", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        fileName = (String)msg.obj;
                        Iterator<HashMap<String, Object>> it = downloadFileData.iterator();
                        while(it.hasNext()){
                            HashMap<String, Object> file = it.next();
                            if(file.get("folderFileName").equals(fileName)){
                                it.remove();
                                downloadFolderFragmentFileAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), fileName+"删除成功", Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                        break;
                }
            }
        };
    }
    public void addItem(String fileName, String fileInfo){
        HashMap<String, Object> fileItem = new HashMap<>();
        fileItem.put("folderFileName", fileName);
        fileItem.put("folderFileInfo", fileInfo);
        downloadFileData.add(fileItem);
        if(downloadFolderFragmentList!=null){
            downloadFolderFragmentFileAdapter.notifyDataSetChanged();
        }
    }
    public void agreeDownload(Message msg){
    }
    public void downloadFilePauseReq(Message msg){
        int id = (int) msg.obj;
        downloadFileData.get(id).put("folderFileState",PAUSE);
        downloadFolderFragmentFileAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "好友文件下载暂停", Toast.LENGTH_SHORT).show();
    }
    public void downloadFileStartReq(Message msg){
        int id = (int) msg.obj;
        downloadFileData.get(id).put("folderFileState",DOWNLOADING);
        downloadFolderFragmentFileAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "好友文件下载开始", Toast.LENGTH_SHORT).show();
    }
    public void downloadFileCancelReq(Message msg){
        int id = (int) msg.obj;
        downloadFileData.get(id).put("folderFileState",DOWNLOADAGAIN);
        downloadFolderFragmentFileAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "好友文件下载取消", Toast.LENGTH_SHORT).show();
    }

    private class DownloadFolderFragmentFileAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<HashMap<String, Object>> downloadFileData;
        public DownloadFolderFragmentFileAdapter(Context context, List<HashMap<String, Object>> downloadFileData) {
            mInflater = LayoutInflater.from(context);
            this.downloadFileData = downloadFileData;
        }
        @Override
        public int getCount() {
            return downloadFileData.size();
        }

        @Override
        public Object getItem(int position) {
            return downloadFileData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolderFile holder = new ViewHolderFile();
            convertView = mInflater.inflate(R.layout.tab06_download_manager_downfolderitem, null);
            holder.folderFileImg = (ImageView) convertView.findViewById(R.id.folderFileImg);
            holder.folderFileName  = (TextView) convertView.findViewById(R.id.folderFileName);
            holder.folderFileInfo = (TextView) convertView.findViewById(R.id.folderFileInfo);
            holder.folderFileDownload = (TextView) convertView.findViewById(R.id.folderFileDownload);

            holder.folderFileImg.setImageResource(fileIndexToImgId.toImgId(FileTypeConst.determineFileType((String) downloadFileData.get(position).get("folderFileName"))));
            holder.folderFileInfo.setText((String) downloadFileData.get(position).get("folderFileInfo"));
            holder.folderFileName.setText((String) downloadFileData.get(position).get("folderFileName"));
//            if((int) downloadFileData.get(position).get("folderFileState") == SUCCESS){
//                holder.folderFileDownload.setText("已下载");
//                holder.folderFileDownload.setBackgroundResource(R.drawable.disabledbuttonradius);
//                holder.folderFileDownload.setEnabled(false);
//            }else if((int) downloadFileData.get(position).get("folderFileState") == PAUSE){
//                holder.folderFileDownload.setText("继续");
//                holder.folderFileDownload.setBackgroundResource(R.drawable.buttonradius);
//                holder.folderFileDownload.setEnabled(true);
//            }else if((int) downloadFileData.get(position).get("folderFileState") == DOWNLOADING) {
//                holder.folderFileDownload.setText("暂停");
//                holder.folderFileDownload.setBackgroundResource(R.drawable.buttonradius);
//                holder.folderFileDownload.setEnabled(true);
//            }else if((int) downloadFileData.get(position).get("folderFileState") == DOWNLOADAGAIN) {
//                holder.folderFileDownload.setText("重新下载");
//                holder.folderFileDownload.setBackgroundResource(R.drawable.buttonradius);
//                holder.folderFileDownload.setEnabled(true);
//            }
//
            holder.folderFileDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String state = holder.folderFileDownload.getText().toString();
//                    if(state.equals("继续")){
//                        Message continueMsg = MainActivity.stateHandler.obtainMessage();
//                        continueMsg.obj = position;
//                        continueMsg.what = OrderConst.downloadFileContinue;
//                        MainActivity.stateHandler.sendMessage(continueMsg);
//                        downloadFileData.get(position).put("folderFileState",DOWNLOADING);
//                        downloadFolderFragmentFileAdapter.notifyDataSetChanged();
//                    }else if(state.equals("暂停")){
//                        Message pauseMsg = MainActivity.stateHandler.obtainMessage();
//                        pauseMsg.obj = position;
//                        pauseMsg.what = OrderConst.downloadFilePause;
//                        MainActivity.stateHandler.sendMessage(pauseMsg);
//                        downloadFileData.get(position).put("folderFileState",PAUSE);
//                        downloadFolderFragmentFileAdapter.notifyDataSetChanged();
//                    }else if (state.equals("重新下载")){
//                        HashMap<String, Object> fileItem = new HashMap<>();
//                        fileItem.put("downloadStateFileId", position);
//                        fileItem.put("downloadStateFileImg", fileIndexToImgId.toImgId(FileTypeConst.determineFileType((String) downloadFileData.get(position).get("folderFileName"))));
//                        fileItem.put("downloadStateFileState", DownloadStateFragment.DOWNLOADING);
//                        fileItem.put("downloadStateFileName", downloadFileData.get(position).get("folderFileName"));
//                        fileItem.put("downloadStateFileSize", downloadFileData.get(position).get("folderFileSize"));
//                        fileItem.put("downloadStateFileProgress", "0.1%");
//                        fileItem.put("downloadStateFileStyle", DownloadStateFragment.DOWNFILE);
//                        Message startMsg = MainActivity.stateHandler.obtainMessage();
//                        startMsg.obj = fileItem;
//                        startMsg.what = OrderConst.downloadFileStart;
//                        MainActivity.stateHandler.sendMessage(startMsg);
//                        downloadFileData.get(position).put("folderFileState",DOWNLOADING);
//                        downloadFolderFragmentFileAdapter.notifyDataSetChanged();
//                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DeleteFileMsg.DeleteFileReq.Builder builder = DeleteFileMsg.DeleteFileReq.newBuilder();
                            builder.setFileName((String) downloadFileData.get(position).get("folderFileName"));
                            try {
                                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.DELETE_FILE_REQ_VALUE, builder.build().toByteArray());
                                Login.base.writeToServer(Login.outputStream,byteArray);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            });
            return convertView;
        }

        class ViewHolderFile {
            ImageView folderFileImg;
            TextView folderFileName;
            TextView folderFileInfo;
            TextView folderFileDownload;
        }
    }
}
