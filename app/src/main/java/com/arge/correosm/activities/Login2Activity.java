package com.arge.correosm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.arge.correosm.HomeActivity;
import com.arge.correosm.R;
import com.arge.correosm.activity_barra;
import com.arge.correosm.map_alumnoA;
import com.arge.correosm.map_alumnoB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class Login2Activity extends AppCompatActivity {

    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mButtonLogin;

    AlertDialog mDialog;


    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mButtonLogin = findViewById(R.id.btnLogin);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        mAuth =  FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDialog = new SpotsDialog.Builder().setContext(Login2Activity.this).setMessage("Ingresando").build();
    }

    private void login() {
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        Pattern patron = Pattern.compile("[a-zA-Z0-9_-]+@[a-zA-Z.]+\\.[a-zA-Z]+");
        Matcher mat = patron.matcher(email);
        boolean cumplePatron = mat.find();

        if(!email.isEmpty() && !password.isEmpty()){
            if(password.length() >= 5){
                mDialog.show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login2Activity.this, "El login se realizo correctamente", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Login2Activity.this, HomeActivity.class);
                            startActivity(intent);

                        }else{
                            Toast.makeText(Login2Activity.this, "La contraseña o el password son incorrectos", Toast.LENGTH_SHORT).show();
                        }
                        mDialog.dismiss();
                    }
                });
            }else{
                Toast.makeText(Login2Activity.this, "La contraseña tienen mas de 5 caracteres", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(Login2Activity.this, "Complete los campos", Toast.LENGTH_SHORT).show();

        }
    }
}