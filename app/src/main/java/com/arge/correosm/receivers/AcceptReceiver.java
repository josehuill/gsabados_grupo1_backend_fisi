package com.arge.correosm.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.arge.correosm.activities.AlumnoB.MapAlumnoBBookingActivity;
import com.arge.correosm.providers.AlumnoABookingProvider;
import com.arge.correosm.providers.AuthProvider;
import com.arge.correosm.providers.GeofireProvider;

public class AcceptReceiver extends BroadcastReceiver {
    private AlumnoABookingProvider malumnoABookingProvider;

    private GeofireProvider mGeofireProvider;
    private AuthProvider mAuthProvider;

    @Override
    public void onReceive(Context context, Intent intent) {
        mAuthProvider = new AuthProvider();
        mGeofireProvider = new GeofireProvider("active_alumnoB");
        mGeofireProvider.removeLocation(mAuthProvider.GetID());

        String idAlumnoA = intent.getExtras().getString("idAlumnoA");
        malumnoABookingProvider = new AlumnoABookingProvider();
        malumnoABookingProvider.usdateStatus(idAlumnoA, "accept");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);

        Intent intent1 = new Intent(context, MapAlumnoBBookingActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        intent1.putExtra("idAlumnoA" , idAlumnoA);
        context.startActivity(intent1);
    }
}
