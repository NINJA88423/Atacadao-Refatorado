package repository;





import model.ProdutoVenda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

import java.lang.Math;

import MyPackages.JOPCreator;





public class ProdutoVendaDAO {
    
    
    private Connection con = new connections.AtacadaoConnection().connect();;
    private PreparedStatement st;
    private ResultSet rs;
    
    private String slqCreate = "INSERT INTO produtovenda (id_produto, id_venda, isKG, quantidade, desconto, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
    private String sqlRead = "SELECT * FROM produtovenda";
    private String sqlUpdate = "UPDATE produtovenda SET quantidade = ?, desconto = ?, subtotal = ? WHERE id = ?";
    private String sqlDelete = "DELETE FROM produtovenda WHERE id = ?";
    
    
    public void create(ProdutoVenda produtovenda) {
        try {
            st = con.prepareStatement(slqCreate);
            st.setInt(1, produtovenda.getIdProduto());
            st.setInt(2, produtovenda.getIdVenda());
            st.setBoolean(3, produtovenda.getIsKG());
            st.setFloat(4, produtovenda.getQuantidade());
            st.setFloat(5, produtovenda.getDesconto());
            st.setFloat(6, produtovenda.getSubtotal());
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            System.out.println(e); // Unknown column 'preco_por_medida' in 'NEW'
        }
    }
    
    
    public List<ProdutoVenda> read() {
        try {
            List<ProdutoVenda> PRODUTOVENDAS = new ArrayList<ProdutoVenda>();
            st = con.prepareStatement(sqlRead);
            rs = st.executeQuery();
            while (rs.next()) {
                ProdutoVenda venda = new ProdutoVenda(
                    rs.getInt("id"),
                    rs.getInt("id_produto"),
                    rs.getInt("id_venda"),
                    rs.getBoolean("isKG"),
                    rs.getFloat("quantidade"),
                    rs.getByte("desconto"),
                    rs.getFloat("subtotal"));
                PRODUTOVENDAS.add(venda);
            }
            rs.close();
            st.close();
            return PRODUTOVENDAS;
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            return null;
        }
    }
    public List<ProdutoVenda> read(int id_venda) {
        try {
            List<ProdutoVenda> PRODUTOVENDAS = new ArrayList<ProdutoVenda>();
            st = con.prepareStatement(sqlRead + " WHERE id = ?");
            st.setInt(1, id_venda);
            rs = st.executeQuery();
            while (rs.next()) {
                ProdutoVenda venda = new ProdutoVenda(
                    rs.getInt("id"),
                    rs.getInt("id_produto"),
                    rs.getInt("id_venda"),
                    rs.getBoolean("isKG"),
                    rs.getFloat("quantidade"),
                    rs.getByte("desconto"),
                    rs.getFloat("subtotal"));
                PRODUTOVENDAS.add(venda);
            }
            rs.close();
            st.close();
            return PRODUTOVENDAS;
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            return null;
        }
    }
    
    
    public boolean update(ProdutoVenda pv) {
        try {
            st = con.prepareStatement(sqlUpdate);
            st.setFloat(1, pv.getQuantidade());
            st.setFloat(2, pv.getDesconto());
            st.setFloat(3, pv.getSubtotal());
            st.setInt(4, pv.getId());
            st.executeUpdate();
            st.close();
            return true;
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            return false;
        }
    }
    
    
    public void delete(int id) {
        try {
            st = con.prepareStatement(sqlDelete);
            st.setInt(1, id);
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
        }
    }
    
    
}