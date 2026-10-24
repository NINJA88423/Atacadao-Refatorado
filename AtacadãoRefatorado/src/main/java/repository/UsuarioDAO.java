package repository;





import model.Usuario;

import connection.AtacadaoConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;





public class UsuarioDAO {
    
    
    
    
    private Connection con = new AtacadaoConnection().connect();;
    private PreparedStatement st;
    private ResultSet rs;
    
    private String slqCreate = "INSERT INTO usuario (nome, cpf, telefone, login, senha, cargo) VALUES (?, ?, ?, ?, ?, ?) ";
    private String sqlRead = "SELECT * FROM usuario";
    private String sqlUpdate = "UPDATE usuario SET telefone = ?, login = ?, senha = ?, cargo = ? WHERE id = ?";
    private String sqlDelete = "DELETE FROM usuario WHERE cpf = ?";
    
    
    
    
    private Usuario buscarUsuario() {
        try {
            Usuario usuario = new Usuario(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("cpf"),
                rs.getString("telefone"),
                rs.getString("login"),
                rs.getString("senha"),
                rs.getString("cargo")
            );
            return usuario;
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível buscar o usúario!", e);
        }
    }
    
    
    
    
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
                System.out.println("Já existe um usuário com o mesmo CPF");
                rs.close();
                st.close();
                return false;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar com o banco de dados!", e);
        }
    }
    
    
    
    
    public List<Usuario> read() {
        try {
            List<Usuario> USUARIOS = new ArrayList<Usuario>();
            st = con.prepareStatement(sqlRead);
            rs = st.executeQuery();
            while (rs.next()) {
                Usuario usuario = buscarUsuario();
                USUARIOS.add(usuario);
            }
            rs.close();
            st.close();
            return USUARIOS;
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar com o banco de dados!", e);
        }
    }
    
    
    public Usuario readByLogin(String login, String senha) {
        try {
            st = con.prepareStatement(sqlRead + " WHERE login = ? AND senha = ?");
            st.setString(1, login);
            st.setString(2, senha);
            rs = st.executeQuery();
            if (rs.next()) {
                Usuario usuario = buscarUsuario();
                rs.close();
                st.close();
                return usuario;
            } else {
                rs.close();
                st.close();
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar com o banco de dados!", e);
        }
    }
    
    
    public Usuario readByCpf(String cpf) {
        try {
            st = con.prepareStatement(sqlRead + " WHERE cpf = ?");
            st.setString(1, cpf);
            rs = st.executeQuery();
            if (rs.next()) {
                Usuario usuario = buscarUsuario();
                rs.close();
                st.close();
                return usuario;
            } else {
                rs.close();
                st.close();
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar com o banco de dados!", e);
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
            throw new IllegalStateException("Não foi possível se conectar com o banco de dados!", e);
        }
    }
    
    
    
    
    public void delete(String cpf) {
        try {
            st = con.prepareStatement(sqlDelete);
            st.setString(1, cpf);
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar com o banco de dados!", e);
        }
    }
    
    
    
    
}