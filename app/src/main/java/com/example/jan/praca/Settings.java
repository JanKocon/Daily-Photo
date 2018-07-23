package com.example.jan.praca;

import android.Manifest;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static com.example.jan.praca.Camera.REQUEST_PERMISSION;

public class Settings extends AppCompatActivity implements DialogInterface.OnDismissListener{


    //////// SWITCH TO ANDROID PREFERENCE API!!!!!!111111oneone

    SwitchCompat notification,sound,vibra,music;
    TextView showTimeTextView,changeTimeTextView,showAudio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.loadLocale(getApplicationContext());
        setContentView(R.layout.activity_tst);
        init();

        SharedPreferences.Editor editor = getSharedPreferences("Alarm",0).edit();
        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                sound.setEnabled(isChecked);
                vibra.setEnabled(isChecked);
                changeTimeTextView.setEnabled(isChecked);

                if(isChecked)
                    changeTimeTextView.setTextColor(ContextCompat.getColor(getApplicationContext(),android.R.color.black));
                else
                    changeTimeTextView.setTextColor(ContextCompat.getColor(getApplicationContext(),android.R.color.darker_gray));

                    editor.putBoolean("enable",isChecked);
                    editor.apply();
            }
        });
        sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                editor.putBoolean("soundAlarm",isChecked);
                editor.apply();
            }
        });
        vibra.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                editor.putBoolean("withVibrate",isChecked);
                editor.apply();
            }
        });
        music.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor ed = getSharedPreferences(Utils.GENERAL_PREFERENCES,MODE_PRIVATE).edit();
                if(isChecked){
                    showAudio.setTextColor(ContextCompat.getColor(getApplicationContext(),android.R.color.black));
                }
                else
                    showAudio.setTextColor(ContextCompat.getColor(getApplicationContext(),android.R.color.darker_gray));
                showAudio.setEnabled(isChecked);
                ed.putBoolean("MusicEnable",isChecked);
                ed.commit();
            }
        });
   }
   void onClickedLogOut(View view){
        Intent intent = new Intent(getApplicationContext(),SingOut.class);
        startService(intent);
   }
   void onClickedSetTime(View view)
   {
       DialogFragment df = new TimeePickerFragment();
       df.show(getFragmentManager(),"TAG");
   }
   void onClickedQuality(View view)
   {
       String[] array = {"60","65","70","75","80","85","90"};
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
       builder.setTitle(getResources().getString(R.string.quality_choose));
       SharedPreferences.Editor editor = getSharedPreferences(Utils.GENERAL_PREFERENCES,MODE_PRIVATE).edit();
       builder.setItems(array, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i){
               editor.putInt("quality",Integer.parseInt(array[i]));
               editor.apply();
           }
       });

       AlertDialog alertDialog = builder.create();
       alertDialog.show();
   }
   void init()
   {
       notification = (SwitchCompat) findViewById(R.id.dailyReminder_Switch);
       sound = (SwitchCompat) findViewById(R.id.soundReminder_Switch);
       vibra = (SwitchCompat) findViewById(R.id.vibraReminder_Switch);
       music = (SwitchCompat) findViewById(R.id.withSoundVideo_Switch);
       showTimeTextView = (TextView) findViewById(R.id.showTime_TextView);
       changeTimeTextView = (TextView) findViewById(R.id.setTimeTextView);
       showAudio = (TextView) findViewById(R.id.setSound_TextView);
       SharedPreferences sharedPreferences = getSharedPreferences("Alarm",MODE_PRIVATE);
       sound.setChecked(sharedPreferences.getBoolean("soundAlarm",false));
       vibra.setChecked(sharedPreferences.getBoolean("withVibrate",false));
       boolean isEnabled = sharedPreferences.getBoolean("enable",false);
       notification.setChecked(isEnabled);
       if(isEnabled == false)
       {
            changeTimeTextView.setTextColor(ContextCompat.getColor(this,android.R.color.darker_gray));
            changeTimeTextView.setEnabled(false);
            showTimeTextView.setEnabled(false);
            sound.setEnabled(false);
            vibra.setEnabled(false);
       }
       else{
           changeTimeTextView.setTextColor(ContextCompat.getColor(getApplicationContext(),android.R.color.black));
       }
       SharedPreferences audio = getSharedPreferences(Utils.GENERAL_PREFERENCES,MODE_PRIVATE);
       if(audio.getBoolean("MusicEnable",false) == false){
           showAudio.setTextColor(ContextCompat.getColor(this,android.R.color.darker_gray));
           showAudio.setEnabled(false);
       }
       else{
           music.setChecked(true);
           showAudio.setTextColor(ContextCompat.getColor(getApplicationContext(),android.R.color.black));
       }
       int hour = sharedPreferences.getInt("hour",-1);
       int minute = sharedPreferences.getInt("minute",-1);
       if(hour == -1 || minute == -1) showTimeTextView.setText("");
       else{
           if(minute >= 10)
           showTimeTextView.setText(hour + ":" + minute);
           else showTimeTextView.setText(hour + ":0" + minute);
       }


   }
   void onClickedChangeVideoOutputFormat(View view){
       AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
       SharedPreferences.Editor sharedPreferences = getSharedPreferences(Utils.GENERAL_PREFERENCES,MODE_PRIVATE).edit();
       String[] format = {"MP4","3GP"};
       alertDialog.setTitle("Format");
       alertDialog.setItems(format, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               sharedPreferences.putString("FORMAT",format[i]);
               sharedPreferences.commit();
           }
       });
       AlertDialog dialog = alertDialog.create();
       dialog.show();
   }

    void extractAssets(String filename) {
        File file = new File(Utils.ASSETS_DIR + "/" + filename);
        if (!file.exists()) {
            InputStream inputStream;
            FileOutputStream outputStream;
            try {
                inputStream = getAssets().open(filename);
                outputStream = new FileOutputStream(file);
                byte[] array = new byte[1024];
                int len;
                while ((len = inputStream.read(array)) > 0) {
                    outputStream.write(array, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void onClickedAudioChange(View view)
   {


       if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
           Toast.makeText(getApplicationContext(),"Muzyka: https://www.bensound.com/",Toast.LENGTH_LONG).show();
           try {
               String[] assetsName = getAssets().list("");
               for (String filename : assetsName) extractAssets(filename);
           } catch (IOException e) {
               e.printStackTrace();
           }
           SharedPreferences.Editor sharedPreferences = getSharedPreferences(Utils.GENERAL_PREFERENCES,MODE_PRIVATE).edit();
           AlertDialog.Builder alert = new AlertDialog.Builder(this);
           alert.setTitle("Audio");
           String[] items = {"ukulele.mp3","moose.mp3"};
           alert.setItems(items, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   sharedPreferences.putString("MUSIC",items[i]);
                   sharedPreferences.commit();
               }
           });
           AlertDialog alertDialog = alert.create();
           alertDialog.show();
       }
       else requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION);
   }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    String[] assetsName = getAssets().list("");
                    for (String filename : assetsName) extractAssets(filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SharedPreferences.Editor sharedPreferences = getSharedPreferences(Utils.GENERAL_PREFERENCES,MODE_PRIVATE).edit();
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Audio");
                String[] items = {"ukulele.mp3","moose.mp3"};
                alert.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sharedPreferences.putString("MUSIC",items[i]);
                        sharedPreferences.commit();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Nie uzyskano uprawnien",Toast.LENGTH_LONG).show();

            }
        }
   }

   void onClickedChangeLanguage(View view)
   {
       String[] lang = {"PL","EN"};
       AlertDialog.Builder alertDialog = new AlertDialog.Builder(Settings.this);
       alertDialog.setTitle(getResources().getString(R.string.changeLang));
       alertDialog.setItems(lang, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               switch(i)
               {
                   case 0: setLocale("pl");
                       clear();
                       break;
                   case 1: setLocale("en");
                       clear();
                       break;
               }
           }
       });
       AlertDialog alert = alertDialog.create();
       alert.show();
   }
   void onChangeFrameRate(View view)
   {
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
       builder.setTitle(getResources().getString(R.string.fps));
       String[] items = {"1","2","3","4","5"};
       builder.setItems(items, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {

           }
       });
       AlertDialog alertDialog = builder.create();
       alertDialog.show();
   }
    private void setLocale(String lang)
    {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("LANG",MODE_PRIVATE).edit();
        editor.putString("MyLang",lang);
        editor.apply();
    }
    private void clear()
    {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        SharedPreferences sharedPreferences = getSharedPreferences("Alarm",MODE_PRIVATE);
        int hour = sharedPreferences.getInt("hour",-1);
        int minute = sharedPreferences.getInt("minute",-1);
        showTimeTextView.setText(hour + ":" + minute);
    }

}
