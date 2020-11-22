package com.gps.ludke;

public class Card {
    private String produto, peso, precoKg, total;

    public Card(String produto, String peso, String precoKg, String total){
        this.produto = produto;
        this.peso = peso;
        this.precoKg = precoKg;
        this.total = total;
    }

    public String getProduto(){return produto;}

    public String getPeso(){return peso;}

    public String getPrecoKg(){return precoKg;}

    public String getTotal(){return total;}

}
