package com.gps.ludke.ui.venda;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gps.ludke.CardArrayAdapter;
import com.gps.ludke.Carrinho;
import com.gps.ludke.CarrinhoActivity;
import com.gps.ludke.ItensPedido;
import com.gps.ludke.ListagemCardArrayAdapter;
import com.gps.ludke.MenuActivity;
import com.gps.ludke.R;
import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Cliente;
import com.gps.ludke.entidade.Comanda;
import com.gps.ludke.entidade.Produto;
import com.gps.ludke.entidade.Venda;
import com.gps.ludke.repositorio.ClienteRepositorio;
import com.gps.ludke.repositorio.ComandaRepositorio;
import com.gps.ludke.repositorio.ProdutoRepositorio;
import com.gps.ludke.repositorio.VendaRepositorio;
import com.gps.ludke.ui.cliente.ClienteFragment;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VendaFragment extends Fragment {

    private VendaViewModel mViewModel;

    private ComandaRepositorio comandaRepositorio;
    private ProdutoRepositorio produtoRepositorio;
    private ClienteRepositorio clienteRepositorio;
    private VendaRepositorio vendaRepositorio;

    private DadosOpenHelper dadosOpenHelper;
    private SQLiteDatabase conexao;

    private TextView textViewVazio;
    //private TextView numComanda;
    private TextView peso;

    private ListView listView;

    private ArrayList<Produto> listaProduto;
    private ArrayList<Produto> listaTodosProdutos;

    private ProgressBar progressBarHome;

    private ListagemCardArrayAdapter listagemCardArrayAdapter;

    private ListView listViewCarrinho;
    private CardArrayAdapter cardArrayAdapter;

    private TextView textViewValorTotalCarrinho;
    private TextView textViewQuantdTotalCarrinho;

    private ProgressDialog pDialog;
    private ProgressDialog dialogAguarde;

    private ProgressBar progressBarCarrinho;

    private boolean vendaOk;

    private int idVendas;

    private boolean clienteRemovido;

    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));


    public static VendaFragment newInstance() {
        return new VendaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_venda, container, false);

        criarConexao();

        progressBarHome = root.findViewById(R.id.progressBarHome);

        //numComanda = (TextView) root.findViewById(R.id.textViewComanda);

        textViewVazio = root.findViewById(R.id.textViewVazio);

        listView = (ListView) root.findViewById(R.id.listViewHome);
        //peso = root.findViewById(R.id.textViewPeso);
        progressBarHome.setVisibility(View.VISIBLE);


        listView.addHeaderView(new View(getContext()));
        listView.addFooterView(new View(getContext()));

        if(ItensPedido.getComandaSelecionada() != null){
            //numComanda.setText("Comanda nº: " + ItensPedido.getComandaSelecionada().getCodigo());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    listaProduto = produtoRepositorio.buscarTodosProdutosWeb();
                    //relatorios= relatorioRepositorio.select();

                }finally {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //relatorioArrayAdapter = new RelatorioArrayAdapter(getActivity(),relatorios);
                            if(listaProduto.isEmpty()){
                                textViewVazio.setVisibility(View.VISIBLE);
                            }else {
                                textViewVazio.setVisibility(View.GONE);
                            }
                            //listView.setAdapter(relatorioArrayAdapter);
                            progressBarHome.setVisibility(View.GONE);

                            listagemCardArrayAdapter = new ListagemCardArrayAdapter(getContext(),listaProduto);

                            listView.setAdapter(listagemCardArrayAdapter);


                            //dialog.dismiss();
                        }
                    });
                }
            }
        }).start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final View mView = getLayoutInflater().inflate(R.layout.dialog_peso,null);

                Produto produtoSelecionado = (Produto) listView.getItemAtPosition(position);
                String nomeProd = produtoSelecionado.getNome();
                Double precoKg = produtoSelecionado.getPrecoVenda();
                String codProd = produtoSelecionado.getCodigo();


                final EditText editTextPeso = (EditText) mView.findViewById(R.id.editTextPeso);
                TextView textViewNomeProd = mView.findViewById(R.id.textViewNomeProd);
                TextView textViewCodProd = mView.findViewById(R.id.textViewCodProd);
                TextView textViewPrecoKg = mView.findViewById(R.id.textViewPrecoKg);

                Button buttonAdicionar = mView.findViewById(R.id.buttonAdicionar);
                Button buttonCancelar = mView.findViewById(R.id.buttonCancelar);
                ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);


                textViewCodProd.setText(codProd);
                textViewNomeProd.setText(nomeProd);
                textViewPrecoKg.setText(formatter.format(precoKg) + "/kg");

                if(produtoSelecionado.getPeso()>0){
                    editTextPeso.setText(Double.toString(produtoSelecionado.getPeso()));
                }

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                buttonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        esconderTeclado();
                        dialog.dismiss();

                    }
                });

                buttonAdicionar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (!TextUtils.isEmpty(editTextPeso.getText())) {
                                ((Produto) listView.getItemAtPosition(position)).setPeso(Double.valueOf(editTextPeso.getText().toString()));
                                //listaProduto.get(position-1).setPeso(Double.valueOf(editTextPeso.getText().toString()));
                                listagemCardArrayAdapter.notifyDataSetChanged();
                                confirmaProdutos();
                                Produto produto = (Produto) listView.getItemAtPosition(position);
                                atualizarTotalQuantidade();
                                Toast.makeText(getActivity(), Double.toString(produto.getPeso()) + "Kg de " + produto.getNome() + " no carrinho.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }else{
                                showToast("Digite um peso");
                            }
                        }catch (Exception e){
                            System.out.println(e.getMessage());
                            showToast("Valor inválido");
                        }
                        esconderTeclado();
                    }
                });

                imageButtonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        esconderTeclado();
                        dialog.dismiss();
                    }
                });
            }
        });

        EditText editTextBusca = root.findViewById(R.id.editTextBusca);

        editTextBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(listagemCardArrayAdapter!=null) {
                    listagemCardArrayAdapter.getFilter().filter(charSequence);
                    listagemCardArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return root;
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void esconderTeclado(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(getActivity().getCurrentFocus()!=null) {
            if (imm.isActive())
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    private void atualizarTotalQuantidade(){

        double total = 0;

        for (Produto produto : ItensPedido.getProdutosPedido()){
            total += produto.getPeso()*produto.getPrecoVenda();

        }
        ItensPedido.getComandaSelecionada().setTotal(total);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(VendaViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onResume() {
        super.onResume();
        atualizarComanda();
    }

    public void atualizarComanda(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String codComanda = comandaRepositorio.getCodigoComandaWeb();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (codComanda != null) {
                            int numeroComanda = Integer.parseInt(codComanda) + 1;
                            //numComanda.setText("Comanda nº: " + numeroComanda);
                            Comanda comanda = new Comanda(numeroComanda);
                            ItensPedido.setComandaSelecionada(comanda);
                        } else {
                            //numComanda.setText("Comanda nº: 1");
                            Comanda comanda = new Comanda(1);
                            ItensPedido.setComandaSelecionada(comanda);
                        }
                    }
                });
            }
        }).start();
    }

    private void confirmaProdutos() {

        for ( Produto produto : listaProduto ) {

            if ( produto.getPeso() > 0) {
                if (!ItensPedido.getProdutosPedido().contains(produto)) {
                    ItensPedido.addProdutoPedido(produto);
                } else {
                    ItensPedido.atualizaProdutoPedido(produto);
                }
            }
        }
    }

    private void telaCarregando(){
        showToast("Efetuando Venda");
        final View mView = getLayoutInflater().inflate(R.layout.dialog_load,null);
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void criarConexao(){
        try {

            dadosOpenHelper = new DadosOpenHelper(getContext());

            conexao = dadosOpenHelper.getWritableDatabase();

            produtoRepositorio = new ProdutoRepositorio(conexao);
            clienteRepositorio = new ClienteRepositorio(conexao);
            comandaRepositorio = new ComandaRepositorio(conexao);
            vendaRepositorio = new VendaRepositorio(conexao);

        }catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("OK",null);
            dlg.show();
        }
    }

}
