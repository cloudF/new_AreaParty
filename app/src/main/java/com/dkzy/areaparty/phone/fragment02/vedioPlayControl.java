package com.dkzy.areaparty.phone.fragment02;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.androideventbusutils.events.TvPlayerChangeEvent;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_page;
import com.dkzy.areaparty.phone.fragment02.subtitle.ActionDialog_subTitle;
import com.dkzy.areaparty.phone.fragment02.subtitle.SubTitleUtil;
import com.dkzy.areaparty.phone.fragment02.view.RemoteControlView;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;
import com.dkzy.areaparty.phone.utils_comman.ReceiveCommandFromTVPlayer;

import org.simple.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

import static com.dkzy.areaparty.phone.R.drawable.vedio_play_control_pause;
import static com.dkzy.areaparty.phone.R.drawable.vedio_play_control_play;

public class vedioPlayControl extends AppCompatActivity {
    public static boolean isplaying=true;
    public static SeekBar seekBar;
    public static  TextView player_overlay_time;
    public static TextView player_title;
    public static TextView player_overlay_length;
    public static Button Subtitle;
    public static Button subtitle_before;
    public static Button subtitle_delay;

    SubTitleUtil subTitleUtil;


    private boolean isChanging=false;//互斥变量，防止定时器与SeekBar拖动时进度冲突

    private Timer mTimer;
    private TimerTask mTimerTask;

    private Timer mTimerFast;
    private TimerTask mTimerTaskFast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio_play_control);

        if (!ReceiveCommandFromTVPlayer.getPlayerIsRun()){
            new ReceiveCommandFromTVPlayer(true).start();
            EventBus.getDefault().post(new TvPlayerChangeEvent(true), "tvPlayerStateChanged");
        }

        FrameLayout view=(FrameLayout) findViewById(R.id.control_circle);

        initView();


        seekBar.setMax(500000);//设置进度条.500秒
        seekBar.setProgress(0);

//        //----------定时器记录播放进度---------//
//        mTimer = new Timer();
//        mTimerTask = new TimerTask() {
//            @Override
//            public void run() {
//
//
//                if(isChanging==true||isplaying==false||!ReceiveCommandFromTVPlayer.getPlayerIsRun()) {
//                    return;
//                }else {
//                    SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
//                    sd.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
//                    try {
//                        Date date = sd.parse(player_overlay_time.getText().toString());
//
//                        final String updateTime=sd.format(new Date((date.getTime()+1000)>seekBar.getMax()?seekBar.getMax():(date.getTime()+1000)));
//                        System.out.println(updateTime);
//                        player_overlay_time.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                player_overlay_time.setText(updateTime);
//                                seekBar.setProgress((seekBar.getProgress()+1000)>seekBar.getMax()?seekBar.getMax():(seekBar.getProgress()+1000));
//                            }
//                        });
//
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//
//
//
//
//
//            }
//        };
//
//        mTimer.schedule(mTimerTask, 1000, 1000);

        seekBar.setOnSeekBarChangeListener(new MySeekbar());


        //字幕
        Subtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Subtitle.getText().equals("加载字幕")){
                    if (player_title.getText().toString().equals("当前无播放视频")){
                        Toast.makeText(getApplicationContext(),"当前无播放视频",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    subTitleUtil = new SubTitleUtil();
                    subTitleUtil.setListener(new SubTitleUtil.OnLoadListener() {
                        @Override
                        public void onStartLoad() {

                        }

                        @Override
                        public void onFinishLoad() {
                            Log.w("VideoPlayerActivity",SubTitleUtil.subTitleList.size()+"pppppppp");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (SubTitleUtil.subTitleList.size() > 0){
                                        showHelpInfoDialog();
                                    }else {
                                        Toasty.error(vedioPlayControl.this,"未找到字幕").show();
                                    }

                                }
                            });
                        }
                    });
                    subTitleUtil.getInternetSub(player_title.getText().toString());

                }else {
                    TVAppHelper.vedioPlayControlHideSubtitle();
                    if(ReceiveCommandFromTVPlayer.playerType.equalsIgnoreCase("VIDEO")){
                        Subtitle.setText("加载字幕");
                    }else {
                        Subtitle.setText("加载歌词");
                    }
                }

            }
        });
        subtitle_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TVAppHelper.vedioPlayControlSubtitleBefore();
            }
        });

        subtitle_delay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TVAppHelper.vedioPlayControlSubtitleDelay();
            }
        });

        //然后是调用，调用代码就简单的放几句吧，应该看得懂的
        RemoteControlView roundMenuView=new RemoteControlView(this);
        view.addView(roundMenuView);




        //下面的按钮，VolumeDown
        RemoteControlView.RoundMenu roundMenu = new RemoteControlView.RoundMenu();
        roundMenu.selectSolidColor = Color.rgb(205,205,205);
        roundMenu.strokeColor = Color.rgb(219,219,219);

        roundMenu.icon=BitmapFactory.decodeResource(getResources(), R.drawable.vedio_play_control_down);

        roundMenu.onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    TVAppHelper.vedioPlayControlVolumeDown();


            }
        };

        roundMenuView.addRoundMenu(roundMenu);

        //左面的按钮，快退
        roundMenu = new RemoteControlView.RoundMenu();
        roundMenu.selectSolidColor = Color.rgb(205,205,205);
        roundMenu.strokeColor = Color.rgb(219,219,219);
        if(ReceiveCommandFromTVPlayer.playerType.equalsIgnoreCase("VIDEO")){
            roundMenu.icon=BitmapFactory.decodeResource(getResources(), R.drawable.vedio_play_control_left);
        }else {
            roundMenu.icon=BitmapFactory.decodeResource(getResources(), R.drawable.vedio_play_control_lastsong);
        }
        //TODO:by ervincm.点击事件改为touch事件
