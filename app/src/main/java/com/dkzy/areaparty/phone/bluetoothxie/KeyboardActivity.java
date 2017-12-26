package com.dkzy.areaparty.phone.bluetoothxie;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;
import com.example.action.ControllerDroidAction;
import com.example.action.KeyboardAction;
import com.example.action.MouseClickAction;
import com.example.action.MouseMoveAction;
import com.example.connection.DeviceConnection;

import java.io.IOException;

import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.KeyEvent.KEYCODE_MENU;

/**
 * Created by XIE on 2017/3/13.
 *
 */
public class KeyboardActivity extends Activity {

	
	private Activity act = this;
	private Context ctx = this;
	private EditText edit;
	private KeyboardUtil keyboard;
	private MouseAndBoardAsyncTask myTask;
	private MouseAndBoardCallBack callBack;
	private BluetoothAdapter bluetoothAdapter;
	private float screenDensity;
	private Vibrator vibrator;
	private ImageView clickMove;
	private RelativeLayout layout_mouse;
	private SharedPreferences preferences;
	public static int imageH = 250;
	public static int imageW = 250;
	private double move_times=1;
	public static int onTouchLock = 0 ;
	GestureDetector gestureDetector;

	public static void actionStart(Context context){
		Intent intent = new Intent(context, KeyboardActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
/*		requestWindowFeature(Window.FEATURE_NO_TITLE);*/
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_keyboard);
		if (DeviceConnection.getInstance().isAuthentificated())                            //获得连接状态，未连接跳转到重连函数doInBackground
		{
			Toast.makeText(KeyboardActivity.this, "服务器已连接", Toast.LENGTH_SHORT)
					.show();


		} else
		{
			Toast.makeText(KeyboardActivity.this, "正在连接...", Toast.LENGTH_SHORT)
					.show();
			doInBackground();
		}
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenDensity = dm.density;                                           //density就是一个dp对应几个坐标值，imageH设置成250dp的正方形，对应坐标就是750*750设备density=3
//		Log.d("density", String.valueOf(screenDensity));
		initListeners();
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		act = this;
		ctx = this;
		FloatManagerMain.getFloatManager(KeyboardActivity.this).createView();
		edit = (EditText) findViewById(R.id.edit_query);
		keyboard = new KeyboardUtil(act, ctx,edit);
		keyboard.showKeyboard();
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
	private void initListeners() {
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		Button clickLeft = (Button) findViewById(R.id.btn_left);
		clickLeft.setOnTouchListener(new MouseTouchListener(
				MouseClickAction.BUTTON_LEFT, vibrator, this));
		Button clickRight = (Button) findViewById(R.id.btn_right);
		clickRight.setOnTouchListener(new MouseTouchListener(
				MouseClickAction.BUTTON_RIGHT, vibrator, this));
		/*clickMove = (ImageView) findViewById(R.id.mouse_view);
		clickMove.setOnTouchListener(new ToucherTouchListener(
				screenDensity, preferences, vibrator, this));*/
		layout_mouse = (RelativeLayout) findViewById(R.id.layout_mouse);
		/*layout_mouse.setOnGenericMotionListener(new BlueMousetListener());
		clickLeft.setOnGenericMotionListener(new BlueMousetListener());
		clickRight.setOnGenericMotionListener(new BlueMousetListener());*/
//		clickMove.setScrollContainer(true);
		gestureDetector = new GestureDetector(this, new Mouse_GestureListener());//手势识别监听
	}
	public boolean onTouchEvent(MotionEvent event) {     //空白处触摸操作交给手势识别函数Mouse_GestureListener处理

		if (gestureDetector.onTouchEvent(event))
			return true;
		else
			return false;

	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
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
		if (keyCode == KEYCODE_BACK){
			this.finish();
			return true;
		}
		Integer code;
		if ((code = KeyCode.getJavaAwtKeyCode(keyCode)) != -1) {
			KeyboardAction action = new KeyboardAction(code, false);
			sendAction2Remote(action);
			return false;
		}

		return super.onKeyUp(keyCode, event);
	}
	private void sendAction2Remote(final ControllerDroidAction action) {                     //发送action到服务端
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

	class Mouse_GestureListener extends GestureDetector.SimpleOnGestureListener {
		//手势识别函数，onScroll函数是监听滚动事件的，在手机上操作时，类似于滚轮操作，本意是向下滑动则产生一个向上的distance，这里用作
		//模拟触控板操作，用onTouch方法也能实现，这里用onScroll方便一些，ToucherTouhListener类里面用onTouch方法实现触控板

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			MouseClickAction mouseCliekAction = new MouseClickAction(MouseClickAction.BUTTON_LEFT,
					MouseClickAction.STATE_DOWN);
			sendAction2Remote(mouseCliekAction);
			mouseCliekAction = new MouseClickAction(MouseClickAction.BUTTON_LEFT,
					MouseClickAction.STATE_UP);
			sendAction2Remote(mouseCliekAction);
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			MouseClickAction mouseCliekAction = new MouseClickAction(MouseClickAction.BUTTON_RIGHT,
					MouseClickAction.STATE_DOWN);
			sendAction2Remote(mouseCliekAction);
			mouseCliekAction = new MouseClickAction(MouseClickAction.BUTTON_RIGHT,
					MouseClickAction.STATE_UP);
			sendAction2Remote(mouseCliekAction);
		}

		@Override
		/*public void onLongPress(MotionEvent e) {
			Toast.makeText(KeyboardActivity.this, "onLongPress", Toast.LENGTH_SHORT);
			long moveXFinal = Math.round((e.getX() - KeyboardActivity.imageH * screenDensity / 2) * 0.05);
			Log.d("moveXFinal", Long.toString(moveXFinal));
			long moveYFinal = Math.round((e.getY() - KeyboardActivity.imageW * screenDensity / 2) * 0.05);
			if (moveXFinal != 0 || moveYFinal != 0) {
				MouseMoveAction mouseMoveAction = new MouseMoveAction(
						(short) moveXFinal, (short) moveYFinal);
				sendAction2Remote(mouseMoveAction);
			}
		}*/
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){       //空白处发生滚动事件触发函数

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

	protected void onStart(){
		super.onStart();
		FloatManagerMain.getFloatManager(KeyboardActivity.this).createView();
	}

	protected void onRestart(){
		super.onRestart();
		FloatManagerMain.getFloatManager(KeyboardActivity.this).createView();
	}

	protected void onDestroy(){
		super.onDestroy();
		FloatManagerMain.getFloatManager(KeyboardActivity.this).destroyFloat();
	}
}
