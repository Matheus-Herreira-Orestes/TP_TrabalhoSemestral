package src.erp;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

public class BuscarContrato {
    private JTextField tfEmpresa, tfIdContrato, tfDescricao;
    private JFormattedTextField tfDtInicio, tfDtFim;
    private JTable tabela;
    private ContratoTableModel tableModel;

    public void mostrar() {
        JFrame frame = new JFrame("Buscar Contratos");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Filtros
        JPanel painelFiltros = new JPanel(new GridLayout(3, 4, 10, 10));

        tfEmpresa = new JTextField();
        tfIdContrato = new JTextField();
        tfDescricao = new JTextField();
        tfDtInicio = criarCampoDataFormatado();
        tfDtFim = criarCampoDataFormatado();

        painelFiltros.add(new JLabel("Empresa:"));
        painelFiltros.add(tfEmpresa);
        painelFiltros.add(new JLabel("Data início:"));
        painelFiltros.add(tfDtInicio);

        painelFiltros.add(new JLabel("ID Contrato:"));
        painelFiltros.add(tfIdContrato);
        painelFiltros.add(new JLabel("Data fim:"));
        painelFiltros.add(tfDtFim);

        painelFiltros.add(new JLabel("Descrição:"));
        painelFiltros.add(tfDescricao);

        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnLimpar = new JButton("Limpar filtro");

        btnFiltrar.addActionListener(e -> filtrar());
        btnLimpar.addActionListener(e -> limparFiltros());

        painelFiltros.add(btnLimpar);
        painelFiltros.add(btnFiltrar);

        painelPrincipal.add(painelFiltros, BorderLayout.NORTH);

        // Tabela
        tableModel = new ContratoTableModel(List.of());
        tabela = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabela);
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Botões CRUD
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnVoltar = new JButton("Voltar");
        botoesPanel.add(btnVoltar);
        JButton btnAditamentos = new JButton("Aditamentos");
        botoesPanel.add(btnAditamentos);
        JButton btnAlterar = new JButton("Alterar");
        botoesPanel.add(btnAlterar);
        JButton btnExcluir = new JButton("Excluir");
        botoesPanel.add(btnExcluir);
        JButton btnDetalhar = new JButton("Detalhar");
        botoesPanel.add(btnDetalhar);

        btnVoltar.addActionListener(e -> frame.dispose());

        btnAlterar.addActionListener(e -> abrirFormulario("alterar"));
        btnExcluir.addActionListener(e -> abrirFormulario("excluir"));
        btnDetalhar.addActionListener(e -> abrirFormulario("detalhar"));
        btnAditamentos.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(null, "Selecione um contrato.");
                return;
            }

            Contrato contrato = tableModel.getContratoAt(linha);
            Window janelaPai = SwingUtilities.getWindowAncestor(tabela);
            new GerenciarAditamentos().mostrar(janelaPai, contrato.id);
            carregarTodos();
        });


        
        painelPrincipal.add(botoesPanel, BorderLayout.SOUTH);

        frame.add(painelPrincipal);
        frame.setVisible(true);

        carregarTodos();
    }

    private void filtrar() {
        String empresa = tfEmpresa.getText().trim();
        String idStr = tfIdContrato.getText().trim();
        String descricao = tfDescricao.getText().trim();
        String dtInicio = tfDtInicio.getText().trim();
        String dtFim = tfDtFim.getText().trim();

        Integer id = idStr.isEmpty() ? null : Integer.parseInt(idStr);

        List<Contrato> resultados;
        
        resultados = ContratoDAO.buscarPorFiltros(empresa, id, descricao, dtInicio, dtFim);
        
        tableModel.setDados(resultados);
    }

    private void limparFiltros() {
        tfEmpresa.setText("");
        tfIdContrato.setText("");
        tfDescricao.setText("");
        tfDtInicio.setText("");
        tfDtFim.setText("");
        carregarTodos();
    }

    private void carregarTodos() {
        List<Contrato> todos;
        
        todos = ContratoDAO.buscarTodos();
        
        tableModel.setDados(todos);
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

    private void abrirFormulario(String modo) {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(null, "Selecione um contrato na tabela.");
            return;
        }

        Contrato contrato = tableModel.getContratoAt(linha);
        new ContratoForm((JFrame) SwingUtilities.getWindowAncestor(tabela), modo, contrato);
        carregarTodos();
    }
}
