import javax.swing.*;
import java.awt.*;

// PLACEHOLDER DA LANDING PAGe
public class LandingPage {
    public void mostrar() {
        JFrame frame = new JFrame("Tela Principal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JLabel label = new JLabel("Bem-vindo à tela principal!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(label);

        frame.setVisible(true);
    }
}