//        roundMenu.onClickListener=new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(ReceiveCommandFromTVPlayer.getPlayerIsRun()) {
//                    TVAppHelper.vedioPlayControlRewind();
//                    seekBar.setProgress(seekBar.getProgress()-10000);
//                    updateTime();
//                }else {
//                    Toast.makeText(getApplicationContext(),"当前无播放视频",Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        };
        roundMenu.onTouchListener=new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //----------定时器控制快进---------//


                            mTimerFast = new Timer();
                            mTimerTaskFast = new TimerTask() {
                                @Override
                                public void run() {
                                    try {

                                        TVAppHelper.vedioPlayControlRewind();
                                        updateTimeInThread();
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            };

                            mTimerFast.schedule(mTimerTaskFast, 0, 300);//300ms执行一次快进

                        break;
                    case MotionEvent.ACTION_UP:

                        mTimerFast.cancel();
                        break;
                }
                return true;//false向外传播
            }


        };

        roundMenuView.addRoundMenu(roundMenu);

        //上面的按钮，VloumeUp

        roundMenu = new RemoteControlView.RoundMenu();
        roundMenu.selectSolidColor = Color.rgb(205,205,205);
        roundMenu.strokeColor = Color.rgb(219,219,219);
        roundMenu.icon=BitmapFactory.decodeResource(getResources(), R.drawable.vedio_play_control_up);
        roundMenu.onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    TVAppHelper.vedioPlayControlVolumeUp();


            }
        };
        roundMenuView.addRoundMenu(roundMenu);

        //右面的按钮，快进
        roundMenu = new RemoteControlView.RoundMenu();
        roundMenu.selectSolidColor = Color.rgb(205,205,205);
        roundMenu.strokeColor = Color.rgb(219,219,219);
        if(ReceiveCommandFromTVPlayer.playerType.equalsIgnoreCase("VIDEO")){
            roundMenu.icon=BitmapFactory.decodeResource(getResources(), R.drawable.vedio_play_control_right);
        }else {
            roundMenu.icon=BitmapFactory.decodeResource(getResources(), R.drawable.vedio_play_control_nextsong);
        }


        //TODO:by ervincm.点击事件改为touch事件
