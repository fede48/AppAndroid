package com.example.federico.appandroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.transform.Result;


public class PublicacionFragment extends Fragment {

    private ImageButton selectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription, PostAddress;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri ImageUri;
    private String Descripcion;
    private StorageReference PostsImageReference;
    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, current_user_id;
    private DatabaseReference UsersRef, PostsRef;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private Spinner PostType;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_publicacion, container, false);

        mAuth = FirebaseAuth.getInstance();
        current_user_id= mAuth.getCurrentUser().getUid();


        UsersRef = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");


        loadingBar = new ProgressDialog(getActivity());
        UpdatePostButton = (Button) v.findViewById(R.id.update_post_button);
        PostDescription = (EditText) v.findViewById(R.id.post_description);
        PostAddress = (EditText) v.findViewById(R.id.post_address);
        selectPostImage =(ImageButton)v.findViewById(R.id.select_post_image);
        PostType = (Spinner) v.findViewById(R.id.post_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.opciones,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        PostType.setAdapter(adapter);

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
            loadingBar.setTitle("Nuevo Post");
            loadingBar.setMessage("Por Favor espere, se esta subiendo un nuevo post..");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

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

        final StorageReference filePath = PostsImageReference.child("Post Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(ImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadUrl = task.getResult().toString();
                    SavingPostInformationToDatabase();

                } else {
                    String message= task.getException().getMessage();
                    Toast.makeText(getActivity(),"Error occured: "+ message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void SendUserToIndexActivity()
    {
        Intent indexIntent = new Intent(getActivity(), IndexActivity.class);
        indexIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(indexIntent);
    }

    private void SavingPostInformationToDatabase()
    {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child("Usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    String UserNombre = dataSnapshot.child(current_user_id).child("Nombre").getValue().toString();
                    String UserApellido = dataSnapshot.child(current_user_id).child("Apellido").getValue().toString();
                    String zona = dataSnapshot.child(current_user_id).child("Zona").getValue().toString();
                    String fullname = UserNombre + " " +UserApellido;

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid",current_user_id);
                    postsMap.put("date",saveCurrentDate);
                    postsMap.put("time",saveCurrentTime);
                    postsMap.put("description",Descripcion);
                    postsMap.put("postimage",downloadUrl);
                    postsMap.put("direccion",PostAddress.getText().toString());
                    postsMap.put("tipo",PostType.getSelectedItem().toString());
                    postsMap.put("fullname", fullname);
                    postsMap.put("zona", zona);

                    PostsRef.child(postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getActivity(),"Post is updated successfully ", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        SendUserToIndexActivity();
                                    }
                                    else
                                    {
                                        Toast.makeText(getActivity(),"Error occured while updating your post", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }

                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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


