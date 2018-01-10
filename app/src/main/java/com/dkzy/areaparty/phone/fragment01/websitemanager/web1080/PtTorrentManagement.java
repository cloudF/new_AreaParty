package com.dkzy.areaparty.phone.fragment01.websitemanager.web1080;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.adapter.TorrentFile;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.adapter.TorrentFileAdapter;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.callback.OnTorrentFileItemListener;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class PtTorrentManagement extends AppCompatActivity {

    private TorrentFileAdapter adapter;
    private List<TorrentFile> torrentList = new ArrayList<>();

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web1080_activity_remote_download);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


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

        adapter = new TorrentFileAdapter(torrentList,true);
        recyclerView.setAdapter(adapter);
        getName();
        setListener();




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
        String directory1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/areaparty/downloadedfiles/webmanager_download/pt种子";
        File file = new File(directory1);
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(PtTorrentManagement.this);
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(PtTorrentManagement.this);
                dialog.setTitle("传送");
                dialog.setMessage("将把种子文件发送到电脑");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        torrentList2.add(new DownloadStatus(torrent.getTorrentFileName(), 0, true));
//                        adapter2.notifyDataSetChanged();
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

            }

        });
    }
}
