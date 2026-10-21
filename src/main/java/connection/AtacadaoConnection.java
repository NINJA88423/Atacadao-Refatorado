package connection;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class AtacadaoConnection {

    private Connection con;
    
    public Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/atacadaodatabase", "root", "Casa2509//");
            return con;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("ERROR: não foi possível se conectar com o banco de dados! : " + e.getMessage());
            return null;
        }
    }
    
    public void disconnect() {
        try {
            con.close();
        } catch (SQLException e) {
            System.out.println("ERROR: não foi possível desconectar o banco de dados! : " + e.getMessage());
        }
    }  
    
}