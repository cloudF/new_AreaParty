package com.dkzy.areaparty.phone.bluetoothxie;


public class JavaAwtKeyEvent {

	/* Virtual key codes. */

	public static final int VK_ENTER = '\n'; // 13 0x0D
	public static final int VK_BACK_SPACE = '\b'; // 8 0x08
	public static final int VK_TAB = '\t'; // 9 0x09
	public static final int VK_CANCEL = 0x03;
	public static final int VK_CLEAR = 0x0C;
	public static final int VK_SHIFT = 0x10;
	public static final int VK_CONTROL = 0x11;
	public static final int VK_ALT = 0x12;
	public static final int VK_PAUSE = 0x13;
	public static final int VK_CAPS_LOCK = 0x14;
	public static final int VK_ESCAPE = 0x1B;
	public static final int VK_SPACE = 0x20;
	public static final int VK_PAGE_UP = 0x21;
	public static final int VK_PAGE_DOWN = 0x22;
	public static final int VK_END = 0x23;
	public static final int VK_HOME = 0x24;

	/**
	 * Constant for the non-numpad <b>left</b> arrow key.
	 * 
	 * @see #VK_KP_LEFT
	 */
	public static final int VK_LEFT = 0x25;

	/**
	 * Constant for the non-numpad <b>up</b> arrow key.
	 * 
	 * @see #VK_KP_UP
	 */
	public static final int VK_UP = 0x26;

	/**
	 * Constant for the non-numpad <b>right</b> arrow key.
	 * 
	 * @see #VK_KP_RIGHT
	 */
	public static final int VK_RIGHT = 0x27;

	/**
	 * Constant for the non-numpad <b>down</b> arrow key.
	 * 
	 * @see #VK_KP_DOWN
	 */
	public static final int VK_DOWN = 0x28;

	/**
	 * Constant for the comma key, ","
	 */
	public static final int VK_COMMA = 0x2C;

	/**
	 * Constant for the minus key, "-"
	 * 
	 * @since 1.2
	 */
	public static final int VK_MINUS = 0x2D;

	/**
	 * Constant for the period key, "."
	 */
	public static final int VK_PERIOD = 0x2E;

	/**
	 * Constant for the forward slash key, "/"
	 */
	public static final int VK_SLASH = 0x2F;

	/** VK_0 thru VK_9 are the same as ASCII '0' thru '9' (0x30 - 0x39) */
	public static final int VK_0 = 0x30;
	public static final int VK_1 = 0x31;
	public static final int VK_2 = 0x32;
	public static final int VK_3 = 0x33;
	public static final int VK_4 = 0x34;
	public static final int VK_5 = 0x35;
	public static final int VK_6 = 0x36;
	public static final int VK_7 = 0x37;
	public static final int VK_8 = 0x38;
	public static final int VK_9 = 0x39;

	/**
	 * Constant for the semicolon key, ";"
	 */
	public static final int VK_SEMICOLON = 0x3B;

	/**
	 * Constant for the equals key, "="
	 */
	public static final int VK_EQUALS = 0x3D;

	/** VK_A thru VK_Z are the same as ASCII 'A' thru 'Z' (0x41 - 0x5A) */
	public static final int VK_A = 0x41;
	public static final int VK_B = 0x42;
	public static final int VK_C = 0x43;
	public static final int VK_D = 0x44;
	public static final int VK_E = 0x45;
	public static final int VK_F = 0x46;
	public static final int VK_G = 0x47;
	public static final int VK_H = 0x48;
	public static final int VK_I = 0x49;
	public static final int VK_J = 0x4A;
	public static final int VK_K = 0x4B;
	public static final int VK_L = 0x4C;
	public static final int VK_M = 0x4D;
	public static final int VK_N = 0x4E;
	public static final int VK_O = 0x4F;
	public static final int VK_P = 0x50;
	public static final int VK_Q = 0x51;
	public static final int VK_R = 0x52;
	public static final int VK_S = 0x53;
	public static final int VK_T = 0x54;
	public static final int VK_U = 0x55;
	public static final int VK_V = 0x56;
	public static final int VK_W = 0x57;
	public static final int VK_X = 0x58;
	public static final int VK_Y = 0x59;
	public static final int VK_Z = 0x5A;

