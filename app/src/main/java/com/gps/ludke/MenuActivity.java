package com.gps.ludke;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Cliente;
import com.gps.ludke.entidade.Comanda;
import com.gps.ludke.entidade.Produto;
import com.gps.ludke.entidade.Venda;
import com.gps.ludke.repositorio.ClienteRepositorio;
import com.gps.ludke.repositorio.ComandaRepositorio;
import com.gps.ludke.repositorio.ProdutoRepositorio;
import com.gps.ludke.repositorio.VendaRepositorio;
import com.gps.ludke.ui.cliente.ClienteFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MenuActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SQLiteDatabase conexao;
    private DadosOpenHelper dadosOpenHelper;
    private MenuItem comandaItem;

    private TextView textViewNomeVendedor;
    private TextView textViewNumeroComanda;

    private NavigationView navigationView;


    private DrawerLayout layoutContentMenu;
    private TextView textViewQuantdTotalCarrinho;
    private TextView textViewValorTotalCarrinho;

    private ListView listViewCarrinho;

    private CardArrayAdapter cardArrayAdapter;

    private ProgressBar progressBarCarrinho;

    private ComandaRepositorio comandaRepositorio;
    private ProdutoRepositorio produtoRepositorio;
    private ClienteRepositorio clienteRepositorio;
    private VendaRepositorio vendaRepositorio;

    private ProgressDialog pDialog;

    private boolean vendaOk;

    private int idVendas;

    private int numCom;

    private boolean clienteRemovido;

    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        //toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        textViewNomeVendedor = headerLayout.findViewById(R.id.textViewNomeVendedor);
        textViewNumeroComanda = headerLayout.findViewById(R.id.textViewNumeroComanda);

        if(ItensPedido.getUsuarioLogado() != null) {
            String[] nomeCompleto = ItensPedido.getUsuarioLogado().getNome().trim().split(" ");
            String primeiroNome = nomeCompleto[0];
            String sourceString;
            if (primeiroNome.length() <= 12) {
                textViewNomeVendedor.setText("Olá, " + primeiroNome +"!");
            } else {
                textViewNomeVendedor.setText("Olá, " + primeiroNome.substring(0, 12) + "!");
            }

        }
        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_cliente, R.id.nav_produto,
                R.id.nav_relatorios, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        LinearLayout linearLayoutSobre = navigationView.findViewById(R.id.linearLayoutSobre);
        LinearLayout linearLayoutSair = navigationView.findViewById(R.id.linearLayoutSair);

        linearLayoutSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MenuActivity.this);
                mBuilder.setTitle("Tem certeza que deseja sair?");
                mBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ItensPedido.limparPedido();
                        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
                mBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

        linearLayoutSobre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, SobreActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
            }
        });


        layoutContentMenu = drawer;
        criarConexao();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void criarConexaoLocal(){
        try {

            dadosOpenHelper = new DadosOpenHelper(this);

            conexao = dadosOpenHelper.getWritableDatabase();
/*
            Snackbar.make(layoutContentMenu,"Conexão criada com sucesso!",Snackbar.LENGTH_LONG)
                    .setAction("Ok", null).show();
*/
        }catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("OK",null);
            dlg.show();
        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Tem certeza que deseja sair?");
        mBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ItensPedido.limparPedido();
                Intent intent = new Intent(MenuActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        mBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_carrinho, menu);

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String codComanda = comandaRepositorio.getCodigoComandaWeb();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String textoComanda;
                            if (codComanda != null) {
                                int numeroComanda = Integer.parseInt(codComanda) + 1;
                                textoComanda = ("Nº: " + numeroComanda);
                                Comanda comanda = new Comanda(numeroComanda);
                                ItensPedido.setComandaSelecionada(comanda);
                            } else {
                                textoComanda = ("Nº: 1");
                                Comanda comanda = new Comanda(1);
                                ItensPedido.setComandaSelecionada(comanda);
                            }
                            textViewNumeroComanda.setText("Comanda nº" + textoComanda.replace("Nº:", ""));

                            try {
                                String[] nomeCompleto = ItensPedido.getUsuarioLogado().getNome().trim().split(" ");
                                String primeiroNome = nomeCompleto[0];
                                String sourceString;
                                if (primeiroNome.length() <= 12) {
                                    sourceString = "Olá, " + "<b>" + primeiroNome + "</b>" + "!<br/>" + textoComanda;
                                } else {
                                    sourceString = "Olá, " + "<b>" + primeiroNome.substring(0, 12) + "</b>" + "!<br/>" + "\n \n \n" + textoComanda;
                                }


                                int positionOfMenuItem = 0;
                                MenuItem olaItem = menu.getItem(positionOfMenuItem);
                                //searchViewItem.setTitle("Olá, "+ItensPedido.getUsuarioLogado().getNome() + "!");
                                SpannableString s = new SpannableString(Html.fromHtml(sourceString));
                                s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
                                olaItem.setTitle(s);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_carrinho){
            abrirCarrinho();
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirCarrinho(){
        Intent intent = new Intent(MenuActivity.this, CarrinhoActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
    }

    private void criarConexao(){
        try {

            dadosOpenHelper = new DadosOpenHelper(MenuActivity.this);

            conexao = dadosOpenHelper.getWritableDatabase();

            //Snackbar.make(drawer,"Conexão criada com sucesso!",Snackbar.LENGTH_LONG)
            //      .setAction("Ok", null).show();

            produtoRepositorio = new ProdutoRepositorio(conexao);
            clienteRepositorio = new ClienteRepositorio(conexao);
            comandaRepositorio = new ComandaRepositorio(conexao);
            vendaRepositorio = new VendaRepositorio(conexao);

        }catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(MenuActivity.this);
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("OK",null);
            dlg.show();
        }
    }


}
