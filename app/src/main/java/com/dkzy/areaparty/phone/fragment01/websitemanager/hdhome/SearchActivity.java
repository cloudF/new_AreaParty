package com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;
import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends Activity {
	
	private ViewPager mViewPager;// 用来放置界面切换
	private PagerAdapter mPagerAdapter;// 初始化View适配器
	private List<View> mViews=new ArrayList<View>();//用来存放Tab_search&hdhome_searchresult_layout
	private  static ArrayList<String> listUrl;//存放各电影item的url
	ListView mListView;
	private SearchView searchView;

	//底部下拉加载
	private LinearLayout loadingLayout;
	private View loading_Layout;
	private TextView loadingLayoutText;
	
	private static ArrayList<HashMap<String, Object>> listItem;//数据结构
	private static Document doc;//全局html
	private OkHttpClient client;
	private static int m= 0;
	private static Handler handler;
	private static boolean isLoading;
	SimpleAdapter listItemAdapter;
	private int mScrollState;
	private int count=0;//列表数量
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hdhome_search_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		initView();
		initViewPage();
		
		//search功能
		search();
		showSearchList();

		//在这里可以进行UI操作  ,设置电影视图
		listItemAdapter=new SimpleAdapter(getBaseContext(), //获得适配器
				listItem, R.layout.hdhome_detail_item,new String[]{"ImageUrl","ChineseName","OtherName","Type","CountryTimeSize"}, new int[]{R.id.imageView_Movie,R.id.ChineseMovieName,R.id.EnglishMovieName,R.id.MovieType,
				R.id.movieCountryTimeSize});

		mListView.setAdapter(listItemAdapter);//设置适配器
		
	}

	private void showSearchList() {
		// TODO Auto-generated method stub

	
			Log.i("show", "showww");
			  handler = new Handler() {

					@SuppressWarnings("unchecked")
					public void handleMessage(Message msg) {
						
			            switch (msg.what) {
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
									try{
										Thread.sleep(2000);
									}
									catch(InterruptedException e){
										e.printStackTrace();
									}
									//切换到UI线程执行
									runOnUiThread(new Runnable() {
										
										@Override
										public void run() {
											// TODO Auto-generated method stub
											mListView.addFooterView(loading_Layout);
											loadingLayoutText.setText("Loading...");
											 count+=10;
											 Log.i("counnt22", String.valueOf(count));
								 
										     loadData(count);
											
										}
									});
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
						intent.setClass(SearchActivity.this,DetailsActivity.class);
						startActivity(intent);
					            //显示被单击选项的内容
					}
				});

	}

	private void search() {
		// TODO Auto-generated method stub
//		searchView.setSubmitButtonEnabled(true);
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				mViewPager.setCurrentItem(1);//设置显示tab_search_result页面
				count=0;
				m=0;//重置列表计数m
				listItem.clear();//清除之前的列表数据
				searchResult();
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	protected void searchResult() {//得到Search的HTML结果
		// TODO Auto-generated method stub
		 String url="http://hdhome.org/torrents.php?search=";
		    //获得搜索框输入框
		    int id=searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
		    EditText tv=(EditText)searchView.findViewById(id);
		    url=url+tv.getText().toString();
		    Log.i("url", url);
		   

		    Request request1=new Request.Builder().url(url).build();
		    WelcomeActivity.instance.okHttpClient.newCall(request1).enqueue(new Callback() {//LoginActivity.instance.okHttpClient是LoginActivity中的变量
				
				@Override
				public void onResponse(Call arg0, Response arg1) throws IOException {
					// TODO Auto-generated method stub
					String responseData=arg1.body().string();
					arg1.close();
					 doc=Jsoup.parse(responseData);//得到search的HTML
					 loadData(0);//吧search的结果装入adapter

				}
				
			

				@Override
				public void onFailure(Call arg0, IOException arg1) {
					// TODO Auto-generated method stub
					
				}
			});


			
	}

	protected void loadData(int count) {//search的结果放到Adapter中
		// TODO Auto-generated method stub
//		final Elements elements3=doc.select(".rowfollow").select(".nowrap");
		final Elements elements3=doc.select("td[class=rowfollow][width=100%]");
        Log.i("moviename1", elements3.get(0).text());
        Log.i("int", String.valueOf(elements3.size()));
        client=new OkHttpClient();

        //每次加载最多10条item,最后一次可能小于10
		for( int i=count;i<count+10&&i<elements3.size();i++)//elements3.size()
		{
			HashMap<String, String> map=new HashMap<String,String>();
			map.put("ID", String.valueOf(i));
			/**
			 * 加载国家信息
			 */
			//
			//得到电影对于URL
			String url=elements3.get(i).select("a[href]").get(0).attr("href");
			url="http://hdhome.org/"+url;
			listUrl.add(url);//把url保存起来，便于传给DetialsActivity中
			map.put("URL", url);
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
						//请求的电影列表资源传到UI线程中，在UI线程添加数据
						Message message3=new Message();
						message3.what=3;
						message3.obj=map1;
						handler.sendMessage(message3);


						if(m==elements3.size()&&m<10)//加载最后一条更新
						{
							Message message1=new Message();
							message1.what=1;
							handler.sendMessage(message1);
						}
						else if(m==elements3.size()){
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

	private void initViewPage() {
		// TODO Auto-generated method stub
		//初始化2个布局
		LayoutInflater mLayoutInflater= LayoutInflater.from(this);
		View tab_search=mLayoutInflater.inflate(R.layout.hdhome_search_layout, null);
		View tab_search_result=mLayoutInflater.inflate(R.layout.hdhome_searchresult_layout, null);
		//初始化ListView
		mListView=(ListView) tab_search_result.findViewById(R.id.listViewMovie1);
		
		mViews.add(tab_search);
		mViews.add(tab_search_result);

		// 适配器初始化并设置
		mPagerAdapter=new PagerAdapter() {
			
		

			@Override
			public void destroyItem(ViewGroup container, int position,
                                    Object object) {
				// TODO Auto-generated method stub
				container.removeView(mViews.get(position));
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				// TODO Auto-generated method stub
				View view = mViews.get(position);
				container.addView(view);
				return view;
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mViews.size();
			}
		};
		mViewPager.setAdapter(mPagerAdapter);
	}

	private void initView() {
		// TODO Auto-generated method stub
		mViewPager=(ViewPager)findViewById(R.id.id_viewpage1);
		//初始化searchView
		searchView=(SearchView)findViewById(R.id.searchView2);
		listItem=new ArrayList<HashMap<String, Object>>();
		listUrl=new ArrayList<String>();

		//loadingLayout
		LayoutInflater mLayoutInflater = LayoutInflater.from(this);
		loading_Layout=mLayoutInflater.inflate(R.layout.hdhome_loading_layout, null);
		loadingLayout=(LinearLayout) loading_Layout;
		loadingLayoutText=(TextView) loading_Layout.findViewById(R.id.loadingLayoutText);
		
	}
	

}
