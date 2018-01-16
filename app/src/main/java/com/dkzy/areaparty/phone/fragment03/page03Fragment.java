package com.dkzy.areaparty.phone.fragment03;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.MainActivity;
import com.dkzy.areaparty.phone.model_comman.MyAdapter;
import com.dkzy.areaparty.phone.bluetoothxie.TVBluetoothSet;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.androideventbusutils.events.SelectedDeviceChangedEvent;
import com.dkzy.areaparty.phone.androideventbusutils.events.TVPCNetStateChangeEvent;
import com.dkzy.areaparty.phone.androideventbusutils.events.changeSelectedDeviceNameEvent;
import com.dkzy.areaparty.phone.fragment01.utils.tvpcAppHelper;
import com.dkzy.areaparty.phone.fragment03.Model.AppItem;
import com.dkzy.areaparty.phone.fragment03.ui.ActionNoticeDialog;
import com.dkzy.areaparty.phone.fragment03.ui.SwipeGridView;
import com.dkzy.areaparty.phone.fragment03.utils.PCAppHelper;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import es.dmoral.toasty.Toasty;

import static com.dkzy.areaparty.phone.fragment01.utils.prepareDataForFragment.closeRDP;

/**
 * Created by boris on 2016/11/29.
 * TAB03---应用的Fragment
 */

public class page03Fragment extends Fragment implements View.OnClickListener {

    private final int TVPAGE = 0;
    private final int PCPAGE = 1;

    View rootView;
    Context mContext;
    // 顶部状态栏
    private ImageView PCStateIV, TVStateIV;
    private TextView  PCNameTV,  TVNameTV;
    // 页面选择按钮
    private TextView  TVBarTV, PCBarTV;
    // 内容页面
    private ScrollView TVPageSV, PCPageSV;
    // TV内容页面
    private LinearLayout TVInforLL,   TVRestartLL,      TVShutdownLL,
                         TVDevicesLL, TVUninstallAppLL, TVSettingLL;
    private SwipeGridView TVInstalledAppSGV, TVOwnAppSGV;
    // PC内容页面
    private LinearLayout PCInforLL, PCShutdown_RestartLL, PCUsingHelpLL;
    private TextView PCAppOpenModelNameTV;
    private ImageView PCAppOpenModelNoticeIV, PCGameNoticeIV, openPcDesk, closeRdp;
    private SwipeGridView PCAppSGV, PCGameSGV;

    private MyAdapter<AppItem> pcAppAdapter, pcGameAdapter;
    private MyAdapter<AppItem> tvSysAppAdapter, tvInstalledAppAdapter;

