package com.dkzy.areaparty.phone.bluetoothxie;


import android.util.SparseIntArray;

public class KeyCode {
	// private static Map<Integer, Integer> keycodes;
	private static SparseIntArray keycodes;

	static {
		// keycodes = new HashMap<Integer, Integer>();
		keycodes = new SparseIntArray(100);

		keycodes.put(android.view.KeyEvent.KEYCODE_0, JavaAwtKeyEvent.VK_0);
		keycodes.put(android.view.KeyEvent.KEYCODE_1, JavaAwtKeyEvent.VK_1);
		keycodes.put(android.view.KeyEvent.KEYCODE_2, JavaAwtKeyEvent.VK_2);
		keycodes.put(android.view.KeyEvent.KEYCODE_3, JavaAwtKeyEvent.VK_3);
		keycodes.put(android.view.KeyEvent.KEYCODE_4, JavaAwtKeyEvent.VK_4);
		keycodes.put(android.view.KeyEvent.KEYCODE_5, JavaAwtKeyEvent.VK_5);
		keycodes.put(android.view.KeyEvent.KEYCODE_6, JavaAwtKeyEvent.VK_6);
		keycodes.put(android.view.KeyEvent.KEYCODE_7, JavaAwtKeyEvent.VK_7);
		keycodes.put(android.view.KeyEvent.KEYCODE_8, JavaAwtKeyEvent.VK_8);
		keycodes.put(android.view.KeyEvent.KEYCODE_9, JavaAwtKeyEvent.VK_9);

		keycodes.put(android.view.KeyEvent.KEYCODE_A, JavaAwtKeyEvent.VK_A);
		keycodes.put(android.view.KeyEvent.KEYCODE_B, JavaAwtKeyEvent.VK_B);
		keycodes.put(android.view.KeyEvent.KEYCODE_C, JavaAwtKeyEvent.VK_C);
		keycodes.put(android.view.KeyEvent.KEYCODE_D, JavaAwtKeyEvent.VK_D);
		keycodes.put(android.view.KeyEvent.KEYCODE_E, JavaAwtKeyEvent.VK_E);
		keycodes.put(android.view.KeyEvent.KEYCODE_F, JavaAwtKeyEvent.VK_F);
		keycodes.put(android.view.KeyEvent.KEYCODE_G, JavaAwtKeyEvent.VK_G);
		keycodes.put(android.view.KeyEvent.KEYCODE_H, JavaAwtKeyEvent.VK_H);
		keycodes.put(android.view.KeyEvent.KEYCODE_I, JavaAwtKeyEvent.VK_I);
		keycodes.put(android.view.KeyEvent.KEYCODE_J, JavaAwtKeyEvent.VK_J);
		keycodes.put(android.view.KeyEvent.KEYCODE_K, JavaAwtKeyEvent.VK_K);
		keycodes.put(android.view.KeyEvent.KEYCODE_L, JavaAwtKeyEvent.VK_L);
		keycodes.put(android.view.KeyEvent.KEYCODE_M, JavaAwtKeyEvent.VK_M);
		keycodes.put(android.view.KeyEvent.KEYCODE_N, JavaAwtKeyEvent.VK_N);
		keycodes.put(android.view.KeyEvent.KEYCODE_O, JavaAwtKeyEvent.VK_O);
		keycodes.put(android.view.KeyEvent.KEYCODE_P, JavaAwtKeyEvent.VK_P);
		keycodes.put(android.view.KeyEvent.KEYCODE_Q, JavaAwtKeyEvent.VK_Q);
		keycodes.put(android.view.KeyEvent.KEYCODE_R, JavaAwtKeyEvent.VK_R);
		keycodes.put(android.view.KeyEvent.KEYCODE_S, JavaAwtKeyEvent.VK_S);
		keycodes.put(android.view.KeyEvent.KEYCODE_T, JavaAwtKeyEvent.VK_T);
		keycodes.put(android.view.KeyEvent.KEYCODE_U, JavaAwtKeyEvent.VK_U);
		keycodes.put(android.view.KeyEvent.KEYCODE_V, JavaAwtKeyEvent.VK_V);
		keycodes.put(android.view.KeyEvent.KEYCODE_W, JavaAwtKeyEvent.VK_W);
		keycodes.put(android.view.KeyEvent.KEYCODE_X, JavaAwtKeyEvent.VK_X);
		keycodes.put(android.view.KeyEvent.KEYCODE_Y, JavaAwtKeyEvent.VK_Y);
		keycodes.put(android.view.KeyEvent.KEYCODE_Z, JavaAwtKeyEvent.VK_Z);

		keycodes.put(android.view.KeyEvent.KEYCODE_CAPS_LOCK,
				JavaAwtKeyEvent.VK_CAPS_LOCK);
		keycodes.put(android.view.KeyEvent.KEYCODE_ENTER,
				JavaAwtKeyEvent.VK_ENTER);
		keycodes.put(android.view.KeyEvent.KEYCODE_SPACE,
				JavaAwtKeyEvent.VK_SPACE);
		// delete
		keycodes.put(android.view.KeyEvent.KEYCODE_FORWARD_DEL,
				JavaAwtKeyEvent.VK_DELETE);
		// backspace
		keycodes.put(android.view.KeyEvent.KEYCODE_DEL,
				JavaAwtKeyEvent.VK_BACK_SPACE);
		keycodes.put(android.view.KeyEvent.KEYCODE_TAB, JavaAwtKeyEvent.VK_TAB);
		keycodes.put(android.view.KeyEvent.KEYCODE_ESCAPE,
				JavaAwtKeyEvent.VK_ESCAPE);
		// back
		keycodes.put(android.view.KeyEvent.KEYCODE_BACK,
				JavaAwtKeyEvent.VK_ESCAPE);

		keycodes.put(android.view.KeyEvent.KEYCODE_CTRL_LEFT,
				JavaAwtKeyEvent.VK_CONTROL);
		keycodes.put(android.view.KeyEvent.KEYCODE_CTRL_RIGHT,
				JavaAwtKeyEvent.VK_CONTROL);

		keycodes.put(android.view.KeyEvent.KEYCODE_SHIFT_LEFT,
				JavaAwtKeyEvent.VK_SHIFT);
		keycodes.put(android.view.KeyEvent.KEYCODE_SHIFT_RIGHT,
				JavaAwtKeyEvent.VK_SHIFT);

		keycodes.put(android.view.KeyEvent.KEYCODE_ALT_LEFT,
				JavaAwtKeyEvent.VK_ALT);
		keycodes.put(android.view.KeyEvent.KEYCODE_ALT_RIGHT,
				JavaAwtKeyEvent.VK_ALT);

		keycodes.put(android.view.KeyEvent.KEYCODE_DPAD_LEFT,
				JavaAwtKeyEvent.VK_LEFT);
		keycodes.put(android.view.KeyEvent.KEYCODE_DPAD_UP,
				JavaAwtKeyEvent.VK_UP);
		keycodes.put(android.view.KeyEvent.KEYCODE_DPAD_DOWN,
				JavaAwtKeyEvent.VK_DOWN);
		keycodes.put(android.view.KeyEvent.KEYCODE_DPAD_RIGHT,
				JavaAwtKeyEvent.VK_RIGHT);

		/*
		 * need to set volume up\down\mute code
		 */

		keycodes.put(android.view.KeyEvent.KEYCODE_VOLUME_UP,
				JavaAwtKeyEvent.VK_VOLUME_UP);
		keycodes.put(android.view.KeyEvent.KEYCODE_VOLUME_DOWN,
				JavaAwtKeyEvent.VK_VOLUME_DOWN);
		keycodes.put(android.view.KeyEvent.KEYCODE_VOLUME_MUTE,
				JavaAwtKeyEvent.VK_VOLUME_MUTE);

		/*
		 * need to set browse back tasks camera media_play\pause\prev\next menu
		 */

		// media
		keycodes.put(android.view.KeyEvent.KEYCODE_MEDIA_PLAY,
				JavaAwtKeyEvent.VK_MEDIA_PLAY);
		keycodes.put(android.view.KeyEvent.KEYCODE_MEDIA_PAUSE,
				JavaAwtKeyEvent.VK_MEDIA_PAUSE);
		keycodes.put(android.view.KeyEvent.KEYCODE_MEDIA_NEXT,
				JavaAwtKeyEvent.VK_MEDIA_NEXT);
		keycodes.put(android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS,
				JavaAwtKeyEvent.VK_MEDIA_PREVIOUS);
		keycodes.put(android.view.KeyEvent.KEYCODE_MEDIA_STOP,
				JavaAwtKeyEvent.VK_MEDIA_STOP);

		// MENU
		keycodes.put(android.view.KeyEvent.KEYCODE_MENU,
				JavaAwtKeyEvent.VK_CONTEXT_MENU);
		// home for windows?
		keycodes.put(android.view.KeyEvent.KEYCODE_HOME,
				JavaAwtKeyEvent.VK_WINDOWS);

		/*
		 * symbols
		 */
		// `
		keycodes.put(android.view.KeyEvent.KEYCODE_GRAVE,
				JavaAwtKeyEvent.VK_BACK_QUOTE);
		// -
		keycodes.put(android.view.KeyEvent.KEYCODE_MINUS,
				JavaAwtKeyEvent.VK_MINUS);
		// =
		keycodes.put(android.view.KeyEvent.KEYCODE_EQUALS,
				JavaAwtKeyEvent.VK_EQUALS);
		// [
		keycodes.put(android.view.KeyEvent.KEYCODE_LEFT_BRACKET,
				JavaAwtKeyEvent.VK_OPEN_BRACKET);
		// ]
		keycodes.put(android.view.KeyEvent.KEYCODE_RIGHT_BRACKET,
				JavaAwtKeyEvent.VK_CLOSE_BRACKET);
		// \
		keycodes.put(android.view.KeyEvent.KEYCODE_BACKSLASH,
				JavaAwtKeyEvent.VK_BACK_SLASH);
		// ;
		keycodes.put(android.view.KeyEvent.KEYCODE_SEMICOLON,
				JavaAwtKeyEvent.VK_SEMICOLON);
		// '
		keycodes.put(android.view.KeyEvent.KEYCODE_APOSTROPHE,
				JavaAwtKeyEvent.VK_QUOTE);
		// ,
		keycodes.put(android.view.KeyEvent.KEYCODE_COMMA,
				JavaAwtKeyEvent.VK_COMMA);
		// .
		keycodes.put(android.view.KeyEvent.KEYCODE_PERIOD,
				JavaAwtKeyEvent.VK_PERIOD);
		// /
		keycodes.put(android.view.KeyEvent.KEYCODE_SLASH,
				JavaAwtKeyEvent.VK_SLASH);

		/*
		 * F1 - F12, need using FN to push
		 */
		keycodes.put(android.view.KeyEvent.KEYCODE_F1, JavaAwtKeyEvent.VK_F1);
		keycodes.put(android.view.KeyEvent.KEYCODE_F2, JavaAwtKeyEvent.VK_F2);
		keycodes.put(android.view.KeyEvent.KEYCODE_F3, JavaAwtKeyEvent.VK_F3);
		keycodes.put(android.view.KeyEvent.KEYCODE_F4, JavaAwtKeyEvent.VK_F4);
		keycodes.put(android.view.KeyEvent.KEYCODE_F5, JavaAwtKeyEvent.VK_F5);
		keycodes.put(android.view.KeyEvent.KEYCODE_F6, JavaAwtKeyEvent.VK_F6);
		keycodes.put(android.view.KeyEvent.KEYCODE_F7, JavaAwtKeyEvent.VK_F7);
		keycodes.put(android.view.KeyEvent.KEYCODE_F8, JavaAwtKeyEvent.VK_F8);
		keycodes.put(android.view.KeyEvent.KEYCODE_F9, JavaAwtKeyEvent.VK_F9);
		keycodes.put(android.view.KeyEvent.KEYCODE_F10, JavaAwtKeyEvent.VK_F10);
		keycodes.put(android.view.KeyEvent.KEYCODE_F11, JavaAwtKeyEvent.VK_F11);
		keycodes.put(android.view.KeyEvent.KEYCODE_F12, JavaAwtKeyEvent.VK_F12);
	}

	public static int getJavaAwtKeyCode(int code) {
		int index = -1;
		if (keycodes.get(code) != -1) {
			return keycodes.get(code);
		}
		return index;
	}
}