	/**
	 * Constant for the open bracket key, "["
	 */
	public static final int VK_OPEN_BRACKET = 0x5B;

	/**
	 * Constant for the back slash key, "\"
	 */
	public static final int VK_BACK_SLASH = 0x5C;

	/**
	 * Constant for the close bracket key, "]"
	 */
	public static final int VK_CLOSE_BRACKET = 0x5D;

	public static final int VK_NUMPAD0 = 0x60;
	public static final int VK_NUMPAD1 = 0x61;
	public static final int VK_NUMPAD2 = 0x62;
	public static final int VK_NUMPAD3 = 0x63;
	public static final int VK_NUMPAD4 = 0x64;
	public static final int VK_NUMPAD5 = 0x65;
	public static final int VK_NUMPAD6 = 0x66;
	public static final int VK_NUMPAD7 = 0x67;
	public static final int VK_NUMPAD8 = 0x68;
	public static final int VK_NUMPAD9 = 0x69;
	public static final int VK_MULTIPLY = 0x6A;
	public static final int VK_ADD = 0x6B;

	/**
	 * This constant is obsolete, and is included only for backwards
	 * compatibility.
	 * 
	 * @see #VK_SEPARATOR
	 */
	public static final int VK_SEPARATER = 0x6C;

	/**
	 * Constant for the Numpad Separator key.
	 * 
	 * @since 1.4
	 */
	public static final int VK_SEPARATOR = VK_SEPARATER;

	public static final int VK_SUBTRACT = 0x6D;
	public static final int VK_DECIMAL = 0x6E;
	public static final int VK_DIVIDE = 0x6F;
	public static final int VK_DELETE = 0x7F; /* ASCII DEL */
	public static final int VK_NUM_LOCK = 0x90;
	public static final int VK_SCROLL_LOCK = 0x91;

	/** Constant for the F1 function key. */
	public static final int VK_F1 = 0x70;

	/** Constant for the F2 function key. */
	public static final int VK_F2 = 0x71;

	/** Constant for the F3 function key. */
	public static final int VK_F3 = 0x72;

	/** Constant for the F4 function key. */
	public static final int VK_F4 = 0x73;

	/** Constant for the F5 function key. */
	public static final int VK_F5 = 0x74;

	/** Constant for the F6 function key. */
	public static final int VK_F6 = 0x75;

	/** Constant for the F7 function key. */
	public static final int VK_F7 = 0x76;

	/** Constant for the F8 function key. */
	public static final int VK_F8 = 0x77;

	/** Constant for the F9 function key. */
	public static final int VK_F9 = 0x78;

	/** Constant for the F10 function key. */
	public static final int VK_F10 = 0x79;

	/** Constant for the F11 function key. */
	public static final int VK_F11 = 0x7A;

	/** Constant for the F12 function key. */
	public static final int VK_F12 = 0x7B;

	public static final int VK_PRINTSCREEN = 0x9A;
	public static final int VK_INSERT = 0x9B;
	public static final int VK_HELP = 0x9C;
	public static final int VK_META = 0x9D;

	public static final int VK_BACK_QUOTE = 0xC0;
	public static final int VK_QUOTE = 0xDE;

	/**
	 * Constant for the numeric keypad <b>up</b> arrow key.
	 * 
	 * @see #VK_UP
	 * @since 1.2
	 */
	public static final int VK_KP_UP = 0xE0;

	/**
	 * Constant for the numeric keypad <b>down</b> arrow key.
	 * 
	 * @see #VK_DOWN
	 * @since 1.2
	 */
	public static final int VK_KP_DOWN = 0xE1;

	/**
	 * Constant for the numeric keypad <b>left</b> arrow key.
	 * 
	 * @see #VK_LEFT
	 * @since 1.2
	 */
	public static final int VK_KP_LEFT = 0xE2;

	/**
	 * Constant for the numeric keypad <b>right</b> arrow key.
	 * 
	 * @see #VK_RIGHT
	 * @since 1.2
	 */
	public static final int VK_KP_RIGHT = 0xE3;

