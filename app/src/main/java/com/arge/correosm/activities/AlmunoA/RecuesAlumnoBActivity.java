package com.arge.correosm.activities.AlmunoA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arge.correosm.R;
import com.arge.correosm.map_alumnoA;
import com.arge.correosm.models.AlumnoABooking;
import com.arge.correosm.models.FCMBody;
import com.arge.correosm.models.FCMRResponse;
import com.arge.correosm.providers.AlumnoABookingProvider;
import com.arge.correosm.providers.AuthProvider;
import com.arge.correosm.providers.GeofireProvider;
import com.arge.correosm.providers.NotificationProvider;
import com.arge.correosm.providers.TokenProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecuesAlumnoBActivity extends AppCompatActivity {

    private LottieAnimationView mAnimation;
    private TextView mTextViewLookinFor;
    private Button mButtonCancelRequest;
    private GeofireProvider mGeoFireProvider;
    private double mExtraOriginLat;
    private double mExtraOriginLng;
    private LatLng mOriginLatLng;

    private LatLng mDestinationLatLng;

    private double mRadius = 0.1;
    private boolean mDriverFound = false;
    private String mIdAlumnoBFound = "";
    private LatLng mAlumnoBFoundLatLng;

    private NotificationProvider mNotificationProvider;
    private TokenProvider mTokenProvier;

    private AlumnoABookingProvider mAlumnoABookingProvider;
    private AuthProvider mAuthProvider;

    private String mExtraOrigen;
    private String mExtraDestination;
    private double mExtraDestinationLat;
    private double mExtraDestinationLng;
    JSONObject jso;

    private ValueEventListener mListener;

    private String start_address;
    private String end_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recues_alumno_bactivity);

        mButtonCancelRequest = findViewById(R.id.btnCancelRequest);
        mTextViewLookinFor = findViewById(R.id.textViewLookkingFor);

        mExtraOrigen = getIntent().getStringExtra("start_address");
        mExtraDestination = getIntent().getStringExtra("end_address");

        mExtraOriginLat= getIntent().getDoubleExtra("origin_lat",0);
        mExtraOriginLng= getIntent().getDoubleExtra("origin_lng",0);

        mExtraDestinationLat= getIntent().getDoubleExtra("destination_lat",0);
        mExtraDestinationLng= getIntent().getDoubleExtra("destination_lng",0);

        mOriginLatLng = new LatLng(mExtraOriginLat, mExtraOriginLng);
        mDestinationLatLng = new LatLng(mExtraDestinationLat, mExtraDestinationLng);

        mNotificationProvider = new NotificationProvider();
        mTokenProvier = new TokenProvider();

        mAlumnoABookingProvider = new AlumnoABookingProvider();
        mAuthProvider = new AuthProvider();


        mGeoFireProvider = new GeofireProvider("active_alumnoB");
        getClosesAlumnosB();
    }

    private void getClosesAlumnosB(){

        mGeoFireProvider.getActiveAlumnoB(mOriginLatLng.latitude, mOriginLatLng.longitude , mRadius).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if(!mDriverFound){
                    mDriverFound= true ;
                    mIdAlumnoBFound = key;
                    mAlumnoBFoundLatLng = new LatLng(location.latitude, location.longitude);
                    mTextViewLookinFor.setText("CORREDOR ENCONTRADO\nESPERANDO RESPUESTA");

                    // lo quiete de aqui y lo pase a createAlumnoBooking
                    // sendNotification();

                    createAlumnoBooking();

                    Log.d("AlumoB ", "ID: "+mIdAlumnoBFound);
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //Iingrea al terminar la busqueda en un radio de 0.1 km

                if(!mDriverFound){
                    mRadius = mRadius + 0.1f;
                    if(mRadius > 10){
                        mTextViewLookinFor.setText("NO SE ECONTRÓ UN CORREDOR");
                        Toast.makeText(RecuesAlumnoBActivity.this, "NO SE ENCONTRÓ UN CORREDOR", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        getClosesAlumnosB();
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void createAlumnoBooking(){

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String baseUrl = "https:/maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                + "origin=" + mExtraOriginLat+ "," +mExtraOriginLng + "&"
                + "destination=" + mAlumnoBFoundLatLng.latitude+ "," +mAlumnoBFoundLatLng.longitude+ "&"
                //  + "departure_time" + (new Date().getTime()) + (60*60*1000)+  "&"
                //  + "traffic_model=best_guess&"
                + "key=AIzaSyA5s1KOmTEPdWPZJ1A97-22KgdL68yM-BQ";
        String url = baseUrl+query;

        System.out.println("url MapsAc rec: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    jso = new JSONObject(response);
                   /* JSONArray jsonArray = jso.getJSONArray("routes");
                    JSONObject routes = jsonArray.getJSONObject(0);
                    JSONObject polyline = routes.getJSONObject("overview_polyline");
                    String points = polyline.getString("points");
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("text");

                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");
                    */

                    getDistanceAndDuration(jso);

                   // sendNotification(durationText, distanceText);



                } catch (JSONException e) {
                    System.out.println("errorJSONruta "+e);
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);

    }

    public void getDistanceAndDuration(JSONObject jso){
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        JSONArray distanceJson;
        try {
            jRoutes = jso.getJSONArray("routes");
            for (int i=0; i<jRoutes.length();i++){

                jLegs = ((JSONObject)(jRoutes.get(i))).getJSONArray("legs");

                for (int j=0; j<jLegs.length();j++){

                        String distance = ""+((JSONObject)((JSONObject)jLegs.get(j)).get("distance")).get("text");
                        String duration = ""+((JSONObject)((JSONObject)jLegs.get(j)).get("duration")).get("text");

                        start_address = ""+((JSONObject) jLegs.get(j)).get("start_address");
                        end_address = ""+((JSONObject) jLegs.get(j)).get("end_address");

                        sendNotification(duration, distance);

                        Log.i("distance",""+distance);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(final String time,final String km){

        mTokenProvier.getToken(mIdAlumnoBFound).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String token = snapshot.child("token").getValue().toString();
                    Map<String, String> map = new HashMap<>();
                    map.put("title", "SOLICITUD DE EVENTO A "+ time + " DE TU POSICION" );
                    map.put("body", "Un alumno desea iniciar un evento a "+ km +" de tu posición"+ "\n"+
                            "Inicio : "+ mExtraOrigen+ "\n"+
                            "Destino: "+mExtraDestination);
                    map.put("idAlumnoA", mAuthProvider.GetID());
                    FCMBody fcmBody = new FCMBody(token, "high", map);
                    mNotificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMRResponse>() {
                        @Override
                        public void onResponse(Call<FCMRResponse> call, Response<FCMRResponse> response) {
                            if(response.body() != null){
                                if (response.body().getSuccess() == 1){
                                    /***/
                                    AlumnoABooking alumnoABooking = new AlumnoABooking(
                                            mAuthProvider.GetID(),
                                            mIdAlumnoBFound,
                                            mExtraDestination,
                                            mExtraOrigen,
                                            time,
                                            km,
                                            "create",
                                            mExtraOriginLat,
                                            mExtraOriginLng,
                                            mExtraDestinationLat,
                                            mExtraDestinationLng

                                    );

                                    mAlumnoABookingProvider.create(alumnoABooking).addOnSuccessListener(new OnSuccessListener<Void>() {



                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            CheckStatusAlumnoBBooking();
                                            Toast.makeText(RecuesAlumnoBActivity.this, "La peticion se creo exitosamente", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    /****/

                                   // Toast.makeText(RecuesAlumnoBActivity.this, "La notificaión se ha enviado correctamente", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(RecuesAlumnoBActivity.this, "No se puso enviar la notificación", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMRResponse> call, Throwable t) {
                            Log.d("Error", "Error " + t.getMessage());
                        }
                    });
                }else{
                    Toast.makeText(RecuesAlumnoBActivity.this, "No se puso enviar la notificación porque el usuario no tiene token", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CheckStatusAlumnoBBooking(){
        mListener = mAlumnoABookingProvider.getStatus(mAuthProvider.GetID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String status = snapshot.getValue().toString();
                    if(status.equals("accept")){
                        Intent intent= new Intent(RecuesAlumnoBActivity.this, MapAlunoABookingActivity.class);
                        /***/
                        intent.putExtra("origin_lat" , mOriginLatLng.latitude);
                        intent.putExtra("origin_lng" , mOriginLatLng.longitude);

                        intent.putExtra("origin" , mOriginLatLng);
                        intent.putExtra("destination" , mDestinationLatLng);
                        intent.putExtra("destination_lat" , mDestinationLatLng.latitude);
                        intent.putExtra("destination_lng" , mDestinationLatLng.longitude);

                        intent.putExtra("start_address" , start_address);
                        intent.putExtra("end_address" , end_address);

                        /***/
                        startActivity(intent);
                        finish();
                    }else if(status.equals("cancel")){
                        Toast.makeText(RecuesAlumnoBActivity.this, "El corredor no acepto el evento", Toast.LENGTH_SHORT);
                        Intent intent= new Intent(RecuesAlumnoBActivity.this, map_alumnoA.class);

                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener != null){
            mAlumnoABookingProvider.getStatus(mAuthProvider.GetID()).removeEventListener(mListener);
        }
    }
}














