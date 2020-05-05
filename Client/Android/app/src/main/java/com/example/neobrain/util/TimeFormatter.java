package com.example.neobrain.util;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.neobrain.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeFormatter {
    private final String date;

    public TimeFormatter(String date) {
        this.date = date;
    }

    public String timeForChat(Context context) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat FormatMins = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        FormatMins.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date parsedDate;
        try {
            Calendar nowCal = Calendar.getInstance();
            nowCal.setTimeZone(TimeZone.getTimeZone("GMT"));
            nowCal.add(Calendar.HOUR_OF_DAY, 3);
            Date now = nowCal.getTime();
            parsedDate = FormatMins.parse(date);
            long milliseconds = now.getTime() - parsedDate.getTime();
            int seconds = (int) (milliseconds / (1000));
            if (seconds < 60) {
                return context.getResources().getString(R.string.less_than_one_minute);
            }
            int minutes = (int) (milliseconds / (60 * 1000));
            if (minutes < 60) {
                return minutes + " " + context.getResources().getString(R.string.n_minutes_before);
            }
            int hours = (int) (milliseconds / (60 * 60 * 1000));
            if (hours < 24) {
                return hours + " " + context.getResources().getString(R.string.n_hours_before);
            }
            int days = (int) (milliseconds / (24 * 60 * 60 * 1000));
            if (days < 7) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parsedDate);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == 2) {
                    return context.getResources().getString(R.string.Mn);
                } else if (dayOfWeek == 3) {
                    return context.getResources().getString(R.string.Tu);
                } else if (dayOfWeek == 4) {
                    return context.getResources().getString(R.string.Wd);
                } else if (dayOfWeek == 5) {
                    return context.getResources().getString(R.string.Th);
                } else if (dayOfWeek == 6) {
                    return context.getResources().getString(R.string.Fr);
                } else if (dayOfWeek == 7) {
                    return context.getResources().getString(R.string.Su);
                } else if (dayOfWeek == 1) {
                    return context.getResources().getString(R.string.St);
                }
            }
            if (days == 7) {
                return context.getResources().getString(R.string.week_before);
            }
            if (days < 30) {
                return days + " " + context.getResources().getString(R.string.n_days_before);
            }
            if (days < 365) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(parsedDate.getTime()));
                return cal.get(Calendar.DAY_OF_MONTH) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(parsedDate.getTime()));
                return cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH)
                        + "-" + cal.get(Calendar.YEAR);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return context.getResources().getString(R.string.error_in_date);
    }

    public String timeForMessageSeparator(Context context) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat FormatHours = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date now = new Date();
        Date parsedDate = null;
        try {
            parsedDate = FormatHours.parse(date);
            assert parsedDate != null;
            Calendar calendarNow = Calendar.getInstance();
            Calendar calendarThen = Calendar.getInstance();
            calendarThen.setTime(new Date(parsedDate.getTime()));
            if (calendarNow.get(Calendar.DAY_OF_YEAR) == calendarThen.get(Calendar.DAY_OF_YEAR) &&
                    calendarNow.get(Calendar.YEAR) == calendarThen.get(Calendar.YEAR)) {
                return context.getResources().getString(R.string.today);
            } else if (calendarNow.get(Calendar.DAY_OF_YEAR) == calendarThen.get(Calendar.DAY_OF_YEAR) - 1 &&
                    calendarNow.get(Calendar.YEAR) == calendarThen.get(Calendar.YEAR)) {
                return context.getResources().getString(R.string.yesterday);
            } else {
                return calendarThen.get(Calendar.DAY_OF_MONTH) + " " + calendarThen.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "";
    }

    public String timeForMessageHolder() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat FormatHours = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parsedDate = null;
        try {
            parsedDate = FormatHours.parse(date);
            assert parsedDate != null;
            Calendar calendarThen = Calendar.getInstance();
            calendarThen.setTime(new Date(parsedDate.getTime()));
            if (calendarThen.get(Calendar.MINUTE) <= 9) {
                return calendarThen.get(Calendar.HOUR_OF_DAY) + ":0" + calendarThen.get(Calendar.MINUTE);
            }
            return calendarThen.get(Calendar.HOUR_OF_DAY) + ":" + calendarThen.get(Calendar.MINUTE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
    }

    public Boolean compareWithNowDay() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat FormatHours = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parsedDate = null;
        Date now = new Date();
        try {
            parsedDate = FormatHours.parse(date);
            assert parsedDate != null;
            Calendar calendarThen = Calendar.getInstance();
            calendarThen.setTime(new Date(parsedDate.getTime()));
            return Calendar.getInstance().get(Calendar.DATE) == calendarThen.get(Calendar.DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean compareDates(String date2) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat FormatHours = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parsedDate;
        Date parsedDate2;
        try {
            parsedDate = FormatHours.parse(date);
            parsedDate2 = FormatHours.parse(date2);
            Calendar first = Calendar.getInstance();
            Calendar second = Calendar.getInstance();
            first.setTime(new Date(parsedDate.getTime()));
            second.setTime(new Date(parsedDate2.getTime()));
            int f_h_o_d = first.get(Calendar.HOUR_OF_DAY);
            int s_h_o_d = second.get(Calendar.HOUR_OF_DAY);
            int f_m_o_d = first.get(Calendar.MINUTE);
            int s_m_o_d = second.get(Calendar.MINUTE);
            if (f_h_o_d == s_h_o_d) {
                if (f_m_o_d == s_m_o_d) {
                    if (first.get(Calendar.DATE) == second.get(Calendar.DATE)) {
                        return true;
                    }
                }
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean compareDatesDays(String date2) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat FormatHours = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parsedDate;
        Date parsedDate2;
        try {
            parsedDate = FormatHours.parse(date);
            parsedDate2 = FormatHours.parse(date2);
            Calendar first = Calendar.getInstance();
            Calendar second = Calendar.getInstance();
            first.setTime(new Date(parsedDate.getTime()));
            second.setTime(new Date(parsedDate2.getTime()));
            first.set(Calendar.HOUR_OF_DAY, 0);
            first.set(Calendar.MINUTE, 0);
            first.set(Calendar.SECOND, 0);
            first.set(Calendar.MILLISECOND, 0);
            second.set(Calendar.HOUR_OF_DAY, 0);
            second.set(Calendar.MINUTE, 0);
            second.set(Calendar.SECOND, 0);
            second.set(Calendar.MILLISECOND, 0);
            if (first.equals(second)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String onFewEarlier() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat FormatHours = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parsedDate;
        try {
            parsedDate = FormatHours.parse(date);
            Calendar first = Calendar.getInstance();
            assert parsedDate != null;
            first.setTime(new Date(parsedDate.getTime()));
            first.add(Calendar.SECOND, -1);
            Date newDate = first.getTime();
            return FormatHours.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}