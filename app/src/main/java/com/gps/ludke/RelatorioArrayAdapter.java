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

import com.gps.ludke.entidade.Relatorio;
import com.gps.ludke.entidade.Venda;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RelatorioArrayAdapter  extends ArrayAdapter<Relatorio> {
    private Context context;
    private List<Relatorio> relatorioList = new ArrayList<Relatorio>();
    private List<Relatorio> relatorioListOriginal;
    private List<Relatorio> listaTemp;
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private boolean isUser;

    public RelatorioArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public RelatorioArrayAdapter(Context context, ArrayList<Relatorio> lista) {
        super(context, 0,lista);
        this.context = context;
        this.relatorioList = new ArrayList<>(lista);
        this.relatorioListOriginal = new ArrayList<>(lista);
        this.listaTemp = new ArrayList<>(lista);
    }

    @Override
    public int getCount() {
        if(relatorioList != null) {
            return this.relatorioList.size();
        }else{
            return 0;
        }
    }

    @Override
    public Relatorio getItem(int index) {
        return this.relatorioList.get(index);
    }

    public void setIsUser(boolean is){
        this.isUser = is;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Relatorio itemPosicao = this.relatorioList.get(position);

        convertView = LayoutInflater.from(this.context).inflate(R.layout.item_relatorio_card,null);

        TextView quantidadeProdutos = (TextView) convertView.findViewById(R.id.textViewQuantidadeProdutos);
        TextView data = (TextView) convertView.findViewById(R.id.textViewData);
        TextView total = (TextView) convertView.findViewById(R.id.textViewTotal);
        TextView comanda = (TextView) convertView.findViewById(R.id.textViewComanda);
        TextView nomeCliente = (TextView) convertView.findViewById(R.id.textViewNomeCliente);

        quantidadeProdutos.setText(Integer.toString(itemPosicao.getQuantidadeVendas()));


        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        data.setText(dateFormat.format(itemPosicao.getComanda().getData()));
        total.setText(formatter.format(itemPosicao.getComanda().getTotal()).replace("R$",""));
        comanda.setText(Integer.toString(itemPosicao.getComanda().getCodigo()));
        nomeCliente.setText(itemPosicao.getComanda().getCliente().getNomeReduzido());


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


        return convertView;
    }

    public void filter(String filtroDataInicial, String filtroDataFinal) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        relatorioList.clear();

        if(!filtroDataInicial.isEmpty() && filtroDataFinal.isEmpty()){

            java.sql.Date dataInicial = new java.sql.Date(format.parse(filtroDataInicial).getTime());

            for (Relatorio relatorio : relatorioListOriginal) {
                if (dataInicial.before(relatorio.getComanda().getData()) || dataInicial.equals(relatorio.getComanda().getData())) {
                    relatorioList.add(relatorio);
                }
            }
        }else if(filtroDataInicial.isEmpty() && !filtroDataFinal.isEmpty()){

            java.sql.Date dataFinal = new java.sql.Date(format.parse(filtroDataFinal).getTime());

            for (Relatorio relatorio : relatorioListOriginal) {
                if (dataFinal.after(relatorio.getComanda().getData()) || dataFinal.equals(relatorio.getComanda().getData())) {
                    relatorioList.add(relatorio);
                }
            }
        }else {

            java.sql.Date dataInicial = new java.sql.Date(format.parse(filtroDataInicial).getTime());
            java.sql.Date dataFinal = new java.sql.Date(format.parse(filtroDataFinal).getTime());

            for (Relatorio relatorio : relatorioListOriginal) {
                if ((dataFinal.after(relatorio.getComanda().getData()) && dataInicial.before(relatorio.getComanda().getData()) )|| dataFinal.equals(relatorio.getComanda().getData()) || dataInicial.equals(relatorio.getComanda().getData())) {
                    relatorioList.add(relatorio);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void limparFiltro() {
        this.relatorioList = new ArrayList<>(relatorioListOriginal);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count >= 0) {
                    relatorioList = ((List<Relatorio>) results.values);//if results of search is null set the searched results data
                } else {
                    relatorioList = relatorioListOriginal;// set original values
                }

                notifyDataSetInvalidated();
            }



            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults result = new FilterResults();
                if (!TextUtils.isEmpty(constraint)) {
                    constraint = constraint.toString().toLowerCase();
                    ArrayList<Relatorio> foundItems = new ArrayList<Relatorio>();
                    if(listaTemp!=null)
                    {
                        for(int i=0;i<listaTemp.size();i++)
                        {
                            try {
                                if (isUser) {
                                    String cpfSemPontuacao = listaTemp.get(i).getComanda().getCliente().getCpf().replace(".", "").replace("-", "");
                                    if (listaTemp.get(i).getComanda().getCliente().getCpf().contains(constraint.toString()) || cpfSemPontuacao.contains(constraint.toString()) || Integer.toString(listaTemp.get(i).getComanda().getCodigo()).equals(constraint.toString())) {
                                        foundItems.add(listaTemp.get(i));
                                    }
                                    if(listaTemp.get(i).getComanda().getCliente().getNome() != null){
                                        if( listaTemp.get(i).getComanda().getCliente().getNome().toUpperCase().contains(constraint.toString().toUpperCase())){
                                            foundItems.add(listaTemp.get(i));
                                        }
                                    }
                                    if(listaTemp.get(i).getComanda().getCliente().getNomeReduzido() != null){
                                        if(listaTemp.get(i).getComanda().getCliente().getNomeReduzido().toUpperCase().contains(constraint.toString().toUpperCase())){
                                            foundItems.add(listaTemp.get(i));
                                        }
                                    }
                                } else {
                                    if (Integer.toString(listaTemp.get(i).getComanda().getCodigo()).equals(constraint.toString())) {
                                        foundItems.add(listaTemp.get(i));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
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
