package com.arge.correosm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class perfil_Activity extends AppCompatActivity {

    ImageButton home;
    ImageButton evento;
    ImageButton perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        home = (ImageButton) findViewById(R.id.id_home);
        evento = (ImageButton) findViewById(R.id.id_evento);
        perfil = (ImageButton) findViewById(R.id.id_perfil);

        evento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(perfil_Activity.this, map_alumnoB.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(perfil_Activity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}