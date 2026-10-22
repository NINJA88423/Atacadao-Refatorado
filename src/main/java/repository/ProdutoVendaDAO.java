package repository;





import model.ProdutoVenda;

import connection.AtacadaoConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

import java.math.BigDecimal;





public class ProdutoVendaDAO {
    
    
    
    
    private Connection con = new AtacadaoConnection().connect();;
    private PreparedStatement st;
    private ResultSet rs;
    
    private String slqCreate = "INSERT INTO produtovenda (id_produto, id_venda, isKG, quantidade, desconto, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
    private String sqlRead = "SELECT * FROM produtovenda";
    private String sqlUpdate = "UPDATE produtovenda SET quantidade = ?, desconto = ?, subtotal = ? WHERE id = ?";
    private String sqlDelete = "DELETE FROM produtovenda WHERE id = ?";
    
    
    
    
    private ProdutoVenda buscarProdutoVenda() {
        try {
            ProdutoVenda pvenda = new ProdutoVenda(
                rs.getInt("id"),
                rs.getInt("id_produto"),
                rs.getInt("id_venda"),
                rs.getBoolean("isKG"),
                rs.getFloat("quantidade"),
                rs.getByte("desconto"),
                rs.getBigDecimal("subtotal")
            );
            return pvenda;
        } catch (SQLException e) {
            System.out.println("Não foi possível buscar o produto da venda! : " + e);
            return null;
        }
    }
    
    
    
    
    public void create(ProdutoVenda produtovenda) {
        try {
            st = con.prepareStatement(slqCreate);
            st.setInt(1, produtovenda.getIdProduto());
            st.setInt(2, produtovenda.getIdVenda());
            st.setBoolean(3, produtovenda.getIsKG());
            st.setFloat(4, produtovenda.getQuantidade());
            st.setFloat(5, produtovenda.getDesconto());
            st.setBigDecimal(6, produtovenda.getSubtotal());
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
        }
    }
    
    
    
    
    public List<ProdutoVenda> read() {
        try {
            List<ProdutoVenda> PRODUTOVENDAS = new ArrayList<ProdutoVenda>();
            st = con.prepareStatement(sqlRead);
            rs = st.executeQuery();
            while (rs.next()) {
                ProdutoVenda venda = buscarProdutoVenda();
                PRODUTOVENDAS.add(venda);
            }
            rs.close();
            st.close();
            return PRODUTOVENDAS;
        } catch (SQLException e) {
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
            return null;
        }
    }
    
    
    public List<ProdutoVenda> readByIdVenda(int id_venda) {
        try {
            List<ProdutoVenda> PRODUTOVENDAS = new ArrayList<ProdutoVenda>();
            st = con.prepareStatement(sqlRead + " WHERE id = ?");
            st.setInt(1, id_venda);
            rs = st.executeQuery();
            while (rs.next()) {
                ProdutoVenda venda = buscarProdutoVenda();
            }
            rs.close();
            st.close();
            return PRODUTOVENDAS;
        } catch (SQLException e) {
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
            return null;
        }
    }
    
    
    
    
    public boolean update(ProdutoVenda pv) {
        try {
            st = con.prepareStatement(sqlUpdate);
            st.setFloat(1, pv.getQuantidade());
            st.setFloat(2, pv.getDesconto());
            st.setBigDecimal(3, pv.getSubtotal());
            st.setInt(4, pv.getId());
            st.executeUpdate();
            st.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
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
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
        }
    }
    
    
    
    
}