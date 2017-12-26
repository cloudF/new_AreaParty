package com.dkzy.areaparty.phone.fragment02.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment02.Model.MediaItem;
import com.dkzy.areaparty.phone.fragment02.Model.MediaSetBean;
import com.dkzy.areaparty.phone.fragment02.base.BottomDialogAdapter;
import com.dkzy.areaparty.phone.fragment02.utils.MediafileHelper;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_addFolder;
import com.dkzy.areaparty.phone.fragment02.view.AlwaysMarqueeTextView;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import me.shaohui.bottomdialog.BaseBottomDialog;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by borispaul on 17-7-6.
 * 在图片库和音频库中调用
 */

public class listBottomDialog extends BaseBottomDialog implements View.OnClickListener{
    private TextView typeNameTV;
    private AlwaysMarqueeTextView nameTV;
    private RecyclerView fileSGV;
    private LinearLayout addNewSetLL;
    List<MediaSetBean> setList;
    //MediaItem currentFile;
    List<MediaItem> currentFileList;
    BottomDialogAdapter adapter;
    private Context context;

    public void setFile(MediaItem currentFile) {
        if (currentFileList == null){
            currentFileList = new ArrayList<>();
            currentFileList.add(currentFile);
        }else {
            currentFileList.clear();
            currentFileList.add(currentFile);
        }
    }
    public void setFileList(List<MediaItem> currentFileList) {
        this.currentFileList = currentFileList;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addNewSetLL:
                showAddSetDialog();
                break;
        }
    }

    @Override
    public int getLayoutRes() {
        return R.layout.tab02_listdialog;
    }

    @Override
    public void bindView(View v) {
        typeNameTV = (TextView) v.findViewById(R.id.typeNameTV);
        nameTV = (AlwaysMarqueeTextView) v.findViewById(R.id.nameTV);
        fileSGV = (RecyclerView) v.findViewById(R.id.fileSGV);
        addNewSetLL = (LinearLayout) v.findViewById(R.id.addNewSetLL);
        addNewSetLL.setClickable(true);
        addNewSetLL.setFocusable(true);
        addNewSetLL.setOnClickListener(this);

        if(currentFileList.get(0).getType().equals(OrderConst.audioAction_name)) {
            typeNameTV.setText("音频: ");
        } else typeNameTV.setText("图片: ");

        StringBuilder sBuilder = new StringBuilder();
        for (MediaItem item : currentFileList){
            sBuilder.append(item.getName());
            sBuilder.append(", ");
        }
        nameTV.setText(sBuilder.toString());
        fileSGV.setItemAnimator(new DefaultItemAnimator());
        fileSGV.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        fileSGV.setAdapter(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initData() {
        setList = new ArrayList<>();
        if(MediafileHelper.getMediaType().equals(OrderConst.audioAction_name)) {
            for(Map.Entry<String, List<MediaItem>> temp : MediafileHelper.audioSets.entrySet()) {
                MediaSetBean file = new MediaSetBean();
                file.name = temp.getKey();
                file.thumbnailID = R.drawable.logo_audioset;
                file.numInfor = temp.getValue().size() + "首";
                setList.add(file);
            }
        } else {
            for(Map.Entry<String, List<MediaItem>> temp : MediafileHelper.imageSets.entrySet()) {
                MediaSetBean file = new MediaSetBean();
                file.name = temp.getKey();
                file.thumbnailID = R.drawable.logo_imageset;
                file.numInfor = temp.getValue().size() + "张";
                setList.add(file);
            }
        }

        adapter = new BottomDialogAdapter(context, setList);
        adapter.isFirstOnly(false);
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
//                List<MediaItem> files = new ArrayList<>();
//                files.add(currentFile);
                if(MyApplication.isSelectedPCOnline()) {
                    for (MediaItem item: currentFileList){
                        List<MediaItem> files = new ArrayList<>();
                        files.add(item);
                        MediafileHelper.addFilesToSet(setList.get(i).name, files, myHandler);
                    }
                    MediafileHelper.addFileToLocalSet(setList.get(i).name, currentFileList);
                    listBottomDialog.this.dismiss();
                    Toasty.success(context, "添加成功", Toast.LENGTH_SHORT, true).show();
                } else {
                    listBottomDialog.this.dismiss();
                    Toasty.error(context, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
    }

    private void showAddSetDialog() {
        final ActionDialog_addFolder dialog = new ActionDialog_addFolder(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setEditHintText("请输入新增列表名称");
        dialog.setTitleText("新建列表");
        final EditText editText = dialog.getEditTextView();
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempName = editText.getText().toString();
                if(tempName.equals("") || tempName.endsWith(".") ||
                        tempName.contains("\\") || tempName.contains("/") ||
                        tempName.contains(":")  || tempName.contains("*") ||
                        tempName.contains("?")  || tempName.contains("\"") ||
                        tempName.contains("<")  || tempName.contains(">") ||
                        tempName.contains("|")){
                    Toasty.error(context, "设备名不能为空，不能包含\\ / : * ? \" < > |字符", Toast.LENGTH_SHORT).show();
                } else {
                    if(isSetContained(tempName)) {
                        Toasty.error(context, "当前列表名称已存在", Toast.LENGTH_SHORT).show();
                    } else {
                        InputMethodManager manager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        if(MediafileHelper.getMediaType().equals(OrderConst.audioAction_name)) {
                            MediafileHelper.addAudioPlaySet(tempName, myHandler);
                            MediaSetBean file = new MediaSetBean();
                            file.name = tempName;
                            file.thumbnailID = R.drawable.logo_audioset;
                            file.numInfor = "0首";
                            setList.add(0, file);
                            adapter.notifyDataSetChanged();
                            MediafileHelper.audioSets.put(tempName, new ArrayList<MediaItem>());
                        } else {
                            MediafileHelper.addImagePlaySet(tempName, myHandler);
                            MediaSetBean file = new MediaSetBean();
                            file.name = tempName;
                            file.thumbnailID = R.drawable.logo_imageset;
                            file.numInfor = "0张";
                            setList.add(0, file);
                            adapter.notifyDataSetChanged();
                            MediafileHelper.imageSets.put(tempName, new ArrayList<MediaItem>());
                        }
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private boolean isSetContained(String name) {
        for(int i = 0; i < setList.size(); ++i)
            if(setList.get(i).name.equals(name))
                return true;
        return false;
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            }
        }
    };
}
