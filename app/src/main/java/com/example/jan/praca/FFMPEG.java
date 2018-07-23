package com.example.jan.praca;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FFMPEG extends Activity{

    FFmpeg ffmpeg;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
        ffmpeg = FFmpeg.getInstance(getApplicationContext());
        try {
            ffmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.ffmpeg_failure),Toast.LENGTH_LONG).show();
                }
                @Override
                public void onSuccess() {}
                @Override
                public void onStart() {}
                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {
                      Toast.makeText(getApplicationContext(),"ERROR: " + e.getMessage(),Toast.LENGTH_LONG).show();
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd-hh_mm");
        String videoName = "VIDEO-" + dateFormat.format(cal.getTime());
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.GENERAL_PREFERENCES,MODE_PRIVATE);
        String outputFormat = sharedPreferences.getString("FORMAT",".mp4");
        String outputVideo = "/storage/emulated/0/Pictures/"+videoName+"."+outputFormat;
        String musicPath = Utils.ASSETS_DIR + "/" +sharedPreferences.getString("MUSIC","");
        boolean playMusic = !musicPath.equals(Utils.ASSETS_DIR + "/");
        boolean musicEnable = sharedPreferences.getBoolean("MusicEnable",false);

        String inputPath = Utils.DIR_PATH + "/zdj%05d.webp";
        ArrayList<String> commands = new ArrayList<>();
        commands.add("-framerate");
        commands.add("2");   // input framerate
        commands.add("-y");
        commands.add("-i"); // import
        commands.add(inputPath);
      if(playMusic && musicEnable){
          commands.add("-i");  // import
          commands.add(musicPath);
      }
        commands.add("-c:v"); // video codec
        commands.add("libx264");
        commands.add("-preset");
        commands.add("veryfast");
        commands.add("-c:a"); // audio codec
        commands.add("aac");
        commands.add("-pix_fmt"); // pixel format
        commands.add("yuv420p");
        commands.add("-crf");  // quality
        commands.add("25");
        commands.add("-r"); // output fps
        commands.add("20");
        commands.add("-shortest"); // finish when the shortest input ends
        commands.add(outputVideo);
        String[] arr = new String[commands.size()];
        commands.toArray(arr);

        try {
            ffmpeg.execute(arr, new ExecuteBinaryResponseHandler() {
                boolean enable = false;
                @Override
                public void onStart() {
                    progressDialog.setMessage(getString(R.string.ffmpeg_process));
                    progressDialog.show();
                }
                @Override
                public void onProgress(String message) {
                    progressDialog.setMessage(getResources().getString(R.string.ffmpeg_wait));
                    Log.i("TAG","PROGRESS " + message );
                }
                @Override
                public void onFailure(String message) {
                    Toast.makeText(getApplicationContext(),getString(R.string.ffmpeg_failure)
                            ,Toast.LENGTH_LONG).show();
                    finish();
                }
                @Override
                public void onSuccess(String message) {
                    Log.i("TAG",message);
                    enable = true;
                }
                @Override
                public void onFinish() {
                    progressDialog.dismiss();
                    if(enable == true) {
                        Intent intent = new Intent(getApplicationContext(), Wideo.class);
                        intent.putExtra("URL",outputVideo);
                        startActivity(intent);
                    }
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            Toast.makeText(getApplicationContext(),getString(R.string.ffmpeg_failure)
                    ,Toast.LENGTH_LONG).show();
        }
    }

}
