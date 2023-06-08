package com.example.gan.mywoa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SuksesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sukses);

        TextView nama;
        nama = (TextView) findViewById(R.id.detail);
        Bundle b = getIntent().getExtras();
        String kode = b.getString("kode");
        nama.setText(kode);
    }
}
