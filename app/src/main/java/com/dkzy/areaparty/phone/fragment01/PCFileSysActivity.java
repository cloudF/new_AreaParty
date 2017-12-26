package com.dkzy.areaparty.phone.fragment01;




import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.androideventbusutils.events.SelectedDeviceChangedEvent;
import com.dkzy.areaparty.phone.androideventbusutils.events.TVPCNetStateChangeEvent;
import com.dkzy.areaparty.phone.fragment01.utils.PCFileHelper;
import com.dkzy.areaparty.phone.fragment01.utils.prepareDataForFragment;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.DiskInformat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedDiskListFormat;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetBroadcastReceiver;
import com.lzy.okserver.OkDownload;
import com.wang.avi.AVLoadingIndicatorView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
/**
 * Project Name： FamilyCentralControler
 * Description: PC文件系统
 * Author: boris
 * Time: 2017/1/6 16:20
 * PCFileSysActivity
 */

public class PCFileSysActivity extends Activity implements View.OnClickListener, NetBroadcastReceiver.netEventHandler {
    private AVLoadingIndicatorView page04LoadingAVLIV;   // 上方加载动画的控件
    private ListView page04DiskListLV;                   // 磁盘列表
    private LinearLayout page04LocalFolderRootLL;        // 本地文件的根布局(包含分界线)
    private LinearLayout page04LocalFolderLL;            // 本地文件按钮区域
    private LinearLayout page04SharedFilesRootLL;        // 我分享的文件的根布局(包含分界线)
    private LinearLayout page04SharedFilesLL;            // 我分享的文件的按钮区域
    private LinearLayout page04DiskListActionBarLL;      // 磁盘列表的操作栏
    private LinearLayout page04DiskListRefreshLL;        // 磁盘列表操作栏的刷新按钮
    private LinearLayout page04DiskListMoreLL;           // 磁盘列表操作栏的更多按钮
    private LinearLayout page04CopyBarLL;                // 磁盘列表界面显示的复制操作栏
    private LinearLayout page04CopyCancelLL;             // 磁盘列表界面显示的复制栏中的取消按钮
    private LinearLayout page04CutBarLL;                 // 磁盘列表界面显示的移动操作栏
    private LinearLayout page04CutCancelLL;              // 磁盘列表界面显示的移动栏中的取消按钮
    private LinearLayout page04NASRootLL;                //NAS文件管理
    private LinearLayout page04TVFileRootLL;             //电视可移动磁盘管理

    private Context context;

    public static final String disk_SYS = "Fixed_SYS";
    public static final String disk_Fixed = "Fixed";
    public static final String disk_Removable = "Removable";
    public static final  String disk_Network = "Network";
    public static DiskInformat[] diskDatas;
    public static List<DiskInformat> diskDatasList = new ArrayList<>();
    public static List<DiskInformat> diskNetworkDatasList = new ArrayList<>();
    public static List<DiskInformat> tvDatasList = new ArrayList<>();
    public static List<DiskInformat> tvDatasList_available = new ArrayList<>();
    private MyDiskAdapater diskAdapater;


    class ViewHolderDisk {
        ImageView typeView;
        TextView nameView;
        TextView inforView;
    }
    private class MyDiskAdapater extends BaseAdapter {
        LayoutInflater mInflater;
        public MyDiskAdapater(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return diskDatasList.size();
        }

        @Override
        public Object getItem(int i) {
            return diskDatasList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolderDisk holder;
            if(view == null) {
                view = mInflater.inflate(R.layout.tab04_disklist_item, null);
                holder = new ViewHolderDisk();
                holder.typeView  = (ImageView) view.findViewById(R.id.diskImage);
                holder.nameView  = (TextView) view.findViewById(R.id.diskName);
                holder.inforView = (TextView) view.findViewById(R.id.diskInfor);
                view.setTag(holder);
            } else {
                holder = (ViewHolderDisk) view.getTag();
            }

            DiskInformat fileBeanTemp = diskDatasList.get(i);

            String infor =  fileBeanTemp.volumeLabel + " " + fileBeanTemp.totalFreeSpace + "G/" + fileBeanTemp.totalSize + "G";
            String tabel = fileBeanTemp.name + "盘";
            holder.nameView.setText(tabel);
            holder.inforView.setText(infor);

            switch (fileBeanTemp.driveType) {
                case disk_SYS:
                    holder.typeView.setImageResource(R.drawable.frag04_driver_system_icon);
                    holder.typeView.setPadding(20, 20, 20, 20);
                    break;
                case disk_Fixed:
                    holder.typeView.setImageResource(R.drawable.frag04_driver_normal_icon);
                    break;
                case disk_Removable:
                    holder.typeView.setImageResource(R.drawable.frag04_driver_usb_icon);
                    holder.typeView.setPadding(10, 10, 10, 10);
                    break;
                case disk_Network:
                    holder.typeView.setImageResource(R.drawable.frag04_driver_usb_icon);
                    holder.typeView.setPadding(10, 10, 10, 10);
                    break;
            }
            return view;
        }
    }

