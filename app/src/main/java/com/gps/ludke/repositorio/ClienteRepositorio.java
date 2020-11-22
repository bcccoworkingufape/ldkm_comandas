package com.gps.ludke.repositorio;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gps.ludke.ItensPedido;
import com.gps.ludke.data.DB;
import com.gps.ludke.entidade.Cliente;
import com.gps.ludke.entidade.Endereco;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.xml.transform.Result;

public class ClienteRepositorio {

    private SQLiteDatabase conexao;
    public ClienteRepositorio(SQLiteDatabase conexao){
        this.conexao = conexao;
    }

    public int inserir(Cliente cliente) {
        /*
        ContentValues contentValues = new ContentValues();
        contentValues.put("NOME",cliente.getNome());
        contentValues.put("CPF",cliente.getCpf());
        //contentValues.put("RG",cliente.getRg());
        contentValues.put("TELEFONE",cliente.getTelefone());
        //contentValues.put("LIMITE_CREDITO",cliente.getLimiteCredito());
        //contentValues.put("ENDERECO",cliente.getEndereco());

        conexao.insertOrThrow("CLIENTE",null,contentValues);

         */

        DB db = new DB();

        String sqlEndereco = "INSERT INTO public.enderecos " +
                "(rua, numero, bairro, cidade, uf, cep, complemento, created_at, updated_at) " +
                "VALUES('%s', %s, '%s', '%s', '%s', '%s', '%s', now(), now()) returning id";

        //Endereco
        sqlEndereco = String.format(sqlEndereco, cliente.getEndereco().getRua(), cliente.getEndereco().getNumero(), cliente.getEndereco().getBairro(),cliente.getEndereco().getCidade(), cliente.getEndereco().getUf(), cliente.getEndereco().getCep(), cliente.getEndereco().getComplemento());

        ResultSet resultsetEndereco = db.execute(sqlEndereco);

        int idEndereco = -1;
        if(resultsetEndereco != null){
            try {
                resultsetEndereco.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                idEndereco =  resultsetEndereco.getInt("id");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //Telefone
        String sqlTelefone = "INSERT INTO public.telefones" +
                "(residencial, celular, created_at, updated_at) " +
                "VALUES('%s','%s', now(), now()) returning id";
        String telefone;
        if(cliente.getTelefone() != null){
            telefone = cliente.getTelefone();
        }else{
            telefone = null;
        }
        String celular;
        if(cliente.getCelular() != null){
            celular = cliente.getCelular();
        }else{
            celular = null;
        }

        sqlTelefone = String.format(sqlTelefone, telefone, celular);

        int idTelefone = -1;
        ResultSet resultSetTelefone = db.execute(sqlTelefone);
        if(resultSetTelefone != null){
            try {
                resultSetTelefone.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                idTelefone =  resultSetTelefone.getInt("id");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //user

        String sqlUser = "INSERT INTO public.users (\"name\", tipo, email, \"password\", created_at, updated_at, endereco_id, telefone_id) VALUES('%s', '%s', '%s', '%s', now(), now(), '%s', '%s') returning id";

        sqlUser = String.format(sqlUser,cliente.getNome(), cliente.getTipo(), cliente.getEmail(), cliente.getPass(), idEndereco, idTelefone);

        int idUser = -1;
        ResultSet resultSetUser = db.execute(sqlUser);
        if(resultSetUser != null){
            try {
                resultSetUser.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                idUser =  resultSetUser.getInt("id");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //cliente

        String sqlCliente = "INSERT INTO public.clientes" +
                "(\"nomeReduzido\", \"nomeResponsavel\", \"cpfCnpj\", tipo, \"inscricaoEstadual\", funcionario_id, user_id, created_at, updated_at) " +
                "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', now(), now()) returning id";

        sqlCliente = String.format(sqlCliente, cliente.getNomeReduzido(), cliente.getNomeResponsavel(), cliente.getCpf(), cliente.getTipo(), cliente.getInscricaoEstadual(), ItensPedido.getUsuarioLogado().getFuncionarioID(),idUser);
        int idCliente = -1;
        ResultSet resultSetCliente = db.execute(sqlCliente);
        if(resultSetCliente != null){
            try {
                resultSetCliente.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                idCliente =  resultSetCliente.getInt("id");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return idCliente;
    }

    public void excluir(int codigo){

        String[] parametros = new String[1];
        parametros[0] = String.valueOf(codigo);

        conexao.delete("CLIENTE", "ID = ?", parametros);

    }

    public void alterar(Cliente cliente){
        ContentValues contentValues = new ContentValues();
        contentValues.put("NOME",cliente.getNome());
        contentValues.put("CPF",cliente.getCpf());
        //contentValues.put("RG",cliente.getRg());
        contentValues.put("TELEFONE",cliente.getTelefone());
        //contentValues.put("LIMITE_CREDITO",cliente.getLimiteCredito());
        //contentValues.put("ENDERECO",cliente.getEndereco());

        String[] parametros = new String[1];
        //parametros[0] = String.valueOf(produto.getID());

        conexao.update("CLIENTE",contentValues, "ID = ?",parametros);
    }

    public ArrayList<Cliente> buscarTodosClientes(){
        ArrayList<Cliente> clientes = new ArrayList<Cliente>();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT CPF, NOME, RG, TELEFONE, LIMITE_CREDITO, ENDERECO");
        sql.append("    FROM CLIENTE ");

        Cursor resultado = conexao.rawQuery(sql.toString(),null);

        if(resultado.getCount() > 0) {
            resultado.moveToFirst();

            do{
                Cliente cli = new Cliente();
                cli.setCpf(resultado.getString(resultado.getColumnIndexOrThrow("CPF")));
                cli.setNome(resultado.getString(resultado.getColumnIndexOrThrow("NOME")));
                cli.setTelefone(resultado.getString(resultado.getColumnIndexOrThrow("TELEFONE")));
                //cli.setLimiteCredito(resultado.getDouble(resultado.getColumnIndexOrThrow("LIMITE_CREDITO"))); ;
                //cli.setRg(resultado.getString(resultado.getColumnIndexOrThrow("RG")));
                //cli.setEndereco(resultado.getInt(resultado.getColumnIndexOrThrow("ENDERECO")));

                clientes.add(cli);

            }while (resultado.moveToNext());
        }

        return clientes;
    }

    public ArrayList<Cliente> buscarTodosClientesWeb(){
        ArrayList<Cliente> clientes = new ArrayList<Cliente>();
        try{
            DB db = new DB();
            String sql = String.format("select * from clientes, users u, enderecos e, telefones t where (select id from funcionarios f where user_id = %s) = funcionario_id and u.id = user_id and e.id = endereco_id and t.id  = telefone_id", ItensPedido.getUsuarioLogado().getCpf());

            ResultSet resultSet = db.select(sql);
    
            if (resultSet != null) {
                while(resultSet.next()){
                    Cliente cliente = new Cliente();
                    Endereco endereco = new Endereco();
                    cliente.setNome(resultSet.getString("name"));
                    cliente.setCpf(resultSet.getString("cpfcnpj"));
                    cliente.setNomeReduzido(resultSet.getString("nomereduzido"));
                    cliente.setInscricaoEstadual(resultSet.getString("inscricaoestadual"));
                    cliente.setNomeResponsavel(resultSet.getString("nomeresponsavel"));
                    cliente.setTipo(resultSet.getString("tipo"));
                    cliente.setEmail(resultSet.getString("email"));

                    cliente.setTelefone(resultSet.getString("residencial"));
                    cliente.setCelular(resultSet.getString("celular"));

                    endereco.setRua(resultSet.getString("rua"));
                    endereco.setNumero(resultSet.getString("numero"));
                    endereco.setBairro(resultSet.getString("bairro"));
                    endereco.setCidade(resultSet.getString("cidade"));
                    endereco.setCep(resultSet.getString("cep"));
                    endereco.setUf(resultSet.getString("uf"));
                    endereco.setComplemento(resultSet.getString("complemento"));

                    cliente.setEndereco(endereco);
                    clientes.add(cliente);
                }
            }
            Collections.sort(clientes);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return clientes;
    }

    public void atualizarClienteWeb(Cliente cliente){
        try{
            DB db = new DB();

            String sql = String.format("UPDATE public.clientes " +
                    "SET \"nomeReduzido\"='%s', \"nomeResponsavel\"='%s', tipo='%s', \"inscricaoEstadual\"='%s', updated_at=now() " +
                    "WHERE \"cpfCnpj\" = '%s' returning user_id",
                    cliente.getNomeReduzido(), cliente.getNomeResponsavel(), cliente.getTipo(), cliente.getInscricaoEstadual(), cliente.getCpf());
            ResultSet resultSet = db.execute(sql);

            if(resultSet != null){
                ResultSet result = null;
                try {
                    resultSet.next();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    result = atualizarUser(resultSet.getInt("user_id"),cliente, db);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if(result != null){
                    try{
                        atualizarEndereco(result.getInt("endereco_id"),cliente, db);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try{
                        atualizarTelefone(result.getInt("telefone_id"),cliente, db);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        }catch (Exception e){
            throw new java.lang.IllegalArgumentException("Erro na atualização dos dados");
        }
    }

    private ResultSet atualizarUser(int codigo, Cliente cliente, DB db){
        String sql = "UPDATE public.users " +
                "SET \"name\"='%s', email='%s', updated_at=now() " +
                "WHERE id='%s' returning endereco_id, telefone_id";
        sql = String.format(sql, cliente.getNome(), cliente.getEmail(), codigo);
        ResultSet resultSet = db.execute(sql);
        if(resultSet != null){
            try {
                resultSet.next();
                return resultSet;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void atualizarEndereco(int codigo, Cliente cliente, DB db){
        String sql = "UPDATE public.enderecos " +
                "SET rua='%s', numero='%s', bairro='%s', cidade='%s', cep='%s', complemento='%s', uf='%s', updated_at=now() " +
                "WHERE id='%s'";
        sql = String.format(sql, cliente.getEndereco().getRua(), cliente.getEndereco().getNumero(), cliente.getEndereco().getBairro(), cliente.getEndereco().getCidade(), cliente.getEndereco().getCep(), cliente.getEndereco().getComplemento(), cliente.getEndereco().getUf(), codigo);
        ResultSet resultSet = db.execute(sql);
        if(resultSet != null){
            try{
                resultSet.next();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void atualizarTelefone(int codigo, Cliente cliente, DB db){
        String sql = "UPDATE public.telefones " +
                "SET residencial='%s', celular='%s', updated_at=now() " +
                "WHERE id='%s'";
        sql = String.format(sql, cliente.getTelefone(), cliente.getCelular(), codigo);
        ResultSet resultSet = db.execute(sql);
        if(resultSet != null){
            try{
                resultSet.next();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public Cliente buscarCliente(int codigo){
        Cliente cliente = new Cliente();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT CPF, NOME, TELEFONE, LIMITE_CREDITO, RG, ENDERECO");
        sql.append("    FROM CLIENTE ");
        sql.append("    WHERE CPF = ? ");

        String[] parametros = new String[1];
        parametros[0] = String.valueOf(codigo);

        Cursor resultado = conexao.rawQuery(sql.toString(),parametros);

        if(resultado.getCount() > 0) {
            resultado.moveToFirst();

            cliente.setCpf(resultado.getString(resultado.getColumnIndexOrThrow("CPF")));
            cliente.setNome(resultado.getString(resultado.getColumnIndexOrThrow("NOME")));
            cliente.setTelefone(resultado.getString(resultado.getColumnIndexOrThrow("TELEFONE")));
            //cliente.setLimiteCredito(resultado.getDouble(resultado.getColumnIndexOrThrow("LIMITE_CREDITO"))); ;
            //cliente.setRg(resultado.getString(resultado.getColumnIndexOrThrow("RG")));
            //cli.setEndereco(resultado.getInt(resultado.getColumnIndexOrThrow("ENDERECO")));

            return cliente;
        }

        return null;
    }

    public Cliente buscarClienteWeb(String codigo){
        try {
            DB db = new DB();

            Cliente cliente = new Cliente();

            String sql = String.format("select * from clientes where cpfcnpj = '%s'", codigo);

            ResultSet resultSet = db.select(sql);

            if (resultSet != null) {
                resultSet.next();
                cliente.setCpf(resultSet.getString("cpfcnpj"));
                cliente.setNome(resultSet.getString("nomereduzido"));

                return cliente;
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }
}
