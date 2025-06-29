package src.erp;

public class Contrato {
    public int id;
    public String descricao;
    public java.sql.Date dtFim;

    public Contrato(int id, String descricao, java.sql.Date dtFim) {
        this.id = id;
        this.descricao = descricao;
        this.dtFim = dtFim;
    }

    @Override
    public String toString() {
        return "Contrato #" + id;
    }
}
