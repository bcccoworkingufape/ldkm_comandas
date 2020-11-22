package com.gps.ludke;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gps.ludke.entidade.Produto;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListagemCardArrayAdapter extends ArrayAdapter<Produto> implements Filterable {
    private Context context;
    private List<Produto> produtoList = new ArrayList<Produto>();
    private List<Produto> produtoListOriginal;
    private List<Produto> listaTemp;
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public ListagemCardArrayAdapter(Context context, ArrayList<Produto> lista) {
        super(context, 0,lista);
        this.context = context;
        this.produtoList = lista;
        this.produtoListOriginal = lista;
        this.listaTemp = lista;
    }

    @Override
    public int getCount() {
        return this.produtoList.size();
    }

    @Override
    public Produto getItem(int index) {
        return this.produtoList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Produto itemPosicao = this.produtoList.get(position);

        convertView = LayoutInflater.from(this.context).inflate(R.layout.item_produto_card,null);
        final View layout = convertView;

        TextView nome = (TextView) convertView.findViewById(R.id.textViewIdItem);
        TextView precoVenda = (TextView) convertView.findViewById(R.id.textViewValorKg);
        TextView codigoProd = convertView.findViewById(R.id.textViewCodProd);

        nome.setText(itemPosicao.getNome());
        precoVenda.setText(formatter.format(itemPosicao.getPrecoVenda()));
        codigoProd.setText(itemPosicao.getCodigo());
        //precoVenda.setText(Double.toString(itemPosicao.getPrecoVenda()));

        return convertView;
    }

    //@NonNull
    //@Override
    public Filter filter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filtroResultado = new FilterResults();

                if (constraint == null || constraint.length() == 0) {

                    // Se nao tiver nada para filtrar entao etorna a lista completafiltroResultado.values = produtoList;
                    filtroResultado.values = produtoListOriginal;
                    filtroResultado.count = produtoList.size();
                    return filtroResultado;
                } else {

                    List<Produto> auxProduto = new ArrayList<Produto>();

                    for (Produto p : produtoList) {
                        if (p.getNome().toUpperCase().contains(constraint.toString().toUpperCase())) {
                            auxProduto.add(p);
                        }
                    } //Fim do for

                    filtroResultado.values = auxProduto;
                    filtroResultado.count = auxProduto.size();
                }

                return filtroResultado;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count == 0) {
                    //produtoList = produtoListOriginal;
                    //Notifica os ouvintes
                    notifyDataSetInvalidated();
                }

                else {
                    //Preencho a lista(listaProdutos) do adapter com o novo valor
                    produtoList = (List<Produto>) results.values;

                    //Notifica ouvintes apos a lista ter novos valores
                    notifyDataSetChanged();

                    notifyDataSetInvalidated();
                }
            }
        };
    }
    @NonNull
    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count >= 0) {
                    produtoList = ((List<Produto>) results.values);//if results of search is null set the searched results data
                } else {
                    produtoList = produtoListOriginal;// set original values
                }

                notifyDataSetInvalidated();
            }



            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults result = new FilterResults();
                if (!TextUtils.isEmpty(constraint)) {
                    constraint = constraint.toString().toLowerCase();
                    ArrayList<Produto> foundItems = new ArrayList<Produto>();
                    if(listaTemp!=null)
                    {
                        for(int i=0;i<listaTemp.size();i++)
                        {

                            if (listaTemp.get(i).getNome().toUpperCase().contains(constraint.toString().toUpperCase()) || listaTemp.get(i).getCodigo().toUpperCase().equals(constraint.toString().toUpperCase())) {
                                //System.out.println("My datas"+mTemp.get(i).newDatacus.get(NewData.TAG_CUSTOMER_CODE).toString());
                                foundItems.add(listaTemp.get(i));
                            }
                        }
                    }
                    result.count = foundItems.size();//search results found return count
                    result.values = foundItems;// return values
                }
                else
                {
                    result.count=-1;// no search results found
                }


                return result;
            }
        };
    }
}
