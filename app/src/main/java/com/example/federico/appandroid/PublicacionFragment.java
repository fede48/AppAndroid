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
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.*;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.transform.Result;


public class PublicacionFragment extends Fragment {

    private ImageButton selectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri ImageUri;
    private String Descripcion;
    private StorageReference PostsImageReference;
    private String saveCurrentDate, saveCurrentTime, postRandomName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_publicacion, container, false);

        UpdatePostButton = (Button) v.findViewById(R.id.update_post_button);
        PostDescription = (EditText) v.findViewById(R.id.post_description);
        selectPostImage =(ImageButton)v.findViewById(R.id.select_post_image);
        PostsImageReference = FirebaseStorage.getInstance().getReference();

        selectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInfo();
            }
        });


        return v;
    }

    private void ValidatePostInfo()
    {
        Descripcion = PostDescription.getText().toString();

        if (ImageUri ==null)
        {
            Toast.makeText(getActivity(),"Por favor seleccione una imagen...", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(Descripcion))
        {
            Toast.makeText(getActivity(),"Ingrese alguna descripcion...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoringImageToFirebaseStorage();
        }


    }

    private void StoringImageToFirebaseStorage()
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = PostsImageReference.child("Post Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");
        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(getActivity(),"Image uploaded successfully to Storage...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String message= task.getException().getMessage();
                    Toast.makeText(getActivity(),"Error occured: "+ message, Toast.LENGTH_SHORT).show();
                }
            }
        });
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


