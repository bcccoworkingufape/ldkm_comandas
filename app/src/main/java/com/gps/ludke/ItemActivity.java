package com.gps.ludke;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ItemActivity extends AppCompatActivity {
    private TextView textViewItem;
    private EditText editTextPeso;
    private EditText editTextPrecoKg;
    private TextView textViewItemTotal;
    private Button botaoSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        String produto;
        String peso;
        String precoKg;
        String total;

        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                produto = null;
                peso = null;
                precoKg = null;
                total = null;
            }else{
                produto=extras.getString("Produto");
                peso = extras.getString("Peso");
                precoKg = extras.getString("PrecoKg");
                total = extras.getString("Total");

            }
        }else{
            produto= (String) savedInstanceState.getSerializable("Produto");
            peso = (String) savedInstanceState.getSerializable("Peso");
            precoKg = (String) savedInstanceState.getSerializable("PrecoKg");
            total = (String) savedInstanceState.getSerializable("Total");
        }
        textViewItem = (TextView) findViewById(R.id.textViewItem);
        editTextPeso = (EditText) findViewById(R.id.editTextPeso);
        editTextPrecoKg = (EditText) findViewById(R.id.editTextPrecoKg);
        textViewItemTotal = (TextView) findViewById(R.id.textViewItemTotal);

        textViewItem.setText(produto);
        editTextPeso.setText(peso);
        editTextPrecoKg.setText(precoKg);
        textViewItemTotal.setText(total);

        botaoSalvar = (Button) findViewById(R.id.buttonSalvarProduto);

        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String produtoNome  = textViewItem.getText().toString();
                String produtoPeso  = editTextPeso.getText().toString();
                String produtoPrecoKg  = editTextPrecoKg.getText().toString();
                String produtoTotal  = textViewItemTotal.getText().toString();

                Intent intent= new Intent(ItemActivity.this,MainActivity.class);
                intent.putExtra("ProdutoNome",produtoNome);
                intent.putExtra("ProdutoPeso",produtoPeso);
                intent.putExtra("ProdutoPrecoKg",produtoPrecoKg);
                intent.putExtra("ProdutoTotal",produtoTotal);
                if(getParent() == null){
                    setResult(1, intent);
                }else{
                    getParent().setResult(1, intent);
                }

                //setResult(RESULT_OK,intent);
                finish();
                startActivity(intent);
            }
        });

    }

}
