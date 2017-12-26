package com.dkzy.areaparty.phone.bluetoothxie;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;


/**
 * @author gyw 
 * @version 1.0
 * @time: 2015-10-12 下午3:06:52 
 * @fun:
 */
public class EditDialog extends Dialog implements View.OnClickListener {

	private View mView;
	private Context mContext;

	private LinearLayout mBgLl;
	private TextView mTitleTv;
	private EditText mMsgEt;
	private Button mNegBtn;
	private Button mPosBtn;
	private CheckBox mouse_check;
	private CheckBox keyboard_check;
	private CheckBox audio_check;
	private CheckBox gamepad_check;
	public static final int MOUSE = 0;
	public static final int KEYBOARD = 1;
	public static final int AUDIO = 2;
	public static final int GAMEPAD = 3;
	public static int type = 4;

	public EditDialog(Context context) {
		this(context, 0, null);
	}

	public EditDialog(Context context, int theme, View contentView) {
		super(context, theme == 0 ? R.style.MyDialogStyle : theme);

		this.mView = contentView;
		this.mContext = context;

		if (mView == null) {
			mView = View.inflate(mContext, R.layout.view_enter_edit, null);
		}

		init();
		initView();
		initData();
		initListener();
	}

	private void init() {
		this.setContentView(mView);
	}

	private void initView() {
		mBgLl = (LinearLayout) mView.findViewById(R.id.lLayout_bg);
		mTitleTv = (TextView) mView.findViewById(R.id.txt_title);
		mMsgEt = (EditText) mView.findViewById(R.id.et_msg);
		mNegBtn = (Button) mView.findViewById(R.id.btn_neg);
		mPosBtn = (Button) mView.findViewById(R.id.btn_pos);
		mouse_check = (CheckBox) mView.findViewById(R.id.mouse_check);
		keyboard_check = (CheckBox) mView.findViewById(R.id.keyboard_check);
		audio_check = (CheckBox) mView.findViewById(R.id.audio_check);
		gamepad_check = (CheckBox) mView.findViewById(R.id.gamepad_check);
	}

	private void initData() {
		//设置背景是屏幕的0.85
		mBgLl.setLayoutParams(new FrameLayout.LayoutParams((int) (getMobileWidth(mContext) * 0.85), LayoutParams.WRAP_CONTENT));
		//设置默认为10000
		mMsgEt.setHint(String.valueOf("自定义名称"));
	}

	private void initListener() {
		mNegBtn.setOnClickListener(this);
		mPosBtn.setOnClickListener(this);
		mouse_check.setOnCheckedChangeListener(checkedChangeListener);
		keyboard_check.setOnCheckedChangeListener(checkedChangeListener);
		audio_check.setOnCheckedChangeListener(checkedChangeListener);
		gamepad_check.setOnCheckedChangeListener(checkedChangeListener);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_neg:	//取消,
			if(onPosNegClickListener != null) {
				String mEtValue = mMsgEt.getHint().toString().trim();
//				if(!mEtValue.isEmpty()&&type!=-1) {
					onPosNegClickListener.negCliclListener(mEtValue,type);
//				}
			}
			
			this.dismiss();
			break;

		case R.id.btn_pos:	//确认
			if(onPosNegClickListener != null) {
				String mEtValue = mMsgEt.getText().toString().trim();
//				if(!mEtValue.isEmpty()&&type!=-1) {
//					if (mEtValue.length() > 8 || mEtValue.length() < 4 || Integer.parseInt(mEtValue) <= 0) {
//						//TODO 这里处理条件
//					}
					onPosNegClickListener.posClickListener(mEtValue,type);
//				}
			}
			this.dismiss();
			break;
		}
	}
	CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()){
				case R.id.mouse_check:
					if (isChecked){
						type = MOUSE;
					}else type = 4;
					break;
				case R.id.keyboard_check:
					if(isChecked){
						type = KEYBOARD;
					}else type = 4;
					break;
				case R.id.audio_check:
					if(isChecked){
						type = AUDIO;
					}else type = 4;
					break;
				case R.id.gamepad_check:
					if (isChecked){
						type = GAMEPAD;
					}else type = 4;
					break;
			}
		}
	};
	private OnPosNegClickListener onPosNegClickListener;
	
	public void setOnPosNegClickListener (OnPosNegClickListener onPosNegClickListener) {
		this.onPosNegClickListener = onPosNegClickListener;
	}
	
	public interface OnPosNegClickListener {
		void posClickListener(String value, int type);
		void negCliclListener(String value, int type);
	}



	/**
	 * 工具类
	 * @param context
	 * @return
	 */
	public static int getMobileWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels; // 得到宽度
		return width;
	}
}
