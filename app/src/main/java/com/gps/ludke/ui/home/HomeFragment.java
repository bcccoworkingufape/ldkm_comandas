package com.gps.ludke.ui.home;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gps.ludke.ItensPedido;
import com.gps.ludke.ListagemCardArrayAdapter;
import com.gps.ludke.R;
import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Produto;
import com.gps.ludke.repositorio.ClienteRepositorio;
import com.gps.ludke.repositorio.ComandaRepositorio;
import com.gps.ludke.repositorio.ProdutoRepositorio;
import com.gps.ludke.repositorio.VendaRepositorio;
import com.gps.ludke.ui.cliente.ClienteFragment;
import com.gps.ludke.ui.cliente.ListagemClienteFragment;
import com.gps.ludke.ui.relatorios.RelatorioVendaFragment;
import com.gps.ludke.ui.relatorios.RelatoriosFragment;
import com.gps.ludke.ui.venda.EditarVendaFragment;
import com.gps.ludke.ui.venda.VendaFragment;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener{

    private HomeViewModel homeViewModel;

    private DadosOpenHelper dadosOpenHelper;
    private SQLiteDatabase conexao;
    private ProdutoRepositorio produtoRepositorio;
    private ClienteRepositorio clienteRepositorio;
    private ComandaRepositorio comandaRepositorio;
    private VendaRepositorio vendaRepositorio;

    private String[] produto = new String[]{"LIN. MISTA DEF","LIN. DE CARNE SUINA DEF.", "LIN. ESPECIAL","COSTELA DEF.",
            "SALAME TIPO ITALIANO F.", "SALAME TIPO ITALIANO G.", "BACON DEF.", "PÉS DEF. RABOS DEF." +
            "TORRESMO","SALSICHÃO", "BANHA", "COPA", "LOMBO DEF.", "MORCELA", "SALAME COZIDO", "SALSICHA BOCK",
            "SALSICHA BRANCA", "SALSICHA FRANKFURT", "SALSICHA VIENA", "PERNIL C/ OSSO", "PALETA C/ OSSO", "COSTELA SUÍNA", "LOMBO FRESCO",
            "NUCA DE PORCO", "JOELHO DE PORCO", "FILÉZINHO DE PORCO", "PÉS, RABOS CRUS", "TOUCINHO", "KASSELER"};

    private ListView listView;
    private Handler handler = new Handler();
    private ListagemCardArrayAdapter listagemCardArrayAdapter;
    private TextView textViewTotalGeral;

    private final int REQUEST_CODE = 100;

    private EditText editTextPeso;
    private TextView editTextTotal;
    private TextView buttonAbrirPedido;
    private TextView peso;
    private TextView numComanda;

    private List<Produto> dados;

    private ArrayList<Produto> listaProduto;

    private ProgressBar progressBarHome;

    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private BottomNavigationView navigationView;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        navigationView = (BottomNavigationView) root.findViewById(R.id.bottomNavigationViewHome);
        navigationView.setOnNavigationItemSelectedListener(this);
        if(ItensPedido.getClienteSelecionado() == null) {
            Fragment clienteFragment = ClienteFragment.newInstance();
            openFragment(clienteFragment);
        }else{
            Fragment vendaFragment = VendaFragment.newInstance();
            openFragment(vendaFragment);
        }

        return root;
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.containerLayout2, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_venda: {
                if(ItensPedido.getClienteSelecionado() == null) {
                    Fragment clienteFragment = ClienteFragment.newInstance();
                    openFragment(clienteFragment);
                }else{
                   Fragment vendaFragment = VendaFragment.newInstance();
                   openFragment(vendaFragment);
                }
                break;
            }

            case R.id.navigation_editar_venda: {
                Fragment editarVendaFragmentFragment = EditarVendaFragment.newInstance();
                openFragment(editarVendaFragmentFragment);
                break;
            }

            case R.id.navigation_relatorio: {
                /*
                Fragment relatoriosFragment = RelatoriosFragment.newInstance();
                openFragment(relatoriosFragment);

                 */
                abrirDialogRelatorios();
                break;
            }
        }
        return true;
    }

    private void abrirDialogRelatorios(){
        final View mView = getLayoutInflater().inflate(R.layout.dialog_relatorios,null);
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setCancelable(false);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ImageButton imageButtonClose = mView.findViewById(R.id.imageButtonClose);
        Button buttonClientes = mView.findViewById(R.id.buttonClientes);
        Button buttonVendas = mView.findViewById(R.id.buttonVendas);

        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        buttonClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Fragment relatoriosClientesFragment = ListagemClienteFragment.newInstance();
                openFragment(relatoriosClientesFragment);
            }
        });

        buttonVendas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Fragment relatoriosVendaFragment = RelatorioVendaFragment.newInstance();
                openFragment(relatoriosVendaFragment);
            }
        });
    }


    private void criarConexao(){
        try {

            dadosOpenHelper = new DadosOpenHelper(getContext());

            conexao = dadosOpenHelper.getWritableDatabase();

            //Snackbar.make(drawer,"Conexão criada com sucesso!",Snackbar.LENGTH_LONG)
            //      .setAction("Ok", null).show();

            produtoRepositorio = new ProdutoRepositorio(conexao);
            clienteRepositorio = new ClienteRepositorio(conexao);
            comandaRepositorio = new ComandaRepositorio(conexao);
            vendaRepositorio = new VendaRepositorio(conexao);

        }catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("OK",null);
            dlg.show();
        }
    }
}