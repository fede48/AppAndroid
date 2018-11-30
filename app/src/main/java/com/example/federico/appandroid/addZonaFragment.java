package com.example.federico.appandroid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class addZonaFragment extends Fragment {

    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;


    private FloatingActionButton irmapa;

    private ListView listadepeticiones;


    ArrayList<String> lista=new ArrayList<>();
    ArrayAdapter<String> adapter;
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    SimpleAdapter getAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_add_zona, container, false);

        listadepeticiones=(ListView)v.findViewById(R.id.zonalistass);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("SolicitudZonas");


       // adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,lista);




        getAdapter=new SimpleAdapter(getActivity(), data,
                android.R.layout.simple_list_item_2,
                new String[] {"datos1", "datos2"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
        listadepeticiones.setAdapter(getAdapter);

        irmapa= (FloatingActionButton) v.findViewById(R.id.gomapa);


        irmapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                darpermisosdeLOCATION();

                Intent intent=new Intent(getActivity().getApplicationContext(),MapsActivity.class);
                startActivity(intent);
            }
        });

        listarpeticiones();


        // Inflate the layout for this fragment
        return  v ;
    }

    public void darpermisosdeLOCATION(){
        if(ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        }

    }


    public void listarpeticiones(){
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String zona=dataSnapshot.child("NombreZona").getValue(String.class);
                String user=dataSnapshot.child("Solicitante").getValue(String.class);


                Map<String, String> datum = new HashMap<String, String>(2);
                datum.put("datos1", zona);
                datum.put("datos2", user);

                data.add(datum);


               // lista.add(valor);

                getAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
