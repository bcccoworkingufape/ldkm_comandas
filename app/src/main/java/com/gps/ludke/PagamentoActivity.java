package com.gps.ludke;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Comanda;
import com.gps.ludke.entidade.Pagamento;
import com.gps.ludke.entidade.Parcela;
import com.gps.ludke.entidade.Produto;
import com.gps.ludke.entidade.Relatorio;
import com.gps.ludke.entidade.Venda;
import com.gps.ludke.repositorio.ComandaRepositorio;
import com.gps.ludke.repositorio.VendaRepositorio;
import com.gps.ludke.util.UnicodeFormatter;
import com.gps.ludke.util.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


public class PagamentoActivity extends AppCompatActivity {
    private DecimalFormat form = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private ArrayList<Parcela> arrayListParcelas = new ArrayList<>();
    private PagamentoAdapter pagamentoAdapter;

    private ComandaRepositorio comandaRepositorio;
    private VendaRepositorio vendaRepositorio;
    private DadosOpenHelper dadosOpenHelper;
    private SQLiteDatabase conexao;

    private boolean vendaOk;
    private boolean pagamentoOk;
    private int idVendas;
    private int idPagamento;

    private Pagamento pagamento;

    private ProgressBar progressPagamento;

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    protected static final String TAG = "TAG";
    private BluetoothDevice mBluetoothDevice;
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Comanda comandaSeleciona;
    private ArrayList<Venda> vendasArray = new ArrayList<>();

