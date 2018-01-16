package com.dkzy.areaparty.phone.fragment01;


import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.MainActivity;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.androideventbusutils.events.TVPCNetStateChangeEvent;
import com.dkzy.areaparty.phone.androideventbusutils.events.changeSelectedDeviceNameEvent;
import com.dkzy.areaparty.phone.fragment01.base.GlideImageLoader;
import com.dkzy.areaparty.phone.fragment01.setting.SettingMainActivity;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_launch;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_page;
import com.dkzy.areaparty.phone.fragment01.utils.tvpcAppHelper;
import com.dkzy.areaparty.phone.fragment01.websitemanager.start.StartActivity;
import com.dkzy.areaparty.phone.fragment03.Model.AppItem;
import com.dkzy.areaparty.phone.fragment03.tvInforActivity;
import com.dkzy.areaparty.phone.fragment03.ui.SwipeGridView;
import com.dkzy.areaparty.phone.fragment03.utils.PCAppHelper;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;
import com.dkzy.areaparty.phone.fragment06.headIndexToImgId;
import com.dkzy.areaparty.phone.model_comman.MyAdapter;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.PreferenceUtil;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.IPInforBean;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.JsonUitl;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


/**
 * Created by boris on 2016/11/29.
 * TAB01---首页的Fragment
 */

public class page01Fragment extends Fragment implements View.OnClickListener {

    private final String tag = this.getClass().getSimpleName();
    private final int PCS_RESULTCODE  = 0x1;
    private final int PCS_REQUESTCODE = 0x2;
    private final int TVS_RESULTCODE  = 0x3;
    private final int TVS_REQUESTCODE = 0x4;
    private final String ISPCCHANGEDKEY = "isPCChanged";
    private final String ISTVCHANGEDKEY = "isTVChanged";
    private boolean outline;
    //姜超 登录按钮
    private LinearLayout tab01_loginWrap;
    private TextView id_top01_userName, helpInfo;
    private ImageView userLogo_imgButton;

    private Context mContext;
    private View rootView;
    private ScrollView scrollView;
    private Banner bannerB;
    private LinearLayout PCDevicesLL, TVDevicesLL, blueDevicesLL, settingLL;
    private LinearLayout lastPCInforLL,  lastTVInforLL;
    private TextView isLastUsedPCExistTV, isLastUsedTVExistTV;
    private TextView lastPCInforNameTV,  lastTVInforNameTV;
    private TextView lastPCInforStateTV, lastTVInforStateTV;
    private SwipeGridView TVRecentAppSGV, PCRecentAppSGV;

    private Boolean viewOk = false;
    private ArrayList<Integer> imageList = new ArrayList<>();
    private MyAdapter<AppItem> tvRecentAppAdapter, pcRecentAppAdapter;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.PCDevicesLL:
                startActivityForResult(new Intent(mContext, PCDevicesActivity.class), PCS_REQUESTCODE);
                break;
            case R.id.TVDevicesLL:
                startActivityForResult(new Intent(mContext, TVDevicesActivity.class), TVS_REQUESTCODE);
                break;
            case R.id.blueDevicesLL:
                //ComputerBluetooth.actionStart(mContext);
                startActivity(new Intent(mContext, PCFileSysActivity.class));
                break;
            case R.id.settingLL: {
                Intent intent = new Intent(mContext, SettingMainActivity.class);
                intent.putExtra("isOutline", outline);
                startActivity(intent);
            }
                break;
            case R.id.lastPCInforLL:
                if(MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline())
                    startActivity(new Intent(getActivity(), computerMonitorActivity.class));
                else Toasty.warning(mContext, "当前电脑未验证或不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.lastTVInforLL:
                if(MyApplication.selectedTVVerified && MyApplication.isSelectedTVOnline()) {
                    startActivity(new Intent(mContext, tvInforActivity.class));
                } else Toasty.warning(mContext, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case (R.id.tab01_loginWrap): {
                Intent intent = new Intent();
                intent.setClass(getActivity(),Login.class);
                startActivity(intent);
                getActivity().finish();
            }
                break;
            case R.id.helpInfo:
                ((MainActivity)getActivity()).showHelpInfoDialog(R.layout.dialog_page01);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab01, container, false);
        rootView = view;
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
        initEvent();
    }

