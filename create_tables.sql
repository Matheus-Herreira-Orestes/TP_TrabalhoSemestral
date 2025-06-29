CREATE TABLE usuario (
  id_usuario   SERIAL PRIMARY KEY,
  nome         VARCHAR(100) NOT NULL,
  cpf          CHAR(11)     NOT NULL UNIQUE,
  senha        VARCHAR(255) NOT NULL,
  perfil       VARCHAR(20)  NOT NULL CHECK (perfil IN ('FISCAL', 'ADMIN'))
);

CREATE TABLE empresa (
  id_empresa   SERIAL PRIMARY KEY,
  razao        VARCHAR(150) NOT NULL,
  cnpj         CHAR(14)     NOT NULL UNIQUE,
  telefone     VARCHAR(20),
  endereco     VARCHAR(200),
  bairro       VARCHAR(100),
  cidade       VARCHAR(100),
  estado       CHAR(2)
);

CREATE TABLE contrato (
  id_contrato     SERIAL PRIMARY KEY,
  id_fiscal       INT NOT NULL REFERENCES usuario(id_usuario),
  id_empresa      INT NOT NULL REFERENCES empresa(id_empresa),
  valor_contrato  NUMERIC(15,2) NOT NULL,
  dt_inicio       DATE NOT NULL,
  dt_fim          DATE NOT NULL,
  descricao       TEXT
);

CREATE TABLE aditamento (
  id_aditamento   SERIAL PRIMARY KEY,
  id_contrato     INT NOT NULL REFERENCES contrato(id_contrato),
  dt_aditamento   DATE NOT NULL DEFAULT CURRENT_DATE,
  novo_valor      NUMERIC(15,2),
  novo_dt_inicio  DATE,
  novo_dt_fim     DATE,
  observacoes     TEXT
);
