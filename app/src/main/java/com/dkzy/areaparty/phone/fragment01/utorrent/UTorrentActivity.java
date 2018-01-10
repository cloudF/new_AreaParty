package com.dkzy.areaparty.phone.fragment01.utorrent;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.utorrent.adapter.UTorrentAdapter;
import com.dkzy.areaparty.phone.fragment01.utorrent.adapter.WrapContentLinearLayoutManager;
import com.dkzy.areaparty.phone.fragment01.utorrent.customView.MyItemDecoration;
import com.dkzy.areaparty.phone.fragment01.utorrent.listener.OnRecycleViewClickListener;
import com.dkzy.areaparty.phone.fragment01.utorrent.model.Torrent;
import com.dkzy.areaparty.phone.fragment01.utorrent.utils.OkHttpUtils;
import com.dkzy.areaparty.phone.fragment01.utorrent.utils.TorrentUtils;
import com.dkzy.areaparty.phone.fragment01.utorrent.utils.UrlUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UTorrentActivity extends AppCompatActivity {

//    private String author;
//    private String ip_port;
//    private String token;
    private Timer timer;

    private OnRecycleViewClickListener listener;
    private String torrentc;
    private List<Torrent> torrentList = new ArrayList<>();
    private UTorrentAdapter adapter;

    @BindView(R.id.recyclerView_torrent)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.utorrent_activity_utorrent);
        ButterKnife.bind(this);
//        toolbar.setLogo(R.drawable.ic_utorrent_logo);
        toolbar.setTitle("任务列表");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.webmanager_ic_goback);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.go_home:
                        startActivity(new Intent(UTorrentActivity.this, com.dkzy.areaparty.phone.MainActivity.class));
                        break;
                }
                return true;
            }
        });


//        Intent intent = getIntent();
//        author = intent.getStringExtra("author");
//        ip_port = intent.getStringExtra("ip_port");
//        token = intent.getStringExtra("token");

        initListener();
        adapter = new UTorrentAdapter(torrentList);
        adapter.setListener(listener);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        recyclerView.addItemDecoration(new MyItemDecoration());
        recyclerView.setAdapter(adapter);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.utorrent_toolbar, menu);