    private String mDeviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);
        Toolbar toolbar = findViewById(R.id.toolbar);

        carregarFormasPagamento();

        toolbar.setTitle("Voltar");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView textViewNumeroComanda = findViewById(R.id.textViewNumeroComanda);
        TextView textViewTotal = findViewById(R.id.textViewTotal);
        ListView listViewCarrinho = findViewById(R.id.listViewCarrinho);
        Spinner spinnerParcelas = findViewById(R.id.spinnerParcelas);
        Button buttonFinalizarComanda = findViewById(R.id.buttonFinalizarComanda);
        progressPagamento = findViewById(R.id.progressPagamento);

        if(ItensPedido.getComandaSelecionada() != null) {
            textViewNumeroComanda.setText(Integer.toString(ItensPedido.getComandaSelecionada().getCodigo()));
            textViewTotal.setText(formatter.format(ItensPedido.getComandaSelecionada().getTotal()));
        }

        final ArrayList arrayList = new ArrayList();

        for(int i=1;i<=12;i++){
            arrayList.add(i);
        }

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParcelas.setAdapter(spinnerAdapter);

        spinnerParcelas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorTexto));
                ((TextView) adapterView.getChildAt(0)).setTextSize(12);
                //arrayListParcelas.clear();
                int posicao = position+1;
                int tamanhoList = arrayListParcelas.size();
                if(tamanhoList==0 && posicao==1){
                    Parcela parcela = new Parcela(1);
                    parcela.setValor(ItensPedido.getComandaSelecionada().getTotal());
                    Date date = new Date();
                    parcela.setVencimento(date);
                    parcela.setFormaPagamento("À VISTA");
                    arrayListParcelas.add(parcela);
                    double soma = 0;
                    for(Parcela parc : arrayListParcelas){
                        soma += parc.getValor();
                    }
                    pagamento.setSomaValoresParcelas(soma);
                }else if(tamanhoList<posicao) {
                    for (int k = tamanhoList+1; k <= posicao; k++) {
                        Parcela parcela = new Parcela(k);
                        arrayListParcelas.add(parcela);
                    }
                }else if(tamanhoList>posicao){
                    for(int k = tamanhoList-1; k>=posicao; k--){
                        arrayListParcelas.remove(k);
                    }
                }
                pagamentoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        pagamento = new Pagamento();

        listViewCarrinho.addHeaderView(new View(this));
        listViewCarrinho.addFooterView(new View(this));

        pagamentoAdapter = new PagamentoAdapter(this, arrayListParcelas);
        listViewCarrinho.setAdapter(pagamentoAdapter);
        pagamentoAdapter.setListener(new PagamentoAdapter.Listener() {
            @Override
            public void atualizarSoma() {
                double soma = 0;
                for(Parcela parcela : arrayListParcelas){
                    soma += parcela.getValor();
                }
                pagamento.setSomaValoresParcelas(soma);
            }
        });

        buttonFinalizarComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pagamento.setValorTotalPagamento(ItensPedido.getComandaSelecionada().getTotal());
                double resultadoParcelas = pagamento.getValorTotalPagamento() - pagamento.getSomaValoresParcelas();
                String resultaParcelaTru = new DecimalFormat("#,##0.00").format(resultadoParcelas).replace(",", ".");
                resultadoParcelas = Double.parseDouble(resultaParcelaTru);
                if(resultadoParcelas == 0) {
                    telaCarregando();
                    venda();
                }else if(resultadoParcelas<0){
                    showToast("O valor dos pagamentos está maior do que o valor da compra.");
                }
                else{
                    showToast("Ainda existe " + formatter.format(resultadoParcelas).replace("-","") + " pendentes.");
                }
            }
        });

    }

    private void carregarFormasPagamento(){
        criarConexao();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    ArrayList formasPagamento = comandaRepositorio.getFormasPagamento();
                    ItensPedido.setFormasPagamento(formasPagamento);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void venda(){
        ItensPedido.getComandaSelecionada().setCliente(ItensPedido.getClienteSelecionado());
        new Thread(new Runnable() {
            @Override
            public void run() {
                int numCom = 0;
                try{
                    numCom = comandaRepositorio.inserir(ItensPedido.getComandaSelecionada());

                }finally {
                    final int finalNumCom = numCom;
                    runOnUiThread(new Runnable() {
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

                                            pagamento.setComanda(ItensPedido.getComandaSelecionada());
                                            pagamento.setParcelas(arrayListParcelas);

                                            for (Parcela parcela : pagamento.getParcelas()){
                                                if(parcela.getVencimento() != null && parcela.getValor() != 0 && parcela.getFormaPagamento() != null) {
                                                    idPagamento += comandaRepositorio.insertPagamento(pagamento, parcela);
                                                }
                                            }
                                        }finally {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (idVendas > 0) {
                                                        vendaOk = true;
                                                    } else {
                                                        vendaOk = false;
                                                    }
                                                    if(idPagamento >0){
                                                        pagamentoOk = true;
                                                    }else{
                                                        pagamentoOk = false;
                                                    }

                                                    //progressPagamento.setVisibility(View.GONE);
                                                    if(vendaOk && pagamentoOk){
                                                        showToast("Venda efetuada!");
                                                        comandaSeleciona = ItensPedido.getComandaSelecionada();
                                                        vendasArray = vendasArrayList;
                                                        perguntarImpressao();
                                                    }else{
                                                        showToast("Erro ao efetuar venda!");
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
        showToast("Efetuando Venda");
        final View mView = getLayoutInflater().inflate(R.layout.dialog_load,null);
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(PagamentoActivity.this);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void perguntarImpressao(){

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(PagamentoActivity.this);
        mBuilder.setTitle("Deseja fazer a impressão da comanda?");
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dialogInterface.dismiss();
                /*
                final View mView = getLayoutInflater().inflate(R.layout.dialog_relatorio_venda,null);
                final Relatorio relatorio = new Relatorio(comanda, vendas);

                TextView textViewNomeCliente = mView.findViewById(R.id.textViewNomeCliente);
                TextView textViewCnpjCpfDado = mView.findViewById(R.id.textViewCnpjCpfDado);
                TextView textViewData = mView.findViewById(R.id.textViewData);
                TextView textViewQuantdTotal = mView.findViewById(R.id.textViewQuantdTotal);
                TextView textViewValorTotal = mView.findViewById(R.id.textViewValorTotal);
                ListView listViewRelatorio = mView.findViewById(R.id.listViewRelatorio);
                ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);
                TextView textViewNumCom = mView.findViewById(R.id.textViewNumCom);


                textViewNomeCliente.setText(relatorio.getComanda().getCliente().getNomeReduzido());
                textViewCnpjCpfDado.setText(relatorio.getComanda().getCliente().getCpf());
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                textViewData.setText(dateFormat.format(relatorio.getComanda().getData()));
                textViewQuantdTotal.setText(Integer.toString(relatorio.getVendas().size()));
                textViewValorTotal.setText(formatter.format(relatorio.getComanda().getTotal()).replace("R$",""));
                textViewNumCom.setText("Comanda Nº " + comanda.getCodigo());

                listViewRelatorio.addHeaderView(new View(PagamentoActivity.this));
                listViewRelatorio.addFooterView(new View(PagamentoActivity.this));

                ListagemRelatorioVendaAdapter listagemRelatorioVendaAdapter = new ListagemRelatorioVendaAdapter(PagamentoActivity.this,relatorio.getVendas());

                listViewRelatorio.setAdapter(listagemRelatorioVendaAdapter);

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(PagamentoActivity.this);

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
                        Intent intent = new Intent(PagamentoActivity.this, MenuActivity.class);
                        PagamentoActivity.this.startActivity(intent);

                    }
                });
              */

                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(PagamentoActivity.this, "Problema de conexão bluetooth", Toast.LENGTH_SHORT).show();
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
                        Relatorio relatorio = new Relatorio(comandaSeleciona, vendasArray);
                        Impressao.escolherDispositivoBluetooth(PagamentoActivity.this, relatorio, true);
                    }
                }
            }
        });

        mBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                ItensPedido.limparPedido();
                Intent intent = new Intent(PagamentoActivity.this, MenuActivity.class);
                PagamentoActivity.this.startActivity(intent);
            }
        });
        AlertDialog dialog = mBuilder.create();
        dialog.show();
        mBuilder.setCancelable(false);


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

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    try {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mBluetoothSocket = mBluetoothDevice
                                            .createRfcommSocketToServiceRecord(applicationUUID);
                                    mBluetoothAdapter.cancelDiscovery();
                                    mBluetoothSocket.connect();
                                    mHandler.sendEmptyMessage(0);
                                } catch (IOException eConnectException) {
                                    Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
                                    closeSocket(mBluetoothSocket);
                                    return;
                                } finally {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //imprimir();
                                            Impressao.imprimir(true);
                                        }
                                    });
                                }
                            }
                        }).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Relatorio relatorio = new Relatorio(comandaSeleciona, vendasArray);
                    Impressao.escolherDispositivoBluetooth(PagamentoActivity.this, relatorio, true);;
                } else {
                    Toast.makeText(PagamentoActivity.this, "Erro na permissão para ativar bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void imprimir(){
        Thread t = new Thread() {
            public void run() {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                    OutputStream os = mBluetoothSocket
                            .getOutputStream();
                    imprimirImg(os);
                    //printNewLine(os);
                    os.write(PrinterCommands.ESC_ALIGN_CENTER);
                    String comanda = "Comanda N " +comandaSeleciona.getCodigo();
                    os.write(comanda.getBytes());
                    printNewLine(os);
                    os.write(PrinterCommands.ESC_ALIGN_LEFT);
                    System.out.println("INICIANDO O TEXTO");

                    String BILL = "";

                    BILL =  String.format("%1$-6s %2$23s","Cliente:", comandaSeleciona.getCliente().getNomeReduzido()) +"\n" +
                            String.format("%1$-6s %2$22s","CNPJ/CPF:", comandaSeleciona.getCliente().getCpf()) +"\n" +
                            String.format("%1$-6s %2$25s","Data: ", dateFormat.format(comandaSeleciona.getData()))+"\n"
                    ;
                    BILL = BILL
                            + "--------------------------------\n";



                            BILL = BILL + String.format("%1$-6s %2$7s %3$8s %4$7s", "Produto", "Peso", "Prc(Kg)", "Val(R$)");
                            BILL = BILL + "\n";
                            BILL = BILL
                                    + "--------------------------------";
                            for(Venda venda:vendasArray){
                                String nomeProdutoCompleto = venda.getProduto().getNome();
                                String nomeProduto;
                                if (nomeProdutoCompleto.length() <= 6) {
                                    nomeProduto = nomeProdutoCompleto;
                                } else {
                                    nomeProduto = nomeProdutoCompleto.substring(0, 6);
                                }
                                BILL = BILL + "\n" + String.format("%1$-6s %2$8s %3$7s %4$8s",   nomeProduto, venda.getPeso(), form.format(venda.getProduto().getPrecoVenda()), form.format(venda.getProduto().getPrecoVenda()*venda.getPeso()));
                            }
                            BILL = BILL
                                    + "\n--------------------------------";
                            BILL = BILL + "\n";

                            BILL = BILL + String.format("%1$-6s %2$22s","Qtd Total:", vendasArray.size() + "\n");
                            BILL = BILL + String.format("%1$-6s %2$20s","Valor Total:", "R$"+ form.format(comandaSeleciona.getTotal()) + "\n");

                            BILL = BILL
                                    + "--------------------------------\n";
                            BILL = BILL + "\n\n ";

                    os.write(BILL.getBytes());
                    //This is printer specific code you can comment ==== > Start

                    // Setting height
                    int gs = 29;
                    os.write(intToByteArray(gs));
                    int h = 104;
                    os.write(intToByteArray(h));
                    int n = 162;
                    os.write(intToByteArray(n));

                    // Setting Width
                    int gs_width = 29;
                    os.write(intToByteArray(gs_width));
                    int w = 119;
                    os.write(intToByteArray(w));
                    int n_width = 2;
                    os.write(intToByteArray(n_width));

                } catch (Exception e) {
                    Log.e("PagamentoActivity", "Exe ", e);
                    System.out.println("OLHA O ERROOOO! " + e.toString());
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //if (mBluetoothSocket != null)
                                    //mBluetoothSocket.close();
                            } catch (Exception e) {
                                Log.e("Tag", "Exe ", e);
                            }
                            ItensPedido.limparPedido();
                            Intent intent = new Intent(PagamentoActivity.this, MenuActivity.class);
                            PagamentoActivity.this.startActivity(intent);
                        }
                    });
                }
            }
        };
        t.start();
    }

    private void imprimirImg(OutputStream outputStream){
        //print command
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //outputStream = btsocket.getOutputStream();

        printPhoto(R.drawable.ldk_logo_preto, outputStream);

        //outputStream.flush();
    }

    public void printPhoto(int img, OutputStream outputStream) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    img);
            if(bmp!=null){
                byte[] command = Utils.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command, outputStream);
            }else{
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    private void printText(byte[] msg, OutputStream outputStream) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printText(String msg, OutputStream outputStream) {
        try {
            // Print normal text
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void printNewLine(OutputStream outputStream) {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(PagamentoActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void criarConexao(){
        try {

            dadosOpenHelper = new DadosOpenHelper(this);

            conexao = dadosOpenHelper.getWritableDatabase();

            vendaRepositorio = new VendaRepositorio(conexao);
            comandaRepositorio = new ComandaRepositorio(conexao);

        }catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("OK",null);
            dlg.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}