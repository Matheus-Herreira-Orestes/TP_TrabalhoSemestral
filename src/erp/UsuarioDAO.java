package src.erp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public static List<Usuario> buscarTodos() {
        List<Usuario> usuarios = new ArrayList<>();

        String sql = "SELECT id_usuario, nome FROM usuario ORDER BY nome";

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(new Usuario(rs.getInt("id_usuario"), rs.getString("nome")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }
}
