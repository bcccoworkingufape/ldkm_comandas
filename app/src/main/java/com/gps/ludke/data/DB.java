package com.gps.ludke.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DB extends _Default implements Runnable  {
    private Connection conn;
    private String hostOld = "gpsteste2.postgresql.dbaas.com.br";
    private String dbOld = "gpsteste";
    private String userOld = "gpsteste";
    private String passOld = "ludkeappmobile";
    private int port = 5432;
    private String host2 = "devludke.postgresql.dbaas.com.br";
    private String db2 = "alvanir";
    private String user2 = "alvanir";
    private String pass2 = "boneca24";

    private String hostDev = "devludke.postgresql.dbaas.com.br";
    private String dbDev = "devludke";
    private String userDev = "devludke";
    private String passDev = "devludke2020";

    private String hostProd = "ludkeproducao.postgresql.dbaas.com.br";
    private String dbProd = "ludkeproducao";
    private String userProd = "ludkeproducao";
    private String passProd = "ludkeprod2020";
/*

    //DEV

    private String host = "devludke.postgresql.dbaas.com.br";
    private String db = "devludke";
    private String user = "devludke";
    private String pass = "devludke2020";



 */

    //PRODUCAO
    private String host = "ludkeproducao.postgresql.dbaas.com.br";
    private String db = "ludkeproducao";
    private String user = "ludkeproducao";
    private String pass = "ludkeprod2020";





    private String url = "jdbc:postgresql://%s:%d/%s";

    public DB() {
        super();

        this.url = String.format(this.url, this.host, this.port, this.db );
        this.conecta();
        this.desconecta();

    }
    @Override
    public void run() {
        try {
            Class.forName("org.postgresql.Driver");

            try {
                this.conn = DriverManager.getConnection(
                        this.url, this.user, this.pass);

                if (conn != null) {
                    System.out.println("Connected to the database!");
                } else {
                    System.out.println("Failed to make connection!");
                }

            } catch (SQLException e) {
                System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            this._mensagem = e.getMessage();
            this._status = false;
        }
    }

    private void conecta(){
        Thread thread = new Thread(this);
        thread.start();
        try {
            thread.join();

        } catch (Exception e) {
            this._mensagem = e.getMessage();
            this._status = false;
        }
    }

    private void desconecta(){
        if (this.conn!=null) {
            try{
                this.conn.close();
            } catch (Exception e) {

            } finally {
                this.conn = null;
            }
        }
    }

    public ResultSet  select(String query) {
        this.conecta();
        ResultSet resultSet = null;
        try {
            resultSet = new ExecuteBD(this.conn, query).execute().get();
        } catch (Exception e) {
            this._status = false;
            this._mensagem = e.getMessage();
        }
        return resultSet;
    }

    public ResultSet execute(String query) {
        this.conecta();
        ResultSet resultSet = null;
        try {
            resultSet = new ExecuteBD(this.conn, query).execute().get();
        } catch (Exception e) {
            this._status = false;
            this._mensagem = e.getMessage();
        }
        return resultSet;
    }
}
