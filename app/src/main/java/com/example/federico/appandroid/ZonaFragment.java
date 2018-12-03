package com.example.federico.appandroid;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;


public class ZonaFragment extends Fragment {




    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
    private DatabaseReference mDatabase;

    private ListView zonas;
    private FirebaseAuth mAuth;
    private FloatingActionButton gomap;

    private EditText buscar;




    ArrayList<String> lista=new ArrayList<>();
    ArrayAdapter<String>adapter;

    private Boolean suscrito=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_zona, container, false);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Zona");
        buscar=(EditText)view.findViewById(R.id.buscarZona);

        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,lista);

        zonas=(ListView)view.findViewById(R.id.zonalistas);
        zonas.setAdapter(adapter);

        gomap= (FloatingActionButton) view.findViewById(R.id.irmapa);


        gomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                darpermisosdeLOCATION();

                Intent intent=new Intent(getActivity().getApplicationContext(),MapsActivity.class);
                startActivity(intent);
            }
        });




        listarZonas();

        recorrerZonas(user);

        buscarzona();





        //subscribirZona();


        return view;
    }

    public void darpermisosdeLOCATION(){
        if(ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        }

    }


    public void suscribirZona(final FirebaseUser user){

        zonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String zona= lista.get(position);
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("SUBSCRIPCION").setMessage("¿Desea susscribirse?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                               DatabaseReference currentZona=mDatabase.child(zona);
                               if(suscrito==false){
                                   currentZona.child("Suscriptores").child(user.getUid()).setValue(user.getEmail());

                                   DatabaseReference mDatabase2= FirebaseDatabase.getInstance().getReference().child("Usuarios");
                                   mDatabase2.child(user.getUid()).child("Zona").setValue(zona);

                                   Toast.makeText(getActivity(),"Suscripto a "+ zona,LENGTH_SHORT).show();
                                   suscrito=true;

                                   Intent intent=new Intent(getActivity(),MapsActivity.class);
                                   startActivity(intent);



                               }else {
                                    Toast.makeText(getActivity(),"No se pudo realizar la suscripcion",LENGTH_SHORT).show();
                                    desuscribirte(user);
                               }

                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(),"Cancelado",LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();


            }
        });
    }

    public void listarZonas(){
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //String valor=dataSnapshot.child("Nombre").getValue(String.class);
                String valor=dataSnapshot.getKey();
                lista.add(valor);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String valor=dataSnapshot.getKey();
                lista.remove(valor);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"error"+databaseError.toString(),LENGTH_SHORT).show();


            }
        });

    }

    public void recorrerZonas(final FirebaseUser user){

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.child("Suscriptores").hasChild(user.getUid())){
                    String zonapertenece=dataSnapshot.getKey();

                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                    builder.setIcon(R.mipmap.ic_launcher)
                            .setTitle("INFORMACION")
                            .setMessage("Ya te encuentas suscripto a  " + zonapertenece)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();

                    Toast.makeText(getActivity(),"Te encuentas suscripto a :"+zonapertenece,LENGTH_SHORT).show();
                    suscrito=true;
                }
                suscribirZona(user);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    suscrito=false;

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void desuscribirte(final FirebaseUser user){
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.child("Suscriptores").hasChild(user.getUid())){
                    final String zonapertenece=dataSnapshot.getKey();

                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                    builder.setIcon(R.mipmap.ic_launcher)
                            .setTitle("INFORMACION")
                            .setMessage("Ya te encuentas suscripto a  " + zonapertenece + " ¿Deseas desuscribirte?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dataSnapshot.child("Suscriptores").child(user.getUid()).getRef().removeValue();

                                    DatabaseReference mDatabase3= FirebaseDatabase.getInstance().getReference().child("Usuarios");
                                    mDatabase3.child(user.getUid()).child("Zona").setValue("");
                                    suscrito=false;
                                    Toast.makeText(getActivity(),"Saliste de "+zonapertenece,LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getActivity(),"Cancelado",LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();


                    suscrito=true;
                }



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                suscrito=false;

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void buscarzona(){

        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

}
