package src.erp;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

public class ContratoForm {
    private JDialog dialog;
    private JComboBox<Empresa> cbEmpresa;
    private JTextField txtIdContrato, txtValor;
    private JFormattedTextField txtDtInicio, txtDtFim;
    private JTextArea txtDescricao;
    private String modo; // "inserir", "alterar", "excluir", "detalhar"
    private Contrato contratoSelecionado;
    private JComboBox<Usuario> cbFiscal;

    public ContratoForm(JFrame parent, String modo, Contrato contratoSelecionado) {
        this.modo = modo.toLowerCase();
        this.contratoSelecionado = contratoSelecionado;
        dialog = new JDialog(parent, capitalize(modo) + " Contrato", true);
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
        int linha = 1;

        JLabel lblTitulo = new JLabel(capitalize(modo) + " Contrato", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = linha;
        panel.add(new JLabel("Empresa:"), gbc);

        cbEmpresa = new JComboBox<>();
        carregarEmpresas();
        gbc.gridx = 1;
        gbc.gridy = linha++;
        gbc.gridwidth = 3;
        panel.add(cbEmpresa, gbc);

        gbc.gridwidth = 1;

        if (Sessao.isAdmin) {
            gbc.gridx = 0;
            gbc.gridy = linha++;
            panel.add(new JLabel("Fiscal responsável:"), gbc);

            cbFiscal = new JComboBox<>();
            carregarUsuariosFiscais();

            gbc.gridx = 1;
            gbc.gridwidth = 3;
            panel.add(cbFiscal, gbc);
            gbc.gridwidth = 1;
        }

        gbc.gridx = 0;
        gbc.gridy = linha;
        panel.add(new JLabel("Id do contrato:"), gbc);

        txtIdContrato = new JTextField(15);
        txtIdContrato.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = linha;
        panel.add(txtIdContrato, gbc);

        gbc.gridx = 2;
        gbc.gridy = linha;
        panel.add(new JLabel("Valor Total:"), gbc);

        txtValor = new JTextField(15);
        gbc.gridx = 3;
        gbc.gridy = linha++;
        panel.add(txtValor, gbc);

        gbc.gridx = 0;
        gbc.gridy = linha;
        panel.add(new JLabel("Data início:"), gbc);

        txtDtInicio = criarCampoDataFormatado();
        gbc.gridx = 1;
        gbc.gridy = linha;
        panel.add(txtDtInicio, gbc);

        gbc.gridx = 2;
        gbc.gridy = linha;
        panel.add(new JLabel("Data fim:"), gbc);

        txtDtFim = criarCampoDataFormatado();
        gbc.gridx = 3;
        gbc.gridy = linha++;
        panel.add(txtDtFim, gbc);

        gbc.gridx = 0;
        gbc.gridy = linha++;
        panel.add(new JLabel("Descrição:"), gbc);

        txtDescricao = new JTextArea(5, 30);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        JScrollPane scrollDescricao = new JScrollPane(txtDescricao);
        gbc.gridx = 0;
        gbc.gridy = linha++;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(scrollDescricao, gbc);

        gbc.gridy = linha;
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
        if (!modo.equals("inserir") && contratoSelecionado != null) {
            preencherCamposComContrato();
            if (modo.equals("detalhar")) {
                desabilitarEdicao();
                btnAcao.setVisible(false);
            }
        }

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
        if (empresaSelecionada == null) {
            JOptionPane.showMessageDialog(dialog, "Selecione uma empresa válida.");
            return;
        }

        try (Connection conn = Conexao.conectar()) {
            String descricao = txtDescricao.getText().trim();
            BigDecimal valor = new BigDecimal(txtValor.getText().replace(",", "."));
            Date dtInicio = parseData(txtDtInicio.getText());
            Date dtFim = parseData(txtDtFim.getText());
            int idEmpresa = empresaSelecionada.getId();

            switch (modo) {
                case "inserir" -> {
                    String sql = "INSERT INTO contrato (id_empresa, valor_contrato, dt_inicio, dt_fim, descricao, id_fiscal) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, idEmpresa);
                        stmt.setBigDecimal(2, valor);
                        stmt.setDate(3, dtInicio);
                        stmt.setDate(4, dtFim);
                        stmt.setString(5, descricao);
                        int idFiscal;
                        if (!Sessao.isAdmin) {
                            idFiscal = Sessao.idUsuario;
                        } else {
                            Usuario usuarioSelecionado = (Usuario) cbFiscal.getSelectedItem();
                            if (usuarioSelecionado == null) {
                                JOptionPane.showMessageDialog(dialog, "Selecione um fiscal válido.");
                                return;
                            }
                            idFiscal = usuarioSelecionado.getId();
                        }
                        stmt.setInt(6, idFiscal);
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(dialog, "Contrato inserido com sucesso!");
                        dialog.dispose();
                    }
                }
                case "alterar" -> {
                    int idContrato = Integer.parseInt(txtIdContrato.getText());

                    String sql;
                    if (Sessao.isAdmin) {
                        sql = "UPDATE contrato SET id_empresa = ?, valor_contrato = ?, dt_inicio = ?, dt_fim = ?, descricao = ?, id_fiscal = ? WHERE id_contrato = ?";
                    } else {
                        sql = "UPDATE contrato SET id_empresa = ?, valor_contrato = ?, dt_inicio = ?, dt_fim = ?, descricao = ? WHERE id_contrato = ?";
                    }

                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, idEmpresa);
                        stmt.setBigDecimal(2, valor);
                        stmt.setDate(3, dtInicio);
                        stmt.setDate(4, dtFim);
                        stmt.setString(5, descricao);

                        if (Sessao.isAdmin) {
                            Usuario usuarioSelecionado = (Usuario) cbFiscal.getSelectedItem();
                            if (usuarioSelecionado == null) {
                                JOptionPane.showMessageDialog(dialog, "Selecione um fiscal válido.");
                                return;
                            }
                            stmt.setInt(6, usuarioSelecionado.getId());
                            stmt.setInt(7, idContrato);
                        } else {
                            stmt.setInt(6, idContrato);
                        }

                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(dialog, "Contrato alterado com sucesso!");
                        dialog.dispose();
                    }
                }

                case "excluir" -> {
                    int idContrato = Integer.parseInt(txtIdContrato.getText());
                    int opcao = JOptionPane.showConfirmDialog(dialog, "Deseja realmente excluir o contrato?", "Confirmação", JOptionPane.YES_NO_OPTION);
                    if (opcao == JOptionPane.YES_OPTION) {
                        String sql = "DELETE FROM contrato WHERE id_contrato = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setInt(1, idContrato);
                            stmt.executeUpdate();
                            JOptionPane.showMessageDialog(dialog, "Contrato excluído com sucesso!");
                            dialog.dispose();
                        }
                    }
                }
                case "detalhar" -> {
                    dialog.dispose();
                }
                default -> {
                    JOptionPane.showMessageDialog(dialog, "Modo desconhecido: " + modo);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Erro: " + ex.getMessage());
        }
    }


    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private JFormattedTextField criarCampoDataFormatado() {
        try {
            MaskFormatter mask = new MaskFormatter("##/##/####");
            mask.setPlaceholderCharacter('_');
            mask.setValidCharacters("0123456789");
            
            JFormattedTextField campo = new JFormattedTextField(mask);
            campo.setColumns(10);
            campo.setToolTipText("dd/MM/yyyy");
            
            campo.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    validarData(campo);
                }
            });
            
            return campo;
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao criar máscara de data");
            return new JFormattedTextField();
        }
    }

    private void validarData(JFormattedTextField campo) {
        String texto = campo.getText().replace("_", "").trim();
        if (!texto.isEmpty() && texto.length() == 10) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                java.util.Date data = sdf.parse(texto);
                
                campo.setValue(sdf.format(data));
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(campo, 
                    "Data inválida!\nUse o formato dd/MM/yyyy", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
                campo.requestFocus();
            }
        }
    }

    private void preencherCamposComContrato() {
        txtIdContrato.setText(String.valueOf(contratoSelecionado.id));
        txtDescricao.setText(contratoSelecionado.descricao);
        txtValor.setText("");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtDtInicio.setText(sdf.format(contratoSelecionado.dtInicio));
        txtDtFim.setText(sdf.format(contratoSelecionado.dtFim));

        for (int i = 0; i < cbEmpresa.getItemCount(); i++) {
            Empresa emp = cbEmpresa.getItemAt(i);
            if (emp.getRazao().equals(contratoSelecionado.empresa)) {
                cbEmpresa.setSelectedIndex(i);
                break;
            }
        }
        txtValor.setText(contratoSelecionado.valorContrato != null ? contratoSelecionado.valorContrato.toString() : "");
        if (Sessao.isAdmin && cbFiscal != null) {
            for (int i = 0; i < cbFiscal.getItemCount(); i++) {
                Usuario u = cbFiscal.getItemAt(i);
                if (u.getId() == contratoSelecionado.idFiscal) {
                    cbFiscal.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void desabilitarEdicao() {
        cbEmpresa.setEnabled(false);
        txtDescricao.setEditable(false);
        txtValor.setEditable(false);
        txtDtInicio.setEditable(false);
        txtDtFim.setEditable(false);
        if (cbFiscal != null) cbFiscal.setEnabled(false);
    }

    private Date parseData(String texto) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date utilDate = sdf.parse(texto);
        return new Date(utilDate.getTime());
    }

    private void carregarUsuariosFiscais() {
        for (Usuario u : UsuarioDAO.buscarTodos()) {
            cbFiscal.addItem(u);
        }
    }
}