    private void initData() {
        imageList.add(R.drawable.redboat);

        tvRecentAppAdapter = new MyAdapter<AppItem>(tvpcAppHelper.tvApps, R.layout.tab03_app_item) {
            @Override
            public void bindView(ViewHolder holder, AppItem obj) {
                holder.setImage(R.id.appImageIV, obj.getIconURL(), R.drawable.logo_loading, mContext);
                holder.setText(R.id.appNameTV, obj.getAppName());
                holder.setVisibility(R.id.isRunningIV, View.GONE);
            }
        };
        pcRecentAppAdapter = new MyAdapter<AppItem>(tvpcAppHelper.pcApps, R.layout.tab03_app_item) {
            @Override
            public void bindView(ViewHolder holder, AppItem obj) {
                holder.setImage(R.id.appImageIV, obj.getIconURL(), R.drawable.logo_loading, mContext);
                holder.setText(R.id.appNameTV, obj.getAppName());
                holder.setVisibility(R.id.isRunningIV, View.GONE);
            }
        };
        tvpcAppHelper.initPCApps();
        tvpcAppHelper.initTVApps();
    }

    @Override
    public void onStop() {
        super.onStop();
        bannerB.stopAutoPlay();
    }

    @Override
    public void onDestroyView() {
        viewOk = false;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Subscriber(tag = "TVAppInitialed")
    private void initialedTVApps(ArrayList<AppItem> tvapps) {
        tvRecentAppAdapter.notifyDataSetChanged();
    }

    @Subscriber(tag = "PCAppInitialed")
    private void initialedPCApps(ArrayList<AppItem> pcapps) {
        pcRecentAppAdapter.notifyDataSetChanged();
    }

    @Subscriber(tag = "TVAppAdded")
    private void updateTVApps(ArrayList<AppItem> tvapps) {
        tvRecentAppAdapter.notifyDataSetChanged();
    }

    @Subscriber(tag = "PCAppAdded")
    private void updatePCApps(ArrayList<AppItem> pcapps) {
        pcRecentAppAdapter.notifyDataSetChanged();
    }

    @Subscriber(tag = "selectedDeviceStateChanged")
    private void updateDeviceNetState(TVPCNetStateChangeEvent event) {
        Log.e("stateChange", "TV:" + event.isTVOnline() + " PC:" + event.isPCOnline());
        if(MyApplication.selectedPCVerified && event.isPCOnline()) {
            lastPCInforNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorPcOk));
            lastPCInforStateTV.setText("已连接");
            lastPCInforStateTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedDeviceOK));
        } else {
            lastPCInforNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedFail));
            lastPCInforStateTV.setText("未连接");
            lastPCInforStateTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedFail));
        }
        if(MyApplication.selectedTVVerified && event.isTVOnline()) {
            lastTVInforNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorTvOk));
            lastTVInforStateTV.setText("已连接");
            lastTVInforStateTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedDeviceOK));
        } else {
            lastTVInforNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedFail));
            lastTVInforStateTV.setText("未连接");
            lastTVInforStateTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedFail));
        }
    }

    @Subscriber(tag = "selectedTVNameChange")
    private void upDateTVName(changeSelectedDeviceNameEvent event) {
        lastTVInforNameTV.setText(event.getName());
    }

    @Subscriber(tag = "selectedPCNameChange")
    private void upDatePCName(changeSelectedDeviceNameEvent event) {
        lastPCInforNameTV.setText(event.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(tag, "before set device");
        if(requestCode == PCS_REQUESTCODE && resultCode == PCS_RESULTCODE){
            boolean isPCChanged = data.getExtras().getBoolean(ISPCCHANGEDKEY);
            if(isPCChanged) {
                setDevice(MyApplication.getSelectedPCIP(), "PC");
                tvpcAppHelper.pcApps.clear();
                pcRecentAppAdapter.notifyDataSetChanged();
                tvpcAppHelper.initPCApps();
            }
            MyApplication.selectedPCVerified = true;
            MyApplication.setSelectedPCOnline(true);
            EventBus.getDefault().post(new TVPCNetStateChangeEvent(MyApplication.isSelectedTVOnline(), true), "selectedDeviceStateChanged");
        }

        if(requestCode == TVS_REQUESTCODE && resultCode == TVS_RESULTCODE){
            boolean isTVChanged = data.getExtras().getBoolean(ISTVCHANGEDKEY);
            if(isTVChanged) {
                setDevice(MyApplication.getSelectedTVIP(), "TV");
                tvpcAppHelper.tvApps.clear();
                tvRecentAppAdapter.notifyDataSetChanged();
                tvpcAppHelper.initTVApps();
            }
            MyApplication.selectedTVVerified = true;
            MyApplication.setSelectedTVOnline(true);
            EventBus.getDefault().post(new TVPCNetStateChangeEvent(true, MyApplication.isSelectedPCOnline()), "selectedDeviceStateChanged");

        }
    }

    private void initView() {
        bannerB = (Banner) rootView.findViewById(R.id.bannerB);
        PCDevicesLL = (LinearLayout) rootView.findViewById(R.id.PCDevicesLL);
        TVDevicesLL = (LinearLayout) rootView.findViewById(R.id.TVDevicesLL);
        blueDevicesLL = (LinearLayout) rootView.findViewById(R.id.blueDevicesLL);
        settingLL = (LinearLayout) rootView.findViewById(R.id.settingLL);
        lastPCInforLL = (LinearLayout) rootView.findViewById(R.id.lastPCInforLL);
        lastTVInforLL = (LinearLayout) rootView.findViewById(R.id.lastTVInforLL);
        lastPCInforNameTV = (TextView) rootView.findViewById(R.id.lastPCInforNameTV);
        lastTVInforNameTV = (TextView) rootView.findViewById(R.id.lastTVInforNameTV);
        lastPCInforStateTV = (TextView) rootView.findViewById(R.id.lastPCInforStateTV);
        lastTVInforStateTV = (TextView) rootView.findViewById(R.id.lastTVInforStateTV);
        isLastUsedPCExistTV = (TextView) rootView.findViewById(R.id.isLastUsedPCExistTV);
        isLastUsedTVExistTV = (TextView) rootView.findViewById(R.id.isLastUsedTVExistTV);
        TVRecentAppSGV = (SwipeGridView) rootView.findViewById(R.id.TVRecentAppSGV);
        PCRecentAppSGV = (SwipeGridView) rootView.findViewById(R.id.PCRecentAppSGV);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        helpInfo = (TextView) rootView.findViewById(R.id.helpInfo);

        TVRecentAppSGV.setAdapter(tvRecentAppAdapter);
        PCRecentAppSGV.setAdapter(pcRecentAppAdapter);
        //姜超 初始化登录按钮
        tab01_loginWrap = (LinearLayout) rootView.findViewById(R.id.tab01_loginWrap);
        id_top01_userName = (TextView) rootView.findViewById(R.id.id_top01_userName);
        userLogo_imgButton = (ImageView) rootView.findViewById(R.id.userLogo_imgButton);

        Intent loginIntent = getActivity().getIntent();
        Bundle bundle = loginIntent.getExtras();
        outline = bundle.getBoolean("outline");

        if(outline){
            id_top01_userName.setText("登录");
            userLogo_imgButton.setImageResource(R.drawable.user);
            tab01_loginWrap.setOnClickListener(this);
        }else{
            String userName = bundle.getString("userName");
            int userHeadIndex = bundle.getInt("userHeadIndex");
            id_top01_userName.setText(userName);
            userLogo_imgButton.setImageResource(headIndexToImgId.toImgId(userHeadIndex));
        }

        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int height = size.x / 4;
        ViewGroup.LayoutParams layoutParams = bannerB.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = height;
        bannerB.setLayoutParams(layoutParams);
        bannerB.setBannerStyle(BannerConfig.NOT_INDICATOR);
        bannerB.setImageLoader(new GlideImageLoader());
        bannerB.setImages(imageList);
        bannerB.setBannerAnimation(Transformer.DepthPage);
        bannerB.isAutoPlay(true);
        bannerB.setDelayTime(5000);
        bannerB.setIndicatorGravity(BannerConfig.CENTER);
        bannerB.start();

        IPInforBean tempPC, tempTV;
        tempPC = MyApplication.getSelectedPCIP();
        tempTV = MyApplication.getSelectedTVIP();
        if(tempPC != null && !tempPC.ip.equals("")) {
            lastPCInforNameTV.setText(tempPC.nickName);
            isLastUsedPCExistTV.setText("上次使用");
            if(MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline()) {
                lastPCInforNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorPcOk));
                lastPCInforStateTV.setText("已连接");
                lastPCInforStateTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedDeviceOK));
            } else {
                lastPCInforStateTV.setText("未连接");
                lastPCInforNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedFail));
                lastPCInforStateTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedFail));
            }
        } else {
            lastPCInforNameTV.setText("未选择");
            lastPCInforStateTV.setText("未连接");
            lastPCInforNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedFail));
            lastPCInforStateTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedFail));
            isLastUsedPCExistTV.setText("首次使用");
        }
        if(tempTV != null && !tempTV.ip.equals("")) {
            lastTVInforNameTV.setText(tempTV.nickName);
            isLastUsedTVExistTV.setText("上次使用");
            if(MyApplication.selectedTVVerified && MyApplication.isSelectedTVOnline()) {
                lastTVInforNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorTvOk));
                lastTVInforStateTV.setText("已连接");
                lastTVInforStateTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedDeviceOK));
            } else {
                lastTVInforStateTV.setText("未连接");
                lastTVInforNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedFail));
                lastTVInforStateTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedFail));
            }
        } else {
            lastTVInforNameTV.setText("未选择");
            lastTVInforStateTV.setText("未连接");
            lastTVInforNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedFail));
            lastTVInforStateTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedFail));
            isLastUsedTVExistTV.setText("首次使用");
        }

        viewOk = true;
    }

    private void setDevice(IPInforBean chosenDevice, String deviceType) {
        if (deviceType.equals("PC")) {
            lastPCInforNameTV.setText(chosenDevice.nickName);
            lastPCInforStateTV.setText("已连接");
            lastPCInforNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorPcOk));
            lastPCInforStateTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedDeviceOK));
            String pcJsonString = JsonUitl.objectToString(chosenDevice);
            new PreferenceUtil(mContext.getApplicationContext()).write("lastChosenPC", pcJsonString);
        } else if (deviceType.equals("TV")) {
            lastTVInforNameTV.setText(chosenDevice.nickName);
            lastTVInforStateTV.setText("已连接");
            lastTVInforNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorTvOk));
            lastTVInforStateTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorSelectedDeviceOK));
            String tvJsonString = JsonUitl.objectToString(chosenDevice);
            new PreferenceUtil(mContext.getApplicationContext()).write("lastChosenTV", tvJsonString);
        }
    }

    public void setUserName(Message msg){
        String userName = (String)msg.obj;
        id_top01_userName.setText(userName);
    }

    private void initEvent() {
        PCDevicesLL.setOnClickListener(this);
        TVDevicesLL.setOnClickListener(this);
        blueDevicesLL.setOnClickListener(this);
        lastPCInforLL.setOnClickListener(this);
        lastTVInforLL.setOnClickListener(this);
        settingLL.setOnClickListener(this);
        helpInfo.setOnClickListener(this);


        bannerB.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                startActivity(new Intent(mContext, StartActivity.class));
            }
        });
        TVRecentAppSGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(MyApplication.isSelectedTVOnline()) {
                    TVAppHelper.openApp(tvpcAppHelper.tvApps.get(i).getPackageName());
                    Toasty.success(mContext, "应用即将打开, 请观看电视", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.warning(mContext, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
        PCRecentAppSGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(MyApplication.isSelectedPCOnline()) {

                    if(MyApplication.isSelectedTVOnline()) {
                        if(MyApplication.AccessibilityIsOpen){
                            TVAppHelper.openTVRDP();

                            // 需要用户手动点击进入
                            PCAppHelper.setCurrentMode(PCAppHelper.RDPMODE);
                            PCAppHelper.openApp_Rdp(tvpcAppHelper.pcApps.get(i).getPackageName(),
                                    PCAppHelper.appList.get(i).getAppName(), myHandler);
                        }else {
                            TVAppHelper.openTVAccessibility();
                            Toast.makeText(getContext(),"请在TV上对[AreaParty]授权",Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toasty.warning(mContext, "应用即将投放的屏幕不在线", Toast.LENGTH_SHORT, true).show();
                    }
                } else {
                    Toasty.warning(mContext, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
        scrollView.smoothScrollTo(0,20);
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            }
        }
    };



}
