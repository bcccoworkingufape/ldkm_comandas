package com.gps.ludke.data;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.ResultSet;

public class ExecuteBD extends AsyncTask<String,Void, ResultSet> {

    private Connection conn;

    public ExecuteBD(Connection conn, String query) {
        this.conn = conn;
        this.query = query;
    }

    private String query;

    @Override
    protected ResultSet doInBackground(String... params) {
        ResultSet resultSet = null;
        try {
            resultSet = conn.prepareStatement(query).executeQuery();
        } catch (Exception e) {

        } finally {
            try {
                conn.close();
            }catch (Exception ex){

            }
        }
        return resultSet;
    }
}
