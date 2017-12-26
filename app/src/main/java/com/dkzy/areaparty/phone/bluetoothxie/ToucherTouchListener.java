package com.dkzy.areaparty.phone.bluetoothxie;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.example.action.ControllerDroidAction;
import com.example.action.MouseMoveAction;
import com.example.connection.DeviceConnection;

import java.io.IOException;


public class ToucherTouchListener implements OnTouchListener, MouseAndBoardCallBack
{
	private MouseAndBoardAsyncTask myTask;
	private Vibrator vibrator;
	private SharedPreferences preferences;
	private float screenDensity;
	private float immobileDistance;

	private Double moveSensitivity;
	private float moveAcceleration;
	private float moveDownX;
	private float moveDownY;
	private float movePreviousX;
	private float movePreviousY;
	private float moveResultX;
	private float moveResultY;
	private float currentX;
	private float currentY;
	float x;
	float y;
	float mLastmotionX;
	float mLastmotionY;
	int move_times = 4;
	int h;
	double dis_x;
	double dis_y;
	boolean firstTime = true;
	boolean downFirst = true;

	private float wheelPrevious;
	private float wheelResult;
	private float wheelSensitivity;
	private float wheelAcceleration;
	private boolean clickAtMove = false;

	private Context context;

	public ToucherTouchListener(float screenDensity,
                                SharedPreferences preferences, Vibrator vibrator, Context context)
	{
		this.context = context;
		this.preferences = preferences;
		this.screenDensity = screenDensity;
		this.vibrator = vibrator;
		this.moveSensitivity = 10.00;
		this.moveAcceleration = 10;
//		this.reloadPreferences();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event)
	{
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
		{
			this.onTouchDown(event);
			break;
		}
			case MotionEvent.ACTION_MOVE:
		{
			this.onTouchMove(event);
			break;
		}

			case MotionEvent.ACTION_UP:
		{
			this.onTouchUp(event);
			// Commented out since we're going to be in a loop
			// this.screenCaptureRequest();
			break;
		}
		default:
			break;
		}
		return true;

	}
	;

	private  void onTouchDown(MotionEvent event) {
//		Toast.makeText(context, "onTouchDown()", Toast.LENGTH_SHORT ).show();
		mLastmotionX = event.getX();
		mLastmotionY = event.getY();
		downFirst = true;
		/*long moveXFinal = Math.round((event.getX() - KeyboardActivity.imageH * screenDensity / 2) * 0.05);
		Log.d("moveXFinal", Long.toString(moveXFinal));
		long moveYFinal = Math.round((event.getY() - KeyboardActivity.imageW * screenDensity / 2) * 0.05);
		if (moveXFinal != 0 || moveYFinal != 0) {
			MouseMoveAction mouseMoveAction = new MouseMoveAction(
					(short) moveXFinal, (short) moveYFinal);
			sendAction2Remote(mouseMoveAction);
		}*/
	}

