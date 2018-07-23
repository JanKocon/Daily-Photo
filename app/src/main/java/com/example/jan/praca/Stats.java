package com.example.jan.praca;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class Stats extends AppCompatActivity {

    TextView appSince,daysOfUse,filesCount,photosPerDay;
    ArrayList<Date> lista;
    final int READ_STORAGE = 321;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.loadLocale(getApplicationContext());
        setContentView(R.layout.activity_stats);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initTextViews();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE);
        }


    }
    public void initTextViews()
    {
        appSince = (TextView) findViewById(R.id.appsSince);
        daysOfUse = (TextView) findViewById(R.id.daysOfUse);
        filesCount = (TextView) findViewById(R.id.photosCount);
        photosPerDay = (TextView) findViewById(R.id.photosPerDay);
        SharedPreferences sharedPreferences = getSharedPreferences("SPLASH",MODE_PRIVATE);
        long diff = Calendar.getInstance().getTimeInMillis() - sharedPreferences.getLong("firstDate",0L);
        int days = (int) TimeUnit.MILLISECONDS.toDays(diff);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(sharedPreferences.getLong("firstDate",0L));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        appSince.setText(dateFormat.format(cal.getTime()));
        int files = Utils.getImages().size();
        daysOfUse.setText("" + days);
        filesCount.setText("" + files);
        if(days>0) photosPerDay.setText("" + (float)files/days);
        else photosPerDay.setText("" + files);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == READ_STORAGE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initTextViews();

            } else {
                Toast.makeText(this, "Nie uzyskano uprawnien", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


}
