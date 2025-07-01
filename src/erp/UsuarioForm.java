package src.erp;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class UsuarioForm {
    private JDialog dialog;
    private JTextField tfNome, tfCpf;
    private JPasswordField tfSenha;
    private JRadioButton rbPadrao, rbAdmin;
    private ButtonGroup grupoPerfil;
    private String modo;
    private Usuario usuarioSelecionado;

    public UsuarioForm(Window parent, String modo, Usuario usuarioSelecionado) {
        this.modo = modo.toLowerCase();
        this.usuarioSelecionado = usuarioSelecionado;
        dialog = new JDialog(parent, capitalizar(modo) + " Usuário", Dialog.ModalityType.APPLICATION_MODAL);
        criarTela();
    }

    private void criarTela() {
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        Insets labelInsets = new Insets(6, 6, 2, 6);
        Insets fieldInsets = new Insets(0, 6, 10, 6);

        gbc.insets = labelInsets;
        painelCampos.add(new JLabel("Nome:"), gbc);
        gbc.gridy++;
        gbc.insets = fieldInsets;
        tfNome = new JTextField(25);
        painelCampos.add(tfNome, gbc);

        gbc.gridy++;
        gbc.insets = labelInsets;
        painelCampos.add(new JLabel("CPF:"), gbc);
        gbc.gridy++;
        gbc.insets = fieldInsets;
        tfCpf = new JTextField(25);
        painelCampos.add(tfCpf, gbc);

        gbc.gridy++;
        gbc.insets = labelInsets;
        painelCampos.add(new JLabel("Senha:"), gbc);
        gbc.gridy++;
        gbc.insets = fieldInsets;
        tfSenha = new JPasswordField(25);
        painelCampos.add(tfSenha, gbc);

        gbc.gridy++;
        gbc.insets = labelInsets;
        painelCampos.add(new JLabel("Perfil:"), gbc);

        gbc.gridy++;
        gbc.insets = fieldInsets;
        rbPadrao = new JRadioButton("Padrão");
        grupoPerfil = new ButtonGroup();
        grupoPerfil.add(rbPadrao);
        painelCampos.add(rbPadrao, gbc);

        gbc.gridy++;
        rbAdmin = new JRadioButton("Administrador");
        grupoPerfil.add(rbAdmin);
        painelCampos.add(rbAdmin, gbc);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnVoltar = new JButton("Voltar");
        JButton btnConfirmar = new JButton(capitalizar(modo));

        btnVoltar.addActionListener(e -> dialog.dispose());
        btnConfirmar.addActionListener(this::executarAcao);

        painelBotoes.add(btnVoltar);
        painelBotoes.add(btnConfirmar);

        dialog.add(painelCampos, BorderLayout.CENTER);
        dialog.add(painelBotoes, BorderLayout.SOUTH);

        if (!modo.equals("inserir") && usuarioSelecionado != null) {
            preencherCampos();
            if (modo.equals("detalhar")) {
                desabilitarEdicao();
                btnConfirmar.setVisible(false);
            }
        }

        dialog.setVisible(true);
    }

    private void preencherCampos() {
        tfNome.setText(usuarioSelecionado.getNome());
        tfCpf.setText(usuarioSelecionado.getCpf());
        tfSenha.setText(usuarioSelecionado.getSenha());

        if ("admin".equalsIgnoreCase(usuarioSelecionado.getPerfil())) {
            rbAdmin.setSelected(true);
        } else {
            rbPadrao.setSelected(true);
        }
    }

    private void desabilitarEdicao() {
        tfNome.setEditable(false);
        tfCpf.setEditable(false);
        tfSenha.setEditable(false);
        rbAdmin.setEnabled(false);
        rbPadrao.setEnabled(false);
    }

    private void executarAcao(ActionEvent e) {
        String nome = tfNome.getText().trim();
        String cpf = tfCpf.getText().trim();
        String senha = new String(tfSenha.getPassword());
        String perfil = rbAdmin.isSelected() ? "ADMIN" : "FISCAL";

        if (nome.isEmpty() || cpf.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Preencha todos os campos.");
            return;
        }

        try {
            switch (modo) {
                case "inserir" -> {
                    Usuario novo = new Usuario(0, nome, cpf, senha, perfil);
                    UsuarioDAO.inserir(novo);
                    JOptionPane.showMessageDialog(dialog, "Usuário inserido com sucesso!");
                    dialog.dispose();
                }
                case "alterar" -> {
                    usuarioSelecionado.setNome(nome);
                    usuarioSelecionado.setCpf(cpf);
                    usuarioSelecionado.setSenha(senha);
                    usuarioSelecionado.setPerfil(perfil);
                    UsuarioDAO.atualizar(usuarioSelecionado);
                    JOptionPane.showMessageDialog(dialog, "Usuário alterado com sucesso!");
                    dialog.dispose();
                }
                case "excluir" -> {
                    int opcao = JOptionPane.showConfirmDialog(dialog, "Deseja realmente excluir este usuário?", "Confirmação", JOptionPane.YES_NO_OPTION);
                    if (opcao == JOptionPane.YES_OPTION) {
                        UsuarioDAO.excluir(usuarioSelecionado.getId());
                        JOptionPane.showMessageDialog(dialog, "Usuário excluído com sucesso!");
                        dialog.dispose();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Erro ao executar ação: " + ex.getMessage());
        }
    }

    private String capitalizar(String texto) {
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
}
