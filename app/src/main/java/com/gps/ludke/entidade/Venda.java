package com.gps.ludke.entidade;

import androidx.annotation.NonNull;

import com.gps.ludke.entidade.Produto;

import java.text.NumberFormat;
import java.util.Locale;

public class Venda {

    private int id;
    private Produto produto;
    private double  peso;
    private Comanda comanda;
    private boolean isAvista;
    private boolean isDinheiro;
    private boolean isBoleto;
    private String pagamento;
    private double preco;

    private NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));



    public Venda(Produto produto, double peso, Comanda comanda, boolean isAvista, boolean isDinheiro, boolean isBoleto){
        this.produto = produto;
        this.peso = peso;
        this.comanda = comanda;
        this.isAvista = isAvista;
        this.isDinheiro = isDinheiro;
        this.isBoleto = isBoleto;
    }

    public Venda(){}

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Comanda getComanda(){return comanda;}

    public void setComanda(Comanda comanda){
        this.comanda = comanda;
    }

    public boolean isAvista() {
        return isAvista;
    }

    public void setAvista(boolean avista) {
        isAvista = avista;
    }

    public String formaDePagamento(){
        if(isAvista() == true){
            if(isDinheiro == true){
                pagamento = "Dinheiro";
                return pagamento;
            }
            else{
                pagamento = "Cheque";
                return pagamento;
            }
        }
        else{
            if(isBoleto == true){
                pagamento = "Boleto";
                return pagamento;
            }
            else if (isDinheiro == true){
                pagamento = "Dinheiro";
                return pagamento;
            }
            else{
                pagamento = "Cheque";
                return pagamento;
            }
        }
    }

    public void setPagamento(String pagamento){
        this.pagamento = pagamento;
    }

    public double getPeso(){
        return this.peso;
    }

    public void setPeso(double peso){
        this.peso = peso;
    }

    public double valorTotal(int quantidade){
        double total = peso * produto.getPrecoVenda();
        return total;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return produto.getNome() + " | " + formatter.format(preco);
    }
}
