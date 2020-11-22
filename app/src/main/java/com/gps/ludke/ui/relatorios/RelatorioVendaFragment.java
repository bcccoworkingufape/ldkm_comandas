package com.gps.ludke.ui.relatorios;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gps.ludke.Impressao;
import com.gps.ludke.ItensPedido;
import com.gps.ludke.ListagemRelatorioCardArrayAdapter;
import com.gps.ludke.ListagemRelatorioVendaAdapter;
import com.gps.ludke.PagamentoActivity;
import com.gps.ludke.R;
import com.gps.ludke.RelatorioArrayAdapter;
import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Produto;
import com.gps.ludke.entidade.Relatorio;
import com.gps.ludke.entidade.Venda;
import com.gps.ludke.repositorio.ClienteRepositorio;
import com.gps.ludke.repositorio.ComandaRepositorio;
import com.gps.ludke.repositorio.ProdutoRepositorio;
import com.gps.ludke.repositorio.RelatorioRepositorio;
import com.gps.ludke.repositorio.VendaRepositorio;
import com.gps.ludke.ui.cliente.ListagemClienteFragment;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class RelatorioVendaFragment extends Fragment {
    private ProgressBar progressBarRelatorioVenda;
    private TextView textViewVazio;
    private ListView listView;

    private RelatorioRepositorio relatorioRepositorio;
    private VendaRepositorio vendaRepositorio;
    private ComandaRepositorio comandaRepositorio;
    private DadosOpenHelper dadosOpenHelper;
    private SQLiteDatabase conexao;
    private ClienteRepositorio clienteRepositorio;
    private ProdutoRepositorio produtoRepositorio;

    private static ArrayList<Relatorio> relatorios;

    private RelatorioArrayAdapter relatorioArrayAdapter;
    private SwipeRefreshLayout pullToRefresh;

    private String filtroDataInicial;
    private String filtroDataFinal;
    private boolean existeMudanca;

    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private ListagemRelatorioVendaAdapter listagemRelatorioVendaAdapter;

    public static RelatorioVendaFragment newInstance() {
        return new RelatorioVendaFragment();
    }

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    protected static final String TAG = "TAG";
    private BluetoothDevice mBluetoothDevice;
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private DatePickerDialog picker;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_relatorio_venda, container, false);

        progressBarRelatorioVenda = root.findViewById(R.id.progressBarRelatorioVenda);

        progressBarRelatorioVenda.setVisibility(View.VISIBLE);

        criarConexao();

        existeMudanca = false;

        textViewVazio = root.findViewById(R.id.textViewVazio);
        listView = root.findViewById(R.id.listViewRelatorio);

        listView.addHeaderView(new View(getActivity()));
        listView.addFooterView(new View(getActivity()));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    relatorios = relatorioRepositorio.select(ItensPedido.getUsuarioLogado().getCpf());
                    Collections.reverse(relatorios);
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
                            progressBarRelatorioVenda.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final View mView = getLayoutInflater().inflate(R.layout.dialog_relatorio_venda,null);
                final Relatorio relatorio = (Relatorio) listView.getItemAtPosition(position);

                TextView textViewNomeCliente = mView.findViewById(R.id.textViewNomeCliente);
                TextView textViewCnpjCpfDado = mView.findViewById(R.id.textViewCnpjCpfDado);
                TextView textViewData = mView.findViewById(R.id.textViewData);
                TextView textViewQuantdTotal = mView.findViewById(R.id.textViewQuantdTotal);
                TextView textViewValorTotal = mView.findViewById(R.id.textViewValorTotal);
                ListView listViewRelatorio = mView.findViewById(R.id.listViewRelatorio);
                ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);
                TextView textViewNumCom = mView.findViewById(R.id.textViewNumCom);
                Button buttonImprimir = mView.findViewById(R.id.buttonImprimir);


                textViewNomeCliente.setText(relatorio.getComanda().getCliente().getNomeReduzido());
                textViewCnpjCpfDado.setText(relatorio.getComanda().getCliente().getCpf());
                textViewData.setText(relatorio.getComanda().getData().toString());
                textViewQuantdTotal.setText(Integer.toString(relatorio.getVendas().size()));
                textViewValorTotal.setText(formatter.format(relatorio.getComanda().getTotal()).replace("R$",""));
                textViewNumCom.setText("Comanda Nº " + relatorio.getComanda().getCodigo());

                listViewRelatorio.addHeaderView(new View(getContext()));
                listViewRelatorio.addFooterView(new View(getContext()));

                listagemRelatorioVendaAdapter = new ListagemRelatorioVendaAdapter(getContext(),relatorio.getVendas());

                listViewRelatorio.setAdapter(listagemRelatorioVendaAdapter);

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());

                mBuilder.setView(mView);
                final AlertDialog dialogVendaRelatorio = mBuilder.create();
                dialogVendaRelatorio.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogVendaRelatorio.show();

                imageButtonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogVendaRelatorio.dismiss();
                    }
                });

                buttonImprimir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (mBluetoothAdapter == null) {
                            Toast.makeText(getActivity(), "Problema de conexão bluetooth", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!mBluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(
                                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent,
                                        REQUEST_ENABLE_BT);


                            } else {
                                ListPairedDevices();
                                //Intent connectIntent = new Intent(PagamentoActivity.this,
                                //       DeviceListActivity.class);
                                //startActivityForResult(connectIntent,
                                //       REQUEST_CONNECT_DEVICE);
                                Impressao.escolherDispositivoBluetooth(getActivity(), relatorio, false);
                            }
                        }


                    }
                });

            }
        });

        Button buttonVoltar = root.findViewById(R.id.buttonVoltar);
        buttonVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogRelatorios();
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

            }
        });


        return root;
    }

    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "Dispositivis pareados: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    private void abrirDialogRelatorios(){
        final View mView = getLayoutInflater().inflate(R.layout.dialog_relatorios,null);
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setCancelable(false);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
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
                            progressBarRelatorioVenda.setVisibility(View.GONE);

                            pullToRefresh.setRefreshing(false);

                        }
                    });
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

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
