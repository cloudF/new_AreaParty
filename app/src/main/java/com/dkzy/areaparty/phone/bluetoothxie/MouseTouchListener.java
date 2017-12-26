package com.dkzy.areaparty.phone.bluetoothxie;

import android.app.Activity;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.example.action.ControllerDroidAction;
import com.example.action.MouseClickAction;
import com.example.connection.DeviceConnection;

import java.io.IOException;


public class MouseTouchListener implements OnTouchListener,
        MouseAndBoardCallBack
{
	private MouseAndBoardAsyncTask myTask;
	private byte button;
	private Vibrator vibrator;
	private Activity context;

	public MouseTouchListener(byte button, Vibrator vibrator, Activity context)
	{
		this.context = context;
		this.button = button;
		this.vibrator = vibrator;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event)
	{
		switch (event.getAction())
		{
		case MotionEvent.ACTION_MOVE:
		{
			// this.onTouchMove(event);
			break;
		}
		case MotionEvent.ACTION_DOWN:
		{
			this.onTouchDown(event);
			break;
		}
		case MotionEvent.ACTION_UP:
		{
			this.onTouchUp(event);
			break;
		}
		default:
			break;
		}
		return true;
	}

	private void onTouchDown(MotionEvent event)
	{
		vibrator.vibrate(new long[]
		{ 0, 100 }, -1);
		MouseClickAction mouseCliekAction = new MouseClickAction(this.button,
				MouseClickAction.STATE_DOWN);
		Toast.makeText(context, "MouseClickDown", Toast.LENGTH_LONG);
		Log.d("MouseClick","MouseClickDown");
		sendAction2Remote(mouseCliekAction);
	}

	/*
	 * private void onTouchMove(MotionEvent event) { if (!this.hold &&
	 * event.getEventTime() - event.getDownTime() >= this.holdDelay) { this.hold
	 * = true;
	 * 
	 * this.application.vibrate(100); } }
	 */

	private void onTouchUp(MotionEvent event)
	{
		MouseClickAction mouseCliekAction = new MouseClickAction(this.button,
				MouseClickAction.STATE_UP);
		Toast.makeText(context, "MouseClickUp", Toast.LENGTH_LONG);
		Log.d("MouseClick","MouseClickUp");
		sendAction2Remote(mouseCliekAction);
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
