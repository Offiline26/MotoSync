# MotoSync API

**LINK DO VIDEO**: [PRODUZINDO]

**MotoSync** é uma API RESTful desenvolvida em Java com Spring Boot para o gerenciamento inteligente de motos em pátios da empresa Mottu. Integrando tecnologias modernas e recursos de autenticação, o sistema permite organização e rastreamento das motos de forma segura, com integração com dispositivos IoT e um aplicativo mobile.

---

## 📚 Visão Geral

A aplicação resolve o problema da desorganização de motos nos pátios da empresa, automatizando o processo de alocação de vagas, leitura de entradas e saídas com sensores RFID, controle por operadores e gestão de registros de movimentação.

---

## ⚙️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **Spring Security + JWT**
- **Spring Cache**
- **Banco H2** (para testes)
- **Postman** (testes)

---

## 🧠 Entidades e Funcionalidades

### 🏢 Pátio (`/patios`)

- `GET /patios`: Lista todos os pátios
- `GET /patios/{id}`: Busca um pátio por ID
- `GET /patios/cidade/{cidade}`: Filtra pátios pela cidade
- `POST /patios`: Cria um novo pátio

**Campos:** `nome`, `rua`, `numero`, `bairro`, `cidade`, `estado`, `pais`

---

### 📍 Vaga (`/vagas`)

- `GET /vagas`: Lista todas as vagas
- `GET /vagas/{id}`: Busca vaga por ID
- `GET /vagas/patio/{patioId}/status/{status}`: Filtra vagas por pátio e status (OCUPADA, LIVRE)
- `POST /vagas`: Cria nova vaga

**Campos:** `coordenadaLat`, `coordenadaLong`, `status`, `patioId`, `motoId`

---

### 🏍️ Moto (`/motos`)

- `GET /motos`: Lista todas as motos
- `GET /motos/{id}`: Busca por ID
- `GET /motos/placa/{placa}`: Busca por placa
- `POST /motos`: Cadastra nova moto

**Campos:** `placa`, `marca`, `modelo`, `cor`, `vagaId` (relacionada com a vaga)

---

### 📡 Leitor (`/leitores`)

- `GET /leitores`: Lista todos os leitores
- `GET /leitores/{id}`: Busca leitor por ID
- `GET /leitores/patio/{patioId}`: Busca leitores de um pátio
- `GET /leitores/vaga/{vagaId}/tipo/{tipo}`: Busca leitor por tipo e vaga
- `POST /leitores`: Cadastra novo leitor

**Campos:** `tipo` (ENTRADA ou SAÍDA), `vagaId`, `patioId`

---

### 🧾 Registro (`/registros`)

- `GET /registros`: Lista todos os registros
- `GET /registros/moto/{motoId}`: Busca registros por moto
- `GET /registros/moto/{motoId}/tipo/{tipo}`: Filtra por tipo (ENTRADA/SAIDA)
- `GET /registros/periodo?inicio=...&fim=...`: Busca por período
- `POST /registros`: Cria novo registro

**Campos:** `motoId`, `leitorId`, `tipo` (ENTRADA/SAIDA), `dataHora`

---

### 👤 Autenticação Simulada

O login do sistema é realizado automaticamente, sem necessidade de credenciais reais.
Durante essa fase de testes, um token JWT falso é gerado para permitir o acesso às rotas autenticadas e facilitar os testes no Postman e na aplicação.

---

## 🔐 Segurança com JWT

A autenticação é realizada via JWT (JSON Web Token), garantindo proteção aos endpoints privados.

- Ao fazer login, um token JWT é gerado.
- Esse token deve ser enviado no header `Authorization` em todas as requisições privadas. Mas ja esta inserido por padrão para testes.

---

## ▶️ Como Executar

1. Clone o projeto:

```bash
git clone https://github.com/Offiline26/MotoSync.git
cd MotoSync
```

2. Configure o application.properties para banco local (H2).

3. Execute a aplicação:

```bash
./mvnw spring-boot:run
```

4. Use o Postman para testar os endpoints.

---

## 🧪 Testes no Postman

Todos os endpoints estão prontos para serem testados via Postman. Você pode importar o seguinte JSON de coleção (gerado automaticamente no futuro).

**Testes ja pré prontos no Body**

---

## 👨‍💻 Autores

Projeto desenvolvido por :
**Thiago Mendes** — RM 555352

**Guilherme Gonçalves** - RM 555352

**Vinicius Banciela** - RM 558117

---
