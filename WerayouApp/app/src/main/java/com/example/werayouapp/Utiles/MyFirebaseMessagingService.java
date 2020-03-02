package com.example.werayouapp.Utiles;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.werayouapp.Activity.ActivityPrincipal;
import com.example.werayouapp.Activity.ChatActivity;
import com.example.werayouapp.Activity.DetailPhotoActivity;
import com.example.werayouapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        Map<String, String> extraData = remoteMessage.getData();

        String id = extraData.get("id_user");
        String type = extraData.get("type");
        String id_post = extraData.get("id_post");
        String id_user = extraData.get("id_user");
        String description = extraData.get("description");
        String image = extraData.get("image");
        String date = extraData.get("date");

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "WERAYOUCHANNEL")
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(R.drawable.welogo);

        Intent intent = null;
        if (type.equals("chat_notification")) {
            intent = new Intent(this, ChatActivity.class);
            intent.putExtra("id", id);

        } else if (type.equals("post_notification")){
            intent = new Intent(this, DetailPhotoActivity.class);
            intent.putExtra("id_post", id_post);
            intent.putExtra("id_user", id_user);
            intent.putExtra("description", description);
            intent.putExtra("image", image);
            intent.putExtra("date", date);


        } else if (type.equals("new_friends_notification")){
            intent = new Intent(this, ActivityPrincipal.class);
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        int idNotification = (int) System.currentTimeMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("WERAYOUCHANNEL", "demo", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(idNotification, notificationBuilder.build());

    }
}