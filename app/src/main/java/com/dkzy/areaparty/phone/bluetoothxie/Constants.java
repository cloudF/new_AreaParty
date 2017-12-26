package com.dkzy.areaparty.phone.bluetoothxie;


import com.dkzy.areaparty.phone.myapplication.MyApplication;

public class Constants {
	public static final String PERFERENCE_NAME 				= "prefer_floating";

	public static final String PREF_KEY_FLOAT_X				= "float_x";
	public static final String PREF_KEY_FLOAT_Y				= "float_y";
	public static final String PREF_KEY_DISPLAY_ON_HOME		= "display_on_home";
	public static final String PREF_KEY_IS_RIGHT			= "is_right";
	public static final String PREF_KEY_GIFT_CLICK			= "get_gift_click";

	public static final String PREF_KEY_FIRST_SUCCESS_GET	= "first_success_get";

	public static final String PREF_KEY_GAME_ID	= "game_id";

	public static final String PREF_KEY_RED_POINT_EVENT	= "red_point_event";

	public static final String PREF_KEY_EVENT_DIALOG	= "event_dialog";

	public static final String GAME_PACKAGE_NAME="game_package_name_belongto2345";

	public static final String UPDATE_PATH="update_path_belongto2345";

	public static final String FIRST_FLOAT_VIEW="first_float_view_belongto2345";
	public static Constants instance;

	public static String IP_PC;//"192.168.1.119"

	public static String IP_TV;//"192.168.1.100"
	public static String getIP_PC(){
		if (MyApplication.getSelectedPCIP() != null)
			IP_PC = MyApplication.getSelectedPCIP().ip;
//		IP_PC = "192.168.1.119";
		return IP_PC;
	}
	public static String getIP_TV(){
/*		if (MyApplication.getInstance().getTVIPInforList().size()!=0)
			IP_TV = MyApplication.getInstance().getTVIPInforList().get(0).ip;*/
		if (MyApplication.getSelectedTVIP() != null)
			IP_TV = MyApplication.getSelectedTVIP().ip;
		return IP_TV;
	}
	public synchronized static Constants getInstance(){
		if (null == instance){
			instance = new Constants();
		}
		return instance;
	}
}
