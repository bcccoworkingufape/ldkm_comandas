package com.gps.ludke.entidade;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Comanda{

    private int codigo;
    private Cliente cliente;
    private Usuario usuario;
    private double total;
    private Date data;

    public Comanda(Cliente cliente, int codigo){
        this.codigo = codigo;
        this.cliente=cliente;
        data = new Date(System.currentTimeMillis());
    }

    public Comanda(int codigo){
        this.codigo = codigo;
        data = new Date(System.currentTimeMillis());
    }

    public Comanda(){

    }

    public Cliente getCliente(){return cliente;}

    public void setCliente(Cliente cliente){
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

}
