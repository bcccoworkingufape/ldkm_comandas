package com.gps.ludke.ui.cliente;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gps.ludke.R;

public class ClienteDadosFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener{

    private BottomNavigationView navigationView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cliente_dados, container, false);

        navigationView = (BottomNavigationView) root.findViewById(R.id.bottomNavigationViewCliente);
        navigationView.setOnNavigationItemSelectedListener(this);

        Fragment editClienteFragment = EditarClienteFragment.newInstance();
        openFragment(editClienteFragment);

        return root;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            /*
            case R.id.navigation_clientes: {
                Fragment clientesFragment = ListagemClienteFragment.newInstance();
                openFragment(clientesFragment);
                break;
            }

             */
            case R.id.navigation_novo_cliente: {
                Fragment criarCliFragment = CriarClienteFragment.newInstance();
                openFragment(criarCliFragment);
                break;
            }
            case R.id.navigation_editar_cliente: {
                Fragment editClientFragment = EditarClienteFragment.newInstance();
                openFragment(editClientFragment);
                break;
            }
        }
        return true;
    }


    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.containerLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
