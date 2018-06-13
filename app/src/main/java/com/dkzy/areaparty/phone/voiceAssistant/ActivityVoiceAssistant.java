package com.dkzy.areaparty.phone.voiceAssistant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ActivityVoiceAssistant extends AppCompatActivity implements EventListener {
    protected TextView txtLog;
    protected TextView txtResult;
//    protected Button btn;
//    protected Button stopBtn;
    private static String DESC_TEXT = "请对当前界面说控制命令，目前只支持：\n" +
            "增大音量，减小音量，暂停，播放，停止（退出播放器）\n" ;
//            +"需要完整版请参见之前的唤醒示例。\n\n" +
//            "唤醒词是纯离线功能，需要获取正式授权文件（与离线命令词的正式授权文件是同一个）。 第一次联网使用唤醒词功能自动获取正式授权文件。之后可以断网测试\n" +
//            "请说“小度你好”或者 “百度一下”\n\n";

    private EventManager wakeup;

    private boolean logTime = true;

    /**
     * 测试参数填在这里
     */
    private void start() {
        Map<String, Object> params = new TreeMap<String, Object>();

        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        // "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下

        String json = null; // 这里可以替换成你需要测试的json
        json = new JSONObject(params).toString();
        wakeup.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
        printLog("语音助手启动成功：");
//        ---------------------------------
//        System.out.println(params);
    }

    private void stop() {
        wakeup.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0); //
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_mini);
        initView();
        initPermission();
        wakeup = EventManagerFactory.create(this, "wp");
        wakeup.registerListener(this); //  EventListener 中 onEvent方法

        start();
//        btn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                start();
//            }
//        });
//        stopBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                stop();
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
        wakeup.send(SpeechConstant.WAKEUP_STOP, "{}", null, 0, 0);
    }


    @Override
    protected void onStop() {
        super.onStop();
        stop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        start();
    }

    //   EventListener  回调方法
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
    /*    String logTxt = "name: " + name;
        if (params != null && !params.isEmpty()) {
            logTxt += " ;params :" + params;
        } else if (data != null) {
            logTxt += " ;data length=" + data.length;
        }
        printLog(logTxt);
        System.out.println(params);*/
//    以上为DEMO自带的调用方式，现在改换自己写
//        Log.d(TAG, String.format("event: name=%s, params=%s", name, params));

        //唤醒成功
        if(name.equals("wp.data")){
            try {
                Gson gson = new Gson();
                WakeUpInstruction wakeUpInstruction = gson.fromJson(params,WakeUpInstruction.class);
                int errorCode = wakeUpInstruction.getErrorCode();
                if(errorCode == 0){
                    //唤醒成功
                    if(wakeUpInstruction.getWord().equals("增大音量")){
                        TVAppHelper.vedioPlayControlVolumeUp();
//                        printLog(wakeUpInstruction.getWord());
                    }
                    else if(wakeUpInstruction.getWord().equals("减小音量")){
                        TVAppHelper.vedioPlayControlVolumeDown();
//                        printLog(wakeUpInstruction.getWord());
                    } else if(wakeUpInstruction.getWord().equals("暂停")){
                        TVAppHelper.vedioPlayControlPause();
                    } else if(wakeUpInstruction.getWord().equals("播放")){
                        TVAppHelper.vedioPlayControlPlay();
                    } else if(wakeUpInstruction.getWord().equals("停止")){
                        TVAppHelper.vedioPlayControlExit();
                    }else if(wakeUpInstruction.getWord().equals("上一首")){
                        TVAppHelper.vedioPlayControlRewind();
                    }else if(wakeUpInstruction.getWord().equals("下一首")){
                        TVAppHelper.vedioPlayControlFast();
                    }

                    printLog(wakeUpInstruction.getWord());
                } else {
                    //唤醒失败
                    printLog(wakeUpInstruction.getErrorDesc());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if("wp.exit".equals(name)){
            //唤醒已停止
        }


    }

    private void printLog(String text) {
        text += "\n";
        Log.i(getClass().getName(), text);
        txtLog.append(text + "\n");
    }


    private void initView() {
        txtResult = (TextView) findViewById(R.id.txtResult);
        txtLog = (TextView) findViewById(R.id.txtLog);
//        btn = (Button) findViewById(R.id.btn);
//        stopBtn = (Button) findViewById(R.id.btn_stop);
        txtResult.setText(DESC_TEXT + "\n");
        txtLog.setText("\n");
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }

    }
}
