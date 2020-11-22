package com.gps.ludke;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.gps.ludke.entidade.Produto;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CardArrayAdapter extends ArrayAdapter<Produto> {
    private Context context;
    private List<Produto> produtoList = new ArrayList<Produto>();
    private Listener mListener;
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));



    public CardArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CardArrayAdapter(Context context, ArrayList<Produto> lista) {
        super(context, 0,lista);
        this.context = context;
        this.produtoList = lista;
    }
/*
    @Override
    public void add(Produto object) {
        produtoList.add(object);
        super.add(object);
    }
*/
    @Override
    public int getCount() {
        return this.produtoList.size();
    }

    @Override
    public Produto getItem(int index) {
        return this.produtoList.get(index);
    }

    public void refreshData(double peso){

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Produto itemPosicao = this.produtoList.get(position);

        convertView = LayoutInflater.from(this.context).inflate(R.layout.item_carrinho_card,null);
        final View layout = convertView;

        TextView nome = (TextView) convertView.findViewById(R.id.textViewIdItem);
        TextView total = (TextView) convertView.findViewById(R.id.textViewTotal);
        ImageButton buttonDelete = convertView.findViewById(R.id.buttonDelete);

        //peso.setText(Double.toString(itemPosicao.getPeso()) + " Kg");

        nome.setText(itemPosicao.getNome());
        //precoVenda.setText(formatter.format(itemPosicao.getPrecoVenda()) + "/Kg");
        total.setText(formatter.format(itemPosicao.getPeso()*itemPosicao.getPrecoVenda()).replace("R$",""));

        /*
        View row = convertView;
        CardViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_produto_card, parent, false);
            viewHolder = new CardViewHolder();
            viewHolder.nome = (TextView) row.findViewById(R.id.textViewNomeItem);
            //viewHolder.peso = (TextView) row.findViewById(R.id.textViewPeso);
            //viewHolder.total = (TextView) row.findViewById(R.id.textViewTotal);
            viewHolder.precoVenda = (TextView) row.findViewById(R.id.textViewValorKg);

            row.setTag(viewHolder);
        } else {
            viewHolder = (CardViewHolder)row.getTag();
        }
        Produto produto = getItem(position);
        viewHolder.nome.setText(produto.getNome());
        viewHolder.precoVenda.setText(Double.toString(produto.getPrecoVenda()));
        //viewHolder.total.setText(produto.getTotal());

*/

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                mBuilder.setTitle("Tem certeza que quer excluir o item?");
                mBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Produto produto = ItensPedido.getProdutosPedido().get(position);
                        ItensPedido.removeProdutoPedido(produto);
                        remove(produto);
                        mListener.atualizarCarrinho();
                        notifyDataSetChanged();
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
        });
        return convertView;
    }

    public void setListener( Listener listener ) {
        mListener = listener;
    }

    public interface Listener {

        void atualizarCarrinho();
    }

}
