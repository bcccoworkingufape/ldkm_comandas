package com.gps.ludke.entidade;

import java.util.Date;

public class Parcela {
    private int id;
    private Date vencimento;
    private String formaPagamento;
    private double valor;
    private String descontoPagamento;

    public Parcela(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getDescontoPagamento() {
        return descontoPagamento;
    }

    public void setDescontoPagamento(String descontoPagamento) {
        this.descontoPagamento = descontoPagamento;
    }
}
