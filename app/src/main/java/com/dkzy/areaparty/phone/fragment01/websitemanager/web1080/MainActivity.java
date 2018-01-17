package com.dkzy.areaparty.phone.fragment01.websitemanager.web1080;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome.store.CookieStore;
import com.dkzy.areaparty.phone.fragment01.websitemanager.start.ADFilterTool;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.Send2PCThread;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    private WebView mWebview;
    private WebSettings mWebSettings;
    private ProgressBar mProgressBar;
    private Toolbar toolbar;

    private String StringUrl;
    private DownloadManager downloadManager;
    private long mTaskId;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownloadStatus();//检查下载状态
        }
    };

    //定义是否退出程序的标记
    private boolean isExit=false;
    //定义接受用户发送信息的handler
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //标记用户不退出状态
            isExit=false;
        }
    };


    private String torrentLink;
    private String torrentFileName;
    CookieManager cookieManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web1080_activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        StringUrl=bundle.getString("URL");



        toolbar = (Toolbar) findViewById(R.id.login_toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.webmanager_ic_goback);
        }


        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }



        mWebview = (WebView) findViewById(R.id.webView1);

        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(mWebview, true);



        //cookieManager.removeAllCookie();
        //581
        /*if (StringUrl.contains("www.1080.net") && (TextUtils.isEmpty(cookieManager.getCookie("www.1080.net")) || cookieManager.getCookie("www.1080.net").length()<500)){
            cookieManager.setCookie("www.1080.net", "__jsluid=d56c839813a4c2cfd6b0ac1cb2c1e1d9;");
            cookieManager.setCookie("www.1080.net","rzkV_2132_saltkey=YijN27F6;");
            cookieManager.setCookie("www.1080.net","rzkV_2132_lastvisit=1510814999;");
            cookieManager.setCookie("www.1080.net","rzkV_2132_ulastactivity=fb922NnM1UzZYh6LE0ckifMe0x%2BaLTR2ya5ds0q7z7AoXPP%2FJFO%2B;");
            cookieManager.setCookie("www.1080.net","rzkV_2132_auth=9e02RFsZLCsZSHCFhzhavBIQZVFmvVRAuyfuCTnpx0OmKM5%2FY2cKRVZKfl0cuCCHpBIQRdBEareOk1MnuoPMaxQLSsA;");
            cookieManager.setCookie("www.1080.net","rzkV_2132_lastcheckfeed=705931%7C1510819636;");
            cookieManager.setCookie("www.1080.net","rzkV_2132_lip=223.85.200.129%2C1510819051;");
            cookieManager.setCookie("www.1080.net","rzkV_2132_security_cookiereport=71d6UvHQZGfEua4MBYDW1bJVAk4WxNXasfUMd7Gl9PwFmkrnnD6w;");
            cookieManager.setCookie("www.1080.net","rzkV_2132_nofavfid=1;rzkV_2132_home_diymode=1;");
            cookieManager.setCookie("www.1080.net","rzkV_2132_lastact=1510819992%09forum.php%09;");
            cookieManager.setCookie("www.1080.net","rzkV_2132_connect_is_bind=0;");
            cookieManager.setCookie("www.1080.net","rzkV_2132_sid=MS2UqP;");
        }else if (StringUrl.contains("www.longbaidu.com") && (TextUtils.isEmpty(cookieManager.getCookie("www.longbaidu.com")) || cookieManager.getCookie("www.longbaidu.com").length()<500)){
            cookieManager.setCookie("www.longbaidu.com","yncM_2132_saltkey=k3SSg6Ao;");
            cookieManager.setCookie("www.longbaidu.com","yncM_2132_lastvisit=1510902845;");
            cookieManager.setCookie("www.longbaidu.com","yncM_2132_onlineusernum=586;");
            cookieManager.setCookie("www.longbaidu.com","yncM_2132_pc_size_c=0;");
            cookieManager.setCookie("www.longbaidu.com","yncM_2132_ulastactivity=08dfLqKUAx856SEIbCha68R4Q2HPCvJNk8E%2Fsqc%2FPLJs4SboK2rL;");
            cookieManager.setCookie("www.longbaidu.com","yncM_2132_auth=68774qhxMXbvZ7Liif%2FyFqXLRFHc5dVzyMOAeIDhPfFYiP4ec6lTuU3HMJaKUWOLUQLZJq04hPcepzLowkf3w2JlISs;");
            cookieManager.setCookie("www.longbaidu.com","yncM_2132_lastcheckfeed=128006%7C1510906849;");
            cookieManager.setCookie("www.longbaidu.com","yncM_2132_checkfollow=1;");
            cookieManager.setCookie("www.longbaidu.com","yncM_2132_lip=223.85.200.129%2C1510906206;");
            cookieManager.setCookie("www.longbaidu.com","yncM_2132_security_cookiereport=8c29Iw7JuFXhe1xooNHTyYVqbts11X%2Bi4Cz%2FP44BTh4BlT1Od%2BYJ;");
            cookieManager.setCookie("www.longbaidu.com","yncM_2132_lastact=1510906855%09forum.php%09;");
            cookieManager.setCookie("www.longbaidu.com","yncM_2132_connect_is_bind=0;");
            cookieManager.setCookie("www.longbaidu.com","yncM_2132_nofavfid=1;");
            cookieManager.setCookie("www.longbaidu.com","yncM_2132_sid=v560Wo;");
        }else if (StringUrl.contains("www.vkugq.com") && (TextUtils.isEmpty(cookieManager.getCookie("www.vkugq.com")) || cookieManager.getCookie("www.vkugq.com").length()<500)){
            cookieManager.setCookie("www.vkugq.com","5mQg_2132_saltkey=y0Vc9rLY;");
            cookieManager.setCookie("www.vkugq.com","5mQg_2132_lastvisit=1510905040;");
            cookieManager.setCookie("www.vkugq.com","5mQg_2132_onlineusernum=697;");
            cookieManager.setCookie("www.vkugq.com","5mQg_2132_ulastactivity=bbd4ZW0G0ptvj1qPAotLpc%2BmBEv80c%2FBSfYzwRB6aCpJor9bshbh;");
            cookieManager.setCookie("www.vkugq.com","5mQg_2132_auth=fc27BbWzx%2BMqCgXy9BlddfgRTU%2BBHvcqCRmsY7gpuYd9LuGIDt%2BWEC5mlgvnMiMXa03CA1sYA0KmlM0ZFqTs2bcYnBw;");
            cookieManager.setCookie("www.vkugq.com","5mQg_2132_lastcheckfeed=170793%7C1510908729;");
            cookieManager.setCookie("www.vkugq.com","5mQg_2132_lip=223.85.200.129%2C1510907868;");
            cookieManager.setCookie("www.vkugq.com","5mQg_2132_lastact=1510908734%09index.php%09;");
            cookieManager.setCookie("www.vkugq.com","5mQg_2132_connect_is_bind=0;");
            cookieManager.setCookie("www.vkugq.com","5mQg_2132_nofavfid=1;");
            cookieManager.setCookie("www.vkugq.com","5mQg_2132_sid=uB65To");
        }*/
        mProgressBar = (ProgressBar) findViewById(R.id.myProgressBar);
        mWebSettings = mWebview.getSettings();
        mWebview.loadUrl(StringUrl);

        mWebview.requestFocus();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setBlockNetworkImage(false);
        mWebSettings.setBlockNetworkLoads(false);



        //设置不用系统浏览器打开,直接显示在当前Webview
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().toLowerCase();
                return super.shouldInterceptRequest(view, request);
                /*if (!ADFilterTool.hasAd(MyApplication.getContext(), url)) {
                    return super.shouldInterceptRequest(view, request);//正常加载
                }else{
                    return new WebResourceResponse(null,null,null);//含有广告资源屏蔽请求
                }*/
            }
        });



    //设置WebChromeClient类
        mWebview.setWebChromeClient(new WebChromeClient() {


            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                toolbar.setTitle(title);
            }


            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == mProgressBar.getVisibility()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });


        //设置WebViewClient类
        mWebview.setWebViewClient(new WebViewClient() {

            //设置加载前的函数
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contains("mobile=yes")||url.contains("mod=mobil")){
                    mWebSettings.setSupportZoom(false);
                    mWebSettings.setBuiltInZoomControls(false);
                }
                if (url.contains("mobile=no")){
                    mWebSettings.setSupportZoom(true);
                    mWebSettings.setBuiltInZoomControls(true);
                }
            }

            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {
                //CookieManager cookieManager = CookieManager.getInstance();
//                String CookieStr1 = cookieManager.getCookie(url);
//                Log.w("WebView", "2Cookies = "+CookieStr1.length()+"***"+ CookieStr1);
            }
        });

        //文件下载
        mWebview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String s, String s1, String s2, String s3, long l) {
                final String filename = s2.substring(s2.indexOf("=")+2,s2.indexOf("torrent")+7).replaceAll("\\s*","");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                final View textEntryView = factory.inflate(R.layout.web1080_dialog_view, null);
                final EditText fileNameEditText = (EditText) textEntryView.findViewById(R.id.file_name);
                fileNameEditText.setText(filename);
                EditText filePathEditText = (EditText) textEntryView.findViewById(R.id.file_path);
                filePathEditText.setText(Environment.getExternalStorageDirectory().getAbsolutePath() + "/areaparty/downloadedfiles/webmanager_download/");
                builder.setIcon(R.drawable.webmanager_ic_download);
                builder.setTitle("种子文件下载");
                builder.setView(textEntryView);
                builder.setCancelable(true);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String finalFileName = fileNameEditText.getText().toString();

                        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/areaparty/downloadedfiles/webmanager_download/" + finalFileName);
                        if (f.exists()){
                            Toast.makeText(MainActivity.this, "种子已经存在", Toast.LENGTH_SHORT).show();
                        }else{
                            torrentFileName = finalFileName;
                            torrentLink = s;
                            downloadTorrent(s,finalFileName);
                        }


                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                builder.show();


            }
        });


    }

