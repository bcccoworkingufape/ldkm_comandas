package com.gps.ludke.ui.cliente;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gps.ludke.CardArrayAdapter;
import com.gps.ludke.Carrinho;
import com.gps.ludke.ClienteArrayAdapter;
import com.gps.ludke.ItensPedido;
import com.gps.ludke.MenuActivity;
import com.gps.ludke.R;
import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Cliente;
import com.gps.ludke.entidade.Comanda;
import com.gps.ludke.entidade.Produto;
import com.gps.ludke.entidade.Relatorio;
import com.gps.ludke.entidade.Venda;
import com.gps.ludke.repositorio.ClienteRepositorio;
import com.gps.ludke.repositorio.ComandaRepositorio;
import com.gps.ludke.repositorio.ProdutoRepositorio;
import com.gps.ludke.repositorio.VendaRepositorio;
import com.gps.ludke.ui.venda.VendaFragment;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ClienteFragment extends Fragment {

    private VendaRepositorio vendaRepositorio;
    private ClienteRepositorio clienteRepositorio;
    private ComandaRepositorio comandaRepositorio;
    private DadosOpenHelper dadosOpenHelper;
    private SQLiteDatabase conexao;

    //private TextView numComanda;
    private TextView textViewValorTotalCarrinho;
    private TextView textViewQuantdTotalCarrinho;
    private TextView textViewVazio;
    private ProgressBar progressBarCarrinho;
    private ListView listViewCarrinho;
    private CardArrayAdapter cardArrayAdapter;
    private ProgressDialog pDialog;
    private boolean vendaOk;

    private static ArrayList<Cliente> clientes;

    private ListView listView;

    private ProgressBar progressBarCliente;

    private ClienteArrayAdapter clienteArrayAdapter;

    private boolean clienteRemovido;

    private int idVendas;


    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cliente, container, false);

        progressBarCliente = root.findViewById(R.id.progressBarCliente);

        progressBarCliente.setVisibility(View.VISIBLE);

        criarConexao();

        //numComanda = (TextView) root.findViewById(R.id.textViewComanda);
        textViewVazio = root.findViewById(R.id.textViewVazio);
        listView = root.findViewById(R.id.listViewCliente);

        listView.addHeaderView(new View(getActivity()));
        listView.addFooterView(new View(getActivity()));

        if(ItensPedido.getComandaSelecionada() != null){
            //numComanda.setText("Comanda nº: " + ItensPedido.getComandaSelecionada().getCodigo());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //if(ItensPedido.getClientesUser().isEmpty()) {
                        clientes = clienteRepositorio.buscarTodosClientesWeb();
                        //ItensPedido.setClientesUser(clientes);
                    //}else{
                        //clientes = ItensPedido.getClientesUser();
                    //}

                }finally {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(clientes.isEmpty()){
                                textViewVazio.setVisibility(View.VISIBLE);
                            }else {
                                textViewVazio.setVisibility(View.GONE);
                            }
                            clienteArrayAdapter = new ClienteArrayAdapter(getActivity(),clientes);

                            listView.setAdapter(clienteArrayAdapter);
                            progressBarCliente.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Cliente clienteSelecionado = (Cliente) listView.getItemAtPosition(position);
                ItensPedido.setClienteSelecionado(clienteSelecionado);
                listView.setVisibility(View.GONE);
                progressBarCliente.setVisibility(View.VISIBLE);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment myFragment = new VendaFragment();
                getFragmentManager().beginTransaction().replace(R.id.containerLayout2,myFragment).commit();

            }


        });

        EditText editTextBusca = root.findViewById(R.id.editTextBusca);

        editTextBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(clienteArrayAdapter!=null){
                    clienteArrayAdapter.getFilter().filter(charSequence);
                    clienteArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            clientes = clienteRepositorio.buscarTodosClientesWeb();
                            //ItensPedido.setClientesUser(clientes);
                        }finally {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(clientes.isEmpty()){
                                        textViewVazio.setVisibility(View.VISIBLE);
                                    }else {
                                        textViewVazio.setVisibility(View.GONE);
                                    }
                                    clienteArrayAdapter = new ClienteArrayAdapter(getActivity(),clientes);

                                    listView.setAdapter(clienteArrayAdapter);
                                    progressBarCliente.setVisibility(View.GONE);
                                    pullToRefresh.setRefreshing(false);
                                }
                            });
                        }
                    }
                }).start();

            }
        });

        return root;
    }
    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.containerLayout2, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void venda(){
        Cliente cliente = ItensPedido.getClienteSelecionado();
        ItensPedido.getComandaSelecionada().setCliente(ItensPedido.getClienteSelecionado());
        new Thread(new Runnable() {
            @Override
            public void run() {
                int numCom = 0;
                try{
                    numCom = comandaRepositorio.inserir(ItensPedido.getComandaSelecionada());

                }finally {
                    final int finalNumCom = numCom;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (finalNumCom > 0) {
                                ItensPedido.getComandaSelecionada().setCodigo(finalNumCom);
                                idVendas = 0;

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            for (Produto produto : ItensPedido.getProdutosPedido()) {
                                                if (produto.getPeso() > 0) {
                                                    Venda venda = new Venda(produto, produto.getPeso(), ItensPedido.getComandaSelecionada(), true, false, false);
                                                    idVendas += vendaRepositorio.inserir(venda);
                                                }
                                            }
                                        }finally {
                                            getActivity().runOnUiThread(new Runnable() {
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
                                                        Toast.makeText(getActivity(),"Venda efetuada!",Toast.LENGTH_LONG).show();
                                                        ItensPedido.limparPedido();
                                                        Intent intent = new Intent(getActivity(), MenuActivity.class);
                                                        startActivity(intent);
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

    private void telaCarregando(){
        Toast.makeText(getActivity(), "Efetuando Venda!", Toast.LENGTH_SHORT).show();

        final View mView = getLayoutInflater().inflate(R.layout.dialog_load,null);
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void atualizarTotalQuantidadeCarrinho(){
        textViewQuantdTotalCarrinho.setText(Integer.toString(ItensPedido.getProdutosPedido().size()));

        double total = 0;

        for (Produto produto : ItensPedido.getProdutosPedido()){
            total += produto.getPeso()*produto.getPrecoVenda();

        }
        textViewValorTotalCarrinho.setText(formatter.format(total).replace("R$",""));
    }

    public static ClienteFragment newInstance() {
        return new ClienteFragment();
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
                           // numComanda.setText("Comanda nº: 1");
                            Comanda comanda = new Comanda(1);
                            ItensPedido.setComandaSelecionada(comanda);
                        }
                    }
                });
            }
        }).start();
    }

    private void criarConexao(){
        try {

            dadosOpenHelper = new DadosOpenHelper(getContext());

            conexao = dadosOpenHelper.getWritableDatabase();

            vendaRepositorio = new VendaRepositorio(conexao);
            clienteRepositorio = new ClienteRepositorio(conexao);
            comandaRepositorio = new ComandaRepositorio(conexao);

        }catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("OK",null);
            dlg.show();
        }
    }
}