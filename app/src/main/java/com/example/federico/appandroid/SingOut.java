package com.example.federico.appandroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;


public class SingOut extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_sing_out, container, false);


        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity().getApplicationContext(),MainActivity.class));






        return v;
    }


}
