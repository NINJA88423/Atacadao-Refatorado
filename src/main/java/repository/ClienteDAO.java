package repository;





import model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

import MyPackages.JOPCreator;





public class ClienteDAO {
    
    
    private Connection con = new connections.AtacadaoConnection().connect();;
    private PreparedStatement st;
    private ResultSet rs;
    
    private String slqCreate = "INSERT INTO cliente (nome, cpf, telefone) VALUES (?, ?, ?) ";
    private String sqlRead = "SELECT * FROM cliente";
    private String sqlUpdate = "UPDATE cliente SET telefone = ? WHERE id = ?";
    private String sqlDelete = "DELETE FROM cliente WHERE cpf = ?";
    
    
    public boolean create(Cliente cliente) {
        try {
            st = con.prepareStatement(sqlRead + " WHERE cpf = ?");
            st.setString(1, cliente.getCpf());
            rs = st.executeQuery();
            if (!rs.next()) {
                st = this.con.prepareStatement(slqCreate);
                st.setString(1, cliente.getNome());
                st.setString(2, cliente.getCpf());
                st.setString(3, cliente.getTelefone());
                st.executeUpdate();
                rs.close();
                st.close();
                return true;
            } else {
                new JOPCreator().create("Message.Warn", "Já existe um cliente com o mesmo CPF");
                rs.close();
                st.close();
                return false;
            }
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            return false;
        }
    }
    
    
    public List<Cliente> read() {
        try {
            List<Cliente> CLIENTES = new ArrayList<Cliente>();
            st = con.prepareStatement(sqlRead);
            rs = st.executeQuery();
            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("telefone"));
                CLIENTES.add(cliente);
            }
            rs.close();
            st.close();
            return CLIENTES;
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            System.out.println(e);
            return null;
        }
    }
    public Cliente read(int id) {
        try {
            Cliente cliente = null;
            st = con.prepareStatement(sqlRead + " WHERE id = ?");
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                cliente = new Cliente(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("telefone"));
            }
            rs.close();
            st.close();
            return cliente;
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            System.out.println(e);
            return null;
        }
    }
    public Cliente read(String cpf) {
        try {
            Cliente cliente = null;
            st = con.prepareStatement(sqlRead + " WHERE cpf = ?");
            st.setString(1, cpf);
            rs = st.executeQuery();
            if (rs.next()) {
                cliente = new Cliente(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("telefone"));
            }
            rs.close();
            st.close();
            return cliente;
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            System.out.println(e);
            return null;
        }
    }
    
    
    public boolean update(Cliente c) {
        try {
            st = con.prepareStatement(sqlUpdate);
            st.setString(1, c.getTelefone());
            st.setInt(2, c.getId());
            st.executeUpdate();
            return true;
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            return false;
        }
    }
    
    
    public void delete(String cpf) {
        try {
            st = con.prepareStatement(sqlDelete);
            st.setString(1, cpf);
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
        }
    }
    
    
}