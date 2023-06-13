package com.example.tunetastic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    Button btnplay,btnnext,btnprev,btnff,btnfr;
   static MediaPlayer mediaPlayer;
    TextView txtsname,txtstart,txtsstop;
    SeekBar seekmusic;
    String sname;
    public  static final String EXTRA_NAME="song_name";
    int position;
    ArrayList<File> mysongs;
    ImageView imageView;
    Thread updateseekbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnprev=findViewById(R.id.btnpre);
        btnnext=findViewById(R.id.btnnext);
        btnplay=findViewById(R.id.playbutton);
        btnff=findViewById(R.id.btnforward);
        btnfr=findViewById(R.id.btnskipprev);
        txtstart=findViewById(R.id.txtstart);
        txtsstop=findViewById(R.id.txtstop);
        seekmusic=findViewById(R.id.seekbar);
        txtsname=findViewById(R.id.txtsn);
        imageView=findViewById(R.id.imageview);
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i=getIntent();
      Bundle bundle=i.getExtras();
      mysongs=(ArrayList) bundle.getParcelableArrayList("songs");
      String songname=i.getStringExtra("songname");
     position=bundle.getInt("position",0);
      txtsname.setSelected(true);
        Uri uri=Uri.parse(mysongs.get(position).toString());
        sname=mysongs.get(position).getName();
        txtsname.setText(sname);
        mediaPlayer=MediaPlayer.create(getApplicationContext(), uri);

        mediaPlayer.start();

        updateseekbar=new Thread()
        {
            @Override
            public void run() {
                int totalduration=mediaPlayer.getDuration();
                int currentposition=0;
                while(currentposition<totalduration)
                {
                    try {
                        sleep(500);
                        currentposition= mediaPlayer.getCurrentPosition();
                        int finalCurrentposition = currentposition;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                seekmusic.setProgress(finalCurrentposition);
                            }
                        });

                    }
                    catch (InterruptedException | IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        seekmusic.setMax(mediaPlayer.getDuration());
        updateseekbar.start();
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.orchid), PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.orchid),PorterDuff.Mode.SRC_IN);
       seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {
               mediaPlayer.seekTo(seekBar.getProgress());

           }
       });
        String endtime=createtime(mediaPlayer.getDuration());
        txtsstop.setText(endtime);
        final Handler handler= new Handler();
        final int delay=1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currenttime=createtime(mediaPlayer.getCurrentPosition());
                txtstart.setText(currenttime);
                handler.postDelayed(this,delay);
            }
        }, delay);


        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    btnplay.setBackgroundResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else {
                    btnplay.setBackgroundResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position+1)%mysongs.size());
                Uri u=Uri.parse(mysongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mysongs.get(position).getName();
                txtsname.setText(sname);
                mediaPlayer.start();
                btnplay.setBackgroundResource(R.drawable.pause);
                startanimation(imageView);
            }
        });
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position-1)<0)?(mysongs.size()-1):(position-1);
                Uri u=Uri.parse(mysongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mysongs.get(position).getName();
                txtsname.setText(sname);
                mediaPlayer.start();
                btnplay.setBackgroundResource(R.drawable.pause);
                startanimation(imageView);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnnext.performClick();
            }
        });
        btnff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });
        btnfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });
    }
    public void startanimation(View view)
    {
        ObjectAnimator animator=ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet= new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public  String createtime(int duration)
    {
        String time =" ";
        int minute=duration/1000/60;
        int sec=duration/1000%60;
        time+=minute+":";
        if(sec<10)
        {
            time+="0";
        }
        time+=sec;
        return time;

    }
}