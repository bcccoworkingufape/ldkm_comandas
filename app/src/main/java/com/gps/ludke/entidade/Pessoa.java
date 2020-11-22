package com.gps.ludke.entidade;


public abstract class Pessoa {

	private String nome;
	private String cpf;
	private Endereco endereco;
	private String telefone;
	private String celular;
	private String email;
	private String pass;

	public Pessoa(String nome, String cpf, String telefone,Endereco endereco, String celular, String email, String pass){
		this.nome = nome;
		this.cpf = cpf;
		this.telefone = telefone;
		this.endereco = endereco;
		this.celular = celular;
		this.email = email;
		this.pass = pass;
	}

    public Pessoa() {}

    public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
}