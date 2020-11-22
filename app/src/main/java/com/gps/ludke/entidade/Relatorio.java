package com.gps.ludke.entidade;

import java.sql.Array;
import java.util.ArrayList;

public class Relatorio implements Comparable<Relatorio>{

    private Comanda comanda;
    private ArrayList<Venda> vendas;

    public Relatorio(Comanda comanda, ArrayList<Venda> vendas){
        this.comanda = comanda;
        this.vendas = vendas;
    }

    public Relatorio(){
        this.vendas = new ArrayList<>();
    }

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

    public ArrayList<Venda> getVendas() {
        return vendas;
    }

    public int getQuantidadeVendas(){
        return vendas.size();
    }

    public void setVendas(ArrayList<Venda> vendas) {
        this.vendas = vendas;
    }

    public void addVenda(Venda venda){
        this.vendas.add(venda);
    }

    @Override
    public boolean equals(Object v){
        boolean retVal = false;

        if (v instanceof Relatorio){
            Relatorio ptr = (Relatorio) v;
            retVal = ptr.getComanda().getCodigo() == this.comanda.getCodigo();//.longValue() == this.id;
        }

        return retVal;
    }

    @Override
    public int compareTo(Relatorio relatorio) { //é esperado que retorne um valor positivo se o codigo for comparativamente menor, um valor negativo se for comparativamente maior e 0 se forem iguais.
        return this.comanda.getCodigo() > relatorio.getComanda().getCodigo() ? 1 :
                this.comanda.getCodigo() < relatorio.getComanda().getCodigo() ? -1 : 0;
    }

}