//    /*//*/点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(mWebview.canGoBack()) {
                //如果isExit标记为false，提示用户再次按键
                if(!isExit){
                    mWebview.goBack();
                    isExit=true;
                    //如果用户没有在2秒内再次按返回键的话，就发送消息标记用户为不退出状态
                    mHandler.sendEmptyMessageDelayed(0, 500);
                }
                //如果isExit标记为true，退出程序
                else{
                    //退出程序
                    finish();
                }//退出程序
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (mWebview != null) {
            mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebview.clearHistory();

            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
        }
        super.onDestroy();
    }

    private void downloadTorrent(String versionUrl, String versionName) {
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(versionUrl));
        String c = cookieManager.getCookie(StringUrl);
        if (!TextUtils.isEmpty(c)){
            Log.w("WebView", "Cookies = "+ c);
            String[] cookies = c.split(" ");
            for (int i = 0; i < cookies.length; i++){
                String cookie = cookies[i];
                request.addRequestHeader("Set-Cookie",cookie);
                Log.w("WebView", "Cookies = "+ cookie);
            }
        }
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载



        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);

        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir("/areaparty/webmanager_download/", versionName);
        //request.setDestinationInExternalFilesDir(),也可以自己制定下载路径

        //将下载请求加入下载队列
        downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        mTaskId = downloadManager.enqueue(request);

        //注册广播接收者，监听下载状态
        getApplicationContext().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
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
                    Toast.makeText(this, "下载成功", Toast.LENGTH_SHORT).show();
//                    Snackbar.make(mCoordinatorLayout, "下载成功",Snackbar.LENGTH_INDEFINITE).show();
//                            .setAction("查看", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(MyApplication.getContext(), SubTitleUtil.class);
//                                    intent.putExtra("index", 3);
//                                    startActivity(intent);
//                                }
//                            })

                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("_downloaded", Context.MODE_PRIVATE).edit();
                    editor.putString(torrentFileName, torrentFileName);
                    editor.apply();
                    break;
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(this, "下载失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up webmanager_button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case android.R.id.home :
                if (mWebview.canGoBack()){
                    mWebview.goBack();
                }else{
                    this.finish();
                }
                break;
            case R.id.go_home:
                startActivity(new Intent(this, com.dkzy.areaparty.phone.MainActivity.class));
                break;
            case R.id.refresh:
                mWebview.reload();
                break;
            case R.id.remote_download:
                new Send2PCThread(OrderConst.UTOrrent, "",new Handler()).start();
                Intent intent1 = new Intent(MainActivity.this, RemoteDownloadActivity.class);
                startActivity(intent1);
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
