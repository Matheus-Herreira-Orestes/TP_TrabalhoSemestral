import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String URL = "jdbc:postgresql://localhost:5432/trabalho_tp";
    private static final String USUARIO = "postgres";
    private static final String SENHA = "postgres";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static void main(String[] args) {
        try (Connection conn = conectar()) {
            System.out.println("Conexão realizada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao conectar: " + e.getMessage());
        }
    }
}
