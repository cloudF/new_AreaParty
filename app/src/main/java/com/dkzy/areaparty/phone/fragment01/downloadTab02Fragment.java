package com.dkzy.areaparty.phone.fragment01;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dkzy.areaparty.phone.FileTypeConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.base.BaseFragment;
import com.dkzy.areaparty.phone.fragment01.base.DownloadedAdapter;
import com.dkzy.areaparty.phone.fragment01.model.downloadedFileBean;
import com.dkzy.areaparty.phone.fragment01.ui.DeleteDialog;
import com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.db.DownloadManager;
import com.lzy.okgo.model.Progress;

import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper.addLocalFiles;
import static com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper.getDuration;
import static com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper.getLocalFiles;

/**
 * Created by borispaul on 17-4-27.
 */

public class downloadTab02Fragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context context;
    private RecyclerView recyclerView;
    private LinearLayout downloadedNothingLL;
    private SwipeRefreshLayout downloaededFileSRL;
    private DownloadedAdapter adapter;
    private List<downloadedFileBean> allfiles;

    @Override
    public void onRefresh() {
        List<Progress> allTasks = DownloadManager.getInstance().getDownloading();
        addLocalFiles(allTasks);
        getDuration();

        adapter.notifyDataSetChanged();
        downloaededFileSRL.setRefreshing(false);
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }


    public void changeBgView() {
        if(allfiles.size() == 0)
            downloadedNothingLL.setVisibility(View.VISIBLE);
        else downloadedNothingLL.setVisibility(View.GONE);
    }

    public void dataChangeNotify() {
        if(adapter != null){
            allfiles = getLocalFiles();
            adapter.notifyDataSetChanged();
        }

    }

    public void dlnaNotify(boolean state) {
        try{
            if(state) {
                Toasty.info(context, "投屏成功", Toast.LENGTH_SHORT).show();
            } else {
                Toasty.error(context, "投屏失败", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * <summary>
     *  显示删除本地文件对话框并执行相应监听操作
     * </summary>
     */
    private void deleteFileDialog(final String name, final int index) {
        final DeleteDialog deleteDialog = new DeleteDialog(context);
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.show();
        deleteDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });
        deleteDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadFileManagerHelper.removeFile(name);
                deleteDialog.dismiss();
                allfiles.remove(index);
                adapter.notifyItemRemoved(index);
            }
        });
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab04_downloaded_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.downloadedFileRV);
        downloadedNothingLL = (LinearLayout) view.findViewById(R.id.downloadedNothingLL);
        downloaededFileSRL = (SwipeRefreshLayout) view.findViewById(R.id.downloaededFileSRL);
        downloaededFileSRL.setColorSchemeResources(R.color.colorPrimary);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.e("circle1", "ActivityCreated");
        super.onActivityCreated(savedInstanceState);
        allfiles = getLocalFiles();
        changeBgView();

        downloaededFileSRL.setOnRefreshListener(this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new DownloadedAdapter(this, context, allfiles);
        adapter.isFirstOnly(false);

        adapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                switch(view.getId()) {
                    case R.id.downloadMediaFileCastLL:
                        //投屏代码...
                        int netState = MyApplication.getmNetWorkState();
                        if(netState == NetUtil.NETWORK_MOBILE || netState == NetUtil.NETWORK_NONE) {
                            Toasty.info(context, "网络出错, 请将设备和投屏接收设备连接到同一网络中再尝试投屏", Toast.LENGTH_SHORT, true).show();
                        } else if(MyApplication.isSelectedTVOnline()) {
                            DownloadFileManagerHelper.dlnaCast(allfiles.get(i));
                        } else {
                            Toasty.info(context, "投屏接收设备未在线", Toast.LENGTH_SHORT, true).show();
                        }
                        break;
                    case R.id.downloadFileDeleteLL:
                        // 删除文件代码....
                        deleteFileDialog(allfiles.get(i).getName(), i);
                        break;
                    case R.id.downloadedVideoPlayingIconIV:
                    case R.id.downloadedFileImageIV: {
                        int fileType = allfiles.get(i).getFileType();
                        openFileActivity(fileType, allfiles.get(i));
                    }
                    break;
                }
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void openFileActivity(int fileType, downloadedFileBean file) {
        switch (fileType) {
            case FileTypeConst.pic: {
                String imgPath = file.getPath();
                startActivity(DownloadFileManagerHelper.openPicFileIntent(imgPath, context));
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } break;
            case FileTypeConst.music: {
                startActivity(DownloadFileManagerHelper.openAudioFileIntent(file.getPath()));
            } break;
            case FileTypeConst.video: {
                startActivity(DownloadFileManagerHelper.openVideoFileIntent(file.getPath()));
            } break;
            case FileTypeConst.apk: {
                startActivity(DownloadFileManagerHelper.openApkFileIntent(file.getPath()));
            } break;
            case FileTypeConst.excel: {
                startActivity(DownloadFileManagerHelper.openExcelFileIntent(file.getPath()));
            } break;
            case FileTypeConst.pdf: {
                startActivity(DownloadFileManagerHelper.openPdfFileIntent(file.getPath()));
            } break;
            case FileTypeConst.ppt: {
                startActivity(DownloadFileManagerHelper.openPPTFileIntent(file.getPath()));
            } break;
            case FileTypeConst.word: {
                startActivity(DownloadFileManagerHelper.openWordFileIntent(file.getPath()));
            } break;
            case FileTypeConst.txt: {
                startActivity(DownloadFileManagerHelper.openTextFileIntent(file.getPath()));
            } break;
            case FileTypeConst.none:
            case FileTypeConst.zip:
                Toasty.info(context, "暂不支持的文件类型", Toast.LENGTH_SHORT, true).show();
                break;
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }


}
