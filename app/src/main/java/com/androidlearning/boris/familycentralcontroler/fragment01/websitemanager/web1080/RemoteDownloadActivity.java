package com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.web1080;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.androidlearning.boris.familycentralcontroler.R;
import com.androidlearning.boris.familycentralcontroler.fragment01.utorrent.*;
import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.web1080.adapter.TorrentFile;
import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.web1080.adapter.TorrentFileAdapter;
import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.web1080.callback.OnTorrentFileItemListener;
import com.androidlearning.boris.familycentralcontroler.myapplication.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class RemoteDownloadActivity extends AppCompatActivity {

    private TorrentFileAdapter adapter;
    private List<TorrentFile> torrentList = new ArrayList<>();

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;

    private SimpleAdapter ptAdapter;
    private ArrayList<Map<String, String>> ptListData;
    private ListView ptListView;
    private Handler mHandler;

    private Button remoteControl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web1080_activity_remote_download);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.webmanager_ic_goback);
        }

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                torrentList.clear();
                getName();
                swipeRefresh.setRefreshing(false);
            }
        });
//种子文件
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_torrent_file);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new TorrentFileAdapter(torrentList);
        recyclerView.setAdapter(adapter);
        getName();
        setListener();

        //设置pt文件夹的adapter
        ptAdapter=new SimpleAdapter(RemoteDownloadActivity.this,ptListData,R.layout.web1080_pt_torrent_file__item,new String[]{"pt"},new int[]{R.id.torrent_file_name});
        ptListView.setAdapter(ptAdapter);

        ptListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(RemoteDownloadActivity.this,PtTorrentManagement.class);
                startActivity(intent);
            }
        });




    }

    private void initView() {
        ptListView=(ListView) findViewById(R.id.ptTorrentListView);
        ptListData=new ArrayList<>();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        Toasty.warning(getApplicationContext(), "文件已存在，请勿重新发送", Toast.LENGTH_SHORT, true).show();
                        break;
                }
            }
        };
        remoteControl = (Button) findViewById(R.id.download_remote_control);
        remoteControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RemoteDownloadActivity.this, com.androidlearning.boris.familycentralcontroler.fragment01.utorrent.MainActivity.class));
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up webmanager_button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getName(){
        String directory1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/areaparty/webmanager_download";
        File file = new File(directory1);
        if(!file.exists()){
            file.mkdirs();
        }
        if(file != null){
            File[] files = file.listFiles(new FileNameSelector("torrent"));
            for(File f : files){
                torrentList.add(new TorrentFile(f.getName(), f.getAbsolutePath()));
//                String torrentFileName = f.getName();
////                torrentList.add(new DownloadTorrent(torrentFileName, torrentFileName, f.getAbsolutePath()));
//                String torrentTitle = getApplicationContext().getSharedPreferences("1080net_downloaded", Context.MODE_PRIVATE).getString(torrentFileName, null);
//                if (torrentTitle != null){
//                    torrentList.add(new TorrentFile(torrentTitle, torrentFileName, f.getAbsolutePath()));
//                }
            }

            File[] files1 = file.listFiles();
            for(File f : files1){
                if(f.getName().contains("pt种子")){
                    Map<String,String> map=new HashMap<String, String>();
                    map.put("pt","pt种子");
                    ptListData.add(map);
                }

            }
        }
        adapter.notifyDataSetChanged();
    }


    public class FileNameSelector implements FilenameFilter {
        String extension = ".";
        public FileNameSelector(String fileExtensionNoDot) {
            extension += fileExtensionNoDot;
        }

        public boolean accept(File dir, String name) {
            return name.endsWith(extension);
        }
    }

    private void setListener() {
        adapter.setOnTorrentFileListent(new OnTorrentFileItemListener() {
            @Override
            public void delete(int position) {
                final TorrentFile torrent = torrentList.get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(RemoteDownloadActivity.this);
                dialog.setTitle("删除种子文件:");
                dialog.setMessage(torrent.getTorrentPath());
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File f = new File(torrent.getTorrentPath());
                        if (f.exists()){
                            f.delete();
                        }
                        adapter.remove(torrent);
                        adapter.notifyDataSetChanged();



                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();


            }

            @Override
            public void download(int position) {
                final TorrentFile torrent = torrentList.get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(RemoteDownloadActivity.this);
                dialog.setTitle("传送");
                dialog.setMessage("将把种子文件发送到电脑");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        torrentList2.add(new DownloadStatus(torrent.getTorrentFileName(), 0, true));
//                        adapter2.notifyDataSetChanged();
                        final String path = torrent.getTorrentPath();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    new FileClient(path);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
            }
        });
    }
    class FileClient  //客户端
    {
        FileClient(final String fileStr) throws Exception
        {
            System.out.println("客户端启动....");
            File file = new File(fileStr);  //关联一个文件
            if(file.isFile())  //是一个标准文件吗?
            {
                if (MyApplication.isSelectedPCOnline()){
                    client(file);    //启动连接
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.warning(getApplicationContext(), "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                        }
                    });

                }

            }
            else
            {
                System.out.println("要发送的文件 "+fileStr+" 不是一个标准文件,请正确指定");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toasty.warning(getApplicationContext(), "要发送的文件 "+fileStr+" 不是一个标准文件,请正确指定", Toast.LENGTH_SHORT, true).show();
                    }
                });

            }
        }

        public void client(File file)throws Exception
        {
            String ip = MyApplication.getSelectedPCIP().ip;
            Socket sock= new Socket(ip,10003); //指定服务端地址和端口

            FileInputStream fis = new FileInputStream(file); //读取本地文件
            OutputStream sockOut = sock.getOutputStream();   //定义socket输出流

            //先发送文件名.让服务端知道
            String fileName = file.getName();
            System.out.println("待发送文件:"+fileName);
            sockOut.write(fileName.getBytes("GBK"));

            String serverInfo= servInfoBack(sock); //反馈的信息:服务端是否获取文件名并创建文件成功
            if(serverInfo.equals("FileSendNow"))   //服务端说已经准备接收文件,发吧
            {
                byte[] bufFile= new byte[1024];
                int len=0;
                while(true)
                {
                    len=fis.read(bufFile);
                    if(len!=-1)
                    {
                        sockOut.write(bufFile,0,len); //将从硬盘上读取的字节数据写入socket输出流
                    }
                    else
                    {
                        break;
                    }
                }
            }else if(serverInfo.contains("file exist"))
            {
                mHandler.sendEmptyMessage(0);
                return;
            }
            else
            {
                System.out.println("服务端返回信息:"+serverInfo);
            }
            sock.shutdownOutput();   //必须的,要告诉服务端该文件的数据已写完
            System.out.println("服务端最后一个返回信息:"+servInfoBack(sock));//显示服务端最后返回的信息

            fis.close();
            sock.close();
        }

        public String servInfoBack(Socket sock) throws Exception  //读取服务端的反馈信息
        {
            InputStream sockIn = sock.getInputStream(); //定义socket输入流
            byte[] bufIn =new byte[1024];
            int lenIn=sockIn.read(bufIn);            //将服务端返回的信息写入bufIn字节缓冲区
            String info=new String(bufIn,0,lenIn);
            return info;
        }
    }
}
