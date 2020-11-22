package com.gps.ludke;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Parcela;
import com.gps.ludke.repositorio.ComandaRepositorio;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PagamentoAdapter extends ArrayAdapter<Parcela> {
    private Context context;
    private ArrayList<Parcela> pagamentoArrayList = new ArrayList<Parcela>();
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private ComandaRepositorio comandaRepositorio;
    private DadosOpenHelper dadosOpenHelper;
    private SQLiteDatabase conexao;

    private  ArrayList formasPagamento;

    private Listener mListener;

    private DatePickerDialog picker;

    public PagamentoAdapter(Context context, ArrayList<Parcela> lista){
        super(context, 0,lista);
        this.context = context;
        this.pagamentoArrayList = lista;
    }

    @Override
    public int getCount() {
        return this.pagamentoArrayList.size();
    }

    @Override
    public Parcela getItem(int index) {
        return this.pagamentoArrayList.get(index);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Parcela parcelaPosicao = this.pagamentoArrayList.get(position);

        convertView = LayoutInflater.from(this.context).inflate(R.layout.item_pagamento_card,null);

        TextView textViewId = convertView.findViewById(R.id.textViewId);
        TextView textViewData = convertView.findViewById(R.id.textViewData);
        TextView textViewFormaPagamento = convertView.findViewById(R.id.textViewFormaPagamento);
        TextView textViewPreco = convertView.findViewById(R.id.textViewPreco);
        TextView textViewPorcentagem = convertView.findViewById(R.id.textViewPorcentagem);
        ImageButton imageButtonAcao = convertView.findViewById(R.id.imageButtonAcao);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if(parcelaPosicao.getVencimento() != null) {
            textViewData.setText(dateFormat.format(parcelaPosicao.getVencimento()));
        }else{
            textViewData.setText("...");
        }
        textViewId.setText(Integer.toString(parcelaPosicao.getId()));
        if(parcelaPosicao.getFormaPagamento() != null) {
            textViewFormaPagamento.setText(parcelaPosicao.getFormaPagamento());
        }else{
            textViewFormaPagamento.setText("...");
        }
        if(parcelaPosicao.getValor() != 0.0){
            textViewPreco.setText(formatter.format(parcelaPosicao.getValor()).replace("R$",""));
        }else{
            textViewPreco.setText("...");
        }
        if(parcelaPosicao.getDescontoPagamento() != null && parcelaPosicao.getDescontoPagamento() != "0%"){
            textViewPorcentagem.setText(parcelaPosicao.getDescontoPagamento());
        }else{
            textViewPorcentagem.setBackground(context.getResources().getDrawable(R.drawable.porcent_shape_gray));
            parcelaPosicao.setDescontoPagamento("0%");

        }

        imageButtonAcao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

                final View mView = inflater.inflate(R.layout.dialog_parcela,null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                final Spinner spinnerPagamento = mView.findViewById(R.id.spinnerPagamento);
                final EditText editTextData = mView.findViewById(R.id.editTextData);
                disableInput(editTextData);
                final EditText editTextValor = mView.findViewById(R.id.editTextValor);
                Button buttonConcluir = mView.findViewById(R.id.buttonConcluir);
                Button buttonVoltar = mView.findViewById(R.id.buttonVoltar);
                TextView textViewValorTotal = mView.findViewById(R.id.textViewValorTotal);
                ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);
                TextView textViewValor = mView.findViewById(R.id.textViewValor);
                TextView textViewParcela = mView.findViewById(R.id.textViewParcela);

                editTextValor.setText(Double.toString(parcelaPosicao.getValor()));




                formasPagamento = ItensPedido.getFormasPagamento();

                if(formasPagamento != null){
                    final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, formasPagamento);

                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPagamento.setAdapter(spinnerAdapter);
                    for (int i=0;i<formasPagamento.size();i++) {
                        if (formasPagamento.get(i).toString().equalsIgnoreCase(parcelaPosicao.getFormaPagamento())) {
                            spinnerPagamento.setSelection(i);
                        }
                    }
                    if(parcelaPosicao.getVencimento()!=null){
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String currentDateandTime = sdf.format(parcelaPosicao.getVencimento());
                        editTextData.setText(currentDateandTime);

                    }
                    if(ItensPedido.getComandaSelecionada().getTotal() >0){
                        textViewValorTotal.setText(formatter.format(ItensPedido.getComandaSelecionada().getTotal()));
                    }
                }else{
                    criarConexao();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                formasPagamento = comandaRepositorio.getFormasPagamento();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }finally {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, formasPagamento);

                                        for (int i=0;i<spinnerPagamento.getCount();i++){
                                            if (spinnerPagamento.getItemAtPosition(i).toString().equalsIgnoreCase(parcelaPosicao.getFormaPagamento())){
                                                spinnerPagamento.setSelection(i);
                                            }
                                        }

                                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinnerPagamento.setAdapter(spinnerAdapter);
                                    }
                                });
                            }
                        }
                    }).start();
                }

                editTextData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!spinnerPagamento.getSelectedItem().toString().equals("À VISTA")) {
                            final Calendar cldr = Calendar.getInstance();
                            int day = cldr.get(Calendar.DAY_OF_MONTH);
                            int month = cldr.get(Calendar.MONTH);
                            int year = cldr.get(Calendar.YEAR);
                            // date picker dialog
                            picker = new DatePickerDialog(context, R.style.datepicker,
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                            editTextData.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                        }
                                    }, year, month, day);
                            picker.show();
                        }
                    }
                });

                spinnerPagamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(spinnerPagamento.getSelectedItem().toString().equals("À VISTA")){
                            //disableInput(editTextData);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            String currentDateandTime = sdf.format(new Date());
                            editTextData.setText(currentDateandTime);
                            editTextData.setBackground(context.getResources().getDrawable(R.drawable.edittext_shape_gray));
                        }else{
                            //enableInput(editTextData);
                            editTextData.setBackground(context.getResources().getDrawable(R.drawable.edittext_shape));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                buttonVoltar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                buttonConcluir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if(TextUtils.isEmpty(editTextValor.getText())){
                                showToast("Digite o valor da parcela");
                                return;
                            }
                            parcelaPosicao.setFormaPagamento(spinnerPagamento.getSelectedItem().toString());
                            Date date= new SimpleDateFormat("dd/MM/yy").parse(editTextData.getText().toString());
                            parcelaPosicao.setVencimento(date);
                            parcelaPosicao.setValor(Double.parseDouble(editTextValor.getText().toString()));
                            mListener.atualizarSoma();
                            notifyDataSetChanged();
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                imageButtonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                textViewValor.setText("Valor da " + parcelaPosicao.getId() + "ª parcela(R$)" );
                textViewParcela.setText(parcelaPosicao.getId() + "ª Parcela");
            }
        });

        textViewPorcentagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

                final View mView = inflater.inflate(R.layout.dialog_desconto,null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                final Spinner spinnerDesconto = mView.findViewById(R.id.spinnerDesconto);
                Button buttonConcluir = mView.findViewById(R.id.buttonConcluir);
                Button buttonVoltar = mView.findViewById(R.id.buttonVoltar);
                ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);

                ArrayList arrayList = new ArrayList();
                arrayList.add("0%");

                for(int i=5;i<=50; i+=5){
                    arrayList.add(Integer.toString(i)+'%');
                }

                final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayList);

                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDesconto.setAdapter(spinnerAdapter);

                buttonConcluir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        parcelaPosicao.setDescontoPagamento(spinnerDesconto.getSelectedItem().toString());
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                buttonVoltar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
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



        return convertView;
    }

    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private void disableInput(EditText editText){
        editText.setTextIsSelectable(false);
        editText.setFocusable(false);
    }

    void enableInput(EditText editText){
        editText.setTextIsSelectable(true);
        editText.setFocusable(true);
    }

    private void criarConexao(){
        try {

            dadosOpenHelper = new DadosOpenHelper(getContext());

            conexao = dadosOpenHelper.getWritableDatabase();

            comandaRepositorio = new ComandaRepositorio(conexao);

        }catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("OK",null);
            dlg.show();
        }
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void atualizarSoma();
    }
}
