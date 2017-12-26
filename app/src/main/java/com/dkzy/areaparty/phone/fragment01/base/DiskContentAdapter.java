package com.dkzy.areaparty.phone.fragment01.base;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.FileTypeConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.diskContentActivity;
import com.dkzy.areaparty.phone.fragment01.model.fileBean;
import com.dkzy.areaparty.phone.fragment01.utils.PCFileHelper;
import com.dkzy.areaparty.phone.fragment01.utils.prepareDataForFragment;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by borispaul on 17-5-6.
 */

public class DiskContentAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<fileBean> datas;

    public DiskContentAdapter(Context context, List<fileBean> datas) {
        this.context = context;
        this.datas = datas;
        this.inflater = LayoutInflater.from(context);
        Log.w("DiskContentAdapter",context.toString());
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null) {
            view = inflater.inflate(R.layout.tab04_file_item, null);
            holder = new ViewHolder();
            holder.typeView  = (ImageView) view.findViewById(R.id.image_type);
            holder.nameView  = (TextView) view.findViewById(R.id.text_name);
            holder.inforView = (TextView) view.findViewById(R.id.fileInformation);
            holder.checkBox  = (CheckBox) view.findViewById(R.id.checkbox);
            holder.imageView = (ImageView) view.findViewById(R.id.playMediaIV) ;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    datas.get(i).isChecked = true;
                    int selectedNum = 0;
                    for (fileBean file:datas) {
                        if(file.isChecked)
                            selectedNum++;
                    }
                    if(selectedNum == datas.size()) {
                        ((ImageView)((diskContentActivity)(context)).findViewById(R.id.bar02IconSelectAllIV)).setImageResource(R.drawable.selectedall_pressed);
                        ((TextView)((diskContentActivity)(context)).findViewById(R.id.bar02TxSelectAllTV)).setText("取消全选");
                    } else {
                        ((ImageView)((diskContentActivity)(context)).findViewById(R.id.bar02IconSelectAllIV)).setImageResource(R.drawable.selectedall_normal);
                        ((TextView)((diskContentActivity)(context)).findViewById(R.id.bar02TxSelectAllTV)).setText("全选");
                    }
                    ((diskContentActivity)(context)).findViewById(R.id.bar02CopyLL).setClickable(true);
                    ((diskContentActivity)(context)).findViewById(R.id.bar02CutLL).setClickable(true);
                    ((diskContentActivity)(context)).findViewById(R.id.bar02DeleteLL).setClickable(true);
                    ((ImageView)((diskContentActivity)(context)).findViewById(R.id.bar02IconCopyIV)).setImageResource(R.drawable.copy_normal);
                    ((ImageView)((diskContentActivity)(context)).findViewById(R.id.bar02IconCutIV)).setImageResource(R.drawable.cut_normal);
                    ((ImageView)((diskContentActivity)(context)).findViewById(R.id.bar02IconDeleteIV)).setImageResource(R.drawable.delete_normal);
                    ((TextView)((diskContentActivity)(context)).findViewById(R.id.bar02TxCopyTV)).setTextColor(Color.rgb(128, 128, 128));
                    ((TextView)((diskContentActivity)(context)).findViewById(R.id.bar02TxCutTV)).setTextColor(Color.rgb(128, 128, 128));
                    ((TextView)((diskContentActivity)(context)).findViewById(R.id.bar02TxDeleteTV)).setTextColor(Color.rgb(128, 128, 128));
                } else {
                    datas.get(i).isChecked = false;
                    int selectedNum = 0;
                    for (fileBean file:datas) {
                        if(file.isChecked)
                            selectedNum++;
                    }
                    ((ImageView)((diskContentActivity)(context)).findViewById(R.id.bar02IconSelectAllIV)).setImageResource(R.drawable.selectedall_normal);
                    ((TextView)((diskContentActivity)(context)).findViewById(R.id.bar02TxSelectAllTV)).setText("全选");
                    if(selectedNum < 1) {
                        ((diskContentActivity)(context)).findViewById(R.id.bar02CopyLL).setClickable(false);
                        ((diskContentActivity)(context)).findViewById(R.id.bar02CutLL).setClickable(false);
                        ((diskContentActivity)(context)).findViewById(R.id.bar02DeleteLL).setClickable(false);
                        ((ImageView)((diskContentActivity)(context)).findViewById(R.id.bar02IconCopyIV)).setImageResource(R.drawable.copy_pressed);
                        ((ImageView)((diskContentActivity)(context)).findViewById(R.id.bar02IconCutIV)).setImageResource(R.drawable.cut_pressed);
                        ((ImageView)((diskContentActivity)(context)).findViewById(R.id.bar02IconDeleteIV)).setImageResource(R.drawable.delete_pressed);
                        ((TextView)((diskContentActivity)(context)).findViewById(R.id.bar02TxCopyTV)).setTextColor(Color.rgb(211, 211, 211));
                        ((TextView)((diskContentActivity)(context)).findViewById(R.id.bar02TxCutTV)).setTextColor(Color.rgb(211, 211, 211));
                        ((TextView)((diskContentActivity)(context)).findViewById(R.id.bar02TxDeleteTV)).setTextColor(Color.rgb(211, 211, 211));
                    }
                }
            }
        });
        try{
            fileBean fileBeanTemp = datas.get(i);

            if(fileBeanTemp.isShow) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.imageView.setVisibility(View.GONE);
                holder.checkBox.setClickable(true);
                if(fileBeanTemp.isChecked) {
                    holder.checkBox.setChecked(true);
                } else {
                    holder.checkBox.setChecked(false);
                }
            } else {
                holder.checkBox.setVisibility(View.GONE);
                holder.imageView.setVisibility(View.GONE);
                holder.checkBox.setClickable(false);
            }
            setHolder(holder, fileBeanTemp);
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    private void setHolder(ViewHolder holder, final fileBean file) {
        String infor;
        switch (file.type) {
            case FileTypeConst.folder:
                infor = "文件: " + file.subNum;
                holder.typeView.setImageResource(R.drawable.folder);
                holder.nameView.setText(file.name);
                holder.inforView.setText(infor);
                // 其他操作。。。
                break;
            case FileTypeConst.excel:
                infor = file.lastChangeTime + " " + file.size + "KB";
                holder.typeView.setImageResource(R.drawable.excel);
                holder.nameView.setText(file.name);
                holder.inforView.setText(infor);
                // 其他操作。。。
                break;
            case FileTypeConst.word:
                infor = file.lastChangeTime + " " + file.size + "KB";
                holder.typeView.setImageResource(R.drawable.word);
                holder.nameView.setText(file.name);
                holder.inforView.setText(infor);
                // 其他操作。。。
                break;
            case FileTypeConst.ppt:
                infor = file.lastChangeTime + " " + file.size + "KB";
                holder.typeView.setImageResource(R.drawable.ppt);
                holder.nameView.setText(file.name);
                holder.inforView.setText(infor);
                // 其他操作。。。
                break;
            case FileTypeConst.music:
                infor = file.lastChangeTime + " " + file.size + "KB";
                holder.typeView.setImageResource(R.drawable.music);
                holder.nameView.setText(file.name);
                holder.inforView.setText(infor);
                // 其他操作。。。
                break;
            case FileTypeConst.pdf:
                infor = file.lastChangeTime + " " + file.size + "KB";
                holder.typeView.setImageResource(R.drawable.pdf);
                holder.nameView.setText(file.name);
                holder.inforView.setText(infor);
                // 其他操作。。。
                break;
            case FileTypeConst.video:
                infor = file.lastChangeTime + " " + file.size + "KB";
                holder.typeView.setImageResource(R.drawable.video);
                holder.nameView.setText(file.name);
                holder.inforView.setText(infor);
                if (file.isShow){
                    holder.imageView.setVisibility(View.GONE);
                }else{
                    holder.imageView.setVisibility(View.VISIBLE);
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (MyApplication.isSelectedTVOnline()){//是视频则可以播放
                                if (context.toString().contains("diskContentActivity")){
                                    PCFileHelper.playMedia(file);
                                }else if (context.toString().contains("diskTVContentActivity")){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            prepareDataForFragment.getDlnaCastState(file,"video");
                                        }
                                    }).start();

                                }

                            }else {
                                Toasty.warning(context, "当前电视不在线", Toast.LENGTH_SHORT, true).show();

                        }
                        }
                    });
                }
                // 其他操作。。。
                break;
            case FileTypeConst.zip:
                infor = file.lastChangeTime + " " + file.size + "KB";
                holder.typeView.setImageResource(R.drawable.zip);
                holder.nameView.setText(file.name);
                holder.inforView.setText(infor);
                // 其他操作。。。
                break;
            case FileTypeConst.txt:
                infor = file.lastChangeTime + " " + file.size + "KB";
                holder.typeView.setImageResource(R.drawable.txt);
                holder.nameView.setText(file.name);
                holder.inforView.setText(infor);
                // 其他操作。。。
                break;
            case FileTypeConst.pic:
                infor = file.lastChangeTime + " " + file.size + "KB";
                holder.typeView.setImageResource(R.drawable.pic);
                holder.nameView.setText(file.name);
                holder.inforView.setText(infor);
                // 其他操作。。。
                break;
            case FileTypeConst.none:
                infor = file.lastChangeTime + " " + file.size + "KB";
                holder.typeView.setImageResource(R.drawable.none);
                holder.nameView.setText(file.name);
                holder.inforView.setText(infor);

                // 其他操作。。。
                break;
        }
    }

    class ViewHolder {
        ImageView typeView;
        TextView nameView;
        TextView inforView;
        CheckBox checkBox;
        ImageView imageView;
    }
}


