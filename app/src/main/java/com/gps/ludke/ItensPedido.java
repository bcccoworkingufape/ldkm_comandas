package com.gps.ludke;

import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.gps.ludke.entidade.Cliente;
import com.gps.ludke.entidade.Comanda;
import com.gps.ludke.entidade.Produto;
import com.gps.ludke.entidade.Relatorio;
import com.gps.ludke.entidade.Usuario;
import com.gps.ludke.entidade.Venda;
import com.gps.ludke.repositorio.RelatorioRepositorio;

import java.util.ArrayList;

public class ItensPedido {

    private final static ArrayList<Produto> produtosSelecionados = new ArrayList<>();
    private final static ArrayList<Produto> produtosPedido = new ArrayList<>();
    private final static ArrayList<Venda> vendasPedido = new ArrayList<>();
    private static  ArrayList formasPagamento = new ArrayList();

    private static Comanda comandaSelecionada;

    private static Cliente clienteSelecionado;

    private static Usuario usuarioLogado;

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public static void setUsuarioLogado(Usuario usuarioLogado) {
        ItensPedido.usuarioLogado = usuarioLogado;
    }


    public static Cliente getClienteSelecionado() {
        return clienteSelecionado;
    }

    public static void setClienteSelecionado(Cliente clienteSelecionado) {
        ItensPedido.clienteSelecionado = clienteSelecionado;
    }

    public static void removerClienteSelecionado(){
        clienteSelecionado = null;
    }


    public static Comanda getComandaSelecionada() {
        return comandaSelecionada;
    }

    public static void setComandaSelecionada(Comanda comandaSelecionada) {
        ItensPedido.comandaSelecionada = comandaSelecionada;
    }



    public static void addProdutoPedido( Produto produto ) {
        produtosPedido.add(produto);
    }

    public static ArrayList<Produto> getProdutosPedido() {
        return new ArrayList<>( produtosPedido );
    }

    public static void removeProdutoPedido(Produto produto){
        produtosPedido.remove(produto);
    }

    public static void addProdutoSelecionado( Produto produto ) {
        produtosSelecionados.add(produto);
    }

    public static ArrayList<Produto> getProdutosSelecionados() {
        return new ArrayList<>( produtosSelecionados );
    }

    public static void removerProdutoSelecionado( Produto produto ) {
        produtosSelecionados.remove(produto);
    }

    public static void limparPedido(){
        produtosSelecionados.clear();
        produtosPedido.clear();
        vendasPedido.clear();
        comandaSelecionada = null;
        clienteSelecionado = null;
    }

    public static void atualizaProdutoSelecionado(Produto produto){
        produtosSelecionados.remove(produto);
        produtosSelecionados.add(produto);
    }

    public static void atualizaProdutoPedido(Produto produto){
        produtosPedido.remove(produto);
        produtosPedido.add(produto);
    }

    public static void addVendaPedido( Venda venda ) {
        vendasPedido.add(venda);
    }

    public static void limpaVendaPedido() {
        vendasPedido.clear();
    }

    public static ArrayList<Venda> getVendaPedido() {
        return new ArrayList<>( vendasPedido );
    }

    public static void removeVendaPedido( Venda venda ) {
        vendasPedido.remove(venda);
    }

    public static void atualizaVendaPedido(Venda venda){
        vendasPedido.remove(venda);
        vendasPedido.add(venda);
    }

    public static ArrayList<Venda> getVendasPedido() {
        return vendasPedido;
    }

    public static ArrayList getFormasPagamento() {
        return formasPagamento;
    }

    public static void setFormasPagamento(ArrayList formasPagamento) {
        ItensPedido.formasPagamento = formasPagamento;
    }
}
