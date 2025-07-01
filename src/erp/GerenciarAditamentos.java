package src.erp;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GerenciarAditamentos {
    private JDialog dialog;
    private JTable tabela;
    private int idContrato;
    private DefaultTableModel model;

    public void mostrar(Window parent, int idContrato) {
        this.idContrato = idContrato;

        dialog = new JDialog(parent, "Aditamentos do Contrato #" + idContrato, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(parent);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] colunas = { "ID", "Contrato", "Descrição ADT", "Novo valor", "Nova data Início", "Nova data Fim" };
        model = new DefaultTableModel(colunas, 0);
        tabela = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tabela);
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnNovo = new JButton("Novo Aditamento");
        JButton btnAlterar = new JButton("Alterar Aditamento");
        JButton btnExcluir = new JButton("Excluir Aditamento");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnAlterar);
        painelBotoes.add(btnExcluir);

        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        dialog.setContentPane(painelPrincipal);

        carregarDados();

        dialog.setVisible(true);
    }

    private void carregarDados() {
        model.setRowCount(0); // limpa a tabela
        List<Aditamento> aditamentos = AditamentoDAO.buscarADTPorContrato(idContrato);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Aditamento a : aditamentos) {
            model.addRow(new Object[] {
                a.id,
                a.idContrato,
                a.observacoes != null ? a.observacoes : "",
                a.novoValor != null ? a.novoValor : "INALTERADO",
                a.novoDtInicio != null ? sdf.format(a.novoDtInicio) : "INALTERADO",
                a.novoDtFim != null ? sdf.format(a.novoDtFim) : "INALTERADO"
            });
        }
    }
}
