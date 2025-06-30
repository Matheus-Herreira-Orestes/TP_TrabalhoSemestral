package src.erp;

import java.util.List;
import javax.swing.table.AbstractTableModel;

public class ContratoTableModel extends AbstractTableModel {
    private final String[] colunas = {"ID", "Descrição", "Empresa", "Data Início", "Data Fim"};
    private List<Contrato> dados;

    public ContratoTableModel(List<Contrato> dados) {
        this.dados = dados;
    }

    public void setDados(List<Contrato> dados) {
        this.dados = dados;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return dados.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public String getColumnName(int column) {
        return colunas[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Contrato c = dados.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> c.id;
            case 1 -> c.descricao;
            case 2 -> c.empresa;
            case 3 -> c.dtInicio;
            case 4 -> c.dtFim;
            default -> null;
        };
    }

    public Contrato getContratoAt(int row) {
        return dados.get(row);
    }

}
