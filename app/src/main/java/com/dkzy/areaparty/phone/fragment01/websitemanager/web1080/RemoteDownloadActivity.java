package com.dkzy.areaparty.phone.fragment01.websitemanager.web1080;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.*;
import com.dkzy.areaparty.phone.fragment01.diskContentActivity;
import com.dkzy.areaparty.phone.fragment01.model.fileBean;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_reName;
import com.dkzy.areaparty.phone.fragment01.ui.DeleteDialog;
import com.dkzy.areaparty.phone.fragment01.utils.PCFileHelper;
import com.dkzy.areaparty.phone.fragment01.utorrent.*;
import com.dkzy.areaparty.phone.fragment01.utorrent.customView.MyItemDecoration;
import com.dkzy.areaparty.phone.fragment01.utorrent.utils.OkHttpUtils;
import com.dkzy.areaparty.phone.fragment01.utorrent.utils.UrlUtils;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.adapter.TorrentFile;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.adapter.TorrentFileAdapter;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.callback.OnTorrentFileItemListener;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.Send2PCThread;
import com.dkzy.areaparty.phone.utilseverywhere.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RemoteDownloadActivity extends AppCompatActivity {
    public static String TAG = "RemoteDownloadActivity";
    public static String rootPath;
    public static String btFilesPath ;
    public static String downloadPath;
    public static String targetPath;//uTorrent自动从此路径加载种子
    public static boolean isCreated = false;

    private TorrentFileAdapter adapter;
    private TorrentFileAdapter adapter_pc;
    private List<TorrentFile> torrentList = new ArrayList<>();
    private List<TorrentFile> torrentList_pc = new ArrayList<>();

    private RecyclerView recyclerView, recyclerView_pcFile;
    private SwipeRefreshLayout swipeRefresh;
    private Toolbar toolbar;

    private SimpleAdapter ptAdapter;
    private ArrayList<Map<String, String>> ptListData;
    private ListView ptListView;

    private Button remoteControl;
    private TextView pc_file, app_file;
    private LinearLayout menuList, addToDownload, sendToPc, torrentDelete;
    private LinearLayout menuList_pc, addToDownload_pc,torrentDelete_pc;
    private int count_success, count_exist;

    private PCFileHelper pcFileHelper;
    private final MyHandler myHanlder = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<RemoteDownloadActivity> mAcitivity;

        MyHandler(RemoteDownloadActivity activity) {
            mAcitivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            RemoteDownloadActivity activity = mAcitivity.get();
            if(activity != null) {
                switch (msg.what) {
                    case OrderConst.openFolder_order_successful:
                        Log.w("RemoteDownloadActivity","openFolder_order_successful");
                            activity.openPCFileSuccess();
                        break;
                        case OrderConst.actionSuccess_order:
                            Log.w("RemoteDownloadActivity","actionSuccess_order");
                            activity.actionSuccess(msg);
                            break;
                        case OrderConst.actionFail_order:
                            Log.w("RemoteDownloadActivity","actionFail_order");
                            activity.actionFail(msg);
                            break;
                    default:
                        break;
                }
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web1080_activity_remote_download);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("种子管理");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.webmanager_ic_goback);
        }


        initData();

        initView();
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (recyclerView.getVisibility() == View.VISIBLE){
                    refresh();
                }else if (recyclerView_pcFile.getVisibility() == View.VISIBLE){
                    refresh_pc();
                }
                swipeRefresh.setRefreshing(false);

            }
        });
