package src.erp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class AlterarUsuario {
    private int inserir;
    public AlterarUsuario(int inserir){
        this.inserir = inserir;
    }
    
    public void mostrar() {
        JFrame frame = new JFrame("Inserir Usuário");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        // Painel principal com GridLayout para labels e campos
        JPanel camposPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        camposPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel labelNome = new JLabel("Nome:");
        JTextField campoNome = new JTextField();

        JLabel labelCpf = new JLabel("CPF:");
        JTextField campoCpf = new JTextField();

        JLabel labelSenha = new JLabel("Senha:");
        JPasswordField campoSenha = new JPasswordField();

        JLabel labelPerfil = new JLabel("Perfil:");

        // Painel para os checkboxes de perfil
        JPanel painelPerfil = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox cbPadrao = new JCheckBox("Padrão");
        JCheckBox cbAdmin = new JCheckBox("Administrador");

        // Grupo para deixar os checkboxes mutuamente exclusivos
        ButtonGroup grupoPerfil = new ButtonGroup();
        grupoPerfil.add(cbPadrao);
        grupoPerfil.add(cbAdmin);

        painelPerfil.add(cbPadrao);
        painelPerfil.add(cbAdmin);

        // Adiciona componentes ao painel principal
        camposPanel.add(labelNome);
        camposPanel.add(campoNome);
        camposPanel.add(labelCpf);
        camposPanel.add(campoCpf);
        camposPanel.add(labelSenha);
        camposPanel.add(campoSenha);
        camposPanel.add(labelPerfil);
        camposPanel.add(painelPerfil);

        frame.add(camposPanel, BorderLayout.CENTER);

        // Painel dos botões Salvar e Voltar
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnVoltar = new JButton("Voltar");

        botoesPanel.add(btnSalvar);
        botoesPanel.add(btnVoltar);

        frame.add(botoesPanel, BorderLayout.SOUTH);

        btnVoltar.addActionListener(e -> frame.dispose());

        btnSalvar.addActionListener(e -> {
            String nome = campoNome.getText().trim();
            String cpf = campoCpf.getText().trim();
            String senha = new String(campoSenha.getPassword());
            String perfil = null;
            if (cbPadrao.isSelected()) perfil = "PADRAO";
            else if (cbAdmin.isSelected()) perfil = "ADMIN";

            campoNome.setText("");
            campoCpf.setText("");
            campoSenha.setText("");
            grupoPerfil.clearSelection();
        });

        frame.setVisible(true);
    }
}
