package com.gps.ludke.ui.cliente;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gps.ludke.BCrypt;
import com.gps.ludke.R;
import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Cliente;
import com.gps.ludke.entidade.Endereco;
import com.gps.ludke.repositorio.ClienteRepositorio;

public class CriarClienteFragment extends Fragment {

    private ClienteRepositorio clienteRepositorio;
    private DadosOpenHelper dadosOpenHelper;
    private SQLiteDatabase conexao;

    private EditText editTextNomeCompleto;
    private EditText editTextNomeReduzido;
    private EditText editTextNomeResponsavel;
    private EditText editTextCpfCnpj;
    private Spinner spinnerTipo;
    private EditText editTextInscEst;

    private EditText editTextCelular, editTextTelefone, editTextEmail;

    private EditText editTextRua,editTextNumero, editTextBairro, editTextCidade, editTextCep, editTextComplemento, editTextUf;

    private Button buttonContinuarDados, buttonContinuarContato, voltarParaDados, buttonVoltarContato, buttonCriar;

    private ScrollView scrollViewDados, scrollViewContato, scrollEndereco;

    private ProgressBar progressBar;

    private Cliente cliente;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cliente_criar, container, false);

        criarConexao();

        setarView(root);

        cliente = new Cliente();

        buttonContinuarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    checarDados();
                    scrollViewDados.setVisibility(View.GONE);
                    scrollViewContato.setVisibility(View.VISIBLE);
                    scrollEndereco.setVisibility(View.GONE);

                }catch (Exception e){
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonContinuarContato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    checarContatos();
                    scrollViewDados.setVisibility(View.GONE);
                    scrollViewContato.setVisibility(View.GONE);
                    scrollEndereco.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    Toast.makeText(getActivity(),  e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    checarEndereco();
                    carregando(true);
                    cliente.setPass(gerarSenha("123456").replaceFirst("2a", "2y"));
                    Toast.makeText(getActivity(),  "Criando cliente", Toast.LENGTH_LONG).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int idCliente = clienteRepositorio.inserir(cliente);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    carregando(false);
                                    Toast.makeText(getActivity(),  "Cliente \""+cliente.getNomeReduzido()+"\" criado!", Toast.LENGTH_LONG).show();
                                    Fragment criarCliFragment = CriarClienteFragment.newInstance();
                                    openFragment(criarCliFragment);
                                }
                            });
                        }
                    }).start();

                    //Toast.makeText(getActivity(),  "Cliente com o Id: "+Integer.toString(idCliente) + " criado!", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(getActivity(),  e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        voltarParaDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollViewDados.setVisibility(View.VISIBLE);
                scrollViewContato.setVisibility(View.GONE);
                scrollEndereco.setVisibility(View.GONE);
            }
        });

        buttonVoltarContato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollViewDados.setVisibility(View.GONE);
                scrollViewContato.setVisibility(View.VISIBLE);
                scrollEndereco.setVisibility(View.GONE);
            }
        });

        /*
        buttonCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollViewDados.setVisibility(View.GONE);
                scrollViewContato.setVisibility(View.GONE);
                scrollEndereco.setVisibility(View.VISIBLE);
            }
        });

         */
        return root;
    }


    private void carregando(boolean isTrue){
        if(isTrue) {
            scrollViewDados.setVisibility(View.GONE);
            scrollViewContato.setVisibility(View.GONE);
            scrollEndereco.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            scrollViewDados.setVisibility(View.VISIBLE);
            scrollViewContato.setVisibility(View.GONE);
            scrollEndereco.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setarView(View root){

        editTextNomeCompleto = root.findViewById(R.id.editTextNomeCompleto);
        editTextNomeReduzido  = root.findViewById(R.id.editTextNomeReduzido);
        editTextNomeResponsavel  = root.findViewById(R.id.editTextNomeResponsavel);
        editTextCpfCnpj  = root.findViewById(R.id.editTextCpfCnpj);
        spinnerTipo  = root.findViewById(R.id.spinnerTipo);
        editTextInscEst  = root.findViewById(R.id.editTextInscEst);

        editTextCelular = root.findViewById(R.id.editTextCelular);
        editTextTelefone = root.findViewById(R.id.editTextTelefone);
        editTextEmail = root.findViewById(R.id.editTextEmail);

        editTextRua  = root.findViewById(R.id.editTextRua);
        editTextNumero  = root.findViewById(R.id.editTextNumero);
        editTextBairro  = root.findViewById(R.id.editTextBairro);
        editTextCidade  = root.findViewById(R.id.editTextCidade);
        editTextCep  = root.findViewById(R.id.editTextCep);
        editTextComplemento  = root.findViewById(R.id.editTextComplemento);
        editTextUf = root.findViewById(R.id.editTextUF);

        buttonContinuarDados = root.findViewById(R.id.buttonContinuarDados);
        buttonContinuarContato = root.findViewById(R.id.buttonContinuarContato);
        voltarParaDados = root.findViewById(R.id.buttonVoltarDados);
        buttonVoltarContato = root.findViewById(R.id.buttonVoltarContato);
        buttonCriar = root.findViewById(R.id.buttonCriar);


        scrollViewDados = root.findViewById(R.id.scrollDados);
        scrollViewContato = root.findViewById(R.id.scrollContato);
        scrollEndereco = root.findViewById(R.id.scrollEndereco);

        progressBar = root.findViewById(R.id.progressBarCriarCliente);

        String spinnerArray[] = {"Pessoa Juridica", "Pessoa Física"};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(spinnerAdapter);
    }

    private void checarDados(){
        if(isEmpty(editTextNomeCompleto)){
            throw new java.lang.IllegalArgumentException("Digite o nome completo");
        }else if(isEmpty(editTextNomeReduzido)) {
            throw new java.lang.IllegalArgumentException("Digite o nome reduzido");
        }else if(isEmpty(editTextCpfCnpj)){
            throw new java.lang.IllegalArgumentException("Digite o CPF/CNPJ");
        }

        try {
            cliente.setNome(editTextNomeCompleto.getText().toString());
            cliente.setNomeReduzido(editTextNomeReduzido.getText().toString());
            cliente.setCpf(editTextCpfCnpj.getText().toString());
            cliente.setNomeResponsavel(editTextNomeResponsavel.getText().toString());
            cliente.setInscricaoEstadual(editTextInscEst.getText().toString());

            if (spinnerTipo.getSelectedItemId() == 0) {
                cliente.setTipo("pessoaJuridica");
            } else {
                cliente.setTipo("pessoaFisica");
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new java.lang.IllegalArgumentException("Erro na inserção dos dados");
        }

    }

    private void checarContatos(){
        if(isEmpty(editTextCelular) && isEmpty(editTextTelefone)){
            throw new java.lang.IllegalArgumentException("Digite um telefone ou celular");
        }else if(isEmpty(editTextEmail)){
            throw new java.lang.IllegalArgumentException("Digite o email");
        }

        try{
            cliente.setTelefone(editTextTelefone.getText().toString());
            cliente.setCelular(editTextCelular.getText().toString());
            cliente.setEmail(editTextEmail.getText().toString());

        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new java.lang.IllegalArgumentException("Erro na inserção dos dados");
        }
    }

    private void checarEndereco(){
        if(isEmpty(editTextRua)){
            throw new java.lang.IllegalArgumentException("Digite a rua");
        }else if(isEmpty(editTextNumero)){
            throw new java.lang.IllegalArgumentException("Digite o número");
        }
        else if(isEmpty(editTextBairro)){
            throw new java.lang.IllegalArgumentException("Digite o bairro");
        }
        else if(isEmpty(editTextCidade)){
            throw new java.lang.IllegalArgumentException("Digite a cidade");
        }else if( isEmpty(editTextUf)){
            throw new java.lang.IllegalArgumentException("Digite o UF");
        }

        try{
            Endereco endereco = new Endereco();
            endereco.setRua(editTextRua.getText().toString());
            endereco.setNumero(editTextNumero.getText().toString());
            endereco.setBairro(editTextBairro.getText().toString());
            endereco.setCidade(editTextCidade.getText().toString());
            endereco.setCep(editTextCep.getText().toString());
            endereco.setComplemento(editTextComplemento.getText().toString());
            endereco.setUf(editTextUf.getText().toString());

            cliente.setEndereco(endereco);

        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new java.lang.IllegalArgumentException("Erro na inserção dos dados");
        }

    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    private String gerarSenha(String plain_password){
        String pw_hash = BCrypt.hashpw(plain_password, BCrypt.gensalt());
        return pw_hash;
    }

    public static CriarClienteFragment newInstance() {
        return new CriarClienteFragment();
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

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.containerLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
