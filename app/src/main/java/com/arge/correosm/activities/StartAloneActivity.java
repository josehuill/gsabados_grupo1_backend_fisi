package com.arge.correosm.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arge.correosm.R;
import com.arge.correosm.activities.AlumnoB.CalificationAlumnoA_Activity;
import com.arge.correosm.activities.AlumnoB.MapAlumnoBBookingActivity;
import com.arge.correosm.providers.AlumnoABookingProvider;
import com.arge.correosm.providers.AlumnoAprovider;
import com.arge.correosm.providers.AuthProvider;
import com.arge.correosm.providers.GeofireProvider;
import com.arge.correosm.providers.TokenProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class StartAloneActivity extends AppCompatActivity  implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    boolean isPermisionGranted;
    SupportMapFragment mapView;

    GoogleMap mGoogleMap2;

    private FusedLocationProviderClient mLocationClient;

    private int GPS_REQUEST_CODE = 9010;

    /********/
    private LocationManager ubicacion;
    private AuthProvider mAuthProvider;
    /********/

    private Marker mMarker;

    private boolean isConect = true;

    private LatLng mCurrentLantlong;
    private GeofireProvider mGeofireProvider;

    private TokenProvider mTokenProvider;

    private TextView mTextViewAlumnoABooking;
    private TextView mTextViewEmailAlumnoABooking;

    private String mExtraAlumnoAid;

    private AlumnoAprovider mAlumnoAprovider;

    private AlumnoABookingProvider mAlumnoABookingProvider;


    private String mExtraOrigen;
    private String mExtraDestination;
    private double mExtraDestinationLat;
    private double mExtraDestinationLng;
    private double mExtraOriginLat;
    private double mExtraOriginLng;


    JSONObject jso;

    LatLng mOriginLatLng;
    LatLng mDestinationLatLng;

    private Button mbtnStartBooking;
    private Button mbtnFinishBooking;

    //private LinearLayout startedLayout;
    //private LinearLayout EsperandoLayout;

    private boolean isCloseToAlumnoA = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_alone);

        mTextViewEmailAlumnoABooking = findViewById(R.id.textViewEmailAlumnoABooking);
        mTextViewAlumnoABooking = findViewById(R.id.textViewAlumnoABooking);

        mbtnStartBooking = findViewById(R.id.btnStartBooking);
        mbtnFinishBooking = findViewById(R.id.btnFinishBooking);

        /***/
        //startedLayout = (LinearLayout)this.findViewById(R.id.started);
        //EsperandoLayout = (LinearLayout)this.findViewById(R.id.EsperandoId);
        /***/
        mExtraOrigen = getIntent().getStringExtra("start_address");
        mExtraDestination = getIntent().getStringExtra("end_address");

        mExtraOriginLat= getIntent().getDoubleExtra("origin_lat",0);
        mExtraOriginLng= getIntent().getDoubleExtra("origin_lng",0);

        mExtraDestinationLat= getIntent().getDoubleExtra("destination_lat",0);
        mExtraDestinationLng= getIntent().getDoubleExtra("destination_lng",0);

        mOriginLatLng = new LatLng(mExtraOriginLat, mExtraOriginLng);
        mDestinationLatLng = new LatLng(mExtraDestinationLat, mExtraDestinationLng);

        mAlumnoAprovider = new AlumnoAprovider();
        mAlumnoABookingProvider = new AlumnoABookingProvider();

        mGeofireProvider = new GeofireProvider("alumnoB_working");
        mAuthProvider = new AuthProvider();

        mTokenProvider = new TokenProvider();

        mapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view_respuesta);
        mapView.getMapAsync(this);

        checkMyPermission();

        mLocationClient = new FusedLocationProviderClient(this);

        Localizacion();
        registerUbication();


        if (isPermisionGranted) {
            if (isGPSenable()) {
                mapView.getMapAsync(this);
                mapView.onCreate(savedInstanceState);

            }
        }

        mbtnStartBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCloseToAlumnoA){
                    startBooking();

                }else{
                    Toast.makeText(StartAloneActivity.this,"Debes estar mas cerca para iniciar evento", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mbtnFinishBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finishBooking();
                Intent intent = new Intent(StartAloneActivity.this, CalificationAlumnoA_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        //goToStart();
    }

    public void goToStart(){
        //RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        mGoogleMap2.addMarker(new MarkerOptions().position(mOriginLatLng).title("Inicio").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_star)));
/*
        String baseUrl = "https:/maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                + "origin=" + mCurrentLantlong.latitude+ "," +mCurrentLantlong.longitude + "&"
                + "destination=" + mOriginLatLng.latitude+ "," +mOriginLatLng.longitude+ "&"
                //  + "departure_time" + (new Date().getTime()) + (60*60*1000)+  "&"
                //  + "traffic_model=best_guess&"
                + "key=AIzaSyA5s1KOmTEPdWPZJ1A97-22KgdL68yM-BQ";
        String url = baseUrl+query;

        System.out.println("url MapsAc: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    jso = new JSONObject(response);
                    trazarRuta(jso);

                } catch (JSONException e) {
                    System.out.println("errorJSONruta "+e);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
        */
    }

    public void startBooking(){
        //mAlumnoABookingProvider.usdateStatus(mExtraAlumnoAid, "start");
        mbtnStartBooking.setVisibility(View.GONE);
        mbtnFinishBooking.setVisibility(View.VISIBLE);

        /***/
        //EsperandoLayout.setVisibility(LinearLayout.GONE);
        /***/

        mGoogleMap2.clear();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        mGoogleMap2.addMarker(new MarkerOptions().position(mCurrentLantlong).title("Inicio").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_star)));
        mGoogleMap2.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Meta").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_meta_finish)));


        String baseUrl = "https:/maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                + "origin=" + mCurrentLantlong.latitude+ "," +mCurrentLantlong.longitude + "&"
                + "destination=" + mDestinationLatLng.latitude+ "," +mDestinationLatLng.longitude+ "&"
                //  + "departure_time" + (new Date().getTime()) + (60*60*1000)+  "&"
                //  + "traffic_model=best_guess&"
                + "key=AIzaSyA5s1KOmTEPdWPZJ1A97-22KgdL68yM-BQ";
        String url = baseUrl+query;

        System.out.println("url MapsAc: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    jso = new JSONObject(response);
                    trazarRuta(jso);

                } catch (JSONException e) {
                    System.out.println("errorJSONruta "+e);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }



    public double getDistanceBetwen(LatLng alumnoALatLng, LatLng alumnoBLatLng){
        double distance = 0;
        Location alumnoALocation = new Location("");
        Location alumnoBLocation = new Location("");
        alumnoALocation.setLatitude(alumnoALatLng.latitude);
        alumnoALocation.setLongitude(alumnoALatLng.longitude);
        alumnoBLocation.setLatitude(alumnoBLatLng.latitude);
        alumnoBLocation.setLongitude(alumnoBLatLng.longitude);
        distance = alumnoALocation.distanceTo(alumnoBLocation);

        return distance;
    }

    private boolean isGPSenable() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnable) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("holi")
                    .setMessage("sda")
                    .setPositiveButton("Si", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();
        }
        return false;
    }

    private void registerUbication() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new StartAloneActivity.miLocationListener());
    }

    @SuppressLint("MissingPermission")
    public void getCurrLoc() {

        mLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Location location = task.getResult();

                mCurrentLantlong = new LatLng(location.getLatitude(), location.getLongitude());

                updateLocation(location.getLatitude(), location.getLongitude());


                gotoLocation(location.getLatitude(), location.getLongitude());
            }
        });


    }
    /*
    private void updateLocation(){
       // mGeofireProvider.saveLocation(mAuthProvider.GetID(), double latitude, double longitude);
    }*/

    private void updateLocation(double latitude, double longitude){
        mGeofireProvider.saveLocation(mAuthProvider.GetID(), latitude, longitude);

        if (!isCloseToAlumnoA){
            if (mOriginLatLng != null && mCurrentLantlong != null){
                double distance = getDistanceBetwen(mOriginLatLng, mCurrentLantlong);
                if (distance <= 200){
                    mbtnStartBooking.setEnabled(true);
                    isCloseToAlumnoA= true;
                    Toast.makeText(this,"Estas cerca del punto de Inicio", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void gotoLocation(double latitude, double longitude) {
        LatLng LatLng = new LatLng(latitude, longitude);
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng, 15f);
        //mGoogleMap2.moveCamera(cameraUpdate);
        mGoogleMap2.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (mMarker != null){
            mMarker.remove();
        }

        mMarker = mGoogleMap2.addMarker(new MarkerOptions().position(
                new LatLng(latitude,longitude)
                )
                        .title("Tu posicion")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mapa))
        );

        mGoogleMap2.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(LatLng)
                        .zoom(14f)
                        .build()
        ));


        mMarker = mGoogleMap2.addMarker(new MarkerOptions().position(
                new LatLng(mOriginLatLng.latitude,mOriginLatLng.longitude)
                )
                        .title("Punto de inicio")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_star))
        );

    }

    private void disconect(){

        mGeofireProvider.removeLocation(mAuthProvider.GetID());
        isConect = false;
    }


    private void Localizacion() {

        Toast.makeText(StartAloneActivity.this, "localizacion", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1000);
        }
        ubicacion = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //OBTENEMOS la ultima posicion de la ubicacion actual
        Location log = ubicacion.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (ubicacion != null) {
            Toast.makeText(StartAloneActivity.this, "ir a get Currloc", Toast.LENGTH_SHORT).show();
            getCurrLoc();
        }

    }


    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    Toast.makeText(StartAloneActivity.this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                isPermisionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GPS_REQUEST_CODE){
            LocationManager locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);

            System.out.println("Estoy en location Manager");

            Boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if(providerEnable){
                Toast.makeText(this, "GPS activo", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this, "GPS no activo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap2 = googleMap;
        //mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap2.getUiSettings().setZoomControlsEnabled(true);



    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        //Toast.makeText(map_alumnoB.this, "movido", Toast.LENGTH_SHORT).show();
        //Localizacion();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /******/
    private class miLocationListener implements LocationListener {
        /**********Este metodo actualiza mi posicion cada segundo********/
        @Override
        public void onLocationChanged(@NonNull Location location) {

            //System.out.println("Se cambio la ubicaicon");
            //Toast.makeText(map_alumnoB.this, "nueva pisiccion", Toast.LENGTH_SHORT).show();

            if(isConect){
                getCurrLoc();
            }

        }

        @Override
        public void onLocationChanged(@NonNull List<Location> locations) {

        }

        @Override
        public void onFlushComplete(int requestCode) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    }

    public void generateToken(){
        mTokenProvider.create(mAuthProvider.GetID());
    }

    public void trazarRuta(JSONObject jso){
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {
            jRoutes = jso.getJSONArray("routes");
            for (int i=0; i<jRoutes.length();i++){

                jLegs = ((JSONObject)(jRoutes.get(i))).getJSONArray("legs");

                for (int j=0; j<jLegs.length();j++){

                    jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");

                    for (int k = 0; k<jSteps.length();k++){

                        String polyline = ""+((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        Log.i("end",""+polyline);
                        List<LatLng> list = PolyUtil.decode(polyline);
                        mGoogleMap2.addPolyline(new PolylineOptions().addAll(list).color(Color.GRAY).width(10).jointType(JointType.ROUND));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}