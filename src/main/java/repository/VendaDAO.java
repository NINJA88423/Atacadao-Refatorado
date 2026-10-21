package repository;





import model.Venda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.List;
import java.util.ArrayList;

import MyPackages.JOPCreator;





public class VendaDAO {
    
    
    private Connection con = new connections.AtacadaoConnection().connect();;
    private PreparedStatement st;
    private ResultSet rs;
    
    private String slqCreate = "INSERT INTO venda (id_cliente, datahora, total) VALUES (?, ?, ?)";
    private String sqlRead = "SELECT * FROM venda";
    private String []sqlReadTime = {
        "SELECT * FROM venda WHERE datahora >= CURDATE() AND datahora < CURDATE() + INTERVAL 1 DAY",
        "SELECT * FROM venda WHERE datahora >= DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY) AND datahora < DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY) + INTERVAL 7 DAY",
        "SELECT * FROM venda WHERE datahora >= DATE_FORMAT(CURDATE(), '%Y-%m-01') AND datahora < DATE_FORMAT(CURDATE() + INTERVAL 1 MONTH, '%Y-%m-01')",
        "SELECT * FROM venda WHERE datahora >= MAKEDATE(YEAR(CURDATE()), 1) AND datahora < MAKEDATE(YEAR(CURDATE()) + 1, 1)"};
    private String sqlReadLestRow = "SELECT * FROM venda ORDER BY id DESC LIMIT 1";
    private String sqlUpdate = "UPDATE venda SET total = ?, datahora = ? WHERE id = ?";
    private String sqlDelete = "DELETE FROM venda WHERE id = ?";
    
    
    public Venda create(Venda venda) {
        try {
            Timestamp tsp = (venda.getDatahora() != null) ? Timestamp.valueOf(venda.getDatahora()) : new Timestamp(System.currentTimeMillis());
            st = con.prepareStatement(slqCreate);
            st.setInt(1, venda.getIdCliente());
            st.setTimestamp(2, tsp);
            st.setFloat(3, venda.getTotal());
            st.executeUpdate();
            st.close();
            st = con.prepareStatement(sqlReadLestRow);
            rs = st.executeQuery();
            if (rs.next()) {
                venda.setId(rs.getInt("id"));
                return venda;
            } else {
                return null;
            }
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            return null;
        }
    }
    
    
    public List<Venda> read() {
        try {
            List<Venda> VENDAS = new ArrayList<Venda>();
            st = con.prepareStatement(sqlRead);
            rs = st.executeQuery();
            while (rs.next()) {
                Venda venda = new Venda(
                    rs.getInt("id"),
                    rs.getInt("id_cliente"),
                    rs.getTimestamp("datahora").toLocalDateTime(),
                    rs.getFloat("total"));
                VENDAS.add(venda);
            }
            rs.close();
            st.close();
            return VENDAS;
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            System.out.println(e);
            return null;
        }
    }
    public List<Venda> read(int v) {
        try {
            List<Venda> VENDAS = new ArrayList<Venda>();
            st = con.prepareStatement(sqlReadTime[v]);
            rs = st.executeQuery();
            while (rs.next()) {
                Venda venda = new Venda(
                    rs.getInt("id"),
                    rs.getInt("id_cliente"),
                    rs.getTimestamp("datahora").toLocalDateTime(),
                    rs.getFloat("total"));
                VENDAS.add(venda);
            }
            rs.close();
            st.close();
            return VENDAS;
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            System.out.println(e);
            return null;
        }
    }
    
    
    public boolean update(Venda v) {
        try {
            Timestamp tsp = (v.getDatahora() != null) ? Timestamp.valueOf(v.getDatahora()) : new Timestamp(System.currentTimeMillis());
            st = con.prepareStatement(sqlUpdate);
            st.setFloat(1, v.getTotal());
            st.setTimestamp(2, tsp);
            st.setInt(3, v.getId());
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