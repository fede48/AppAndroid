package com.example.federico.appandroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


public class PublicacionFragment extends Fragment {

    private ImageButton selectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;
    private final static  int Gallery_Pick = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_publicacion, container, false);

        selectPostImage =(ImageButton)v.findViewById(R.id.select_post_image);
        UpdatePostButton = (Button) v.findViewById(R.id.update_post_button);
        PostDescription = (EditText) v.findViewById(R.id.post_description);

        selectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        return v;
    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_Pick);

    }

}


