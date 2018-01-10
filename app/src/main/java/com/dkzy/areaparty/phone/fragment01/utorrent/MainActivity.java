package com.dkzy.areaparty.phone.fragment01.utorrent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.utorrent.utils.OkHttpUtils;
import com.dkzy.areaparty.phone.fragment01.utorrent.utils.UrlUtils;
import com.dkzy.areaparty.phone.utils_comman.PreferenceUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private String author;
    private String ip_port;
    private String token;

    private String ip;
    private String port;
    private String username;
    private String password;

    @BindView(R.id.text)
    TextView textView;
    @BindView(R.id.ip)
    EditText ip_editText;
    @BindView(R.id.port)
    EditText port_editText;
    @BindView(R.id.username)
    EditText username_editText;
    @BindView(R.id.password)
    EditText password_editText;
    @BindView(R.id.login)
    Button login_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.utorrent_activity_main);
        ButterKnife.bind(this);


        ip = new PreferenceUtil("utorrentPrefs",getApplicationContext()).read("ip");
        port = new PreferenceUtil("utorrentPrefs",getApplicationContext()).read("port");
        username = new PreferenceUtil("utorrentPrefs",getApplicationContext()).read("username");
        password = new PreferenceUtil("utorrentPrefs",getApplicationContext()).read("password");

        ip_editText.setText(ip);
        port_editText.setText(port);
        username_editText.setText(username);
        password_editText.setText(password);
    }
    @OnClick(R.id.login)
    public void login(){
        //Toasty.info(getApplicationContext(),"login").show();
        ip = ip_editText.getText().toString();
        port = port_editText.getText().toString();
        username = username_editText.getText().toString();
        password = password_editText.getText().toString();

        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port) || TextUtils.isEmpty(username)){
            Toasty.error(getApplicationContext(),"请完善信息").show();
            return;
        }
        try{
            String username_password = username + ":" + password;
            author = "Basic "+ Base64.encodeToString(username_password.getBytes("utf-8") , Base64.NO_WRAP);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(author)){
            Toasty.error(getApplicationContext(),"输入信息错误").show();
            return;
        }
        ip_port = ip + ":" + port;
        String url = "http://" + ip_port + "/gui/token.html?t=" + String.valueOf(new Date().getTime());
        OkHttpUtils.getInstance().setAuthorization(author).setUrl(url).build()
                .execute(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.w("SubTitleUtil", "onFailure");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.error(getApplicationContext(),"请检查ip地址和端口号是否正确").show();
                            }
                        });
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
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("ip",ip);
                                    map.put("port",port);
                                    map.put("username",username);
                                    map.put("password",password);
                                    new PreferenceUtil("utorrentPrefs",getApplicationContext()).writeAll(map);
                                    token = responseData.substring(44,108);

                                    Intent intent = new Intent(MainActivity.this, UTorrentActivity.class);
                                    OkHttpUtils.authorization = author;
                                    UrlUtils.ip_port = ip_port;
                                    UrlUtils.token = token;
//                                    intent.putExtra("author",author);
//                                    intent.putExtra("ip_port",ip_port);
//                                    intent.putExtra("token",token);
                                    startActivity(intent);
                                }
                            }
                        }

                    }
                });

    }
}
