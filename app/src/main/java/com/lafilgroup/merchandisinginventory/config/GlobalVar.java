package com.lafilgroup.merchandisinginventory.config;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ronald Remond Salas on 3/7/2018.
 */

public class GlobalVar
{

    public static final String DB_NAME="db_merchandiser";
    public static final int DB_VERSION=2;

    public static String user_id;
    public static String api_token;
    public static String full_name;
    public static String login_type;
    public static String customer_code;
    public static String customer_name;
    public static String schedule_id;
    public static String customer_id;
    public static String time_in;

    //use in send feedback
    public static String getCurrentDateTime()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTimeString = simpleDateFormat.format(new Date());
        return currentDateTimeString;
    }

    public static final String getCurrentDate(int addDays)
    {
        Calendar calendar = Calendar.getInstance(); // this would default to now
        calendar.add(Calendar.DAY_OF_YEAR, + addDays);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = simpleDateFormat.format(calendar.getTime());
        return currentDate;
    }

    public static final String getCurrentTime()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String currentDate = simpleDateFormat.format(new Date());
        return currentDate;
    }

    public static final String toDatetimeToString (String stringdate)
    {
        String generatedate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try
        {
            date = format.parse(stringdate);
            generatedate = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.SHORT).format(date);
            return generatedate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  "";
    }

    public static final String toDateToString (String stringDate)
    {
        String generatedate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try
        {
            date = format.parse(stringDate);
            generatedate = DateFormat.getDateInstance(DateFormat.LONG).format(date);
            return generatedate;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  "";
    }

    public static final String toTime24to12 (String stringTime)
    {
        String generatedate;
        SimpleDateFormat format = new SimpleDateFormat("H:mm:ss");
        Date time;
        try
        {
            time = format.parse(stringTime);
            generatedate =new SimpleDateFormat("h:mm a").format(time);
            return generatedate;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  "";
    }

    public static final Boolean compareTime (String time_out, String current_time)
    {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date out,current;
        try
        {
            out = format.parse(time_out);
            current= format.parse(current_time);
            if(out.getTime()>=current.getTime())
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (ParseException e)
        {
            return false;
        }
    }

    public static final String toStringToDate (String stringdate)
    {
        String generatedate;
        DateFormat format = DateFormat.getDateInstance(DateFormat.LONG);
        Date date;
        try
        {
            date = format.parse(stringdate);
            generatedate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            return generatedate;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return  "";
    }

    public static final String totalDifferenceTime (String startTime, String endTime)
    {
        SimpleDateFormat format = new SimpleDateFormat("H:mm:ss");
        Date startTime1, endTime1;
        String h,m;
        try
        {
            startTime1 = format.parse(startTime);
            endTime1 = format.parse(endTime);

            long difference = endTime1.getTime() - startTime1.getTime();
            long days = (int) (difference / (1000*60*60*24));
            long hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
            long min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);

            if (hours==1 || hours==0)
            {
                h=" hour ";
            }
            else
            {
                h=" hours ";
            }

            if (min==1 || min==0)
            {
                m=" minute ";
            }
            else
            {
                m=" minutes ";
            }

            return "Total Spend Time: " + hours+ h + "and " + min+ m;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

//    public static final String md5(final String s)
//    {
//        final String MD5 = "MD5";
//        try {
//            // Create MD5 Hash
//            MessageDigest digest = MessageDigest
//                    .getInstance(MD5);
//            digest.update(s.getBytes());
//            byte messageDigest[] = digest.digest();
//
//            // Create Hex String
//            StringBuilder hexString = new StringBuilder();
//            for (byte aMessageDigest : messageDigest) {
//                String h = Integer.toHexString(0xFF & aMessageDigest);
//                while (h.length() < 2)
//                    h = "0" + h;
//                hexString.append(h);
//            }
//            return hexString.toString();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
}
