package src.erp;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

public class LoginPage {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().criarTelaLogin());
    }

    public boolean validarLogin(String usuario, String senha) {
        String sql = "SELECT senha FROM usuario WHERE cpf = ?";

        try (Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String senhaBanco = rs.getString("senha");
                    return senha.equals(senhaBanco);
                } else {
                    return false;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco: " + e.getMessage());
            return false;
        }
    }


    public void criarTelaLogin() {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 220);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel userLabel = new JLabel("Login:");
        JTextField userText = new JTextField(15);
        userText.setMaximumSize(userText.getPreferredSize());
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passwordLabel = new JLabel("Senha:");
        JPasswordField passwordText = new JPasswordField(15);
        passwordText.setMaximumSize(passwordText.getPreferredSize());
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginButton = new JButton("Logar");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(userLabel);
        mainPanel.add(userText);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(passwordLabel);
        mainPanel.add(passwordText);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(loginButton);

        frame.add(mainPanel);
        frame.setVisible(true);

        loginButton.addActionListener(e -> {
            String usuario = userText.getText();
            String senha = new String(passwordText.getPassword());

            if (validarLogin(usuario, senha)) {
                frame.dispose(); // fecha a tela de login
                new LandingPage().mostrar();
            } else {
                JOptionPane.showMessageDialog(frame, "Login ou senha inválidos");
            }
        });
    }
}