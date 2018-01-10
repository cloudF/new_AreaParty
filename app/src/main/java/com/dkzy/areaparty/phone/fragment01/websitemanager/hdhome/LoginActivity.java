package com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome.store.PersistentCookieStore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LoginActivity extends Activity {
	
	public static LoginActivity instance=null;//该变量用于共享数据变量，在oncreat中赋值了


	private static final String TAG = "用户登录";
	private EditText username;
    private EditText password;
    private EditText checkCode;
    private CheckBox autoLogin;
    private SharedPreferences sharedPreferences;
    private String message;
    private ImageView checkCodePicture;
    private OkHttpClient client;
    public String imagehash;
    private PersistentCookieStore persistentCookieStore;

     Handler handler;

    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      //  MyApplication.getInstance().addActivity(this);  
          
        //handler
        handler = new Handler() {
			  
	        public void handleMessage(Message msg) {
	            switch (msg.what) {  
	            case 1:  
	                //在这里可以进行UI操作  
	                //对msg.obj进行String强制转换  
	                String string=(String)msg.obj;
	                Toast.makeText(getBaseContext(), string, Toast.LENGTH_SHORT).show();
	                break;  
	            case 2:
	            	imagehash=(String)msg.obj;
	            	 Editor editor = sharedPreferences.edit();
	                 editor.putString("imagehash", imagehash);  
	                 editor.commit();  
	            	break;
	            
	            default:  
	                break;  
	            }  
	        }  
	  
	    };
       
       
	
        
        sharedPreferences = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
  //判断是否选择自动登录
        if (sharedPreferences.getBoolean("AUTO_ISCHECK", false)) { //自动登录
        	
        	 Request request2=new Request.Builder().url("http://hdhome.org/torrents.php").build();
			  WelcomeActivity.instance.okHttpClient.newCall(request2).enqueue(new Callback() {
				
				@Override
				public void onResponse(Call arg0, Response arg1) throws IOException {
					// TODO Auto-generated method stub
					String responseData1=arg1.body().string();
					Intent intent=new Intent();
					intent.putExtra("extra", responseData1);
					  intent.setClass(LoginActivity.this,IndexMainActivity.class);
					  startActivity(intent);
					  LoginActivity.this.finish();
				}
				
				@Override
				public void onFailure(Call arg0, IOException arg1) {
					// TODO Auto-generated method stub
					
				}
			});
              
        } else {  
            setContentView(R.layout.hdhome_login_activity);
            initView();  
            logon();
            loadingCaptchaPic();
            
            
            
          username = (EditText) findViewById(R.id.accountEdittext);
          password = (EditText) findViewById(R.id.pwdEdittext);
          Button btn=(Button) findViewById(R.id.login_in);
       	 btn.setOnClickListener(new OnClickListener() {
             
             @Override
             public void onClick(View v) {
          
               
            
             	userLogin();
             	
             	
             }
         });
        }  
      
        instance=this;
        
   
  
    }  
   
  
    /** 
     * 初始化视图控�?
     */  
    public void initView() {  
       
  
        username = (EditText) findViewById(R.id.accountEdittext);
        password = (EditText) findViewById(R.id.pwdEdittext);
        autoLogin = (CheckBox) findViewById(R.id.checkBox1);
        checkCode=(EditText) findViewById(R.id.checkCode);
        checkCodePicture=(ImageView) findViewById(R.id.checkCode_picture);
        // 默认记住用户�? 
        username.setText(sharedPreferences.getString("userName", ""));  
  
    }  
  
    /** 
     * 点击登录按钮时触发的方法 
     */  
    public void userLogin() {  
        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();
        String checkCodeString=checkCode.getText().toString();
        
      
       
        //post参数
        FormBody formBody=new FormBody.Builder().add("username", usernameString)
        		                       .add("password", passwordString)
        		                       .add("imagestring", checkCodeString)
        		                       .add("imagehash", imagehash)
        		                       .build();
       
        		                       
        Request request=new Request.Builder().post(formBody)
        		                     .url("http://hdhome.org/takelogin.php").build();   
      
        WelcomeActivity.instance.okHttpClient.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				 String responseData = arg1.body().string();
				 //netWorkResponse返回的响应内容包含url
				 String networkResponseData=arg1.networkResponse().toString();
				 Log.i("tag", networkResponseData);
				 if(networkResponseData.contains("index.php"))
				 {
					  //得到torrents。php页面HTML
					  Request request1=new Request.Builder().url("http://hdhome.org/torrents.php").build();
					  WelcomeActivity.instance.okHttpClient.newCall(request1).enqueue(new Callback() {
						
						@Override
						public void onResponse(Call arg0, Response arg1) throws IOException {
							// TODO Auto-generated method stub
							String responseData1=arg1.body().string();
							Intent intent=new Intent();
							intent.putExtra("extra", responseData1);
							  intent.setClass(LoginActivity.this,IndexMainActivity.class);
							  startActivity(intent);
							  LoginActivity.this.finish();
						}
						
						@Override
						public void onFailure(Call arg0, IOException arg1) {
							// TODO Auto-generated method stub
							
						}
					});
					  
					  //记录选择自动登录选项
				       Editor editor = sharedPreferences.edit();
			            if (autoLogin.isChecked()) {// 自动登录  
			                editor.putBoolean("AUTO_ISCHECK", true).commit();  
			            }  
			            editor.commit(); 
				 }
				 else{
					 password.post(new Runnable() {
                         @Override
                         public void run() {
                             password.setText("");
                         }
                     });
                     checkCode.post(new Runnable() {
                         @Override
                         public void run() {
                             checkCode.setText("");
                         }
                     });
					  Message message1=new Message();
					  message1.obj="输入错误,请重新输入";
					  message1.what=1;
					  handler.sendMessage(message1);

                     //重新加载验证码
                     loadingCaptchaPic();

				 }
				 
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				
			}
		});


        //记住用户�?
        Editor editor = sharedPreferences.edit();
        editor.putString("userName", usernameString);  
        editor.putString("password", passwordString);  
        editor.putString("checkCode", checkCodeString);  
        editor.commit();  


  
