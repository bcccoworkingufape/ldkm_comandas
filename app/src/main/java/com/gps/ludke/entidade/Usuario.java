package com.gps.ludke.entidade;

public class Usuario extends Pessoa {

    private String tipo;
    private int funcionarioID;

    public Usuario(String nome, String cpf, Endereco endereco, String email, String tipo, String password, String celular,  String telefone, int funcionarioID){
        super(nome, cpf, telefone, endereco, celular, email, password);
        this.tipo = tipo;
        this.funcionarioID = funcionarioID;
    }

    public Usuario(){super();}

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getFuncionarioID() {
        return funcionarioID;
    }

    public void setFuncionarioID(int funcionarioID) {
        this.funcionarioID = funcionarioID;
    }
}
