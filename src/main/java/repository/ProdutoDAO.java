package repository;





import model.Produto;

import connection.AtacadaoConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;





public class ProdutoDAO {
    
    
    
    
    private Connection con = new AtacadaoConnection().connect();;
    private PreparedStatement st;
    private ResultSet rs;
    
    private String slqCreate = "INSERT INTO produto (codigo, nome, marca, preco, isKG) VALUES (?, ?, ?, ?, ?) ";
    private String sqlRead = "SELECT * FROM produto";
    private String sqlUpdate = "UPDATE produto SET nome = ?, marca = ?, preco = ?, isKG = ? WHERE id = ?";
    private String sqlDelete = "DELETE FROM produto WHERE codigo = ?";
    
    
    
    
    private Produto buscarProduto() {
        try {
            Produto produto = new Produto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nome"),
                rs.getString("marca"),
                rs.getBoolean("isKG"),
                rs.getBigDecimal("preco")
            );
            return produto;
        } catch (SQLException e) {
            System.out.println("Não foi possível buscar o produto! : " + e);
            System.out.println(e);
            return null;
        }
    }
     
    
    
    
    public boolean create(Produto produto) {
        try {
            st = con.prepareStatement(sqlRead + " WHERE codigo = ?");
            st.setString(1, produto.getCodigo());
            rs = st.executeQuery();
            if (!rs.next()) {
                st = this.con.prepareStatement(slqCreate);
                st.setString(1, produto.getCodigo());
                st.setString(2, produto.getNome());
                st.setString(3, produto.getMarca());
                st.setBigDecimal(4, produto.getPreco());
                st.setBoolean(5, produto.getIsKG());
                st.executeUpdate();
                rs.close();
                st.close();
                return true;
            } else {
                System.out.println("Já existe um produto com o mesmo código");
                rs.close();
                st.close();
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
            return false;
        }
    }
    
    
    
    
    public List<Produto> read() {
        try {
            List<Produto> PRODUTOS = new ArrayList<Produto>();
            st = con.prepareStatement(sqlRead);
            rs = st.executeQuery();
            while (rs.next()) {
                Produto produto = buscarProduto();
                PRODUTOS.add(produto);
            }
            rs.close();
            st.close();
            return PRODUTOS;
        } catch (SQLException e) {
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
            return null;
        }
    }
    
    
    public Produto readByCodigo(String codigo) {
        try {
            Produto p = null;
            st = con.prepareStatement(sqlRead + " WHERE codigo = ?");
            st.setString(1, codigo);
            rs = st.executeQuery();
            if (rs.next()) {
                p = buscarProduto();
            }
            rs.close();
            st.close();
            return p;
        } catch (SQLException e) {
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
            return null;
        }
    }
    
    
    public Produto readById(int id) {
        try {
            Produto p = null;
            st = con.prepareStatement(sqlRead + " WHERE id = ?");
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                p = buscarProduto();
            }
            rs.close();
            st.close();
            return p;
        } catch (SQLException e) {
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
            return null;
        }
    }
    
    
    
    
    public boolean update(Produto p) {
        try {
            st = con.prepareStatement(sqlUpdate);
            st.setString(1, p.getNome());
            st.setString(2, p.getMarca());
            st.setBigDecimal(3, p.getPreco());
            st.setBoolean(4, p.getIsKG());
            st.setInt(5, p.getId());
            st.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
            return false;
        }
    }
    
    
    
    
    public void delete(String codigo) {
        try {
            st = con.prepareStatement(sqlDelete);
            st.setString(1, codigo);
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
        }
    }
    
    
    
    
}