package com.example.jan.praca;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.DefaultDayViewAdapter;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Callendar extends AppCompatActivity {

    ArrayList<Date> lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.loadLocale(getApplicationContext());
        setContentView(R.layout.activity_callendar);
        loaddata();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.calendar_view);

        calendar.setCustomDayView(new DefaultDayViewAdapter());
        calendar.setDecorators(Collections.<CalendarCellDecorator>emptyList());
        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 2);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -3);
        calendar.init(lastYear.getTime(), nextYear.getTime())
                .displayOnly();
        calendar.scrollToDate(c.getTime());
        calendar.highlightDates(lista);
    }

    public void loaddata()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCES", Activity.MODE_PRIVATE);
        Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
        String json = sharedPreferences.getString("daty","");
        Type type = new TypeToken<ArrayList<Date>>(){}.getType();
        lista = gson.fromJson(json,type);
        if(lista == null) lista = new ArrayList<Date>();

    }

}

