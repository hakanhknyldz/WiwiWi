package wiwiwi.io.wearwithweather.network;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import wiwiwi.io.wearwithweather.MainActivity;
import wiwiwi.io.wearwithweather.NotificationShower;
import wiwiwi.io.wearwithweather.R;

/**
 * Created by hakan on 24.05.2016.
 */
public class wiAlarmReceiver extends BroadcastReceiver {
    int MID=0;
    final int MORNING_FILTER = 1111;
    final int NIGHT_FILTER = 9999;

    @Override
    public void onReceive(Context context, Intent intent) {
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int requestCode = intent.getExtras().getInt("requestCode");
        String description = intent.getExtras().getString("description");
        String title = intent.getExtras().getString("title");

        Intent myIntent = new Intent(context, wiAlarmService.class);
        myIntent.putExtra("title", title);
        myIntent.putExtra("description", description);
        myIntent.putExtra("requestCode", requestCode);
        context.startService(myIntent);
/*

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(
                context).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Wear With Weather - Daily Reminder")
                .setContentText("Sabah oldu. Haydi nasıl giyineceğimize karar verme vakti!").setSound(alarmSound)
                .setAutoCancel(true).setWhen(when)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        notificationManager.notify(MID, mNotifyBuilder.build());
        MID++;
*/
    }
}
