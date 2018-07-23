package com.example.jan.praca;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Menu extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.loadLocale(getApplicationContext());
        setContentView(R.layout.activity_menu);
        SharedPreferences sharedPreferences = getSharedPreferences("SPLASH", MODE_PRIVATE);
        boolean splashed = sharedPreferences.getBoolean("splash", false);
        if (!splashed) {
            Intent i = new Intent(getApplicationContext(), Splash.class);
            startActivity(i);
        }
        sharedPreferences = getSharedPreferences("Alarm", 0);
        int hour = sharedPreferences.getInt("hour", 12);
        int minute = sharedPreferences.getInt("minute", 20);
        Log.i("TAG", hour + ":" + minute);

    }
    public void onCardClick(View view)
    {

        Intent intent;
        switch (view.getId()) {
            case R.id.CameraView:
                intent = new Intent(this,Camera.class);
                startActivity(intent);
                break;
            case R.id.GalleryView:
                intent = new Intent(this,GalleryMainActivity.class);
                startActivity(intent);
                break;
            case R.id.SettingsView:
                intent = new Intent(this,Settings.class);
                startActivity(intent);
                break;
            case R.id.CalendarView:
                intent = new Intent(this,Callendar.class);
                startActivity(intent);
                break;
            case R.id.FFMPEGView:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String[] items = {"Video","GoogleDrive"};
                builder.setTitle("Export");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch(i)
                        {
                            case 0: Intent FFmpegIntent = new Intent(getApplicationContext(),FFMPEG.class);
                            startActivity(FFmpegIntent);
                            break;
                            case 1: Intent SendToDriveIntent = new Intent(getApplicationContext(),GDriveUpload.class);
                            startActivity(SendToDriveIntent);
                            break;
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;

            case R.id.StatsView:
                intent = new Intent(this,Stats.class);
                startActivity(intent);
                break;

        }
    }

}

