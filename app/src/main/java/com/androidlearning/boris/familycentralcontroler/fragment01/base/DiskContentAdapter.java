package com.androidlearning.boris.familycentralcontroler.fragment01.base;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidlearning.boris.familycentralcontroler.FileTypeConst;
import com.androidlearning.boris.familycentralcontroler.R;
import com.androidlearning.boris.familycentralcontroler.fragment01.diskContentActivity;
import com.androidlearning.boris.familycentralcontroler.fragment01.model.fileBean;

import java.util.List;

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

        fileBean fileBeanTemp = datas.get(i);
        if(fileBeanTemp.isShow) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setClickable(true);
            if(fileBeanTemp.isChecked) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.checkBox.setClickable(false);
        }
        setHolder(holder, fileBeanTemp);

        return view;
    }

    private void setHolder(ViewHolder holder, fileBean file) {
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
    }
}