//种子文件
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_torrent_file);
        recyclerView_pcFile = (RecyclerView) findViewById(R.id.recycler_view_torrent_pcFile);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager); recyclerView_pcFile.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new MyItemDecoration()); recyclerView_pcFile.addItemDecoration(new MyItemDecoration());

        adapter = new TorrentFileAdapter(torrentList,true);
        adapter_pc = new TorrentFileAdapter(torrentList_pc,false);

        recyclerView.setAdapter(adapter);
        recyclerView_pcFile.setAdapter(adapter_pc);
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

        initEvent();


        //getSettings();

    }

    @Override
    protected void onStop() {
        super.onStop();
        isCreated = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isCreated = true;
        MyApplication.getPcAreaPartyPath();
        initUTorrent();

    }

    private void initEvent() {
        app_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app_file.setTextColor(Color.parseColor("#FF5050"));
                app_file.setBackgroundResource(R.drawable.barback03_left_pressed);
                pc_file.setTextColor(Color.parseColor("#707070"));
                pc_file.setBackgroundResource(R.drawable.barback03_right_normal);
                recyclerView_pcFile.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                menuList_pc.setVisibility(View.GONE);
                if (isShow()){
                    remoteControl.setVisibility(View.GONE);
                    menuList.setVisibility(View.VISIBLE);
                    swipeRefresh.setEnabled(false);
                }else {
                    menuList.setVisibility(View.GONE);
                    remoteControl.setVisibility(View.VISIBLE);
                    swipeRefresh.setEnabled(true);
                }
            }
        });
        pc_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.isSelectedPCOnline()){
                    pc_file.setTextColor(Color.parseColor("#FF5050"));
                    pc_file.setBackgroundResource(R.drawable.barback03_right_pressed);
                    app_file.setTextColor(Color.parseColor("#707070"));
                    app_file.setBackgroundResource(R.drawable.barback03_left_normal);
                    recyclerView.setVisibility(View.GONE);
                    recyclerView_pcFile.setVisibility(View.VISIBLE);
                    menuList.setVisibility(View.GONE);
                    if (isShow_pc()){
                        remoteControl.setVisibility(View.GONE);
                        menuList_pc.setVisibility(View.VISIBLE);
                        swipeRefresh.setEnabled(false);
                    }else {
                        menuList_pc.setVisibility(View.GONE);
                        remoteControl.setVisibility(View.VISIBLE);
                        swipeRefresh.setEnabled(true);
                    }
                }else {
                    Toasty.warning(getApplicationContext(), "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
        addToDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<TorrentFile> selectList = getSelectedTorrent();
                if (selectList != null && selectList.size() > 0){
                    final DeleteDialog deleteDialog = new DeleteDialog(RemoteDownloadActivity.this);
                    deleteDialog.setCanceledOnTouchOutside(false);
                    deleteDialog.show();
                    deleteDialog.setTitleText("确定把"+getSelectedTorrent().size()+"个种子文件加入下载队列？");
                    deleteDialog.setPositiveButtonText("确定");
                    deleteDialog.setOnPositiveListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!TextUtils.isEmpty(UrlUtils.ip_port)&&!TextUtils.isEmpty(UrlUtils.token)&&!TextUtils.isEmpty(OkHttpUtils.authorization)){
                                final String url = "http://"+UrlUtils.ip_port+"/gui/?token="+ UrlUtils.token+"&action=add-file&download_dir=0&path=";
                                final List<String> paths = getSelectedPaths();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        OkHttpUtils.getInstance().setUrl(url).upFile(paths);
                                        try {
                                            Thread.sleep(1500);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toasty.info(getApplicationContext(),"成功添加"+OkHttpUtils.account+"个任务到下载列表").show();
                                                }
                                            });
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                refresh();
                            }
                            deleteDialog.dismiss();
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
        });

        sendToPc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSelectedTorrent().size()>0){
                    final DeleteDialog deleteDialog = new DeleteDialog(RemoteDownloadActivity.this);
                    deleteDialog.setCanceledOnTouchOutside(false);
                    deleteDialog.show();
                    deleteDialog.setTitleText("确定发送"+getSelectedTorrent().size()+"个种子文件到电脑？");
                    deleteDialog.setPositiveButtonText("确定");
                    deleteDialog.setOnPositiveListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            count_success = 0;
                            count_exist = 0;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    for (TorrentFile f : getSelectedTorrent()){
                                        if (f.isShow()&&f.isChecked()){
                                            try {
                                                new FileClient(f.getTorrentPath());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    try {
                                        Thread.sleep(1500);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (count_success != 0 && count_exist!=0){
                                                    Toasty.info(getApplicationContext(),""+count_success+"个种子文件成功发送到电脑\n"+count_exist+"个种子文件已存在").show();
                                                }else if (count_success != 0 && count_exist==0){
                                                    Toasty.info(getApplicationContext(),""+count_success+"个种子文件成功发送到电脑").show();
                                                }else if (count_exist != 0 && count_success == 0){
                                                    Toasty.info(getApplicationContext(),"种子已存在，请勿重复发送").show();
                                                }

                                            }
                                        });
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            deleteDialog.dismiss();
                            refresh();
                        }
                    });
                    deleteDialog.setOnNegativeListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteDialog.dismiss();
                        }
                    });

