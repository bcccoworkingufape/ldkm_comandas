package com.gps.ludke.ui.relatoriogeral;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gps.ludke.ClienteArrayAdapter;
import com.gps.ludke.ItensPedido;
import com.gps.ludke.ListagemCardArrayAdapter;
import com.gps.ludke.ListagemRelatorioCardArrayAdapter;
import com.gps.ludke.R;
import com.gps.ludke.RelatorioArrayAdapter;
import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Relatorio;
import com.gps.ludke.entidade.Venda;
import com.gps.ludke.repositorio.ClienteRepositorio;
import com.gps.ludke.repositorio.RelatorioRepositorio;
import com.gps.ludke.repositorio.VendaRepositorio;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RelatorioGeralFragment extends Fragment {

    private RelatorioGeralViewModel mViewModel;

    private VendaRepositorio vendaRepositorio;
    private ClienteRepositorio clienteRepositorio;
    private RelatorioRepositorio relatorioRepositorio;
    private DadosOpenHelper dadosOpenHelper;
    private SQLiteDatabase conexao;
    private TextView textViewVazio;

    private ListView listView;

    private static ArrayList<Relatorio> relatorios;

    private RelatorioArrayAdapter relatorioArrayAdapter;

    private ProgressBar progressBarRelatorioGeral;

    private ListagemRelatorioCardArrayAdapter listagemRelatorioCardArrayAdapter;

    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public static RelatorioGeralFragment newInstance() {
        return new RelatorioGeralFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.relatorio_geral_fragment, container, false);

        progressBarRelatorioGeral = root.findViewById(R.id.progressBarRelatorioGeral);

        progressBarRelatorioGeral.setVisibility(View.VISIBLE);
        //final ProgressDialog dialog = ProgressDialog.show(getActivity(), "Aguarde...", "Carregando relatório...", true);
        criarConexao();
        textViewVazio = root.findViewById(R.id.textViewVazio);
        listView = root.findViewById(R.id.listViewRelatorio);

        listView.addHeaderView(new View(getActivity()));
        listView.addFooterView(new View(getActivity()));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //if(ItensPedido.getRelatorioGeral().isEmpty()) {
                    relatorios = relatorioRepositorio.select();
                    Collections.reverse(relatorios);
                        //ItensPedido.setRelatorioGeral(relatorios);
                    //}else{
                        //relatorios = ItensPedido.getRelatorioGeral();
                    //}
                }finally {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(relatorios.isEmpty()){
                                textViewVazio.setVisibility(View.VISIBLE);
                            }else {
                                textViewVazio.setVisibility(View.GONE);
                            }
                            relatorioArrayAdapter = new RelatorioArrayAdapter(getActivity(),relatorios);

                            listView.setAdapter(relatorioArrayAdapter);
                            progressBarRelatorioGeral.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final View mView = getLayoutInflater().inflate(R.layout.dialog_relatorio,null);
                Relatorio relatorio = (Relatorio) listView.getItemAtPosition(position);

                TextView textViewComanda = mView.findViewById(R.id.textViewNumCom);
                TextView textViewNomeVendedor = mView.findViewById(R.id.textViewNomeVendedor);
                TextView textViewCliente = mView.findViewById(R.id.textViewNomeCliente);
                TextView textViewData = mView.findViewById(R.id.textViewData);
                TextView textViewQuantdTotal = mView.findViewById(R.id.textViewQuantdTotal);
                TextView textViewValorTotal = mView.findViewById(R.id.textViewValorTotal);


                ListView listViewRelatorio = mView.findViewById(R.id.listViewRelatorio);

                ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);
                //Button buttonFechar = mView.findViewById(R.id.buttonFechar);

                textViewQuantdTotal.setText(Integer.toString(relatorio.getQuantidadeVendas()));
                textViewNomeVendedor.setText(relatorio.getComanda().getUsuario().getNome());
                textViewComanda.setText("Comanda "+relatorio.getComanda().getCodigo());
                if(relatorio.getComanda().getCliente() != null) {
                    textViewCliente.setText(relatorio.getComanda().getCliente().getNomeReduzido());
                }else{
                    textViewCliente.setText("não informado");
                }

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                textViewData.setText(dateFormat.format(relatorio.getComanda().getData()));

                textViewValorTotal.setText(formatter.format(relatorio.getComanda().getTotal()).replace("R$",""));

                listViewRelatorio.addHeaderView(new View(getContext()));
                listViewRelatorio.addFooterView(new View(getContext()));

                listagemRelatorioCardArrayAdapter = new ListagemRelatorioCardArrayAdapter(getContext(),relatorio.getVendas());

                listViewRelatorio.setAdapter(listagemRelatorioCardArrayAdapter);

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());


                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
/*
                buttonFechar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

 */

                imageButtonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
                if(relatorioArrayAdapter != null) {
                    relatorioArrayAdapter.setIsUser(false);
                    relatorioArrayAdapter.getFilter().filter(charSequence);
                    relatorioArrayAdapter.notifyDataSetChanged();
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
                            relatorios = relatorioRepositorio.select();
                        }finally {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(relatorios.isEmpty()){
                                        textViewVazio.setVisibility(View.VISIBLE);
                                    }else {
                                        textViewVazio.setVisibility(View.GONE);
                                    }
                                    relatorioArrayAdapter = new RelatorioArrayAdapter(getActivity(),relatorios);

                                    listView.setAdapter(relatorioArrayAdapter);
                                    progressBarRelatorioGeral.setVisibility(View.GONE);
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

    private int dpToPx(int dp) {
        int px = dp;
        try {
            px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        } catch (Exception ignored){}
        return px;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RelatorioGeralViewModel.class);
        // TODO: Use the ViewModel
    }

    private void criarConexao(){
        try {

            dadosOpenHelper = new DadosOpenHelper(getContext());

            conexao = dadosOpenHelper.getWritableDatabase();

            //Snackbar.make(drawer,"Conexão criada com sucesso!",Snackbar.LENGTH_LONG)
            //      .setAction("Ok", null).show();

            //produtoRepositorio = new ProdutoRepositorio(conexao);
            clienteRepositorio = new ClienteRepositorio(conexao);
            //comandaRepositorio = new ComandaRepositorio(conexao);
            vendaRepositorio = new VendaRepositorio(conexao);
            relatorioRepositorio = new RelatorioRepositorio(conexao);

        }catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("OK",null);
            dlg.show();
        }
    }

}
