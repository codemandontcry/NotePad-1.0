package com.zc.memo.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.zc.memo.R;
import com.zc.memo.activity.ContentActivity;
import com.zc.memo.item.Note;
import com.zc.memo.manager.NoteManager;

public class OneShotAlarm extends BroadcastReceiver {

    private String alarm_title;

    @Override
    public void onReceive(Context context, Intent intent) {
        //showMemo(context);

        alarm_title=intent.getStringExtra("alarm_title");

        Toast.makeText(context,"Time UP!",Toast.LENGTH_LONG).show();

        Vibrator vb =(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(300);

        showNotice(context);
    }

    //show notice and it can be clicked
    private void showNotice(Context context) {

        Intent intent=new Intent(context,ContentActivity.class);
        Note record = new NoteManager(context).get(alarm_title);
        new NoteManager(context).deleteAlarm(alarm_title);
        PendingIntent pi=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);//PendingIntent.FLAG_UPDATE_CURRENT is very important which caused a bug and troubles me for a long time

        NotificationManager manager=(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Notification notification=new NotificationCompat.Builder(context)
                .setContentTitle(record.getCreate_date())
                .setContentText(record.getText())
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.icon))
                .setContentIntent(pi)
                .setAutoCancel(true)
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(record.getMainText()))
                .setLights(Color.GREEN,1000,1000)
                .build();
        manager.notify(0,notification);
    }

}
