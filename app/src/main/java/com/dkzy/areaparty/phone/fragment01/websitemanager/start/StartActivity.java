package com.dkzy.areaparty.phone.fragment01.websitemanager.start;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dkzy.areaparty.phone.IPAddressConst;
import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_help;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_page;
import com.dkzy.areaparty.phone.fragment01.utorrent.utils.OkHttpUtils;
import com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome.CookieJarImpl;
import com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome.store.PersistentCookieStore;
import com.dkzy.areaparty.phone.fragment01.websitemanager.vipShare.VipLeaseActivity;
import com.dkzy.areaparty.phone.fragment01.websitemanager.vipShare.VipRentActivity;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.MainActivity;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.RemoteDownloadActivity;
import com.dkzy.areaparty.phone.fragment05.accessible_service.AutoLoginService;
import com.dkzy.areaparty.phone.fragment05.accessible_service.Util;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.PreferenceUtil;
import com.dkzy.areaparty.phone.utilseverywhere.utils.IntentUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import info.hoang8f.widget.FButton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.dkzy.areaparty.phone.myapplication.MyApplication.AREAPARTY_NET;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{
    //图片按钮
    private ImageView[] image = new ImageView[5];

    //链接按钮
    private Button[] btn = new Button[5];
    //搜索结果数
    private TextView[] searchCounts = new TextView[5];

    //下载管理
    private Button downloadManagement;

    private TextView share_tv;
    private TextView floatViewTV, autoLoginServiceTV, autoLoginHelper;
    private TextView helpInfo;

    //网站地址
    //http://www.87lou.com/
    //http://www.xixizh.com/
    //http://www.1080.net
    //http://www.dayangd.com/
    //http://www.vkugq.com
    //http://www.dyttbbs.com
    //http://www.btshoufa.net/forum.php
    //http://www.1080.cn/
    public static String[] url;
    public static String[] imageUrl;
    private String[] resultUrl = new String[5];
    public static int[] type;
    private String urlWeb1080="http://www.dayangd.com";
    private String urlBlufans="http://www.longbaidu.com";

    private String urlHdchd="http://www.87lou.com";
    private String urlHdchd1 = "http://www.hdchd.cc";
    private String urlXunleicun="http://www.webmanager_xunleicun.com/misc.php?mod=mobile";

    ///phoneVIPAppActivity的变量
    public static final Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
    //public static boolean serviceEnabled;
    private IntentFilter intentFilter;
    public static int QQVersionCode;


    private FButton btn_login_iqiyi, btn_logout_iqiyi, btn_login_youku, btn_logout_youku, btn_login_tencent, btn_logout_tencent, btn_login_leshi, btn_logout_leshi;
    private ImageView tagIqiyi,tagYouku,tagTencent,tagLeshi;

    private boolean isHelpDialogShow;

    public static String logined = "";

    private EditText searchET;
    private TextView clearText;
    private String content = "";
    WebView webView;
    OkHttpClient client;
    okhttp3.OkHttpClient.Builder builder;
    private PersistentCookieStore persistentCookieStore;
    CookieManager cookieManager;
    int count  = 0;
    int i;
    int j;

    private MyCount mc = null;
    private boolean wait = false;

    private TextView select1,select2;
    private RelativeLayout webContent;
    private LinearLayout vipContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.websitemanager_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        phoneVIPAppActivity();///执行phoneVIPAppActivity的onCreate操作
        initView();
        downloadManagement.setOnClickListener(this);
        share_tv.setOnClickListener(this);
        helpInfo.setOnClickListener(this);
        findViewById(R.id.vipRent).setOnClickListener(this);
        findViewById(R.id.vipLease).setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //setTag();

    }

    @Override
    protected void onDestroy() {
        closeWebView();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.image1:
           case R.id.url1:
               goToWebSite(0);
               break;
           case R.id.image2:
           case R.id.url2:
               goToWebSite(1);
               break;
           case R.id.image3:
           case R.id.url3:
               goToWebSite(2);
               break;
           case R.id.image4:
           case R.id.url4:
               goToWebSite(3);
               break;
           case R.id.image5:
           case R.id.url5:
               goToWebSite(4);
               break;
           case R.id.downloadManagement:
               Intent intent4=new Intent(StartActivity.this, RemoteDownloadActivity.class);
               startActivity(intent4);
               break;
           case R.id.tv_share:
//               startActivity(new Intent(StartActivity.this,phoneVIPAppActivity.class));
               break;
           case R.id.floatViewTV:
               if (!MyApplication.mFloatView.isShow()){
                   MyApplication.mFloatView.showAWhile();

               }else {
                   MyApplication.mFloatView.close();

               }
               break;
           case R.id.autoLoginServiceTV:
               IntentUtils.gotoAccessibilitySetting();
               break;
           case R.id.autoLogin_help:
               startActivity(new Intent(StartActivity.this,AutoLoginHelperActivity.class));
               break;
           case R.id.vipRent:
               if (TextUtils.isEmpty(Login.userId)){
                   Toast.makeText(this, "您未登录", Toast.LENGTH_SHORT).show();
               }else if (!Login.mainMobile){
                   Toast.makeText(this, "当前设备不是主设备", Toast.LENGTH_SHORT).show();
               }else {
                   startActivity(new Intent(StartActivity.this, VipRentActivity.class));
               }
               break;
           case R.id.vipLease:
               if (TextUtils.isEmpty(Login.userId)){
                   Toast.makeText(this, "您未登录", Toast.LENGTH_SHORT).show();
               }else if (!Login.mainMobile){
                   Toast.makeText(this, "当前设备不是主设备", Toast.LENGTH_SHORT).show();
               }else {
                   startActivity(new Intent(StartActivity.this, VipLeaseActivity.class));
               }
               break;
           case R.id.helpInfo:
               showHelpInfoDialog(R.layout.dialog_web);
               break;
           case R.id.clear_text:
               searchET.setText("");
               closeWebView();
               closeSearch();
               clearText.setVisibility(View.GONE);
               break;
           default:
               break;

       }
    }
    public  void showHelpInfoDialog(int layout){
        final ActionDialog_page dialog = new ActionDialog_page(this,layout);
        dialog.setCancelable(true);
        dialog.show();
    }


    private void goToWebSite(int i){
        if (i > 4 ) return;
        String urL = "";
        if (searchCounts[i].getVisibility() == View.VISIBLE){
            urL = resultUrl[i];
        }else {
            urL = url[i];
        }
        if (type[i] == 0){
            Intent intent=new Intent(StartActivity.this, MainActivity.class);
            intent.putExtra("URL",urL);
            startActivity(intent);
        }else if (type[i] == 1){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(urL));
            startActivity(intent);
        }
    }


    private  void initView(){
        image[0]=(ImageView) findViewById(R.id.image1);
        image[1]=(ImageView) findViewById(R.id.image2);
        image[2]=(ImageView) findViewById(R.id.image3);
        image[3]=(ImageView) findViewById(R.id.image4);
        image[4]=(ImageView) findViewById(R.id.image5);

        btn[0]=(Button)findViewById(R.id.url1);
        btn[1]=(Button)findViewById(R.id.url2);
        btn[2]=(Button)findViewById(R.id.url3);
        btn[3]=(Button)findViewById(R.id.url4);
        btn[4]=(Button)findViewById(R.id.url5);

        searchCounts[0] = (TextView) findViewById(R.id.searchCount1);
        searchCounts[1] = (TextView) findViewById(R.id.searchCount2);
        searchCounts[2] = (TextView) findViewById(R.id.searchCount3);
        searchCounts[3] = (TextView) findViewById(R.id.searchCount4);
        searchCounts[4] = (TextView) findViewById(R.id.searchCount5);


        if (url == null || imageUrl == null) {
            getWebSiteUrl();
        }else{
            initWebSite();
        }

        downloadManagement=(Button)findViewById(R.id.downloadManagement);
        share_tv = (TextView) findViewById(R.id.tv_share);
        helpInfo = (TextView) findViewById(R.id.helpInfo);
        searchET = (EditText) findViewById(R.id.search_editText);
        clearText = (TextView) findViewById(R.id.clear_text) ;
        clearText.setOnClickListener(this);
        searchET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null){
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    if (TextUtils.isEmpty(searchET.getText().toString())){
                        Toasty.error(StartActivity.this, "搜索内容不能为空").show();
                        return true;
                    }
                    if (wait){
                        Toasty.error(StartActivity.this, "30s内只能执行一次搜索操作").show();
                        return true;
                    }
                    content = searchET.getText().toString();
                    Log.w("StartA","aaaaa:"+content);
                    doSearch();
                    clearText.setVisibility(View.VISIBLE);
                    mc = new MyCount(30000, 1000);
                    mc.start();
                    return true;
                }

                return false;
            }
        });


        select1 = (TextView) findViewById(R.id.select1);
        select2 = (TextView) findViewById(R.id.select2);
        webContent = (RelativeLayout)findViewById(R.id.webContent);
        vipContent = (LinearLayout) findViewById(R.id.vipContent);

        select1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webContent.setVisibility(View.VISIBLE);
                vipContent.setVisibility(View.GONE);

                select1.setTextColor(Color.parseColor("#FF5050"));
                select1.setBackgroundResource(R.drawable.barback03_left_pressed);
                select2.setTextColor(Color.parseColor("#707070"));
                select2.setBackgroundResource(R.drawable.barback03_left_normal);
            }
        });

        select2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webContent.setVisibility(View.GONE);
                vipContent.setVisibility(View.VISIBLE);

                select2.setTextColor(Color.parseColor("#FF5050"));
                select2.setBackgroundResource(R.drawable.barback03_right_pressed);
                select1.setTextColor(Color.parseColor("#707070"));
                select1.setBackgroundResource(R.drawable.barback03_right_normal);
            }
        });

    }


    private void phoneVIPAppActivity() {


        //initViews();//初始化界面

        /*logined = Util.getRecordWebsit(getApplicationContext());
        if (TextUtils.isEmpty(logined)){
            //checkInfo();
        }else {
            setTag();
        }

        isHelpDialogShow = new PreferenceUtil("isHelpDialogShow",getApplicationContext()).readBoole("isHelpDialogShow");*/


    }

    public void showDialog(final View v){
        final ActionDialog_help dialog = new ActionDialog_help(this);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onClickListener(v);
                //onClickListener(v);
                if (dialog.isRadioButtonChecked()){
                    new PreferenceUtil("isHelpDialogShow",getApplicationContext()).writeBoole("isHelpDialogShow",true);
                    isHelpDialogShow = true;
                }
                dialog.dismiss();
            }
        });
    }


    public static void logoutVip(final String type){
        String userName = "";
        if (!TextUtils.isEmpty(Login.userId)){userName = Login.userId;}else if(!TextUtils.isEmpty(MyApplication.getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE).getString("USER_ID", ""))){userName = MyApplication.getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE).getString("USER_ID", "");}
        String url = "http://"+AREAPARTY_NET+"/AreaParty/LogoutVip?userName="+ userName+"&userMac="+ Login.getAdresseMAC(MyApplication.getContext())+"&vipType="+type;
        Log.w("StartActivity",url);

        Util.setRecord(MyApplication.getContext(),"null");
        logined = "null";
        if (type.equals("tencent")){
            Util.clearRecordId(MyApplication.getContext());
        }

        OkHttpUtils.getInstance().setUrl(url).buildNormal().execute(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }





    private void initViews() {

        autoLoginHelper = (TextView) findViewById(R.id.autoLogin_help);  autoLoginHelper.setOnClickListener(this);
        floatViewTV = (TextView) findViewById(R.id.floatViewTV); floatViewTV.setOnClickListener(this);floatViewTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        autoLoginServiceTV = (TextView) findViewById(R.id.autoLoginServiceTV); autoLoginServiceTV.setOnClickListener(this);autoLoginServiceTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        btn_login_iqiyi = (FButton) findViewById(R.id.btn_login_iqiyi);  btn_login_iqiyi.setOnClickListener(this);
        btn_logout_iqiyi = (FButton) findViewById(R.id.btn_logout_iqiyi);    btn_logout_iqiyi.setOnClickListener(this);
        btn_login_youku = (FButton) findViewById(R.id.btn_login_youku);  btn_login_youku.setOnClickListener(this);
        btn_logout_youku = (FButton) findViewById(R.id.btn_logout_youku);    btn_logout_youku.setOnClickListener(this);
        btn_login_tencent = (FButton) findViewById(R.id.btn_login_tencent);  btn_login_tencent.setOnClickListener(this);
        btn_logout_tencent = (FButton) findViewById(R.id.btn_logout_tencent);    btn_logout_tencent.setOnClickListener(this);
        btn_login_leshi = (FButton) findViewById(R.id.btn_login_leshi);  btn_login_leshi.setOnClickListener(this);
        btn_logout_leshi = (FButton) findViewById(R.id.btn_logout_leshi);    btn_logout_leshi.setOnClickListener(this);
        tagIqiyi = (ImageView) findViewById(R.id.tag_iqiyi);
        tagTencent = (ImageView) findViewById(R.id.tag_tencent);
        tagYouku = (ImageView) findViewById(R.id.tag_youku);
        tagLeshi = (ImageView) findViewById(R.id.tag_leshi);

        findViewById(R.id.img_tencent).setOnClickListener(this);
        findViewById(R.id.img_leshi).setOnClickListener(this);
        findViewById(R.id.img_youku).setOnClickListener(this);

    }



    public  void getWebSiteUrl(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://"+ IPAddressConst.statisticServer_ip+"/bt_website/get_data.json").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                response.close();
                //Log.w("getWebSiteUrl",responseData);
                try {
                    JSONArray jsonArray = new JSONArray(responseData);
                    StartActivity.url = new String[5];
                    StartActivity.imageUrl = new String[5];
                    StartActivity.type = new int[5];
                    for (int i =0 ; i< jsonArray.length() ; i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        StartActivity.url[i] = jsonObject.getString("url");
                        StartActivity.imageUrl[i] = "http://"+ IPAddressConst.statisticServer_ip+"/bt_website/"+jsonObject.getString("image");
                        StartActivity.type[i] = jsonObject.getInt("type");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initWebSite();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void initWebSite(){
        for (int i = 0; i< 5; i++){
            Glide.with(this).load(imageUrl[i]).into(image[i]);
            btn[i].setText(url[i].replace("http://",""));
            image[i].setOnClickListener(this);
            btn[i].setOnClickListener(this);
        }
    }

    private void initWebView(){
        builder = new OkHttpClient.Builder();
        persistentCookieStore = new PersistentCookieStore(getApplicationContext());
        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
        builder.cookieJar(cookieJarImpl);
        client=new OkHttpClient();


        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setBlockNetworkImage(false);
        webView.getSettings().setBlockNetworkLoads(false);

        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                super.onPageFinished(view, url);
                if (j == 0) {
                    if (i == 1) {

                        view.evaluateJavascript("javascript:document.getElementById('scform_srchtxt').value='" + content + "'", null);
                        view.evaluateJavascript("javascript:document.getElementById('scform_submit').click()", null);
                    }
                    i++;
                    if (i == 4) {
                        Log.w("OKHTTP longbaidu", view.getUrl());
                        resultUrl[1] = view.getUrl();
                        view.evaluateJavascript("javascript:document.getElementsByClassName('thread_tit')[0].innerHTML;", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                                String count = s.substring(s.indexOf("容 ") + 2, s.indexOf(" 个"));
                                Log.w("OKHTTP longbaidu", "**" + count + "**");
                                showSearchCount(1,count);
                                d();
                            }
                        });
                    }
                }
                else if (j == 1) {
                    if (i == 1) {

                        view.evaluateJavascript("javascript:document.getElementById('scform_srchtxt').value='" + content + "'", null);
                        view.evaluateJavascript("javascript:document.getElementById('scform_submit').click()", null);
                    }
                    i++;
                    if (i == 4) {
                        Log.w("OKHTTP 87lou", view.getUrl());
                        resultUrl[2] = view.getUrl();
                        view.evaluateJavascript("javascript:document.getElementsByClassName('mbn')[0].innerHTML;", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                                String count = s.substring(s.indexOf("容 ") + 2, s.indexOf(" 个"));
                                Log.w("OKHTTP 87lou", "**" + count + "**");
                                showSearchCount(2,count);
                                closeWebView();
                            }
                        });
                    }
                }
                else if (j == 2) {
                    if (i == 1) {
                        view.evaluateJavascript("javascript:document.getElementById('scform_srchtxt').value='" + content + "'", null);
                        view.evaluateJavascript("javascript:document.getElementById('scform_submit').click()", null);
                    }
                    i++;
                    if (i == 4) {
                        resultUrl[3] = view.getUrl();
                        Log.w("OKHTTP hdchd", view.getUrl());
                        view.evaluateJavascript("javascript:document.getElementsByClassName('thread_tit')[0].innerHTML;", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                                String count = s.substring(s.indexOf("容 ") + 2, s.indexOf(" 个"));
                                Log.w("OKHTTP hdchd", "**" + count + "**");
                                showSearchCount(3,count);
                                e();
                            }
                        });
                    }
                }
                else if (j == 3) {
                    if (i == 1) {
                        view.evaluateJavascript("javascript:document.getElementById('scform_srchtxt').value='" + content + "'", null);
                        view.evaluateJavascript("javascript:document.getElementById('scform_submit').click()", null);
                    }
                    i++;
                    if (i == 4) {
                        Log.w("OKHTTP vkgq", view.getUrl());
                        resultUrl[4] = view.getUrl();
                        view.evaluateJavascript("javascript:document.getElementsByClassName('mbn')[0].innerHTML;", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                                String count = s.substring(s.indexOf("容 ") + 2, s.indexOf(" 个"));
                                Log.w("OKHTTP vkgq", "**" + count + "**");
                                showSearchCount(4,count);
                                c();
                            }
                        });
                    }
                }
            }

        });
        webView.setWebChromeClient(new WebChromeClient() {


            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {

            }


            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                super.onProgressChanged(view, newProgress);
            }
        });
    }

    private void doSearch() {
        closeSearch();
        initWebView();

        count = 0;
        search1(content);
        b();
    }

    public void b(){
        //content = editText.getText().toString();
        i = 1;
        String searchUrl = "http://www.longbaidu.com/search.php?mod=forum&mobile=2";
        search(0,searchUrl);

    }

    public void c(){
        //content = editText.getText().toString();
        i = 1;
        String searchUrl = "http://www.87lou.com/search.php?mobile=no";
        search(1,searchUrl);

    }

    public void d(){
        //content = editText.getText().toString();
        i = 1;
        String searchUrl = "http://www.hdchd.cc/search.php?mod=forum&mobile=2";
        search(2,searchUrl);
    }
    public void e(){
        //content = editText.getText().toString();
        i = 1;
        String searchUrl = "http://www.vkugq.com/search.php?mod=forum";
        search(3,searchUrl);
    }
    private void search(int j, String searchUrl){
        this.j = j;
        webView.loadUrl(searchUrl);

    }
    private void search1(final String content){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://zhannei.baidu.com/cse/search?q=" + content +"&click=1&s=3055704869350336262&nsid=";
                resultUrl[0] = url;
                Log.w("OKHTTP 大洋岛",url);
                /*http://zhannei.baidu.com/cse/search?q=%E9%87%91%E5%88%9A&click=1&s=3055704869350336262&nsid=*/
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //e.printStackTrace();
                        //Log.w("OKHTTTP 大洋岛","出错");
                        if (count<5){
                            count++;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            search1(content);
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();

                        Document document = Jsoup.parse(responseData);
                        Element element = document.getElementsByClass("support-text-top").first();
                        if (element != null){
                            String regEx="[^0-9]";
                            Pattern p = Pattern.compile(regEx);
                            Matcher m = p.matcher(element.html());
                            final String count = m.replaceAll("").trim();
                            Log.w("OKHTTP 大洋岛搜索个数", count);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showSearchCount(0,count);
                                }
                            });
                        }else {
                            Log.w("OKHTTP 大洋岛搜索个数","element is null");
                        }
                    }
                });
            }
        }).start();
    }

    private void showSearchCount(int i,String count){
        searchCounts[i].setText(count);
        searchCounts[i].setVisibility(View.VISIBLE);
    }

    private void closeSearch(){
        for (int i = 0; i<=4; i++){
            searchCounts[i].setVisibility(View.GONE);
        }
    }

    private void closeWebView(){
        if (webView!=null){
            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearHistory();
            webView.clearView();
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }

    class MyCount extends CountDownTimer {
        private long second;

        /**
         * MyCount的构造方法
         *
         * @param millisInFuture    要倒计时的时间
         * @param countDownInterval 时间间隔
         */
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            wait = true;
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onTick(long millisUntilFinished) {//在进行倒计时的时候执行的操作
            second = millisUntilFinished / 1000;
        }

        @Override
        public void onFinish() {//倒计时结束后要做的事情
            // TODO Auto-generated method stub
            wait = false;
        }

        public long getSecond() {
            return second;
        }
    }

}
