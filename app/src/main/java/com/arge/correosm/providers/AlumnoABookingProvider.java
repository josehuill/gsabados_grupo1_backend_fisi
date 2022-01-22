package com.arge.correosm.providers;

import com.arge.correosm.models.AlumnoABooking;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AlumnoABookingProvider {
    private DatabaseReference mDatabase;

    public AlumnoABookingProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ClientBooking");
    }

    public Task<Void> create(AlumnoABooking alumnoABooking) {
        return mDatabase.child(alumnoABooking.getIdClient()).setValue(alumnoABooking);
    }

    public Task<Void> usdateStatus(String idAlumnoBooking, String status){
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        return mDatabase.child(idAlumnoBooking).updateChildren(map);
    }

    public DatabaseReference getStatus(String idAlumnoBooking){
        return mDatabase.child(idAlumnoBooking).child("status");
    }

    public DatabaseReference getAlumnoABooking(String idAlumnoBooking){
        return mDatabase.child(idAlumnoBooking);
    }

}
