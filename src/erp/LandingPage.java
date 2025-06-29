package src.erp;
import javax.swing.*;
import java.awt.*;

// PLACEHOLDER DA LANDING PAGe
public class LandingPage {
   private boolean isAdmin = true;

    public void mostrar() {
        JFrame frame = new JFrame("Tela Inicial");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Tela Inicial", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titulo, BorderLayout.NORTH);

        JPanel colunasPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        colunasPanel.add(criarColuna("Ano vigente"));
        colunasPanel.add(criarColuna("Próximo ano"));
        colunasPanel.add(criarColuna("> 1 ano"));
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

    private JPanel criarColuna(String titulo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(titulo, SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 14));

        panel.add(label);
        panel.add(Box.createVerticalStrut(10));

        for (int i = 0; i < 5; i++) {
            JButton item = new JButton("Item " + (i + 1));
            item.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(item);
            panel.add(Box.createVerticalStrut(5));
        }

        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return panel;
    }
}