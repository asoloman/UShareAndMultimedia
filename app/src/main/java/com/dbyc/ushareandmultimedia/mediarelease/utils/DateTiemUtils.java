package com.dbyc.ushareandmultimedia.mediarelease.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016-10-13.
 */
public class DateTiemUtils {

    public static String editTime(String time , boolean is){

        Date d1 = null;
        SimpleDateFormat sdf;
        String str="";
        try {

            if(is){
                d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(time);
                sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");//年月日 时分秒的
                str = sdf.format(d1);
                return str;

            }else {
                d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
                sdf = new SimpleDateFormat("yyyy年MM月dd日");//年月日的

                str = sdf.format(d1);//取出特定日期d1的日
                return str ;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return str;

    }
    public static String editTime(String time){

        Date d1 = null;
        SimpleDateFormat sdf;
        String str="";
        try {


                d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(time);
                sdf = new SimpleDateFormat("MM月dd日    HH:mm");//月日 时分
                str = sdf.format(d1);
                return str;


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return str;

    }

    public static long dateToTime(String str){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

        try {
            long millionSeconds = sdf.parse(str).getTime();//毫秒
            return millionSeconds;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public static String dateToDate(String date){

        Date d1 = null;
        SimpleDateFormat sdf;
        String str="";
        try {

            d1 = new SimpleDateFormat("yyyy年MM月dd日").parse(date);
            sdf = new SimpleDateFormat("yyyy/MM/dd");//年月日
            str = sdf.format(d1);
            return str;


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return str;
    }

    public static String longToDate(int time){

        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String str = sdf.format(d);

        return str;
    }


}
