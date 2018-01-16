package com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome.object.TorrentFile;
import com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome.object.TorrentFileAdapter;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.callback.OnTorrentFileItemListener;
import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class IndexMainActivity extends Activity {

	private ViewPager mViewPager;// 用来放置界面切换
	private PagerAdapter mPagerAdapter;// 初始化View适配器
	private List<View> mViews = new ArrayList<View>();// 用来存放Tab01-04
	// 四个Tab，每个Tab包含一个按钮
	private LinearLayout mTabMovieSeed;
	private LinearLayout mTabDownload;
	private LinearLayout mTabForum;
	private LinearLayout mTabMe;
	// 四个按钮
	private ImageButton mMovieSeedImg;
	private ImageButton mDownloadImg;
	private ImageButton mForumImg;
	private ImageButton mMeImg;
	//4个按钮下文字
	private TextView mDownloadTv;
	private TextView mMovieSeedTv;
	private TextView mMeTv;
	private TextView mForumTv;
	
	//搜索按钮
	private Button SearchButton;
	
	
	private static Document doc;//接收主页的HTML
	private Button logout; //注销
	private static ArrayList<HashMap<String, Object>> listItem=null;//数据结构
	private static ArrayList<String> listUrl;
	private ListView mListView;
	SimpleAdapter listItemAdapter;
	private int mScrollState;
	private int count=0;//列表数量
	SearchView searchView;
	ArrayList<HashMap<String, String>> listItemSearch;
	private static Integer i = 0;
	private static int m= 0;
	private static Handler handler;
	private static boolean isLoading;
	private OkHttpClient client;
	private LinearLayout loadingLayout;
	private View loading_Layout;
	private TextView loadingLayoutText;

	//downloadPage页面变量
	private TorrentFileAdapter adapter;
	private List<TorrentFile> torrentList = new ArrayList<>();

	private RecyclerView recyclerView;
	private SwipeRefreshLayout swipeRefresh;



	//MePage页面变量
	private TextView usernameTV;
	private TextView meilizhiTV;
	private TextView fenxianglvTV;
	private TextView shangchuanliangTV;
	private TextView xiazailiangTV;
	private ImageView mTab1= null;
	private ImageView mTab2= null;
	private ImageView mTab3= null;
	@SuppressWarnings("unused")
	private Button unlogin_button;
	private Button qiandao_button;


	private Integer qiandao=0;


	private SharedPreferences sharedPreferences;
	private SharedPreferences sharedPreferences1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//隐藏最上面的电池电量及信号栏（全屏）： 
//		getWindow().setFlags(WindowManager.LayoutParams.TYPE_STATUS_BAR, WindowManager.LayoutParams.TYPE_STATUS_BAR); 
		setContentView(R.layout.hdhome_main_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		/**
		 ViewPage加载，并设置滑动，点击监听事件
		 */
		initView();
		initViewPage();
		initEvent();
		
		 for (int j = 0; j < 10; j++) {
	            Thread th = new Thread() {
	                @Override
	                public void run() {
	                    synchronized (i) {//互斥访问资源，资源同步锁
	                        System.out.println(this.getName() + ":" + i);
	                        i++;

	                    }
	                }
	            };
	            th.start();
	        }
		
		//获得响应消息（HTML）
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		String StringE=bundle.getString("extra");
		doc=Jsoup.parse(StringE);
//		if(StringE.length()==0)
//		{
//			 Request request1=new Request.Builder().url("http://hdhome.org/torrents.php").build();
//			  WelcomeActivity.instance.okHttpClient.newCall(request1).enqueue(new Callback() {
//
//				@Override
//				public void onResponse(Call arg0, Response arg1) throws IOException {
//					// TODO Auto-generated method stub
//					String responseData1=arg1.body().string();
//					doc=Jsoup.parse(responseData1);
//				}
//
//				@Override
//				public void onFailure(Call arg0, IOException arg1) {
//
//					// TODO Auto-generated method stub
//
//				}
//			});
//		}
//		else{
//			 doc=Jsoup.parse(StringE);
//		}
	   

	    
		//显示用户名
	    showUsername();


		//电影列表显示
		showMovieList();
		
		//设置监听函数，跳到搜索Activity
		SearchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(IndexMainActivity.this, SearchActivity.class);
				startActivity(intent);
			}
		});
		
     	//在这里可以进行UI操作  ,设置电影视图
		listItemAdapter=new SimpleAdapter(getBaseContext(), //获得适配器
				listItem, R.layout.hdhome_detail_item,new String[]{"ImageUrl","ChineseName","OtherName","Type","CountryTimeSize"}, new int[]{R.id.imageView_Movie,R.id.ChineseMovieName,R.id.EnglishMovieName,R.id.MovieType,
				R.id.movieCountryTimeSize});

   		mListView.setAdapter(listItemAdapter);//设置适配器
		
		 
	}

	@SuppressLint("HandlerLeak")
	private void showMovieList() {
		// TODO Auto-generated method stub
		listItem=new ArrayList<HashMap<String, Object>>();
		listUrl=new ArrayList<String>();
		lodaData(0);//加载数据
		
		  handler = new Handler() {
		        
				@SuppressWarnings("unchecked")
				public void handleMessage(Message msg) {
					
		            switch (msg.what%10) { 


//			        		//动态更新ListView，因为
//			        //		listItemAdapter.notifyDataSetChanged();
//			        		 mListView.invalidateViews(); 
//		            	 break;
		            	
//		            case 1:
//		                //在这里可以进行UI操作
//		        		listItemAdapter=new SimpleAdapter(getBaseContext(), //获得适配器
//		        		listItem, R.layout.hdhome_detail_item,new String[]{"Image","ChineseName","OtherName","Type","CountryTimeSize"}, new int[]{R.id.imageView_Movie,R.id.ChineseMovieName,R.id.EnglishMovieName,R.id.MovieType,
//		        			R.id.movieCountryTimeSize});
//		        		//实现ViewBinder()接口，显示Bitmap图片
//		        		listItemAdapter.setViewBinder(new ViewBinder() {
//
//							@Override
//							public boolean setViewValue(View view, Object data,
//									String textRepresentation) {
//								// TODO Auto-generated method stub
//								if(view instanceof ImageView&& data instanceof Bitmap){//instanceof判断
//									ImageView i=(ImageView) view;
//									i.setImageBitmap((Bitmap)data);
//									return true;
//								}
//								return false;
//
//							}
//						});
		            case 1:
		                //在这里可以进行UI操作
		        		listItemAdapter=new SimpleAdapter(getBaseContext(), //获得适配器
		        		listItem, R.layout.hdhome_detail_item,new String[]{"ImageUrl","ChineseName","OtherName","Type","CountryTimeSize"}, new int[]{R.id.imageView_Movie,R.id.ChineseMovieName,R.id.EnglishMovieName,R.id.MovieType,
		        			R.id.movieCountryTimeSize});
		        		//实现ViewBinder()接口，显示Bitmap图片
		        		listItemAdapter.setViewBinder(new ViewBinder() {

							@Override
							public boolean setViewValue(View view, Object data,
                                                        String textRepresentation) {
								// TODO Auto-generated method stub
								if(view instanceof ImageView){//instanceof判断
									if(!data.equals("default")){//不使用默认图片，加载网络图片
										ImageView i=(ImageView) view;
										String ImageUrl=(String)data;
										Glide.with(getBaseContext()).load(ImageUrl).into(i);
									}else {//使用默认图片

										ImageView i=(ImageView) view;

										//加载图片
										Glide.with(getBaseContext()).load(R.drawable.webmanager_tab_movieseed_pressed).into(i);
									}
									return true;
								}
								return false;

							}
						});

		        		mListView.setAdapter(listItemAdapter);//设置适配器
						listItemAdapter.notifyDataSetChanged();
						mListView.invalidateViews();
		        		break;
						case 3:
							HashMap<String, Object> map=(HashMap<String, Object>)msg.obj;
							listItem.add(map);
							listItemAdapter.notifyDataSetChanged();
							//去掉底部loading
							mListView.removeFooterView(loading_Layout);
//							mListView.invalidateViews();
							break;
						default:
		                break;  
		            }  
		        }  
		  
		    };

		
		
		 
	mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				 mScrollState = scrollState;
				 Log.i("count", String.valueOf(mScrollState));
				 
//				 if(scrollState==0){//当屏幕停止滚动时
//					 /**执行线程，睡眠5秒钟后添加10个列表项*/
//					 
//					 new Thread(){
//					
//						 private  Handler handler = new Handler() {
//					 
//				@Override
//					 public void handleMessage(Message msg) {
//                         super.handleMessage(msg);
//     					Toast.makeText(getBaseContext(), "123", Toast.LENGTH_LONG).show();
//     					listItem=lodaData(10);//加载数据
//     					Log.i("scrollname", listItem.get(0).toString());
//                         listItemAdapter.notifyDataSetChanged(); 
//                         } 
//				};
//					  @Override 
//					 public void run() { 
//						  super.run(); 
//						 try {
//							 sleep(1000); 
//							 handler.sendEmptyMessage(0); 
//							 } 
//						 catch (InterruptedException e) 
//						 {
//							 e.printStackTrace();
//						 }
//				 } 
//					  }.start();	
//				 }

				 
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				 // 可视的最后一个列表项的索引
				  int lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
				  //当列表正处于滑动状态且滑动到列表底部时，执行
				 if (mScrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE
				                      && lastVisibleItem == totalItemCount - 1&&!isLoading)
				   {
					 isLoading=true;
					 /**
					  * 下拉时的一个底部视图问题需要解决
					  */

					 //下拉时，睡眠2秒后更新
					 new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
//							try{
//								Thread.sleep(2000);
//							}
//							catch(InterruptedException e){
//								e.printStackTrace();
//							}
							//切换到UI线程执行
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									 mListView.addFooterView(loading_Layout);
									loadingLayoutText.setText("Loading...");
									 count+=10;
									 Log.i("counnt22", String.valueOf(count));

								     lodaData(count);
								}
							});
