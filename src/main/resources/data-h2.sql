-- =======================
-- DROPS SEGUROS
-- =======================

DROP TABLE IF EXISTS tb_registro;
DROP TABLE IF EXISTS tb_leitor;
DROP TABLE IF EXISTS tb_vaga;
DROP TABLE IF EXISTS tb_moto;
DROP TABLE IF EXISTS tb_patio;
DROP TABLE IF EXISTS tb_usuario;

-- =======================
-- CRIAÇÃO DAS TABELAS (versão H2)
-- =======================

-- TABELA PÁTIO (cria primeiro porque é FK de várias)
CREATE TABLE tb_patio (
                          id       UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                          nome     VARCHAR(100) NOT NULL,
                          rua      VARCHAR(100),
                          numero   VARCHAR(20),
                          bairro   VARCHAR(50),
                          cidade   VARCHAR(50),
                          estado   VARCHAR(50),
                          pais     VARCHAR(50)
);

-- nome único por pátio
CREATE UNIQUE INDEX uk_patio_nome ON tb_patio (nome);

-- TABELA USUÁRIO
CREATE TABLE tb_usuario (
                            id        UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                            email     VARCHAR(255) NOT NULL UNIQUE,
                            senha     VARCHAR(255) NOT NULL,
                            cargo     VARCHAR(50)  NOT NULL,
                            patio_id  UUID,
                            CONSTRAINT fk_usuario_patio
                                FOREIGN KEY (patio_id) REFERENCES tb_patio(id)
);

ALTER TABLE tb_usuario ADD COLUMN expo_push_token VARCHAR(255);


-- TABELA MOTO
CREATE TABLE tb_moto (
                         id        UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                         placa     VARCHAR(7)   NOT NULL UNIQUE,
                         status    VARCHAR(50)  NOT NULL,
                         patio_id  UUID,
                         vaga_id   UUID,              -- <<< NOVA COLUNA
                         CONSTRAINT fk_moto_patio
                             FOREIGN KEY (patio_id) REFERENCES tb_patio(id)
);

CREATE INDEX ix_moto_patio ON tb_moto(patio_id);

-- TABELA VAGA
CREATE TABLE tb_vaga (
                         id             UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                         status         VARCHAR(50),
                         patio_id       UUID,
                         moto_id        UUID UNIQUE,        -- impede duas vagas com a mesma moto (permite vários NULL)
                         identificacao  VARCHAR(20),

                         CONSTRAINT fk_vaga_patio
                             FOREIGN KEY (patio_id) REFERENCES tb_patio(id),

                         CONSTRAINT fk_vaga_moto
                             FOREIGN KEY (moto_id) REFERENCES tb_moto(id) ON DELETE SET NULL
);

-- uma identificação de vaga é única dentro de um pátio
CREATE UNIQUE INDEX ux_vaga_patio_nome ON tb_vaga (patio_id, identificacao);

-- TABELA LEITOR
CREATE TABLE tb_leitor (
                           id        UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                           tipo      VARCHAR(50) NOT NULL,
                           patio_id  UUID NOT NULL,
                           vaga_id   UUID,

                           CONSTRAINT fk_leitor_patio
                               FOREIGN KEY (patio_id) REFERENCES tb_patio(id),

                           CONSTRAINT fk_leitor_vaga
                               FOREIGN KEY (vaga_id) REFERENCES tb_vaga(id)
);

-- TABELA REGISTRO
CREATE TABLE tb_registro (
                             id         UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                             moto_id    UUID NOT NULL,
                             leitor_id  UUID NOT NULL,
                             tipo       VARCHAR(50) NOT NULL,
                             data_hora  TIMESTAMP   NOT NULL,

                             CONSTRAINT fk_registro_moto
                                 FOREIGN KEY (moto_id)   REFERENCES tb_moto(id),

                             CONSTRAINT fk_registro_leitor
                                 FOREIGN KEY (leitor_id) REFERENCES tb_leitor(id)
);

-- =======================
-- DADOS INICIAIS
-- =======================

-- Pátio padrão
INSERT INTO tb_patio (nome, rua, numero, bairro, cidade, estado, pais)
VALUES ('Pátio Central', 'Rua Principal', '123', 'Centro', 'São Paulo', 'SP', 'Brasil');

-- Vaga livre no mesmo pátio
INSERT INTO tb_vaga (status, patio_id, moto_id, identificacao)
VALUES (
           'LIVRE',
           (SELECT id FROM tb_patio WHERE nome = 'Pátio Central'),
           NULL,
           'A02'
       );

INSERT INTO tb_usuario (id, email, senha, cargo, patio_id)
VALUES (
           RANDOM_UUID(),                                   -- gera UUID no H2
           'admin@motosync.com',                           -- e-mail do admin
           '$2b$10$mDn1QxWAF1esglWOvThEEurwjZ2V540nTbKd/lpPoQJsBwRIAEQxy', -- senha BCrypt (Admin@123)
           'ADMIN',                                        -- cargo
           NULL                                            -- admin não precisa de pátio
       );