    /**
     * <summary>
     * 监听在该activity中的返回按钮操作, 内部调用
     * </summary>
     */
    private void returnToParentFolder() {
        if(PCFileHelper.isCopy() || PCFileHelper.isCut()) {
            PCFileHelper.setIsCut(false);
            PCFileHelper.setIsCopy(false);
            PCFileHelper.setIsInitial(true);
            page04CopyBarLL.setVisibility(View.GONE);
            page04CutBarLL.setVisibility(View.GONE);
            page04DiskListActionBarLL.setVisibility(View.VISIBLE);
            page04LocalFolderRootLL.setVisibility(View.VISIBLE);
            if(Login.userId.equals(""))
                page04SharedFilesRootLL.setVisibility(View.GONE);
            else page04SharedFilesRootLL.setVisibility(View.VISIBLE);
        } else this.finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                returnToParentFolder();
                break;
        }
        return true;
    }

    /**
     * <summary>
     * 开启线程加载磁盘列表
     * </summary>
     */
    public void loadDisks() {
        if(diskDatas.length <= 0 && MyApplication.isSelectedPCOnline() && MyApplication.selectedPCVerified) {
            if(!page04LoadingAVLIV.isShown())
                page04LoadingAVLIV.show();
            new Thread(){
                @Override
                public void run() {
                    try {
                        ReceivedDiskListFormat disks = (ReceivedDiskListFormat)
                                prepareDataForFragment.getDiskActionStateData(
                                        OrderConst.diskAction_name,
                                        OrderConst.diskAction_get_command, "");
                        if(disks.getStatus() == OrderConst.success) {
                            diskDatas = new DiskInformat[disks.getData().size()];
                            disks.getData().toArray(diskDatas);

                            Message message = new Message();
                            message.what = OrderConst.getDiskList_order_successful;
                            myHandler.sendMessage(message);
                        } else {
                            Message message = new Message();
                            message.what = OrderConst.getDiskList_order_fail;
                            myHandler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        Message message = new Message();
                        message.what = OrderConst.getDiskList_order_fail;
                        myHandler.sendMessage(message);
                    }
                }
            }.start();
        }
    }
    public void loadDisks_tv() {
        if(tvDatasList.size() <=0 && MyApplication.isSelectedTVOnline() && MyApplication.selectedTVVerified) {
            new Thread(){
                @Override
                public void run() {
                    try {
                        ReceivedDiskListFormat disks = (ReceivedDiskListFormat)
                                prepareDataForFragment.getDiskActionStateData_tv(
                                        OrderConst.diskAction_name,
                                        OrderConst.diskAction_get_command, "");
                        if(disks.getStatus() == OrderConst.success) {
                            tvDatasList.addAll(disks.getData());
                            for (DiskInformat disk : tvDatasList){
                                if (disk.totalSize > 0){
                                    tvDatasList_available.add(disk);
                                }
                            }
                            Log.w("PCFileSysActivity",tvDatasList_available.size()+"");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }


    @Subscriber(tag = "selectedPCChanged")
    private void updatePCState(SelectedDeviceChangedEvent event) {
        PCFileHelper.setIsCut(false);
        PCFileHelper.setIsCopy(false);
        PCFileHelper.setIsInitial(true);
        onResume();
    }

    @Subscriber(tag = "selectedDeviceStateChanged")
    private void updateDeviceNetState(TVPCNetStateChangeEvent event) {
        if(event.isPCOnline())
            loadDisks();
        if (event.isTVOnline()){
            loadDisks_tv();
            if (!page04TVFileRootLL.isShown()){
                page04TVFileRootLL.setVisibility(View.VISIBLE);
            }
        }else {
            if (page04TVFileRootLL.isShown()){
                page04TVFileRootLL.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onNetChange() {
        //loadDisks();
        Log.e("page04Fragment", "netChangeLoadDisks");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.page04DiskListRefreshLL:
                diskNetworkDatasList.clear();
                diskDatasList.clear();
                diskDatas = new DiskInformat[0];
                tvDatasList.clear();
                tvDatasList_available.clear();
                diskAdapater.notifyDataSetChanged();
                loadDisks();
                loadDisks_tv();
                Log.e("page04Fragment", "刷新磁盘列表");
                break;
            case R.id.page04DiskListMoreLL:
                Toasty.info(context, "暂无更多", Toast.LENGTH_SHORT, true).show();
                // ... 更多操作
                break;
            case R.id.page04CopyCancelLL:
            case R.id.page04CutCancelLL:
                PCFileHelper.setIsCut(false);
                PCFileHelper.setIsCopy(false);
                PCFileHelper.setIsInitial(true);
                page04CopyBarLL.setVisibility(View.GONE);
                page04CutBarLL.setVisibility(View.GONE);
                page04DiskListActionBarLL.setVisibility(View.VISIBLE);
                page04LocalFolderRootLL.setVisibility(View.VISIBLE);
                if(Login.userId.equals(""))
                    page04SharedFilesRootLL.setVisibility(View.GONE);
                else page04SharedFilesRootLL.setVisibility(View.VISIBLE);
                break;
            case R.id.page04SharedFilesLL:
                Intent intent = new Intent(this, sharedFileIntentActivity.class);
                this.startActivity(intent);
                break;
            case R.id.page04LocalFolderLL:
                startActivity(new Intent(this, downloadActivity.class));
                break;
            case R.id.page04NASRootLL:
                if (diskNetworkDatasList.size()>0){
                    startActivity(new Intent(this,PCNASFileSysActivity.class));
                }else {
                    Toasty.info(this,"当前无NAS设备，请在电脑上配置NAS连接").show();
                }
                break;
            case R.id.page04TVFileRootLL:
                if (tvDatasList_available.size()>0){
                    startActivity(new Intent(this,PCTVFileSysActivity.class));
                }else {
                    Toasty.info(this,"当前电视上未插入u盘").show();
                }
                break;

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab04);
        context = MyApplication.getContext();

        OkDownload.getInstance().setFolder(Environment.getExternalStorageDirectory().getAbsolutePath() + "/areaparty/downloadedfiles/");
        OkDownload.getInstance().getThreadPool().setCorePoolSize(10);

        diskDatas = new DiskInformat[0];
        initView();
        initEvent();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(PCFileHelper.isCopy()) {
            page04DiskListActionBarLL.setVisibility(View.GONE);
            page04CopyBarLL.setVisibility(View.VISIBLE);
            page04CutBarLL.setVisibility(View.GONE);
            page04LocalFolderRootLL.setVisibility(View.GONE);
            page04SharedFilesRootLL.setVisibility(View.GONE);
        } else if(PCFileHelper.isCut()) {
            page04DiskListActionBarLL.setVisibility(View.GONE);
            page04CopyBarLL.setVisibility(View.GONE);
            page04CutBarLL.setVisibility(View.VISIBLE);
            page04LocalFolderRootLL.setVisibility(View.GONE);
            page04SharedFilesRootLL.setVisibility(View.GONE);
        } else if(PCFileHelper.isInitial()) {
            page04DiskListActionBarLL.setVisibility(View.VISIBLE);
            page04CopyBarLL.setVisibility(View.GONE);
            page04CutBarLL.setVisibility(View.GONE);
            page04LocalFolderRootLL.setVisibility(View.VISIBLE);
            loadDisks();
        }
        diskAdapater.notifyDataSetChanged();
        Log.e("page04Fragment", "resumeLoadDisks");

        if (MyApplication.isSelectedTVOnline() && MyApplication.selectedTVVerified){
            page04TVFileRootLL.setVisibility(View.VISIBLE);
        }else page04TVFileRootLL.setVisibility(View.GONE);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initView() {
        page04LocalFolderRootLL   = (LinearLayout) this.findViewById(R.id.page04LocalFolderRootLL);
        page04LocalFolderLL       = (LinearLayout) this.findViewById(R.id.page04LocalFolderLL);
        page04SharedFilesRootLL   = (LinearLayout) this.findViewById(R.id.page04SharedFilesRootLL);
        page04SharedFilesLL       = (LinearLayout) this.findViewById(R.id.page04SharedFilesLL);
        page04DiskListActionBarLL = (LinearLayout) this.findViewById(R.id.page04DiskListActionBarLL);
        page04DiskListRefreshLL   = (LinearLayout) this.findViewById(R.id.page04DiskListRefreshLL);
        page04DiskListMoreLL      = (LinearLayout) this.findViewById(R.id.page04DiskListMoreLL);
        page04CopyBarLL           = (LinearLayout) this.findViewById(R.id.page04CopyBarLL);
        page04DiskListLV          = (ListView) this.findViewById(R.id.page04DiskListLV);
        page04CopyCancelLL        = (LinearLayout) this.findViewById(R.id.page04CopyCancelLL);
        page04CutBarLL            = (LinearLayout) this.findViewById(R.id.page04CutBarLL);
        page04CutCancelLL         = (LinearLayout) this.findViewById(R.id.page04CutCancelLL);
        page04LoadingAVLIV        = (AVLoadingIndicatorView) this.findViewById(R.id.page04LoadingAVLIV);
        page04NASRootLL           = (LinearLayout) findViewById(R.id.page04NASRootLL);
        page04TVFileRootLL        = (LinearLayout) findViewById(R.id.page04TVFileRootLL);

        diskAdapater = new MyDiskAdapater(this);
        page04DiskListLV.setAdapter(diskAdapater);
    }

    private void initEvent() {
        page04DiskListRefreshLL.setOnClickListener(this);
        page04DiskListMoreLL.setOnClickListener(this);
        page04CopyCancelLL.setOnClickListener(this);
        page04CutCancelLL.setOnClickListener(this);
        page04SharedFilesLL.setOnClickListener(this);
        page04LocalFolderLL.setOnClickListener(this);
        page04NASRootLL.setOnClickListener(this);
        page04TVFileRootLL.setOnClickListener(this);
        NetBroadcastReceiver.mListeners.add(this);

        page04DiskListLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String initDisk = diskDatasList.get(i).name;
                // ...
                Bundle data = new Bundle();
                data.putString("diskName", initDisk);
                Intent intent = new Intent(PCFileSysActivity.this, diskContentActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }});
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderConst.getDiskList_order_successful:
                    // 如果用户未登录
                    if(Login.userId.equals("")) {
                        page04SharedFilesRootLL.setVisibility(View.GONE);
                    } else {
                        page04SharedFilesRootLL.setVisibility(View.VISIBLE);
                    }
                    page04LoadingAVLIV.hide();
                    page04NASRootLL.setVisibility(View.VISIBLE);
                    handleDatas();
                    diskAdapater.notifyDataSetChanged();
                    break;
                case OrderConst.getDiskList_order_fail:
                    diskNetworkDatasList.clear();
                    diskDatasList.clear();
                    diskDatas = new DiskInformat[0];
                    diskAdapater.notifyDataSetChanged();
                    // 如果用户未登录
                    if(Login.userId.equals("")) {
                        page04SharedFilesRootLL.setVisibility(View.GONE);
                    } else {
                        page04SharedFilesRootLL.setVisibility(View.GONE);
                    }
                    page04LoadingAVLIV.hide();
                    //Toasty.error(getActivity(), "获取磁盘列表失败，请刷新重试", Toast.LENGTH_SHORT, true).show();
                    break;
            }
        }
    };

    public static void handleDatas() {
        diskNetworkDatasList.clear();
        diskDatasList.clear();
        for (int i = 0; i<diskDatas.length; i++){
            DiskInformat fileBeanTemp = diskDatas[i];
            if (fileBeanTemp.driveType.equals(disk_Network)){
                diskNetworkDatasList.add(fileBeanTemp);
            }else {
                diskDatasList.add(fileBeanTemp);
            }
        }
    }
}
