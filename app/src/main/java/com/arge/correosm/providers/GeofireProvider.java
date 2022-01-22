package com.arge.correosm.providers;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.libraries.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.PrivateKey;

public class GeofireProvider {

    private DatabaseReference mDataBase;
    private GeoFire mGeofire;
    private  LatLng latLng;

    public GeofireProvider(String reference){
        mDataBase = FirebaseDatabase.getInstance().getReference().child(reference);
        mGeofire = new GeoFire(mDataBase);
    }

    public void saveLocation(String idAlumnoB, double latitude, double longitude){
        mGeofire.setLocation(idAlumnoB, new GeoLocation(latitude, longitude));
    }

    public void removeLocation(String idAlumnoB){
        mGeofire.removeLocation(idAlumnoB);
    }

    public GeoQuery getActiveAlumnoB(double latitude, double longitude, double radius){
        GeoQuery geoQuery = mGeofire.queryAtLocation(new GeoLocation(latitude, longitude), radius);
        geoQuery.removeAllListeners();
        return geoQuery;
    }

    public DatabaseReference getAlumnoBLication(String idALumnoB){
        return mDataBase.child(idALumnoB).child("l");
    }

    public DatabaseReference isAlumnoBworking(String idAlumnoB){
        return FirebaseDatabase.getInstance().getReference().child("alumnoB_working").child(idAlumnoB);
    }
}
