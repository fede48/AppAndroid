package com.example.federico.appandroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.support.v7.widget.Toolbar;
import com.google.firebase.storage.FirebaseStorage;
import android.Manifest;
import android.content.*;
import android.support.v4.app.*;
import android.content.pm.*;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private TextView bienvenida;
    private TextView documetn;
    private TextView residencia;
    private Button cerrar;

    private FusedLocationProviderClient mfuedLocation;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private RecyclerView postList;
    private FrameLayout Container;
    private DatabaseReference mDatabase, PostsRef, LikesRef;
    private  String current_user_id, zonaCurrentUser;
    Boolean LikeChecker = false;


    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // A continuacion busco y devuelvo los datos con los que me registre guardados en la base de datos

        postList = (RecyclerView)v.findViewById(R.id.all_users_post_list);
        Container = (FrameLayout)v.findViewById(R.id.main_container);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        progressDialog = new ProgressDialog(getActivity().getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        current_user_id= mAuth.getCurrentUser().getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {

                    mDatabase.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //bienvenida.setText("Bienvenido "+String.valueOf(dataSnapshot.child("Nombre").getValue())+"!");
                            //documetn.setText("Tu DNI es: "+String.valueOf(dataSnapshot.child("Documento").getValue()));
                            //residencia.setText("Tu residencia es en: "+String.valueOf(dataSnapshot.child("Zona").getValue()));




                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );

                }
                else {
                    startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                    getActivity().getFragmentManager().popBackStack();
                }
            }





        };

        DisplayAllUsersPosts();

        return v;
    }

    private void DisplayAllUsersPosts()
    {

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                zonaCurrentUser = dataSnapshot.child("Usuarios").child(current_user_id).child("Zona").getValue().toString();
                /*
                if(zonaCurrentUser.isEmpty())
                {

                    // setup the alert builder

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("INFORMACION");
                    builder.setMessage("No estas subscripto a ninguna Zona.");

                    // add a button
                    builder.setPositiveButton("OK", null);

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }*/

                Query query = PostsRef.orderByChild("zona").equalTo(zonaCurrentUser);

                FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
                        new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                                (
                                        Posts.class,
                                        R.layout.all_posts_layout,
                                        PostsViewHolder.class,
                                        query
                                ) {

                            @Override
                            protected void populateViewHolder(final PostsViewHolder viewHolder, final Posts model, final int position) {

                                final String PostKey = getRef(position).getKey();
                                viewHolder.setFullname(model.getFullname());
                                viewHolder.setTime(model.getTime());
                                viewHolder.setDate(model.getDate());
                                viewHolder.setDescription(model.getDescription());
                                viewHolder.setPostimage(getActivity().getApplicationContext(), model.getPostimage());

                                viewHolder.setLikeButtonStatus(PostKey);


                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent clickPostIntent = new Intent(getActivity(), ClickPostActivity.class);
                                        clickPostIntent.putExtra("PostKey", PostKey);
                                        startActivity(clickPostIntent);
                                    }
                                });

                                viewHolder.CommentPostButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent commentsPostIntent = new Intent(getActivity(), CommentsActivity.class);
                                        commentsPostIntent.putExtra("PostKey", PostKey);
                                        startActivity(commentsPostIntent);
                                    }
                                });

                                viewHolder.LikePostButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        LikeChecker = true;

                                        LikesRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (LikeChecker.equals(true)) {
                                                    if (dataSnapshot.child(PostKey).hasChild(current_user_id)) {
                                                        LikesRef.child(PostKey).child(current_user_id).removeValue();
                                                        LikeChecker = false;
                                                    } else {
                                                        LikesRef.child(PostKey).child(current_user_id).setValue(true);
                                                        LikeChecker = false;
                                                    }

                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });

                            }
                        };


                    postList.setAdapter(firebaseRecyclerAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        ImageButton LikePostButton, CommentPostButton;
        TextView DisplayNoOfLikes;
        int countLikes;
        String currentUserId;
        DatabaseReference LikesRef;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            LikePostButton = (ImageButton) mView.findViewById(R.id.like_button);
            CommentPostButton = (ImageButton) mView.findViewById(R.id.comment_button);
            DisplayNoOfLikes = (TextView) mView.findViewById(R.id.display_no_of_likes);
            LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }

        public void setLikeButtonStatus(final String PostKey)
        {
            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(PostKey).hasChild(currentUserId))
                    {
                        countLikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.like);
                        DisplayNoOfLikes.setText((Integer.toString(countLikes) + ("Likes")));
                    }
                    else
                    {
                        countLikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.dislike);
                        DisplayNoOfLikes.setText((Integer.toString(countLikes) + ("Likes")));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void setFullname(String fullname)
        {
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setTime(String time)
        {
            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
            PostTime.setText("   "+time);
        }

        public void setDate(String date)
        {
            TextView PostDate= (TextView) mView.findViewById(R.id.post_date);
            PostDate.setText("   "+ date);
        }

        public void setDescription(String description)
        {
            TextView PostDescription= (TextView) mView.findViewById(R.id.post_description);
            PostDescription.setText(description);
        }

        public void setPostimage(Context ctx,String postimage)
        {
            ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(postimage).into(PostImage);

        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
    }


}
