package com.dkzy.areaparty.phone.fragment01.utorrent;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;


import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.utorrent.adapter.TorrentFileAdapter;
import com.dkzy.areaparty.phone.fragment01.utorrent.adapter.WrapContentLinearLayoutManager;
import com.dkzy.areaparty.phone.fragment01.utorrent.model.TorrentFile;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TorrentFileActivity extends AppCompatActivity {

    private List<TorrentFile> torrentList = new ArrayList<>();
    private TorrentFileAdapter adapter;

    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.recyclerView_torrent)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.utorrent_activity_phone_torrent);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);

        getName();
        adapter = new TorrentFileAdapter(torrentList);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        recyclerView.setAdapter(adapter);
//        recyclerView.setAdapter(MultipleSelect
//                .with(this)
//                .adapter(adapter)
//                .decorateFactory(new ColorFactory())
//                .linkList(adapter.getmTorrentList())
//                .customMenu(new MyMenuBar(this,R.menu.menu_select, getResources().getColor(R.color.colorPrimary), Gravity.BOTTOM,torrentList))
//        .build());
    }

    public void getName(){
        String directory1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/areaparty/webmanager_download";
        File file = new File(directory1);
        if(!file.exists()){
            file.mkdirs();
        }
        if(file != null){
            File[] files = file.listFiles(new FileNameSelector("torrent"));
            Log.w("TorrentFileActivity", files.length+"");
            for(File f : files){
                torrentList.add(new TorrentFile(f.getName(), f.getAbsolutePath()));
            }
        }
//        adapter.notifyDataSetChanged();
    }


    private class FileNameSelector implements FilenameFilter {
        String extension = ".";
        public FileNameSelector(String fileExtensionNoDot) {
            extension += fileExtensionNoDot;
        }

        public boolean accept(File dir, String name) {
            return name.endsWith(extension);
        }
    }
}
