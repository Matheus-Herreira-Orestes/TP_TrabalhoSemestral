package src.erp;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

public class GerenciarUsuario {
    JTextField[] campos = new JTextField[6];
    JPanel camposPanel;

    public void mostrar() {
        JDialog modal = new JDialog();
        modal.setTitle("Gerenciar Usuário");
        modal.setModal(true);
        modal.setSize(500, 500);
        modal.setLocationRelativeTo(null);
        modal.setLayout(new BorderLayout(20, 10));

        JPanel filtrosPanel = new JPanel(new BorderLayout());
        filtrosPanel.setBorder(BorderFactory.createTitledBorder("Filtro"));

        // Painel com campos
        JPanel camposFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField campoNome = new JTextField(15);
        JTextField campoCpfCnpj = new JTextField(15);
        camposFiltro.add(new JLabel("Nome:"));
        camposFiltro.add(campoNome);
        camposFiltro.add(new JLabel("CPF/CNPJ:"));
        camposFiltro.add(campoCpfCnpj);

        // Painel com botões
        JPanel botoesFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpar = new JButton("Limpar Filtro");
        botoesFiltro.add(btnBuscar);
        botoesFiltro.add(btnLimpar);

        // Monta tudo no painel de filtros
        filtrosPanel.add(camposFiltro, BorderLayout.NORTH);
        filtrosPanel.add(botoesFiltro, BorderLayout.SOUTH);

        // Campos de texto principais
        JPanel camposPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        camposPanel.setBorder(BorderFactory.createTitledBorder("Dados do Usuário"));

        String[] labels = {"Usuario 1", "Usuario 2", "Usuario 3", "Usuario 4", "Usuario 5", "Usuario 6"};

        for (int i = 0; i < 6; i++) {
            campos[i] = new JTextField(20);
            camposPanel.add(new JLabel(labels[i] + ":"));
            camposPanel.add(campos[i]);
        }

        // Botões de ação
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnInserir = new JButton("Inserir");
        JButton btnAlterar = new JButton("Alterar");
        JButton btnExcluir = new JButton("Excluir");

        botoesPanel.add(btnInserir);
        botoesPanel.add(btnAlterar);
        botoesPanel.add(btnExcluir);

        // Ações básicas de exemplo
        btnBuscar.addActionListener(e ->{
            pesquisaUsuario(campoNome, campoCpfCnpj);
        });

        btnLimpar.addActionListener(e -> {
            campoNome.setText("");
            campoCpfCnpj.setText("");
        });

        btnInserir.addActionListener(e ->{
            AlterarUsuario inserir = new AlterarUsuario(1);
            inserir.mostrar();
        });

        modal.add(filtrosPanel, BorderLayout.NORTH);
        modal.add(camposPanel, BorderLayout.CENTER);
        modal.add(botoesPanel, BorderLayout.SOUTH);

        modal.setVisible(true);
    }

    private void pesquisaUsuario(JTextField nome, JTextField cpf){
        String cpfTexto = cpf.getText();
        String nomeTexto = nome.getText();
        
        try(Connection conn = Conexao.conectar()){
            String sql = "SELECT nome, cpf FROM usuario WHERE cpf = ? AND nome LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, cpfTexto);
            stmt.setString(2, "%" + nomeTexto + "%");

            ResultSet ret = stmt.executeQuery();
            
            if (ret.next()) {
                for (int i = 0; i < campos.length; i++) {
                    String valor = null;
                    try {
                        valor = ret.getString(i + 1);
                    } catch (SQLException e) {
                        // Se não existir essa coluna, sai do loop
                        break;
                    }
                    if (valor == null || valor.isEmpty()) {
                        break;
                    }
                    campos[i].setText(valor);
                }
            }


        }catch(SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro na busca: " + e.getMessage());
        }
    }

    


}
