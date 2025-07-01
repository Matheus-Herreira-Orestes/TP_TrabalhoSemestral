package src.erp;
import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class LandingPage {
   private boolean isAdmin = Sessao.isAdmin;
   JPanel colunasPanel;

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

        colunasPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        colunasPanel.add(criarColuna("Ano vigente", grupos.get("vigente")));
        colunasPanel.add(criarColuna("Próximo ano", grupos.get("proximo")));
        colunasPanel.add(criarColuna("> 1 ano", grupos.get("futuro")));
        mainPanel.add(colunasPanel, BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> atualizarTabelas());
        botoesPanel.add(btnAtualizar);

        if (isAdmin) {
            JButton btnUsuarios = new JButton("Gerenciar Usuários");
            btnUsuarios.addActionListener(e -> {
                new GerenciarUsuarioEmpresa("usuario").mostrar();
            });
            botoesPanel.add(btnUsuarios);
        }

        JButton btnEmpresas = new JButton("Gerenciar Empresas");
        btnEmpresas.addActionListener(e -> {
            new GerenciarUsuarioEmpresa("empresa").mostrar();
        });
        botoesPanel.add(btnEmpresas);

        JButton btnInserir = new JButton("Novo Contrato");
        btnInserir.addActionListener(e -> {
            new ContratoForm(frame, "inserir", null);
        });
        botoesPanel.add(btnInserir);
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> {
            new BuscarContrato().mostrar();
        });
        botoesPanel.add(btnBuscar);

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
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            dados[i][0] = String.valueOf(c.id);
            dados[i][1] = c.descricao;
            dados[i][2] = c.dtFim != null ? sdf.format(c.dtFim) : "";
        }


        JTable tabela = new JTable(dados, colunas);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);
        JScrollPane scrollPane = new JScrollPane(tabela);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void atualizarTabelas() {
        colunasPanel.removeAll();
        Map<String, List<Contrato>> grupos = ContratoDAO.buscarContratosAgrupadosPorVencimento();
        colunasPanel.add(criarColuna("Ano vigente", grupos.get("vigente")));
        colunasPanel.add(criarColuna("Próximo ano", grupos.get("proximo")));
        colunasPanel.add(criarColuna("> 1 ano", grupos.get("futuro")));
        colunasPanel.revalidate();
        colunasPanel.repaint();
    }
}
