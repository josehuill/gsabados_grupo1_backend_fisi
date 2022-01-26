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

import com.arge.correosm.R;
import com.arge.correosm.activities.AlmunoA.RegisterActivity;
import com.arge.correosm.models.AlumnoA;
import com.arge.correosm.providers.AlumnoAprovider;
import com.arge.correosm.providers.AuthProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class Register2Activity extends AppCompatActivity {

    SharedPreferences mPref;

    AuthProvider mAuthProvider;
    AlumnoAprovider mAlumnoAprovider;

    Button mbtnRegister;
    TextInputEditText mTextInputNombre;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    TextInputEditText mtextInputCodigo;
    TextInputEditText mtextInputAddres;

    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);


        mAuthProvider = new AuthProvider();
        mAlumnoAprovider = new AlumnoAprovider();
/*
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

*/
        mDialog = new SpotsDialog.Builder().setContext(Register2Activity.this).setMessage("Guardando").build();

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        String selecterUser= mPref.getString("user", "");

        mTextInputEmail =findViewById(R.id.textInputEmail);
        mTextInputPassword =findViewById(R.id.textInputPassword);
        mtextInputCodigo =findViewById(R.id.textInputCodigo);
        mTextInputNombre =findViewById(R.id.textInputName);
        mtextInputAddres =findViewById(R.id.textInputAddres);
        mbtnRegister = findViewById(R.id.btnRegister2);



        mbtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickRegister();
            }
        });
    }


    void ClickRegister(){
        final String name = mTextInputNombre.getText().toString();
        final String email = mTextInputEmail.getText().toString();
        final String password = mTextInputPassword.getText().toString();
        final String address = mtextInputAddres.getText().toString();
        final String codigo = mtextInputCodigo.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !address.isEmpty() && !codigo.isEmpty()){
            Toast.makeText(Register2Activity.this,"Registro exitoso", Toast.LENGTH_SHORT).show();
            if(password.length()>=5){
                mDialog.show();
                register(name, email, password, address, codigo);
            }else {
                Toast.makeText(this,"La contraseña es muy corta", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Ingrese todos los campos", Toast.LENGTH_SHORT ).show();
        }
        mDialog.dismiss();
    }


    void register(final String name, String email, String password, String address, String codigo){
        mAuthProvider.register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    AlumnoA alumnoA = new AlumnoA(id,name,email,address,codigo);
                    create(alumnoA);
                }else{
                    Toast.makeText(Register2Activity.this,"No se pudo registrar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void create(AlumnoA alumnoA){
        mAlumnoAprovider.create(alumnoA).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Register2Activity.this,"Registro exitoso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register2Activity.this, LoginUniqueActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(Register2Activity.this,"No se pudo registrar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}