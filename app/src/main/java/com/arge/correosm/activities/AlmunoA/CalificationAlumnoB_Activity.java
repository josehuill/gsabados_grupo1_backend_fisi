package com.arge.correosm.activities.AlmunoA;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.arge.correosm.ActivityReporting;
import com.arge.correosm.R;
import com.google.android.gms.maps.model.LatLng;

public class CalificationAlumnoB_Activity extends AppCompatActivity {

    private Button mbtnCAlification;

    /******/
    private String mExtraOrigen;
    private String mExtraDestination;
    private double mExtraDestinationLat;
    private double mExtraDestinationLng;
    private double mExtraOriginLat;
    private double mExtraOriginLng;
    /******/
    LatLng mOriginLatLng;
    LatLng mDestinationLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calification_alumno_bactivity);

        /***/
        mExtraOrigen = getIntent().getStringExtra("start_address");
        mExtraDestination = getIntent().getStringExtra("end_address");

        mExtraOriginLat= getIntent().getDoubleExtra("origin_lat",0);
        mExtraOriginLng= getIntent().getDoubleExtra("origin_lng",0);

        mExtraDestinationLat= getIntent().getDoubleExtra("destination_lat",0);
        mExtraDestinationLng= getIntent().getDoubleExtra("destination_lng",0);

        mOriginLatLng = new LatLng(mExtraOriginLat, mExtraOriginLng);
        mDestinationLatLng = new LatLng(mExtraDestinationLat, mExtraDestinationLng);
        /***/

        mbtnCAlification = findViewById(R.id.btnCalificationBlumnoB);
        mbtnCAlification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalificationAlumnoB_Activity.this, ActivityReporting.class);
                intent.putExtra("origin_lat" , mOriginLatLng.latitude);
                intent.putExtra("origin_lng" , mOriginLatLng.longitude);

                intent.putExtra("origin" , mOriginLatLng);
                intent.putExtra("destination" , mDestinationLatLng);
                intent.putExtra("destination_lat" , mDestinationLatLng.latitude);
                intent.putExtra("destination_lng" , mDestinationLatLng.longitude);

                intent.putExtra("start_address" , mExtraOrigen);
                intent.putExtra("end_address" , mExtraDestination);
                intent.putExtra("prueba", "holi");
                startActivity(intent);
                finish();
            }
        });
    }
}