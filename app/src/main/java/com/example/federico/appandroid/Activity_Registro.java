package com.example.federico.appandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Activity_Registro extends AppCompatActivity {

    private EditText nombre;
    private EditText apellido;
    private EditText dni;
    private EditText email;
    private EditText pw;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private Button enviarregistro;
    private Button cancelar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__registro);
        mAuth = FirebaseAuth.getInstance();
        nombre=(EditText)findViewById(R.id.name);
        apellido=(EditText)findViewById(R.id.surname);
        dni=(EditText)findViewById(R.id.documento);
        enviarregistro=(Button) findViewById(R.id.send);
        cancelar=(Button)findViewById(R.id.btn_cancelar);

        mProgress=new ProgressDialog(this);



        enviarregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Activity_Registro.this,MainActivity.class);
                startActivity(intent);
            }
        });





    }

    public void registrar(){

        //Recupero el valor del RadioGroup
        email = (EditText) findViewById(R.id.usuario_txt);
        pw = (EditText) findViewById(R.id.password_txt);

        mProgress.setMessage("Registrando ...");
        mProgress.show();

        mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), pw.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgress.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            mAuth.signInWithEmailAndPassword(email.getText().toString().trim(),pw.getText().toString().trim());
                            FirebaseUser user = mAuth.getCurrentUser();


                            //Guardo los datos en FIREBASE

                            DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("Usuarios");
                            DatabaseReference currentUserDB=mDatabase.child(user.getUid());
                            currentUserDB.child("Nombre").setValue(nombre.getText().toString());
                            currentUserDB.child("Apellido").setValue(apellido.getText().toString());
                            currentUserDB.child("Documento").setValue(dni.getText().toString());
                            currentUserDB.child("Zona").setValue("");
                            currentUserDB.child("Zona").setValue("user");
                            currentUserDB.child("Email").setValue(email.getText().toString());


                            Toast.makeText(Activity_Registro.this, "Registro con exito",
                                    Toast.LENGTH_SHORT).show();




                           Intent intent=new Intent(Activity_Registro.this,IndexActivity.class);

                           intent.putExtra("nombre",nombre.getText().toString());
                           startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Activity_Registro.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

    }










}
