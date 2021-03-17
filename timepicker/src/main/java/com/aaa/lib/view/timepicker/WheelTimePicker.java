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

public class WheelTimePicker
{
    private static final String DATAFORMAT = "HH:mm:ss";
    private Dialog dialog;
    private Context context;
    private String title;
    private WheelView hour, min, sec;
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
                dialog.dismiss();
            } else if (v.getId() == R.id.btn_dialog_set_time_confirm)
            {
                isConfirm = true;
                dialog.dismiss();
            }
        }
    };


    public WheelTimePicker(Context ctx)
    {
        context = ctx;
    }

    public void timePicker(Date date, String title, OnDatetimeSetListener callback)
    {
        isConfirm = false;
        tmpTimestamp = "";

        this.callback = callback;
        this.title = title;

        if (dialog == null)
        {
            initDialog(context);
        }

        initAdapter();                    //每次显示都重新设置数据适配

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

            View _v = getView(c, R.layout.dialog_date_time_picker);

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


        hour = (WheelView) view.findViewById(R.id.wv_hour);
        min = (WheelView) view.findViewById(R.id.wv_minute);
        sec = (WheelView) view.findViewById(R.id.wv_secend);

        tv_title = (TextView) view.findViewById(R.id.tv_dialog_title_set_time);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_dialog_set_time_cancel);
        Button btn_confirm = (Button) view.findViewById(R.id.btn_dialog_set_time_confirm);


        String _hourLabel = c.getResources().getString(R.string.wheel_label_hour);
        String _minLabel = c.getResources().getString(R.string.wheel_label_minute);
        String _secondLabel = c.getResources().getString(R.string.wheel_label_second);

        hour.setLabel(_hourLabel);
        min.setLabel(_minLabel);
        sec.setLabel(_secondLabel);


        hour.setCyclic(true);
        min.setCyclic(true);
        sec.setCyclic(true);

        btn_cancel.setOnClickListener(clickListener);
        btn_confirm.setOnClickListener(clickListener);

        return view;
    }

    private void initAdapter()
    {
        hour.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        min.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        sec.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));

        tv_title.setText(title);
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

        hour.setCurrentItem(c.get(Calendar.HOUR_OF_DAY));
        min.setCurrentItem(c.get(Calendar.MINUTE));
        sec.setCurrentItem(c.get(Calendar.SECOND));
    }


    private void formatDateTime()
    {
        StringBuffer strBuffer = new StringBuffer();

        strBuffer.append(String.format("%02d", hour.getCurrentItem())).append(":")
                .append(String.format("%02d", min.getCurrentItem())).append(":")
                .append(String.format("%02d", sec.getCurrentItem()));

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


    DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener()
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
                    Date _d = null;

                    if (!TextUtils.isEmpty(tmpTimestamp))
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat(DATAFORMAT, Locale.getDefault());
                        try
                        {
                            _d = sdf.parse(tmpTimestamp);
                        } catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    callback.onDatetimeSet(tmpTimestamp, _d);
                }
            }
        }
    };
}
