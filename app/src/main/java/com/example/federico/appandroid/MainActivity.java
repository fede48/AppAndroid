package com.example.federico.appandroid;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.support.annotation.NonNull;

public class MainActivity extends AppCompatActivity {


    private Button btn_aceptar;
    private Button btn_login;
    private EditText usuario_nombre;
    private EditText usuario_password;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private CheckBox mCheckBoxRemerber;
    private SharedPreferences mPrefs;
    private static final String PREFS_NAME = "PrefsFile";


    // Ejecuta al iniciar si estas logueado
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btn_login=(Button)findViewById(R.id.btn_login);
        btn_aceptar = findViewById(R.id.btn_aceptar);
        usuario_nombre = (EditText) findViewById(R.id.usuario_txt);
        usuario_password = (EditText) findViewById(R.id.password_txt);
        mCheckBoxRemerber = (CheckBox) findViewById(R.id.checkboxRememberMe);

        mProgress=new ProgressDialog(this);

        getPreferencesData();
        // Metodo para registrarte
        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,Activity_Registro.class));

                //openActivity();
            }
        });


        // Metodo para logiarte
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loguearse();
            }
        });

        //Listener de estado de coneccion
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() !=null){
                    Toast.makeText(MainActivity.this,"Estas Logueado en la cuenta : " +firebaseAuth.getCurrentUser().getEmail(),Toast.LENGTH_SHORT).show();

                    //CHEQIEA SI EL USUARIO ES ADM O USER Y LO REEDIRIGE A SU RESPECTIVA ACTIVIDAD
                    mDatabase.child("Usuarios").child(firebaseAuth.getCurrentUser().getUid()).child("Rol").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue(String.class).equals("adm")) {
                                Intent intent = new Intent(MainActivity.this,Indexadm.class);
                                startActivity(intent);
                                finish();

                            }
                            else {
                                Intent intent = new Intent(MainActivity.this, IndexActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });





                }

            }
        };



    }

    private void getPreferencesData()
    {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        if (sp.contains("pref_name"))
        {
            String u = sp.getString("pref_name","not found.");
            usuario_nombre.setText(u.toString());
            //usuario_password.setText();
        }
        if (sp.contains("pref_pass"))
        {
            String p = sp.getString("pref_pass","not found.");
            usuario_password.setText(p.toString());
        }
        if (sp.contains("pref_check"))
        {
            Boolean b = sp.getBoolean("pref_check",false);
            mCheckBoxRemerber.setChecked(b);
        }
    }


    // TRASLADE EL METODO REGISTRAR A OTRA ACTIVIDAD(ACTIVITY_REGISTRO) , PARA COMPLETAR MAS DATOS al momentode resgistrarse
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
                            Intent intent=new Intent(MainActivity.this,Activity_Registro.class);

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
        final String usuario=usuario_nombre.getText().toString().trim();
        final String pass=usuario_password.getText().toString().trim();


        if (!TextUtils.isEmpty(usuario) && !TextUtils.isEmpty(pass)) {

            mProgress.setMessage("Iniciando sesion...");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(usuario, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgress.dismiss();
                            if (task.isSuccessful()) {

                                if (mCheckBoxRemerber.isChecked())
                                {
                                    Boolean boolIsChecked =  mCheckBoxRemerber.isChecked();
                                    SharedPreferences.Editor editor = mPrefs.edit();
                                    editor.putString("pref_name",usuario);
                                    editor.putString("pref_pass",pass);
                                    editor.putBoolean("pref_check", boolIsChecked);
                                    editor.apply();
                                }
                                else
                                {
                                    mPrefs.edit().clear().apply();
                                }

                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();

                                Toast.makeText(MainActivity.this, "Bienvenido: " + usuario,
                                        Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(MainActivity.this, IndexActivity.class);
                                //intent.putExtra("email",usuario_nombre.getText().toString());
                                startActivity(intent);

                                usuario_nombre.getText().clear();
                                usuario_password.getText().clear();


                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();

                            }

                            // ...
                        }
                    });
        }//else {
            //Toast.makeText(this,"Error, algun campo faltante o incorrecto",Toast.LENGTH_SHORT).show();
       // }
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

        if (id == R.id.camara) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
