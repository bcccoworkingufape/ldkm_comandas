package com.gps.ludke.ui.relatoriocliente;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.gps.ludke.ItensPedido;
import com.gps.ludke.ListagemRelatorioCardArrayAdapter;
import com.gps.ludke.R;
import com.gps.ludke.RelatorioArrayAdapter;
import com.gps.ludke.ViewPagerAdapter;
import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Relatorio;
import com.gps.ludke.repositorio.RelatorioRepositorio;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RelatorioClienteFragment extends Fragment {

    private RelatorioClienteViewModel mViewModel;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ViewPagerAdapter mPagerAdapter;

    private RelatorioRepositorio relatorioRepositorio;
    private DadosOpenHelper dadosOpenHelper;
    private SQLiteDatabase conexao;

    private TextView textViewVazio;

    private ListView listView;

    private ProgressBar progressBarRelatorioCliente;

    private static ArrayList<Relatorio> relatorios;

    private RelatorioArrayAdapter relatorioArrayAdapter;

    private ListagemRelatorioCardArrayAdapter listagemRelatorioCardArrayAdapter;

    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));


    public static RelatorioClienteFragment newInstance() {
        return new RelatorioClienteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.relatorio_cliente_fragment, container, false);

        progressBarRelatorioCliente = root.findViewById(R.id.progressBarRelatorioCliente);

        progressBarRelatorioCliente.setVisibility(View.VISIBLE);

        criarConexao();

        textViewVazio = root.findViewById(R.id.textViewVazio);
        listView = root.findViewById(R.id.listViewRelatorio);

        listView.addHeaderView(new View(getActivity()));
        listView.addFooterView(new View(getActivity()));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    relatorios= relatorioRepositorio.select(ItensPedido.getUsuarioLogado().getCpf());

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
                            progressBarRelatorioCliente.setVisibility(View.GONE);
                            //dialog.dismiss();
                        }
                    });
                }
            }
        }).start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final View mView = getLayoutInflater().inflate(R.layout.dialog_relatorio,null);
                Relatorio relatorio = relatorios.get(position - 1);

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
                relatorioArrayAdapter.getFilter().filter(charSequence);
                relatorioArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RelatorioClienteViewModel.class);
        // TODO: Use the ViewModel
    }

    private void criarConexao(){
        try {

            dadosOpenHelper = new DadosOpenHelper(getContext());

            conexao = dadosOpenHelper.getWritableDatabase();

            //Snackbar.make(drawer,"Conexão criada com sucesso!",Snackbar.LENGTH_LONG)
            //      .setAction("Ok", null).show();

            //produtoRepositorio = new ProdutoRepositorio(conexao);
            //clienteRepositorio = new ClienteRepositorio(conexao);
            //comandaRepositorio = new ComandaRepositorio(conexao);
            //vendaRepositorio = new VendaRepositorio(conexao);
            relatorioRepositorio = new RelatorioRepositorio(conexao);

        }catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("OK",null);
            dlg.show();
        }
    }



}
