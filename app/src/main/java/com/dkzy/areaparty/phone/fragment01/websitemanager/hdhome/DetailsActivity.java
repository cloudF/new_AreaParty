package com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class DetailsActivity extends AppCompatActivity {
    private String detailsUrl;

    //显示控件
    private CoordinatorLayout mCoordinatorLayout;
    private TextView torrentContent;
    private ImageView torrentImage;
    private TextView torrent_title;
    private TextView torrent_information;
    private FloatingActionButton fab;

    String torrentTitle;
   private String torrentLink = null;
   private String torrentFileName = null;

    private long mTaskId;
    private DownloadManager downloadManager;

    //广播接受者，接收下载状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownloadStatus();//检查下载状态
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.hdhome_details_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
//        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        //获得DetailUrl
        Intent intent = getIntent();
        detailsUrl = intent.getStringExtra("DetailUrl");

        //初始化控件
        initView();

        //加载详细信息
        loadWebPage();

        //下载种子文件
        downloadTorrent();

    }



    /*
    修改下载判定和新建文件夹
     */
    private void downloadTorrent(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(torrentFileName) && !TextUtils.isEmpty(torrentLink)){
                    Snackbar.make(v, "下载种子文件", Snackbar.LENGTH_SHORT)
                            .setAction("下载", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    if (MyApplication.getContext().getSharedPreferences("torrent_downloaded", Context.MODE_PRIVATE).getString(torrentFileName,"null").equals(torrentTitle)){
//                                        Toast.makeText(DetailsActivity.this, "种子已经存在", Toast.LENGTH_SHORT).show();
//                                    }else{
//                                        downloadFile(torrentLink, torrentFileName);
//                                    }

                                    File f = new File(Environment.getExternalStoragePublicDirectory
                                            (Environment.DIRECTORY_DOWNLOADS).getPath()+"/pt种子"+"/"+torrentFileName);
                                    if (f.exists()){
                                        Toast.makeText(DetailsActivity.this, "种子已经存在", Toast.LENGTH_SHORT).show();
                                    }else{
                                        downloadFile(torrentLink, torrentFileName);
                                    }

                                }
                            }).show();



                }else{
                    Toast.makeText(DetailsActivity.this, "页面加载中，请稍等...", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //使用系统下载器下载
    private void downloadFile(String versionUrl, String versionName) {
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(versionUrl));
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载



        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);

        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir("/aeraparty/webmanager_download/pt种子", versionName);
        //request.setDestinationInExternalFilesDir(),也可以自己制定下载路径

        //将下载请求加入下载队列
        downloadManager = (DownloadManager) MyApplication.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        mTaskId = downloadManager.enqueue(request);

        //注册广播接收者，监听下载状态
        MyApplication.getContext().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    //检查下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    Toast.makeText(this, "下载暂停", Toast.LENGTH_SHORT).show();break;
                case DownloadManager.STATUS_PENDING:
                    Toast.makeText(this, "下载延迟", Toast.LENGTH_SHORT).show();break;
                case DownloadManager.STATUS_RUNNING:
                    Toast.makeText(this, "正在下载", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Snackbar.make(mCoordinatorLayout, "下载成功", Snackbar.LENGTH_INDEFINITE).show();
//                            .setAction("查看", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(MyApplication.getContext(), SubTitleUtil.class);
//                                    intent.putExtra("index", 3);
//                                    startActivity(intent);
//                                }
//                            })

                    SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("torrent_downloaded", Context.MODE_PRIVATE).edit();
                    editor.putString(torrentFileName, torrentTitle);
                    editor.apply();
                    break;
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(this, "下载失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private  void loadWebPage(){
        Request request = new Request.Builder().url(detailsUrl).build();
        WelcomeActivity.instance.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseData = response.body().string();
                Document parse = Jsoup.parse(responseData);
                Elements elements = parse.select("td[class=rowfollow]");
                //种子文件名
                torrentFileName = elements.get(0).child(0).text().replace(" ","");
                //Title名字
                torrentTitle=elements.get(0).child(0).text().replace(" ","").replace("[HDHome].","").replace(".torrent","");
                //种子信息
                final String torrentInformation = elements.get(2).text().replaceAll("\\s([\\u4e00-\\u9fa5])","\n"+"$1");
                torrentLink = elements.get(5).text();
                //图片
                final Element contentElement = parse.getElementById("kdescr");
                Element imageElement = contentElement.select("img").first();
                final String imageURL;
                if (imageElement == null){
                    imageURL = null;
                }else{
                    imageURL = imageElement.attr("src");
                }

                //电影详情介绍
                contentElement.select("img").remove();
                contentElement.select("div").remove();
                contentElement.select("span").remove();
                contentElement.select("fieldset").remove();
                //加载图片
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(imageURL)){
                            Glide.with(getBaseContext()).load(imageURL).apply(new RequestOptions().placeholder(R.drawable.webmanager_movie).error(R.drawable.webmanager_movie)).into(torrentImage);
                        }else{
                            Glide.with(getBaseContext()).load(R.drawable.webmanager_movie).into(torrentImage);
                        }
                        torrent_title.setText(torrentTitle);
                        torrent_information.setText(torrentInformation);
                        torrentContent.setMovementMethod(LinkMovementMethod.getInstance());
                        torrentContent.setText(Html.fromHtml(contentElement.outerHtml()));
                    }
                });
            }
        });

    }

    private void initView(){
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_torrent_detail);
        torrentContent = (TextView) findViewById(R.id.textView2);
        torrent_title = (TextView) findViewById(R.id.torrent_title);
        torrentImage = (ImageView) findViewById(R.id.torrent_image);
        torrent_information = (TextView) findViewById(R.id.torrent_information);

    //    torrent_title.setText(torrentTitle);

        fab = (FloatingActionButton)findViewById(R.id.fab);

    }
}
