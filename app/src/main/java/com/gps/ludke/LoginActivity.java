package com.gps.ludke;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Usuario;
import com.gps.ludke.repositorio.ClienteRepositorio;
import com.gps.ludke.repositorio.ComandaRepositorio;
import com.gps.ludke.repositorio.ProdutoRepositorio;
import com.gps.ludke.repositorio.UserRepositorio;
import com.gps.ludke.repositorio.VendaRepositorio;


public class LoginActivity extends AppCompatActivity {

    private DadosOpenHelper dadosOpenHelper;
    private UserRepositorio userRepositorio;
    private SQLiteDatabase conexao;
    private Button login;
    private EditText userLogin;
    private EditText userSenha;
    private ProgressBar progressBarLogin;

    private TextInputLayout textInputLayoutLogin;
    private TextInputLayout textInputLayoutSenha;

    private Usuario user;

    private String msgErro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        criarConexao();



        progressBarLogin = findViewById(R.id.progressBarLogin);
        userLogin = (EditText) findViewById(R.id.editTextLogin);
        userSenha = (EditText) findViewById(R.id.editTextSenha);
        login = (Button) findViewById(R.id.buttonLogin);

        textInputLayoutLogin = findViewById(R.id.textInputLayoutLogin);
        textInputLayoutSenha = findViewById(R.id.textInputLayoutSenha);



    }

    @Override
    protected void onResume() {
        super.onResume();
        userLogin.setVisibility(View.VISIBLE);
        userSenha.setVisibility(View.VISIBLE);
        login.setVisibility(View.VISIBLE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String email = sharedPref.getString("ultimo_email","");

        userLogin.setText(email);
        userSenha.setText("");
        progressBarLogin.setVisibility(View.GONE);
    }

    public void doLogin(View view) { //aqui é feito o login
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null) {
            if (imm.isActive())
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            login.setClickable(false);
        }
        progressBarLogin.setVisibility(View.VISIBLE);
        userLogin.setVisibility(View.GONE);
        userSenha.setVisibility(View.GONE);
        login.setVisibility(View.GONE);
        textInputLayoutLogin.setVisibility(View.GONE);
        textInputLayoutSenha.setVisibility(View.GONE);

        user = new Usuario();



        final String usuario = userLogin.getText().toString().trim();
        final String senha = userSenha.getText().toString();

        msgErro = "";

        user = new Usuario();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    user = userRepositorio.buscarUsuario(usuario, senha);

                } catch (IllegalArgumentException e) {

                    msgErro = e.getMessage();
                } catch (Exception e){
                    msgErro = e.getMessage();
                }
                finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(!msgErro.isEmpty() && msgErro != null) {

                                Toast.makeText(getApplicationContext(), msgErro, Toast.LENGTH_LONG).show();
                                progressBarLogin.setVisibility(View.GONE);
                                userLogin.setVisibility(View.VISIBLE);
                                userSenha.setVisibility(View.VISIBLE);
                                login.setVisibility(View.VISIBLE);
                                textInputLayoutLogin.setVisibility(View.VISIBLE);
                                textInputLayoutSenha.setVisibility(View.VISIBLE);

                            }else {
                                if (user.getEmail() != null) {

                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("ultimo_email", user.getEmail());
                                    editor.commit();

                                    ItensPedido.setUsuarioLogado(user);
                                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "Bem-vindo(a), " + user.getNome(), Toast.LENGTH_LONG).show();

                                }
                            }
                            login.setClickable(true);

                        }
                    });
                }
            }
        }).start();

    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    private void criarConexao(){
        try {

            dadosOpenHelper = new DadosOpenHelper(this);

            conexao = dadosOpenHelper.getWritableDatabase();

            userRepositorio = new UserRepositorio(conexao);

        }catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("OK",null);
            dlg.show();
        }
    }


}
