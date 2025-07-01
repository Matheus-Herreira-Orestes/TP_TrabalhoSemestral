package src.erp;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

public class AditamentoForm {
    private JDialog dialog;
    private JTextField txtIdAditamento;
    private JTextArea txtObservacao;
    private JTextField txtNovoValor;
    private JFormattedTextField txtNovaDtInicio;
    private JFormattedTextField txtNovaDtFim;

    private String modo; // inserir, alterar, excluir, detalhar
    private Aditamento aditamentoSelecionado;
    private int idContrato;

    public AditamentoForm(Window parent, String modo, Aditamento aditamentoSelecionado, int idContrato) {
        this.modo = modo.toLowerCase();
        this.aditamentoSelecionado = aditamentoSelecionado;
        this.idContrato = idContrato;
        dialog = new JDialog(parent, capitalize(modo) + " Aditamento", Dialog.ModalityType.APPLICATION_MODAL);
        criarTela();
    }

    private void criarTela() {
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int linha = 0;

        JLabel lblTitulo = new JLabel(capitalize(modo) + " Aditamento", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = linha++;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;

        if (!modo.equals("inserir")) {
            txtIdAditamento = new JTextField(10);
            txtIdAditamento.setEnabled(false);
        }

        gbc.gridx = 0;
        gbc.gridy = linha;
        panel.add(new JLabel("Novo Valor:"), gbc);

        txtNovoValor = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtNovoValor, gbc);
        linha++;

        gbc.gridx = 0;
        gbc.gridy = linha;
        panel.add(new JLabel("Nova Data Início:"), gbc);

        txtNovaDtInicio = criarCampoDataFormatado();
        gbc.gridx = 1;
        panel.add(txtNovaDtInicio, gbc);
        linha++;

        gbc.gridx = 0;
        gbc.gridy = linha;
        panel.add(new JLabel("Nova Data Fim:"), gbc);

        txtNovaDtFim = criarCampoDataFormatado();
        gbc.gridx = 1;
        panel.add(txtNovaDtFim, gbc);
        linha++;

        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.anchor = GridBagConstraints.NORTH;
        panel.add(new JLabel("Observação:"), gbc);

        txtObservacao = new JTextArea(5, 20);
        txtObservacao.setLineWrap(true);
        txtObservacao.setWrapStyleWord(true);
        JScrollPane scrollObs = new JScrollPane(txtObservacao);
        gbc.gridx = 1;
        panel.add(scrollObs, gbc);
        linha++;

        gbc.gridy = linha++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnConfirmar = new JButton(capitalize(modo));

        btnCancelar.addActionListener(e -> dialog.dispose());
        btnConfirmar.addActionListener(this::executarAcao);

        botoes.add(btnCancelar);
        botoes.add(btnConfirmar);

        panel.add(botoes, gbc);

        dialog.add(panel);

        if (!modo.equals("inserir") && aditamentoSelecionado != null) {
            preencherCamposComAditamento();
            if (modo.equals("detalhar")) {
                desabilitarEdicao();
                btnConfirmar.setVisible(false);
            }
            if (modo.equals("excluir"))
            {
                desabilitarEdicao();
            }
        }

        dialog.setVisible(true);
    }

