package com.gps.ludke;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gps.ludke.ui.relatoriogeral.RelatorioGeralFragment;

public class RelatorioGeralActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatorio_geral_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, RelatorioGeralFragment.newInstance())
                    .commitNow();
        }
    }
}
