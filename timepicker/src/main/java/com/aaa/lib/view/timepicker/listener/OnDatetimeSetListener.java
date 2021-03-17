package com.aaa.lib.view.timepicker.listener;

import java.util.Date;

/**
 * Description
 * Created by aaa on 2016/12/14.
 */
public interface OnDatetimeSetListener
{
    /**
     * 上一次选择的时间
     *
     * @param timestamp 时间戳
     * @param date      时间
     */
    void onDatetimeSet(String timestamp, Date date);
}
