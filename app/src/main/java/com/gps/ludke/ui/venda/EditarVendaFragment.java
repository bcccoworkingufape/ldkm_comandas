package com.gps.ludke.ui.venda;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gps.ludke.ClienteArrayAdapter;
import com.gps.ludke.ItensPedido;
import com.gps.ludke.ListagemCardArrayAdapter;
import com.gps.ludke.ListagemRelatorioCardArrayAdapter;
import com.gps.ludke.R;
import com.gps.ludke.RelatorioArrayAdapter;
import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Cliente;
import com.gps.ludke.entidade.Produto;
import com.gps.ludke.entidade.Relatorio;
import com.gps.ludke.entidade.Venda;
import com.gps.ludke.repositorio.ClienteRepositorio;
import com.gps.ludke.repositorio.ComandaRepositorio;
import com.gps.ludke.repositorio.ProdutoRepositorio;
import com.gps.ludke.repositorio.RelatorioRepositorio;
import com.gps.ludke.repositorio.VendaRepositorio;
import com.gps.ludke.ui.relatorios.RelatorioUsuarioViewModel;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class EditarVendaFragment extends Fragment {

    private RelatorioUsuarioViewModel mViewModel;

    private RelatorioRepositorio relatorioRepositorio;
    private VendaRepositorio vendaRepositorio;
    private ComandaRepositorio comandaRepositorio;
    private DadosOpenHelper dadosOpenHelper;
    private SQLiteDatabase conexao;

    private ListView listView;

    private static ArrayList<Relatorio> relatorios;

    private RelatorioArrayAdapter relatorioArrayAdapter;

    private ProgressBar progressBarEditarVenda;

    private TextView textViewVazio;

    private ListagemRelatorioCardArrayAdapter listagemRelatorioCardArrayAdapter;

    private SwipeRefreshLayout pullToRefresh;

    private AlertDialog dialogCarregando;
    private AlertDialog dialogCliente;

    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private Relatorio relatorioAtual;
    private boolean existeMudanca;
    private static ArrayList<Cliente> clientes;

    private ClienteRepositorio clienteRepositorio;
    private ClienteArrayAdapter clienteArrayAdapter;
    private ProgressBar progressBarCliente;
    private ListView listViewCliente;
    private TextView textViewClienteVazio;

    private ArrayList<Produto> listaProduto;
    private ListagemCardArrayAdapter listagemCardArrayAdapter;
    private ProdutoRepositorio produtoRepositorio;
    private String filtroDataInicial;
    private String filtroDataFinal;

    private DatePickerDialog picker;

    public static EditarVendaFragment newInstance() {
        return new EditarVendaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root= inflater.inflate(R.layout.fragment_venda_editar, container, false);

        progressBarEditarVenda = root.findViewById(R.id.progressBarEditarVenda);

        progressBarEditarVenda.setVisibility(View.VISIBLE);

        criarConexao();

        textViewVazio = root.findViewById(R.id.textViewVazio);
        listView = root.findViewById(R.id.listViewRelatorio);

        listView.addHeaderView(new View(getActivity()));
        listView.addFooterView(new View(getActivity()));

        existeMudanca = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    relatorios = relatorioRepositorio.select(ItensPedido.getUsuarioLogado().getCpf());
                    Collections.reverse(relatorios);
                } catch (Exception e) {
                    e.printStackTrace();
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
                            progressBarEditarVenda.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final View mView = getLayoutInflater().inflate(R.layout.dialog_relatorio,null);
                final Relatorio relatorio = (Relatorio) listView.getItemAtPosition(position);
                relatorioAtual = relatorio;

                TextView textViewComanda = mView.findViewById(R.id.textViewNumCom);
                TextView textViewNomeVendedor = mView.findViewById(R.id.textViewNomeVendedor);
                final TextView textViewCliente = mView.findViewById(R.id.textViewNomeCliente);
                TextView textViewData = mView.findViewById(R.id.textViewData);
                final TextView textViewQuantdTotal = mView.findViewById(R.id.textViewQuantdTotal);
                final TextView textViewValorTotal = mView.findViewById(R.id.textViewValorTotal);


                final ListView listViewRelatorio = mView.findViewById(R.id.listViewRelatorio);

                ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);

                Button buttonAtualizar = mView.findViewById(R.id.buttonAtualizar);
                Button buttonExcluir = mView.findViewById(R.id.buttonExcluir);
                Button buttonAlterarCliente = mView.findViewById(R.id.buttonAlterarCliente);

                //textViewQuantdTotal.setText(Integer.toString(relatorio.getQuantidadeVendas()));
                textViewNomeVendedor.setText(relatorio.getComanda().getUsuario().getNome());
                textViewComanda.setText("Comanda "+relatorio.getComanda().getCodigo());
                if(relatorio.getComanda().getCliente() != null) {
                    textViewCliente.setText(relatorio.getComanda().getCliente().getNomeReduzido());
                }else{
                    textViewCliente.setText("não informado");
                }

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                textViewData.setText(dateFormat.format(relatorio.getComanda().getData()));

                //textViewValorTotal.setText(formatter.format(relatorio.getComanda().getTotal()).replace("R$",""));

                listViewRelatorio.addHeaderView(new View(getContext()));
                listViewRelatorio.addFooterView(new View(getContext()));

                listagemRelatorioCardArrayAdapter = new ListagemRelatorioCardArrayAdapter(getContext(),relatorio.getVendas());

                listViewRelatorio.setAdapter(listagemRelatorioCardArrayAdapter);

                listagemRelatorioCardArrayAdapter.setListener(new ListagemRelatorioCardArrayAdapter.Listener() {
                    @Override
                    public void atualizarVenda() {

                        double total = 0;
                        for (Venda venda : relatorio.getVendas()){
                            total += venda.getPreco();
                        }
                        relatorio.getComanda().setTotal(total);
                        textViewValorTotal.setText(formatter.format(relatorio.getComanda().getTotal()).replace("R$",""));
                        textViewQuantdTotal.setText(Integer.toString(relatorio.getQuantidadeVendas()));
                        existeMudanca=true;
                    }

                    @Override
                    public void alterarPeso(final int position) {


                        final View mView = getLayoutInflater().inflate(R.layout.dialog_edit_peso,null);

                        Venda vendaSelecionada = relatorio.getVendas().get(position);
                        final Produto produtoSelecionado = relatorio.getVendas().get(position).getProduto();
                        String nomeProd = produtoSelecionado.getNome();
                        Double precoKg = produtoSelecionado.getPrecoVenda();
                        String codProd = produtoSelecionado.getCodigo();


                        final EditText editTextPeso = (EditText) mView.findViewById(R.id.editTextPeso);
                        TextView textViewNomeProd = mView.findViewById(R.id.textViewNomeProd);
                        TextView textViewCodProd = mView.findViewById(R.id.textViewCodProd);
                        TextView textViewPrecoKg = mView.findViewById(R.id.textViewPrecoKg);

                        Button buttonAtualizar = mView.findViewById(R.id.buttonAtualizar);
                        Button buttonCancelar = mView.findViewById(R.id.buttonCancelar);
                        ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);

                        if(vendaSelecionada.getPeso()>0){
                            editTextPeso.setText(Double.toString(vendaSelecionada.getPeso()));
                        }

                        textViewCodProd.setText(codProd);
                        textViewNomeProd.setText(nomeProd);
                        textViewPrecoKg.setText(formatter.format(precoKg) + "/kg");

                        if(produtoSelecionado.getPeso()>0){
                            editTextPeso.setText(Double.toString(produtoSelecionado.getPeso()));
                        }

                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());

                        mBuilder.setView(mView);
                        final AlertDialog dialogPeso = mBuilder.create();
                        dialogPeso.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialogPeso.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogPeso.show();

                        imageButtonClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogPeso.dismiss();
                            }
                        });

                        buttonAtualizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Double peso = Double.valueOf(editTextPeso.getText().toString());
                                Double valor = produtoSelecionado.getPrecoVenda();
                                produtoSelecionado.setPeso(peso);
                                relatorio.getVendas().get(position).setPeso(peso);
                                relatorio.getVendas().get(position).setProduto(produtoSelecionado);
                                relatorio.getVendas().get(position).setPreco(peso*valor);
                                listagemRelatorioCardArrayAdapter.notifyDataSetChanged();
                                showToast("Peso Atualizado");
                                dialogPeso.dismiss();
                                atualizarVenda();
                            }
                        });

                        buttonCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogPeso.dismiss();
                            }
                        });


                    }
                });

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());


                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.setCancelable(false);
                dialog.show();


                imageButtonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(existeMudanca){
                            atualizarTela();
                        }
                        dialog.dismiss();
                    }
                });

                buttonAtualizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(relatorio.getVendas().size() > 0){
                            telaCarregando("Atualizando Venda");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        if(relatorio.getVendas().size() > 0){
                                            comandaRepositorio.atualizarComanda(relatorio.getComanda());
                                            vendaRepositorio.removerVendasComanda(relatorio.getComanda());
                                            for (Venda venda : relatorio.getVendas()) {
                                                vendaRepositorio.inserir(venda);
                                            }
                                        }

                                    }finally {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //dialogCarregando.dismiss();
                                                //carregarLista(false);
                                                Toast.makeText(getActivity(),"Venda Atualizada!",Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                atualizarTela();
                                                dialogCarregando.dismiss();
                                            }
                                        });
                                    }
                                }
                            }).start();

                        }else {
                            removerVenda(dialog, relatorio);
                        }
                    }
                });

                buttonExcluir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removerVenda(dialog, relatorio);
                    }
                });

                buttonAlterarCliente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final View mView = getLayoutInflater().inflate(R.layout.dialog_alterar_cliente,null);
                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

                        progressBarCliente = mView.findViewById(R.id.progressBarCliente);
                        listViewCliente = mView.findViewById(R.id.listViewCliente);
                        textViewClienteVazio = mView.findViewById(R.id.textViewVazio);

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
                                                textViewClienteVazio.setVisibility(View.VISIBLE);
                                            }else {
                                                textViewClienteVazio.setVisibility(View.GONE);
                                            }
                                            clienteArrayAdapter = new ClienteArrayAdapter(getActivity(),clientes);

                                            listViewCliente.setAdapter(clienteArrayAdapter);
                                            progressBarCliente.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }
                        }).start();

                        EditText editTextBusca = mView.findViewById(R.id.editTextBusca);

                        editTextBusca.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                if(clienteArrayAdapter != null) {
                                    clienteArrayAdapter.getFilter().filter(charSequence);
                                    clienteArrayAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        mBuilder.setView(mView);
                        final AlertDialog dialogCliente = mBuilder.create();
                        dialogCliente.setCancelable(false);
                        dialogCliente.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialogCliente.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogCliente.show();

                        ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);
                        imageButtonClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogCliente.dismiss();
                            }
                        });

                        listViewCliente.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                Cliente clienteSelecionado = (Cliente) listViewCliente.getItemAtPosition(position);
                                relatorio.getComanda().setCliente(clienteSelecionado);
                                textViewCliente.setText(relatorio.getComanda().getCliente().getNomeReduzido());
                                dialogCliente.dismiss();
                                showToast("Cliente Alterado");
                            }
                        });
                    }
                });


                ConstraintLayout constraintLayoutAdicionarProduto = mView.findViewById(R.id.constraintLayoutAdicionarProduto);
                constraintLayoutAdicionarProduto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final View mView = getLayoutInflater().inflate(R.layout.dialog_add_produto,null);
                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());


                        final ProgressBar progressBarAddProd = mView.findViewById(R.id.progressBarAddProd);
                        final ListView listViewAddProd = (ListView) mView.findViewById(R.id.listViewAddProd);

                        progressBarAddProd.setVisibility(View.VISIBLE);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try{
                                    listaProduto = produtoRepositorio.buscarTodosProdutosWeb();
                                }finally {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(listaProduto.isEmpty()){
                                                textViewVazio.setVisibility(View.VISIBLE);
                                            }else {
                                                textViewVazio.setVisibility(View.GONE);
                                            }
                                            progressBarAddProd.setVisibility(View.GONE);

                                            listagemCardArrayAdapter = new ListagemCardArrayAdapter(getContext(),listaProduto);

                                            listViewAddProd.setAdapter(listagemCardArrayAdapter);

                                        }
                                    });
                                }
                            }
                        }).start();

                        mBuilder.setView(mView);
                        final AlertDialog dialogProduto = mBuilder.create();
                        dialogProduto.show();

                        Button buttonVoltar = mView.findViewById(R.id.buttonVoltar);
                        buttonVoltar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogProduto.dismiss();
                            }
                        });

                        listViewAddProd.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                                final View mView = getLayoutInflater().inflate(R.layout.dialog_edit_peso,null);

                                Produto produtoSelecionado = (Produto) listViewAddProd.getItemAtPosition(position);
                                String nomeProd = produtoSelecionado.getNome();
                                Double precoKg = produtoSelecionado.getPrecoVenda();
                                String codProd = produtoSelecionado.getCodigo();


                                final EditText editTextPeso = (EditText) mView.findViewById(R.id.editTextPeso);
                                TextView textViewNomeProd = mView.findViewById(R.id.textViewNomeProd);
                                TextView textViewCodProd = mView.findViewById(R.id.textViewCodProd);
                                TextView textViewPrecoKg = mView.findViewById(R.id.textViewPrecoKg);

                                Button buttonAdicionar = mView.findViewById(R.id.buttonAtualizar);
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
                                final AlertDialog dialogPeso = mBuilder.create();
                                dialogPeso.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialogPeso.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialogPeso.show();

                                buttonCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        esconderTeclado();
                                        dialogPeso.dismiss();

                                    }
                                });

                                buttonAdicionar.setText("Adicionar");

                                buttonAdicionar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        try {
                                            if (!TextUtils.isEmpty(editTextPeso.getText())) {
                                                Double peso = Double.valueOf(editTextPeso.getText().toString());
                                                ((Produto) listViewAddProd.getItemAtPosition(position)).setPeso(peso);

                                                Produto produtoSelc = (Produto) listViewAddProd.getItemAtPosition(position);

                                                Venda venda = new Venda();
                                                venda.setProduto(produtoSelc);
                                                venda.setPeso(peso);
                                                venda.setComanda(relatorio.getComanda());
                                                venda.setPreco(produtoSelc.getPrecoVenda()*peso);

                                                relatorio.getVendas().add(venda);

                                                listagemCardArrayAdapter.notifyDataSetChanged();
                                                listagemRelatorioCardArrayAdapter.notifyDataSetChanged();

                                                Produto produto = (Produto) listViewAddProd.getItemAtPosition(position);
                                                Toast.makeText(getActivity(), Double.toString(produto.getPeso()) + "Kg de " + produto.getNome() + " adicionados ao pedido.", Toast.LENGTH_LONG).show();
                                                dialogPeso.dismiss();
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
                                        dialogPeso.dismiss();
                                    }
                                });
                            }
                        });


                        EditText editTextBusca = mView.findViewById(R.id.editTextBusca);

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

                        ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);
                        imageButtonClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogProduto.dismiss();
                            }
                        });

                    }
                });

            }


        });

        final EditText editTextBusca = root.findViewById(R.id.editTextBusca);

        editTextBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(relatorioArrayAdapter != null) {
                    relatorioArrayAdapter.setIsUser(true);
                    relatorioArrayAdapter.getFilter().filter(charSequence);
                    relatorioArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        pullToRefresh = root.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               atualizarTela();
            }
        });

        TextView textViewPeriodo = root.findViewById(R.id.textViewPeriodo);
        textViewPeriodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View mView = getLayoutInflater().inflate(R.layout.dialog_periodo,null);
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                mBuilder.setView(mView);
                final AlertDialog dialogFiltro = mBuilder.create();
                dialogFiltro.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogFiltro.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogFiltro.show();

                final EditText editTextDateIni = mView.findViewById(R.id.editTextDateIni);
                final EditText editTextDateFin = mView.findViewById(R.id.editTextDateFin);
                disableInput(editTextDateIni);
                disableInput(editTextDateFin);
                Button buttonFiltrar = mView.findViewById(R.id.buttonFiltrar);

                if(filtroDataInicial != null) {
                    editTextDateIni.setText(filtroDataInicial.toString());
                }
                if(filtroDataFinal != null) {
                    editTextDateFin.setText(filtroDataFinal.toString());
                }

                buttonFiltrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(relatorioArrayAdapter != null) {
                            filtroDataInicial = editTextDateIni.getText().toString();
                            filtroDataFinal = editTextDateFin.getText().toString();
                            //SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                            try {
                                if(filtroDataInicial.isEmpty() && filtroDataFinal.isEmpty()) {
                                    relatorioArrayAdapter.limparFiltro();

                                    showToast("Sem filtragem");
                                }else{
                                    relatorioArrayAdapter.filter(filtroDataInicial, filtroDataFinal);
                                    showToast("Filtragem efetuada");
                                }
                                dialogFiltro.dismiss();
                            }catch (ParseException e) {
                                showToast("Dados inseridos incorretamente");
                                e.printStackTrace();
                            }
                        }
                    }
                });

                Button buttonLimpar = mView.findViewById(R.id.buttonLimpar);
                buttonLimpar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!editTextDateIni.getText().toString().isEmpty() && !editTextDateFin.getText().toString().isEmpty()) {
                            editTextDateIni.setText("");
                            editTextDateFin.setText("");

                            filtroDataInicial = "";
                            filtroDataFinal = "";

                            relatorioArrayAdapter.limparFiltro();

                            showToast("Filtro removido");
                        }
                    }
                });

                ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);
                imageButtonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogFiltro.dismiss();
                    }
                });

                editTextDateIni.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar cldr = Calendar.getInstance();
                        int day = cldr.get(Calendar.DAY_OF_MONTH);
                        int month = cldr.get(Calendar.MONTH);
                        int year = cldr.get(Calendar.YEAR);
                        // date picker dialog
                        picker = new DatePickerDialog(getContext(), R.style.datepicker,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        editTextDateIni.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    }
                                }, year, month, day);
                        picker.show();
                    }
                });

                editTextDateFin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar cldr = Calendar.getInstance();
                        int day = cldr.get(Calendar.DAY_OF_MONTH);
                        int month = cldr.get(Calendar.MONTH);
                        int year = cldr.get(Calendar.YEAR);
                        // date picker dialog
                        picker = new DatePickerDialog(getContext(),  R.style.datepicker,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        editTextDateFin.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    }
                                }, year, month, day);
                        picker.show();
                    }
                });

            }
        });

        return root;
    }

    private void removerVenda(final AlertDialog dialogIni, final Relatorio relatorio){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setTitle("Tem certeza que quer excluir a venda?");
        mBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                telaCarregando("Removendo venda");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            vendaRepositorio.removerVendasComanda(relatorio.getComanda());
                            comandaRepositorio.removerComanda(relatorio.getComanda());
                        }finally {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //dialogCarregando.dismiss();
                                    //carregarLista(false);
                                    Toast.makeText(getActivity(),"Venda Removida!",Toast.LENGTH_SHORT).show();
                                    dialogIni.dismiss();
                                    atualizarTela();
                                    dialogCarregando.dismiss();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void disableInput(EditText editText){
        editText.setTextIsSelectable(false);
        editText.setFocusable(false);
    }

    private void atualizarTela(){
        existeMudanca = false;
        pullToRefresh.setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    relatorios= relatorioRepositorio.select(ItensPedido.getUsuarioLogado().getCpf());
                    Collections.reverse(relatorios);
                }finally {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (relatorios.isEmpty()) {
                                textViewVazio.setVisibility(View.VISIBLE);
                            } else {
                                textViewVazio.setVisibility(View.GONE);
                            }
                            relatorioArrayAdapter = new RelatorioArrayAdapter(getActivity(), relatorios);

                            listView.setAdapter(relatorioArrayAdapter);
                            progressBarEditarVenda.setVisibility(View.GONE);

                            pullToRefresh.setRefreshing(false);

                        }
                    });
                }
            }
        }).start();
    }

    private void esconderTeclado(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(getActivity().getCurrentFocus()!=null) {
            if (imm.isActive())
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    private void telaCarregando(String msg){
        showToast(msg);
        final View mView = getLayoutInflater().inflate(R.layout.dialog_load,null);
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

        mBuilder.setView(mView);
        dialogCarregando = mBuilder.create();
        dialogCarregando.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCarregando.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCarregando.show();
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RelatorioUsuarioViewModel.class);
        // TODO: Use the ViewModel
    }

    private void criarConexao(){
        try {

            dadosOpenHelper = new DadosOpenHelper(getContext());

            conexao = dadosOpenHelper.getWritableDatabase();

            //Snackbar.make(drawer,"Conexão criada com sucesso!",Snackbar.LENGTH_LONG)
            //      .setAction("Ok", null).show();

            produtoRepositorio = new ProdutoRepositorio(conexao);
            clienteRepositorio = new ClienteRepositorio(conexao);
            comandaRepositorio = new ComandaRepositorio(conexao);
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
