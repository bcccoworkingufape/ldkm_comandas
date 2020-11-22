package com.gps.ludke.ui.cliente;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;
import com.gps.ludke.ClienteArrayAdapter;
import com.gps.ludke.R;
import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Cliente;
import com.gps.ludke.repositorio.ClienteRepositorio;
import com.gps.ludke.ui.relatorios.RelatorioVendaFragment;
import com.gps.ludke.ui.relatorios.RelatoriosFragment;

import java.util.ArrayList;

public class ListagemClienteFragment extends Fragment {

    private ProgressBar progressBarCliente;


    private DadosOpenHelper dadosOpenHelper;
    private SQLiteDatabase conexao;
    private ClienteRepositorio clienteRepositorio;

    private TextView textViewVazio;
    private ListView listView;

    private static ArrayList<Cliente> clientes;

    private ClienteArrayAdapter clienteArrayAdapter;

    private TabLayout mTabLayout;

    private Cliente clienteSelecionado;

    private androidx.appcompat.app.AlertDialog dialogCarregando;

    private SwipeRefreshLayout pullToRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cliente_listagem, container, false);

        progressBarCliente = root.findViewById(R.id.progressBarListagemCliente);

        progressBarCliente.setVisibility(View.VISIBLE);

        criarConexao();

        textViewVazio = root.findViewById(R.id.textViewVazio);
        listView = root.findViewById(R.id.listViewCliente);

        listView.addHeaderView(new View(getActivity()));
        listView.addFooterView(new View(getActivity()));

        carregarLista(false);

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final View mView = getLayoutInflater().inflate(R.layout.dialog_cliente_dados,null);
                clienteSelecionado = (Cliente) listView.getItemAtPosition(position);

                final String nomeCliente = clienteSelecionado.getNome();
                final String nomeReduzido = clienteSelecionado.getNomeReduzido();
                final String nomeResposavel = clienteSelecionado.getNomeResponsavel();
                String cpfCnpjCliente = clienteSelecionado.getCpf();
                final String inscricaoEstudual = clienteSelecionado.getInscricaoEstadual();
                String tipo = clienteSelecionado.getTipo();

                final String celular = clienteSelecionado.getCelular();
                final String telefone = clienteSelecionado.getTelefone();
                final String email = clienteSelecionado.getEmail();

                String rua = clienteSelecionado.getEndereco().getRua();
                String numero = clienteSelecionado.getEndereco().getNumero();
                String bairro = clienteSelecionado.getEndereco().getBairro();
                String cidade = clienteSelecionado.getEndereco().getCidade();
                String uf = clienteSelecionado.getEndereco().getUf();
                String cep = clienteSelecionado.getEndereco().getCep();
                String complemento = clienteSelecionado.getEndereco().getComplemento();

                final TextView textViewNomeCompleto = mView.findViewById(R.id.textViewNomeCompleto);
                final TextView textViewNomeReduzido = mView.findViewById(R.id.textViewNomeReduzido);
                final TextView textViewNomeResponsavel = mView.findViewById(R.id.textViewNomeResponsavel);
                //TextView editTextCpfCnpj = mView.findViewById(R.id.editTextCpfCnpj);
                final TextView textViewInscEst = mView.findViewById(R.id.textViewInscEst);
                final TextView textViewTipo = mView.findViewById(R.id.textViewTipo);

                final TextView textViewCelular = mView.findViewById(R.id.textViewCelular);
                final TextView textViewTelefone = mView.findViewById(R.id.textViewTelefone);
                final TextView textViewEmail = mView.findViewById(R.id.textViewEmail);

                final TextView textViewRua  = mView.findViewById(R.id.textViewRua);
                final TextView textViewNumero  = mView.findViewById(R.id.textViewNumero);
                final TextView textViewBairro  = mView.findViewById(R.id.textViewBairro);
                final TextView textViewCidade  = mView.findViewById(R.id.textViewCidade);
                final TextView textViewUF = mView.findViewById(R.id.textViewUF);
                final TextView textViewCep  = mView.findViewById(R.id.textViewCep);
                TextView textViewComplemento  = mView.findViewById(R.id.textViewComplemento);

                final ScrollView scrollDados = mView.findViewById(R.id.scrollDados);
                final ScrollView scrollContato = mView.findViewById(R.id.scrollContato);
                final ScrollView scrollEndereco = mView.findViewById(R.id.scrollEndereco);

                ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);

                setTextViewValue(textViewNomeCompleto, nomeCliente);
                setTextViewValue(textViewNomeReduzido, nomeReduzido);
                setTextViewValue(textViewNomeResponsavel, nomeResposavel);
                //editTextCpfCnpj.setText(cpfCnpjCliente);
                setTextViewValue(textViewInscEst, inscricaoEstudual);

                setTextViewValue(textViewCelular, celular);
                setTextViewValue(textViewTelefone, telefone);
                setTextViewValue(textViewEmail, email);

                setTextViewValue(textViewRua, rua);
                setTextViewValue(textViewNumero, numero);
                setTextViewValue(textViewBairro, bairro);
                setTextViewValue(textViewCidade, cidade);
                setTextViewValue(textViewUF, uf);
                setTextViewValue(textViewCep, cep);
                setTextViewValue(textViewComplemento, complemento);

                if(tipo.equals("pessoaFisica")){
                    setTextViewValue(textViewTipo, "Pessoa Física");
                }else if(tipo.equals("pessoaJuridica")){
                    setTextViewValue(textViewTipo, "Pessoa Jurídica");
                }

                final androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(getContext());

                mBuilder.setView(mView);
                final androidx.appcompat.app.AlertDialog dialog = mBuilder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                mTabLayout = mView.findViewById(R.id.tabLayout);
                mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        switch (tab.getPosition()) {
                            case 0:
                                scrollDados.setVisibility(View.VISIBLE);
                                scrollContato.setVisibility(View.GONE);
                                scrollEndereco.setVisibility(View.GONE);
                                break;
                            case 1:
                                scrollDados.setVisibility(View.GONE);
                                scrollContato.setVisibility(View.VISIBLE);
                                scrollEndereco.setVisibility(View.GONE);
                                break;
                            case 2:
                                scrollDados.setVisibility(View.GONE);
                                scrollContato.setVisibility(View.GONE);
                                scrollEndereco.setVisibility(View.VISIBLE);
                                break;
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

                imageButtonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        pullToRefresh = root.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                carregarLista(true);
            }
        });

        Button buttonVoltar = root.findViewById(R.id.buttonVoltar);
        buttonVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogRelatorios();
            }
        });

        return root;
    }
    public static ListagemClienteFragment newInstance () {
        return new ListagemClienteFragment();
    }

    private void setTextViewValue(TextView textView, String texto){
        if(texto != null){
            if(texto.isEmpty()){
                textView.setText("Não informado");
            }else{
                textView.setText(texto);
            }
        }else{
            textView.setText("Não informado");
        }
    }

    private void abrirDialogRelatorios(){
        final View mView = getLayoutInflater().inflate(R.layout.dialog_relatorios,null);
        final androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        mBuilder.setCancelable(false);

        mBuilder.setView(mView);
        final androidx.appcompat.app.AlertDialog dialog = mBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);
        Button buttonClientes = mView.findViewById(R.id.buttonClientes);
        Button buttonVendas = mView.findViewById(R.id.buttonVendas);

        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        buttonClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Fragment relatoriosClientesFragment = ListagemClienteFragment.newInstance();
                openFragment(relatoriosClientesFragment);
            }
        });

        buttonVendas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Fragment relatoriosVendaFragment = RelatorioVendaFragment.newInstance();
                openFragment(relatoriosVendaFragment);
            }
        });
    }

    private void carregarLista(final boolean isRefresh){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    clientes = clienteRepositorio.buscarTodosClientesWeb();
                }finally {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (clientes.isEmpty()) {
                                    textViewVazio.setVisibility(View.VISIBLE);
                                } else {
                                    textViewVazio.setVisibility(View.GONE);
                                }
                                clienteArrayAdapter = new ClienteArrayAdapter(getActivity(), clientes);

                                listView.setAdapter(clienteArrayAdapter);
                                progressBarCliente.setVisibility(View.GONE);
                                if (isRefresh) {
                                    pullToRefresh.setRefreshing(false);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.containerLayout2, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void criarConexao(){
        try {

            dadosOpenHelper = new DadosOpenHelper(getContext());

            conexao = dadosOpenHelper.getWritableDatabase();

            clienteRepositorio = new ClienteRepositorio(conexao);

        }catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("OK",null);
            dlg.show();
        }
    }
}
