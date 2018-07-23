package com.example.jan.praca;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class Wideo extends AppCompatActivity {

    String videoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wideo);
        getWindow().clearFlags(WindowManager
                .LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        videoPath = intent.getStringExtra("URL");
        play();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            Intent intent = new Intent(Wideo.this,Menu.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    public void play()
    {
        VideoView videoView = (VideoView) findViewById(R.id.myvideoview);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoPath(videoPath);
        videoView.requestFocus();
        videoView.start();
    }
}
