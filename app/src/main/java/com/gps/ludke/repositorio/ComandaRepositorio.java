package com.gps.ludke.repositorio;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gps.ludke.ItensPedido;
import com.gps.ludke.data.DB;
import com.gps.ludke.entidade.Comanda;
import com.gps.ludke.entidade.Pagamento;
import com.gps.ludke.entidade.Parcela;
import com.gps.ludke.entidade.Produto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ComandaRepositorio {

    private SQLiteDatabase conexao;
    public ComandaRepositorio(SQLiteDatabase conexao){
        this.conexao = conexao;
    }

     public int inserir(Comanda comanda){
        /*
        ContentValues contentValues = new ContentValues();
        contentValues.put("CODIGO",comanda.getCodigo());

        contentValues.put("DATA",comanda.getData().toString());
        if(!comanda.getCliente().toString().isEmpty()){
            contentValues.put("CLIENTE",comanda.getCliente().toString());
        }

        conexao.insertOrThrow("COMANDA",null,contentValues);

*/
         double total = 0;

         for (Produto produto : ItensPedido.getProdutosPedido()){
             total += produto.getPeso()*produto.getPrecoVenda();
         }

         DB db = new DB();
         String sql3 = "insert into comanda (codigo, cliente, preco, usuario) values(%d, '%s',%s, '%s');";
         String sql = "insert into pedidos (\"dataEntrega\", \"valorTotal\", status_id, cliente_id, funcionario_id, entregador_id, created_at, updated_at, tipo) " +
                 "VALUES(now(), %s, (select st.id from statuses st where st.status  = 'ENTREGUE'), %s, %s, %s, now(), now(), 'vm') returning id";

         String cpfCliente = "0";
         if(comanda.getCliente() != null){
             cpfCliente = comanda.getCliente().getCpf();
             cpfCliente= "(select c.id from clientes c where c.\"cpfCnpj\" = '"+cpfCliente+"')";
         }


         String sqlFuncionario = "(select f.id from funcionarios f where f.user_id = '"+ItensPedido.getUsuarioLogado().getCpf()+"')";

         sql = String.format(sql, total,cpfCliente, sqlFuncionario, sqlFuncionario);
         ResultSet resultSet = db.execute(sql);

         if(resultSet != null){
             try {
                 resultSet.next();
             } catch (SQLException e) {
                 e.printStackTrace();
             }
             try {
                 return resultSet.getInt("id");
             } catch (SQLException e) {
                 e.printStackTrace();
             }
         }
         return 0;
     }

     public void atualizarComanda(Comanda comanda){
        try{
            DB db = new DB();

            String sql = "UPDATE public.pedidos " +
                    "SET \"valorTotal\"=%s, cliente_id=(select c.id from clientes c where c.\"cpfCnpj\" = '%s'), updated_at=now() " +
                    "WHERE id=%s;";
            sql = String.format(sql, comanda.getTotal(), comanda.getCliente().getCpf(), comanda.getCodigo());

            ResultSet resultSet = db.execute(sql);
            if(resultSet != null){
                try {
                    resultSet.next();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }



        }catch (Exception e){
            throw new java.lang.IllegalArgumentException("Erro na atualização dos dados");
        }
     }

    public String getCodigoComanda(){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CODIGO");
        sql.append(" FROM COMANDA");
        sql.append(" WHERE CODIGO = (SELECT MAX(CODIGO) FROM COMANDA)");

        Cursor resultado = conexao.rawQuery(sql.toString(),null);

        if(resultado.getCount()>0){
            resultado.moveToFirst();
            return resultado.getString(resultado.getColumnIndexOrThrow("CODIGO"));

        }
        return null;
    }

    public String getCodigoComandaWeb(){
        try{
            DB db = new DB();
            String sql = "select last_value from pedidos_id_seq";

            ResultSet resultSet = db.select(sql);


            if(resultSet != null){
                resultSet.next();
                return resultSet.getString("last_value");
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void removerComanda(Comanda comanda) {
        try{
            DB db = new DB();

            String sqlDelete = "DELETE FROM public.pedidos " +
                    "WHERE id=%s;";

            sqlDelete = String.format(sqlDelete, comanda.getCodigo());

            ResultSet resultSet = db.execute(sqlDelete);
            if(resultSet != null){
                try {
                    resultSet.next();
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                }
            }

        }catch (Exception e){
            throw new java.lang.IllegalArgumentException("Erro na atualização dos dados");
        }
    }

    public ArrayList getFormasPagamento(){
        try{
            ArrayList arrayListPagamento = new ArrayList();
            DB db = new DB();
            String sql = "SELECT id, nome, created_at, updated_at " +
                    "FROM public.forma_pagamentos;";
            ResultSet resultSet = db.execute(sql);
            if(resultSet != null){
                while(resultSet.next()){
                    String pagamento = resultSet.getString("nome");
                    arrayListPagamento.add(pagamento);
                }
            }
            return arrayListPagamento;
        } catch (SQLException e) {
            throw new java.lang.IllegalArgumentException("Erro no carregamento das formas de pagamento");
        }
    }

    public int insertPagamento(Pagamento pagamento, Parcela parcela){
        try{
            DB db = new DB();
            String sqlFuncionario = "INSERT INTO public.pagamentos " +
                    "(\"dataVencimento\", \"dataPagamento\", obs, \"descontoPagamento\", \"valorTotalPagamento\", \"valorPago\", status, funcionario_id, pedido_id, created_at, updated_at, \"formaPagamento_id\") " +
                    "VALUES('%s', now(), '%s', '%s', '%s', '%s', '%s', '%s', '%s', now(), now(), (select id from public.forma_pagamentos where nome='%s'))  returning id;";
            sqlFuncionario = String.format(sqlFuncionario, parcela.getVencimento(),pagamento.getObservação() ,parcela.getDescontoPagamento().replace("%",""), pagamento.getValorTotalPagamento(), parcela.getValor(), "fechado", ItensPedido.getUsuarioLogado().getFuncionarioID(), pagamento.getComanda().getCodigo(), parcela.getFormaPagamento());

            ResultSet resultSet = db.execute(sqlFuncionario);
            if(resultSet != null){
                try {
                    resultSet.next();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    return resultSet.getInt("id");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        } catch (Exception e) {
            throw new java.lang.IllegalArgumentException("Erro no carregamento na inserção do pagamento");
        }
    }
}
