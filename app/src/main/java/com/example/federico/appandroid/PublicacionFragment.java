package com.example.federico.appandroid;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.*;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Result;


public class PublicacionFragment extends Fragment {

    private ImageButton selectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri ImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_publicacion, container, false);

        UpdatePostButton = (Button) v.findViewById(R.id.update_post_button);
        PostDescription = (EditText) v.findViewById(R.id.post_description);
        selectPostImage =(ImageButton)v.findViewById(R.id.select_post_image);

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
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,PICK_IMAGE_REQUEST);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data != null) {

            ImageUri = data.getData();
            try {
                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), ImageUri);
                selectPostImage.setImageBitmap(bitmapImage);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }




}


