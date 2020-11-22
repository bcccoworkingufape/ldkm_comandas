package com.gps.ludke;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gps.ludke.ui.relatoriocliente.RelatorioClienteFragment;

public class RelatorioClienteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatorio_cliente_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, RelatorioClienteFragment.newInstance())
                    .commitNow();
        }
    }
}
