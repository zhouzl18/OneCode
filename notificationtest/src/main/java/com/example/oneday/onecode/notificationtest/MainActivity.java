package com.example.oneday.onecode.notificationtest;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private NotificationManager noticeManager;
    private Notification.Builder builder;
    private final int NOTICE_TESt = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noticeManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.home_more_white);
        builder.setAutoCancel(true);
        builder.setTicker("This is a ticker.");
        builder.setContentIntent(getPendingIntent("This is a pending intent."));
        builder.setContentTitle("This is a title");
        builder.setContentText("This is a content1.");
    }

    PendingIntent getPendingIntent(String text){
        return PendingIntent.getActivity(this, text.hashCode(),
                new Intent(this, NoticeActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);

    }

    @TargetApi(16)
    public void sendNotice(View view){
        noticeManager.notify(NOTICE_TESt, builder.build());
    }
}
