package com.example.neobrain.sorts;

import android.annotation.SuppressLint;

import com.example.neobrain.API.model.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SortByTime {
    public ArrayList<Long> datesUNIX;

    public SortByTime(ArrayList<String> datesNotUNIX) throws ParseException {
        for (String dateNotUNIX : datesNotUNIX) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(dateNotUNIX);
            assert date != null;
            Long timestamp = date.getTime();
            datesUNIX.add(timestamp);
        }
    }

    public ArrayList<Long> sort() {
        Collections.sort(datesUNIX, (o1, o2) -> o2.compareTo(o1));
        return datesUNIX;
    }

    public ArrayList<Long> getDatesUNIX() {
        return datesUNIX;
    }

    public void setDatesUNIX(ArrayList<Long> datesUNIX) {
        this.datesUNIX = datesUNIX;
    }
}
