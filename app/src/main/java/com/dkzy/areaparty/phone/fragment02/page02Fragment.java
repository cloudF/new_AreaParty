package com.dkzy.areaparty.phone.fragment02;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.MainActivity;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.androideventbusutils.events.SelectedDeviceChangedEvent;
import com.dkzy.areaparty.phone.androideventbusutils.events.TVPCNetStateChangeEvent;
import com.dkzy.areaparty.phone.androideventbusutils.events.TvPlayerChangeEvent;
import com.dkzy.areaparty.phone.androideventbusutils.events.changeSelectedDeviceNameEvent;
import com.dkzy.areaparty.phone.fragment02.Model.MediaItem;
import com.dkzy.areaparty.phone.fragment02.searchContent.SearchMediaActivity;
import com.dkzy.areaparty.phone.fragment02.ui.BreakTextView;
import com.dkzy.areaparty.phone.fragment02.utils.MediafileHelper;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.ReceiveCommandFromTVPlayer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import es.dmoral.toasty.Toasty;


/**
 * Created by boris on 2016/11/29.
 * TAB02---媒体界面的Fragment
 */

public class page02Fragment extends Fragment implements View.OnClickListener{

    private Context mContext;
    private View rootView;
    private ImageView PCStateIV, TVStateIV;
    private TextView  PCNameTV, TVNameTV;
    private LinearLayout videoLibLL, audioLibLL, picLibLL;
    private LinearLayout picsPlayListLL, audiosPlayListLL;
    private TextView picsPlayListNumTV, audiosPlayListNumTV;
    private ImageView lastVideoThumbnailIV, audioPicIV, remoteControlImg;
    private TextView lastVideoNameTV, moreVideoRecordsTV;
    private TextView lastAudioNameTV, moreAudioRecordsTV;
    private ImageButton  lastAudioCastIB, lastVideoCastIB;
    private LinearLayout remoteControlLayout;
    private EditText searchET;

