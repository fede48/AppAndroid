package com.example.federico.appandroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.BoringLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Collator;
import java.text.DecimalFormat;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    private DatabaseReference mDatabase;
    private ImageView info;
    private EditText input;
    private EditText input2;
    private ImageButton back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();


        mDatabase = FirebaseDatabase.getInstance().getReference();

        back=(ImageButton) findViewById(R.id.atras);
        info=(ImageView)findViewById(R.id.place_info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Infomacion")
                        .setMessage("Si dessea crear una nueva zona, debe mantener presionado en el lugar donde desa crearla!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                return;
                            }
                        })
                        .show();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MapsActivity.this,IndexActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //LatLng sydney = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);




        getZonas(googleMap);
        //agregarZonas(googleMap);





    }

    public void getZonas(final GoogleMap googleMap){
        final FirebaseUser user = mAuth.getCurrentUser();

        mMap=googleMap;
        mDatabase.child("Zona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                agregarZonas(googleMap);



                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

                    MapaFirebase mp = snapshot1.getValue(MapaFirebase.class);
                    long cant=snapshot1.child("Suscriptores").getChildrenCount();
                    Double latitud = mp.getLatitud();
                    Double longitud = mp.getLongitud();
                    Double radio=mp.getRadio();
                    LatLng city= new LatLng(latitud,longitud);


                    MarkerOptions markerOptions = new MarkerOptions()
                            .title(mp.getNombre()).snippet("cantidad de suscriptores: "+cant);
                    markerOptions.position(city);

                    // con este metodo voy , cuando voy al mapa , voy a la zona a la que estoy suscripto
                    for(DataSnapshot snapshot2: snapshot1.child("Suscriptores").getChildren()){
                        if(snapshot2.getKey().equals(user.getUid())){
                            MapaFirebase mp2=snapshot1.getValue(MapaFirebase.class);
                            LatLng city2=new LatLng(mp2.getLatitud(),mp2.getLongitud());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(city2));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(city2,15),5000,null);
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_foreground));
                        }
                        else {
                             mMap.moveCamera(CameraUpdateFactory.newLatLng(city));
                             mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(city,13),5000,null);

                        }
                    }

                    mMap.addMarker(markerOptions);
                   // mMap.moveCamera(CameraUpdateFactory.newLatLng(city));
                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(city,13),5000,null);
                    mMap.addCircle(new CircleOptions()
                    .center(city)
                    .radius(radio)
                    .strokeWidth(2f)
                    .strokeColor(Color.GRAY)
                    .fillColor(0x550000FF));

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            marker.showInfoWindow();
                            return true;
                        }
                    });


                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }

    public void agregarZonas(GoogleMap googleMap){
        mMap= googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("¿Desea crear una nueva Zona?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                AlertDialog.Builder bil2=new AlertDialog.Builder(MapsActivity.this);
                                bil2.setTitle("Ingrese el nombre de la Zona");
                                input=new EditText(MapsActivity.this);
                                input.setInputType(InputType.TYPE_CLASS_TEXT);
                                input2=new EditText(MapsActivity.this);
                                bil2.setView(input);

                                bil2.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {



                                        if(input.getText().toString().trim().isEmpty()){
                                            Toast.makeText(MapsActivity.this,"Eror, zona vacia",LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                        else {


                                            AlertDialog.Builder bil3 = new AlertDialog.Builder(MapsActivity.this);


                                            bil3.setTitle("Ingrese radio de la zona");
                                            input2.setInputType(InputType.TYPE_CLASS_NUMBER);
                                            bil3.setView(input2);
                                            bil3.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(final DialogInterface dialog, int which) {


                                                    final String radio = input2.getText().toString();
                                                    if (radio.isEmpty()) {

                                                        Toast.makeText(MapsActivity.this, "El radio no puede ir  vacio", LENGTH_SHORT).show();
                                                        dialog.dismiss();

                                                    } else {
                                                        final String nombre = input.getText().toString().trim().replaceAll("\\s", "");
                                                        final DatabaseReference databasezona = mDatabase.child("Zona");
                                                        databasezona.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                final Collator comparador = Collator.getInstance();
                                                                comparador.setStrength(Collator.PRIMARY);
                                                                Double radioconvert = Double.parseDouble(radio);
                                                                boolean repe = false;
                                                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {


                                                                    String zona1 = snapshot1.getKey();
                                                                    if (comparador.equals(zona1, nombre)) {
                                                                        Toast.makeText(MapsActivity.this, "Esta zona ya se encuentra registrada, intente con otro", Toast.LENGTH_LONG).show();
                                                                        repe=true;
                                                                        dialog.dismiss();

                                                                    }


                                                                }
                                                                if (radioconvert >= 200 && radioconvert <= 1000 && !repe) {
                                                                    DatabaseReference database = mDatabase.child("SolicitudZonas");
                                                                    DatabaseReference currentuser = database.child(mAuth.getCurrentUser().getUid());
                                                                    currentuser.child("Solicitante").setValue(mAuth.getCurrentUser().getEmail());
                                                                    currentuser.child("NombreZona").setValue(nombre);
                                                                    currentuser.child("coordenadas").setValue(latLng);
                                                                    currentuser.child("radio").setValue(radioconvert);
                                                                    Toast.makeText(MapsActivity.this, "Has realizado tu peticion de la zona " + nombre + "con exito", LENGTH_SHORT).show();

                                                                    mMap.addMarker(new MarkerOptions().position(latLng));

                                                                    mMap.addCircle(new CircleOptions()
                                                                            .center(latLng).radius(radioconvert).strokeWidth(2f).strokeColor(Color.GRAY).fillColor(0x550000FF));
                                                                } else {
                                                                    Toast.makeText(MapsActivity.this, "El radio no debe ser menor a 200mts", LENGTH_SHORT).show();
                                                                }


                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                    dialog.dismiss();
                                                }
                                            });
                                            bil3.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(MapsActivity.this,"Cancelado",LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            }).show();

                                            dialog.dismiss();


                                        }//DatabaseReference database=mDatabase.child("Zona");
                                        //DatabaseReference currentZona=database.child(nombre);
                                        //currentZona.child("Nombre").setValue(nombre);
                                        //currentZona.child("latitud").setValue(lat);
                                        //currentZona.child("longitud").setValue(lon);


                                    }
                                });
                                bil2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                bil2.show();




                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

    }


}





//}//
