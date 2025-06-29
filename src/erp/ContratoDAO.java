package src.erp;
import java.sql.*;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;


public class ContratoDAO {
    public static Map<String, List<Contrato>> buscarContratosAgrupadosPorVencimento() {
        Map<String, List<Contrato>> grupos = new HashMap<>();
        grupos.put("vigente", new ArrayList<>());
        grupos.put("proximo", new ArrayList<>());
        grupos.put("futuro", new ArrayList<>());

        String sql = """
            SELECT 
                c.id_contrato,
                c.descricao,
                COALESCE(a.novo_dt_fim, c.dt_fim) AS dt_fim
            FROM contrato c
            LEFT JOIN LATERAL (
                SELECT novo_dt_fim
                FROM aditamento a
                WHERE a.id_contrato = c.id_contrato AND novo_dt_fim IS NOT NULL
                ORDER BY dt_aditamento DESC
                LIMIT 1
            ) a ON true
        """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return grupos;
    }
}
