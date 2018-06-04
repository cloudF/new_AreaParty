package com.dkzy.areaparty.phone.fragment01.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment06.headIndexToImgId;
import com.facebook.common.internal.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import protocol.Data.GroupData;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/3/6 13:00
 */

public class SharedFileDialog extends Dialog {
    private TextView title;
    private TextView shareFileName;
    private EditText editText;
    private EditText shareFileUrlET;
    private EditText shareFilePwdET;
    private Button positiveButton;
    private Button negativeButton;
    private MultiSelectionSpinner sp_group;
    private static List<GroupData.GroupItem> userGroup_list = null;
    private List<String> list;
    private Context context;
    private List<Integer> listSelected;

    public List<String> getSelected() {
        List<String> listGroupId = new ArrayList<>();
        listSelected = sp_group.getSelectedIndices();
        for (int i = 0;i<listSelected.size();i++){
            listGroupId.add(userGroup_list.get(listSelected.get(i)).getGroupId());
        }
        return listGroupId;
    }

    public SharedFileDialog(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.tab04_sharefile_dialog, null);
        title = (TextView) mView.findViewById(R.id.shareFileTitleTV);
        sp_group = (MultiSelectionSpinner) mView.findViewById(R.id.sp_group);
        shareFileName = (TextView) mView.findViewById(R.id.shareFileNameTV);
        editText = (EditText) mView.findViewById(R.id.shareFileDesET);
        shareFileUrlET = (EditText) mView.findViewById(R.id.shareFileUrlET);
        shareFilePwdET = (EditText) mView.findViewById(R.id.shareFilePwdET);
        positiveButton = (Button) mView.findViewById(R.id.shareFilePositiveButton);
        negativeButton = (Button) mView.findViewById(R.id.shareFileNegativeButton);
        super.setContentView(mView);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth() - 100; //设置dialog的宽度为当前手机屏幕的宽度-100
        getWindow().setAttributes(p);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        initData();
    }

    private void initData() {
        userGroup_list = Login.userGroups;
        list=new ArrayList<String>();
        if(userGroup_list.size()>0){
            for(GroupData.GroupItem group : userGroup_list){
//                FileGroup fg = new FileGroup();
//                fg.groupId=group.getGroupId();
//                fg.groupName=group.getGroupName();
//                fg.isChecked=false;
                list.add(group.getGroupName());
            }
        }
        sp_group.setItems(list);
    }


    public void setOnPositiveListener(View.OnClickListener listener) {
        positiveButton.setOnClickListener(listener);
    }
    public void setOnNegativeListener(View.OnClickListener listener) {
        negativeButton.setOnClickListener(listener);
    }

    public void setFileName(String text) {
        this.shareFileName.setText(text);
    }
    public void hindFileName(){this.shareFileName.setVisibility(View.GONE);}
    public void setTitleText(String text) {
        this.title.setText(text);
    }

    public String getEditText() {
        return editText.getText().toString();
    }
    public EditText getEditTextView() {
        return editText;
    }
    public String getUrlEditText() {
        return shareFileUrlET.getText().toString();
    }
    public EditText getUrlEditTextView() {
        return shareFileUrlET;
    }
    public String getPwdEditText() {
        return shareFilePwdET.getText().toString();
    }
    public EditText getPwdEditTextView() {
        return shareFilePwdET;
    }
    public void setPositiveButtonText(String text) {
        this.positiveButton.setText(text);
    }
    public void setNegativeButtonText(String text) {
        this.negativeButton.setText(text);
    }

    public void setEdit1HintText(String hint) {
        editText.setHint(hint);
    }
    public void setEdit2HintText(String hint) {
        shareFileUrlET.setHint(hint);
    }
    public void setEdit3HintText(String hint) {
        shareFilePwdET.setHint(hint);
    }
//    class FileGroup{
//        String groupId;
//        String groupName;
//        boolean isChecked;
//    }
}