    private TextView helpInfo;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab02, container, false);
        rootView = view;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(MyApplication.selectedPCVerified &&
                MediafileHelper.getAudioSets().size() <= 0 &&
                MediafileHelper.getImageSets().size() <= 0 &&
                MediafileHelper.getRecentVideos().size() <= 0 &&
                MediafileHelper.getRecentAudios().size() <= 0) {
            MediafileHelper.loadMediaSets(myHandler);
            MediafileHelper.loadRecentMediaFiles(myHandler);
        }
        audiosPlayListNumTV.setText("(" + MediafileHelper.getAudioSets().size() + ")");
        picsPlayListNumTV.setText("(" + MediafileHelper.getImageSets().size() + ")");
        setLastChosenMedia(OrderConst.audioAction_name, true);
        setLastChosenMedia(OrderConst.videoAction_name, true);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.videoLibLL:
                if((MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline())||(MyApplication.selectedTVVerified && MyApplication.isSelectedTVOnline())) {
                    MediafileHelper.setMediaType(OrderConst.videoAction_name);
                    startActivity(new Intent(mContext, videoLibActivity.class));
                } else  Toasty.warning(mContext, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.audioLibLL:
                if((MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline())||(MyApplication.selectedTVVerified && MyApplication.isSelectedTVOnline())) {
                    MediafileHelper.setMediaType(OrderConst.audioAction_name);
                    startActivity(new Intent(mContext, audioLibActivity.class));
                } else  Toasty.warning(mContext, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.picLibLL:
                if((MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline())||(MyApplication.selectedTVVerified && MyApplication.isSelectedTVOnline())) {
                    MediafileHelper.setMediaType(OrderConst.imageAction_name);
                    startActivity(new Intent(mContext, imageLibActivity.class));
                } else  Toasty.warning(mContext, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.picsPlayListLL:
                if((MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline())||(MyApplication.selectedTVVerified && MyApplication.isSelectedTVOnline())) {
                    startActivity(new Intent(mContext, imageSetActivity.class));
                } else  Toasty.warning(mContext, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.audiosPlayListLL:
                if((MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline())||(MyApplication.selectedTVVerified && MyApplication.isSelectedTVOnline())) {
                    startActivity(new Intent(mContext, audioSetActivity.class));
                } else  Toasty.warning(mContext, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.moreVideoRecordsTV:
                if(MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline()) {
                    startActivity(new Intent(mContext, recentVideosActivity.class));
                }
                break;
            case R.id.moreAudioRecordsTV:
                if(MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline()) {
                    startActivity(new Intent(mContext, recentAudiosActivity.class));
                }
                break;
            case R.id.lastVideoCastIB:
                if(MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline()) {
                    if(MyApplication.isSelectedTVOnline()) {
                        MediaItem file = MediafileHelper.getRecentVideos().get(0);
                        MediafileHelper.playMediaFile(file.getType(),
                                file.getPathName(),
                                file.getName(),
                                MyApplication.getSelectedTVIP().name,
                                myHandler);
                        startActivity(new Intent(MyApplication.getContext(), vedioPlayControl.class));
                    } else Toasty.warning(mContext, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                } else Toasty.warning(mContext, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.lastAudioCastIB:
                if(MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline()) {
                    if(MyApplication.isSelectedTVOnline()) {
                        MediaItem file = MediafileHelper.getRecentAudios().get(0);
                        MediafileHelper.playMediaFile(file.getType(),
                                file.getPathName(),
                                file.getName(),
                                MyApplication.getSelectedTVIP().name,
                                myHandler);
                    } else Toasty.warning(mContext, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                } else Toasty.warning(mContext, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.RemoteControlLayout:
                if (MyApplication.isSelectedTVOnline()){
                    startActivity(new Intent(MyApplication.getContext(), vedioPlayControl.class));
                }else {
                    Toasty.warning(mContext, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                }
                break;
            case R.id.search_editText:
                startActivity(new Intent(MyApplication.getContext(), SearchMediaActivity.class));
                break;
            case R.id.helpInfo:
                ((MainActivity)getActivity()).showHelpInfoDialog(R.layout.dialog_page02);
                break;
        }
    }

    @Subscriber(tag = "selectedTVNameChange")
    private void upDateTVName(changeSelectedDeviceNameEvent event) {
        TVNameTV.setText("在线");
    }

    @Subscriber(tag = "selectedPCNameChange")
    private void upDatePCName(changeSelectedDeviceNameEvent event) {
        PCNameTV.setText("在线");
    }

    @Subscriber(tag = "selectedPCChanged")
    private void updatePCState(SelectedDeviceChangedEvent event) {
        setLastChosenMedia(OrderConst.videoAction_name, false);
        setLastChosenMedia(OrderConst.audioAction_name, false);
        audiosPlayListNumTV.setText("(0)");
        picsPlayListNumTV.setText("(0)");
        if(event.isDeviceOnline()) {
            // 重置界面(最近播放、播放列表)
            PCStateIV.setImageResource(R.drawable.pcconnected);
            PCNameTV.setText("在线");
            PCNameTV.setTextColor(Color.parseColor("#ffffff"));
            // 重新获取数据
            MediafileHelper.loadRecentMediaFiles(myHandler);
            MediafileHelper.loadMediaSets(myHandler);
        } else {
            PCStateIV.setImageResource(R.drawable.pcbroke);
            PCNameTV.setText("离线中");
            PCNameTV.setTextColor(Color.parseColor("#dbdbdb"));
        }
    }

    @Subscriber(tag = "selectedTVChanged")
    private void updateTVState(SelectedDeviceChangedEvent event) {
        if(event.isDeviceOnline()) {
            TVStateIV.setImageResource(R.drawable.tvconnected);
            TVNameTV.setText("在线");
            TVNameTV.setTextColor(Color.parseColor("#ffffff"));
        } else {
            TVStateIV.setImageResource(R.drawable.tvbroke);
            TVNameTV.setText("离线中");
            TVNameTV.setTextColor(Color.parseColor("#dbdbdb"));
        }
    }

    @Subscriber(tag = "selectedDeviceStateChanged")
    private void updateDeviceNetState(TVPCNetStateChangeEvent event) {
        if(event.isPCOnline() && MyApplication.selectedPCVerified) {
            // 判断是否已有数据
            if(MediafileHelper.recentAudios.size() <= 0 &&
                    MediafileHelper.recentVideos.size() <= 0 &&
                    MediafileHelper.getImageSets().size() <= 0 &&
                    MediafileHelper.getAudioSets().size() <= 0) {
                MediafileHelper.loadMediaSets(myHandler);
                MediafileHelper.loadRecentMediaFiles(myHandler);
            }
            PCStateIV.setImageResource(R.drawable.pcconnected);
            PCNameTV.setText("在线");
            PCNameTV.setTextColor(Color.parseColor("#ffffff"));
        } else {
            PCStateIV.setImageResource(R.drawable.pcbroke);
            PCNameTV.setText("离线中");
            PCNameTV.setTextColor(Color.parseColor("#dbdbdb"));
        }
        if(event.isTVOnline() && MyApplication.selectedTVVerified) {
            TVStateIV.setImageResource(R.drawable.tvconnected);
            TVNameTV.setText("在线");
            TVNameTV.setTextColor(Color.parseColor("#ffffff"));
        } else {
            TVStateIV.setImageResource(R.drawable.tvbroke);
            TVNameTV.setText("离线中");
            TVNameTV.setTextColor(Color.parseColor("#dbdbdb"));
        }
    }
    @Subscriber(tag="tvPlayerStateChanged")
    private void updateRemoteControl(TvPlayerChangeEvent event){
            Glide.with(mContext).load(R.drawable.remote_control).into(remoteControlImg);
            remoteControlLayout.setVisibility(View.VISIBLE);
            remoteControlLayout.setOnClickListener(this);
    }


    private void initViews() {
        PCStateIV  = (ImageView) rootView.findViewById(R.id.PCStateIV);
        TVStateIV  = (ImageView) rootView.findViewById(R.id.TVStateIV);
        PCNameTV   = (TextView) rootView.findViewById(R.id.PCNameTV);
        TVNameTV   = (TextView) rootView.findViewById(R.id.TVNameTV);
        videoLibLL = (LinearLayout) rootView.findViewById(R.id.videoLibLL);
        audioLibLL = (LinearLayout) rootView.findViewById(R.id.audioLibLL);
        picLibLL   = (LinearLayout) rootView.findViewById(R.id.picLibLL);
        picsPlayListLL = (LinearLayout) rootView.findViewById(R.id.picsPlayListLL);
        audiosPlayListLL = (LinearLayout) rootView.findViewById(R.id.audiosPlayListLL);
        picsPlayListNumTV = (TextView) rootView.findViewById(R.id.picsPlayListNumTV);
        audiosPlayListNumTV = (TextView) rootView.findViewById(R.id.audiosPlayListNumTV);
        lastVideoThumbnailIV = (ImageView) rootView.findViewById(R.id.lastVideoThumbnailIV);
        lastVideoNameTV = (BreakTextView) rootView.findViewById(R.id.lastVideoNameTV);
        moreVideoRecordsTV = (TextView) rootView.findViewById(R.id.moreVideoRecordsTV);
        lastAudioNameTV = (TextView) rootView.findViewById(R.id.lastAudioNameTV);
        moreAudioRecordsTV = (TextView) rootView.findViewById(R.id.moreAudioRecordsTV);
        lastVideoCastIB = (ImageButton) rootView.findViewById(R.id.lastVideoCastIB);
        lastAudioCastIB = (ImageButton) rootView.findViewById(R.id.lastAudioCastIB);
        audioPicIV = (ImageView) rootView.findViewById(R.id.audioPicIV);
        remoteControlLayout = (LinearLayout) rootView.findViewById(R.id.RemoteControlLayout);
        remoteControlImg = (ImageView) rootView.findViewById(R.id.RemoteControlImg);
        searchET = (EditText) rootView.findViewById(R.id.search_editText);

        helpInfo = (TextView) rootView.findViewById(R.id.helpInfo);


        Glide.with(mContext).load(R.drawable.lastmusic).into(audioPicIV);
        Glide.with(mContext).load(R.drawable.nothing).into(lastVideoThumbnailIV);

        updateDeviceNetState(new TVPCNetStateChangeEvent(MyApplication.isSelectedTVOnline(),
                MyApplication.isSelectedPCOnline()));
        updateRemoteControl(new TvPlayerChangeEvent(ReceiveCommandFromTVPlayer.getPlayerIsRun()));
    }

    private void initEvents() {
        videoLibLL.setOnClickListener(this);
        audioLibLL.setOnClickListener(this);
        picLibLL.setOnClickListener(this);
        picsPlayListLL.setOnClickListener(this);
        audiosPlayListLL.setOnClickListener(this);
        moreAudioRecordsTV.setOnClickListener(this);
        moreVideoRecordsTV.setOnClickListener(this);
        lastVideoCastIB.setOnClickListener(this);
        lastAudioCastIB.setOnClickListener(this);
        searchET.setOnClickListener(this);
        helpInfo.setOnClickListener(this);

//        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if ((keyEvent!=null && keyEvent.getKeyCode()==KeyEvent.KEYCODE_ENTER)){
//                    Log.w("page02Fragment", searchET.getText().toString());
//                    return true;
//                }
//                return false;
//            }
//        });

    }

    private void setLastChosenMedia(String typeName, boolean isOK) {
        if(isOK) {
            MediaItem mediaFile;
            switch (typeName) {
                case OrderConst.videoAction_name:
                    if(MediafileHelper.getRecentVideos().size() > 0) {
                        lastVideoCastIB.setVisibility(View.VISIBLE);
                        mediaFile = MediafileHelper.getRecentVideos().get(0);
                        lastVideoNameTV.setText(mediaFile.getName());
                        Glide.with(MyApplication.getContext())
                                .load(mediaFile.getThumbnailurl()).apply(new RequestOptions().placeholder(R.drawable.videotest).dontAnimate().centerCrop())
                                .into(lastVideoThumbnailIV);
                    } else {
                        lastVideoCastIB.setVisibility(View.GONE);
                        lastVideoNameTV.setText("暂无数据");
                        Glide.with(mContext).load(R.drawable.nothing).into(lastVideoThumbnailIV);
                    }
                    break;
                case OrderConst.audioAction_name:
                    if(MediafileHelper.getRecentAudios().size() > 0) {
                        lastAudioCastIB.setVisibility(View.VISIBLE);
                        mediaFile = MediafileHelper.getRecentAudios().get(0);
                        lastAudioNameTV.setText(mediaFile.getName());
                    } else {
                        lastAudioCastIB.setVisibility(View.GONE);
                        lastAudioNameTV.setText("暂无数据");
                    }
                    break;
            }
        } else {
            switch (typeName) {
                case OrderConst.videoAction_name:
                    lastVideoCastIB.setVisibility(View.GONE);
                    lastVideoNameTV.setText("暂无数据");
                    Glide.with(MyApplication.getContext()).load(R.drawable.nothing).into(lastVideoThumbnailIV);
                    break;
                case OrderConst.audioAction_name:
                    lastAudioCastIB.setVisibility(View.GONE);
                    lastAudioNameTV.setText("暂无数据");
                    break;
            }
        }
    }

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderConst.getPCAudioSets_OK:
                    audiosPlayListNumTV.setText("(" + MediafileHelper.getAudioSets().size() + ")");
                    break;
                case OrderConst.getPCAudioSets_Fail:
                    audiosPlayListNumTV.setText("(0)");
                    break;
                case OrderConst.getPCImageSets_OK:
                    picsPlayListNumTV.setText("(" + MediafileHelper.getImageSets().size() + ")");
                    break;
                case OrderConst.getPCImageSets_Fail:
                    picsPlayListNumTV.setText("(0)");
                    break;
                case OrderConst.getPCRecentAudio_OK:
                    if(MediafileHelper.getRecentAudios().size() > 0)
                        setLastChosenMedia(OrderConst.audioAction_name, true);
                    break;
                case OrderConst.getPCRecentAudio_Fail:
                    setLastChosenMedia(OrderConst.audioAction_name, false);
                    break;
                case OrderConst.getPCRecentVideo_OK:
                    if(MediafileHelper.getRecentVideos().size() > 0)
                        setLastChosenMedia(OrderConst.videoAction_name, true);
                    break;
                case OrderConst.getPCRecentVideo_Fail:
                    setLastChosenMedia(OrderConst.videoAction_name, false);
                    break;
                case OrderConst.playPCMedia_OK:
                    Toasty.success(mContext, "即将在当前电视上打开媒体文件, 请观看电视", Toast.LENGTH_SHORT, true).show();
                    break;
                case OrderConst.playPCMedia_Fail:
                    Toasty.info(mContext, "打开媒体文件失败", Toast.LENGTH_SHORT, true).show();
                    break;
            }
        }
    };
}

