package com.medicare_service.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.medicare_service.R;
import com.medicare_service.model.Notification;
import com.orhanobut.hawk.Hawk;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final static AtomicInteger c = new AtomicInteger(0);

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String type = remoteMessage.getData().get("type");
        if (!Hawk.get(Constants.TYPE_IS_MESSAGES_SCREEN_OPENED, false)) {
            sendNotification(title, body, type);
        }
    }

    @Override
    public void onNewToken(@NonNull String mToken) {
        super.onNewToken(mToken);
    }

    private void sendNotification(String title, String body, String type) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        CharSequence name = getString(R.string.app_name);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "notify_0020");
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(body);
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("notify_0020", name, importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(c.incrementAndGet(), notificationBuilder.build());
        if (!Objects.equals(type, "message")) {
            addNotificationRequest(title, body);
        }

    }

    private void addNotificationRequest(String title, String content) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.TABLE_NOTIFICATIONS).push();
        Notification model = new Notification(
                reference.getKey(),
                title,
                content,
                System.currentTimeMillis() / 1000,
                Functions.getUserId()
        );
        reference.setValue(model);
    }

}
