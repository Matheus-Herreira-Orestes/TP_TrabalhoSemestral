package src.erp;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GerenciarUsuarioEmpresa {
    private String tipo; // "usuario" ou "empresa"
    private JTextField tfNomeOuRazao;
    private JTextField tfCpfCnpj;
    private JTable tabela;
    private DefaultTableModel tableModel;
    

    public GerenciarUsuarioEmpresa(String tipo) {
        this.tipo = tipo;
    }

    public void mostrar() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Gerenciar " + (tipo.equals("usuario") ? "Usuários" : "Empresas"));
        dialog.setSize(800, 500);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel filtrosPanel = new JPanel(new GridBagLayout());
        filtrosPanel.setBorder(BorderFactory.createTitledBorder("Filtro"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        int linha = 0;

        gbc.gridy = linha++;
        gbc.insets = new Insets(5, 10, 1, 10);
        filtrosPanel.add(new JLabel(tipo.equals("usuario") ? "Nome:" : "Razão Social:"), gbc);

        gbc.gridy = linha++;
        gbc.insets = new Insets(1, 10, 10, 10);
        tfNomeOuRazao = new JTextField();
        filtrosPanel.add(tfNomeOuRazao, gbc);

        gbc.gridy = linha++;
        gbc.insets = new Insets(5, 10, 1, 10);
        filtrosPanel.add(new JLabel(tipo.equals("usuario") ? "CPF:" : "CNPJ:"), gbc);

        gbc.gridy = linha++;
        gbc.insets = new Insets(1, 10, 10, 10);
        tfCpfCnpj = new JTextField();
        filtrosPanel.add(tfCpfCnpj, gbc);

        JPanel botoesPanelFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnLimpar = new JButton("Limpar");
        btnFiltrar.addActionListener(e -> filtrar());
        btnLimpar.addActionListener(e -> limpar());
        botoesPanelFiltro.add(btnLimpar);
        botoesPanelFiltro.add(btnFiltrar);

        gbc.gridy = linha++;
        gbc.anchor = GridBagConstraints.EAST;
        filtrosPanel.add(botoesPanelFiltro, gbc);

        dialog.add(filtrosPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabela);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnVoltar = new JButton("Voltar");
        JButton btnInserir = new JButton("Inserir");
        JButton btnAlterar = new JButton("Alterar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnDetalhar = new JButton("Detalhar");

        btnVoltar.addActionListener(e -> dialog.dispose());
        btnInserir.addActionListener(e -> inserir());
        btnAlterar.addActionListener(e -> alterar());
        btnExcluir.addActionListener(e -> excluir());
        btnDetalhar.addActionListener(e -> detalhar());

        botoesPanel.add(btnVoltar);
        botoesPanel.add(btnInserir);
        botoesPanel.add(btnAlterar);
        botoesPanel.add(btnExcluir);
        botoesPanel.add(btnDetalhar);

        dialog.add(botoesPanel, BorderLayout.SOUTH);

        carregarTodos();

        dialog.setVisible(true);
    }

    private void carregarTodos() {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (tipo.equals("usuario")) {
            tableModel.setColumnIdentifiers(new String[]{"ID", "Nome", "CPF"});
            for (Usuario u : UsuarioDAO.buscarTodos()) {
                tableModel.addRow(new Object[]{u.getId(), u.getNome(), u.getCpf()});
            }
        } else {
            tableModel.setColumnIdentifiers(new String[]{"ID", "Razão Social", "CNPJ"});
            for (Empresa e : EmpresaDAO.buscarTodos()) {
                tableModel.addRow(new Object[]{e.getId(), e.getRazao(), e.getCnpj()});
            }
        }
    }

    private void filtrar() {
        String nomeRazao = tfNomeOuRazao.getText().trim();
        String cpfCnpj = tfCpfCnpj.getText().trim();

        tableModel.setRowCount(0);

        if (tipo.equals("usuario")) {
            List<Usuario> resultados = UsuarioDAO.buscarPorFiltros(nomeRazao, cpfCnpj);
            for (Usuario u : resultados) {
                tableModel.addRow(new Object[]{u.getId(), u.getNome(), u.getCpf()});
            }
        } else {
            List<Empresa> resultados = EmpresaDAO.buscarPorFiltros(nomeRazao, cpfCnpj);
            for (Empresa e : resultados) {
                tableModel.addRow(new Object[]{e.getId(), e.getRazao(), e.getCnpj()});
            }
        }
    }

    private void limpar() {
        tfNomeOuRazao.setText("");
        tfCpfCnpj.setText("");
        carregarTodos();
    }

    private void inserir() {
        if (tipo.equals("usuario")) {
            new UsuarioForm(null, "inserir", null);
        } else {
            new EmpresaForm(null, "inserir", null);
        }
        carregarTodos();
    }

    private void alterar() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(null, "Selecione uma linha para alterar.");
            return;
        }
        int id = (int) tabela.getValueAt(linha, 0);

        if (tipo.equals("usuario")) {
            Usuario u = UsuarioDAO.buscarPorId(id);
            new UsuarioForm(null, "alterar", u);
        } else {
            Empresa e = EmpresaDAO.buscarPorId(id);
            new EmpresaForm(null, "alterar", e);
        }
        carregarTodos();
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(null, "Selecione uma linha para excluir.");
            return;
        }
        int id = (int) tabela.getValueAt(linha, 0);
        int opcao = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (opcao != JOptionPane.YES_OPTION) return;

        if (tipo.equals("usuario")) {
            UsuarioDAO.excluir(id);
        } else {
            EmpresaDAO.excluir(id);
        }
        carregarTodos();
    }

    private void detalhar() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(null, "Selecione uma linha.");
            return;
        }
        int id = (int) tabela.getValueAt(linha, 0);

        if (tipo.equals("usuario")) {
            Usuario u = UsuarioDAO.buscarPorId(id);
            new UsuarioForm(null, "detalhar", u);
        } else {
            Empresa e = EmpresaDAO.buscarPorId(id);
            new EmpresaForm(null, "detalhar", e);
        }
        carregarTodos();
    }
}
