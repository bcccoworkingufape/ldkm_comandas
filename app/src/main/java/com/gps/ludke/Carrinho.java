package com.gps.ludke;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gps.ludke.entidade.Comanda;
import com.gps.ludke.entidade.Produto;
import com.gps.ludke.entidade.Relatorio;
import com.gps.ludke.entidade.Venda;
import com.gps.ludke.repositorio.ComandaRepositorio;
import com.gps.ludke.repositorio.VendaRepositorio;
import com.gps.ludke.ui.cliente.ClienteFragment;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Carrinho {

    private static TextView textViewValorTotalCarrinho;
    private static TextView textViewQuantdTotalCarrinho;
    private static NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static Context context;
    private static LayoutInflater inflater;
    private static boolean clienteRemovido;
    private static ProgressBar progressBarCarrinho;
    private static boolean vendaOk;
    private static int idVendas;
    private static ComandaRepositorio comandaRepositorio;
    private static VendaRepositorio vendaRepositorio;


    public static void abrirCarrinho(Context contextP, ComandaRepositorio comandaRe, VendaRepositorio vendaRe){
        context = contextP;
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        comandaRepositorio = comandaRe;
        vendaRepositorio = vendaRe;

        final View mView = inflater.inflate(R.layout.dialog_carrinho,null);

        TextView textViewNumCom = mView.findViewById(R.id.textViewNumCom);
        TextView textViewNomeVendedor = mView.findViewById(R.id.textViewNomeVendedor);
        final TextView textViewNomeCliente = mView.findViewById(R.id.textViewNomeCliente);
        TextView textViewData = mView.findViewById(R.id.textViewData);
        textViewQuantdTotalCarrinho = mView.findViewById(R.id.textViewQuantdTotal);
        textViewValorTotalCarrinho = mView.findViewById(R.id.textViewValorTotal);
        final TextView textViewCarrinhoVazio = mView.findViewById(R.id.textViewCarrinhoVazio);
        final Space spaceCarrinhoVazio = mView.findViewById(R.id.spaceCarrinhoVazio);
        progressBarCarrinho = mView.findViewById(R.id.progressBarCarrinho);
        ListView listViewCarrinho = mView.findViewById(R.id.listViewCarrinho);
        ImageView imageButtonClose = mView.findViewById(R.id.imageButtonClose);
        Button buttonCancelar = mView.findViewById(R.id.buttonCancelar);
        Button buttonFecharCompra = mView.findViewById(R.id.buttonFecharCompra);
        final Button buttonDeleteCliente = mView.findViewById(R.id.buttonDeleteCliente);

        if(ItensPedido.getComandaSelecionada() != null) {
            textViewNumCom.setText("Comanda: " + Integer.toString(ItensPedido.getComandaSelecionada().getCodigo()));
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            textViewData.setText(dateFormat.format(ItensPedido.getComandaSelecionada().getData()));
        }

        if(ItensPedido.getClienteSelecionado() == null){
            buttonDeleteCliente.setVisibility(View.GONE);
        }

        textViewNomeVendedor.setText(ItensPedido.getUsuarioLogado().getNome());


        if(ItensPedido.getClienteSelecionado() != null){
            textViewNomeCliente.setText(ItensPedido.getClienteSelecionado().getNomeReduzido());
        }
        atualizarTotalQuantidadeCarrinho();

        listViewCarrinho.addHeaderView(new View(context));
        listViewCarrinho.addFooterView(new View(context));

        CardArrayAdapter cardArrayAdapter = new CardArrayAdapter(context,ItensPedido.getProdutosPedido());

        listViewCarrinho.setAdapter(cardArrayAdapter);


        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);

        mBuilder.setView(mView);

        final AlertDialog dialog = mBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

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

        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clienteRemovido) {
                    Fragment clienteFragment = ClienteFragment.newInstance();
                    openFragment(clienteFragment);
                }
                dialog.dismiss();
            }
        });

        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clienteRemovido) {
                    Fragment clienteFragment = ClienteFragment.newInstance();
                    openFragment(clienteFragment);
                }
                dialog.dismiss();
            }
        });

        buttonFecharCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!ItensPedido.getProdutosPedido().isEmpty()){

                    telaCarregando();

                    venda();
                }else{
                    showToast("O carrinho está vazio");

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
    }

    private static void telaCarregando(){
        showToast("Efetuando Venda");
        final View mView = inflater.inflate(R.layout.dialog_load,null);
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    public static void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private static void atualizarTotalQuantidadeCarrinho(){
        textViewQuantdTotalCarrinho.setText(Integer.toString(ItensPedido.getProdutosPedido().size()));

        double total = 0;

        for (Produto produto : ItensPedido.getProdutosPedido()){
            total += produto.getPeso()*produto.getPrecoVenda();

        }
        ItensPedido.getComandaSelecionada().setTotal(total);
        textViewValorTotalCarrinho.setText(formatter.format(total).replace("R$",""));
    }

    private static void openFragment(Fragment fragment) {
        FragmentActivity activity = (FragmentActivity)context;
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.containerLayout2, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private static void venda(){
        ItensPedido.getComandaSelecionada().setCliente(ItensPedido.getClienteSelecionado());
        new Thread(new Runnable() {
            @Override
            public void run() {
                int numCom = 0;
                try{
                    numCom = comandaRepositorio.inserir(ItensPedido.getComandaSelecionada());

                }finally {
                    final int finalNumCom = numCom;
                    ((Activity)context).runOnUiThread(new Runnable() {
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
                                            ((Activity)context).runOnUiThread(new Runnable() {
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

    private static void perguntarImpressao(final Comanda comanda, final ArrayList<Venda> vendas){

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
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
                Button buttonImprimir = mView.findViewById(R.id.buttonImprimir);

                buttonImprimir.setVisibility(View.GONE);

                textViewNomeCliente.setText(relatorio.getComanda().getCliente().getNomeReduzido());
                textViewCnpjCpfDado.setText(relatorio.getComanda().getCliente().getCpf());
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                textViewData.setText(dateFormat.format(relatorio.getComanda().getData()));
                textViewQuantdTotal.setText(Integer.toString(relatorio.getVendas().size()));
                textViewValorTotal.setText(formatter.format(relatorio.getComanda().getTotal()).replace("R$",""));
                textViewNumCom.setText("Comanda Nº " + comanda.getCodigo());

                listViewRelatorio.addHeaderView(new View(context));
                listViewRelatorio.addFooterView(new View(context));

                ListagemRelatorioVendaAdapter listagemRelatorioVendaAdapter = new ListagemRelatorioVendaAdapter(context,relatorio.getVendas());

                listViewRelatorio.setAdapter(listagemRelatorioVendaAdapter);

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);

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
                        Intent intent = new Intent(context, MenuActivity.class);
                        context.startActivity(intent);

                    }
                });
            }
        });

        mBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                ItensPedido.limparPedido();
                Intent intent = new Intent(context, MenuActivity.class);
                context.startActivity(intent);
            }
        });
        AlertDialog dialog = mBuilder.create();
        dialog.show();


    }
}
