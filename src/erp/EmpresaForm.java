package src.erp;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class EmpresaForm {
    private JDialog dialog;
    private JTextField tfRazao, tfCnpj, tfTelefone, tfEndereco, tfBairro, tfCidade;
    private JComboBox<String> cbEstado;
    private String modo;
    private Empresa empresa;

    private static final String[] ESTADOS = {
        "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES",
        "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR",
        "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC",
        "SP", "SE", "TO"
    };

    public EmpresaForm(Window parent, String modo, Empresa empresa) {
        this.modo = modo.toLowerCase();
        this.empresa = empresa;
        dialog = new JDialog(parent, capitalize(modo) + " Empresa", Dialog.ModalityType.APPLICATION_MODAL);
        criarTela();
    }

    private void criarTela() {
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Insets labelInsets = new Insets(5, 10, 2, 10); 
        Insets fieldInsets = new Insets(0, 10, 10, 10);

        int linha = 0;

        gbc.gridx = 0; gbc.gridy = linha++;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = labelInsets;
        formPanel.add(new JLabel("Razão Social:"), gbc);

        gbc.gridy = linha++;
        gbc.insets = fieldInsets;
        tfRazao = new JTextField();
        formPanel.add(tfRazao, gbc);

        gbc.gridy = linha++;
        gbc.gridx = 0; gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.insets = labelInsets;
        formPanel.add(new JLabel("CNPJ:"), gbc);

        gbc.gridx = 1;
        formPanel.add(new JLabel("Telefone:"), gbc);

        gbc.gridy = linha++;
        gbc.gridx = 0;
        gbc.insets = fieldInsets;
        tfCnpj = new JTextField();
        formPanel.add(tfCnpj, gbc);

        gbc.gridx = 1;
        tfTelefone = new JTextField();
        formPanel.add(tfTelefone, gbc);

        gbc.gridy = linha++;
        gbc.gridx = 0;
        gbc.insets = labelInsets;
        formPanel.add(new JLabel("Endereço:"), gbc);

        gbc.gridx = 1;
        formPanel.add(new JLabel("Bairro:"), gbc);

        gbc.gridy = linha++;
        gbc.gridx = 0;
        gbc.insets = fieldInsets;
        tfEndereco = new JTextField();
        formPanel.add(tfEndereco, gbc);

        gbc.gridx = 1;
        tfBairro = new JTextField();
        formPanel.add(tfBairro, gbc);

        gbc.gridy = linha++;
        gbc.gridx = 0;
        gbc.insets = labelInsets;
        formPanel.add(new JLabel("Cidade:"), gbc);

        gbc.gridx = 1;
        formPanel.add(new JLabel("Estado:"), gbc);

        gbc.gridy = linha++;
        gbc.gridx = 0;
        gbc.insets = fieldInsets;
        tfCidade = new JTextField();
        formPanel.add(tfCidade, gbc);

        gbc.gridx = 1;
        cbEstado = new JComboBox<>(ESTADOS);
        formPanel.add(cbEstado, gbc);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnVoltar = new JButton("Voltar");
        JButton btnFuncao = new JButton(capitalize(modo));

        btnVoltar.addActionListener(e -> dialog.dispose());
        btnFuncao.addActionListener(this::executarAcao);

        botoesPanel.add(btnVoltar);
        botoesPanel.add(btnFuncao);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(botoesPanel, BorderLayout.SOUTH);

        if (!modo.equals("inserir") && empresa != null) {
            preencherCampos();
            if(modo.equals("detalhar")){
                desabilitarCampos();
                btnFuncao.setVisible(false);
            }
            if (modo.equals("excluir")) {
                desabilitarCampos();
            }
        }

        dialog.setVisible(true);
    }

    private void preencherCampos() {
        tfRazao.setText(empresa.getRazao());
        tfCnpj.setText(empresa.getCnpj());
        tfTelefone.setText(empresa.getTelefone());
        tfEndereco.setText(empresa.getEndereco());
        tfBairro.setText(empresa.getBairro());
        tfCidade.setText(empresa.getCidade());
        cbEstado.setSelectedItem(empresa.getEstado());
    }

    private void desabilitarCampos() {
        tfRazao.setEditable(false);
        tfCnpj.setEditable(false);
        tfTelefone.setEditable(false);
        tfEndereco.setEditable(false);
        tfBairro.setEditable(false);
        tfCidade.setEditable(false);
        cbEstado.setEnabled(false);
    }

    private void executarAcao(ActionEvent e) {
        Empresa emp = new Empresa(
            empresa != null ? empresa.getId() : 0,
            tfRazao.getText().trim(),
            tfCnpj.getText().trim(),
            tfTelefone.getText().trim(),
            tfEndereco.getText().trim(),
            tfBairro.getText().trim(),
            tfCidade.getText().trim(),
            cbEstado.getSelectedItem().toString()
        );

        switch (modo) {
            case "inserir" -> EmpresaDAO.inserir(emp);
            case "alterar" -> EmpresaDAO.atualizar(emp);
            case "excluir" -> EmpresaDAO.excluir(emp.getId());
        }

        dialog.dispose();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
