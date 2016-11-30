package com.gaoyy.restaurant.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.gaoyy.restaurant.R;
import com.gaoyy.restaurant.ui.CheckActivity;
import com.gaoyy.restaurant.utils.Constant;
import com.gaoyy.restaurant.utils.GsonUtils;
import com.gaoyy.restaurant.utils.OkhttpUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Request;

public class PollingService extends Service
{
    public static final String ACTION = "com.gaoyy.restaurant.service.PollingService";
    private Notification notification;
    private NotificationManager manager;
    private SharedPreferences orderCount;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        initNotifiManager();
        orderCount = getSharedPreferences("order_count", Activity.MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(Constant.TAG, "onStartCommand() executed");
        OkhttpUtils.getAsync(this, Constant.ORDER_GET_ORDER_COUNT, "get_order_count", new OkhttpUtils.ResultCallback()
        {
            @Override
            public void onError(Request request, Exception e)
            {

            }

            @Override
            public void onSuccess(String body)
            {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Log.e(Constant.TAG, "service time===" + df.format(new Date()));
                if (GsonUtils.getResponseCode(body) == Constant.ERROR)
                {
                    Log.e(Constant.TAG, "=============error===============");
                }
                else
                {

                    int spCount = orderCount.getInt("orderCount", -1);
                    int count = Integer.parseInt(GsonUtils.getResponseInfo(body, "data"));
                    Log.e(Constant.TAG, "===order count==" + count);
                    Log.e(Constant.TAG, "===sp order count==" + spCount);
                    SharedPreferences.Editor editor = orderCount.edit();
                    if (spCount == -1)
                    {
                        editor.putInt("orderCount", count);
                        editor.apply();
                    }
                    else
                    {
                        if (spCount != count)
                        {
                            showNotification();
                            editor.putInt("orderCount", count);
                            editor.apply();
                        }
                    }

                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    //初始化通知栏配置
    private void initNotifiManager()
    {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, CheckActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker("订单列表有更新");
        builder.setContentTitle("订单列表有更新");
        builder.setContentText("速度去看看");
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.ic_notifications_green_24dp);
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_notifications_green_24dp));
        builder.setContentIntent(pendingIntent);
        builder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
        builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        notification = builder.build();

    }

    //弹出Notification
    private void showNotification()
    {
        manager.notify(0, notification);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        System.out.println("Service:onDestroy");
        OkhttpUtils.cancelTag("get_order_count");
    }
}
