package com.gps.ludke;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.gps.ludke.entidade.Venda;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListagemRelatorioCardArrayAdapter  extends ArrayAdapter<Venda> implements Filterable {
    private Context context;
    private List<Venda> vendaList = new ArrayList<Venda>();
    private Listener mListener;
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));


    public ListagemRelatorioCardArrayAdapter(Context context, ArrayList<Venda> lista) {
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

        convertView = LayoutInflater.from(this.context).inflate(R.layout.item_produto_listagem_card,null);

        TextView nome = (TextView) convertView.findViewById(R.id.textViewIdItem);
        TextView precoVenda = (TextView) convertView.findViewById(R.id.textViewValorKg);
        TextView codigo = convertView.findViewById(R.id.textViewCodProd);
        ImageButton buttonDelete = convertView.findViewById(R.id.buttonDelete);


        nome.setText(itemPosicao.getProduto().getNome());
        precoVenda.setText(formatter.format(itemPosicao.getPreco()).replace("R$",""));
        codigo.setText(itemPosicao.getProduto().getCodigo());
        //precoVenda.setText(Double.toString(itemPosicao.getPrecoVenda()));

        mListener.atualizarVenda();

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getCount() == 1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Não existirá mais itens e a venda será apagada por completo. ");
                    builder.setMessage("Deseja continuar?");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            remove(itemPosicao);
                            notifyDataSetChanged();
                            mListener.atualizarVenda();
                        }
                    });
                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else {
                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                    mBuilder.setTitle("Tem certeza que quer excluir o item?");
                    mBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            remove(itemPosicao);
                            notifyDataSetChanged();
                            mListener.atualizarVenda();
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
            }
        });

        nome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.alterarPeso(position);
            }
        });

        precoVenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.alterarPeso(position);
            }
        });

        codigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.alterarPeso(position);
            }
        });

        return convertView;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void atualizarVenda();
        void alterarPeso(int position);
    }


}
