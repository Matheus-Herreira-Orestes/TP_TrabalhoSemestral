package src.erp;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AditamentoDAO {
    public static List<Aditamento> buscarADT() {
        List<Aditamento> aditamentos = new ArrayList<>();

        String sql = """
            SELECT id_aditamento,
                   id_contrato,
                   dt_aditamento,
                   novo_valor,
                   novo_dt_inicio,
                   novo_dt_fim,
                   observacoes
            FROM aditamento
        """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_aditamento");
                int idContrato = rs.getInt("id_contrato");
                Date dtAditamento = rs.getDate("dt_aditamento");

                BigDecimal novoValor = rs.getBigDecimal("novo_valor");
                Date novoDtInicio = rs.getDate("novo_dt_inicio");
                Date novoDtFim = rs.getDate("novo_dt_fim");
                String observacoes = rs.getString("observacoes");

                Aditamento adt = new Aditamento(id, idContrato, dtAditamento, novoValor, novoDtInicio, novoDtFim, observacoes);
                aditamentos.add(adt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return aditamentos;
    }
}
