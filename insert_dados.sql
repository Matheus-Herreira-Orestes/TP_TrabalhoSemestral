INSERT INTO usuario (nome, cpf, senha, perfil) VALUES
('Carlos Silva',     '12345678901', 'senha123', 'ADMIN'),
('Fernanda Souza',   '98765432100', 'senha456', 'FISCAL'),
('João Oliveira',    '11122233344', 'senha789', 'FISCAL');

INSERT INTO empresa (razao, cnpj, telefone, endereco, bairro, cidade, estado) VALUES
('Construtora Alpha LTDA',  '12345678000199', '(11) 3333-4444', 'Rua A, 100', 'Centro', 'São Paulo', 'SP'),
('Serviços Beta EIRELI',    '98765432000188', '(21) 98888-7777', 'Av. B, 200', 'Copacabana', 'Rio de Janeiro', 'RJ');

INSERT INTO contrato (id_fiscal, id_empresa, valor_contrato, dt_inicio, dt_fim, descricao) VALUES
(2, 1, 150000.00, '2025-01-01', '2025-12-31', 'Construção de unidade básica de saúde.'),
(3, 2, 80000.00,  '2025-03-15', '2025-09-15', 'Prestação de serviços de TI.');

INSERT INTO aditamento (id_contrato, dt_aditamento, novo_valor, novo_dt_inicio, novo_dt_fim, observacoes) VALUES
(1, '2025-06-01', 160000.00, NULL, '2026-01-31', 'Prorrogação do contrato com acréscimo de valor.'),
(2, '2025-07-01', NULL, '2025-04-01', NULL, 'Alteração na data de início dos serviços.');
