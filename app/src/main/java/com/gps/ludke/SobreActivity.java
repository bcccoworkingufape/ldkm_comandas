package com.gps.ludke;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

public class SobreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Voltar");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView textViewBccCo = findViewById(R.id.textViewBccCo);

        String sourceString = "O LDK foi desenvolvido pelo Laboratório de Pesquisa e Desenvolvimento <b>BCC Coworking</b> , na Universidade Federal do Agreste de Pernambuco - <b>UFAPE</b>." ;
        textViewBccCo.setText(Html.fromHtml(sourceString));

        TextView textViewLink = findViewById(R.id.textViewLink);

        String link = " <a href=http://app.uag.ufrpe.br/bcccoworking/all.project >Clique aqui</a>";
        String texto = " para conhecer outros projetos.";
        String allText = link + texto;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textViewLink.setMovementMethod(LinkMovementMethod.getInstance());
            textViewLink.setText(Html.fromHtml(allText, Html.FROM_HTML_MODE_LEGACY));
        }
        else {
            textViewLink.setMovementMethod(LinkMovementMethod.getInstance());
            textViewLink.setText(Html.fromHtml(allText));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}