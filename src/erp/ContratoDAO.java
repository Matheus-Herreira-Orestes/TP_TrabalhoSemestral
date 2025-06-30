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

    // Método para buscar todos os contratos
    public static List<Contrato> buscarTodos() {
        List<Contrato> contratos = new ArrayList<>();

        // SQL base
        StringBuilder sql = new StringBuilder("""
            SELECT 
                c.id_contrato, 
                c.descricao, 
                e.razao AS empresa, 
                c.dt_inicio, 
                COALESCE(a.novo_dt_fim, c.dt_fim) AS dt_fim,
                c.valor_contrato
            FROM contrato c
            JOIN empresa e ON c.id_empresa = e.id_empresa
            LEFT JOIN aditamento a ON a.id_contrato = c.id_contrato
        """);

        // Se não for admin, adiciona cláusula WHERE
        boolean aplicarFiltro = !Sessao.isAdmin;
        if (aplicarFiltro) {
            sql.append(" WHERE c.id_fiscal = ?");
        }

        try (Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Se necessário, define o parâmetro
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


    // Método para buscar contratos filtrados
    public static List<Contrato> buscarPorFiltros(String empresa, Integer idContrato, String descricao, String dtInicio, String dtFim) {
        List<Contrato> contratos = new ArrayList<>();

        // SQL base
        StringBuilder sql = new StringBuilder("""
            SELECT c.id_contrato, c.descricao, e.razao AS empresa, c.dt_inicio, 
                COALESCE(a.novo_dt_fim, c.dt_fim) AS dt_fim,
                c.valor_contrato
            FROM contrato c
            JOIN empresa e ON c.id_empresa = e.id_empresa
            LEFT JOIN aditamento a ON a.id_contrato = c.id_contrato
            WHERE 1=1
        """);

        List<Object> parametros = new ArrayList<>();

        // Filtro para usuários fiscais
        if (!Sessao.isAdmin) {
            sql.append(" AND c.id_fiscal = ?");
            parametros.add(Sessao.idUsuario);
        }

        // Adicionar filtros adicionais
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

        // Executar a consulta
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Definir parâmetros da consulta
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            // Preencher a lista de contratos
            while (rs.next()) {
                contratos.add(contratoFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contratos;
    }

    // Método auxiliar para converter o ResultSet em um objeto Contrato
    private static Contrato contratoFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_contrato");
        String descricao = rs.getString("descricao");
        String empresa = rs.getString("empresa");
        Date dtInicio = rs.getDate("dt_inicio");
        Date dtFim = rs.getDate("dt_fim");
        BigDecimal valor = rs.getBigDecimal("valor_contrato");

        return new Contrato(id, descricao, empresa, dtInicio, dtFim, valor);
    }

    // Método para buscar contratos agrupados por vencimento
    public static Map<String, List<Contrato>> buscarContratosAgrupadosPorVencimento() {
        Map<String, List<Contrato>> grupos = new HashMap<>();
        grupos.put("vigente", new ArrayList<>());
        grupos.put("proximo", new ArrayList<>());
        grupos.put("futuro", new ArrayList<>());

        // Início da SQL base
        String sql = """
            SELECT 
                c.id_contrato,
                c.descricao,
                COALESCE(a.novo_dt_fim, c.dt_fim) AS dt_fim,
                c.valor_contrato
            FROM contrato c
            LEFT JOIN aditamento a ON a.id_contrato = c.id_contrato
        """;

        // Adiciona filtro para fiscais
        if (!Sessao.isAdmin) {
            sql += " WHERE c.id_fiscal = ?";
        }

        try (Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Se não for admin, adiciona o id_fiscal como parâmetro
            if (!Sessao.isAdmin) {
                stmt.setInt(1, Sessao.idUsuario); // Usa o ID do fiscal da sessão
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