    private void executarAcao(ActionEvent e) {
        try (var conn = Conexao.conectar()) {
            String observacao = txtObservacao.getText().trim();

            BigDecimal novoValor = null;
            if (!txtNovoValor.getText().trim().isEmpty()) {
                novoValor = new BigDecimal(txtNovoValor.getText().replace(",", "."));
            }

            Date novaDtInicio = parseData(txtNovaDtInicio.getText());
            Date novaDtFim = parseData(txtNovaDtFim.getText());

            switch (modo) {
                case "inserir" -> {
                    String sql = "INSERT INTO aditamento (id_contrato, observacoes, novo_valor, novo_dt_inicio, novo_dt_fim) VALUES (?, ?, ?, ?, ?)";
                    try (var stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, idContrato);
                        stmt.setString(2, observacao);
                        if (novoValor != null)
                            stmt.setBigDecimal(3, novoValor);
                        else
                            stmt.setNull(3, java.sql.Types.DECIMAL);

                        if (novaDtInicio != null)
                            stmt.setDate(4, novaDtInicio);
                        else
                            stmt.setNull(4, java.sql.Types.DATE);

                        if (novaDtFim != null)
                            stmt.setDate(5, novaDtFim);
                        else
                            stmt.setNull(5, java.sql.Types.DATE);

                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(dialog, "Aditamento inserido com sucesso!");
                        dialog.dispose();
                    }
                }
                case "alterar" -> {
                    int id = Integer.parseInt(txtIdAditamento.getText());
                    String sql = "UPDATE aditamento SET observacoes = ?, novo_valor = ?, novo_dt_inicio = ?, novo_dt_fim = ? WHERE id_aditamento = ?";
                    try (var stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, observacao);

                        if (novoValor != null)
                            stmt.setBigDecimal(2, novoValor);
                        else
                            stmt.setNull(2, java.sql.Types.DECIMAL);

                        if (novaDtInicio != null)
                            stmt.setDate(3, novaDtInicio);
                        else
                            stmt.setNull(3, java.sql.Types.DATE);

                        if (novaDtFim != null)
                            stmt.setDate(4, novaDtFim);
                        else
                            stmt.setNull(4, java.sql.Types.DATE);

                        stmt.setInt(5, id);

                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(dialog, "Aditamento alterado com sucesso!");
                        dialog.dispose();
                    }
                }
                case "excluir" -> {
                    int id = Integer.parseInt(txtIdAditamento.getText());
                    int opcao = JOptionPane.showConfirmDialog(dialog, "Deseja realmente excluir este aditamento?", "Confirmação", JOptionPane.YES_NO_OPTION);
                    if (opcao == JOptionPane.YES_OPTION) {
                        String sql = "DELETE FROM aditamento WHERE id_aditamento = ?";
                        try (var stmt = conn.prepareStatement(sql)) {
                            stmt.setInt(1, id);
                            stmt.executeUpdate();
                            JOptionPane.showMessageDialog(dialog, "Aditamento excluído com sucesso!");
                            dialog.dispose();
                        }
                    }
                }
                case "detalhar" -> {
                    dialog.dispose();
                }
                default -> JOptionPane.showMessageDialog(dialog, "Modo desconhecido: " + modo);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Erro: " + ex.getMessage());
        }
    }

    private void preencherCamposComAditamento() {
        txtIdAditamento.setText(String.valueOf(aditamentoSelecionado.id));
        txtObservacao.setText(aditamentoSelecionado.observacoes != null ? aditamentoSelecionado.observacoes : "");
        txtNovoValor.setText(aditamentoSelecionado.novoValor != null ? aditamentoSelecionado.novoValor.toString() : "");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtNovaDtInicio.setText(aditamentoSelecionado.novoDtInicio != null ? sdf.format(aditamentoSelecionado.novoDtInicio) : "");
        txtNovaDtFim.setText(aditamentoSelecionado.novoDtFim != null ? sdf.format(aditamentoSelecionado.novoDtFim) : "");
    }

    private void desabilitarEdicao() {
        if (txtObservacao != null) txtObservacao.setEditable(false);
        if (txtNovoValor != null) txtNovoValor.setEditable(false);
        if (txtNovaDtInicio != null) txtNovaDtInicio.setEditable(false);
        if (txtNovaDtFim != null) txtNovaDtFim.setEditable(false);
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
                JOptionPane.showMessageDialog(campo, "Data inválida!\nUse o formato dd/MM/yyyy", "Erro", JOptionPane.ERROR_MESSAGE);
                campo.requestFocus();
            }
        }
    }

    private Date parseData(String texto) throws ParseException {
        if (texto == null || texto.isBlank() || texto.contains("_")) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date utilDate = sdf.parse(texto);
        return new Date(utilDate.getTime());
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}