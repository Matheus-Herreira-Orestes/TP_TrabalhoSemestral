package src.erp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class UsuarioDAO {

    public static List<Usuario> buscarTodos() {
        List<Usuario> usuarios = new ArrayList<>();

        String sql = "SELECT id_usuario, nome, cpf, senha, perfil FROM usuario ORDER BY nome";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("senha"),
                        rs.getString("perfil")
                );
                usuarios.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }

    public static List<Usuario> buscarPorFiltros(String nome, String cpf) {
        List<Usuario> usuarios = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id_usuario, nome, cpf, senha, perfil FROM usuario WHERE 1=1");
        List<Object> parametros = new ArrayList<>();

        if (nome != null && !nome.isBlank()) {
            sql.append(" AND LOWER(nome) LIKE ?");
            parametros.add("%" + nome.toLowerCase() + "%");
        }
        if (cpf != null && !cpf.isBlank()) {
            sql.append(" AND cpf LIKE ?");
            parametros.add("%" + cpf + "%");
        }

        sql.append(" ORDER BY nome");

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nome"),
                            rs.getString("cpf"),
                            rs.getString("senha"),
                            rs.getString("perfil")
                    );
                    usuarios.add(u);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }

    public static Usuario buscarPorId(int id) {
        String sql = "SELECT id_usuario, nome, cpf, senha, perfil FROM usuario WHERE id_usuario = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nome"),
                            rs.getString("cpf"),
                            rs.getString("senha"),
                            rs.getString("perfil")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void excluir(int id) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                JOptionPane.showMessageDialog(null,
                    "Não é possível excluir o usuário, pois ele está vinculado a um contrato.",
                    "Erro de Integridade Referencial",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao excluir usuário: " + e.getMessage());
            }
        }
    }

    public static void inserir(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, cpf, senha, perfil) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getPerfil());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao inserir usuário: " + e.getMessage());
        }
    }

    public static void atualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nome = ?, cpf = ?, senha = ?, perfil = ? WHERE id_usuario = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getPerfil());
            stmt.setInt(5, usuario.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage());
        }
    }
}
