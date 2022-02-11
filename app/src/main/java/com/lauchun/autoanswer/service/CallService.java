package com.lauchun.autoanswer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.lauchun.autoanswer.R;
import com.lauchun.autoanswer.listener.CallReceiver;

/**
 * @author ：lauchun
 * @date ：Created in 2/9/22
 * @description ：
 * @version: 1.0
 */
public class CallService extends Service {

    private static final int NOTICE_ID = 0x888;
    private static final String CHANNEL_ID = "CallService";
    private static final String CHANNEL_NAME = "CallService";
    private String phoneNum = null;


    CallReceiver callReceiver = null;
    TelephonyManager telephonyManager = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        int importance = NotificationManager.IMPORTANCE_HIGH;//channel的重要性
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                importance);//生成channel
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);//添加channel
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("AutoAnswer")
                .setContentText("自动接听已启动")
                .setAutoCancel(true)
                .build();
        manager.notify(NOTICE_ID, notification);
        //启动前台服务
        startForeground(NOTICE_ID, notification);

        registerOrUnregisterListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        phoneNum = intent.getStringExtra("phoneNum");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        NotificationManager mManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mManager.cancel(NOTICE_ID);
        registerOrUnregisterListener();

        super.onDestroy();

//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        startActivity(intent);
    }

    private void registerOrUnregisterListener() {
        callReceiver = new CallReceiver(this);
        if (telephonyManager == null) {
            telephonyManager =
                    (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(callReceiver, PhoneStateListener.LISTEN_CALL_STATE);
            System.out.println("监听已开启");
        } else {
            telephonyManager.listen(callReceiver,PhoneStateListener.LISTEN_NONE);
            System.out.println("监听已关闭");
        }
    }
}