	/* For European keyboards */
	/** @since 1.2 */
	public static final int VK_DEAD_GRAVE = 0x80;
	/** @since 1.2 */
	public static final int VK_DEAD_ACUTE = 0x81;
	/** @since 1.2 */
	public static final int VK_DEAD_CIRCUMFLEX = 0x82;
	/** @since 1.2 */
	public static final int VK_DEAD_TILDE = 0x83;
	/** @since 1.2 */
	public static final int VK_DEAD_MACRON = 0x84;
	/** @since 1.2 */
	public static final int VK_DEAD_BREVE = 0x85;
	/** @since 1.2 */
	public static final int VK_DEAD_ABOVEDOT = 0x86;
	/** @since 1.2 */
	public static final int VK_DEAD_DIAERESIS = 0x87;
	/** @since 1.2 */
	public static final int VK_DEAD_ABOVERING = 0x88;
	/** @since 1.2 */
	public static final int VK_DEAD_DOUBLEACUTE = 0x89;
	/** @since 1.2 */
	public static final int VK_DEAD_CARON = 0x8a;
	/** @since 1.2 */
	public static final int VK_DEAD_CEDILLA = 0x8b;
	/** @since 1.2 */
	public static final int VK_DEAD_OGONEK = 0x8c;
	/** @since 1.2 */
	public static final int VK_DEAD_IOTA = 0x8d;
	/** @since 1.2 */
	public static final int VK_DEAD_VOICED_SOUND = 0x8e;
	/** @since 1.2 */
	public static final int VK_DEAD_SEMIVOICED_SOUND = 0x8f;

	/** @since 1.2 */
	public static final int VK_AMPERSAND = 0x96;
	/** @since 1.2 */
	public static final int VK_ASTERISK = 0x97;
	/** @since 1.2 */
	public static final int VK_QUOTEDBL = 0x98;
	/** @since 1.2 */
	public static final int VK_LESS = 0x99;

	/** @since 1.2 */
	public static final int VK_GREATER = 0xa0;
	/** @since 1.2 */
	public static final int VK_BRACELEFT = 0xa1;
	/** @since 1.2 */
	public static final int VK_BRACERIGHT = 0xa2;

	/**
	 * Constant for the Microsoft Windows "Windows" key. It is used for both the
	 * left and right version of the key.
	 * 
	 * @see #getKeyLocation()
	 * @since 1.5
	 */
	public static final int VK_WINDOWS = 0x020C;

	/**
	 * Constant for the Microsoft Windows Context Menu key.
	 * 
	 * @since 1.5
	 */
	public static final int VK_CONTEXT_MENU = 0x020D;

	/* for Sun keyboards */
	/** @since 1.2 */
	public static final int VK_CUT = 0xFFD1;
	/** @since 1.2 */
	public static final int VK_COPY = 0xFFCD;
	/** @since 1.2 */
	public static final int VK_PASTE = 0xFFCF;
	/** @since 1.2 */
	public static final int VK_UNDO = 0xFFCB;
	/** @since 1.2 */
	public static final int VK_AGAIN = 0xFFC9;
	/** @since 1.2 */
	public static final int VK_FIND = 0xFFD0;
	/** @since 1.2 */
	public static final int VK_PROPS = 0xFFCA;
	/** @since 1.2 */
	public static final int VK_STOP = 0xFFC8;

	/**
	 * Constant for the Compose function key.
	 * 
	 * @since 1.2
	 */
	public static final int VK_COMPOSE = 0xFF20;

	/**
	 * Constant for the AltGraph function key.
	 * 
	 * @since 1.2
	 */
	public static final int VK_ALT_GRAPH = 0xFF7E;

	/**
	 * Constant for the Begin key.
	 * 
	 * @since 1.5
	 */
	public static final int VK_BEGIN = 0xFF58;

	/**
	 * This value is used to indicate that the keyCode is unknown. KEY_TYPED
	 * events do not have a keyCode value; this value is used instead.
	 */
	public static final int VK_UNDEFINED = 0x0;

	/**
	 * specially generated keys
	 */
	public static final int VK_VOLUME_UP = 0xB0;
	public static final int VK_VOLUME_DOWN = 0xB1;
	public static final int VK_VOLUME_MUTE = 0xB2;

	public static final int VK_MEDIA_PLAY = 0xB3;
	public static final int VK_MEDIA_PAUSE = 0xB4;
	public static final int VK_MEDIA_NEXT = 0xB5;
	public static final int VK_MEDIA_PREVIOUS = 0xB6;
	public static final int VK_MEDIA_STOP = 0xB7;

}
