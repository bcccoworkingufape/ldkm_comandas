package com.gps.ludke;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Comanda;
import com.gps.ludke.entidade.Produto;
import com.gps.ludke.entidade.Relatorio;
import com.gps.ludke.entidade.Venda;
import com.gps.ludke.repositorio.ClienteRepositorio;
import com.gps.ludke.repositorio.ComandaRepositorio;
import com.gps.ludke.repositorio.ProdutoRepositorio;
import com.gps.ludke.repositorio.VendaRepositorio;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CarrinhoActivity extends AppCompatActivity {

    private ListView listView;
    private CardArrayAdapter cardArrayAdapter;
    private Button buttonEfetuarVenda;
    private TextView totalPedido;
    private TextView textViewCarrinhoVazio;

    private DadosOpenHelper dadosOpenHelper;
    private SQLiteDatabase conexao;
    private ProdutoRepositorio produtoRepositorio;
    private ClienteRepositorio clienteRepositorio;
    private ComandaRepositorio comandaRepositorio;
    private VendaRepositorio vendaRepositorio;

    private static TextView textViewValorTotalCarrinho;
    private static TextView textViewQuantdTotalCarrinho;
    private static NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static LayoutInflater inflater;
    private static boolean clienteRemovido;
    private static ProgressBar progressBarCarrinho;
    private static boolean vendaOk;
    private static int idVendas;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Voltar");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        criarConexao();

        TextView textViewNumCom = findViewById(R.id.textViewNumCom);
        TextView textViewNomeVendedor = findViewById(R.id.textViewNomeVendedor);
        final TextView textViewNomeCliente = findViewById(R.id.textViewNomeCliente);
        TextView textViewData = findViewById(R.id.textViewData);
        textViewQuantdTotalCarrinho = findViewById(R.id.textViewQuantdTotal);
        textViewValorTotalCarrinho = findViewById(R.id.textViewValorTotal);
        final TextView textViewCarrinhoVazio = findViewById(R.id.textViewCarrinhoVazio);
        final Space spaceCarrinhoVazio = findViewById(R.id.spaceCarrinhoVazio);
        progressBarCarrinho = findViewById(R.id.progressBarCarrinho);
        ListView listViewCarrinho = findViewById(R.id.listViewCarrinho);
        final Button buttonDeleteCliente = findViewById(R.id.buttonDeleteCliente);
        Button buttonFecharCompra = findViewById(R.id.buttonFecharCompra);

        if(ItensPedido.getComandaSelecionada() != null) {
            textViewNumCom.setText(Integer.toString(ItensPedido.getComandaSelecionada().getCodigo()));
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            textViewData.setText(dateFormat.format(ItensPedido.getComandaSelecionada().getData()));
        }


        if(ItensPedido.getClienteSelecionado() == null){
            buttonDeleteCliente.setVisibility(View.GONE);
        }

        if(ItensPedido.getUsuarioLogado() != null) {
            textViewNomeVendedor.setText(ItensPedido.getUsuarioLogado().getNome());
        }
        if(ItensPedido.getClienteSelecionado() != null){
            textViewNomeCliente.setText(ItensPedido.getClienteSelecionado().getNomeReduzido());
        }

        atualizarTotalQuantidadeCarrinho();

        listViewCarrinho.addHeaderView(new View(this));
        listViewCarrinho.addFooterView(new View(this));

        CardArrayAdapter cardArrayAdapter = new CardArrayAdapter(this,ItensPedido.getProdutosPedido());

        listViewCarrinho.setAdapter(cardArrayAdapter);

        if(ItensPedido.getProdutosPedido().isEmpty()){
            textViewCarrinhoVazio.setVisibility(View.VISIBLE);
            spaceCarrinhoVazio.setVisibility(View.VISIBLE);
        }else{
            textViewCarrinhoVazio.setVisibility(View.GONE);
            spaceCarrinhoVazio.setVisibility(View.GONE);
        }

        cardArrayAdapter.setListener(new CardArrayAdapter.Listener() {
            @Override
            public void atualizarCarrinho() {
                atualizarTotalQuantidadeCarrinho();
                if(ItensPedido.getProdutosPedido().isEmpty()){
                    textViewCarrinhoVazio.setVisibility(View.VISIBLE);
                    spaceCarrinhoVazio.setVisibility(View.VISIBLE);
                }else{
                    textViewCarrinhoVazio.setVisibility(View.GONE);
                    spaceCarrinhoVazio.setVisibility(View.GONE);
                }
            }
        });

        buttonDeleteCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clienteRemovido = true;
                ItensPedido.removerClienteSelecionado();
                textViewNomeCliente.setText("Não informado");
                buttonDeleteCliente.setVisibility(View.GONE);
            }
        });

        buttonFecharCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!ItensPedido.getProdutosPedido().isEmpty()){
/*
                    telaCarregando();

                    venda();

 */
                    Intent intent = new Intent(CarrinhoActivity.this, PagamentoActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);

                }else{
                    showToast("O carrinho está vazio");
                }
            }
        });

    }

    private void venda(){
        ItensPedido.getComandaSelecionada().setCliente(ItensPedido.getClienteSelecionado());
        new Thread(new Runnable() {
            @Override
            public void run() {
                int numCom = 0;
                try{
                    numCom = comandaRepositorio.inserir(ItensPedido.getComandaSelecionada());

                }finally {
                    final int finalNumCom = numCom;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (finalNumCom > 0) {
                                ItensPedido.getComandaSelecionada().setCodigo(finalNumCom);
                                idVendas = 0;
                                final ArrayList<Venda> vendasArrayList = new ArrayList<>();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            for (Produto produto : ItensPedido.getProdutosPedido()) {
                                                if (produto.getPeso() > 0) {
                                                    Venda venda = new Venda(produto, produto.getPeso(), ItensPedido.getComandaSelecionada(), true, false, false);
                                                    idVendas += vendaRepositorio.inserir(venda);
                                                    vendasArrayList.add(venda);
                                                }
                                            }
                                        }finally {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (idVendas > 0) {
                                                        vendaOk = true;
                                                    } else {
                                                        vendaOk = false;
                                                    }

                                                    //dialogAguarde.dismiss();
                                                    //pDialog.dismiss();
                                                    progressBarCarrinho.setVisibility(View.GONE);
                                                    if(vendaOk){
                                                        showToast("Venda efetuada!");

                                                        perguntarImpressao(ItensPedido.getComandaSelecionada(), vendasArrayList);
                                                    }


                                                }
                                            });

                                        }
                                    }
                                }).start();


                            }else{
                                vendaOk = false;
                            }
                        }
                    });
                }
            }
        }).start();

    }

    private void perguntarImpressao(final Comanda comanda, final ArrayList<Venda> vendas){

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Deseja fazer a impressão da comanda?");
        mBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final View mView = inflater.inflate(R.layout.dialog_relatorio_venda,null);
                final Relatorio relatorio = new Relatorio(comanda, vendas);

                TextView textViewNomeCliente = mView.findViewById(R.id.textViewNomeCliente);
                TextView textViewCnpjCpfDado = mView.findViewById(R.id.textViewCnpjCpfDado);
                TextView textViewData = mView.findViewById(R.id.textViewData);
                TextView textViewQuantdTotal = mView.findViewById(R.id.textViewQuantdTotal);
                TextView textViewValorTotal = mView.findViewById(R.id.textViewValorTotal);
                ListView listViewRelatorio = mView.findViewById(R.id.listViewRelatorio);
                ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);
                TextView textViewNumCom = mView.findViewById(R.id.textViewNumCom);


                textViewNomeCliente.setText(relatorio.getComanda().getCliente().getNomeReduzido());
                textViewCnpjCpfDado.setText(relatorio.getComanda().getCliente().getCpf());
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                textViewData.setText(dateFormat.format(relatorio.getComanda().getData()));
                textViewQuantdTotal.setText(Integer.toString(relatorio.getVendas().size()));
                textViewValorTotal.setText(formatter.format(relatorio.getComanda().getTotal()).replace("R$",""));
                textViewNumCom.setText("Comanda Nº " + comanda.getCodigo());

                listViewRelatorio.addHeaderView(new View(CarrinhoActivity.this));
                listViewRelatorio.addFooterView(new View(CarrinhoActivity.this));

                ListagemRelatorioVendaAdapter listagemRelatorioVendaAdapter = new ListagemRelatorioVendaAdapter(CarrinhoActivity.this,relatorio.getVendas());

                listViewRelatorio.setAdapter(listagemRelatorioVendaAdapter);

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(CarrinhoActivity.this);

                mBuilder.setView(mView);
                final AlertDialog dialogVendaRelatorio = mBuilder.create();
                dialogVendaRelatorio.setCancelable(false);
                dialogVendaRelatorio.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogVendaRelatorio.show();

                imageButtonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogVendaRelatorio.dismiss();
                        ItensPedido.limparPedido();
                        Intent intent = new Intent(CarrinhoActivity.this, MenuActivity.class);
                        startActivity(intent);

                    }
                });
            }
        });

        mBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                ItensPedido.limparPedido();
                Intent intent = new Intent(CarrinhoActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void telaCarregando(){
        showToast("Efetuando Venda");
        final View mView = getLayoutInflater().inflate(R.layout.dialog_load,null);
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(CarrinhoActivity.this);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void atualizarTotalQuantidadeCarrinho(){
        textViewQuantdTotalCarrinho.setText(Integer.toString(ItensPedido.getProdutosPedido().size()));

        double total = 0;

        for (Produto produto : ItensPedido.getProdutosPedido()){
            total += produto.getPeso()*produto.getPrecoVenda();
        }
        textViewValorTotalCarrinho.setText(formatter.format(total).replace("R$",""));
        if(ItensPedido.getComandaSelecionada() != null){
            ItensPedido.getComandaSelecionada().setTotal(total);
        }
    }


    private void criarConexao(){
        try {

            dadosOpenHelper = new DadosOpenHelper(this);

            conexao = dadosOpenHelper.getWritableDatabase();

            //Snackbar.make(drawer,"Conexão criada com sucesso!",Snackbar.LENGTH_LONG)
            //      .setAction("Ok", null).show();

            produtoRepositorio = new ProdutoRepositorio(conexao);
            clienteRepositorio = new ClienteRepositorio(conexao);
            comandaRepositorio = new ComandaRepositorio(conexao);
            vendaRepositorio = new VendaRepositorio(conexao);

        }catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("OK",null);
            dlg.show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right);
    }
}
