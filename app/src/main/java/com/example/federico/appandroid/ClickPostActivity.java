package com.example.federico.appandroid;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    private ImageView PostImage;
    private TextView PostDescription,PostAddress;
    private Button DeletePostButton, EditPostButtom;
    private  String PostKey, currentUserID, databaseUserID,description,image, direccion,tipo;
    private DatabaseReference ClickPostRef;
    private FirebaseAuth mAuth;
    private Spinner PostType;
    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);

        PostImage = (ImageView)findViewById(R.id.click_post_image);
        PostDescription = (TextView)findViewById(R.id.click_post_description);
        PostAddress = (TextView)findViewById(R.id.click_post_address);

        PostType = (Spinner)findViewById(R.id.click_post_type);
        adapter = ArrayAdapter.createFromResource(this,R.array.opciones,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        PostType.setAdapter(adapter);
        PostType.setEnabled(false);


        DeletePostButton = (Button) findViewById(R.id.eliminar_post_button);
        EditPostButtom = (Button) findViewById(R.id.editar_post_button);

        DeletePostButton.setVisibility(View.INVISIBLE);
        EditPostButtom.setVisibility(View.INVISIBLE);

        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if (dataSnapshot.exists())
                {
                    description = dataSnapshot.child("description").getValue().toString();
                    direccion = dataSnapshot.child("direccion").getValue().toString();
                    tipo = dataSnapshot.child("tipo").getValue().toString();
                    image = dataSnapshot.child("postimage").getValue().toString();
                    databaseUserID = dataSnapshot.child("uid").getValue().toString();


                    PostDescription.setText(description);
                    PostAddress.setText(direccion);
                    int spinnerPosition = adapter.getPosition(tipo);
                    PostType.setSelection(spinnerPosition);


                    Picasso.with(ClickPostActivity.this).load(image).into(PostImage);

                    if (currentUserID.equals(databaseUserID)) {
                        DeletePostButton.setVisibility(View.VISIBLE);
                        EditPostButtom.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DeletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DeleteCurrentPost();

            }
        });

    }

    private void DeleteCurrentPost()
    {
        ClickPostRef.removeValue();
        SendUserToIndexActivity();
        Toast.makeText(this, "Has eliminado un post", Toast.LENGTH_SHORT).show();

    }

    private void SendUserToIndexActivity()
    {
        Intent indexIntent = new Intent(ClickPostActivity.this, IndexActivity.class);
        indexIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(indexIntent);
        finish();
    }
}
