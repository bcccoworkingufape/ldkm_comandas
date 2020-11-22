package com.gps.ludke.data;

public class ScriptDLL {

    public static String getCreateTableProduto(){
        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE IF NOT EXISTS PRODUTO( ");
        sql.append("    CODIGO VARCHAR (50) PRIMARY KEY NOT NULL, ");
        sql.append("    NOME VARCHAR (100) NOT NULL DEFAULT (''),");
        sql.append("    DESCRICAO VARCHAR (200) NOT NULL DEFAULT (''),");
        sql.append("    FORNECEDOR VARCHAR (100) NOT NULL DEFAULT (''),");
        sql.append("    PRECO_COMPRA MONEY NOT NULL DEFAULT (''),");
        sql.append("    PRECO_VENDA MONEY NOT NULL DEFAULT (''),");
        sql.append("    ENDERECO INTEGER NOT NULL DEFAULT (''))");
        return  sql.toString();
        // FOREIGN KEY(ENDERECO) REFERENCES ENDERECO(ID)
    }

    public static String getCreateTableCliente(){
        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE IF NOT EXISTS CLIENTE( ");
        sql.append("    CPF VARCHAR (17) PRIMARY KEY NOT NULL, ");
        sql.append("    NOME VARCHAR (100) NOT NULL DEFAULT (''),");
        sql.append("    RG VARCHAR (10) NOT NULL DEFAULT (''),");
        sql.append("    ENDERECO INTEGER NOT NULL DEFAULT (''),");
        sql.append("    TELEFONE VARCHAR(15) NOT NULL DEFAULT (''),");
        sql.append("    LIMITE_CREDITO MONEY NOT NULL DEFAULT (''))");
        return sql.toString();
    }

    public  static String getCreateTableComanda(){
        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE IF NOT EXISTS COMANDA( ");
        sql.append("    CODIGO INTEGER PRIMARY KEY NOT NULL, ");
        sql.append("    CLIENTE INTEGER NOT NULL DEFAULT (''),");
        sql.append("    DATA STRING (30) NOT NULL DEFAULT (''))");
        return sql.toString();
    }

    public static String getCreateTableVenda(){
        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE IF NOT EXISTS VENDA( ");
        sql.append("    COMANDA INTEGER PRIMARY KEY NOT NULL, ");
        sql.append("    PESO DOUBLE NOT NULL DEFAULT (''),");
        sql.append("    PRODUTO INTEGER NOT NULL DEFAULT (''),");
        sql.append("    PAGAMENTO STRING (8) NOT NULL DEFAULT (''))");
        return sql.toString();
    }

    public  static String dropTables(){
        StringBuilder sql = new StringBuilder();
        sql.append("DROP TABLE IF EXISTS PRODUTO");
        return sql.toString();
    }



}
