package repository;





import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

import MyPackages.JOPCreator;





public class UsuarioDAO {
    
    
    private Connection con = new connections.AtacadaoConnection().connect();;
    private PreparedStatement st;
    private ResultSet rs;
    
    private String slqCreate = "INSERT INTO usuario (nome, cpf, telefone, login, senha, cargo) VALUES (?, ?, ?, ?, ?, ?) ";
    private String sqlRead = "SELECT * FROM usuario";
    private String sqlUpdate = "UPDATE usuario SET telefone = ?, login = ?, senha = ?, cargo = ? WHERE id = ?";
    private String sqlDelete = "DELETE FROM usuario WHERE cpf = ?";
    
    
    public boolean create(Usuario usuario) {
        try {
            st = con.prepareStatement(sqlRead + " WHERE cpf = ? AND login = ?");
            st.setString(1, usuario.getCpf());
            st.setString(2, usuario.getLogin());
            rs = st.executeQuery();
            if (!rs.next()) {
                st = this.con.prepareStatement(slqCreate);
                st.setString(1, usuario.getNome());
                st.setString(2, usuario.getCpf());
                st.setString(3, usuario.getTelefone());
                st.setString(4, usuario.getLogin());
                st.setString(5, usuario.getSenha());
                st.setString(6, usuario.getCargo());
                st.executeUpdate();
                rs.close();
                st.close();
                return true;
            } else {
                new JOPCreator().create("Message.Warn", "Já existe um usuário com o mesmo CPF");
                rs.close();
                st.close();
                return false;
            }
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    
    public List<Usuario> read() {
        try {
            List<Usuario> USUARIOS = new ArrayList<Usuario>();
            st = con.prepareStatement(sqlRead);
            rs = st.executeQuery();
            while (rs.next()) {
                Usuario usuario = new Usuario(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("telefone"),
                    rs.getString("login"),
                    rs.getString("senha"),
                    rs.getString("cargo"));
                USUARIOS.add(usuario);
            }
            rs.close();
            st.close();
            return USUARIOS;
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            System.out.println(e);
            return null;
        }
    }
    public Usuario read(String login, String senha) {
        try {
            st = con.prepareStatement(sqlRead + " WHERE login = ? AND senha = ?");
            st.setString(1, login);
            st.setString(2, senha);
            rs = st.executeQuery();
            if (rs.next()) {
                Usuario usuario = new Usuario(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("telefone"),
                    rs.getString("login"),
                    rs.getString("senha"),
                    rs.getString("cargo"));
                rs.close();
                st.close();
                return usuario;
            } else {
                rs.close();
                st.close();
                return null;
            }
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            System.out.println(e);
            return null;
        }
    }
    public Usuario read(String cpf) {
        try {
            st = con.prepareStatement(sqlRead + " WHERE cpf = ?");
            st.setString(1, cpf);
            rs = st.executeQuery();
            if (rs.next()) {
                Usuario usuario = new Usuario(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("telefone"),
                    rs.getString("login"),
                    rs.getString("senha"),
                    rs.getString("cargo"));
                rs.close();
                st.close();
                return usuario;
            } else {
                rs.close();
                st.close();
                return null;
            }
        } catch (SQLException e) {
            new JOPCreator().create("Message.Error", "Não foi possível se conectar com o banco de dados!");
            System.out.println(e);
            return null;
        }
    }
    
    
    public boolean update(Usuario u) {
        try {
            st = con.prepareStatement(sqlUpdate);
            st.setString(1, u.getTelefone());
            st.setString(2, u.getLogin());
            st.setString(3, u.getSenha());
            st.setString(4, u.getCargo());
            st.setInt(5, u.getId());
            st.executeUpdate();
            st.close();
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