//        roundMenu.onClickListener=new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(ReceiveCommandFromTVPlayer.getPlayerIsRun()) {
//                    TVAppHelper.vedioPlayControlFast();
//                    seekBar.setProgress(seekBar.getProgress()+10000);
//                    updateTime();
//                }else {
//                    Toast.makeText(getApplicationContext(),"当前无播放视频",Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        };
        roundMenu.onTouchListener=new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                       //----------定时器控制快进---------//


                            mTimerFast = new Timer();
                            mTimerTaskFast = new TimerTask() {
                                @Override
                                public void run() {
                                    try {

                                        TVAppHelper.vedioPlayControlFast();
                                        updateTimeInThread();
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            };

                            mTimerFast.schedule(mTimerTaskFast, 0, 300);//300ms执行一次快进

                        break;
                    case MotionEvent.ACTION_UP:

                        mTimerFast.cancel();
                        break;
                }
                return true;//false向外传播
            }


        };

        roundMenuView.addRoundMenu(roundMenu);




        roundMenuView.setCoreMenu(Color.WHITE,
                Color.GRAY, Color.GRAY
                , 1, 0.43, BitmapFactory.decodeResource(getResources(), vedio_play_control_pause), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            if(isplaying){
                                isplaying=false;
                                RemoteControlView.coreBitmap=BitmapFactory.decodeResource(getResources(), vedio_play_control_play);
                                TVAppHelper.vedioPlayControlPlayPause();
                            }else {
                                isplaying=true;
                                RemoteControlView.coreBitmap=BitmapFactory.decodeResource(getResources(), vedio_play_control_pause);
                                TVAppHelper.vedioPlayControlPlayPause();
                            }
              //              TVAppHelper.vedioPlayControlPlayPause();



                    }
                });

        roundMenuView.setBackMenu(Color.WHITE,
                Color.GRAY, Color.GRAY
                , 1, 0.43, BitmapFactory.decodeResource(getResources(), R.drawable.vedio_play_control_back), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mTimer!=null)
                        mTimer.cancel();
                        vedioPlayControl.this.finish();

                    }
                });
        roundMenuView.setStopMenu(Color.rgb(219,219,219),
                Color.rgb(205,205,205), Color.rgb(219,219,219)
                , 2, 0.4, BitmapFactory.decodeResource(getResources(), R.drawable.vedio_play_control_stop), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TVAppHelper.vedioPlayControlExit();
                        if(mTimer!=null)
                        mTimer.cancel();

                        vedioPlayControl.this.finish();
                    }
                });




    }

    private void  initView(){
        seekBar=(SeekBar)findViewById(R.id.player_overlay_seekbar);
        player_overlay_length=(TextView)findViewById(R.id.player_overlay_length);
        player_overlay_time=(TextView)findViewById(R.id.player_overlay_time);
        player_title=(TextView)findViewById(R.id.player_title);
        Subtitle=(Button) findViewById(R.id.subtitle);
        subtitle_before=(Button)findViewById(R.id.subtitle_before);
        subtitle_delay=(Button)findViewById(R.id.subtitle_delay);

        if(ReceiveCommandFromTVPlayer.playerType.equalsIgnoreCase("audio")){
            Subtitle.setText("加载歌词");
        }
    }

    class MySeekbar implements OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            isChanging=true;
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            if(ReceiveCommandFromTVPlayer.playerIsRun){
                updateTime();
                TVAppHelper.playAppointPosition2TV();
            }else {
                if (player_overlay_time!=null){
                    player_overlay_time.post(new Runnable() {
                        @Override
                        public void run() {
                            player_overlay_time.setText("00:00:00");
                            player_overlay_length.setText("00:00:00");
                            player_title.setText("当前无播放视频");

                        }
                    });
                }
                Toast.makeText(getApplicationContext(),"当前无播放视频",Toast.LENGTH_SHORT).show();
            }


            isChanging=false;
        }

    }




    private  void updateTime(){
        seekBar.setProgress(seekBar.getProgress());

        SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
        sd.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String updateTime=sd.format(new Date(seekBar.getProgress()));
        player_overlay_time.setText(updateTime);
    }
    private  void updateTimeInThread(){
        seekBar.setProgress(seekBar.getProgress());

        SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
        sd.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        final String updateTime=sd.format(new Date(seekBar.getProgress()));
        if (player_overlay_time!=null){
            player_overlay_time.post(new Runnable() {
                @Override
                public void run() {
                    player_overlay_time.setText(updateTime);
                }
            });
        }


    }

    public static void play(){
        isplaying=true;

     //   RemoteControlView.coreBitmap=BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.vedio_play_control_pause);

    }

    public static void pause(){
        isplaying=false;
     //   RemoteControlView.coreBitmap=BitmapFactory.decodeResource(Resources.getSystem(), vedio_play_control_play);
    }

    public static  void exit(){
        if (player_overlay_time!=null){
            player_overlay_time.post(new Runnable() {
                @Override
                public void run() {
                    player_overlay_time.setText("00:00:00");
                    seekBar.setProgress(0);
                    player_overlay_length.setText("00:00:00");
                    player_title.setText("当前无播放视频");
                }
            });
        }


  //      RemoteControlView.coreBitmap=BitmapFactory.decodeResource(Resources.getSystem(), vedio_play_control_play);

    }

    public static  void checkPlayInfo(final String currentTime, final String length, final String title){
        if (player_overlay_time!=null){
            player_overlay_time.post(new Runnable() {
                @Override
                public void run() {
                    if(currentTime.contains("23:59:59")){
                        player_overlay_time.setText("00:00:00");
                    }else {
                        player_overlay_time.setText(currentTime);
                    }

                    player_overlay_length.setText(length);
                    player_title.setText(title);
                }
            });
        }

        SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
        sd.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        try {
            Date date1 = sd.parse(length);
            if (seekBar!=null){
                if(length.contains("23:59:59"))
                {
                    seekBar.setMax(5000000);//随机设置的值，避免异常退出
                }else {
                    seekBar.setMax((int)date1.getTime());
                }

            }



        }
        catch (Exception e){
            e.printStackTrace();
        }



        try {
            Date date = sd.parse(currentTime);
            if (seekBar!=null){
                seekBar.setProgress((int)date.getTime());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void playAppointPosition(String appointPosition){
        player_overlay_time.setText(appointPosition);
        SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
        sd.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        try {
            Date date = sd.parse(appointPosition);

            seekBar.setProgress((int)date.getTime());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //重写返回按钮
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //TODO something
            if(mTimer!=null)
            mTimer.cancel();
            vedioPlayControl.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public  void showHelpInfoDialog(){
        final ActionDialog_subTitle dialog = new ActionDialog_subTitle(this, SubTitleUtil.subTitleList);
        dialog.setCancelable(true);
        dialog.show();
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dialog.isSubmit()){
                    subTitleUtil.setListener1(new SubTitleUtil.OnLoadListener() {
                        @Override
                        public void onStartLoad() {

                        }

                        @Override
                        public void onFinishLoad() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (SubTitleUtil.subTitleListDetail.size() < 1){
                                        Toasty.error(vedioPlayControl.this,"获取字幕文件失败").show();
                                        dialog.dismiss();
                                    }else if (SubTitleUtil.subTitleListDetail.size() == 1){
                                        sendSubTitleUrl(SubTitleUtil.subTitleListDetail.get(0).getId(),SubTitleUtil.subTitleListDetail.get(0).getName());
                                        dialog.dismiss();
                                    }else {
                                        dialog.setTitleText("请选择字幕文件");
                                        dialog.setData(SubTitleUtil.subTitleListDetail);
                                        dialog.setSubmit(true);
                                    }
                                }
                            });
                        }
                    });
                    subTitleUtil.getSubTitleUrl(dialog.getSelected());
                }
                else {
                    //Toast.makeText(vedioPlayControl.this, ""+dialog.getSelected(), Toast.LENGTH_SHORT).show();
                    sendSubTitleUrl(dialog.getSelected(),dialog.getSelectedName());
                    dialog.dismiss();
                }

            }
        });
    }
    private void sendSubTitleUrl(String url , String f){
        Log.w("videoPlayerActivity2",url);
        TVAppHelper.vedioPlayControlLoadSubtitle(url, f );
        if(ReceiveCommandFromTVPlayer.playerType.equalsIgnoreCase("VIDEO")){
            Subtitle.setText("隐藏字幕");
        }
    }
}
