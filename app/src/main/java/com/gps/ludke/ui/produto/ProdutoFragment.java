package com.gps.ludke.ui.produto;

import android.app.ProgressDialog;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.gps.ludke.CardArrayAdapter;
import com.gps.ludke.ClienteArrayAdapter;
import com.gps.ludke.ItensPedido;
import com.gps.ludke.R;
import com.gps.ludke.data.DadosOpenHelper;
import com.gps.ludke.entidade.Cliente;
import com.gps.ludke.entidade.Comanda;
import com.gps.ludke.entidade.Produto;
import com.gps.ludke.repositorio.ClienteRepositorio;
import com.gps.ludke.repositorio.ComandaRepositorio;
import com.gps.ludke.repositorio.ProdutoRepositorio;
import com.gps.ludke.repositorio.VendaRepositorio;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProdutoFragment extends Fragment {


    private VendaRepositorio vendaRepositorio;
    private ClienteRepositorio clienteRepositorio;
    private ComandaRepositorio comandaRepositorio;
    private DadosOpenHelper dadosOpenHelper;
    private SQLiteDatabase conexao;

    private TextView numComanda;
    private Button buttonAbrirPedido;
    private TextView textViewValorTotalCarrinho;
    private TextView textViewQuantdTotalCarrinho;
    private TextView textViewVazio;
    private ProgressBar progressBarCarrinho;
    private ListView listViewCarrinho;
    private CardArrayAdapter cardArrayAdapter;
    private ProgressDialog pDialog;
    private boolean vendaOk;

    private static ArrayList<Cliente> clientes;

    private ListView listView;

    private ProgressBar progressBarCliente;

    private ClienteArrayAdapter clienteArrayAdapter;


    private int idVendas;


    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_produto, container, false);

        progressBarCliente = root.findViewById(R.id.progressBarRelatorioGeral);

        progressBarCliente.setVisibility(View.VISIBLE);

        criarConexao();

        numComanda = (TextView) root.findViewById(R.id.textViewComanda);
        textViewVazio = root.findViewById(R.id.textViewVazio);
        listView = root.findViewById(R.id.listViewCliente);





        return root;
    }

    private void atualizarTotalQuantidadeCarrinho(){
        textViewQuantdTotalCarrinho.setText(Integer.toString(ItensPedido.getProdutosPedido().size()));

        double total = 0;

        for (Produto produto : ItensPedido.getProdutosPedido()){
            total += produto.getPeso()*produto.getPrecoVenda();

        }
        textViewValorTotalCarrinho.setText(formatter.format(total).replace("R$",""));
    }

    public static ProdutoFragment newInstance() {
        return new ProdutoFragment();
    }


    public void atualizarComanda(){
        String codComanda = comandaRepositorio.getCodigoComandaWeb();
        if(codComanda != null){
            int numeroComanda = Integer.parseInt(codComanda)+1;
            numComanda.setText("Comanda nº: " + numeroComanda);
            Comanda comanda = new Comanda(numeroComanda);
            ItensPedido.setComandaSelecionada(comanda);
        }else{
            numComanda.setText("Comanda nº: 1");
            Comanda comanda = new Comanda(1);
            ItensPedido.setComandaSelecionada(comanda);
        }
    }

    private void criarConexao(){
        try {

            dadosOpenHelper = new DadosOpenHelper(getContext());

            conexao = dadosOpenHelper.getWritableDatabase();

            //Snackbar.make(drawer,"Conexão criada com sucesso!",Snackbar.LENGTH_LONG)
            //      .setAction("Ok", null).show();

            clienteRepositorio = new ClienteRepositorio(conexao);
            comandaRepositorio = new ComandaRepositorio(conexao);

        }catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("OK",null);
            dlg.show();
        }
    }

}