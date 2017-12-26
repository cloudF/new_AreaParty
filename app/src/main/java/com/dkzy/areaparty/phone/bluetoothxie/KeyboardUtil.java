package com.dkzy.areaparty.phone.bluetoothxie;
import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;
import com.example.action.ControllerDroidAction;
import com.example.action.KeyboardAction;
import com.example.connection.DeviceConnection;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KeyboardUtil {

	private final Activity act;
	private final Context ctx;
	private final EditText edit;

	private static boolean isPressing = false;
	private static int lastcode;

	private boolean isAlt = false;
	private boolean isCapsLock = false;
	private boolean isCtrl = false;
	private boolean isShift = false;
	private boolean isWindows = false;
	private int count = 0;

	private final Keyboard k1;
	private final Keyboard k2;

	private final KeyboardView keyboardView;
	private KeyboardView.OnKeyboardActionListener listener;

	private final Lock lock = new ReentrantLock();
	private MouseAndBoardAsyncTask myTask;
    private MouseAndBoardCallBack callBack;

	private final ExecutorService singleThread;
	private final List<Keyboard.Key> keyList;

	private final int VK_SHIFT = -8;
	private final int VK_CLEAR = -9;
	
	public KeyboardUtil(Activity act, Context ctx, EditText edit) {
		this.ctx = ctx;
		this.act = act;
		this.edit = edit;

		k1 = new Keyboard(this.ctx, R.xml.layer_qwerty);
		k2 = new Keyboard(this.ctx, R.xml.layer_symbols);
		keyList = k1.getKeys();
		keyboardView = (KeyboardView) this.act.findViewById(R.id.keyboard_view);
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(false);
		keyboardView.setKeyboard(k1);
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(false);

		initKeyboardViewListener();
		keyboardView.setOnKeyboardActionListener(listener);

		singleThread = Executors.newSingleThreadExecutor();

	}

	private void initKeyboardViewListener() {
		listener = new KeyboardView.OnKeyboardActionListener() {
			@Override
			public void onKey(int primaryCode, int[] keyCodes) {
				Editable editable = edit.getText();
				int start = edit.getSelectionStart();
				ControllerDroidAction action = null;
				switch (primaryCode) {
					case KeyboardAction.VK_1:
						editable.insert(start, Character.toString((char) (primaryCode)));
						break;
					case KeyboardAction.VK_2:
						editable.insert(start, Character.toString((char) (primaryCode)));
						break;
					case KeyboardAction.VK_3:
						editable.insert(start, Character.toString((char) (primaryCode)));
						break;
					case KeyboardAction.VK_4:
						editable.insert(start, Character.toString((char) (primaryCode)));
						break;
					case KeyboardAction.VK_5:
						editable.insert(start, Character.toString((char) (primaryCode)));
						break;
					case KeyboardAction.VK_6:
						editable.insert(start, Character.toString((char) (primaryCode)));
						break;
					case KeyboardAction.VK_7:
						editable.insert(start, Character.toString((char) (primaryCode)));
						break;
					case KeyboardAction.VK_8:
						editable.insert(start, Character.toString((char) (primaryCode)));
						break;
					case KeyboardAction.VK_9:
						editable.insert(start, Character.toString((char) (primaryCode)));
						break;
					case KeyboardAction.VK_0:
						editable.insert(start, Character.toString((char) (primaryCode)));
						break;
					case KeyboardAction.VK_A:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_B:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_C:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_D:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_E:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_F:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_G:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_H:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_I:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_J:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_K:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_L:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_M:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_N:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_O:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_P:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_Q:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_R:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_S:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_T:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_U:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_V:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_W:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_X:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_Y:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_Z:
						if(isShift){
							editable.insert(start, Character.toString((char) (primaryCode)));
						} else editable.insert(start, Character.toString((char) (primaryCode+32)));
						break;
					case KeyboardAction.VK_BACK_SPACE:
						if (editable != null && editable.length() > 0) {
							if (start > 0) {
								editable.delete(start - 1, start);
							}
						}
						break;
					case KeyboardAction.VK_CAPS_LOCK :
						if (isCapsLock) {
								isCapsLock = false;
								action = new KeyboardAction(primaryCode);
								for (Keyboard.Key key : keyList) {
								if (key.label != null && isWord(key.label.toString())) {
									key.label =
											key.label.toString().toLowerCase(Locale.getDefault());
								}
							}
						} else {
							isCapsLock = true;
							action = new KeyboardAction(primaryCode);
							for (Keyboard.Key key : keyList) {
								if (key.label != null && isWord(key.label.toString())) {
									key.label =
											key.label.toString().toUpperCase(Locale.getDefault());
								}
							}
						}
						keyboardView.setKeyboard(k1);
						break;
					case KeyboardAction.VK_SHIFT :
						if (isShift) {
							isShift = false;
							action = new KeyboardAction(primaryCode, false);
							for (Keyboard.Key key : keyList) {
								if (key.label != null && isWord(key.label.toString())) {
									key.label = key.label.toString().toLowerCase(Locale.getDefault());
								}

							}
						} else {
							isShift = true;
							action = new KeyboardAction(primaryCode, true);
							for (Keyboard.Key key : keyList) {
								if (key.label != null && isWord(key.label.toString())) {
									key.label = key.label.toString().toUpperCase(Locale.getDefault());
								}
							}
						}
						keyboardView.setKeyboard(k1);
						break;
					case KeyboardAction.VK_CONTROL :
						if (isCtrl) {
							action = new KeyboardAction(primaryCode, false);
							isCtrl = false;
						} else {
							action = new KeyboardAction(primaryCode, true);
							isCtrl = true;
						}
						break;
					case KeyboardAction.VK_ALT :
						if (isAlt) {
							action = new KeyboardAction(primaryCode, false);
							isAlt = false;
						} else {
							action = new KeyboardAction(primaryCode, true);
							isAlt = true;
						}
						break;
					case KeyboardAction.VK_WINDOWS :
						if (isWindows) {
							action = new KeyboardAction(primaryCode, false);
							isWindows = false;
						} else {
							action = new KeyboardAction(primaryCode, true);
							isWindows = true;
						}
						break;
					case Keyboard.KEYCODE_CANCEL :
						keyboardView.setKeyboard(k1);
						break;
					case Keyboard.KEYCODE_MODE_CHANGE :
						keyboardView.setKeyboard(k2);
						break;

					case KeyboardAction.VK_PRINTSCREEN :
						act.finish();
						/*
						 * action = new SystemControlAction(
						 * SystemControlAction.SAVE_SCREEN);
						 */
						break;
					case VK_SHIFT:
						action = new KeyboardAction(16);
						break;
					case VK_CLEAR:
						editable.clear();
						break;
					default :


				}

				if (action != null) {
					sendAction2Remote(action);
				}
			}

			@Override
			public void onPress(final int primaryCode) {
				if (isPressing && lastcode == primaryCode) {
					return;
				}

				switch (primaryCode) {
					case KeyboardAction.VK_CAPS_LOCK :
					case KeyboardAction.VK_SHIFT :
					case KeyboardAction.VK_CONTROL :
					case KeyboardAction.VK_ALT :
					case KeyboardAction.VK_WINDOWS :
					case Keyboard.KEYCODE_CANCEL :
					case Keyboard.KEYCODE_MODE_CHANGE :

					case KeyboardAction.VK_PRINTSCREEN :
						return;
					default :
						break;
				}

				final KeyboardAction action = new KeyboardAction(primaryCode, true);

				lock.lock();
				isPressing = true;
				sendAction2Remote(action);
				lastcode = primaryCode;
				if ((count & 0xa) == 0xa) {
					count = 0;
				}
				final int c = ++count;
				lock.unlock();

				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				if (isPressing && lastcode == primaryCode) {
					singleThread.execute(new Runnable() {

						@Override
						public void run() {
							try {
								if (isPressing && lastcode == primaryCode && c == count) {
									Thread.sleep(700);
								} else {
									return;
								}
								while (isPressing && lastcode == primaryCode && c == count) {
									sendAction2Remote(action);
									Thread.sleep(80);
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}

			@Override
			public void onRelease(int primaryCode) {
				switch (primaryCode) {
					case KeyboardAction.VK_CAPS_LOCK :
					case KeyboardAction.VK_SHIFT :
					case KeyboardAction.VK_CONTROL :
					case KeyboardAction.VK_ALT :
					case KeyboardAction.VK_WINDOWS :
					case Keyboard.KEYCODE_CANCEL :
					case Keyboard.KEYCODE_MODE_CHANGE :

					case KeyboardAction.VK_PRINTSCREEN :
						return;
					default :
						KeyboardAction keyboardAction = new KeyboardAction(primaryCode, false);
						lock.lock();
						isPressing = false;
						sendAction2Remote(keyboardAction);
						lastcode = -1;
						lock.unlock();
				}
			}

			@Override
			public void onText(CharSequence text) {
			}

			@Override
			public void swipeDown() {
			}

			@Override
			public void swipeLeft() {
			}

			@Override
			public void swipeRight() {
			}

			@Override
			public void swipeUp() {
			}
		};
	}

	private boolean isWord(String str) {
		String wordStr = "abcdefghijklmnopqrstuvwxyz";
		return wordStr.contains(str.toLowerCase(Locale.getDefault()));
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
					//Toast.makeText(ctx, "连接已断开,正在重连...", Toast.LENGTH_SHORT).show();
					doInBackground();
				}
			}
		}).run();

	}

	public void callback() {
		if (DeviceConnection.getInstance().isAuthentificated()) {
			Toast.makeText(ctx, "连接成功", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(ctx, "连接失败", Toast.LENGTH_SHORT).show();
		}

		myTask = null;
	}

	public void showKeyboard() {
		int visibility = keyboardView.getVisibility();
		if (visibility == View.GONE || visibility == View.INVISIBLE) {
			keyboardView.setVisibility(View.VISIBLE);
		}
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
	
	
}
