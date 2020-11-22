package com.gps.ludke.repositorio;

import android.database.sqlite.SQLiteDatabase;

import com.gps.ludke.data.DB;
import com.gps.ludke.entidade.Cliente;
import com.gps.ludke.entidade.Comanda;
import com.gps.ludke.entidade.Produto;
import com.gps.ludke.entidade.Relatorio;
import com.gps.ludke.entidade.Usuario;
import com.gps.ludke.entidade.Venda;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class RelatorioRepositorio {

    private SQLiteDatabase conexao;
    public RelatorioRepositorio(SQLiteDatabase conexao){
        this.conexao = conexao;
    }

    public ArrayList<Relatorio> select(){
        ArrayList<Relatorio> relatoriosArrayList = new ArrayList<Relatorio>();
        try{
            DB db = new DB();

            String sql = "select item.\"pesoFinal\" , item.\"valorReal\", item.produto_id, item.pedido_id, p.nome, ped.\"valorTotal\", ped.created_at, c.\"nomeReduzido\", c.\"cpfCnpj\", u.\"name\" from itens_pedidos item, produtos p, pedidos ped, clientes c, users u where p.id = item.produto_id and ped.id = pedido_id and c.id = ped.cliente_id and (select f.user_id from funcionarios f where ped.funcionario_id = f.id) = u.id";
            ResultSet resultSet = db.select(sql);
            if(resultSet != null){
                while (resultSet.next()){
                    Venda venda = new Venda();
                    Produto produto = new Produto();
                    Comanda comanda = new Comanda();
                    Cliente cliente = new Cliente();
                    Usuario user = new Usuario();

                    Relatorio relatorio = new Relatorio();

                    comanda.setCodigo(resultSet.getInt("pedido_id"));
                    comanda.setTotal(resultSet.getDouble("valorTotal"));

                    cliente.setNomeReduzido(resultSet.getString("nomeReduzido"));
                    cliente.setCpf(resultSet.getString("cpfCnpj"));

                    user.setNome(resultSet.getString("name"));
                    Date data = resultSet.getDate("created_at");

                    venda.setPeso(resultSet.getDouble("pesofinal"));
                    venda.setPreco(resultSet.getDouble("valorreal"));
                    produto.setCodigo(resultSet.getString("produto_id"));
                    produto.setNome(resultSet.getString("nome"));

                    comanda.setData(data);
                    comanda.setCliente(cliente);
                    comanda.setUsuario(user);

                    venda.setProduto(produto);
                    venda.setComanda(comanda);

                    relatorio.setComanda(comanda);
                    relatorio.addVenda(venda);

                    if(!relatoriosArrayList.contains(relatorio)){
                        relatoriosArrayList.add(relatorio);
                    }else{
                        for(Relatorio rel : relatoriosArrayList) {
                            if(rel.getComanda().getCodigo() == relatorio.getComanda().getCodigo()) {
                                rel.addVenda(venda);
                            }
                        }
                    }
                }
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        Collections.sort(relatoriosArrayList);
        return relatoriosArrayList;
    }

    public ArrayList<Relatorio> select(String id){
        ArrayList<Relatorio> relatoriosArrayList = new ArrayList<Relatorio>();
        try{
            DB db = new DB();

            String sql = "select item.id, item.\"pesoFinal\" , item.\"valorReal\", item.produto_id, item.pedido_id, p.nome, p.preco, ped.\"valorTotal\", ped.created_at, c.\"nomeReduzido\", c.\"cpfCnpj\", u.\"name\" from itens_pedidos item, produtos p, pedidos ped, clientes c, users u where p.id = item.produto_id and ped.id = pedido_id and c.id = ped.cliente_id and u.id = "+id+ " and (select f.user_id from funcionarios f where ped.funcionario_id = f.id) = u.id";
            ResultSet resultSet = db.select(sql);
            if(resultSet != null){
                while (resultSet.next()){
                    Venda venda = new Venda();
                    Produto produto = new Produto();
                    Comanda comanda = new Comanda();
                    Cliente cliente = new Cliente();
                    Usuario user = new Usuario();

                    Relatorio relatorio = new Relatorio();

                    comanda.setCodigo(resultSet.getInt("pedido_id"));
                    comanda.setTotal(resultSet.getDouble("valorTotal"));

                    cliente.setNomeReduzido(resultSet.getString("nomeReduzido"));
                    cliente.setCpf(resultSet.getString("cpfCnpj"));

                    user.setNome(resultSet.getString("name"));
                    Date data = resultSet.getDate("created_at");

                    venda.setPeso(resultSet.getDouble("pesofinal"));
                    venda.setPreco(resultSet.getDouble("valorreal"));
                    venda.setId(resultSet.getInt("id"));
                    produto.setCodigo(resultSet.getString("produto_id"));
                    produto.setNome(resultSet.getString("nome"));
                    produto.setPrecoVenda(resultSet.getDouble("preco"));

                    comanda.setData(data);
                    comanda.setCliente(cliente);
                    comanda.setUsuario(user);

                    venda.setProduto(produto);
                    venda.setComanda(comanda);

                    relatorio.setComanda(comanda);
                    relatorio.addVenda(venda);

                    if(!relatoriosArrayList.contains(relatorio)){
                        relatoriosArrayList.add(relatorio);
                    }else{
                        for(Relatorio rel : relatoriosArrayList) {
                            if(rel.getComanda().getCodigo() == relatorio.getComanda().getCodigo()) {
                                rel.addVenda(venda);
                            }
                        }
                    }
                }
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        Collections.sort(relatoriosArrayList);
        return relatoriosArrayList;
    }

}
