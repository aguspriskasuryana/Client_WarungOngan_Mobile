package com.example.gan.mywoa.Utils;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GUSWIK on 7/19/2017.
 */
public class Formater {

    public static Date getDateFromString(String dateS){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date= new Date();
        try {
            date=  dateFormat.parse(dateS);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }


    public static String getStringDate(Date date){
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        String s = formatter.format(date);
        return s;
    }

    public static String getDateLastDayOfTheMonth() {
        String dateFormat = "";
        Date today = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);

            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DATE, -1);

            Date lastDay = calendar.getTime();

            //lastDayOfTheMonth = formatter.format(lastDay);


            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            dateFormat = formatter.format(lastDay);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFormat;
    }


    public static String getDateFirstDayOfTheMonth() {
        String dateFormat = "";
        Date today = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);

            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DATE, -1);

            Date firstDay = calendar.getTime();

            //lastDayOfTheMonth = formatter.format(lastDay);


            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            //dateFormat = formatter.format(lastDay);
            String monthS = "";
            if ((firstDay.getMonth()+1) < 10){
                monthS = "0"+(firstDay.getMonth()+1);
            } else {
                monthS = ""+(firstDay.getMonth()+1);
            }
            dateFormat = (1900+firstDay.getYear())+"-"+monthS+"-01" ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFormat;
    }


}
