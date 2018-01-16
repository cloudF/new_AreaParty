package com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome;

/**
 * Created by ervincm on 2017/5/21.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome.store.PersistentCookieStore;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class WelcomeActivity extends Activity {
    private SharedPreferences sharedPreferences;
    OkHttpClient okHttpClient;
    okhttp3.OkHttpClient.Builder builder;
    Handler handler;
    //   public String imagehash1;
    private PersistentCookieStore persistentCookieStore;

    public static WelcomeActivity instance=null;//该变量用于共享数据变量，在oncreat中赋值了

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.hdhome_welcome_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //cookie
        builder = new OkHttpClient.Builder();
        persistentCookieStore = new PersistentCookieStore(getApplicationContext());
        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
        builder.cookieJar(cookieJarImpl);

//       builder.cookieJar(new CookieJar() {
//
//    	   private final HashMap<String, List<Cookie>> cookieStore=new HashMap<String, List<Cookie>>();
//		@Override
//		public void saveFromResponse(HttpUrl arg0, List<Cookie> arg1) {
//			// TODO Auto-generated method stub
//			cookieStore.put(arg0.host(), arg1);
//		}
//
//		@Override
//		public List<Cookie> loadForRequest(HttpUrl arg0) {
//			// TODO Auto-generated method stub
//			List<Cookie> cookies=cookieStore.get(arg0.host());
//			return cookies!=null?cookies:new ArrayList<Cookie>();
//		}
//	});
//

        okHttpClient=builder.build();
        instance=this;

        Handler handler = new Handler();
        sharedPreferences = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("AUTO_ISCHECK", false)) { //自动登录
            //当计时结束,跳转至主界面
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    Request request1=new Request.Builder().url("http://hdhome.org/torrents.php").build();
                    WelcomeActivity.instance.okHttpClient.newCall(request1).enqueue(new Callback() {

                        @Override
                        public void onResponse(Call arg0, Response arg1) throws IOException {
                            // TODO Auto-generated method stub
                            String responseData1=arg1.body().string();
                            arg1.close();
                            Intent intent=new Intent();
                            intent.putExtra("extra", responseData1);
                            intent.setClass(WelcomeActivity.this, IndexMainActivity.class);
                            startActivity(intent);
                            WelcomeActivity.this.finish();
                        }

                        @Override
                        public void onFailure(Call arg0, IOException arg1) {

                            // TODO Auto-generated method stub

                        }
                    });



                }
            }, 100);

        }
        else{
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Intent intent=new Intent(WelcomeActivity.this,LoginActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }
            }, 100);
        }
    }



}


