package com.example.jan.praca;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Utils {

   public static final String IMG_FORMAT = ".webp";
   public static final String MIME_IMG = "image/webp";
   public static final String MIME_DRIVE_FOLDER = "application/vnd.google-apps.folder";
   public static final String GENERAL_PREFERENCES = "GENERAL";
   public static final String REMINDER_PREFERENCES = "ALARM";
   public static final String DIR_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/DailyPhoto";
   public static final String ASSETS_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/";
   public static boolean compareDate(Date date)
   {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      Calendar currentCalendar = Calendar.getInstance();
      currentCalendar.setTime(new Date());
      if(calendar.get(Calendar.DAY_OF_MONTH) != currentCalendar.get(Calendar.DAY_OF_MONTH))
      {
         return true;
      }
      return false;
   }
   public static Date getToday()
   {
      Calendar calendar = Calendar.getInstance();
      return calendar.getTime();

   }
   public static List<String> getImages() {
      ArrayList<String> result= new ArrayList<>();
      File[] files = new File(DIR_PATH).listFiles();
      if(files != null && files.length > 0) {
         for (int i = 0; i < files.length; i++) {
            if (files[i].getAbsolutePath().endsWith(IMG_FORMAT) && files[i].length() > 0) {
               result.add(files[i].getAbsolutePath());
            }
         }
         Collections.sort(result);
      }
      return result;
   }

   public static void loadLocale(Context ctx) {
      SharedPreferences sharedPreferences = ctx.getSharedPreferences("LANG", ctx.MODE_PRIVATE);
      String language = sharedPreferences.getString("MyLang", "");
      Log.i("TAG", "LANG: " + language);
      if (!language.equals("")) {
         Locale locale = new Locale(language);
         Locale.setDefault(locale);
         Configuration config = new Configuration();
         config.locale = locale;
         ctx.getResources().updateConfiguration(config, ctx.getResources().getDisplayMetrics());
      }
   }
}
