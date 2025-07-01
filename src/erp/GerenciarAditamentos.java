package src.erp;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

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

        String[] colunas = { "ID", "Contrato", "Observação", "Novo Valor", "Data Início", "Data Fim" };
        model = new DefaultTableModel(colunas, 0);
        tabela = new JTable(model)
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane scrollPane = new JScrollPane(tabela);
        TableColumnModel columnModel = tabela.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(0));
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnVoltar = new JButton("Voltar");
        JButton btnNovo = new JButton("Novo");
        JButton btnAlterar = new JButton("Alterar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnDetalhar = new JButton("Detalhar");

        btnVoltar.addActionListener(e -> {
            dialog.dispose();
        });


        btnNovo.addActionListener(e -> {
            new AditamentoForm(dialog, "inserir", null, idContrato);
            carregarDados();
        });

        btnAlterar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(dialog, "Selecione um aditamento.");
                return;
            }
            int idAditamento = (int) model.getValueAt(linha, 0);
            Aditamento selecionado = AditamentoDAO.buscarPorId(idAditamento);
            new AditamentoForm(dialog, "alterar", selecionado, idContrato);
            carregarDados();
        });

        btnExcluir.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(dialog, "Selecione um aditamento.");
                return;
            }
            int idAditamento = (int) model.getValueAt(linha, 0);
            Aditamento selecionado = AditamentoDAO.buscarPorId(idAditamento);
            new AditamentoForm(dialog, "excluir", selecionado, idContrato);
            carregarDados();
        });

        btnDetalhar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(dialog, "Selecione um aditamento.");
                return;
            }
            int idAditamento = (int) model.getValueAt(linha, 0);
            Aditamento selecionado = AditamentoDAO.buscarPorId(idAditamento);
            new AditamentoForm(dialog, "detalhar", selecionado, idContrato);
            carregarDados();
        });

        painelBotoes.add(btnVoltar);
        painelBotoes.add(btnNovo);
        painelBotoes.add(btnAlterar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnDetalhar);

        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        dialog.setContentPane(painelPrincipal);

        carregarDados();

        dialog.setVisible(true);
    }

    private void carregarDados() {
        model.setRowCount(0);
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
