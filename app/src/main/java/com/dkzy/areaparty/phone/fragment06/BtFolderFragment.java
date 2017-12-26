package com.dkzy.areaparty.phone.fragment06;

import android.content.Context;
import android.os.Bundle;
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

import com.dkzy.areaparty.phone.MainActivity;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SnowMonkey on 2017/5/31.
 */

public class BtFolderFragment extends Fragment {
    private final int SUCCESS = 1;//已下载
    private final int DOWNLOAD = 2;//未下载
    private final int PAUSE = 3;//暂停
    private final int DOWNLOADING = 4;//正在下载
    private final int DOWNLOADAGAIN = 5;//重新下载
    private ListView btFolderFragmentList;
    private List<HashMap<String, Object>> btFileData = null;
    private BtFolderFragmentFileAdapter btFolderFragmentFileAdapter = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab06_download_manager_btfolderfragment, container, false);
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
        btFileData = (btFileData==null)?new ArrayList<HashMap<String, Object>>():btFileData;
    }
    private void getData(){
        //从服务器获取文件和状态
        initData();
        if(btFileData.size()==0) {
            addItem("大兵小将.torrent", "电影-大兵小将", PAUSE, "374M/1358M");
            addItem("僵尸叔叔.torrent", "电影-僵尸叔叔", DOWNLOADING, "1136M/1248M");
            addItem("厉鬼将映.torrent", "电影-厉鬼将映", SUCCESS, "1400M/1400M");
            addItem("美人鱼.torrent", "电影-美人鱼", DOWNLOAD, "1M/1389M");
        }
    }
    private void initViews() {
        btFolderFragmentList = (ListView) getActivity().findViewById(R.id.btFolderFragmentList);
        btFolderFragmentFileAdapter = new BtFolderFragmentFileAdapter(getActivity(),btFileData);
        btFolderFragmentList.setAdapter(btFolderFragmentFileAdapter);
    }
    private void initEvents(){
    }
    public void torrentFileStartReq(Message msg){
        int id = (int) msg.obj;
        btFileData.get(id).put("folderFileState",DOWNLOADING);
        btFolderFragmentFileAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "种子文件下载开始", Toast.LENGTH_SHORT).show();
    }
    public void torrentFilePauseReq(Message msg){
        int id = (int) msg.obj;
        btFileData.get(id).put("folderFileState",PAUSE);
        btFolderFragmentFileAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "种子文件下载暂停", Toast.LENGTH_SHORT).show();
    }
    public void torrentFileCancelReq(Message msg){
        int id = (int) msg.obj;
        btFileData.get(id).put("folderFileState",DOWNLOADAGAIN);
        btFolderFragmentFileAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "种子文件下载取消", Toast.LENGTH_SHORT).show();
    }
    private void addItem(String fileName, String fileInfo, int fileState, String fileSize){
        HashMap<String, Object> fileItem = new HashMap<>();
        fileItem.put("folderFileInfo", fileInfo);
        fileItem.put("folderFileName", fileName);
        fileItem.put("folderFileState", fileState);
        fileItem.put("folderFileSize", fileSize);
        btFileData.add(fileItem);
    }

    private class BtFolderFragmentFileAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<HashMap<String, Object>> btFileData;
        public BtFolderFragmentFileAdapter(Context context, List<HashMap<String, Object>> btFileData) {
            mInflater = LayoutInflater.from(context);
            this.btFileData = btFileData;
        }
        @Override
        public int getCount() {
            return btFileData.size();
        }

        @Override
        public Object getItem(int position) {
            return btFileData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolderFile holder = new ViewHolderFile();
            convertView = mInflater.inflate(R.layout.tab06_download_manager_btfolderitem, null);
            holder.folderFileImg = (ImageView) convertView.findViewById(R.id.folderFileImg);
            holder.folderFileName  = (TextView) convertView.findViewById(R.id.folderFileName);
            holder.folderFileInfo = (TextView) convertView.findViewById(R.id.folderFileInfo);
            holder.folderFileDownload = (TextView) convertView.findViewById(R.id.folderFileDownload);

            //holder.folderFileImg.setBackgroundResource(R.drawable.torrent);
            holder.folderFileInfo.setText((String) btFileData.get(position).get("folderFileInfo"));
            holder.folderFileName.setText((String) btFileData.get(position).get("folderFileName"));
            if((int) btFileData.get(position).get("folderFileState") == SUCCESS){
                holder.folderFileDownload.setText("已下载");
                holder.folderFileDownload.setBackgroundResource(R.drawable.disabledbuttonradius);
                holder.folderFileDownload.setEnabled(false);
            }else if((int) btFileData.get(position).get("folderFileState") == DOWNLOAD){
                holder.folderFileDownload.setText("下载");
                holder.folderFileDownload.setBackgroundResource(R.drawable.buttonradius);
                holder.folderFileDownload.setEnabled(true);
            }else if((int) btFileData.get(position).get("folderFileState") == PAUSE){
                holder.folderFileDownload.setText("继续");
                holder.folderFileDownload.setBackgroundResource(R.drawable.buttonradius);
                holder.folderFileDownload.setEnabled(true);
            }else if((int) btFileData.get(position).get("folderFileState") == DOWNLOADING) {
                holder.folderFileDownload.setText("暂停");
                holder.folderFileDownload.setBackgroundResource(R.drawable.buttonradius);
                holder.folderFileDownload.setEnabled(true);
            }else if((int) btFileData.get(position).get("folderFileState") == DOWNLOADAGAIN) {
                holder.folderFileDownload.setText("重新下载");
                holder.folderFileDownload.setBackgroundResource(R.drawable.buttonradius);
                holder.folderFileDownload.setEnabled(true);
            }

            holder.folderFileDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String state = holder.folderFileDownload.getText().toString();
                    if(state.equals("继续")){
                        Message continueMsg = MainActivity.stateHandler.obtainMessage();
                        continueMsg.obj = position;
                        continueMsg.what = OrderConst.torrentFileContinue;
                        MainActivity.stateHandler.sendMessage(continueMsg);
                        btFileData.get(position).put("folderFileState",DOWNLOADING);
                        btFolderFragmentFileAdapter.notifyDataSetChanged();
                    }else if(state.equals("下载") || state.equals("重新下载")){
                        HashMap<String, Object> fileItem = new HashMap<>();
                        fileItem.put("downloadStateFileId", position);
                        fileItem.put("downloadStateFileImg", R.drawable.torrent);
                        fileItem.put("downloadStateFileState", DownloadStateFragment.DOWNLOADING);
                        fileItem.put("downloadStateFileName", btFileData.get(position).get("folderFileName"));
                        fileItem.put("downloadStateFileSize", btFileData.get(position).get("folderFileSize"));
                        fileItem.put("downloadStateFileProgress", "0.1%");
                        fileItem.put("downloadStateFileStyle", DownloadStateFragment.TORRENT);
                        Message startMsg = MainActivity.stateHandler.obtainMessage();
                        startMsg.obj = fileItem;
                        startMsg.what = OrderConst.torrentFileStart;
                        MainActivity.stateHandler.sendMessage(startMsg);
                        btFileData.get(position).put("folderFileState",DOWNLOADING);
                        btFolderFragmentFileAdapter.notifyDataSetChanged();
                    }else if(state.equals("暂停")){
                        Message continueMsg = MainActivity.stateHandler.obtainMessage();
                        continueMsg.obj = position;
                        continueMsg.what = OrderConst.torrentFilePause;
                        MainActivity.stateHandler.sendMessage(continueMsg);
                        btFileData.get(position).put("folderFileState",PAUSE);
                        btFolderFragmentFileAdapter.notifyDataSetChanged();
                    }
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
