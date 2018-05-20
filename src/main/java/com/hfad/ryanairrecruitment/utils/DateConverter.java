package com.hfad.ryanairrecruitment.utils;

import java.util.Calendar;
import java.util.Date;

public class DateConverter {
    public static Calendar convertDateToCalendar(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
