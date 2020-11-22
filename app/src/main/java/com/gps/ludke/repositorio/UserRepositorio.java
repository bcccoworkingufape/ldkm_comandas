package com.gps.ludke.repositorio;

import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.gps.ludke.BCrypt;
import com.gps.ludke.data.DB;
import com.gps.ludke.entidade.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepositorio {

    private SQLiteDatabase conexao;
    public UserRepositorio(SQLiteDatabase conexao){
        this.conexao = conexao;
    }

    public Usuario buscarUsuario(String email, String password){
        try {

            DB db = new DB();
            Usuario usuario = new Usuario();

            String sql = String.format("select u.*, f.id AS funcionarioID from users u, funcionarios f where u.email = '%s' and f.user_id = u.id", email);

            ResultSet resultSet = db.select(sql);

            if (resultSet != null) {
                resultSet.next();
                if(checkPassword(password, resultSet.getString("password"))){
                    usuario.setCpf(resultSet.getString("id"));
                    usuario.setNome(resultSet.getString("name"));
                    usuario.setTipo(resultSet.getString("tipo"));
                    usuario.setEmail(resultSet.getString("email"));
                    usuario.setPass(resultSet.getString("password"));
                    usuario.setFuncionarioID(resultSet.getInt("funcionarioID"));
                    return usuario;
                }else{
                    throw new java.lang.IllegalArgumentException("Dados incorretos");
                }

            }else{
                throw new java.lang.IllegalArgumentException("Problemas de conexão");
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
            throw new java.lang.IllegalArgumentException("Dados incorretos");
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw e;
        }

    }

    public static boolean checkPassword(String password_plaintext, String stored_hash) {
        boolean password_verified = false;

        String hash_php = stored_hash.replaceFirst("2y", "2a");

        if(null == hash_php || !hash_php.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Comparação inválida");

        password_verified = BCrypt.checkpw(password_plaintext, hash_php);

        return(password_verified);
    }


}