	private void onTouchMove(MotionEvent event){
//		Toast.makeText(context, "onTouchMove()", Toast.LENGTH_SHORT ).show();
		h = event.getHistorySize();
		if(h  > 0){
			mLastmotionX = event.getHistoricalX( event.getPointerCount()-1 , h -1);
			mLastmotionY = event.getHistoricalY( event.getPointerCount()-1 , h -1);
		}
		dis_x = event.getX() - mLastmotionX ;
		dis_y = event.getY() - mLastmotionY ;
		/*if (downFirst) {																										//如果是第一次触摸屏幕，先执行一次move操作，再进行判断是不是手指停顿
			dis_x *= move_times;																								//如果是手指停顿就判断是否是第一次停顿，是第一次停顿，线程睡眠0.5秒后判断是否还停顿
			dis_y *= move_times;																								//还停顿，就往停顿的方向移动指针，发生move事件就执行move操作
			MouseMoveAction mouseMoveAction = new MouseMoveAction(																//整个函数的作用就是滑动屏幕就执行类似触控板的操作，手指停顿就执行类似thinkpad小红点的操作
					(short) dis_x, (short) dis_y);
			sendAction2Remote(mouseMoveAction);
			downFirst = false;
		}
		else if (Math.abs(dis_x) < 0.1 && Math.abs(dis_y) < 0.1) {
				if (firstTime) {																//第一次停顿才睡眠然后再判断是否是停顿状态，之后连续执行小红点操作（即向着手指方向移动鼠标）
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if (Math.abs(dis_x) <0.1 && Math.abs(dis_y) < 0.1) firstTime = false;
						}
					}).start();
				}else {																										//小红点操作
						long moveXFinal = Math.round((event.getX() - KeyboardActivity.imageH * screenDensity / 2) * 0.05);//density就是一个dp对应几个坐标值，imageH设置成250dp的正方形，对应坐标就是750*750设备density=3
						Log.d("moveXFinal", Long.toString(moveXFinal));
						long moveYFinal = Math.round((event.getY() - KeyboardActivity.imageW * screenDensity / 2) * 0.05);
						if (moveXFinal != 0 || moveYFinal != 0) {
							MouseMoveAction mouseMoveAction = new MouseMoveAction(
									(short) moveXFinal, (short) moveYFinal);
							sendAction2Remote(mouseMoveAction);
						}

				}
			} else {								//触控板操作*/
//			firstTime = true;
			dis_x *= move_times;
			dis_y *= move_times;
			/*Log.d("dis_x", String.valueOf(dis_x));
			Log.d("dis_y", String.valueOf(dis_y));*/
			MouseMoveAction mouseMoveAction = new MouseMoveAction(                             //mouseMoveAction是移动鼠标action，参数是移动的距离
						(short) dis_x, (short) dis_y);
			sendAction2Remote(mouseMoveAction);

	}
	private void onTouchUp(MotionEvent event){
		firstTime = true;
		downFirst = true;
	}
	/*private void onTouchDown(MotionEvent event)
	{
		this.moveDownX = this.movePreviousX = event.getRawX();
		this.moveDownY = this.movePreviousY = event.getRawY();

		this.moveResultX = 0;
		this.moveResultY = 0;

		this.clickAtMove = true;
	}

	private void onTouchMove(MotionEvent event)
	{
		this.clickAtMove = false;

		float moveRawX = event.getRawX() - this.movePreviousX;
		float moveRawY = event.getRawY() - this.movePreviousY;

		moveRawX *= this.moveSensitivity;
		moveRawY *= this.moveSensitivity;

		moveRawX = (float) ((Math
				.pow(Math.abs(moveRawX), this.moveAcceleration) * Math
				.signum(moveRawX)));
		moveRawY = (float) ((Math
				.pow(Math.abs(moveRawY), this.moveAcceleration) * Math
				.signum(moveRawY)));

		moveRawX += this.moveResultX;
		moveRawY += this.moveResultY;

		int moveXFinal = Math.round(moveRawX);
		int moveYFinal = Math.round(moveRawY);

		if (moveXFinal != 0 || moveYFinal != 0)
		{
			MouseMoveAction mouseMoveAction = new MouseMoveAction(
					(short) moveXFinal, (short) moveYFinal);
			sendAction2Remote(mouseMoveAction);
			this.moveResultX = moveRawX - moveXFinal;
			this.moveResultY = moveRawY - moveYFinal;
			this.movePreviousX = event.getRawX();
			this.movePreviousY = event.getRawY();
		}
	}

	private void onTouchUp(MotionEvent event)
	{
		if (this.clickAtMove == true)
		{
			vibrator.vibrate(new long[]
			{ 0, 100 }, -1);
			MouseClickAction mouseDown = new MouseClickAction(
					MouseClickAction.BUTTON_LEFT, MouseClickAction.STATE_DOWN);
			sendAction2Remote(mouseDown);
			MouseClickAction mouseUp = new MouseClickAction(
					MouseClickAction.BUTTON_LEFT, MouseClickAction.STATE_UP);
			sendAction2Remote(mouseUp);
		}
	}*/

	/*private double getDistanceFromDown(MotionEvent event)
	{
		return Math.sqrt(Math.pow(event.getRawX() - this.moveDownX, 2)
				+ Math.pow(event.getRawY() - this.moveDownY, 2));
	}*/

	private void reloadPreferences()
	{
		/*this.immobileDistance = Float.parseFloat(preferences.getString(
				"control_immobile_distance", null));
		this.immobileDistance *= screenDensity;*/

//		this.moveSensitivity = 0.01*Float.parseFloat(preferences.getString(
//				"control_sensitivity", null));
		/*this.moveSensitivity /= screenDensity;
		this.moveAcceleration = Float.parseFloat(preferences.getString(
				"control_acceleration", null));
		this.wheelSensitivity = this.moveSensitivity / 10f;
		this.wheelAcceleration = this.moveAcceleration;*/

	}

	private void sendAction2Remote(final ControllerDroidAction action)
	{
		if (myTask != null)
		{
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DeviceConnection.getInstance().sendAction(action);
				} catch (IOException e) {
					Looper.prepare();
					Toast.makeText(context, "连接已断开,正在重连...", Toast.LENGTH_SHORT).show();
					Looper.loop();
					doInBackground();
				}
			}
		}).start();
	}

	@Override
	public void callback()
	{
		myTask = null;
		if (DeviceConnection.getInstance().isAuthentificated())
		{
			Toast.makeText(context, "连接成功", Toast.LENGTH_SHORT).show();
		} else
		{
			Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
		}
	}

	private void doInBackground()
	{
		if (myTask != null)
		{
			return;
		}

		myTask = new MouseAndBoardAsyncTask(this);
		myTask.execute();
	}
}
