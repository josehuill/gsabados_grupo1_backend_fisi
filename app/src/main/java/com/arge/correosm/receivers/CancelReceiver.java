package com.arge.correosm.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.arge.correosm.providers.AlumnoABookingProvider;

public class CancelReceiver extends BroadcastReceiver {
    private AlumnoABookingProvider malumnoABookingProvider;

    @Override
    public void onReceive(Context context, Intent intent) {

        String idAlumnoA = intent.getExtras().getString("idAlumnoA");
        malumnoABookingProvider = new AlumnoABookingProvider();
        malumnoABookingProvider.usdateStatus(idAlumnoA, "cancel");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);
    }
}
