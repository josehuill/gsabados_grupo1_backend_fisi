package com.arge.correosm.activities.AlumnoB;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.arge.correosm.HomeActivity;
import com.arge.correosm.R;

public class CalificationAlumnoA_Activity extends AppCompatActivity {

    private Button mbtnCalificationAlumnoA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calification_alumno_aactivity);

        mbtnCalificationAlumnoA = findViewById(R.id.btnCalificationAlumnoA);

        mbtnCalificationAlumnoA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalificationAlumnoA_Activity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}