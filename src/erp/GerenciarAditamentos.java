package src.erp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class GerenciarAditamentos {
    public void mostrar() {
        JFrame frame = new JFrame("Aditamentos");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] colunas = { "ID", "Contrato", "Descrição ADT", "Novo valor", "Nova data Início", "Nova data Fim" };

        List<Aditamento> aditamentos = AditamentoDAO.buscarADT();
        Object[][] dados = new Object[aditamentos.size()][colunas.length];

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (int i = 0; i < aditamentos.size(); i++) {
            Aditamento a = aditamentos.get(i);
            dados[i][0] = a.id;
            dados[i][1] = a.idContrato;
            dados[i][2] = a.observacoes != null ? a.observacoes : "";
            dados[i][3] = a.novoValor != null ? a.novoValor : "INALTERADO";
            dados[i][4] = a.novoDtInicio != null ? sdf.format(a.novoDtInicio) : "INALTERADO";
            dados[i][5] = a.novoDtFim != null ? sdf.format(a.novoDtFim) : "INALTERADO";
        }

        JTable tabela = new JTable(new DefaultTableModel(dados, colunas));
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

        frame.setContentPane(painelPrincipal);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GerenciarAditamentos().mostrar());
    }
}
