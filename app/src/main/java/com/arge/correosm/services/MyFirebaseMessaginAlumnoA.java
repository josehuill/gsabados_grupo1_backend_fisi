package com.arge.correosm.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.arge.correosm.R;
import com.arge.correosm.channel.NotficationHealper;
import com.arge.correosm.providers.AuthProvider;
import com.arge.correosm.receivers.AcceptReceiver;
import com.arge.correosm.receivers.CancelReceiver;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessaginAlumnoA extends FirebaseMessagingService {

    private AuthProvider mAuthProvider;

    private static final int  NOTIFICATION_CODE =100;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.e("token", "mi token es: " +s);

        guardarToken(s);
    }

    public void guardarToken(String s) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("token");
        mAuthProvider = new AuthProvider();
        ref.child(mAuthProvider.GetID()).setValue(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification notification= remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");

        if (title != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                if (title.contains("SOLICITUD DE EVENTO")){
                    String idAlumnoA = data.get(("idAlumnoA"));
                    showNotificationApiOreoActions(title, body, idAlumnoA );
                }else {
                    showNotificationApiOreo(title, body);
                }
            }else {
                if(title.contains("SOLICITUD DE EVENTO")){
                    String idAlumnoA = data.get(("idAlumnoA"));
                    ShowNotificaionAcction(title, body, idAlumnoA );
                }else   {
                    ShowNotificaion(title, body);
                }
            }
        }
    }

    private void ShowNotificaion(String title, String body) {

        PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotficationHealper notficationHealper = new NotficationHealper(getBaseContext());
        NotificationCompat.Builder builder = notficationHealper.getBotificaionOldAPI(title, body, intent, sound);
        notficationHealper.getManager().notify(1, builder.build());
    }

    private void ShowNotificaionAcction(String title, String body, String idAlumnoA) {
        //Aceptar
        Intent acceptItent = new Intent(this, AcceptReceiver.class);
        acceptItent.putExtra("idAlumnoA", idAlumnoA);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this,NOTIFICATION_CODE, acceptItent ,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action acceptAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Aceptar",
                acceptPendingIntent
        ).build();

        //cancelar
        Intent cancelIntent = new Intent(this, CancelReceiver.class);
        cancelIntent.putExtra("idAlumnoA", idAlumnoA);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this,NOTIFICATION_CODE, cancelIntent ,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action cancelAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Cancelar",
                cancelPendingIntent
        ).build();


        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotficationHealper notficationHealper = new NotficationHealper(getBaseContext());
        NotificationCompat.Builder builder = notficationHealper.getBotificaionOldAPIAction(title, body, sound, acceptAction, cancelAction);
        notficationHealper.getManager().notify(2, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationApiOreo(String title, String body) {

        PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotficationHealper notficationHealper = new NotficationHealper(getBaseContext());
        Notification.Builder builder = notficationHealper.getNotificaion(title, body, intent, sound);
        notficationHealper.getManager().notify(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationApiOreoActions(String title, String body, String idAlumnoA) {
        //aceptar
        Intent acceptItent = new Intent(this, AcceptReceiver.class);
        acceptItent.putExtra("idAlumnoA", idAlumnoA);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this,NOTIFICATION_CODE, acceptItent ,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action acceptAction = new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Aceptar",
                acceptPendingIntent
        ).build();

        //cancelar

        Intent canceltItent = new Intent(this, CancelReceiver.class);
        canceltItent.putExtra("idAlumnoA", idAlumnoA);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this,NOTIFICATION_CODE, canceltItent ,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action canceltAction = new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Cancelar",
                cancelPendingIntent
        ).build();

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotficationHealper notficationHealper = new NotficationHealper(getBaseContext());
        Notification.Builder builder = notficationHealper.getNotificaionActions(title, body, sound, acceptAction, canceltAction);
        notficationHealper.getManager().notify(2, builder.build());
    }
}
