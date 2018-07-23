package com.example.jan.praca;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class TimeePickerFragment extends DialogFragment implements DialogInterface.OnDismissListener {

    PendingIntent pendingIntent;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Utils.loadLocale(getContext());
        Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final Calendar CALENDAR = Calendar.getInstance();
        int hour = CALENDAR.get(Calendar.HOUR_OF_DAY);
        int minute = CALENDAR.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog,timeSetListener,hour,minute,true);
        tpd.setIcon(R.drawable.ic_today_black_24dp);
        tpd.setTitle(getString(R.string.set_alarm));
        tpd.setButton(Dialog.BUTTON_POSITIVE,getString(R.string.ok_btn),tpd);
        tpd.setButton(Dialog.BUTTON_NEGATIVE,getContext().getString(R.string.cancel_btn),tpd);

        return tpd;
    }

    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
       @Override
       public void onTimeSet(TimePicker timePicker, int i, int i1) {
           Log.i("TAG",i + ":" + i1);
           AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
           int interval = 1000 * 60 * 5;
           SharedPreferences.Editor editor = getActivity().getSharedPreferences("Alarm",0).edit();
           editor.putInt("hour",i);
           editor.apply();
           editor.putInt("minute",i1);
           editor.apply();


           Calendar calendar = Calendar.getInstance();
           calendar.setTimeInMillis(System.currentTimeMillis());
           calendar.set(Calendar.HOUR_OF_DAY, i);
           calendar.set(Calendar.MINUTE, i1);
           calendar.set(Calendar.SECOND,0);
           calendar.set(Calendar.MILLISECOND,0);

           manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                   AlarmManager.INTERVAL_DAY, pendingIntent);
       }
   };
    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}
