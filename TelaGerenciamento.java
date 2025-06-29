import javax.swing.*;
import java.awt.*;

public class TelaGerenciamento {

    public void mostrar(String tipo) {
        JFrame frame = new JFrame("Gerenciar " + tipo);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel label = new JLabel("Gerenciando " + tipo, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.NORTH);

        JTextArea area = new JTextArea("Aqui você pode gerenciar os " + tipo.toLowerCase() + ".");
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        panel.add(area, BorderLayout.CENTER);

        JButton voltar = new JButton("Voltar");
        voltar.addActionListener(e -> {
            frame.dispose(); // fecha esta tela
            new LandingPage().mostrar(); // volta para a tela inicial
        });
        panel.add(voltar, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }
}
