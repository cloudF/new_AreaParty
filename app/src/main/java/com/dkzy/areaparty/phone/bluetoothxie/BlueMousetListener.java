package com.dkzy.areaparty.phone.bluetoothxie;

import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnGenericMotionListener;

import com.example.action.ControllerDroidAction;
import com.example.action.MouseClickAction;
import com.example.action.MouseMove2Action;
import com.example.action.MouseWheelAction;
import com.example.connection.DeviceConnection;

import java.io.IOException;

/**
 * Created by XIE on 2017/1/3.
 * 此类用来监听蓝牙鼠标操作
 */

public class BlueMousetListener implements OnGenericMotionListener,MouseAndBoardCallBack {

	private float moveSensitivity =0.7f;
	private float moveAcceleration= 1.4f;
	private float moveDownX;
	private float moveDownY;
	private float movePreviousX;
	private float movePreviousY;
	private float moveResultX;
	private float moveResultY;
	public static int offset = 0;
	private double dis_x;
	private double dis_y;
	private int move_times = 1;
	private boolean leftDown = false;
	
	private MouseAndBoardAsyncTask myTask;
	
	@Override
	public boolean onGenericMotion(View arg0, MotionEvent event) {

		 if(InputDevice.SOURCE_MOUSE == event.getSource())
		   {
			   switch (event.getAction()) {

				   /**
                    * 滚轮可用
					*/

				   case MotionEvent.ACTION_SCROLL:
						if( event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f){
							MouseWheelAction mouseWheel = new MouseWheelAction((byte)1);
							sendAction2Remote(mouseWheel);
							event.setLocation(50f, 50f);
						}
						else if(event.getAxisValue(MotionEvent.AXIS_VSCROLL) > 0.0f)
						{
							MouseWheelAction mouseWheel = new MouseWheelAction((byte)-1);
							sendAction2Remote(mouseWheel);
						}
						break;

				   case MotionEvent.ACTION_HOVER_MOVE:
                       /**
                        * 监听蓝牙鼠标在控件上的移动，发送坐标至电脑端
						*/
					   dis_x = offset+event.getRawX()*move_times;
					   dis_y = event.getRawY()*move_times;
					   if (dis_x != 0 || dis_y != 0){
						   MouseMove2Action mouseMove2Action = new MouseMove2Action((int) dis_x,(int) dis_y);
                           /**
                            * mouseMove2Action是鼠标移动action，参数是移动目地的坐标值
							*/
						   sendAction2Remote(mouseMove2Action);
					   }
						break;
			    }

			   switch (event.getButtonState()){
				/*case MotionEvent.BUTTON_PRIMARY:
					if (!leftDown) {
						MouseClickAction mouseCliekAction = new MouseClickAction(MouseClickAction.BUTTON_LEFT,
								MouseClickAction.STATE_DOWN);
						sendAction2Remote(mouseCliekAction);
						leftDown = true;
						return true;
					}else {
						MouseClickAction mouseCliekAction = new MouseClickAction(MouseClickAction.BUTTON_LEFT,
								MouseClickAction.STATE_UP);
						sendAction2Remote(mouseCliekAction);
						leftDown = false;
						return true;
					}*/

			   case MotionEvent.BUTTON_SECONDARY:
                   /**
                    *鼠标右键
                    */
				   MouseClickAction mouseCliekAction = new MouseClickAction(MouseClickAction.BUTTON_RIGHT,
						   MouseClickAction.STATE_DOWN);
				   sendAction2Remote(mouseCliekAction);
				   mouseCliekAction = new MouseClickAction(MouseClickAction.BUTTON_RIGHT,
						   MouseClickAction.STATE_UP);
				   sendAction2Remote(mouseCliekAction);
				   break;
		   }
   	   }

		return true;
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
					doInBackground();
				}
			}
		}).start();
	}
	@Override
	public void callback() {
		myTask = null;
	}
	private void doInBackground() {
		if (myTask != null) {
			return;
		}

		myTask = new MouseAndBoardAsyncTask(this);
		myTask.execute();
	}
	public static void  setOffset(int i){
		offset = i;
	}

}

