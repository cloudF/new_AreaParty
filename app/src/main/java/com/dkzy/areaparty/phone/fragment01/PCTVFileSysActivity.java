package com.dkzy.areaparty.phone.fragment01;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.utils.*;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.DiskInformat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedDiskListFormat;

import es.dmoral.toasty.Toasty;

public class PCTVFileSysActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    private ListView page04DiskListLV;                   // 磁盘列表
    private LinearLayout page04DiskListActionBarLL;      // 磁盘列表的操作栏
    private LinearLayout page04DiskListRefreshLL;        // 磁盘列表操作栏的刷新按钮
    private LinearLayout page04DiskListMoreLL;           // 磁盘列表操作栏的更多按钮
    private LinearLayout page04CopyBarLL;                // 磁盘列表界面显示的复制操作栏
    private LinearLayout page04CopyCancelLL;             // 磁盘列表界面显示的复制栏中的取消按钮
    private LinearLayout page04CutBarLL;                 // 磁盘列表界面显示的移动操作栏
    private LinearLayout page04CutCancelLL;              // 磁盘列表界面显示的移动栏中的取消按钮

    private MyDiskAdapater diskAdapater;

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcnasfile_sys);

        initViews();
        initevents();
    }

    private void initevents() {
        page04DiskListRefreshLL.setOnClickListener(this);
        page04DiskListMoreLL.setOnClickListener(this);
        page04CopyCancelLL.setOnClickListener(this);
        page04CutCancelLL.setOnClickListener(this);


        page04DiskListLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ReceivedFileManagerMessageFormat fileManagerMessage = (ReceivedFileManagerMessageFormat)
                                com.androidlearning.boris.familycentralcontroler.fragment01.utils.prepareDataForFragment.getFileActionStateData_tv(OrderConst.folderAction_name,
                                        OrderConst.folderAction_openInComputer_command, PCFileSysActivity.tvDatasList_available.get(i).rootDirectory);
                    }
                }).start();*/
                Bundle data = new Bundle();
                data.putString("diskName", PCFileSysActivity.tvDatasList_available.get(i).rootDirectory);
                Intent intent = new Intent(PCTVFileSysActivity.this, diskTVContentActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }});
    }

    private void initViews() {
        page04DiskListLV          = (ListView) this.findViewById(R.id.page04DiskListLV);
        page04DiskListActionBarLL = (LinearLayout) this.findViewById(R.id.page04DiskListActionBarLL);
        page04DiskListRefreshLL   = (LinearLayout) this.findViewById(R.id.page04DiskListRefreshLL);
        page04DiskListMoreLL      = (LinearLayout) this.findViewById(R.id.page04DiskListMoreLL);
        page04CopyBarLL           = (LinearLayout) this.findViewById(R.id.page04CopyBarLL);
        page04CopyCancelLL        = (LinearLayout) this.findViewById(R.id.page04CopyCancelLL);
        page04CutBarLL            = (LinearLayout) this.findViewById(R.id.page04CutBarLL);
        page04CutCancelLL         = (LinearLayout) this.findViewById(R.id.page04CutCancelLL);


        diskAdapater = new MyDiskAdapater(this);
        page04DiskListLV.setAdapter(diskAdapater);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if(PCFileHelper.isCopy()) {
            page04DiskListActionBarLL.setVisibility(View.GONE);
            page04CopyBarLL.setVisibility(View.VISIBLE);
            page04CutBarLL.setVisibility(View.GONE);
        } else if(PCFileHelper.isCut()) {
            page04DiskListActionBarLL.setVisibility(View.GONE);
            page04CopyBarLL.setVisibility(View.GONE);
            page04CutBarLL.setVisibility(View.VISIBLE);
        } else if(PCFileHelper.isInitial()) {
            page04DiskListActionBarLL.setVisibility(View.VISIBLE);
            page04CopyBarLL.setVisibility(View.GONE);
            page04CutBarLL.setVisibility(View.GONE);
        }*/
        diskAdapater.notifyDataSetChanged();
        Log.e("page04Fragment", "resumeLoadDisks");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.page04DiskListRefreshLL:
                loadDiskInTV();
                break;
            case R.id.page04DiskListMoreLL:
                Toasty.info(getBaseContext(), "暂无更多", Toast.LENGTH_SHORT, true).show();
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

                break;
        }
    }

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
            return PCFileSysActivity.tvDatasList_available.size();
        }

        @Override
        public Object getItem(int i) {
            return PCFileSysActivity.tvDatasList_available.get(i);
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

            DiskInformat fileBeanTemp = PCFileSysActivity.tvDatasList_available.get(i);

            String infor =  fileBeanTemp.volumeLabel + " " + fileBeanTemp.totalFreeSpace + "G/" + fileBeanTemp.totalSize + "G";
            String tabel = fileBeanTemp.name + "盘";
            holder.nameView.setText(tabel);
            holder.inforView.setText(infor);


            holder.typeView.setImageResource(R.drawable.frag04_driver_usb_icon);
            holder.typeView.setPadding(10, 10, 10, 10);


            return view;
        }
    }
    /*
    private void deleteDialog(final DiskInformat disk) {
        final DeleteDialog deleteDialog = new DeleteDialog(this);
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.show();
        deleteDialog.setTitleText("是否取消映射");
        deleteDialog.setPositiveButtonText("确定");
        deleteDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
                Log.w(TAG,disk.rootDirectory);
                PCFileHelper.deleteNAS(disk.rootDirectory, handler);
            }
        });
        deleteDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });
    }

*/
    public  void loadDiskInTV() {
        if(MyApplication.isSelectedTVOnline() && MyApplication.selectedTVVerified) {
            PCFileSysActivity.tvDatasList.clear();
            new Thread(){
                @Override
                public void run() {
                    try {
                        ReceivedDiskListFormat disks = (ReceivedDiskListFormat)
                                com.dkzy.areaparty.phone.fragment01.utils.prepareDataForFragment.getDiskActionStateData_tv(
                                        OrderConst.diskAction_name,
                                        OrderConst.diskAction_get_command, "");
                        if(disks.getStatus() == OrderConst.success) {
                            PCFileSysActivity.tvDatasList.addAll(disks.getData());

                            PCFileSysActivity.tvDatasList_available.clear();
                            for (DiskInformat disk : PCFileSysActivity.tvDatasList){
                                if (disk.totalSize > 0){
                                    PCFileSysActivity.tvDatasList_available.add(disk);
                                }
                            }
                            Log.w("PCTVFileSysActivity",PCFileSysActivity.tvDatasList_available.size()+"");
                            page04DiskListLV.post(new Runnable() {
                                @Override
                                public void run() {
                                    diskAdapater.notifyDataSetChanged();
                                }
                            });
                        }
                    } catch (Exception e) {

                    }
                }
            }.start();
        }
    }
}
