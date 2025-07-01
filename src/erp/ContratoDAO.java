package src.erp;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContratoDAO {

    public static List<Contrato> buscarTodos() {
        List<Contrato> contratos = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT 
                c.id_contrato, 
                c.descricao, 
                e.razao AS empresa, 
                c.dt_inicio, 
                COALESCE(MAX(a.novo_dt_fim), c.dt_fim) AS dt_fim,
                c.valor_contrato,
                c.id_fiscal
            FROM contrato c
            JOIN empresa e ON c.id_empresa = e.id_empresa
            LEFT JOIN aditamento a ON a.id_contrato = c.id_contrato
            GROUP BY 
                c.id_contrato, 
                c.descricao, 
                e.razao, 
                c.dt_inicio, 
                c.dt_fim, 
                c.valor_contrato,
                c.id_fiscal
        """);

        boolean aplicarFiltro = !Sessao.isAdmin;
        if (aplicarFiltro) {
            sql.append(" WHERE c.id_fiscal = ?");
        }

        sql.append(" ORDER BY c.id_contrato");

        try (Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            if (aplicarFiltro) {
                stmt.setInt(1, Sessao.idUsuario);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contratos.add(contratoFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contratos;
    }

    public static List<Contrato> buscarPorFiltros(String empresa, Integer idContrato, String descricao, String dtInicio, String dtFim) {
        List<Contrato> contratos = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT c.id_contrato, c.descricao, e.razao AS empresa, c.dt_inicio, 
                COALESCE(a.novo_dt_fim, c.dt_fim) AS dt_fim,
                c.valor_contrato,
                c.id_fiscal
            FROM contrato c
            JOIN empresa e ON c.id_empresa = e.id_empresa
            LEFT JOIN aditamento a ON a.id_contrato = c.id_contrato
            WHERE 1=1
        """);

        List<Object> parametros = new ArrayList<>();

        if (!Sessao.isAdmin) {
            sql.append(" AND c.id_fiscal = ?");
            parametros.add(Sessao.idUsuario);
        }

        if (empresa != null && !empresa.isBlank()) {
            sql.append(" AND e.razao ILIKE ?");
            parametros.add("%" + empresa + "%");
        }
        if (idContrato != null) {
            sql.append(" AND c.id_contrato = ?");
            parametros.add(idContrato);
        }
        if (descricao != null && !descricao.isBlank()) {
            sql.append(" AND c.descricao ILIKE ?");
            parametros.add("%" + descricao + "%");
        }
        if (dtInicio != null && !dtInicio.isBlank()) {
            try {
                LocalDate dataInicio = LocalDate.parse(dtInicio, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                sql.append(" AND c.dt_inicio >= ?");
                parametros.add(Date.valueOf(dataInicio));
            } catch (DateTimeParseException e) {
                System.err.println("Data início inválida: " + dtInicio);
            }
        }
        if (dtFim != null && !dtFim.isBlank()) {
            try {
                LocalDate dataFim = LocalDate.parse(dtFim, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                sql.append(" AND COALESCE(a.novo_dt_fim, c.dt_fim) <= ?");
                parametros.add(Date.valueOf(dataFim));
            } catch (DateTimeParseException e) {
                System.err.println("Data fim inválida: " + dtFim);
            }
        }

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                contratos.add(contratoFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contratos;
    }

    private static Contrato contratoFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_contrato");
        String descricao = rs.getString("descricao");
        String empresa = rs.getString("empresa");
        Date dtInicio = rs.getDate("dt_inicio");
        Date dtFim = rs.getDate("dt_fim");
        BigDecimal valor = rs.getBigDecimal("valor_contrato");
        int idFiscal = rs.getInt("id_fiscal");

        Contrato contrato = new Contrato(id, descricao, empresa, dtInicio, dtFim, valor);
        contrato.setIdFiscal(idFiscal);

        return contrato;
    }

    public static Map<String, List<Contrato>> buscarContratosAgrupadosPorVencimento() {
        Map<String, List<Contrato>> grupos = new HashMap<>();
        grupos.put("vigente", new ArrayList<>());
        grupos.put("proximo", new ArrayList<>());
        grupos.put("futuro", new ArrayList<>());

        String sql = """
            SELECT 
                c.id_contrato,
                c.descricao,
                COALESCE(a.novo_dt_fim, c.dt_fim) AS dt_fim,
                c.valor_contrato
            FROM contrato c
            LEFT JOIN aditamento a ON a.id_contrato = c.id_contrato
        """;

        if (!Sessao.isAdmin) {
            sql += " WHERE c.id_fiscal = ?";
        }

        sql += " ORDER BY dt_fim ASC";

        try (Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (!Sessao.isAdmin) {
                stmt.setInt(1, Sessao.idUsuario);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                int anoAtual = java.time.Year.now().getValue();

                while (rs.next()) {
                    int id = rs.getInt("id_contrato");
                    String descricao = rs.getString("descricao");
                    Date dtFim = rs.getDate("dt_fim");

                    int anoFim = dtFim.toLocalDate().getYear();

                    Contrato contrato = new Contrato(id, descricao, dtFim);
                    if (anoFim == anoAtual) {
                        grupos.get("vigente").add(contrato);
                    } else if (anoFim == anoAtual + 1) {
                        grupos.get("proximo").add(contrato);
                    } else if (anoFim > anoAtual + 1) {
                        grupos.get("futuro").add(contrato);
                    }
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return grupos;
    }

}
