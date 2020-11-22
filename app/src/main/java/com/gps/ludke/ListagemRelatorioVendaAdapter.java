package com.gps.ludke;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.gps.ludke.entidade.Venda;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListagemRelatorioVendaAdapter  extends ArrayAdapter<Venda> implements Filterable {
    private Context context;
    private List<Venda> vendaList = new ArrayList<Venda>();
    private ListagemRelatorioCardArrayAdapter.Listener mListener;
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public ListagemRelatorioVendaAdapter(Context context, ArrayList<Venda> lista) {
        super(context, 0,lista);
        this.context = context;
        this.vendaList = lista;
    }

    @Override
    public int getCount() {
        return this.vendaList.size();
    }

    @Override
    public Venda getItem(int index) {
        return this.vendaList.get(index);
    }

    public List<Venda> getVendaList() {
        return vendaList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Venda itemPosicao = this.vendaList.get(position);

        convertView = LayoutInflater.from(this.context).inflate(R.layout.item_produto_listagem_venda_card,null);

        TextView textViewNomeProduto = (TextView) convertView.findViewById(R.id.textViewNomeProduto);
        TextView textViewPeso = (TextView) convertView.findViewById(R.id.textViewPeso);
        TextView textViewPrecoKg = convertView.findViewById(R.id.textViewPrecoKg);
        TextView textViewTotal = convertView.findViewById(R.id.textViewTotal);


        textViewNomeProduto.setText(itemPosicao.getProduto().getNome());
        textViewPeso.setText(Double.toString(itemPosicao.getPeso()));
        textViewPrecoKg.setText(formatter.format(itemPosicao.getProduto().getPrecoVenda()).replace("R$",""));
        textViewTotal.setText(formatter.format(itemPosicao.getPreco()).replace("R$",""));

        //precoVenda.setText(Double.toString(itemPosicao.getPrecoVenda()));


        return convertView;
    }
}
