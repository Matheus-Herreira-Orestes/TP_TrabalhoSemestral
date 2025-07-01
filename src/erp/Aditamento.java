package src.erp;

import java.math.BigDecimal;
import java.sql.Date;

public class Aditamento {
    public int id;
    public int idContrato;
    public Date dtAditamento;
    public BigDecimal novoValor;
    public Date novoDtInicio;
    public Date novoDtFim;
    public String observacoes;

    public Aditamento(int id, int idContrato, Date dtAditamento, BigDecimal novoValor, Date novoDtInicio, Date novoDtFim, String observacoes) {
        this.id = id;
        this.idContrato = idContrato;
        this.dtAditamento = dtAditamento;
        this.novoValor = novoValor;
        this.novoDtInicio = novoDtInicio;
        this.novoDtFim = novoDtFim;
        this.observacoes = observacoes;
    }

    @Override
    public String toString() {
        return "Aditamento #" + id + " (Contrato #" + idContrato + ")";
    }
}
