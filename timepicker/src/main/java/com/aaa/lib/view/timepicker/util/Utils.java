package com.aaa.lib.view.timepicker.util;

/**
 * Description
 * Created by aaa on 2016/12/14.
 */

public class Utils
{
    public static  int getDay(int year, int month)
    {
        int day = 30;
        boolean flag = false;

        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
        {
            flag = true;        //闰年
        } else
        {
            flag = false;
        }

        switch (month)
        {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }
}
