package com.example.federico.appandroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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





    private DatabaseReference mDatabase;

    private ListView zonas;
    private FirebaseAuth mAuth;



    ArrayList<String> lista=new ArrayList<>();
    ArrayAdapter<String>adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_zona, container, false);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Zona");

        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,lista);

        zonas=(ListView)view.findViewById(R.id.zonalistas);
        zonas.setAdapter(adapter);




        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //String valor=dataSnapshot.child("Nombre").getValue(String.class);
                String valor=dataSnapshot.getKey();
                lista.add(valor);
                adapter.notifyDataSetChanged();
                subscribirZona(dataSnapshot);

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

        //subscribirZona();


        return view;
    }


    public void subscribirZona(final DataSnapshot dataSnapshot){

        zonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String zona= lista.get(position);
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("SUBSCRIPCION").setMessage("Desea subscribirse?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String d=dataSnapshot.getKey();
                                FirebaseUser user = mAuth.getCurrentUser();
                               DatabaseReference currentUserDB=mDatabase.child(zona);
                               currentUserDB.child("Sub").setValue(user.getUid());

                                Toast.makeText(getActivity(),zona,LENGTH_SHORT).show();
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
}
