package com.gps.ludke;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gps.ludke.ui.venda.VendaFragment;

public class VendaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.venda_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, VendaFragment.newInstance())
                    .commitNow();
        }
    }
}
