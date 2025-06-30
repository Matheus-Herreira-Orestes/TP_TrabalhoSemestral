package src.erp;

import java.math.BigDecimal;

public class Contrato {
    public int id;
    public String descricao;
    public String empresa;
    public java.sql.Date dtInicio;
    public java.sql.Date dtFim;
    public BigDecimal valorContrato;

    public Contrato(int id, String descricao, java.sql.Date dtFim) {
        this.id = id;
        this.descricao = descricao;
        this.dtFim = dtFim;
    }

    public Contrato(int id, String descricao, String empresa, java.sql.Date dtInicio, java.sql.Date dtFim, BigDecimal valorContrato) {
        this.id = id;
        this.descricao = descricao;
        this.empresa = empresa;
        this.dtInicio = dtInicio;
        this.dtFim = dtFim;
        this.valorContrato = valorContrato;
    }

    @Override
    public String toString() {
        return "Contrato #" + id;
    }
}
