package com.gps.ludke;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gps.ludke.entidade.Cliente;
import com.gps.ludke.entidade.Relatorio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ClienteArrayAdapter extends ArrayAdapter<Cliente> {
    private Context context;
    private List<Cliente> clienteList = new ArrayList<Cliente>();
    private List<Cliente> clienteListOriginal;
    private List<Cliente> listaTemp;

    public ClienteArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public ClienteArrayAdapter(Context context, ArrayList<Cliente> lista) {
        super(context, 0,lista);
        this.context = context;
        this.clienteList = lista;
        this.clienteListOriginal = lista;
        this.listaTemp = lista;
    }

    @Override
    public int getCount() {
        return this.clienteList.size();
    }

    @Override
    public Cliente getItem(int index) {
        return this.clienteList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Cliente itemPosicao = this.clienteList.get(position);

        convertView = LayoutInflater.from(this.context).inflate(R.layout.item_cliente_card,null);

        TextView nome = convertView.findViewById(R.id.textViewNome);
        TextView cpfCnpj = convertView.findViewById(R.id.textViewCpfCnpj);

        nome.setText(itemPosicao.getNomeReduzido());
        cpfCnpj.setText(itemPosicao.getCpf());

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count >= 0) {
                    clienteList = ((List<Cliente>) results.values);//if results of search is null set the searched results data
                } else {
                    clienteList = clienteListOriginal;// set original values
                }

                notifyDataSetInvalidated();
            }



            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults result = new FilterResults();
                if (!TextUtils.isEmpty(constraint)) {
                    constraint = constraint.toString().toLowerCase();
                    ArrayList<Cliente> foundItems = new ArrayList<Cliente>();
                    if(listaTemp!=null)
                    {
                        for(int i=0;i<listaTemp.size();i++) {

                            if (listaTemp.get(i).getCpf().contains(constraint.toString()) || listaTemp.get(i).getNomeReduzido().toUpperCase().contains(constraint.toString().toUpperCase()) || listaTemp.get(i).getNome().toUpperCase().contains(constraint.toString().toUpperCase())) {
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
