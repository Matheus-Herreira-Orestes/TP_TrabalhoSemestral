package src.erp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class EmpresaDAO {

    public static List<Empresa> buscarTodos() {
        List<Empresa> empresas = new ArrayList<>();
        String sql = "SELECT id_empresa, razao, cnpj, telefone, endereco, bairro, cidade, estado FROM empresa ORDER BY razao";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Empresa emp = new Empresa(
                        rs.getInt("id_empresa"),
                        rs.getString("razao"),
                        rs.getString("cnpj"),
                        rs.getString("telefone"),
                        rs.getString("endereco"),
                        rs.getString("bairro"),
                        rs.getString("cidade"),
                        rs.getString("estado")
                );
                empresas.add(emp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return empresas;
    }

    public static List<Empresa> buscarPorFiltros(String razao, String cnpj) {
        List<Empresa> empresas = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id_empresa, razao, cnpj, telefone, endereco, bairro, cidade, estado FROM empresa WHERE 1=1");
        List<Object> parametros = new ArrayList<>();

        if (razao != null && !razao.isBlank()) {
            sql.append(" AND LOWER(razao) LIKE ?");
            parametros.add("%" + razao.toLowerCase() + "%");
        }
        if (cnpj != null && !cnpj.isBlank()) {
            sql.append(" AND cnpj LIKE ?");
            parametros.add("%" + cnpj + "%");
        }

        sql.append(" ORDER BY razao");

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Empresa emp = new Empresa(
                            rs.getInt("id_empresa"),
                            rs.getString("razao"),
                            rs.getString("cnpj"),
                            rs.getString("telefone"),
                            rs.getString("endereco"),
                            rs.getString("bairro"),
                            rs.getString("cidade"),
                            rs.getString("estado")
                    );
                    empresas.add(emp);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return empresas;
    }

    public static Empresa buscarPorId(int id) {
        String sql = "SELECT id_empresa, razao, cnpj, telefone, endereco, bairro, cidade, estado FROM empresa WHERE id_empresa = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Empresa(
                            rs.getInt("id_empresa"),
                            rs.getString("razao"),
                            rs.getString("cnpj"),
                            rs.getString("telefone"),
                            rs.getString("endereco"),
                            rs.getString("bairro"),
                            rs.getString("cidade"),
                            rs.getString("estado")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void excluir(int id) {
        String sql = "DELETE FROM empresa WHERE id_empresa = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao excluir empresa: " + e.getMessage());
        }
    }

    public static void inserir(Empresa empresa) {
        String sql = "INSERT INTO empresa (razao, cnpj, telefone, endereco, bairro, cidade, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empresa.getRazao());
            stmt.setString(2, empresa.getCnpj());
            stmt.setString(3, empresa.getTelefone());
            stmt.setString(4, empresa.getEndereco());
            stmt.setString(5, empresa.getBairro());
            stmt.setString(6, empresa.getCidade());
            stmt.setString(7, empresa.getEstado());

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Empresa inserida com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao inserir empresa: " + e.getMessage());
        }
    }

    public static void atualizar(Empresa empresa) {
        String sql = "UPDATE empresa SET razao = ?, cnpj = ?, telefone = ?, endereco = ?, bairro = ?, cidade = ?, estado = ? WHERE id_empresa = ?";

        try (Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empresa.getRazao());
            stmt.setString(2, empresa.getCnpj());
            stmt.setString(3, empresa.getTelefone());
            stmt.setString(4, empresa.getEndereco());
            stmt.setString(5, empresa.getBairro());
            stmt.setString(6, empresa.getCidade());
            stmt.setString(7, empresa.getEstado());
            stmt.setInt(8, empresa.getId());

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Empresa atualizada com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao atualizar empresa: " + e.getMessage());
        }
    }
}
