package com.dkzy.areaparty.phone.fragment01.setting;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.lljjcoder.citypickerview.widget.CityPicker;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import protocol.Msg.PersonalSettingsMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

/**
 * Created by SnowMonkey on 2017/7/16.
 */

public class SettingAddressActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView setting_address;
    private EditText setting_street;
    private EditText setting_community;
    private ImageButton settingAddressBackBtn;
    private Button sendChangeAddressMsgBtn;

    public static Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab01_setting_address);
        MyApplication.getInstance().addActivity(this);

        getData();
        initView();
        initEvent();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.settingAddressBackBtn:
                this.finish();
                break;
            case R.id.sendChangeAddressMsgBtn:
                if(setting_address.getText().toString().isEmpty()){
                    Toast.makeText(SettingAddressActivity.this, "请选择地区", Toast.LENGTH_LONG).show();
                    return;
                }
                if(setting_street.getText().toString().isEmpty() || !check(setting_street.getText().toString())){
                    Toast.makeText(SettingAddressActivity.this, "请正确填写居住街道", Toast.LENGTH_LONG).show();
                    return;
                }
                if(setting_community.getText().toString().isEmpty() || !check(setting_community.getText().toString())){
                    Toast.makeText(SettingAddressActivity.this, "请正确填写居住小区", Toast.LENGTH_LONG).show();
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PersonalSettingsMsg.PersonalSettingsReq.Builder builder = PersonalSettingsMsg.PersonalSettingsReq.newBuilder();
                        builder.setUserAddress(setting_address.getText().toString());
                        builder.setUserStreet(setting_street.getText().toString());
                        builder.setUserCommunity(setting_community.getText().toString());
                        byte[] reByteArray;
                        try {
                            reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.PERSONALSETTINGS_REQ.getNumber(), builder.build().toByteArray());
                            Login.base.writeToServer(Login.outputStream, reByteArray);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.setting_address:
                chooseArea(v);
                break;
        }
    }
    private void getData(){

    }
    private void initView(){
        setting_address = (TextView) this.findViewById(R.id.setting_address);
        setting_street = (EditText) this.findViewById(R.id.setting_street);
        setting_community = (EditText) this.findViewById(R.id.setting_community);
        settingAddressBackBtn = (ImageButton) this.findViewById(R.id.settingAddressBackBtn);
        sendChangeAddressMsgBtn = (Button) this.findViewById(R.id.sendChangeAddressMsgBtn);
    }
    private void initEvent(){
        settingAddressBackBtn.setOnClickListener(this);
        sendChangeAddressMsgBtn.setOnClickListener(this);
        setting_address.setOnClickListener(this);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        Toast.makeText(SettingAddressActivity.this, "修改失败，请重试", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(SettingAddressActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                        SettingAddressActivity.this.finish();
                        break;
                }
            }
        };
    }

    public void chooseArea(View view) {
        //判断输入法的隐藏状态
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            selectAddress();//调用CityPicker选取区域
        }
    }
    private void selectAddress() {
        String province = "";
        String city = "";
        String district = "";
        if(setting_address.getText().toString().equals("")){
            province = "四川省";
            city = "成都市";
            district = "郫县";
        }else{
            province = setting_address.getText().toString().split("-")[0];
            city = setting_address.getText().toString().split("-")[1];
            district = setting_address.getText().toString().split("-")[2];
        }
        CityPicker cityPicker = new CityPicker.Builder(SettingAddressActivity.this)
                .textSize(14)
                .title("地址选择")
                .titleBackgroundColor("#FFFFFF")
                .titleTextColor("#696969")
                .confirTextColor("#696969")
                .cancelTextColor("#696969")
                .province(province)
                .city(city)
                .district(district)
                .textColor(Color.parseColor("#000000"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(10)
                .onlyShowProvinceAndCity(false)
                .build();
        cityPicker.show();
        //监听方法，获取选择结果
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                //省份
                String province = citySelected[0];
                //城市
                String city = citySelected[1];
                //区县（如果设定了两级联动，那么该项返回空）
                String district = citySelected[2];
                //邮编
                String code = citySelected[3];
                //为TextView赋值
                setting_address.setText(province.trim() + "-" + city.trim() + "-" + district.trim());
            }

            @Override
            public void onCancel() {
                Toast.makeText(SettingAddressActivity.this, "已取消", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean check(String str){
        Pattern p = Pattern.compile("^[A-Za-z0-9\u4e00-\u9fa5]+$");
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
