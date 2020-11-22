package com.gps.ludke.entidade;

public class Cliente extends Pessoa implements Comparable<Cliente>{


    private String inscricaoEstadual;
    private String nomeResponsavel;
    private String nomeReduzido;
    private String tipo;

    public Cliente(String nome, String cpf, Endereco endereco,  String inscricaoEstadual, String nomeResponsavel, String nomeReduzido, String tipo,  String celular,  String telefone, String email, String pass) {
        super(nome, cpf, telefone, endereco, celular, email, pass);
        this.nomeResponsavel = nomeResponsavel;
        this.inscricaoEstadual = inscricaoEstadual;
        this.nomeReduzido= nomeReduzido;
        this.tipo = tipo;
    }

    public Cliente(){
        super();
    }

    public String getInscricaoEstadual(){
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual){
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public String getNomeReduzido() {
        return nomeReduzido;
    }

    public void setNomeReduzido(String nomeReduzido) {
        this.nomeReduzido = nomeReduzido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return getNome();
    }

    @Override
    public int compareTo(Cliente c) {
        return this.getNome().compareTo(c.getNome());
    }
}
