package com.example.neobrain.util;

import android.annotation.SuppressLint;

import com.example.neobrain.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeFormater {
    private final String date;

    public TimeFormater(String date) {
        this.date = date;
    }

    public String time() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat FormatMins = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date now = new Date();
        Date parsedDate;
        try {
            parsedDate = FormatMins.parse(date);
            assert parsedDate != null;
            long milliseconds = now.getTime() - parsedDate.getTime();
            int seconds = (int) (milliseconds / (1000));
            if (seconds < 60) {
                return String.valueOf(R.string.less_than_one_minute);
            }
            int minutes = (int) (milliseconds / (60 * 1000));
            if (minutes < 60) {
                return minutes + String.valueOf(R.string.n_minutes_before);
            }
            int hours = (int) (milliseconds / (60 * 60 * 1000));
            if (hours < 24) {
                return hours + String.valueOf(R.string.n_hours_before);
            }
            int days = (int) (milliseconds / (24 * 60 * 60 * 1000));
            if (days < 7) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parsedDate);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == 2) {
                    return String.valueOf(R.string.Mn);
                } else if (dayOfWeek == 3) {
                    return String.valueOf(R.string.Tu);
                } else if (dayOfWeek == 4) {
                    return String.valueOf(R.string.Wd);
                } else if (dayOfWeek == 5) {
                    return String.valueOf(R.string.Th);
                } else if (dayOfWeek == 6) {
                    return String.valueOf(R.string.Fr);
                } else if (dayOfWeek == 7) {
                    return String.valueOf(R.string.Su);
                } else if (dayOfWeek == 1) {
                    return String.valueOf(R.string.St);
                }
            }
            if (days == 7) {
                return String.valueOf(R.string.week_before);
            }
            if (days < 30) {
                return days + String.valueOf(R.string.n_days_before);
            }
            if  (days < 365) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(parsedDate.getTime()));
                return cal.get(Calendar.DAY_OF_MONTH) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);
            }
            else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(parsedDate.getTime()));
                return cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH)
                        + "-" + cal.get(Calendar.YEAR);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(R.string.error_in_date);
    }
}