package com.dkzy.areaparty.phone.bluetoothxie;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Looper;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;
import com.example.action.ControllerDroidAction;
import com.example.action.KeyboardAction;
import com.example.action.MouseClickAction;
import com.example.action.MouseMoveAction;
import com.example.connection.DeviceConnection;

import java.io.IOException;

import static android.view.KeyEvent.KEYCODE_MENU;

/**
 * Created by XIE on 2017/1/3.
 * 中转蓝牙鼠标
 */

public class BluetoothKeyboard extends Activity {
    private RelativeLayout layout_bluetooth;
    private BluetoothAdapter bluetoothAdapter;
    private Activity act ;
    private Context ctx;
    private double move_times=1;
    private MouseAndBoardAsyncTask myTask;
    private MouseAndBoardCallBack callBack;
    GestureDetector gestureDetector;

    public static void actionStart(Context context){
        Intent intent = new Intent(context, BluetoothKeyboard.class);
        context.startActivity(intent);
    }

    protected void onRestart(){
        super.onRestart();
        FloatManager.getFloatManager(BluetoothKeyboard.this).createView();
    }

    protected void onDestroy(){
        super.onDestroy();
        FloatManager.getFloatManager(BluetoothKeyboard.this).destroyFloat();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    /*    requestWindowFeature(Window.FEATURE_NO_TITLE);*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_bluetooth);
        initListeners();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        act = this;
        ctx = this;
        //显示悬浮窗
        FloatManager.getFloatManager(BluetoothKeyboard.this).createView();
        //获取屏幕宽度
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        FloatManager.getFloatManager(BluetoothKeyboard.this).setWidth(width);
    }

    public void initListeners(){
        layout_bluetooth = (RelativeLayout) findViewById(R.id.layout_bluetooth);
        layout_bluetooth.setOnGenericMotionListener(new BlueMousetListener());
        gestureDetector = new GestureDetector(this, new Mouse_GestureListener());
    }

    public boolean onTouchEvent(MotionEvent event) {
        //空白处触摸操作交给手势识别处理

        if (event.getButtonState() == MotionEvent.BUTTON_PRIMARY) {
            //鼠标左键按下就发送STATE_DOWN到服务端
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    MouseClickAction mouseCliekAction = new MouseClickAction(MouseClickAction.BUTTON_LEFT,
                            MouseClickAction.STATE_DOWN);
                    sendAction2Remote(mouseCliekAction);
                    break;
            }
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //鼠标左键抬起就发送STATE_UP，左键和触摸屏幕操作是一样的，但是先判断event.getButtonState()==MotionEvent.BUTTON_PRIMARY就能分辨是
                //按下了鼠标左键还是触摸屏幕，触摸屏幕还是执行触控板操作
                MouseClickAction mouseCliekAction = new MouseClickAction(MouseClickAction.BUTTON_LEFT,
                        MouseClickAction.STATE_UP);
                sendAction2Remote(mouseCliekAction);
                break;
        }

        if (gestureDetector.onTouchEvent(event))                                            //启动手势识别函数
            return true;
        else
            return false;

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {                                 //蓝牙键盘按键监听
        /*int key =event.getKeyCode();
        Log.d("back" , Integer.toString(KeyEvent.KEYCODE_BACK));
        Log.d("keyCode", Integer.toString(keyCode));
        Log.d("key" , Integer.toString(key));*/
        if(keyCode == KEYCODE_MENU){
            return super.onKeyDown(keyCode, event);
        }
        if(bluetoothAdapter ==null|| !bluetoothAdapter.isEnabled()){
            return super.onKeyDown(keyCode, event);
        }
        Integer code = null;
        if ((code = KeyCode.getJavaAwtKeyCode(keyCode)) != -1) {
            KeyboardAction action = new KeyboardAction(code, true);
            sendAction2Remote(action);
            return false;
        } else {
            Toast.makeText(this, "不支持的按键", Toast.LENGTH_SHORT).show();
        }

        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KEYCODE_MENU){
            return super.onKeyUp(keyCode, event);
        }
        if(bluetoothAdapter ==null|| !bluetoothAdapter.isEnabled()){
            return super.onKeyDown(keyCode, event);
        }
        Integer code;
        if ((code = KeyCode.getJavaAwtKeyCode(keyCode)) != -1) {
            KeyboardAction action = new KeyboardAction(code, false);
            sendAction2Remote(action);
            return false;
        }

        return super.onKeyUp(keyCode, event);
    }

    class Mouse_GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {                                      //如果没有蓝牙设备，单击屏幕就执行鼠标左键操作
            if(bluetoothAdapter ==null|| !bluetoothAdapter.isEnabled()) {
                MouseClickAction mouseCliekAction = new MouseClickAction(MouseClickAction.BUTTON_LEFT,
                        MouseClickAction.STATE_DOWN);
                sendAction2Remote(mouseCliekAction);
                mouseCliekAction = new MouseClickAction(MouseClickAction.BUTTON_LEFT,
                        MouseClickAction.STATE_UP);
                sendAction2Remote(mouseCliekAction);
            }
            return true;
        }



        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){

            float dis_x = -distanceX;
            float dis_y = -distanceY;
            dis_x *= move_times;
            dis_y *= move_times;

            if(dis_x != 0 || dis_y != 0) {
                MouseMoveAction mouseMoveAction = new MouseMoveAction(
                        (short) dis_x, (short) dis_y);
                sendAction2Remote(mouseMoveAction);
            }
            return true;
        }
    }

    private void sendAction2Remote(final ControllerDroidAction action) {
        if (myTask != null) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DeviceConnection.getInstance().sendAction(action);
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(ctx, "连接已断开,正在重连...", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    doInBackground();
                }
            }
        }).start();
    }

    private void doInBackground() {
        if (myTask != null) {
            return;
        }
        callBack = new MouseAndBoardCallBack() {
            @Override
            public void callback() {
                if (DeviceConnection.getInstance().isAuthentificated()) {
                    Toast.makeText(ctx, "连接成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ctx, "连接失败", Toast.LENGTH_SHORT).show();
                }

                myTask = null;
            }
        };


        myTask = new MouseAndBoardAsyncTask(callBack);

        myTask.execute();
    }
    /*public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bluemouse_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected( MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_bluetooth_exit:
                this.finish();
                break;
            case R.id.action_settings_bluetoothActivity:
                Intent i = new Intent("android.bluetooth.devicepicker.action.LAUNCH");
                startActivity(i);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
