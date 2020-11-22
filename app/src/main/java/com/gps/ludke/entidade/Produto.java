package com.gps.ludke.entidade;

public class Produto implements Comparable<Produto>{

    private String codigo;
    private String nome;
    private String descricao;
    private String fornecedor;
    private double precoCompra;
    private double precoVenda;
    private double peso;

    public Produto(String codigo, String nome, String descricao, String fornecedor, double precoCompra, double precoVenda){
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.fornecedor = fornecedor;
        this.precoCompra = precoCompra;
        this.precoVenda = precoVenda;
    }

    public Produto(){}

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public double getPrecoCompra() {
        return precoCompra;
    }

    public void setPrecoCompra(double precoCompra) {
        this.precoCompra = precoCompra;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public double getPeso(){
        return peso;
    }

    public void setPeso(double peso){
        this.peso = peso;
    }

    @Override
    public int compareTo(Produto p) {
        return this.getNome().compareTo(p.getNome());
    }
}
