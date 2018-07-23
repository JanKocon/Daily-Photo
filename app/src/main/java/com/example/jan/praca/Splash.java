package com.example.jan.praca;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class Splash extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dotsLayout;
    private TextView[] dots;
    private SliderAdapter sliderAdapter;
    Button btnNext,btnBack;
    int currentPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sharedPreferences = getSharedPreferences("SPLASH",MODE_PRIVATE);
        dotsLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        addDots(0);
        viewPager.addOnPageChangeListener(viewListener);
        btnBack = (Button) findViewById(R.id.BTN_BACK);
        btnNext = (Button) findViewById(R.id.BTN_NEXT);
        currentPage=0;
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPage == dots.length -1)
                {
                    SharedPreferences.Editor editor = getSharedPreferences("SPLASH",MODE_PRIVATE).edit();
                    editor.putBoolean("splash",true);
                    editor.apply();
                    editor.putLong("firstDate", Calendar.getInstance().getTimeInMillis());
                    editor.apply();
                    displayDialog();

                }else {
                    viewPager.setCurrentItem(currentPage + 1);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(currentPage -1);
            }
        });
    }
    public void displayDialog()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Chciałbyś przywrócić zdjęcia z Dysku Google?");
        dialog.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(),Menu.class);
                startActivity(intent);
            }
        });
        dialog.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(),GDriveDownload.class);
                startActivity(intent);
            }
        });
        AlertDialog alert = dialog.create();
        alert.show();
    }
    public void addDots(int position)
    {
        dots = new TextView[3];
        dotsLayout.removeAllViews();
        for(int i=0; i<dots.length;i++)
        {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            dotsLayout.addView(dots[i]);
        }
        if(dots.length > 0)
        {
            dots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
        addDots(position);
        currentPage = position;
        if(position == 0)
        {
            btnBack.setEnabled(false);
            btnBack.setVisibility(View.INVISIBLE);

        }
        else if(position == dots.length -1)
        {
            btnNext.setText("Menu");
        }
        else{
            btnBack.setVisibility(View.VISIBLE);
            btnBack.setEnabled(true);
            btnNext.setText("Dalej");
        }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
}
