package com.gps.ludke;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.gps.ludke.entidade.Comanda;
import com.gps.ludke.entidade.Relatorio;
import com.gps.ludke.entidade.Venda;
import com.gps.ludke.util.UnicodeFormatter;
import com.gps.ludke.util.Utils;
import com.itextpdf.text.pdf.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/***
        *Criado por Alessandro Marques on 20/08/2020
        */

public class Impressao {
    private static BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    protected static final String TAG = "TAG";
    private static BluetoothDevice mBluetoothDevice;
    private static ProgressDialog mBluetoothConnectProgressDialog;
    private static BluetoothSocket mBluetoothSocket;
    private static UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static Comanda comandaSeleciona;
    private static ArrayList<Venda> vendasArray = new ArrayList<>();
    private static String mDeviceAddress;

    private static Context context;
    private static DecimalFormat form = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));

    private static int tentativaImpressao;

    private static AlertDialog dialogDispositivos;

    public static void imprimir(final boolean isPedido){
        Thread t = new Thread() {
            public void run() {
                boolean photoOk = false;
                boolean textoOk = false;
                try {
                    OutputStream os = mBluetoothSocket
                            .getOutputStream();
                    printPhoto(os);

                    os.write(PrinterCommands.ESC_ALIGN_CENTER);
                    String comanda = "Comanda N " +comandaSeleciona.getCodigo();
                    //os.write(comanda.getBytes());
                    photoOk = printText(comanda.getBytes(), os);
                    printNewLine(os);
                    os.write(PrinterCommands.ESC_ALIGN_LEFT);
                    textoOk = printText(deAccent(textoImpressao()).getBytes(), os);
                    os.write(PrinterCommands.ESC_ALIGN_CENTER);
                    String notFiscal;
                    notFiscal =  "CUPOM PARA SIMPLES CONFERENCIA\n";
                    notFiscal = notFiscal
                            + "***NAO E DOCUMENTO FISCAL***\n";
                    notFiscal = notFiscal + "\n\n ";
                    //os.write(notFiscal.getBytes());
                    printText(notFiscal.getBytes(), os);

                } catch (Exception e) {
                    Log.e("PagamentoActivity", "Exe ", e);
                    showToast("Erro na impressão!");
                } finally {
                    final boolean finalPhotoOk = photoOk;
                    final boolean finalTextoOk = textoOk;
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(finalPhotoOk && finalTextoOk) {
                                    //if (mBluetoothSocket != null)
                                     //   mBluetoothSocket.close();
                                    tentativaImpressao = 0;
                                }else if(!finalPhotoOk){
                                    showToast("Erro na impressão da imagem");
                                    conectarBlue(true, true, isPedido);
                                }else if(!finalTextoOk){
                                    showToast("Erro na impressão do texto");
                                }
                            } catch (Exception e) {
                                Log.e("Tag", "Exe ", e);
                            }
                            if(isPedido) {
                                ItensPedido.limparPedido();
                                Intent intent = new Intent(context, MenuActivity.class);
                                context.startActivity(intent);
                            }else{
                                dialogDispositivos.dismiss();
                            }
                        }
                    });
                }
            }
        };
        t.start();
    }

    public static void printPhoto(OutputStream outputStream) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int img = R.drawable.ldk_logo_preto;
        try {
            Bitmap bmp = BitmapFactory.decodeResource(((Activity)context).getResources(),
                    img);
            if(bmp!=null){
                byte[] command = Utils.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command, outputStream);
            }else{
                Log.e("Print Photo error", "the file isn't exists");
                showToast("Erro ao carregar imagem para impressão¹");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists = " + e.getMessage());
            showToast("Erro ao carregar imagem para impressão");
        }
    }

    private static String textoImpressao(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String BILL = "";

        BILL =  String.format("%1$-6s %2$23s","Cliente:", comandaSeleciona.getCliente().getNomeReduzido()) +"\n" +
                String.format("%1$-6s %2$22s","CNPJ/CPF:", comandaSeleciona.getCliente().getCpf()) +"\n" +
                String.format("%1$-6s %2$25s","Data: ", dateFormat.format(comandaSeleciona.getData()))+"\n"
        ;
        BILL = BILL
                + "--------------------------------\n";
        BILL = BILL + String.format(" %1$-1s %2$4s %3$5s %4$6s %5$5s", "Cód", "Nome", "Peso", "Prc(Kg)", "Val(R$)");
        BILL = BILL + "\n";
        BILL = BILL
                + "--------------------------------";
        for(Venda venda:vendasArray){
            BILL = BILL + "\n " + venda.getProduto().getCodigo() + " " + venda.getProduto().getNome();
            String valor =  form.format(venda.getProduto().getPrecoVenda()*venda.getPeso());
            if(valor.length() <= 5) {
                BILL = BILL + "\n" + String.format(" %1$1s %2$3s %3$17s ", venda.getPeso(), "X " + form.format(venda.getProduto().getPrecoVenda()), form.format(venda.getProduto().getPrecoVenda() * venda.getPeso()));
            }else if(valor.length() >= 6){
                BILL = BILL + "\n" + String.format(" %1$1s %2$3s %3$16s ", venda.getPeso(), "X " + form.format(venda.getProduto().getPrecoVenda()), form.format(venda.getProduto().getPrecoVenda() * venda.getPeso()));
            }

        }
        BILL = BILL
                + "\n--------------------------------";

        BILL = BILL + "\n";

        BILL = BILL + String.format("%1$-6s %2$22s","Qtd Total:", vendasArray.size() + "\n");
        BILL = BILL + String.format("%1$-6s %2$20s","Valor Total:", "R$"+ form.format(comandaSeleciona.getTotal()) + "\n");

        BILL = BILL
                + "--------------------------------\n";
        return BILL;
    }

    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    private static boolean printText(byte[] msg, OutputStream outputStream) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine(outputStream);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void printText(String msg, OutputStream outputStream) {
        try {
            // Print normal text
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void printNewLine(OutputStream outputStream) {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void escolherDispositivoBluetooth(Context context1, Relatorio relatorio, final boolean isPedido){
        comandaSeleciona = relatorio.getComanda();
        vendasArray = relatorio.getVendas();
        context = context1;
        View mView =  ((Activity)context).getLayoutInflater().inflate(R.layout.device_list,null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setCancelable(false);
        mBuilder.setView(mView);
        dialogDispositivos = mBuilder.create();
        dialogDispositivos.show();

        ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);

        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDispositivos.dismiss();
                if(isPedido) {
                    ItensPedido.limparPedido();
                    Intent intent = new Intent(context, MenuActivity.class);
                    context.startActivity(intent);
                    showToast("A impressão pode ser efetuada na tela de relatório da venda.");
                }
            }
        });

        ArrayAdapter<String> mPairedDevicesArrayAdapter;
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(context, R.layout.device_name);

        ListView mPairedListView = (ListView) mView.findViewById(R.id.paired_devices);
        mPairedListView.setAdapter(mPairedDevicesArrayAdapter);


        mPairedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {

                    mBluetoothAdapter.cancelDiscovery();
                    String mDeviceInfo = ((TextView) view).getText().toString();
                    mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length() - 17);
                    Log.v(TAG, "Endereço do dispositivo " + mDeviceAddress);

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    conectarBlue(false, false, isPedido);
                }
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();

        if (mPairedDevices.size() > 0) {
            mView.findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice mDevice : mPairedDevices) {
                mPairedDevicesArrayAdapter.add(mDevice.getName() + "\n" + mDevice.getAddress());
            }
        } else {
            String mNoDevices = "Nenhum dispositivo pareado";//getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(mNoDevices);
        }

    }
    private static void conectarBlue(final boolean reconectando, final boolean photo, final boolean isPedido) {
        if(tentativaImpressao <=3) {
            if (reconectando) {
                tentativaImpressao++;
            }
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.cancelDiscovery();
            }
            Log.v(TAG, "Endereço de entrada " + mDeviceAddress);
            mBluetoothDevice = mBluetoothAdapter
                    .getRemoteDevice(mDeviceAddress);
            if (!reconectando) {
                mBluetoothConnectProgressDialog = ProgressDialog.show(context,
                        "Conectando...", mBluetoothDevice.getName() + " : "
                                + mBluetoothDevice.getAddress(), true, false);
            } else {
                mBluetoothConnectProgressDialog = ProgressDialog.show(context,
                        "Reconectando...", mBluetoothDevice.getName() + " : "
                                + mBluetoothDevice.getAddress(), true, false);
            }
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mBluetoothSocket = mBluetoothDevice
                                    .createInsecureRfcommSocketToServiceRecord(applicationUUID);
                            mBluetoothAdapter.cancelDiscovery();
                            mBluetoothSocket.connect();
                            mHandler.sendEmptyMessage(0);
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!reconectando || photo) {
                                        imprimir(isPedido);
                                    } else {

                                    }
                                }
                            });
                        } catch (IOException eConnectException) {
                            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
                            closeSocket(mBluetoothSocket);
                            mBluetoothConnectProgressDialog.dismiss();
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("Erro na conexão com a impressora! Tente novamente.");
                                }
                            });
                            return;
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            tentativaImpressao = 0;
        }
    }
    private static void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(context, "Impressora Conectada. Efetuando impressão", Toast.LENGTH_SHORT).show();
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

    public static void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}