//        return true;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case 1:
                    List<String> list = data.getStringArrayListExtra("paths");
                    if (list!=null && list.size()>0 && list.get(0).endsWith(".torrent")){
                        String url = "http://"+ UrlUtils.ip_port+"/gui/?token="+UrlUtils.token+"&action=add-file&download_dir=0&path=";
                        OkHttpUtils.getInstance().setUrl(url).postBuild(list)
                                .execute(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Log.w("UTorrentActivity","error");
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String responseData = response.body().string();
                                        if (TextUtils.isEmpty(responseData)){
                                            Log.w("UTorrentActivity","responseData is null");
                                        }else {
                                            Log.w("UTorrentActivity",responseData);
                                        }
                                    }
                                });
                    }
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                TextView title = new TextView(this);
                title.setText("添加种子任务");
                title.setPadding(10,10,10,50);
                title.setTextSize(20);
                title.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                title.setGravity(Gravity.CENTER);
                builder.setCustomTitle(title);
                builder.setCancelable(true);
                builder.setPositiveButton("从手机添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //startActivity();
                        startActivityForResult(new Intent(UTorrentActivity.this, TorrentFileActivity.class),1);
                    }
                });
                builder.setNeutralButton("从电脑添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();

                break;
        }
        return  true;
    }

    @Override
    protected void onStop() {
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();

    }

    private void initData() {

        getTorrentsList();
    }
    private void updateDate(){
        if (timer == null){
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getUpdateTorrentsList();
            }
        },1000,1500);
    }

    private void getTorrentsList(){
        String url = new UrlUtils().setList("1").toString();
        OkHttpUtils.getInstance().setUrl(url).build()
                .execute(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toasty.error(getApplicationContext(),"网络信号差").show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        //Log.w("UTorrentActivity",responseData);
                        if ((responseData.length() <= 17) && responseData.contains("invalid request")){
                            getNewToken();
                        }else{
                            try {
                                JSONObject jsonObject = new JSONObject(responseData);
                                String torrents = jsonObject.getString("torrents");
                                torrentc = jsonObject.getString("torrentc");
                                torrentList.clear();
                                torrentList.addAll(TorrentUtils.paserTorrents(torrents));
                                for (Torrent torrent: torrentList){
                                    Log.w("UTorrentActivity","**"+torrent.getStatus()+"**");
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                        updateDate();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
    }

    private void getUpdateTorrentsList(){
        String url = new UrlUtils().setList("1").setCid(torrentc).toString();
        OkHttpUtils.getInstance().setUrl(url).build()
                .execute(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
//
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        updateTorrentsList(responseData);
                    }
                });
    }



    private void getNewToken() {
        String url = "http://" + UrlUtils.ip_port + "/gui/token.html?t=" + String.valueOf(new Date().getTime());
        OkHttpUtils.getInstance().setUrl(url).build()
                .execute(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.w("SubTitleUtil", "onFailure");
                        Toasty.error(getApplicationContext(),"请检查ip地址和端口号是否正确").show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ResponseBody responseBody = response.body();
                        if (responseBody == null){
                            Log.w("SubTitleUtil","responseBody null");
                        }else {
                            String responseData = responseBody.string();
                            if (TextUtils.isEmpty(responseData)){
                                Log.w("SubTitleUtil","responseData null");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toasty.error(getApplicationContext(),"请检查用户名和密码是否正确").show();
                                    }
                                });

                            }else {
                                Log.w("SubTitleUtil",responseData+"&&&"+responseData.length());
                                if (!(responseData.length() == 121)){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toasty.error(getApplicationContext(),"未知错误").show();
                                        }
                                    });

                                }else {
                                   UrlUtils.token = responseData.substring(44,108);
                                    initData();
                                }
                            }
                        }

                    }
                });
    }


    private void initListener() {
        listener = new OnRecycleViewClickListener() {
            @Override
            public void getFileList(int position) {
                if (!TextUtils.isEmpty(torrentList.get(position).getHash())){
                    Intent intent = new Intent(UTorrentActivity.this, UTFileActivity.class);
                    intent.putExtra("hash",torrentList.get(position).getHash());
                    startActivity(intent);
                }
            }

            @Override
            public void start(final int position) {
                String url = new UrlUtils().setAction("start").setHashList(Arrays.asList(torrentList.get(position).getHash())).setList("1").setCid(torrentc).toString();
                OkHttpUtils.getInstance().setUrl(url).build()
                        .execute(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseData = response.body().string();
                                Log.w("UTorrentActivity",responseData);
                                updateTorrentsList(responseData);
                            }
                        });
            }

            @Override
            public void stop(int position) {
                String url = new UrlUtils().setAction("stop").setHashList(Arrays.asList(torrentList.get(position).getHash())).setList("1").setCid(torrentc).toString();
                OkHttpUtils.getInstance().setUrl(url).build()
                        .execute(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseData = response.body().string();
                                Log.w("UTorrentActivity",responseData);
                                updateTorrentsList(responseData);
                            }
                        });
            }

            @Override
            public void pause(int position) {
                String url = new UrlUtils().setAction("pause").setHashList(Arrays.asList(torrentList.get(position).getHash())).setList("1").setCid(torrentc).toString();
                OkHttpUtils.getInstance().setUrl(url).build()
                        .execute(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseData = response.body().string();
                                Log.w("UTorrentActivity",responseData);
                                updateTorrentsList(responseData);
                            }
                        });
            }

            @Override
            public void remove(int position) {
                String url = new UrlUtils().setAction("remove").setHashList(Arrays.asList(torrentList.get(position).getHash())).setList("1").setCid(torrentc).toString();
                OkHttpUtils.getInstance().setUrl(url).build()
                        .execute(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseData = response.body().string();
                                Log.w("UTorrentActivity",responseData);
                                updateTorrentsList(responseData);
                            }
                        });
            }
        };
    }

    private void updateTorrentsList(String responseData){
        if ((responseData.length() <= 17) && responseData.contains("invalid request")){
            getNewToken();
        }else{
            try {
                JSONObject jsonObject = new JSONObject(responseData);
                torrentc = jsonObject.getString("torrentc");
                //
                if (jsonObject.has("torrents")){//返回的是全部torrents列表时，执行喝上一个函数相同操作
                    String torrents = jsonObject.getString("torrents");
                    torrentc = jsonObject.getString("torrentc");
                    torrentList.clear();
                    torrentList.addAll(TorrentUtils.paserTorrents(torrents));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            updateDate();
                        }
                    });
                }//
                else{
                    String torrents = jsonObject.getString("torrentp");//可能有异常产生
                    String torrentm = jsonObject.getString("torrentm");//被移除的种子hash s;

                    JSONArray removedTorent = new JSONArray(torrentm);
                    for (int i = 0; i<removedTorent.length();i++){
                        for (Torrent torrent_old : torrentList){
                            if (removedTorent.getString(i).equals(torrent_old.getHash())){
                                torrentList.remove(torrent_old);
                                break;
                            }
                        }
                    }


                    List<Torrent> newTorrentList = TorrentUtils.paserTorrents(torrents);
                    for (Torrent torrent_new : newTorrentList){
                        boolean find = false;
                        for (Torrent torrent_old : torrentList){
                            if (torrent_new.getHash().equals(torrent_old.getHash())){
                                find = true;
                                torrent_old.setPgress(torrent_new.getPgress());
                                torrent_old.setDownloadSpeed(torrent_new.getDownloadSpeed());
                                torrent_old.setETA(torrent_new.getETA());
                                torrent_old.setStatus(torrent_new.getStatus());
                                break;
                            }
                        }
                        if (!find){
                            torrentList.add(torrent_new);
                        }
                    }
                    if (removedTorent.length() > 0 || newTorrentList.size() > 0){//有更新的内容时刷新界面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }


                }
            } catch (JSONException e) {
                if (timer!=null){
                    timer.cancel();
                    timer = null;
                    initData();
                }
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gohome, menu);
        return true;
    }
}
