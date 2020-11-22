package com.gps.ludke;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    private float totalGeral = 0;
    private String[] produto = new String[]{"LIN. MISTA DEF","LIN. DE CARNE SUINA DEF.", "LIN. ESPECIAL","COSTELA DEF.",
    "SALAME TIPO ITALIANO F.", "SALAME TIPO ITALIANO G.", "BACON DEF.", "PÉS DEF. RABOS DEF." +
            "TORRESMO","SALSICHÃO", "BANHA", "COPA", "LOMBO DEF.", "MORCELA", "SALAME COZIDO", "SALSICHA BOCK",
    "SALSICHA BRANCA", "SALSICHA FRANKFURT", "SALSICHA VIENA", "PERNIL C/ OSSO", "PALETA C/ OSSO", "COSTELA SUÍNA", "LOMBO FRESCO",
    "NUCA DE PORCO", "JOELHO DE PORCO", "FILÉZINHO DE PORCO", "PÉS, RABOS CRUS", "TOUCINHO", "KASSELER"};

    private ListView listView;
    private Handler handler = new Handler();
    private CardArrayAdapter cardArrayAdapter;
    private TextView textViewTotalGeral;

    private CardArrayAdapter cardArrayAdapterr;
    private final int REQUEST_CODE = 100;

    private EditText editTextPeso;
    private EditText editTextPrecoKg;
    private TextView editTextTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

        listView.addHeaderView(new View(this));
        listView.addFooterView(new View(this));

        cardArrayAdapter = new CardArrayAdapter(this,R.layout.card_item);
/*
        for (int i = 0; i < produto.length; i++) {
            Card card = new Card( produto[i],"0","0","5.85");
            totalGeral +=5.85;
            cardArrayAdapter.add(card);
        }
        listView.setAdapter(cardArrayAdapter);

*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(MainActivity.this, ItemActivity.class);

                TextView produtoText = view.findViewById(R.id.textViewProduto);
                TextView pesoText = view.findViewById(R.id.editTextPeso);
                TextView precoKgText = view.findViewById(R.id.editTextPrecoKg);
                TextView totalText = view.findViewById(R.id.textViewTotal);

                String produto = produtoText.getText().toString();
                String peso = pesoText.getText().toString();
                String precoKg = precoKgText.getText().toString();
                String total = totalText.getText().toString();


                if(!produto.equals("Produto") && !peso.equals("Peso") && !precoKg.equals("Preco Kg") && !total.equals("Total")){

                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    it.putExtra("Produto", produto);
                    it.putExtra("Peso",peso);
                    it.putExtra("PrecoKg",precoKg);
                    it.putExtra("Total",total);
                    //startActivity(new Intent(CardListActivity.this, PerfilActivity.class));
                    //startActivity(it);
                    startActivityForResult(it,REQUEST_CODE);
                }

            }
        });

        editTextPeso = (EditText) findViewById(R.id.editTextPeso);
        editTextPrecoKg = (EditText) findViewById(R.id.editTextPrecoKg);
        editTextTotal = (TextView) findViewById(R.id.textViewTotal);
        textViewTotalGeral = (TextView) findViewById(R.id.textViewTotalGeral);



        //totalGeral += Float.parseFloat(editTextTotal.getText().toString());
        textViewTotalGeral.setText("Total: "+ Float.toString(totalGeral) +" reais.");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //super.onActivityResult(requestCode, resultCode, data);
        // Verfica se o requestCode é o mesmo que foi passado

        if(requestCode==1 && resultCode == RESULT_OK)
        {
            String produtoNome=data.getStringExtra("ProdutoNome");
            String produtoPeso=data.getStringExtra("ProdutoPeso");
            String produtoPrecoKg=data.getStringExtra("ProdutoPrecoKg");
            String produtoTotal=data.getStringExtra("ProdutoTotal");
            //textViewPeso.setText(message);
            //Card card = new Card( produtoNome,produtoPeso,produtoPrecoKg,produtoTotal);
            //cardArrayAdapterr = new CardArrayAdapter(this,R.layout.card_item);
            //cardArrayAdapterr.add(card);
            //listView.setAdapter(cardArrayAdapterr);
        }
    }

}