//							try{
//								Thread.sleep(1000);
//							}
//							catch(InterruptedException e){
//								e.printStackTrace();
//							}
//							//切换到UI线程执行
//							runOnUiThread(new Runnable() {
//
//								@Override
//								public void run() {
//									// TODO Auto-generated method stub
//									mListView.removeFooterView(loadingLayout);
//								}
//							});
						}
					}).start();



				   }



			}
		});



	//点击监听
		mListView.setOnItemClickListener((OnItemClickListener)new OnItemClickListener() {
				//设置监听事件
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
					// TODO 自动生成的方法存根
					//跳转到detials页面
					Intent intent=new Intent();
					intent.putExtra("DetailUrl",listUrl.get(position));
					intent.setClass(IndexMainActivity.this,DetailsActivity.class);
					startActivity(intent);

					//显示被单击选项的内容
				}
			});

	}

	private void lodaData(int count)
	{

		        final Elements elements3=doc.select("td[class=rowfollow][width=100%]");
		        Log.i("moviename1", elements3.get(0).text());
		        client=new OkHttpClient();

		        //每次加载最多10条item,最后一次可能小于10
				for( int i=count;i<count+10&&i<elements3.size();i++)//elements3.size()
				{
					Log.i("size", String.valueOf(elements3.size()));
					/**
					 * 加载国家信息
					 */
					//
					//得到电影对于URL
					String url=elements3.get(i).select("a[href]").get(0).attr("href");
					url="http://hdhome.org/"+url;

					listUrl.add(url);//把url保存起来，便于传给DetialsActivity中
					Request request = new Request.Builder().url(url).build();
					/**
					 * 多线程加载的时候，线程的顺序问题需要解决
					 */

					synchronized (this){

						WelcomeActivity.instance.okHttpClient.newCall(request).enqueue(new Callback() {

							@Override
							public void onResponse(Call arg0, Response arg1) throws IOException {
								// TODO Auto-generated method stub
								String responseData = arg1.body().string();
								arg1.close();
								Document parse = Jsoup.parse(responseData);
								Elements elements=parse.select("td.rowfollow");
								String str=elements.get(7).text();//取得介绍内容

								System.out.println(Thread.currentThread().getName() + ":" + m);
								HashMap<String, Object> map1=new HashMap<String,Object>();

								if(str.length()<10||str.indexOf("译　　名")==-1){//没有介绍内容or没有译名等信息/or没有类别
									Log.i("name", "123");
									Elements elements1=parse.getElementsByTag("h1");
									String name=elements1.get(0).text();
									map1.put("ChineseName", name);
									Log.i("name", name);
									map1.put("OtherName","null");
									map1.put("Type", "null");
									//大小
									String StrSize=elements.get(2).text();
									Log.i("sizenum", StrSize);
									int num=StrSize.indexOf("B");
									Log.i("sizenum", String.valueOf(num));
									String size=StrSize.substring(3, num);
									map1.put("CountryTimeSize", size);
								}
								else{//有介绍内容
									//译名
									Log.i("name", "456");
									if(str.indexOf("译　　名")!=-1&&str.indexOf("片　　名")!=-1){
										int num1=str.indexOf("译　　名");
										Log.i("num1", String.valueOf(num1));
										int num2=str.indexOf("片　　名");
										if(num1>num2){//防止顺序问题
											int a=num2;
											num2=num1;
											num1=a;
										}
										String ChineseName=str.substring(num1+5, num2-2);
										map1.put("ChineseName", ChineseName);
									}
									else{
										map1.put("ChineseName", "null");
									}

									//片名
									if(str.indexOf("年　　代")!=-1&&str.indexOf("片　　名")!=-1){
										int num1=str.indexOf("译　　名");
										int num3=str.indexOf("年　　代");
										int num2=str.indexOf("片　　名");
										if(num1>num2){//防止译名，片名顺序问题
											num2=num1;
										}
										String OtherName=str.substring(num2+5, num3-2);
										map1.put("OtherName", OtherName);
									}
									else{
										map1.put("OtherName", "null");
									}
									//类别
									if(str.indexOf("类　　别")!=-1&&str.indexOf("语　　言")!=-1){
										int num5=str.indexOf("类　　别");
										int num6=str.indexOf("语　　言");
										String type=str.substring(num5+5, num6-2);
										map1.put("Type", type);

									}
									else{
										map1.put("Type", "null");
									}
									String Country = null;
									String time = null;
									//国家
									if(str.indexOf("国　　家")!=-1&&str.indexOf("类　　别")!=-1){
										int num4=str.indexOf("国　　家");
										int num5=str.indexOf("类　　别");
										 Country=str.substring(num4+5, num5-2);
										Log.i("num5", String.valueOf(num5));
									}
									//时长
									if(str.indexOf("片　　长")!=-1&&str.indexOf("导　　演")!=-1){
										int num7=str.indexOf("片　　长");
										int num8=str.indexOf("导　　演");
									    time=str.substring(num7+5, num8-2);
									}

									//大小
									String StrSize=elements.get(2).text();
									Log.i("sizenum", StrSize);
									int num=StrSize.indexOf("B");
									String size=StrSize.substring(3, num);
									map1.put("CountryTimeSize", Country+"/"+time+"/"+size);
								}

								m+=1;

								System.out.println(Thread.currentThread().getName() + ":" + m);

								//加载图片资源
								if(elements.get(7).getElementsByTag("img").size()==0)//如果没有图片，加载默认图片
								{
								    map1.put("ImageUrl", "default");
								}else{
									String src=elements.get(7).getElementsByTag("img").get(0).attr("src");
									Log.i("src", src);
									map1.put("ImageUrl", src);
								}



//								if(elements.get(7).getElementsByTag("img").size()==0)//如果没有图片，加载默认图片
//								{
//									BitmapFactory.Options options=new BitmapFactory.Options();
//			                        options.inSampleSize=2;//图片高宽度都为原来的二分之一，即图片大小为原来的大小的四分之一  
//			                        options.inTempStorage=new byte[5*1024]; //设置16MB的临时存储空间（不过作用还没看出来，待验证） 
//								    Bitmap  bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.webmanager_tab_movieseed_pressed, options);
//								    map1.put("Image", bitmap);
//								}
//								else{
//									String src=elements.get(7).getElementsByTag("img").get(0).attr("src");
//									Log.i("src", src);
////									map1.put("ImageUrl", src);
//									Request request = new Request.Builder().url(src).build();
//
//			                        Response response = client.newCall(request).execute();
//			                        InputStream  is = response.body().byteStream();
//			                        BitmapFactory.Options options=new BitmapFactory.Options();
//				                    options.inSampleSize=4;//图片高宽度都为原来的二分之一，即图片大小为原来的大小的四分之一  
//				                    options.inTempStorage=new byte[5*1024]; //设置16MB的临时存储空间（不过作用还没看出来，待验证）  
//				                    Bitmap captchaPic = BitmapFactory.decodeStream(is, null, options);
//
//				                    map1.put("Image", captchaPic);
//								}



                              //请求的电影列表资源传到UI线程中，在UI线程添加数据
									Message message3=new Message();
									message3.what=3;
								    message3.obj=map1;
									handler.sendMessage(message3);



								if(m==(elements3.size()))//加载最后一条更新
								{
									Message message1=new Message();
									message1.what=1;
									handler.sendMessage(message1);
								}
								else if(m%10==0){
									isLoading=false;
									if(m==10){//第一次设置adapter
									Message message=new Message();
									message.what=1;
									handler.sendMessage(message);
									}
								}
							}

							@Override
							public void onFailure(Call arg0, IOException arg1) {
								// TODO Auto-generated method stub

							}
						});

					}


				}


	}




	private void showUsername() {
		// TODO Auto-generated method stub
		Elements elements=doc.getElementsByTag("b");
		Element element=elements.get(0);
		String name=element.text();//<b>name</b>得到标签内元素
		Log.i("name", name);
		TextView username=(TextView)findViewById(R.id.name);
		username.setText(name);
	}

	private void initEvent() {
		mTabMovieSeed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				click(v);
			}
		});
		mTabDownload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				click(v);
			}
		});
		mTabForum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				click(v);
			}
		});
		mTabMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				click(v);
			}
		});
		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            /**
            *ViewPage左右滑动时
            */
			@Override
			public void onPageSelected(int arg0) {
				int currentItem = mViewPager.getCurrentItem();
				switch (currentItem) {
				case 0:
					 resetImg();
					mMovieSeedImg.setBackgroundResource(R.drawable.webmanager_tab_movieseed_pressed);
					mMovieSeedTv.setTextColor(Color.parseColor("#4acbfa"));
					break;
				case 1:
					 resetImg();
					showPageDownload();
					mDownloadImg.setBackgroundResource(R.drawable.webmanager_tab_download_pressed);
					mDownloadTv.setTextColor(Color.parseColor("#4acbfa"));
					break;
				case 2:
					 resetImg();
					mForumImg.setBackgroundResource(R.drawable.webmanager_tab_forum_pressed);
					mForumTv.setTextColor(Color.parseColor("#4acbfa"));
					break;
				case 3:
					 resetImg();
					showPageMe();
					mMeImg.setBackgroundResource(R.drawable.webmanager_tab_me_pressed);
					mMeTv.setTextColor(Color.parseColor("#4acbfa"));
					break;
				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	/**
	 * 初始化设置
	 */
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.id_viewpage);
		// 初始化四个LinearLayout
		mTabMovieSeed = (LinearLayout) findViewById(R.id.id_tab_movieSeed);
		mTabDownload = (LinearLayout) findViewById(R.id.id_tab_download);
		mTabForum = (LinearLayout) findViewById(R.id.id_tab_forum);
		mTabMe = (LinearLayout) findViewById(R.id.id_tab_me);
		// 初始化四个按钮
		mMovieSeedImg = (ImageButton) findViewById(R.id.id_tab_movieSeed_img);
		mDownloadImg = (ImageButton) findViewById(R.id.id_tab_download_img);
		mForumImg = (ImageButton) findViewById(R.id.id_tab_forum_img);
		mMeImg = (ImageButton) findViewById(R.id.id_tab_me_img);
	    //初始化按钮下文字TV
		mDownloadTv=(TextView)findViewById(R.id.id_tab_download_tv);
		mMovieSeedTv=(TextView)findViewById(R.id.id_tab_movieSeed_tv);
		mMeTv=(TextView)findViewById(R.id.id_tab_me_tv);
		mForumTv=(TextView)findViewById(R.id.id_tab_forum_tv);
		//搜索按钮
		SearchButton=(Button)findViewById(R.id.top_search);
		//loadingLayout
		LayoutInflater mLayoutInflater = LayoutInflater.from(this);
		loading_Layout=mLayoutInflater.inflate(R.layout.hdhome_loading_layout, null);
		loadingLayout=(LinearLayout) loading_Layout;
		loadingLayoutText=(TextView) loading_Layout.findViewById(R.id.loadingLayoutText);

	}

	/**
	 * 初始化ViewPage
	 */
	private void initViewPage() {

		// 初妈化四个布局
		LayoutInflater mLayoutInflater = LayoutInflater.from(this);
		View tab01 = mLayoutInflater.inflate(R.layout.hdhome_movieseed_layout, null);
		//初始化listView
		mListView=(ListView) tab01.findViewById(R.id.listViewMovie);

		View tab02 = mLayoutInflater.inflate(R.layout.websitemanager_downloaded, null);
		//初始化控件

		swipeRefresh = (SwipeRefreshLayout) tab02.findViewById(R.id.swipe_refresh);
		swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

		recyclerView = (RecyclerView) tab02.findViewById(R.id.recycler_view_torrent_file);

		View tab03 = mLayoutInflater.inflate(R.layout.hdhome_forum_layout, null);
		View tab04 = mLayoutInflater.inflate(R.layout.hdhome_me_layout, null);
		//初始化控件
		usernameTV = (TextView) tab04.findViewById(R.id.username);
		meilizhiTV = (TextView) tab04.findViewById(R.id.meilizhi);
		fenxianglvTV = (TextView) tab04.findViewById(R.id.fenxianglv);
		shangchuanliangTV = (TextView) tab04.findViewById(R.id.shangchuanliang);
		xiazailiangTV = (TextView) tab04.findViewById(R.id.xiazailiang);

		mViews.add(tab01);
		mViews.add(tab02);
		mViews.add(tab03);
		mViews.add(tab04);

		// 适配器初始化并设置
		mPagerAdapter = new PagerAdapter() {

			@Override
			public void destroyItem(ViewGroup container, int position,
                                    Object object) {
				container.removeView(mViews.get(position));

			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View view = mViews.get(position);
				container.addView(view);
				return view;
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {

				return arg0 == arg1;
			}

			@Override
			public int getCount() {

				return mViews.size();
			}
		};
		mViewPager.setAdapter(mPagerAdapter);
	}

	/**
	 * 判断哪个要显示，及设置按钮图片
	 */

	public void click(View arg0) {

		switch (arg0.getId()) {
		case R.id.id_tab_movieSeed:
			mViewPager.setCurrentItem(0);
			resetImg();
			mMovieSeedImg.setBackgroundResource(R.drawable.webmanager_tab_movieseed_pressed);
			mMovieSeedTv.setTextColor(Color.parseColor("#4acbfa"));
			break;
		case R.id.id_tab_download:
			mViewPager.setCurrentItem(1);
			showPageDownload();
			resetImg();
			mDownloadImg.setBackgroundResource(R.drawable.webmanager_tab_download_pressed);
			mDownloadTv.setTextColor(Color.parseColor("#4acbfa"));
			break;
		case R.id.id_tab_forum:
			mViewPager.setCurrentItem(2);
			resetImg();
			mForumImg.setBackgroundResource(R.drawable.webmanager_tab_forum_pressed);
			mForumTv.setTextColor(Color.parseColor("#4acbfa"));
			break;
		case R.id.id_tab_me:
			mViewPager.setCurrentItem(3);
			showPageMe();
			resetImg();
			mMeImg.setBackgroundResource(R.drawable.webmanager_tab_me_pressed);
			mMeTv.setTextColor(Color.parseColor("#4acbfa"));
			break;
		default:
			break;
		}
	}

	/**
	 * 把所有图片变暗
	 */
	private void resetImg() {
		mMovieSeedImg.setBackgroundResource(R.drawable.webmanager_tab_movieseed_normal);
		mMovieSeedTv.setTextColor(Color.parseColor("#000000"));

		mDownloadImg.setBackgroundResource(R.drawable.webmanager_tab_download_normal);
		mDownloadTv.setTextColor(Color.parseColor("#000000"));
		mForumImg.setBackgroundResource(R.drawable.webmanager_tab_forum_normal);
		mForumTv.setTextColor(Color.parseColor("#000000"));
		mMeImg.setBackgroundResource(R.drawable.webmanager_tab_me_normal);
		mMeTv.setTextColor(Color.parseColor("#000000"));
	}


/*
* 这以下是实现显示download页面的函数*/
	private void showPageDownload(){

		//切换到页面时，设置设配器。
		LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		torrentList.clear();
		adapter = new TorrentFileAdapter(torrentList);
		recyclerView.setAdapter(adapter);
		//加载数据信息
		updateAdapter();
		setListener();


		swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				torrentList.clear();
				updateAdapter();
				swipeRefresh.setRefreshing(false);
			}
		});


	}



	public void updateAdapter(){
		String directory1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aeraparty/downloadedfiles/webmanager_download/pt种子";
		File file = new File(directory1);
		if(file != null){
			File[] files = file.listFiles(new FileNameSelector("torrent"));//FileNameSelector要实现Fi了那么Filter接口
			for(File f : files){
				torrentList.add(new TorrentFile(f.getName(), f.getAbsolutePath()));
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

	void setListener(){
		adapter.setOnTorrentFileListent(new OnTorrentFileItemListener() {
			@Override
			public void delete(int position) {
				final TorrentFile torrent = torrentList.get(position);
				AlertDialog.Builder dialog = new AlertDialog.Builder(IndexMainActivity.this);
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
				AlertDialog.Builder dialog = new AlertDialog.Builder(IndexMainActivity.this);
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



	/*
* 这以下是实现显示Me页面的函数*/
	private  void showPageMe(){

		//显示个人中心信息
		loadingTV();
		qiandao();

		//注销按钮功能
		unlogin_button= (Button) findViewById(R.id.button);
		unlogin_button.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				removeSharedPreference();

				Intent intent1 = new Intent();
				intent1.setClass(IndexMainActivity.this, WelcomeActivity.class);
				startActivity(intent1);
				IndexMainActivity.this.finish();
			}

		});
	}


	public void qiandao(){


		qiandao_button= (Button) findViewById(R.id.button2);
		qiandao_button.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(qiandao==0){
					new Thread(new Runnable() {
						@Override
						public void run() {
							try{
								Request request1 = new Request.Builder()
										.url("http://hdhome.org/attendance.php ")
										.build();
								Response response1 = WelcomeActivity.instance.okHttpClient.newCall(request1).execute();
								loadingTV();
								qiandao=1;
								qiandao_button.setText("今天已签到");
							}catch (Exception e){
								e.printStackTrace();
							}
						}
					}).start();
				}

				else{
					Toast.makeText(IndexMainActivity.this, "已签到", Toast.LENGTH_SHORT).show();

				}
			}
		});
	}

	public void loadingTV(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					Request request1 = new Request.Builder()
							.url("http://hdhome.org/index.php ")
							.build();
					Response response1 = WelcomeActivity.instance.okHttpClient.newCall(request1).execute();
					String responseData1= response1.body().string();
					response1.close();
					Document doc = Jsoup.parse(responseData1);
					Elements username = doc.select("a.User_Name");
					Elements meilizhi = doc.select("td.bottom>*");
					final String usernamestring = username.select("b").text();
					String string = meilizhi.text();
					Integer mouzi=string.indexOf(":");
					Integer qianzi=string.indexOf("签");
					final String meilizhiqu=string.substring(mouzi+1,qianzi-1);
					Integer lvzi=string.indexOf("率");
					Integer shangzi=string.indexOf("上");
					Integer liangzi=string.indexOf("传");
					Integer xiazi=string.indexOf("下",shangzi);
					Integer zaizi=string.indexOf("载",shangzi);
					Integer dangzi=string.indexOf("当");
					final String fenxianglvstring=string.substring(lvzi+2,shangzi);
					final String shangchuanliangstring=string.substring(liangzi+3,xiazi);
					final String xiazailiangstring=string.substring(zaizi+3,dangzi);
					usernameTV.post(new Runnable() {
						@Override
						public void run() {
							usernameTV.setText(usernamestring);
						}
					});
					meilizhiTV.post(new Runnable() {
						@Override
						public void run() {
						meilizhiTV.setText(meilizhiqu);
						}
					});
					fenxianglvTV.post(new Runnable() {
						@Override
						public void run() {
							fenxianglvTV.setText(fenxianglvstring);
						}
					});
					shangchuanliangTV.post(new Runnable() {
						@Override
						public void run() {
							shangchuanliangTV.setText(shangchuanliangstring);
						}
					});
					xiazailiangTV.post(new Runnable() {
						@Override
						public void run() {
							xiazailiangTV.setText(xiazailiangstring);
						}
					});
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}


	//清除保存的设置
	public void removeSharedPreference() {
		sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		//editor.remove("userName");
		editor.remove("AUTO_ISCHECK");
		editor.commit();// 提交修改

		//清楚保存的cookie
		sharedPreferences1=getSharedPreferences("CookiePrefsFile", Context.MODE_PRIVATE);
		Editor editor2 = sharedPreferences1.edit();
		editor2.clear();
		editor2.apply();

	}

	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			if((System.currentTimeMillis()-exitTime) > 3000){
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}


