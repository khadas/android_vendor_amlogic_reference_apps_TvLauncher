package com.droidlogic.launcher.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Gravity;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeDisplay {
    private TextView mDisplay;
    private Context  mContext;

    public TimeDisplay(Context context, TextView view){
        mContext = context;
        mDisplay = view;
        mDisplay.setGravity(Gravity.RIGHT);
    }

    public void init(){
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        mContext.registerReceiver(receiver,filter);
    }

    public void unInit(){
        mContext.unregisterReceiver(receiver);
    }

    public void update(){
        Date date = new Date();
        //Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm yyyy-MM-dd");
        //SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time = dateFormat.format(date);

        mDisplay.setText(time);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK) || action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                update();
            }
        }
    };
}
