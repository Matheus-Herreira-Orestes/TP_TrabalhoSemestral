import javax.swing.*;
import java.awt.*;

public class LoginPage {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().criarTelaLogin());
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

            //usuario de teste para ver comportamento da tela, retirar para integrar com o banco depois
            if (usuario.equals("admin") && senha.equals("123")) {
                frame.dispose(); // fecha a tela de login
                new LandingPage().mostrar(); // abre nova tela
            } else {
                JOptionPane.showMessageDialog(frame, "Login ou senha inválidos");
            }
        });
    }
}