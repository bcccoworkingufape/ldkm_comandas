package com.gps.ludke.repositorio;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gps.ludke.data.DB;
import com.gps.ludke.entidade.Produto;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ProdutoRepositorio {

    private SQLiteDatabase conexao;
    public ProdutoRepositorio(SQLiteDatabase conexao){
        this.conexao = conexao;
    }

    public void inserir(Produto produto){
        ContentValues contentValues = new ContentValues();
        contentValues.put("CODIGO",produto.getCodigo());
        contentValues.put("NOME",produto.getNome());
        contentValues.put("DESCRICAO",produto.getDescricao());
        contentValues.put("FORNECEDOR",produto.getFornecedor());
        contentValues.put("PRECO_COMPRA",produto.getPrecoCompra());
        contentValues.put("PRECO_VENDA",produto.getPrecoVenda());
        //contentValues.put("ENDERECO",produto.getEndereco());

        conexao.insertOrThrow("PRODUTO",null,contentValues);
    }

    public void excluir(int codigo){

        String[] parametros = new String[1];
        parametros[0] = String.valueOf(codigo);

        conexao.delete("PRODUTO", "ID = ?", parametros);

    }

    public void alterar(Produto produto){
        ContentValues contentValues = new ContentValues();
        contentValues.put("NOME",produto.getNome());
        contentValues.put("DESCRICAO",produto.getDescricao());
        contentValues.put("FORNECEDOR",produto.getFornecedor());
        contentValues.put("PRECO_COMPRA",produto.getPrecoCompra());
        contentValues.put("PRECO_VENDA",produto.getPrecoVenda());
        //contentValues.put("ENDERECO",produto.getEndereco());

        String[] parametros = new String[1];
        //parametros[0] = String.valueOf(produto.getID());

        conexao.update("PRODUTO",contentValues, "ID = ?",parametros);
    }

    public ArrayList<Produto> buscarTodosProdutos(){
        ArrayList<Produto> produtos = new ArrayList<Produto>();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT CODIGO, NOME, DESCRICAO, FORNECEDOR, PRECO_COMPRA, PRECO_VENDA, ENDERECO");
        sql.append("    FROM PRODUTO ");

        Cursor resultado = conexao.rawQuery(sql.toString(),null);

        if(resultado.getCount() > 0) {
            resultado.moveToFirst();

            do{
                Produto prod = new Produto();
                prod.setCodigo(resultado.getString(resultado.getColumnIndexOrThrow("CODIGO")));
                prod.setDescricao(resultado.getString(resultado.getColumnIndexOrThrow("DESCRICAO")));
                prod.setNome(resultado.getString(resultado.getColumnIndexOrThrow("NOME")));
                prod.setFornecedor(resultado.getString(resultado.getColumnIndexOrThrow("FORNECEDOR")));
                prod.setPrecoCompra(resultado.getDouble(resultado.getColumnIndexOrThrow("PRECO_COMPRA"))) ;
                prod.setPrecoVenda(resultado.getDouble(resultado.getColumnIndexOrThrow("PRECO_VENDA")));

                produtos.add(prod);

            }while (resultado.moveToNext());
        }

        return produtos;
    }

    public ArrayList<Produto> buscarTodosProdutosWeb(){
        ArrayList<Produto> produtos = new ArrayList<Produto>();
        try{
            DB db = new DB();

            String sql2 = String.format("select * from produtos");

            ResultSet resultSet = db.select(sql2);
            if(resultSet != null){
                while (resultSet.next()) {
                    Produto prod = new Produto();
                    prod.setCodigo(resultSet.getString("id"));
                    prod.setDescricao(resultSet.getString("descricao"));
                    prod.setNome(resultSet.getString("nome"));
                    //prod.setFornecedor(resultSet.getString("FORNECEDOR"));
                    //prod.setPrecoCompra(resultSet.getDouble("PRECO_COMPRA")) ;
                    prod.setPrecoVenda(resultSet.getDouble("preco"));

                    produtos.add(prod);
                }
            }
            Collections.sort(produtos);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return produtos;
    }

    public Produto buscarProduto(int codigo){
        Produto produto = new Produto();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT CODIGO, NOME, DESCRICAO, FORNECEDOR, PRECO_COMPRA, PRECO_VENDA, ENDERECO");
        sql.append("    FROM PRODUTO ");
        sql.append("    WHERE CODIGO = ? ");

        String[] parametros = new String[1];
        parametros[0] = String.valueOf(codigo);

        Cursor resultado = conexao.rawQuery(sql.toString(),parametros);

        if(resultado.getCount() > 0) {
            resultado.moveToFirst();

            produto.setCodigo(resultado.getString(resultado.getColumnIndexOrThrow("CODIGO")));
            produto.setDescricao(resultado.getString(resultado.getColumnIndexOrThrow("DESCRICAO")));
            produto.setNome(resultado.getString(resultado.getColumnIndexOrThrow("NOME")));
            produto.setFornecedor(resultado.getString(resultado.getColumnIndexOrThrow("FORNECEDOR")));
            produto.setPrecoCompra(resultado.getDouble(resultado.getColumnIndexOrThrow("PRECO_COMPRA"))) ;
            produto.setPrecoVenda(resultado.getDouble(resultado.getColumnIndexOrThrow("PRECO_VENDA")));

            return produto;
        }

        return null;
    }
}
