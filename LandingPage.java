import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.List;

// PLACEHOLDER DA LANDING PAGe
public class LandingPage {
   private boolean isAdmin = true;

    public void mostrar() {
        JFrame frame = new JFrame("Tela Inicial");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        Map<String, List<Contrato>> grupos = ContratoDAO.buscarContratosAgrupadosPorVencimento();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Tela Inicial", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titulo, BorderLayout.NORTH);

        JPanel colunasPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        colunasPanel.add(criarColuna("Ano vigente", grupos.get("vigente")));
        colunasPanel.add(criarColuna("Próximo ano", grupos.get("proximo")));
        colunasPanel.add(criarColuna("> 1 ano", grupos.get("futuro")));
        mainPanel.add(colunasPanel, BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        if (isAdmin) {
            JButton btnUsuarios = new JButton("Gerenciar Usuários");
            btnUsuarios.addActionListener(e -> {
                frame.dispose(); // fecha a tela atual
                new TelaGerenciamento().mostrar("Usuários");
            });
            botoesPanel.add(btnUsuarios);

            JButton btnEmpresas = new JButton("Gerenciar Empresas");
            btnEmpresas.addActionListener(e -> {
                frame.dispose();
                new TelaGerenciamento().mostrar("Empresas");
            });
            botoesPanel.add(btnEmpresas);
        }

        botoesPanel.add(new JButton("Inserir"));
        botoesPanel.add(new JButton("Buscar"));

        mainPanel.add(botoesPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel criarColuna(String titulo, List<Contrato> contratos) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(titulo));

        String[] colunas = {"ID", "Descrição", "Vencimento"};
        String[][] dados = new String[contratos.size()][3];

        for (int i = 0; i < contratos.size(); i++) {
            Contrato c = contratos.get(i);
            dados[i][0] = String.valueOf(c.id);
            dados[i][1] = c.descricao;
            dados[i][2] = c.dtFim.toString();
        }

        JTable tabela = new JTable(dados, colunas);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);
        JScrollPane scrollPane = new JScrollPane(tabela);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}
