package com.gps.ludke.ui.relatorios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gps.ludke.R;
import com.gps.ludke.ui.cliente.ListagemClienteFragment;

public class RelatoriosFragment extends Fragment {

    public static RelatoriosFragment newInstance() {
        return new RelatoriosFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_relatorios, container, false);

        Button relatorioVendas = root.findViewById(R.id.buttonRelatorioVendas);
        relatorioVendas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment relatoriosVendaFragment = RelatorioVendaFragment.newInstance();
                openFragment(relatoriosVendaFragment);
            }
        });

        Button buttonRelatorioClientes = root.findViewById(R.id.buttonRelatorioClientes);
        buttonRelatorioClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment relatoriosClientesFragment = ListagemClienteFragment.newInstance();
                openFragment(relatoriosClientesFragment);
            }
        });



        return root;
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.containerLayout2, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
