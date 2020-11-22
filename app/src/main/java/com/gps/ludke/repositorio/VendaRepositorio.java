package com.gps.ludke.repositorio;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gps.ludke.data.DB;
import com.gps.ludke.entidade.Cliente;
import com.gps.ludke.entidade.Comanda;
import com.gps.ludke.entidade.Venda;

import java.sql.ResultSet;
import java.util.ArrayList;

public class VendaRepositorio {

    private SQLiteDatabase conexao;
    public VendaRepositorio(SQLiteDatabase conexao){
        this.conexao = conexao;
    }

     public int inserir(Venda venda){
        ContentValues contentValues = new ContentValues();
        contentValues.put("COMANDA",venda.getComanda().getCodigo());
        contentValues.put("PESO",venda.getPeso());
        contentValues.put("PAGAMENTO",venda.formaDePagamento());
        contentValues.put("PRODUTO",venda.getProduto().getCodigo());

        //conexao.insertOrThrow("VENDA",null,contentValues);

         try{
             DB db = new DB();

             String peso = String.valueOf(venda.getPeso()).replaceAll(",",".");
             String preco;
             if(venda.getPreco()>0){
                 preco = String.valueOf(venda.getPreco());
             }else{
                 preco = String.valueOf(venda.getPeso()*venda.getProduto().getPrecoVenda());
             }
             String sql = "INSERT INTO itens_pedidos" +
                     "(\"pesoSolicitado\", \"pesoFinal\", \"valorReal\", \"descontoPorcentagem\", \"valorComDesconto\", \"nomeProduto\", produto_id, pedido_id, created_at, updated_at) " +
                     "VALUES(%s, %s, %s, %s, %s, '%s', %s, %s, now(), now()) returning id";

             sql = String.format(sql, peso, peso, preco, 0, preco, venda.getProduto().getNome(), venda.getProduto().getCodigo(),venda.getComanda().getCodigo());

             ResultSet resultSet = db.execute(sql);
             if(resultSet != null){
                 try {
                     resultSet.next();
                 } catch (java.sql.SQLException e) {
                     e.printStackTrace();
                 }
                 try {
                     return resultSet.getInt("id");
                 } catch (java.sql.SQLException e) {
                     e.printStackTrace();
                 }
             }

         }catch (SQLException e){
             System.out.println(e.getMessage());
         }catch (Exception e){
             System.out.println(e.getMessage());
         }
         return 0;


    }

    public ArrayList<Venda> vendasComanda(Comanda comanda){
        ArrayList<Venda> vendas = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT COMANDA, PESO, PAGAMENTO");
        sql.append("    FROM VENDA ");
        sql.append("    WHERE COMANDA = ? ");

        String[] parametros = new String[1];
        parametros[0] = String.valueOf(comanda.getCodigo());

        Cursor resultado = conexao.rawQuery(sql.toString(),parametros);

        if(resultado.getCount() > 0) {
            resultado.moveToFirst();

            do{
                Venda vd = new Venda();
                vd.setComanda(comanda);
                vd.setPeso(resultado.getDouble(resultado.getColumnIndexOrThrow("PESO")));
                vd.setPagamento(resultado.getString(resultado.getColumnIndexOrThrow("PAGAMENTO")));

                vendas.add(vd);

            }while (resultado.moveToNext());
        }

        return vendas;
    }

    public void removerVendasComanda(Comanda comanda) {
        try{
            DB db = new DB();

            String sqlDelete = "DELETE FROM public.itens_pedidos " +
                    "WHERE pedido_id=%s;";

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
}
