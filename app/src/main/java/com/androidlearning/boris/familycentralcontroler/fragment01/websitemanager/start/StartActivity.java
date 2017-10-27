package com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.start;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.androidlearning.boris.familycentralcontroler.R;
import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.hdhome.WelcomeActivity;
import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.web1080.MainActivity;
import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.web1080.RemoteDownloadActivity;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{
    //图片按钮
    private ImageButton web1080;
    private ImageButton blufans;
    private ImageButton hdhome;
    private ImageButton hdchd;
    //链接按钮
    private Button buttonWeb1080;
    private Button buttonBlufans;
    private Button buttonHdhome;
    private Button buttonHdchd;
    //下载管理
    private Button downloadManagement;

    //网站地址
    private String urlBlufans="http://www.chdchd.com";
    private String urlWeb1080="http://www.1080.net";
    private String urlHdchd="http://www.hdchd.cc";
    private String urlXunleicun="http://www.webmanager_xunleicun.com/misc.php?mod=mobile";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.websitemanager_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView();



        //获取权限


        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        //绑定监听
        web1080.setOnClickListener(this);
        blufans.setOnClickListener(this);
        hdhome.setOnClickListener(this);
        hdchd.setOnClickListener(this);

        buttonHdhome.setOnClickListener(this);
        buttonBlufans.setOnClickListener(this);
        buttonWeb1080.setOnClickListener(this);
        buttonHdchd.setOnClickListener(this);

        downloadManagement.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.web1080:
           case R.id.ButtonWeb1080:
               Intent intent=new Intent(StartActivity.this, MainActivity.class);
               intent.putExtra("URL",urlWeb1080);
               startActivity(intent);
               break;
           case R.id.blufans:
           case R.id.ButtonBlufans:
               Intent intent1=new Intent(StartActivity.this, MainActivity.class);
               intent1.putExtra("URL",urlBlufans);
               startActivity(intent1);
               break;
           case R.id.hdchd:
           case R.id.ButtonHdchd:
               Intent intent2=new Intent(StartActivity.this, MainActivity.class);
               intent2.putExtra("URL",urlHdchd);
               startActivity(intent2);
               break;
           case R.id.hdhome:
           case R.id.ButtonHdhome:
               Intent intent3=new Intent(StartActivity.this, WelcomeActivity.class);
               startActivity(intent3);
               break;
           case R.id.downloadManagement:
               Intent intent4=new Intent(StartActivity.this, RemoteDownloadActivity.class);
               startActivity(intent4);
               break;
           default:
               break;
       }
    }

    private long exitTime = 0;



    private  void initView(){
        web1080=(ImageButton)findViewById(R.id.web1080);
        blufans=(ImageButton)findViewById(R.id.blufans);
        hdhome=(ImageButton)findViewById(R.id.hdhome);
        hdchd=(ImageButton)findViewById(R.id.hdchd);

        buttonWeb1080=(Button)findViewById(R.id.ButtonWeb1080);
        buttonBlufans=(Button)findViewById(R.id.ButtonBlufans);
        buttonHdhome=(Button)findViewById(R.id.ButtonHdhome);
        buttonHdchd=(Button)findViewById(R.id.ButtonHdchd) ;

        downloadManagement=(Button)findViewById(R.id.downloadManagement);

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