    private TextView helpInfo;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab03, container, false);
        rootView = view;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initViews();
        initEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(MyApplication.selectedPCVerified && PCAppHelper.appList.size() <= 0 && PCAppHelper.gameList.size() <= 0)
            PCAppHelper.loadList(myHandler);
        if(MyApplication.selectedTVVerified && TVAppHelper.installedAppList.size() <= 0 && TVAppHelper.ownAppList.size() <= 0)
            TVAppHelper.loadApps(myHandler);
        if(MyApplication.selectedTVVerified && TVAppHelper.getTvInfor().isEmpty())
            TVAppHelper.loadTVInfor(myHandler);

        pcAppAdapter.notifyDataSetChanged();
        pcGameAdapter.notifyDataSetChanged();
        tvInstalledAppAdapter.notifyDataSetChanged();
        tvSysAppAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.TVBarTV:
                setSelect(TVPAGE);
                break;
            case R.id.PCBarTV:
                setSelect(PCPAGE);
                break;
            case R.id.TVInforLL:
                if(MyApplication.selectedTVVerified && MyApplication.isSelectedTVOnline()) {
                    // ...跳转信息界面,然后在信息界面获取TV信息
                    startActivity(new Intent(mContext, tvInforActivity.class));
                } else Toasty.warning(mContext, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.TVRestartLL:
                Toasty.warning(mContext, "电视需要获取root权限", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.TVShutdownLL:
                Toasty.warning(mContext, "电视需要获取root权限", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.TVDevicesLL:
                if(MyApplication.selectedTVVerified && MyApplication.isSelectedTVOnline())
                    TVBluetoothSet.actionStart(mContext);
                else Toasty.warning(mContext, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.TVUninstallAppLL:
                // ...跳转卸载应用界面, 此处不进行判断
                startActivity(new Intent(mContext, uninstallTVAppActivity.class));
                break;
            case R.id.TVSettingLL:
                if(MyApplication.selectedTVVerified && MyApplication.isSelectedTVOnline()) {
                    TVAppHelper.openSettingPage();
                    Toasty.success(mContext, "即将打开电视设置界面, 请观看电视", Toast.LENGTH_SHORT, true).show();
                } else Toasty.warning(mContext, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.PCInforLL:
                if(MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline()) {
                    // ...跳转信息界面,然后在信息界面获取PC信息
                    startActivity(new Intent(mContext, pcInforActivity.class));
                } else Toasty.warning(mContext, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.PCShutdown_RestartLL:
                if(MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline())
                    showActionNoticeDialog(OrderConst.computerAction_reboot_command);
                else Toasty.warning(mContext, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.PCUsingHelpLL:
                /*TVAppHelper.openMoonlight();*/
                startActivity(new Intent(mContext, pcUsingHelpActivity.class));
                break;
            case R.id.PCAppOpenModelNoticeIV:
                startActivity(new Intent(mContext, pcAppHelpActivity.class));
                break;
            case R.id.PCGameNoticeIV:
                startActivity(new Intent(mContext, pcGameHelpActivity.class));
                break;
            case R.id.openPcDesk:
                if(MyApplication.isSelectedPCOnline()) {
                    if(MyApplication.isSelectedTVOnline()) {
                        if(PCAppHelper.getCurrentMode() == PCAppHelper.NONEMODE) {
                            TVAppHelper.loadMouses(myHandler);
                        }
                        if(MyApplication.AccessibilityIsOpen){
                            TVAppHelper.openTVRDP();

                            // 需要用户手动点击进入
                            PCAppHelper.setCurrentMode(PCAppHelper.RDPMODE);
                            PCAppHelper.openApp_Rdp("back",
                                    "desk", myHandler);
                        }else {
                            TVAppHelper.openTVAccessibility();
                            Toast.makeText(getContext(),"请在TV上对[AreaParty]授权",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toasty.warning(mContext, "应用即将投放的屏幕不在线", Toast.LENGTH_SHORT, true).show();
                    }
                } else{
                    Toasty.warning(mContext, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                }
                break;
            case R.id.closeRDP:
                if (MyApplication.isSelectedTVOnline()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            closeRDP();
                        }
                    }).start();

                }else
                    Toasty.warning(mContext, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.helpInfo:
                ((MainActivity)getActivity()).showHelpInfoDialog(R.layout.dialog_page03);
                break;

        }
    }

    private void initEvents() {
        TVBarTV.setOnClickListener(this);
        PCBarTV.setOnClickListener(this);
        TVInforLL.setOnClickListener(this);
        TVRestartLL.setOnClickListener(this);
        TVDevicesLL.setOnClickListener(this);
        TVUninstallAppLL.setOnClickListener(this);
        TVSettingLL.setOnClickListener(this);
        TVShutdownLL.setOnClickListener(this);
        PCInforLL.setOnClickListener(this);
        PCShutdown_RestartLL.setOnClickListener(this);
        PCUsingHelpLL.setOnClickListener(this);
        PCAppOpenModelNoticeIV.setOnClickListener(this);
        PCGameNoticeIV.setOnClickListener(this);
        openPcDesk.setOnClickListener(this);
        closeRdp.setOnClickListener(this);
        helpInfo.setOnClickListener(this);

        TVInstalledAppSGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(MyApplication.isSelectedTVOnline()) {
                    TVAppHelper.openApp(TVAppHelper.installedAppList.get(i).getPackageName());
                    Log.w("page03Fragment",TVAppHelper.installedAppList.get(i).getPackageName());
                    //TVAppHelper.installedAppList.get(i).setRunning(true);
                    //tvInstalledAppAdapter.notifyDataSetChanged();
                    tvpcAppHelper.addTVApps(TVAppHelper.installedAppList.get(i));
                    Toasty.success(mContext, "应用即将打开, 请观看电视", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.warning(mContext, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
        TVOwnAppSGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(MyApplication.isSelectedTVOnline()) {
                    TVAppHelper.openApp(TVAppHelper.ownAppList.get(i).getPackageName());
                    //TVAppHelper.ownAppList.get(i).setRunning(true);
                    //tvSysAppAdapter.notifyDataSetChanged();
                    tvpcAppHelper.addTVApps(TVAppHelper.ownAppList.get(i));
                    Toasty.success(mContext, "应用即将打开, 请观看电视", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.warning(mContext, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
        PCAppSGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(MyApplication.isSelectedPCOnline()) {
                    if(MyApplication.isSelectedTVOnline()) {
                        if(PCAppHelper.getCurrentMode() == PCAppHelper.NONEMODE) {
                            TVAppHelper.loadMouses(myHandler);
                        }
                        if(MyApplication.AccessibilityIsOpen){
                            TVAppHelper.openTVRDP();

                            // 需要用户手动点击进入
                            PCAppHelper.setCurrentMode(PCAppHelper.RDPMODE);
                            PCAppHelper.openApp_Rdp(PCAppHelper.appList.get(i).getPackageName(),
                                    PCAppHelper.appList.get(i).getAppName(), myHandler);
                        }else {
                            TVAppHelper.openTVAccessibility();
                            Toast.makeText(getContext(),"请在TV上对[AreaParty]授权",Toast.LENGTH_LONG).show();
                        }


                        tvpcAppHelper.addPCApps(PCAppHelper.appList.get(i));
                    } else {
                        Toasty.warning(mContext, "应用即将投放的屏幕不在线", Toast.LENGTH_SHORT, true).show();
                    }
                } else{
                    Toasty.warning(mContext, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
        PCGameSGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if(MyApplication.isSelectedPCOnline()) {
                    if(MyApplication.isSelectedTVOnline()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    PCAppHelper.killPCGame(myHandler);
                                    Thread.sleep(1000);
                                    TVAppHelper.prepareForPCGame(MyApplication.getSelectedPCIP().ip, MyApplication.getSelectedPCIP().mac);
                                    Thread.sleep(8000);
                                    PCAppHelper.openPCGame(PCAppHelper.gameList.get(i).getPackageName(), PCAppHelper.gameList.get(i).getAppName(), myHandler);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else {
                        Toasty.warning(mContext, "游戏即将投放的屏幕不在线", Toast.LENGTH_SHORT, true).show();
                    }
                } else{
                    Toasty.warning(mContext, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                }
            }
        });

        TVPageSV.smoothScrollTo(0,20);
        PCPageSV.smoothScrollTo(0,20);
    }

    private void initData() {
        pcAppAdapter = new MyAdapter<AppItem>(PCAppHelper.appList, R.layout.tab03_app_item) {
            @Override
            public void bindView(ViewHolder holder, AppItem obj) {
                holder.setImage(R.id.appImageIV, obj.getIconURL(), R.drawable.logo_loading, mContext);
                holder.setText(R.id.appNameTV, obj.getAppName());
                holder.setVisibility(R.id.isRunningIV, View.GONE);
            }
        };
        pcGameAdapter = new MyAdapter<AppItem>(PCAppHelper.gameList, R.layout.tab03_app_item) {
            @Override
            public void bindView(ViewHolder holder, AppItem obj) {
                holder.setImage(R.id.appImageIV, obj.getIconURL(), R.drawable.logo_loading, mContext);
                holder.setText(R.id.appNameTV, obj.getAppName());
                holder.setVisibility(R.id.isRunningIV, View.GONE);
            }
        };
        tvInstalledAppAdapter = new MyAdapter<AppItem>(TVAppHelper.installedAppList, R.layout.tab03_app_item) {
            @Override
            public void bindView(ViewHolder holder, AppItem obj) {
                holder.setImage(R.id.appImageIV, obj.getIconURL(), R.drawable.logo_loading, mContext);
                holder.setText(R.id.appNameTV, obj.getAppName());
                if(obj.isRunning())
                    holder.setVisibility(R.id.isRunningIV, View.VISIBLE);
                else holder.setVisibility(R.id.isRunningIV, View.GONE);
            }
        };
        tvSysAppAdapter = new MyAdapter<AppItem>(TVAppHelper.ownAppList, R.layout.tab03_app_item) {
            @Override
            public void bindView(ViewHolder holder, AppItem obj) {
                holder.setImage(R.id.appImageIV, obj.getIconURL(), R.drawable.logo_loading, mContext);
                holder.setText(R.id.appNameTV, obj.getAppName());
                if(obj.isRunning())
                    holder.setVisibility(R.id.isRunningIV, View.VISIBLE);
                else holder.setVisibility(R.id.isRunningIV, View.GONE);
            }
        };
    }

    private void initViews() {
        PCStateIV = (ImageView)  rootView.findViewById(R.id.PCStateIV);
        TVStateIV = (ImageView)  rootView.findViewById(R.id.TVStateIV);
        PCNameTV  = (TextView)   rootView.findViewById(R.id.PCNameTV);
        TVNameTV  = (TextView)   rootView.findViewById(R.id.TVNameTV);
        TVPageSV  = (ScrollView) rootView.findViewById(R.id.TVPageSV);
        PCPageSV  = (ScrollView) rootView.findViewById(R.id.PCPageSV);
        TVBarTV   = (TextView)   rootView.findViewById(R.id.TVBarTV);
        PCBarTV   = (TextView)   rootView.findViewById(R.id.PCBarTV);
        TVInforLL    = (LinearLayout) rootView.findViewById(R.id.TVInforLL);
        TVRestartLL  = (LinearLayout) rootView.findViewById(R.id.TVRestartLL);
        TVShutdownLL = (LinearLayout) rootView.findViewById(R.id.TVShutdownLL);
        TVDevicesLL  = (LinearLayout) rootView.findViewById(R.id.TVDevicesLL);
        TVSettingLL  = (LinearLayout) rootView.findViewById(R.id.TVSettingLL);
        PCInforLL    = (LinearLayout) rootView.findViewById(R.id.PCInforLL);
        PCShutdown_RestartLL = (LinearLayout) rootView.findViewById(R.id.PCShutdown_RestartLL);
        TVUninstallAppLL   = (LinearLayout) rootView.findViewById(R.id.TVUninstallAppLL);
        PCUsingHelpLL = (LinearLayout) rootView.findViewById(R.id.PCUsingHelpLL);
        TVInstalledAppSGV = (SwipeGridView) rootView.findViewById(R.id.TVInstalledAppSGV);
        PCAppSGV    = (SwipeGridView) rootView.findViewById(R.id.PCAppSGV);
        PCGameSGV   = (SwipeGridView) rootView.findViewById(R.id.PCGameSGV);
        TVOwnAppSGV = (SwipeGridView) rootView.findViewById(R.id.TVOwnAppSGV);
        PCAppOpenModelNameTV = (TextView) rootView.findViewById(R.id.PCAppOpenModelNameTV);
        PCGameNoticeIV = (ImageView) rootView.findViewById(R.id.PCGameNoticeIV);
        PCAppOpenModelNoticeIV = (ImageView) rootView.findViewById(R.id.PCAppOpenModelNoticeIV);
        openPcDesk = (ImageView) rootView.findViewById(R.id.openPcDesk);
        closeRdp = (ImageView) rootView.findViewById(R.id.closeRDP);
        helpInfo = (TextView) rootView.findViewById(R.id.helpInfo);

        updateDeviceNetState(new TVPCNetStateChangeEvent(MyApplication.isSelectedTVOnline(),
                MyApplication.isSelectedPCOnline()));
        TVInstalledAppSGV.setAdapter(tvInstalledAppAdapter);
        TVOwnAppSGV.setAdapter(tvSysAppAdapter);
        PCAppSGV.setAdapter(pcAppAdapter);
        PCGameSGV.setAdapter(pcGameAdapter);
    }

    private void setSelect(int page) {
        switch (page) {
            case TVPAGE:
                TVBarTV.setTextColor(Color.parseColor("#FF5050"));
                TVBarTV.setBackgroundResource(R.drawable.barback03_right_pressed);
                PCBarTV.setTextColor(Color.parseColor("#707070"));
                PCBarTV.setBackgroundResource(R.drawable.barback03_left_normal);
                TVPageSV.setVisibility(View.VISIBLE);
                PCPageSV.setVisibility(View.INVISIBLE);
                break;
            case PCPAGE:
                PCBarTV.setTextColor(Color.parseColor("#FF5050"));
                PCBarTV.setBackgroundResource(R.drawable.barback03_left_pressed);
                TVBarTV.setTextColor(Color.parseColor("#707070"));
                TVBarTV.setBackgroundResource(R.drawable.barback03_right_normal);
                PCPageSV.setVisibility(View.VISIBLE);
                TVPageSV.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void showActionNoticeDialog(String type) {
        final ActionNoticeDialog actionDialog = new ActionNoticeDialog(mContext);
        actionDialog.setCanceledOnTouchOutside(true);
        actionDialog.show();
        actionDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionDialog.dismiss();
            }
        });
        switch (type) {
            case OrderConst.rebootTV_firCommand:
                actionDialog.setTitleText("是否重启电视?");
                actionDialog.setPositiveButtonText("重启");
                actionDialog.setOnPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TVAppHelper.rebootTV();
                        actionDialog.dismiss();
                        Toasty.info(mContext, "电视重启中", Toast.LENGTH_SHORT, true).show();
                    }
                });
                actionDialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionDialog.dismiss();
                    }
                });
                break;
            case OrderConst.shutdownTV_firCommand:
                actionDialog.setTitleText("是否关闭电视?");
                actionDialog.setPositiveButtonText("关闭");
                actionDialog.setOnPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TVAppHelper.shutDownTV();
                        actionDialog.dismiss();
                        Toasty.info(mContext, "电视即将关闭", Toast.LENGTH_SHORT, true).show();
                    }
                });
                actionDialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionDialog.dismiss();
                    }
                });
                break;
            case OrderConst.computerAction_reboot_command:
                actionDialog.setTitleText("请选择操作方式");
                actionDialog.setPositiveButtonText("重启");
                actionDialog.setNegativeButtonText("关闭");
                actionDialog.setOnPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PCAppHelper.rebootPC();
                        actionDialog.dismiss();
                        Toasty.info(mContext, "电脑重启中", Toast.LENGTH_SHORT, true).show();
                    }
                });
                actionDialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PCAppHelper.shutdownPC();
                        actionDialog.dismiss();
                        Toasty.info(mContext, "电脑即将关闭", Toast.LENGTH_SHORT, true).show();
                    }
                });
                break;
        }
    }

    @Subscriber(tag = "selectedTVNameChange")
    private void upDateTVName(changeSelectedDeviceNameEvent event) {
        TVNameTV.setText("在线");
    }

    @Subscriber(tag = "selectedPCNameChange")
    private void upDatePCName(changeSelectedDeviceNameEvent event) {
        PCNameTV.setText("在线");
    }

    @Subscriber(tag = "selectedDeviceStateChanged")
    private void updateDeviceNetState(TVPCNetStateChangeEvent event) {
        if(event.isPCOnline() && MyApplication.selectedPCVerified) {
            // ... 判断是否已有数据
            if(PCAppHelper.appList.size() <= 0 && PCAppHelper.gameList.size() <= 0)
                PCAppHelper.loadList(myHandler);
            PCStateIV.setImageResource(R.drawable.pcconnected);
            PCNameTV.setText("在线");
            PCNameTV.setTextColor(Color.parseColor("#ffffff"));
        } else {
            PCStateIV.setImageResource(R.drawable.pcbroke);
            PCNameTV.setText("离线中");
            PCNameTV.setTextColor(Color.parseColor("#dbdbdb"));
        }
        if(event.isTVOnline() && MyApplication.selectedTVVerified) {
            if(TVAppHelper.installedAppList.size() <= 0 && TVAppHelper.ownAppList.size() <= 0) {
                TVAppHelper.loadApps(myHandler);
            }
            TVStateIV.setImageResource(R.drawable.tvconnected);
            TVNameTV.setText("在线");
            TVNameTV.setTextColor(Color.parseColor("#ffffff"));
        } else {
            TVStateIV.setImageResource(R.drawable.tvbroke);
            TVNameTV.setText("离线中");
            TVNameTV.setTextColor(Color.parseColor("#dbdbdb"));
        }
    }

    @Subscriber(tag = "selectedPCChanged")
    private void updatePCState(SelectedDeviceChangedEvent event) {
        PCAppHelper.setCurrentMode(PCAppHelper.NONEMODE);
        PCAppHelper.resetTotalInfors();
        pcGameAdapter.notifyDataSetChanged();
        pcAppAdapter.notifyDataSetChanged();
        if(event.isDeviceOnline()) {
            // 重置界面(最近播放、播放列表)
            PCStateIV.setImageResource(R.drawable.pcconnected);
            PCNameTV.setText("在线");
            PCNameTV.setTextColor(Color.parseColor("#ffffff"));
            // 重新获取数据
            PCAppHelper.loadList(myHandler);
        } else {
            PCStateIV.setImageResource(R.drawable.pcbroke);
            PCNameTV.setText("离线中");
            PCNameTV.setTextColor(Color.parseColor("#dbdbdb"));
        }
    }

    @Subscriber(tag = "selectedTVChanged")
    private void updateTVState(SelectedDeviceChangedEvent event) {
        PCAppHelper.setCurrentMode(PCAppHelper.NONEMODE);
        TVAppHelper.resetTotalInfors();
        tvSysAppAdapter.notifyDataSetChanged();
        tvInstalledAppAdapter.notifyDataSetChanged();
        if(event.isDeviceOnline()) {
            TVStateIV.setImageResource(R.drawable.tvconnected);
            TVNameTV.setText("在线");
            TVNameTV.setTextColor(Color.parseColor("#ffffff"));
            TVAppHelper.loadApps(myHandler);
        } else {
            TVStateIV.setImageResource(R.drawable.tvbroke);
            TVNameTV.setText("离线中");
            TVNameTV.setTextColor(Color.parseColor("#dbdbdb"));
        }
    }

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderConst.getPCApp_OK:
                    Log.i("Send2PCThread", "获取应用成功");
                    pcAppAdapter.notifyDataSetChanged();
                    break;
                case OrderConst.getPCApp_Fail:
                    break;
                case OrderConst.getPCGame_OK:
                    pcGameAdapter.notifyDataSetChanged();
                    break;
                case OrderConst.getPCGame_Fail:
                    break;
                case OrderConst.getTVOtherApp_OK:
                    tvInstalledAppAdapter.notifyDataSetChanged();
                    break;
                case OrderConst.getTVOtherApp_Fail:
                    break;
                case OrderConst.getTVSYSApp_OK:
                    tvSysAppAdapter.notifyDataSetChanged();
                    break;
                case OrderConst.getTVSYSApp_Fail:
                    break;
                case OrderConst.openPCApp_OK:
                    break;
                case OrderConst.openPCApp_Fail:
                    break;
                case OrderConst.openPCGame_OK:
                    PCAppHelper.setCurrentMode(PCAppHelper.NONEMODE);
                    Toasty.success(mContext, "即将在当前电视上打开选中游戏", Toast.LENGTH_SHORT, true).show();
                    break;
                case OrderConst.openPCGame_Fail:
                    Toasty.info(mContext, "在当前电视上打开选中游戏失败", Toast.LENGTH_SHORT, true).show();
                    break;
                case OrderConst.getTVMouse_OK:
                    Toasty.success(mContext, "当前电视已连接鼠标, 请操作连接电脑" + MyApplication.getSelectedPCIP().name,
                            Toast.LENGTH_LONG, true).show();
                    break;
                case OrderConst.getTVMouse_Fail:
                    Toasty.info(mContext, "当前电视未接有鼠标, 请将鼠标连接到电视", Toast.LENGTH_LONG, true).show();
                    break;
            }
        }
    };

}
