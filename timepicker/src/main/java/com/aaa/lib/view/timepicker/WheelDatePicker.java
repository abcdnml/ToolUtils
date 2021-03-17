package com.aaa.lib.view.timepicker;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.aaa.lib.view.timepicker.adapter.NumericWheelAdapter;
import com.aaa.lib.view.timepicker.listener.OnDatetimeSetListener;
import com.aaa.lib.view.timepicker.listener.OnWheelScrollListener;
import com.aaa.lib.view.timepicker.util.Utils;
import com.aaa.lib.view.timepicker.view.WheelView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Description
 * Created by aaa on 2016/12/13.
 */

public class WheelDatePicker
{
    private static final int START_YEAR = 1970;
    private static final String DATAFORMAT = "yyyy-MM-dd";
    private Dialog dialog;
    private Context context;
    private String title;
    private WheelView year, month, day;
    private TextView tv_title;
    private String tmpTimestamp = "";
    private boolean isConfirm = false;
    private OnDatetimeSetListener callback;
    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            if (v.getId() == R.id.btn_dialog_set_time_cancel)
            {
                isConfirm = false;
                dialog.dismiss();
            } else if (v.getId() == R.id.btn_dialog_set_time_confirm)
            {
                isConfirm = true;
                dialog.dismiss();
            }
        }
    };
    private OnWheelScrollListener scrollListener = new OnWheelScrollListener()
    {
        @Override
        public void onScrollingStarted(WheelView wheel)
        {
        }

        @Override
        public void onScrollingFinished(WheelView wheel)
        {
            int n_year = year.getCurrentItem() + START_YEAR;
            int n_month = month.getCurrentItem() + 1;

            initDay(n_year, n_month);
        }

    };

    public WheelDatePicker(Context ctx)
    {
        context = ctx;
    }

    public void datePicker(Date date, String title, OnDatetimeSetListener callback)
    {
        isConfirm = false;
        tmpTimestamp = "";

        this.callback = callback;
        this.title = title;

        if (dialog == null)
        {
            initDialog(context);
        }

        initAdapter(date);                    //每次显示都重新设置数据适配

		/*
         * 初始化本次日期时间显示，如果没提供要显示的日期对象则显示当前系统时间
		 */
        initDefaultDateTime(date);

        dialog.show();
    }

    private void initDialog(Context c)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            getMyDialog(c);
        } else
        {
            getMyDialogCom(c);
        }

        if (dialog != null)
        {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            View _v = getView(c, R.layout.dialog_date_picker);

            dialog.setContentView(_v);

            dialog.setCancelable(false);

            dialog.setOnDismissListener(dismissListener);

/*			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.alpha = 0.6f;
			dialog.getWindow().setAttributes(lp);*/
        }
    }

    private View getView(Context c, int layout)
    {
        View view = LayoutInflater.from(c).inflate(layout, null);

        year = (WheelView) view.findViewById(R.id.wv_year);
        month = (WheelView) view.findViewById(R.id.wv_month);
        day = (WheelView) view.findViewById(R.id.wv_day);

        tv_title = (TextView) view.findViewById(R.id.tv_dialog_title_select_date);
        tv_title.setText(title);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_dialog_set_time_cancel);
        Button btn_confirm = (Button) view.findViewById(R.id.btn_dialog_set_time_confirm);

        String _yearLabel = c.getResources().getString(R.string.wheel_label_year);
        String _monthLabel = c.getResources().getString(R.string.wheel_label_month);
        String _dayLabel = c.getResources().getString(R.string.wheel_label_day);

        year.setLabel(_yearLabel);
        month.setLabel(_monthLabel);
        day.setLabel(_dayLabel);


        year.setCyclic(true);
        month.setCyclic(true);
        day.setCyclic(true);

        btn_cancel.setOnClickListener(clickListener);
        btn_confirm.setOnClickListener(clickListener);

        // 年月滚动改变的时候需要根据年是否是闰年和月份动态改变天数
        year.addScrollingListener(scrollListener);
        month.addScrollingListener(scrollListener);

        return view;
    }

    private void initAdapter(Date date)
    {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        year.setAdapter(new NumericWheelAdapter(START_YEAR, calendar.get(Calendar.YEAR)+100, "%04d"));
        month.setAdapter(new NumericWheelAdapter(1, 12, "%02d"));

        //日期  根据当前的年月
        if (date != null)
        {
            calendar.setTime(date);
        }
        int norYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH) + 1;    //通过Calendar算出的月数要+1
        initDay(norYear, curMonth);


    }

    private void initDefaultDateTime(Date date)
    {
        Calendar c = Calendar.getInstance();

        try
        {
            if (date != null)
            {
                c.setTime(date);
            }
        } catch (Exception e)
        {
        }

        year.setCurrentItem(c.get(Calendar.YEAR) - START_YEAR);
        month.setCurrentItem(c.get(Calendar.MONTH));
        day.setCurrentItem(c.get(Calendar.DAY_OF_MONTH) - 1);
    }

    private void initDay(int arg1, int arg2)
    {
        day.setAdapter(new NumericWheelAdapter(1, Utils.getDay(arg1, arg2), "%02d"));
    }



    private void formatDateTime()
    {
        StringBuffer strBuffer = new StringBuffer();

        strBuffer.append(year.getCurrentItem() + START_YEAR).append("-")
                .append(String.format("%02d", month.getCurrentItem() + 1)).append("-")
                .append(String.format("%02d", day.getCurrentItem() + 1)).append(" ");

        tmpTimestamp = strBuffer.toString();
    }

    public String getLastedSelectedDatetime()
    {
        return this.tmpTimestamp;
    }

    public void reset()
    {
        tmpTimestamp = "";
        context = null;
        dialog = null;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void getMyDialog(Context c)
    {
        if (dialog == null)
        {
            dialog = new Dialog(c, android.R.style.Theme_Holo_Light_Dialog);
        }
    }

    private void getMyDialogCom(Context c)
    {
        if (dialog == null)
        {
            dialog = new Dialog(c, android.R.style.Theme_Dialog);
        }
    }

    DialogInterface.OnDismissListener dismissListener=new DialogInterface.OnDismissListener()
    {
        @Override
        public void onDismiss(DialogInterface dialog)
        {
            //当前的选中时间
            formatDateTime();

            if (isConfirm)
            {
                if (callback != null)
                {
                    Date date = null;

                    if (!TextUtils.isEmpty(tmpTimestamp))
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat(DATAFORMAT, Locale.getDefault());
                        try
                        {
                            date = sdf.parse(tmpTimestamp);
                        } catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    callback.onDatetimeSet(tmpTimestamp, date);
                }
            }
        }
    };
}
