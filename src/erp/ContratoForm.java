package src.erp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import javax.swing.*;

public class ContratoForm {
    private JDialog dialog;
    private JComboBox<Empresa> cbEmpresa;
    private JTextField txtIdContrato, txtValor, txtDtInicio, txtDtFim;
    private JTextArea txtDescricao;
    private String modo; // "inserir", "alterar", "excluir", "detalhar"

    public ContratoForm(JFrame parent, String modo) {
        this.modo = modo.toLowerCase();
        dialog = new JDialog(parent, capitalize(modo) + " Contrato", true); // modal = true
        criarTela();
    }

    private void criarTela() {
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel(capitalize(modo) + " Contrato", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;

        // Empresa (JComboBox)
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Empresa:"), gbc);

        cbEmpresa = new JComboBox<>();
        carregarEmpresas();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        panel.add(cbEmpresa, gbc);

        gbc.gridwidth = 1;

        // ID Contrato
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Id do contrato:"), gbc);

        txtIdContrato = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(txtIdContrato, gbc);

        // Valor Total
        gbc.gridx = 2;
        gbc.gridy = 2;
        panel.add(new JLabel("Valor Total:"), gbc);

        txtValor = new JTextField(15);
        gbc.gridx = 3;
        gbc.gridy = 2;
        panel.add(txtValor, gbc);

        // Dt Início
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Dt início:"), gbc);

        txtDtInicio = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(txtDtInicio, gbc);

        // Dt Fim
        gbc.gridx = 2;
        gbc.gridy = 3;
        panel.add(new JLabel("Dt fim:"), gbc);

        txtDtFim = new JTextField(15);
        gbc.gridx = 3;
        gbc.gridy = 3;
        panel.add(txtDtFim, gbc);

        // Descrição
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Descrição:"), gbc);

        txtDescricao = new JTextArea(5, 30);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        JScrollPane scrollDescricao = new JScrollPane(txtDescricao);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(scrollDescricao, gbc);

        // Botões
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        JButton btnVoltar = new JButton("Voltar");
        JButton btnAcao = new JButton(capitalize(modo));

        btnVoltar.addActionListener(e -> dialog.dispose());
        btnAcao.addActionListener(this::executarAcao);

        botoes.add(btnVoltar);
        botoes.add(btnAcao);

        panel.add(botoes, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void carregarEmpresas() {
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id_empresa, razao FROM empresa ORDER BY razao")) {

            while (rs.next()) {
                cbEmpresa.addItem(new Empresa(rs.getInt("id_empresa"), rs.getString("razao")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar empresas: " + e.getMessage());
        }
    }

    private void executarAcao(ActionEvent e) {
        Empresa empresaSelecionada = (Empresa) cbEmpresa.getSelectedItem();
        if (empresaSelecionada != null) {
            int idEmpresa = empresaSelecionada.getId();
            System.out.println("ID da empresa selecionada: " + idEmpresa);
        }

        switch (modo) {
            case "inserir":
                JOptionPane.showMessageDialog(dialog, "Contrato inserido com sucesso!");
                break;
            case "alterar":
                JOptionPane.showMessageDialog(dialog, "Contrato alterado com sucesso!");
                break;
            case "excluir":
                int opcao = JOptionPane.showConfirmDialog(dialog, "Deseja realmente excluir o contrato?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (opcao == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(dialog, "Contrato excluído.");
                }
                break;
            case "detalhar":
                JOptionPane.showMessageDialog(dialog, "Visualização de detalhes.");
                break;
            default:
                JOptionPane.showMessageDialog(dialog, "Modo desconhecido: " + modo);
        }
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}