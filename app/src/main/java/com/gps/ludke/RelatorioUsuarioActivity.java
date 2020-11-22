package com.gps.ludke;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gps.ludke.ui.venda.EditarVendaFragment;

public class RelatorioUsuarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatorio_usuario_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, EditarVendaFragment.newInstance())
                    .commitNow();
        }
    }
}
