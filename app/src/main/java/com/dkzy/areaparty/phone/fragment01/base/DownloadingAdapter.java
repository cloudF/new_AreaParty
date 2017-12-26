package com.dkzy.areaparty.phone.fragment01.base;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.FileTypeConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.downloadTab01Fragment;
import com.dkzy.areaparty.phone.fragment01.model.DownloadFileModel;
import com.dkzy.areaparty.phone.fragment01.model.downloadedFileBean;
import com.dkzy.areaparty.phone.fragment01.ui.NumberProgressBar;
import com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetUtil;
import com.lzy.okgo.db.DownloadManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import info.hoang8f.widget.FButton;

/**
 * Created by borispaul on 17-5-9.
 */

public class DownloadingAdapter extends RecyclerView.Adapter<DownloadingAdapter.ViewHolder> {

    public static final int TYPE_ALL = 0;
    public static final int TYPE_FINISH = 1;
    public static final int TYPE_ING = 2;

    private Context context;
    private downloadTab01Fragment tab;
    private List<DownloadTask> values;
    private NumberFormat numberFormat;
    private LayoutInflater inflater;
    private int type;

    public DownloadingAdapter(Context context, downloadTab01Fragment tab) {
        this.context = context;
        this.tab = tab;
        this.numberFormat = NumberFormat.getPercentInstance();
        this.numberFormat.setMinimumFractionDigits(2);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(int type) {
        this.type = type;
        if (type == TYPE_ALL) values = OkDownload.restore(DownloadManager.getInstance().getAll());
        if (type == TYPE_FINISH) values = OkDownload.restore(DownloadManager.getInstance().getFinished());
        if (type == TYPE_ING) values = OkDownload.restore(DownloadManager.getInstance().getDownloading());
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.tab04_downloading_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DownloadTask task = values.get(position)
                .register(new MyDownloadListener("MyDownloadListener_" + type, holder))
                .register(new LogDownloadListener());
        holder.setTask(task);
        holder.bind();
        holder.refresh(task.progress);
    }

    public void unRegister() {
        Map<String, DownloadTask> taskMap = OkDownload.getInstance().getTaskMap();
        for (DownloadTask task : taskMap.values()) {
            task.unRegister("MyDownloadListener_" + type);
        }
    }

    @Override
    public int getItemCount() {
        return values == null ? 0 : values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private DownloadTask task;

        @BindView(R.id.downloadFileIconIV) ImageView icon;
        @BindView(R.id.downloadFileNameTV) TextView  name;
        @BindView(R.id.downloadSize) TextView downloadSize;
        @BindView(R.id.netSpeed) TextView netSpeed;
        @BindView(R.id.tvProgress) TextView tvProgress;
        @BindView(R.id.pbProgress)
        NumberProgressBar progressBar;
        @BindView(R.id.deleteFileIV) ImageButton deleteFileIV;
        @BindView(R.id.downloadFileStateB) FButton stateButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setTask(DownloadTask task) {
            this.task = task;
        }

        public void bind() {
            Progress progress = task.progress;
            DownloadFileModel fileModel = (DownloadFileModel) progress.extra1;
            if(fileModel != null) {
                setIcon(fileModel.getName());
                name.setText(fileModel.getName());
            } else {
                name.setText(progress.fileName);
            }
        }

        public void refresh(Progress progress) {
            String currentSize = Formatter.formatFileSize(context, progress.currentSize);
            String totalSize = Formatter.formatFileSize(context, progress.totalSize);
            downloadSize.setText(currentSize + "/" + totalSize);
            switch (progress.status) {
                case Progress.NONE:
                    netSpeed.setText("停止");
                    stateButton.setButtonColor(Color.parseColor("#e65757"));
                    stateButton.setText("下载");
                    stateButton.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case Progress.PAUSE:
                    netSpeed.setText("暂停中");
                    progressBar.setReachedBarColor(Color.parseColor("#e65757"));
                    progressBar.setProgressTextColor(Color.parseColor("#e65757"));
                    stateButton.setButtonColor(Color.parseColor("#2ecc71"));
                    stateButton.setText("继续");
                    stateButton.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case Progress.ERROR:
                    netSpeed.setText("下载出错");
                    stateButton.setButtonColor(Color.parseColor("#EE0000"));
                    stateButton.setText("重试");
                    stateButton.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case Progress.WAITING:
                    netSpeed.setText("等待中");
                    stateButton.setButtonColor(Color.parseColor("#40E0D0"));
                    stateButton.setText("等待");
                    stateButton.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case Progress.FINISH:
                    netSpeed.setText("下载完成");
                    stateButton.setButtonColor(Color.parseColor("#40E0D0"));
                    stateButton.setText("已下载");
                    stateButton.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case Progress.LOADING:
                    progressBar.setReachedBarColor(Color.parseColor("#2ecc71"));
                    progressBar.setProgressTextColor(Color.parseColor("#2ecc71"));
                    String networkSpeed = Formatter.formatFileSize(context, progress.speed);
                    netSpeed.setText(String.format("%s/s", networkSpeed));
                    stateButton.setButtonColor(Color.parseColor("#ecf0f1"));
                    stateButton.setText("暂停");
                    stateButton.setTextColor(Color.parseColor("#000000"));
                    break;
            }
            tvProgress.setText(numberFormat.format(progress.fraction));
            progressBar.setMax(10000);
            progressBar.setProgress((int) (progress.fraction * 10000));
        }

        private void setIcon(String fileName) {
            int fileType = FileTypeConst.determineFileType(fileName);
            switch (fileType) {
                case FileTypeConst.excel:
                    icon.setImageResource(R.drawable.exceltest);
                    break;
                case FileTypeConst.music:
                    icon.setImageResource(R.drawable.musictest);
                    break;
                case FileTypeConst.none:
                    icon.setImageResource(R.drawable.nonetest);
                    break;
                case FileTypeConst.pdf:
                    icon.setImageResource(R.drawable.pdftest);
                    break;
                case FileTypeConst.pic:
                    icon.setImageResource(R.drawable.picturetest);
                    break;
                case FileTypeConst.ppt:
                    icon.setImageResource(R.drawable.ppttest);
                    break;
                case FileTypeConst.txt:
                    icon.setImageResource(R.drawable.txttest);
                    break;
                case FileTypeConst.video:
                    icon.setImageResource(R.drawable.videotest);
                    break;
                case FileTypeConst.word:
                    icon.setImageResource(R.drawable.wordtest);
                    break;
                case FileTypeConst.zip:
                    icon.setImageResource(R.drawable.rartest);
                    break;
            }
        }

        @OnClick(R.id.deleteFileIV)
        public void deleteTask() {
            task.remove();
            updateData(type);
        }

        @OnClick(R.id.downloadFileStateB)
        public void stateButtonClick() {
            Progress progress = task.progress;
            switch (progress.status) {
                case Progress.PAUSE:
                case Progress.ERROR:
                case Progress.NONE:
                    int netState = MyApplication.getmNetWorkState();
                    if(netState == NetUtil.NETWORK_WIFI) {
                        task.start();
                    } else {
                        Toasty.error(context, "网络异常", Toast.LENGTH_SHORT, true).show();
                    }
                    break;
                case Progress.LOADING:
                    task.pause();
                    break;
            }
        }

    }


    class MyDownloadListener extends DownloadListener {
        private ViewHolder holder;

        public MyDownloadListener(Object tag, ViewHolder holder) {
            super(tag);
            this.holder = holder;
        }

        @Override
        public void onStart(Progress progress) {}

        @Override
        public void onProgress(Progress progress) {
            holder.refresh(progress);
        }

        @Override
        public void onError(Progress progress) {
            Throwable throwable = progress.exception;
            if (throwable != null) throwable.printStackTrace();
        }

        @Override
        public void onFinish(File fileTmp, Progress progress) {
            holder.deleteTask();
            tab.changeView();

            long createTime = System.currentTimeMillis();
            String fileName = progress.fileName;
            String totalLength = Formatter.formatFileSize(context, progress.totalSize);
            int fileType = FileTypeConst.determineFileType(fileName);
            downloadedFileBean file = new downloadedFileBean(fileName, totalLength, createTime, 0, fileType, "");
            DownloadFileManagerHelper.addLocalFiles(file);
        }

        @Override
        public void onRemove(Progress progress) {

        }
    }
}


