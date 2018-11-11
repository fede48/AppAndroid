package com.example.federico.appandroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import android.Manifest;

public class IndexActivity extends AppCompatActivity {
    private TextView bienvenida;
    private TextView documetn;
    private TextView residencia;
    private Button cerrar;
    private Button gomap;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    private DatabaseReference mDatabase;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;

    private FusedLocationProviderClient mfuedLocation;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bienvenida=(TextView)findViewById(R.id.msjbienvenida);
        documetn=(TextView)findViewById(R.id.dni);
        residencia=(TextView)findViewById(R.id.localidad);
        gomap=(Button)findViewById(R.id.irmapa);
        cerrar=(Button)findViewById(R.id.btn_cerrarsesion);



        gomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                darpermisosdeLOCATION();

                Intent intent=new Intent(IndexActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });

        //cerrar sesion
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(IndexActivity.this,MainActivity.class));
            }
        });

        // A continuacion busco y devuelvo los datos con los que me registre guardados en la base de datos

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios");
                    mDatabase.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            bienvenida.setText("Bienvenido "+String.valueOf(dataSnapshot.child("Nombre").getValue())+"!");
                            documetn.setText("Tu DNI es: "+String.valueOf(dataSnapshot.child("Documento").getValue()));
                            residencia.setText("Tu residencia es en: "+String.valueOf(dataSnapshot.child("Zona").getValue()));




                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );

                }
                else {
                    startActivity(new Intent(IndexActivity.this, MainActivity.class));
                    finish();
                }
            }





        };



    }



    public void darpermisosdeLOCATION(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(IndexActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}


