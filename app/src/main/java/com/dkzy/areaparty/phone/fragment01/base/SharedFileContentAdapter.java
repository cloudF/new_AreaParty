package com.dkzy.areaparty.phone.fragment01.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dkzy.areaparty.phone.FileTypeConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.model.SharedfileBean;

import java.util.List;

/**
 * Created by borispaul on 17-4-12.
 *
 */

public class SharedFileContentAdapter extends BaseAdapter {
    private List<SharedfileBean> files;
    private Context context;
    private LayoutInflater inflater;

    public SharedFileContentAdapter(List<SharedfileBean> files, Context context) {
        this.files = files;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<SharedfileBean> getFiles() {
        return files;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int i) {
        return files.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null) {
            view = inflater.inflate(R.layout.tab04_sharedfile_item, null);
            holder = new ViewHolder();
            holder.sharedFileTypeIV  = (ImageView) view.findViewById(R.id.sharedFileTypeIV);
            holder.sharedFileNameTV  = (TextView) view.findViewById(R.id.sharedFileNameTV);
            holder.sharedFileSizeTV = (TextView) view.findViewById(R.id.sharedFileSizeTV);
            holder.sharedFileTimeTV  = (TextView) view.findViewById(R.id.sharedFileTimeTV);
            //holder.sharedFilePathTV = (TextView) view.findViewById(R.id.sharedFilePathTV);
            holder.sharedFileDesTV = (TextView) view.findViewById(R.id.sharedFileDesTV);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        SharedfileBean fileItem = files.get(i);
        String name = fileItem.name;
        String size = "(" + getSize(fileItem.size)+")";
        String time = fileItem.timeStr;
        //String path = fileItem.path;
        String des  = fileItem.des;
        holder.sharedFileNameTV.setText(name);
        holder.sharedFileSizeTV.setText(size);
        holder.sharedFileTimeTV.setText(time);
        //holder.sharedFilePathTV.setText(path);
        holder.sharedFileDesTV.setText(des);
        setImg(holder.sharedFileTypeIV, name);

        return view;
    }

    private void setImg(ImageView iv, String name) {
        int type = FileTypeConst.determineFileType(name);
        switch (type) {
            case FileTypeConst.excel:
                iv.setImageResource(R.drawable.excel);
                break;
            case FileTypeConst.word:
                iv.setImageResource(R.drawable.word);
                break;
            case FileTypeConst.ppt:
                iv.setImageResource(R.drawable.ppt);
                break;
            case FileTypeConst.music:
                iv.setImageResource(R.drawable.music);
                break;
            case FileTypeConst.pdf:
                iv.setImageResource(R.drawable.pdf);
                break;
            case FileTypeConst.video:
                iv.setImageResource(R.drawable.video);
                break;
            case FileTypeConst.zip:
                iv.setImageResource(R.drawable.zip);
                break;
            case FileTypeConst.txt:
                iv.setImageResource(R.drawable.txt);
                break;
            case FileTypeConst.pic:
                iv.setImageResource(R.drawable.pic);
                break;
            case FileTypeConst.apk:
                iv.setImageResource(R.mipmap.ic_launcher);
                break;
            case FileTypeConst.none:
                iv.setImageResource(R.drawable.none);
                break;
        }
    }

    class ViewHolder {
        ImageView sharedFileTypeIV;
        TextView sharedFileNameTV;
        TextView sharedFileSizeTV;
        TextView sharedFileTimeTV;
        //TextView sharedFilePathTV;
        TextView sharedFileDesTV;
    }

    public static String getSize(int size) {
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size*10 / 1024;
        }
        if (size < 10240) {
            //保留1位小数，
            return String.valueOf((size / 10)) + "."
                    + String.valueOf((size % 10)) + "MB";
        } else {
            //保留2位小数
            size = size * 10 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }
}