//        if (validateUser(usernameString, passwordString)) {  
//  
//            Editor editor = sharedPreferences.edit();  
//            editor.putString("userName", usernameString);  
//              
//            if (autoLogin.isChecked()) {// 自动登录  
//                editor.putString("password", passwordString);  
//                editor.putBoolean("AUTO_ISCHECK", true).commit();  
//            }  
//            editor.commit();  
//              
//            Intent intent = new Intent();  
//            intent.setClass(LoginActivity.this, SubTitleUtil.class);
//            startActivity(intent);  
//        } else {  
//            //alert(this, message);  
//        	 Intent intent = new Intent();  
//             intent.setClass(LoginActivity.this, LoginActivity.class);  
//             startActivity(intent); 
//             Editor editor = sharedPreferences.edit();  
//             editor.putString("userName", usernameString);  
//             editor.commit();  
//        }  
    }  


    public boolean validateUser(String username1, String password1) {
  
//    	 String username_correct="admin";
//		 String password_correct="123456";
        Boolean flag = false;
        try {  
            //...此处为调用web服务，验证用户名密码的服务，特此省略 
        	if("admin".equals(username.getText().toString())&&"123456".equals(password.getText().toString()))
                flag = true;  
        } catch (Exception e) {
            // TODO Auto-generated catch block  
            Log.e(TAG, e.getMessage());
            
        }  
  
        return flag;  
    }  
  
   
    public void loadingCaptchaPic() {
    	
    	
    	
        new Thread(new Runnable() {
            String src;
            Bitmap captchaPic;
            
            @Override
            public void run() {
                try{
                	 client=new OkHttpClient();
                	
                    Request request = new Request.Builder()
                            .url("http://hdhome.org/login.php")
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Document parse = Jsoup.parse(responseData);
                    Elements select = parse.select("img[alt=CAPTCHA]");
                    Element element = select.get(0);
                    src = element.attr("src");
                    //imagehash传到主线�?
                   String imagehash = src.substring(36);
                    Message message=new Message();
                    message.obj=imagehash;
                    message.what=2;
                    handler.sendMessage(message);
                   

                    request = new Request.Builder()
                            .url("http://hdhome.org/"+src)
                            .build();
                    response = client.newCall(request).execute();
                    InputStream is = response.body().byteStream();

                    captchaPic = BitmapFactory.decodeStream(is);

//                    textView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            textView.setText(src+" "+imagehash);
//                        }
//                    });

                    //post的方式会把子线程的内容提交�?主线程中，主线程再加载，从�?达到操作UI效果，若在此用SetText，则会报错�?
                    checkCodePicture.post(new Runnable() {
                        @Override
                        public void run() {
                            checkCodePicture.setImageBitmap(captchaPic);
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    public void logon() {
    	Log.i("wwww", "123");
    	persistentCookieStore = new PersistentCookieStore(getApplicationContext());
        persistentCookieStore.removeAll();
    }



}


