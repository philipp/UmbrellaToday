package org.bostonandroid.umbrellatoday;

import java.util.Hashtable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class AlarmService extends Service {
    public final static String TAG = "AlarmReceiver";
    private NotificationManager notificationManager;
    private Hashtable<Integer, ReportRetriever> retrievers;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        retrievers = new Hashtable<Integer, ReportRetriever>();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        ReportConsumer consumer = new AlarmServiceReportConsumer(intent,
                startId);
        ReportRetriever r = new ReportRetriever(consumer);
        retrievers.put(startId, r);
        r.execute(intent.getDataString());
    }

    private void showNotification(String message, PendingIntent contentIntent) {
        Notification notification = new Notification(
                R.drawable.notification_icon, "Umbrella Today", System
                        .currentTimeMillis());

        notification.setLatestEventInfo(AlarmService.this, "UmbrellaToday",
                message, contentIntent);

        notificationManager.notify(1, notification);
    }

    private class AlarmServiceReportConsumer implements ReportConsumer {
        private Intent intent;
        private int startId;

        AlarmServiceReportConsumer(Intent intent, int startId) {
            this.intent = intent;
            this.startId = startId;
        }

        public void consumeReport(Report report) {
            if (report.getAnswer().equals("yes")) {
                PendingIntent contentIntent = notificationIntent(intent);
                showNotification("You should bring your Umbrella today",
                        contentIntent);
                retrievers.remove(startId);
                if (retrievers.isEmpty()) {
                    stopSelf();
                }
            }
        }

        private PendingIntent notificationIntent(Intent intent) {
            Intent notificationIntent = new Intent(Intent.ACTION_VIEW, intent
                    .getData());
            notificationIntent.setClass(AlarmService.this,
                    UmbrellaForToday.class);

            PendingIntent contentIntent = PendingIntent.getActivity(
                    AlarmService.this, 0, notificationIntent, 0);

            return contentIntent;
        }
    }
}
