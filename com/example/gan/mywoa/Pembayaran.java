package com.example.gan.mywoa;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class Pembayaran extends AppCompatActivity {

    TextView nama;
    TextView titlex,tagx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nama = (TextView) findViewById(R.id.kode);
        titlex = (TextView) findViewById(R.id.textviewtitle);
        tagx = (TextView) findViewById(R.id.textViewtag);
        Bundle b = getIntent().getExtras();
        String kode = b.getString("kode");
        String title = b.getString("title");
        String tag = b.getString("tag");
        nama.setText(kode);
        titlex.setText(title);
        tagx.setText(tag);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