//                    AlertDialog.Builder dialog = new AlertDialog.Builder(RemoteDownloadActivity.this);
//                    dialog.setTitle("传送");
//                    dialog.setMessage("确定把"+getSelectedTorrent().size()+"个种子文件发送到电脑");
//                    dialog.setCancelable(true);
//                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                        }
//                    });
//                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                        }
//                    });
//                    dialog.show();
                }
            }
        });

        torrentDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSelectedTorrent().size()>0){
                    final DeleteDialog deleteDialog = new DeleteDialog(RemoteDownloadActivity.this);
                    deleteDialog.setCanceledOnTouchOutside(false);
                    deleteDialog.show();
                    deleteDialog.setTitleText("确定删除已选择的"+getSelectedTorrent().size()+"个种子文件？");
                    deleteDialog.setOnPositiveListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (TorrentFile f : torrentList){
                                if (f.isShow()&&f.isChecked()){
                                    File file = new File(f.getTorrentPath());
                                    if (file.exists()){
                                        file.delete();
                                    }
                                }
                            }
                            deleteDialog.dismiss();
                            refresh();
                        }
                    });
                    deleteDialog.setOnNegativeListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteDialog.dismiss();
                        }
                    });

//                    AlertDialog.Builder dialog = new AlertDialog.Builder(RemoteDownloadActivity.this);
//                    dialog.setTitle("删除种子文件:");
//                    dialog.setMessage("确定删除已选择的"+getSelectedTorrent().size()+"个种子文件？");
//                    dialog.setCancelable(true);
//                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                        }
//                    });
//                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                        }
//                    });
//                    dialog.show();

                }
            }
        });
        torrentDelete_pc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSelectedTorrent_pc().size()>0){
                    final DeleteDialog deleteDialog = new DeleteDialog(RemoteDownloadActivity.this);
                    deleteDialog.setCanceledOnTouchOutside(false);
                    deleteDialog.show();
                    deleteDialog.setTitleText("确定删除已选择的"+getSelectedTorrent_pc().size()+"个种子文件？");
                    deleteDialog.setOnPositiveListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pcFileHelper.deleteFile(getSelectedPaths_pc());
                            deleteDialog.dismiss();
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
        });
        addToDownload_pc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSelectedTorrent_pc().size()>0){
                    final DeleteDialog deleteDialog = new DeleteDialog(RemoteDownloadActivity.this);
                    deleteDialog.setCanceledOnTouchOutside(false);
                    deleteDialog.show();
                    deleteDialog.setTitleText("确定把"+getSelectedTorrent_pc().size()+"个种子文件加入下载队列？");
                    deleteDialog.setPositiveButtonText("确定");
                    deleteDialog.setOnPositiveListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            pcFileHelper.copyFile(getSelectedPaths_pc(),targetPath);

                            deleteDialog.dismiss();
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
        });

    }


    private void initData() {
        PCFileHelper.setNowFilePath(btFilesPath);
        pcFileHelper = new PCFileHelper(myHanlder);
        pcFileHelper.loadFiles();
    }

    private void initView() {
        ptListView=(ListView) findViewById(R.id.ptTorrentListView);
        pc_file = (TextView) findViewById(R.id.pc_file);
        app_file = (TextView) findViewById(R.id.app_file);
        menuList = (LinearLayout) findViewById(R.id.menu_list);
        addToDownload = (LinearLayout) findViewById(R.id.addToDownload);
        sendToPc = (LinearLayout) findViewById(R.id.sendToPc);
        torrentDelete = (LinearLayout) findViewById(R.id.torrentDelete);
        menuList_pc = (LinearLayout) findViewById(R.id.menu_list_pc);
        addToDownload_pc = (LinearLayout) findViewById(R.id.addToDownload_pc);
        torrentDelete_pc = (LinearLayout) findViewById(R.id.torrentDelete_pc);

        ptListData=new ArrayList<>();
        remoteControl = (Button) findViewById(R.id.download_remote_control);
        remoteControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.isSelectedPCOnline()){
                    if (!TextUtils.isEmpty(UrlUtils.token)){
                        startActivity(new Intent(RemoteDownloadActivity.this, UTorrentActivity.class));
                    }else {
                        initUTorrent();
                    }

                }else {
                    Toasty.warning(getApplicationContext(), "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                }
//                startActivity(new Intent(RemoteDownloadActivity.this, com.androidlearning.boris.familycentralcontroler.fragment01.utorrent.SubTitleUtil.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gohome, menu);
        return true;
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
            case R.id.go_home:
                startActivity(new Intent(RemoteDownloadActivity.this, com.dkzy.areaparty.phone.MainActivity.class));
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getName(){
        torrentList.clear();
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
                dialog.setTitle("下载");
                dialog.setMessage("把种子文件添加到电脑下载任务");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final String path = torrent.getTorrentPath();
                        if (!TextUtils.isEmpty(UrlUtils.ip_port)&&!TextUtils.isEmpty(UrlUtils.token)&&!TextUtils.isEmpty(OkHttpUtils.authorization)){
                            final String url = "http://"+UrlUtils.ip_port+"/gui/?token="+ UrlUtils.token+"&action=add-file&download_dir=0&path=";
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    OkHttpUtils.getInstance().setUrl(url).upFile(Arrays.asList(path));
                                    try {
                                        Thread.sleep(1500);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toasty.info(getApplicationContext(),"成功添加"+OkHttpUtils.account+"个任务到下载列表").show();
                                            }
                                        });
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
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
            public void reName(int position) {
                reNameDialog(torrentList.get(position));
            }

            @Override
            public void selectModel() {
                remoteControl.setVisibility(View.GONE);
                menuList.setVisibility(View.VISIBLE);
                swipeRefresh.setEnabled(false);
            }

        });
        adapter_pc.setOnTorrentFileListent(new OnTorrentFileItemListener() {
            @Override
            public void delete(int position) {
                final TorrentFile torrent = torrentList_pc.get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(RemoteDownloadActivity.this);
                dialog.setTitle("删除种子文件:");
                dialog.setMessage(torrent.getTorrentPath());
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pcFileHelper.deleteFile(Arrays.asList(torrent.getTorrentPath()));
                        adapter_pc.remove(torrent);
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
                final TorrentFile torrent = torrentList_pc.get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(RemoteDownloadActivity.this);
                dialog.setTitle("下载");
                dialog.setMessage("把种子文件添加到电脑下载任务");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pcFileHelper.copyFile(Arrays.asList(torrent.getTorrentPath()),targetPath);
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
            public void reName(int position) {

            }

            @Override
            public void selectModel() {
                remoteControl.setVisibility(View.GONE);
                menuList_pc.setVisibility(View.VISIBLE);
                swipeRefresh.setEnabled(false);
            }
        });
    }
    private void reNameDialog(final TorrentFile fileBean) {
        final ActionDialog_reName reNameDialog = new ActionDialog_reName(this);
        final String name = fileBean.getTorrentFileName();

        reNameDialog.setCanceledOnTouchOutside(false);
        reNameDialog.show();

        reNameDialog.setOldName(name);

        final EditText editText = reNameDialog.getValueEditTextView();
        reNameDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String des = editText.getText().toString();
                if(des.equals("") || des.endsWith(".") ||
                        des.contains("\\") || des.contains("/") ||
                        des.contains(":")  || des.contains("*") ||
                        des.contains("?")  || des.contains("\"") ||
                        des.contains("<")  || des.contains(">") ||
                        des.contains("|")){
                    Toasty.error(RemoteDownloadActivity.this, "文件夹名不能为空，不能包含\\ / : * ? \" < > |字符", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                } else {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(reNameDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    if (!des.endsWith(".torrent")) des = des+".torrent";
                    File file = new File(fileBean.getTorrentPath());
                    if (!file.exists()){
                        Toasty.error(RemoteDownloadActivity.this, "文件夹名不存在", Toast.LENGTH_SHORT).show();
                    }else {
                        if (FileUtils.rename(file,des)){
                            getName();
                        }
                    }
                }

                reNameDialog.dismiss();
            }
        });
        reNameDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reNameDialog.dismiss();
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
            sockOut.write(fileName.getBytes("utf-8"));

            String serverInfo= servInfoBack(sock); //反馈的信息:服务端是否获取文件名并创建文件成功
            if(serverInfo.equals("FileSendNow"))   //服务端说已经准备接收文件,发吧
            {
                count_success++;
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
                count_exist++;
                //mHandler.sendEmptyMessage(0);
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

    public void openPCFileSuccess(){
        torrentList_pc.clear();
        for (fileBean fileBean : PCFileHelper.getDatas()){
            if (fileBean.name.endsWith(".torrent"))
            torrentList_pc.add(new TorrentFile(fileBean.name, btFilesPath+fileBean.name));
        }
        adapter_pc.notifyDataSetChanged();
    }
    public void actionSuccess(Message  msg){
        String actionType = msg.getData().getString("actionType");
        if (actionType.equals(OrderConst.fileOrFolderAction_deleteInComputer_command)){
            Toasty.success(this, "删除文件成功", Toast.LENGTH_SHORT, true).show();
            refresh_pc();
        }else if (actionType.equals(OrderConst.fileOrFolderAction_copy_command)){
            Toasty.success(this, "成功添加到下载列表", Toast.LENGTH_SHORT, true).show();
            refresh_pc();
        }
    }
    public void actionFail(Message  msg){
        String actionType = msg.getData().getString("actionType");
        if (actionType.equals(OrderConst.fileOrFolderAction_deleteInComputer_command)){
            Toasty.error(this, "错误信息" + msg.getData().getString("error"), Toast.LENGTH_SHORT, true).show();
            refresh_pc();
        }else if (actionType.equals(OrderConst.fileOrFolderAction_copy_command)){
            Toasty.error(this, "错误信息" + msg.getData().getString("error"), Toast.LENGTH_SHORT, true).show();
            refresh_pc();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (remoteControl.getVisibility() == View.GONE){
            switch(keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    menuList.setVisibility(View.GONE);
                    menuList_pc.setVisibility(View.GONE);
                    remoteControl.setVisibility(View.VISIBLE);
                    swipeRefresh.setEnabled(true);
                    for (TorrentFile f : torrentList){
                        f.setShow(false);
                        f.setChecked(false);
                    }
                    adapter.notifyDataSetChanged();
                    for (TorrentFile f : torrentList_pc){
                        f.setShow(false);
                        f.setChecked(false);
                    }
                    adapter_pc.notifyDataSetChanged();
                    return true;
               default:
                   return super.onKeyUp(keyCode,event);
            }
        }else {
            return super.onKeyUp(keyCode,event);
        }
    }
    private List<TorrentFile> getSelectedTorrent(){
        List<TorrentFile> selectList = new ArrayList<>();
        for (TorrentFile f : torrentList){
            if (f.isShow()&&f.isChecked()){
                selectList.add(f);
            }
        }
        if (selectList.size() == 0){
            Toasty.info(getApplicationContext(),"选择个数为0").show();
        }
        return selectList;
    }
    private boolean isShow(){
        if (torrentList.size()>0){
            return torrentList.get(0).isShow();
        }
        return false;
    }
    private List<String> getSelectedPaths(){
        List<String> paths = new ArrayList<>();
        for (TorrentFile f : torrentList){
            if (f.isShow()&&f.isChecked()){
                paths.add(f.getTorrentPath());
            }
        }
        return paths;
    }
    private List<TorrentFile> getSelectedTorrent_pc(){
        List<TorrentFile> selectList = new ArrayList<>();
        for (TorrentFile f : torrentList_pc){
            if (f.isShow()&&f.isChecked()){
                selectList.add(f);
            }
        }
        if (selectList.size() == 0){
            Toasty.info(getApplicationContext(),"选择个数为0").show();
        }
        return selectList;
    }
    private boolean isShow_pc(){
        if (torrentList_pc.size()>0){
            return torrentList_pc.get(0).isShow();
        }
        return false;
    }
    private List<String> getSelectedPaths_pc(){
        List<String> paths = new ArrayList<>();
        for (TorrentFile f : torrentList_pc){
            if (f.isShow()&&f.isChecked()){
                paths.add(f.getTorrentPath());
            }
        }
        return paths;
    }
    private void refresh(){
        getName();
        swipeRefresh.setEnabled(true);
        menuList.setVisibility(View.GONE);
        remoteControl.setVisibility(View.VISIBLE);
    }
    private void refresh_pc(){
        initData();
        swipeRefresh.setEnabled(true);
        menuList_pc.setVisibility(View.GONE);
        remoteControl.setVisibility(View.VISIBLE);
    }

    public  void initUTorrent() {
        if (MyApplication.isSelectedPCOnline() && !TextUtils.isEmpty(MyApplication.getSelectedPCIP().ip)){
            UrlUtils.ip_port = MyApplication.getSelectedPCIP().ip+":"+IPAddressConst.UTORRENT_PORT;
            try{
                String username_password = "test:1234";
                OkHttpUtils.authorization = "Basic "+ Base64.encodeToString(username_password.getBytes("utf-8") , Base64.NO_WRAP);
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
                OkHttpUtils.authorization = "";
            }
            if (!TextUtils.isEmpty(OkHttpUtils.authorization)){
                String url = "http://" + UrlUtils.ip_port + "/gui/token.html?t=" + String.valueOf(new Date().getTime());
                OkHttpUtils.getInstance().setUrl(url).build()
                        .execute(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.w("SubTitleUtil", "onFailure");
                                //uTorrent可能未启动
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            new Send2PCThread(OrderConst.UTOrrent, "",new Handler()).start();
                                            Thread.sleep(4000);
                                            if (isCreated){
                                                initUTorrent();
                                                MyApplication.getPcAreaPartyPath();
                                            }
                                        } catch (InterruptedException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                ResponseBody responseBody = response.body();
                                //response.close();
                                if (responseBody == null){
                                    Log.w("SubTitleUtil","responseBody null");
                                }else {
                                    String responseData = responseBody.string();
                                    response.close();
                                    if (TextUtils.isEmpty(responseData)){
                                        Log.w("SubTitleUtil","responseData null");
                                        /*handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toasty.error(MyApplication.getContext(),"请检查用户名和密码是否正确").show();
                                            }
                                        });*/

                                    }else {
                                        Log.w("SubTitleUtil",responseData+"&&&"+responseData.length());
                                        if (!(responseData.length() == 121)){
                                            /*handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toasty.error(MyApplication.getContext(),"未知错误").show();
                                                }
                                            });*/

                                        }else {
                                            UrlUtils.token = responseData.substring(44,108);
                                            getSettings();
                                        }
                                    }
                                }

                            }
                        });
            }

        }


    }

    public static void getSettings() {
        if (!TextUtils.isEmpty(rootPath) && !TextUtils.isEmpty(UrlUtils.ip_port)&&!TextUtils.isEmpty(UrlUtils.token)&&!TextUtils.isEmpty(OkHttpUtils.authorization)){
            final String url = "http://"+UrlUtils.ip_port+"/gui/?token="+ UrlUtils.token+"&action=getsettings";
            OkHttpUtils.getInstance().setUrl(url).build()
                    .execute(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            response.close();
                            //Log.w(TAG, responseData);
                            try {
                                JSONObject jsonObject = new JSONObject(responseData);
                                JSONArray jsonArray = jsonObject.getJSONArray("settings");
                                String dir_active_download_flag = jsonArray.getJSONArray(69).getString(2);
                                String dir_active_download = jsonArray.getJSONArray(73).getString(2);
                                String dir_torrent_files_flag = jsonArray.getJSONArray(70).getString(2);
                                String dir_torrent_files = jsonArray.getJSONArray(74).getString(2);
                                String dir_autoload = jsonArray.getJSONArray(180).getString(2);
                                String dir_autoload_flag = jsonArray.getJSONArray(178).getString(2);
                                String dir_autoload_delete = jsonArray.getJSONArray(179).getString(2);
                                Log.w(TAG, dir_active_download_flag+"");
                                Log.w(TAG, dir_active_download+"");
                                Log.w(TAG, dir_torrent_files_flag+"");
                                Log.w(TAG, dir_torrent_files+"");
                                Log.w(TAG, dir_autoload+"");
                                Log.w(TAG, dir_autoload_flag+"");
                                Log.w(TAG, dir_autoload_delete+"");
                                if (!dir_active_download_flag.equals("true")){
                                    setUTorrent("dir_active_download_flag","1");
                                }
                                if (!TextUtils.isEmpty(downloadPath) && !dir_active_download.equals(downloadPath)){
                                    setUTorrent("dir_active_download",downloadPath);
                                }
                                if (!dir_torrent_files_flag.equals("true")){
                                    setUTorrent("dir_torrent_files_flag","1");
                                }
                                if (!TextUtils.isEmpty(rootPath+"\\TorrentHiden") && !dir_torrent_files.equals(rootPath+"\\TorrentHiden")){
                                    setUTorrent("dir_torrent_files",rootPath+"\\TorrentHiden");
                                }

                                if (!dir_autoload_flag.equals("true")){
                                    setUTorrent("dir_autoload_flag","1");
                                }
                                if (!dir_autoload_delete.equals("true")){
                                    setUTorrent("dir_autoload_delete","1");
                                }
                                if (!TextUtils.isEmpty(targetPath) && !dir_autoload.equals(targetPath)){
                                    setUTorrent("dir_autoload",targetPath);
                                }else {
                                    targetPath = dir_autoload;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
    }

    public static void setUTorrent(final String s, String v){
        try {
            String url = new UrlUtils().setAction("setsetting").setS(s).setV(URLEncoder.encode(v,"UTF-8")).toString();
            OkHttpUtils.getInstance().setUrl(url).build().execute(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.w("url","onFailure");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    response.close();
                    //Log.w("url",s + response.body().string());
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
