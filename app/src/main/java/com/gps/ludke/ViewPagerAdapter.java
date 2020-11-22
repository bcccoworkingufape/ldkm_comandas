package com.gps.ludke;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.gps.ludke.ui.relatoriocliente.RelatorioClienteFragment;
import com.gps.ludke.ui.venda.EditarVendaFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position)
        {
            case 0:
                fragment = new EditarVendaFragment();

                break;
            case 1:
                fragment = new RelatorioClienteFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {

        String tab = null;
        switch (position)
        {
            case 0:
                tab = "Usuário";

                break;
            case 1:
                tab = "Cliente";

        }
        return tab;
    }
}
