package com.example.federico.appandroid;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import android.support.annotation.NonNull;

public class MainActivity extends AppCompatActivity {


    private Button btn_aceptar;
    private Button btn_login;
    private EditText usuario_nombre;
    private EditText usuario_password;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        btn_login=(Button)findViewById(R.id.btn_login);
        btn_aceptar = findViewById(R.id.btn_aceptar);

        // Metodo para registrarte
        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openActivity();
            }
        });


        // Metodo para logiarte
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loguearse();
            }
        });
    }


    public void openActivity()
    {

        //Recupero el valor del RadioGroup
        usuario_nombre = (EditText) findViewById(R.id.usuario_txt);
        usuario_password = (EditText) findViewById(R.id.password_txt);

        mAuth.createUserWithEmailAndPassword(usuario_nombre.getText().toString(), usuario_password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Registro con exito",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(MainActivity.this,IndexActivity.class);

                            intent.putExtra("nombre",usuario_nombre.getText().toString());
                            startActivity(intent);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });


        //String[] user_array = {usuario_nombre.getText().toString(),usuario_password.getText().toString()};
        //abro la otra actividad
        //Intent intent = new Intent(this, IndexActivity.class);
        //intent.putExtra("usuario", user_array);
        //startActivity(intent);
    }



    public void loguearse(){
        usuario_nombre = (EditText) findViewById(R.id.usuario_txt);
        usuario_password = (EditText) findViewById(R.id.password_txt);

        mAuth.signInWithEmailAndPassword(usuario_nombre.getText().toString(), usuario_password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Bienvenido: "+usuario_nombre.getText().toString(),
                                    Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(MainActivity.this,IndexActivity.class);
                            intent.putExtra("nombre",usuario_nombre.getText().toString());
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
