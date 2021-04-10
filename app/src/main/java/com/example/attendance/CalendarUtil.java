package com.example.attendance;

import java.util.Calendar;

public class CalendarUtil {
    public int cDay,cYear,cHour,cMinute,cSecond;
    public  String ampm,month,time;
    public CalendarUtil() {

    }

    public static CalendarUtil getcalendar(){
        CalendarUtil obj=new CalendarUtil();
        Calendar calander = Calendar.getInstance();
            obj.cDay    = calander.get(Calendar.DAY_OF_MONTH);
            obj.cYear   = calander.get(Calendar.YEAR);
            obj.cHour   = calander.get(Calendar.HOUR);
            obj.cMinute = calander.get(Calendar.MINUTE);
        if(obj.cHour==0)
        {
            obj.cHour=12;
        }
        obj.cSecond = calander.get(Calendar.SECOND);
        int am_pm=calander.get(Calendar.AM_PM);
        obj.ampm=(am_pm==0)?"AM":"PM";
        String[] montharr ={"January","February","March","April","May","June","July","August","September","October","November","December"};
        obj.month=montharr[calander.get(Calendar.MONTH)];
         obj.time= obj.cHour + ":" + obj.cMinute + ":" + obj.cSecond + obj.ampm;
         return obj;
    }
}
