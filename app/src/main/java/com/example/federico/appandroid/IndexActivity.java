package com.example.federico.appandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class IndexActivity extends AppCompatActivity {
    private TextView bienvenida;
    private Button cerrar;
    public static final String user="nombre";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        bienvenida=(TextView)findViewById(R.id.msjbienvenida);
        cerrar=(Button)findViewById(R.id.btn_cerrarsesion);

        String user=getIntent().getStringExtra("nombre");
        bienvenida.setText("Bienvnido: "+user+"!");



        //cerrar sesion
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(IndexActivity.this,MainActivity.class));
            }
        });

    }
}
