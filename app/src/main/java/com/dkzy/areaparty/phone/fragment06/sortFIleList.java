package com.dkzy.areaparty.phone.fragment06;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SnowMonkey on 2017/4/5.
 */

public class sortFIleList extends AppCompatActivity{
    private List<HashMap<String, Object>> fileData = null;
    private String user_id = null;
    private fileObj agreeFileMsg;
    private ListView fileListView;
    private mFriendFileAdapter fileAdapater;
    private TextView sortFileListTitle;
    private ImageButton sortFileListBackBtn;
    private int fileStyle;
    private String file_date;
    private String file_name;
    private String file_size;

    public static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab06_sortfilelist);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getData();
        initView();
        initEvent();
    }
    private void getData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        fileData = ((myList) bundle.get("fileData")).getList();
        user_id = bundle.getString("userId");
        fileStyle = bundle.getInt("fileStyle");
    }

    private void initView(){
        fileListView = (ListView) this.findViewById(R.id.fileListView);
        sortFileListTitle = (TextView) this.findViewById(R.id.sortFileListTitle);
        sortFileListBackBtn = (ImageButton) this.findViewById(R.id.sortFileListBackBtn);
        switch (fileStyle){
            case 0:
                sortFileListTitle.setText("音频");
                break;
            case 1:
                sortFileListTitle.setText("视频");
                break;
            case 2:
                sortFileListTitle.setText("压缩包");
                break;
            case 3:
                sortFileListTitle.setText("图片");
                break;
            case 4:
                sortFileListTitle.setText("文档");
                break;
            case 5:
                sortFileListTitle.setText("其他");
                break;
        }
        fileAdapater = new mFriendFileAdapter(this, fileData, true, user_id);
        fileListView.setAdapter(fileAdapater);

    }

    private void initEvent(){
        fileListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                file_name = ((Map<String, String>) sortFIleList.this.fileAdapater.getItem(info.position)).get("fileName");
                file_date = ((Map<String, String>) sortFIleList.this.fileAdapater.getItem(info.position)).get("fileDate");
                file_size = ((Map<String, String>) sortFIleList.this.fileAdapater.getItem(info.position)).get("fileSize");
                menu.setHeaderTitle("选择操作");
                menu.add(0, 0, 0, "下载");
                //menu.add(0, 1, 1, "获取网络资源");
            }
        });

        /*fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showFileInfo(fileData.get(position));
            }
        });*/

        sortFileListBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortFIleList.this.finish();
            }
        });

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        Toast.makeText(sortFIleList.this, "下载请求已发送，待对方同意", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        //info.position得到listview中选择的条目绑定的id
//        file_id = (String) fileList.this.musicData.get(info.position).get("fileId");
//        file_name = ((Map<String, String>) fileList.this.musicAdapter.getItem(info.position)).get("fileName");
        switch (item.getItemId()) {
            case 0:
                try {
                    //new Thread(download).start();//下载事件的方法
                    Toast.makeText(sortFIleList.this, "请求已发送，等待对方同意后在下载管理界面查看",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            case 1:

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void showFileInfo(HashMap<String, Object> h){
        String[] items = new String[3];
        items[0] = "文件名： " + h.get("fileName");
        items[1] = "文件大小： " + h.get("fileSize") + "KB";
        if(!h.get("fileInfo").equals(""))
            items[2] = "文件描述： " + h.get("fileInfo");
        else
            items[2] = "文件描述： 这家伙什么都没写";
        AlertDialog.Builder listDialog = new AlertDialog.Builder(sortFIleList.this);
        listDialog.setTitle("文件信息");
        listDialog.setItems(items, null);
        listDialog.show();
    }
}
