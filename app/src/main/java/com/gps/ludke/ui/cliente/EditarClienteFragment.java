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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;
import com.gps.ludke.ClienteArrayAdapter;
import com.gps.ludke.MenuActivity;
import com.gps.ludke.R;
import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Cliente;
import com.gps.ludke.repositorio.ClienteRepositorio;
import com.gps.ludke.repositorio.UserRepositorio;

import java.util.ArrayList;

public class EditarClienteFragment extends Fragment {

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cliente_editar, container, false);

        progressBarCliente = root.findViewById(R.id.progressBarEditarCliente);

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
            public void onItemClick(AdapterView<?> adapterView, View view,  final int position, long id) {
                final View mView = getLayoutInflater().inflate(R.layout.dialog_edit_cliente,null);

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


                final EditText ediTextNomeCompleto = mView.findViewById(R.id.editTextNomeCompleto);
                final EditText editTextNomeReduzido = mView.findViewById(R.id.editTextNomeReduzido);
                final EditText editTextNomeResponsavel = mView.findViewById(R.id.editTextNomeResponsavel);
                EditText editTextCpfCnpj = mView.findViewById(R.id.editTextCpfCnpj);
                final EditText editTextInscEst = mView.findViewById(R.id.editTextInscEst);
                final Spinner spinnerTipo = mView.findViewById(R.id.spinnerTipo);

                final EditText editTextCelular = mView.findViewById(R.id.editTextCelular);
                final EditText editTextTelefone = mView.findViewById(R.id.editTextTelefone);
                final EditText editTextEmail = mView.findViewById(R.id.editTextEmail);

                final EditText editTextRua  = mView.findViewById(R.id.editTextRua);
                final EditText editTextNumero  = mView.findViewById(R.id.editTextNumero);
                final EditText editTextBairro  = mView.findViewById(R.id.editTextBairro);
                final EditText editTextCidade  = mView.findViewById(R.id.editTextCidade);
                final EditText editTextUf = mView.findViewById(R.id.editTextUF);
                final EditText editTextCep  = mView.findViewById(R.id.editTextCep);
                EditText editTextComplemento  = mView.findViewById(R.id.editTextComplemento);

                final ScrollView scrollDados = mView.findViewById(R.id.scrollDados);
                final ScrollView scrollContato = mView.findViewById(R.id.scrollContato);
                final ScrollView scrollEndereco = mView.findViewById(R.id.scrollEndereco);

                ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);


                String spinnerArray[] = {"Pessoa Juridica", "Pessoa Física"};

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);

                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTipo.setAdapter(spinnerAdapter);

                if(tipo.equals("pessoaFisica")){
                    spinnerTipo.setSelection(1);
                }else if(tipo.equals("pessoaJuridica")){
                    spinnerTipo.setSelection(0);
                }

                ediTextNomeCompleto.setText(nomeCliente);
                editTextNomeReduzido.setText(nomeReduzido);
                editTextNomeResponsavel.setText(nomeResposavel);
                //editTextCpfCnpj.setText(cpfCnpjCliente);
                editTextInscEst.setText(inscricaoEstudual);

                editTextCelular.setText(celular);
                editTextTelefone.setText(telefone);
                editTextEmail.setText(email);

                editTextRua.setText(rua);
                editTextNumero.setText(numero);
                editTextBairro.setText(bairro);
                editTextCidade.setText(cidade);
                editTextCep.setText(cep);
                editTextComplemento.setText(complemento);
                editTextUf.setText(uf);

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

                Button buttonSalvar = mView.findViewById(R.id.buttonSalvar);

                buttonSalvar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clienteSelecionado.setNome(ediTextNomeCompleto.getText().toString());
                        clienteSelecionado.setNomeReduzido(editTextNomeReduzido.getText().toString());
                        clienteSelecionado.setNomeResponsavel(editTextNomeResponsavel.getText().toString());
                        clienteSelecionado.setInscricaoEstadual(editTextInscEst.getText().toString());

                        clienteSelecionado.setCelular(editTextCelular.getText().toString());
                        clienteSelecionado.setTelefone(editTextTelefone.getText().toString());
                        clienteSelecionado.setEmail(editTextEmail.getText().toString());

                        clienteSelecionado.getEndereco().setRua(editTextRua.getText().toString());
                        clienteSelecionado.getEndereco().setNumero(editTextNumero.getText().toString());
                        clienteSelecionado.getEndereco().setBairro(editTextBairro.getText().toString());
                        clienteSelecionado.getEndereco().setCidade(editTextCidade.getText().toString());
                        clienteSelecionado.getEndereco().setCep(editTextCep.getText().toString());
                        clienteSelecionado.getEndereco().setUf(editTextUf.getText().toString());

                        if (spinnerTipo.getSelectedItemId() == 0) {
                            clienteSelecionado.setTipo("pessoaJuridica");
                        } else {
                            clienteSelecionado.setTipo("pessoaFisica");
                        }

                        dialog.dismiss();
                        telaCarregando();
                        atualizarCliente();
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
        return root;
    }

    private void carregarLista(final boolean isRefresh){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    clientes = clienteRepositorio.buscarTodosClientesWeb();
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
                            if(isRefresh){
                                pullToRefresh.setRefreshing(false);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void atualizarCliente(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    clienteRepositorio.atualizarClienteWeb(clienteSelecionado);

                }finally {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogCarregando.dismiss();
                            carregarLista(false);
                            Toast.makeText(getActivity(),"Cliente Atualizado!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void telaCarregando(){
        Toast.makeText(getActivity(),"Atualizando Cliente!",Toast.LENGTH_SHORT).show();
        final View mView = getLayoutInflater().inflate(R.layout.dialog_load,null);
        final androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());

        mBuilder.setView(mView);
        dialogCarregando = mBuilder.create();
        dialogCarregando.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCarregando.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCarregando.show();
    }

    public static EditarClienteFragment newInstance() {
        return new EditarClienteFragment();
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
