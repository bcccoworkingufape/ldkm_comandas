package com.gps.ludke.entidade;

import java.util.ArrayList;
import java.util.Date;

public class Pagamento {
    private int id;
    private Date dataPagemento;
    private String observação;
    private double valorTotalPagamento;
    private String status;
    private Comanda comanda;
    private double somaValoresParcelas;
    private ArrayList<Parcela> parcelas;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDataPagemento() {
        return dataPagemento;
    }

    public void setDataPagemento(Date dataPagemento) {
        this.dataPagemento = dataPagemento;
    }

    public String getObservação() {
        return observação;
    }

    public void setObservação(String observação) {
        this.observação = observação;
    }

    public double getValorTotalPagamento() {
        return valorTotalPagamento;
    }

    public void setValorTotalPagamento(double valorTotalPagamento) {
        this.valorTotalPagamento = valorTotalPagamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

    public ArrayList<Parcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(ArrayList<Parcela> parcelas) {
        this.parcelas = parcelas;
    }

    public double getSomaValoresParcelas() {
        return somaValoresParcelas;
    }

    public void setSomaValoresParcelas(double somaValoresParcelas) {
        this.somaValoresParcelas = somaValoresParcelas;
    }
}
