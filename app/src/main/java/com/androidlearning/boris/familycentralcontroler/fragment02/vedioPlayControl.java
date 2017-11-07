package com.androidlearning.boris.familycentralcontroler.fragment02;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.androidlearning.boris.familycentralcontroler.R;
import com.androidlearning.boris.familycentralcontroler.fragment02.view.RemoteControlView;
import com.androidlearning.boris.familycentralcontroler.fragment03.utils.TVAppHelper;
import com.androidlearning.boris.familycentralcontroler.utils_comman.ReceiveCommandFromTVPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static com.androidlearning.boris.familycentralcontroler.R.drawable.vedio_play_control_pause;
import static com.androidlearning.boris.familycentralcontroler.R.drawable.vedio_play_control_play;

public class vedioPlayControl extends AppCompatActivity {
    public static boolean isplaying=true;
    public static SeekBar seekBar;
    public static  TextView player_overlay_time;
    public static TextView player_title;
    public static TextView player_overlay_length;
    public static Button load_Subtitle;
    public static Button hide_Subtitle;


    private boolean isChanging=false;//互斥变量，防止定时器与SeekBar拖动时进度冲突

    private Timer mTimer;
    private TimerTask mTimerTask;

    private Timer mTimerFast;
    private TimerTask mTimerTaskFast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio_play_control);

        RelativeLayout view=(RelativeLayout) findViewById(R.id.control_circle);

        initView();


        seekBar.setMax(500000);//设置进度条.500秒
        seekBar.setProgress(0);

        //----------定时器记录播放进度---------//
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {


                if(isChanging==true||isplaying==false||!ReceiveCommandFromTVPlayer.getPlayerIsRun()) {
                    return;
                }else {
                    SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
                    sd.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                    try {
                        Date date = sd.parse(player_overlay_time.getText().toString());

                        final String updateTime=sd.format(new Date((date.getTime()+1000)>seekBar.getMax()?seekBar.getMax():(date.getTime()+1000)));
                        System.out.println(updateTime);
                        player_overlay_time.post(new Runnable() {
                            @Override
                            public void run() {
                                player_overlay_time.setText(updateTime);
                                seekBar.setProgress((seekBar.getProgress()+1000)>seekBar.getMax()?seekBar.getMax():(seekBar.getProgress()+1000));
                            }
                        });

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }





            }
        };

        mTimer.schedule(mTimerTask, 1000, 1000);

        seekBar.setOnSeekBarChangeListener(new MySeekbar());


        //字幕
        load_Subtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TVAppHelper.vedioPlayControlLoadSubtitle();
            }
        });
        hide_Subtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TVAppHelper.vedioPlayControlHideSubtitle();
            }
        });

        //然后是调用，调用代码就简单的放几句吧，应该看得懂的
        RemoteControlView roundMenuView=new RemoteControlView(this);
        view.addView(roundMenuView);




        //下面的按钮，VolumeDown
        RemoteControlView.RoundMenu roundMenu = new RemoteControlView.RoundMenu();
        roundMenu.selectSolidColor = Color.GRAY;
        roundMenu.strokeColor =Color.GRAY;

        roundMenu.icon=BitmapFactory.decodeResource(getResources(), R.drawable.vedio_play_control_down);

        roundMenu.onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ReceiveCommandFromTVPlayer.getPlayerIsRun()) {
                    TVAppHelper.vedioPlayControlVolumeDown();
                }else {
                    Toast.makeText(getApplicationContext(),"当前无播放视频",Toast.LENGTH_SHORT).show();
                }

            }
        };

        roundMenuView.addRoundMenu(roundMenu);

        //左面的按钮，快退
        roundMenu = new RemoteControlView.RoundMenu();
        roundMenu.selectSolidColor =Color.GRAY;
        roundMenu.strokeColor =Color.GRAY;
        roundMenu.icon=BitmapFactory.decodeResource(getResources(), R.drawable.vedio_play_control_left);
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

                        if(ReceiveCommandFromTVPlayer.getPlayerIsRun()&&isplaying) {
                            mTimerFast = new Timer();
                            mTimerTaskFast = new TimerTask() {
                                @Override
                                public void run() {
                                    try {

                                        TVAppHelper.vedioPlayControlFast();
                                        seekBar.setProgress(seekBar.getProgress()-10000);
                                        updateTimeInThread();
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            };

                            mTimerFast.schedule(mTimerTaskFast, 0, 300);//300ms执行一次快进
                        } else {
                            Toast.makeText(getApplicationContext(),"当前无播放视频或视频暂停",Toast.LENGTH_SHORT).show();
                        }
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
        roundMenu.selectSolidColor =Color.GRAY;
        roundMenu.strokeColor =Color.GRAY;
        roundMenu.icon=BitmapFactory.decodeResource(getResources(), R.drawable.vedio_play_control_up);
        roundMenu.onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ReceiveCommandFromTVPlayer.getPlayerIsRun()) {
                    TVAppHelper.vedioPlayControlVolumeUp();
                }else {
                    Toast.makeText(getApplicationContext(),"当前无播放视频",Toast.LENGTH_SHORT).show();
                }

            }
        };
        roundMenuView.addRoundMenu(roundMenu);

        //右面的按钮，快进
        roundMenu = new RemoteControlView.RoundMenu();
        roundMenu.selectSolidColor =Color.GRAY;
        roundMenu.strokeColor =Color.GRAY;
        roundMenu.icon=BitmapFactory.decodeResource(getResources(), R.drawable.vedio_play_control_left);

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

                        if(ReceiveCommandFromTVPlayer.getPlayerIsRun()&&isplaying) {
                            mTimerFast = new Timer();
                            mTimerTaskFast = new TimerTask() {
                                @Override
                                public void run() {
                                    try {

                                        TVAppHelper.vedioPlayControlFast();
                                        seekBar.setProgress(seekBar.getProgress()+10000);
                                        updateTimeInThread();
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            };

                            mTimerFast.schedule(mTimerTaskFast, 0, 300);//300ms执行一次快进
                        } else {
                            Toast.makeText(getApplicationContext(),"当前无播放视频或视频暂停",Toast.LENGTH_SHORT).show();
                        }
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
                        if(ReceiveCommandFromTVPlayer.getPlayerIsRun()){
                            if(isplaying){
                                isplaying=false;
                                RemoteControlView.coreBitmap=BitmapFactory.decodeResource(getResources(), vedio_play_control_play);
                                TVAppHelper.vedioPlayControlPause();
                            }else {
                                isplaying=true;
                                RemoteControlView.coreBitmap=BitmapFactory.decodeResource(getResources(), vedio_play_control_pause);
                                TVAppHelper.vedioPlayControlPlay();
                            }
              //              TVAppHelper.vedioPlayControlPlayPause();
                        }else {
                            Toast.makeText(getApplicationContext(),"当前无播放视频",Toast.LENGTH_SHORT).show();
                        }


                    }
                });

        roundMenuView.setBackMenu(Color.WHITE,
                Color.GRAY, Color.GRAY
                , 1, 0.43, BitmapFactory.decodeResource(getResources(), R.drawable.vedio_play_control_back), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTimer.cancel();
                        vedioPlayControl.this.finish();

                    }
                });
        roundMenuView.setStopMenu(Color.WHITE,
                Color.GRAY, Color.GRAY
                , 1, 0.43, BitmapFactory.decodeResource(getResources(), R.drawable.vedio_play_control_stop), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TVAppHelper.vedioPlayControlExit();

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
        load_Subtitle=(Button) findViewById(R.id.load_subtitle);
        hide_Subtitle=(Button)findViewById(R.id.hide_subtitle);


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
                player_overlay_time.post(new Runnable() {
                    @Override
                    public void run() {
                        player_overlay_time.setText("00:00:00");
                        player_overlay_length.setText("00:00:00");
                        player_title.setText("当前无播放视频");

                    }
                });
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
        player_overlay_time.post(new Runnable() {
            @Override
            public void run() {
                player_overlay_time.setText(updateTime);
            }
        });

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
        player_overlay_time.post(new Runnable() {
            @Override
            public void run() {
                player_overlay_time.setText("00:00:00");
                seekBar.setProgress(0);
                player_overlay_length.setText("00:00:00");
                player_title.setText("当前无播放视频");
            }
        });

  //      RemoteControlView.coreBitmap=BitmapFactory.decodeResource(Resources.getSystem(), vedio_play_control_play);

    }

    public static  void checkPlayInfo(final String currentTime, final String length, final String title){
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



        SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
        sd.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        try {
            Date date1 = sd.parse(length);
            if(length.contains("23:59:59"))
            {
                seekBar.setMax(5000000);//随机设置的值，避免异常退出
            }else {
                seekBar.setMax((int)date1.getTime());
            }



        }
        catch (Exception e){
            e.printStackTrace();
        }



        try {
            Date date = sd.parse(currentTime);

            seekBar.setProgress((int)date.getTime());
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
            mTimer.cancel();
            vedioPlayControl.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
