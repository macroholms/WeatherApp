package com.example.wapp;
import android.util.Log;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Weather {
    public final String dayOfWeek;
    public final String time;
    public final String minTemp;
    public final String maxTemp;
    public final String humidity;
    public final String description;
    public final String iconURL;


    public Weather (long timeStamp, double minTemp, double maxTemp, double humidity,
                    String description, String iconName)
    {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        this.dayOfWeek= convertTimeStampToDay(timeStamp);
        this.time = convertTimeStampToTime(timeStamp);
        this.minTemp = numberFormat.format(minTemp )+ "\u00B0C";
        this.maxTemp = numberFormat.format(maxTemp )+ "\u00B0C";
        this.humidity = NumberFormat.getPercentInstance().format(humidity / 100.0);
        this.description = description;
        this.iconURL="http://openweathermap.org/ing/w/" + iconName + ".png";
    }

    private static Calendar calendar (long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp * 1000);
        TimeZone tz = TimeZone.getDefault();
        calendar.add(calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        return calendar;
    }

    private static String convertTimeStampToDay (long timeStamp){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE");
        return dateFormatter.format(calendar(timeStamp).getTime());
    }

    private static String convertTimeStampToTime(long timeStamp){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
        return dateFormatter.format(calendar(timeStamp).getTime());
    }
}
