package com.dkzy.areaparty.phone.fragment02.searchContent;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment02.Model.MediaItem;
import com.dkzy.areaparty.phone.fragment02.contentResolver.FileItem;
import com.dkzy.areaparty.phone.fragment02.searchContent.adapter.AudioItem;
import com.dkzy.areaparty.phone.fragment02.searchContent.adapter.ImageItem;
import com.dkzy.areaparty.phone.fragment02.searchContent.adapter.TypeLabel;
import com.dkzy.areaparty.phone.fragment02.searchContent.adapter.MyExpandableAdapter;
import com.dkzy.areaparty.phone.fragment02.searchContent.adapter.VideoItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.zpayh.adapter.IExpandable;
import xyz.zpayh.adapter.IMultiItem;
import xyz.zpayh.adapter.OnItemClickListener;

public class SearchMediaActivity extends AppCompatActivity {
    private final String TAG  = getClass().getSimpleName();
    private String mLabels[] = {"本地视频","电脑视频","本地音频","电脑音频","本地图片","电脑图片"};
    public static String headText = "";


    @BindView(R.id.search_editText)
    EditText searchET;
    @BindView(R.id.clear_text)
    TextView clearTextTV;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @OnClick(R.id.cancel)
    void close(){this.finish();}
    @OnClick(R.id.clear_text)
    void clearText(){searchET.getText().clear();}

    private MyExpandableAdapter mAdapter;
//    private GridLayoutManager mGridLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    List<IMultiItem> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_media);
        ButterKnife.bind(this);
        
        initEvent();
        initViews();
        initData();
    }

    private void initData() {

        if (SearchContainer.videoList.size()>0){
            TypeLabel label = new TypeLabel(mLabels[0]);
            data.add(label);
            List<IMultiItem> subData = new ArrayList<>();
            for (FileItem fileItem : SearchContainer.videoList){
                VideoItem videoItem = new VideoItem(fileItem,this);
                subData.add(videoItem);
            }
            label.addSubData(subData);
        }
        if (SearchContainer.videoList_pc.size()>0){
            TypeLabel label = new TypeLabel(mLabels[1]);
            data.add(label);
            List<IMultiItem> subData = new ArrayList<>();
            for (MediaItem fileItem : SearchContainer.videoList_pc){
                VideoItem videoItem = new VideoItem(fileItem,this);
                subData.add(videoItem);
            }
            label.addSubData(subData);
        }

        if (SearchContainer.audioList.size()>0){
            TypeLabel label = new TypeLabel(mLabels[2]);
            data.add(label);
            List<IMultiItem> subData = new ArrayList<>();
            for (FileItem fileItem : SearchContainer.audioList){
                AudioItem audioItem = new AudioItem(fileItem,this);
                subData.add(audioItem);
            }
            label.addSubData(subData);
        }
        if (SearchContainer.audioList_pc.size()>0){
            TypeLabel label = new TypeLabel(mLabels[3]);
            data.add(label);
            List<IMultiItem> subData = new ArrayList<>();
            for (MediaItem fileItem : SearchContainer.audioList_pc){
                AudioItem audioItem = new AudioItem(fileItem,this);
                subData.add(audioItem);
            }
            label.addSubData(subData);
        }

        if (SearchContainer.imageList.size()>0){
            TypeLabel label = new TypeLabel(mLabels[4]);
            data.add(label);
            List<IMultiItem> subData = new ArrayList<>();
            for (FileItem fileItem : SearchContainer.imageList){
                ImageItem audioItem = new ImageItem(fileItem,this);
                subData.add(audioItem);
            }
            label.addSubData(subData);
        }

        if (SearchContainer.imageList_pc.size()>0){
            TypeLabel label = new TypeLabel(mLabels[5]);
            data.add(label);
            List<IMultiItem> subData = new ArrayList<>();
            for (MediaItem fileItem : SearchContainer.imageList_pc){
                ImageItem audioItem = new ImageItem(fileItem,this);
                subData.add(audioItem);
            }
            label.addSubData(subData);
        }

        mAdapter.setData(data);
        if (data.size() > 0){
            mAdapter.expandAll(1);
        }
    }

    private void initViews() {

        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);

        //屏障默认的Change动画
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mAdapter = new MyExpandableAdapter();
        mAdapter.setAlwaysShowHead(true);
        headText = "上次搜索结果";
        mAdapter.addHeadLayout(R.layout.searchitem_head,true,1);


        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull View view, int adapterPosition) {
                if (view.getId() == R.id.label_root){
                    IMultiItem item = mAdapter.getData(adapterPosition);
                    if (item instanceof IExpandable){
                        IExpandable expandable = (IExpandable) item;
                        if (expandable.isExpandable()){
                            // 修复issue#2
                            // 这个支持包的bug: https://issuetracker.google.com/issues/37034096
                            mStaggeredGridLayoutManager.invalidateSpanAssignments();
                            mAdapter.collapseAll(adapterPosition);
                        }else{
                            mAdapter.expandAll(adapterPosition);
                        }
                    }
                }
            }
        });
        mAdapter.setData(data);

        if (!SearchContainer.isEmpty()){
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void initEvent() {

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()>0){
                    if (!clearTextTV.isShown())
                        clearTextTV.setVisibility(View.VISIBLE);
                } else clearTextTV.setVisibility(View.GONE);
            }
        });
        searchET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && (keyEvent!=null && keyEvent.getAction() == KeyEvent.ACTION_DOWN)){
                    if (!TextUtils.isEmpty(searchET.getText().toString())){
                        data.clear();
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(searchET.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        new SearchThread(searchET.getText().toString() , handler).start();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case OrderConst.success:
                    mRecyclerView.setVisibility(View.VISIBLE);
                    headText = "搜索结果";
                    mAdapter.notifyItemChanged(0);
                    initData();
                    break;
                case OrderConst.failure:

                    break;

            }
        }
    };
}
