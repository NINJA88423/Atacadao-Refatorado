package repository;





import model.Cliente;

import connection.AtacadaoConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;





public class ClienteDAO {
    
    
    
    
    private Connection con = new AtacadaoConnection().connect();
    private PreparedStatement st;
    private ResultSet rs;
    
    private String slqCreate = "INSERT INTO cliente (nome, cpf, telefone) VALUES (?, ?, ?) ";
    private String sqlRead = "SELECT * FROM cliente";
    private String sqlUpdate = "UPDATE cliente SET telefone = ? WHERE id = ?";
    private String sqlDelete = "DELETE FROM cliente WHERE cpf = ?";
    
    
    
    
    private Cliente buscarCliente() {
        try {
            Cliente cliente = new Cliente(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("cpf"),
                rs.getString("telefone"));
            return cliente;
        } catch (SQLException e) {
            System.out.println("Não foi possível buscar o cliente! : " + e);
            System.out.println(e);
            return null;
        }
    }
    
    
    
    
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
                System.out.print("Já existe um cliente com o mesmo CPF");
                rs.close();
                st.close();
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
            return false;
        }
    }
    
    
    
    
    public List<Cliente> read() {
        try {
            List<Cliente> CLIENTES = new ArrayList<Cliente>();
            st = con.prepareStatement(sqlRead);
            rs = st.executeQuery();
            while (rs.next()) {
                Cliente cliente = buscarCliente();
                CLIENTES.add(cliente);
            }
            rs.close();
            st.close();
            return CLIENTES;
        } catch (SQLException e) {
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
            System.out.println(e);
            return null;
        }
    }
    
    
    public Cliente readById(int id) {
        try {
            Cliente cliente = null;
            st = con.prepareStatement(sqlRead + " WHERE id = ?");
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                cliente = buscarCliente();
            }
            rs.close();
            st.close();
            return cliente;
        } catch (SQLException e) {
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
            return null;
        }
    }
    
    
    public Cliente readByCpf(String cpf) {
        try {
            Cliente cliente = null;
            st = con.prepareStatement(sqlRead + " WHERE cpf = ?");
            st.setString(1, cpf);
            rs = st.executeQuery();
            if (rs.next()) {
                cliente = buscarCliente();
            }
            rs.close();
            st.close();
            return cliente;
        } catch (SQLException e) {
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
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
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
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
            System.out.println("Não foi possível se conectar com o banco de dados! : " + e);
        }
    }
    
    
    
    
}