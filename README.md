# MotoSync API

**LINK DO VIDEO**: [[MotoSync]](https://youtu.be/Lxsvl0MYdp4)

**MotoSync** é uma API RESTful desenvolvida em Java com Spring Boot para o gerenciamento inteligente de motos em pátios da empresa Mottu. Integrando tecnologias modernas e recursos de autenticação, o sistema permite organização e rastreamento das motos de forma segura, com integração com dispositivos IoT e um aplicativo mobile.

---

## 📚 Visão Geral

A aplicação resolve o problema da desorganização de motos nos pátios da empresa, automatizando o processo de alocação de vagas, leitura de entradas e saídas com sensores RFID, controle por operadores e gestão de registros de movimentação.

---

## ⚙️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot**
- **Spring Data JPA**
- **Spring Security + JWT**
- **Spring Cache**
- **Oracle SQL**
- **Azure SQL**
- **ThymeLeaf** (Para Testes)
- **H2** (Para Testes)
- **Render**
- **Azure Devops**

---

### 🧠 Entidades e Funcionalidades

## 🏢 Pátio (/patios)

GET /patios – lista pátios

GET /patios/{id} – pátio por ID

GET /patios/cidade/{cidade} – filtra por cidade

POST /patios – cria pátio
Campos: nome, rua, numero, bairro, cidade, estado, pais

## 📍 Vaga (/vagas)

GET /vagas – lista vagas

GET /vagas/{id} – por ID

GET /vagas/patio/{patioId}/status/{status} – por pátio e status (OCUPADA, LIVRE)

POST /vagas – cria vaga
Campos: coordenadaLat, coordenadaLong, status, patioId, motoId

## 🏍️ Moto (/motos)

GET /motos – lista motos

GET /motos/{id} – por ID

GET /motos/placa/{placa} – por placa

POST /motos – cria moto
Campos: placa, marca, modelo, cor, vagaId

## 📡 Leitor (/leitores)

GET /leitores – lista leitores

GET /leitores/{id} – por ID

GET /leitores/patio/{patioId} – por pátio

GET /leitores/vaga/{vagaId}/tipo/{tipo} – por vaga e tipo

POST /leitores – cria leitor
Campos: tipo (ENTRADA | VAGA), vagaId, patioId

## 🧾 Registro (/registros)

GET /registros – lista registros

GET /registros/moto/{motoId} – por moto

GET /registros/moto/{motoId}/tipo/{tipo} – por moto + tipo (ENTRADA | SAIDA)

GET /registros/periodo?inicio=...&fim=... – por período

POST /registros – cria registro
Campos: motoId, leitorId, tipo, dataHora

---

## 🔐 Segurança (JWT + Regras de Escopo)

Login API: POST /api/auth/login → retorna accessToken (JWT).

Uso: enviar Authorization: Bearer <token> nas rotas privadas.

Regras:

ADMIN: acesso total.

OPERADOR_PATIO: tudo filtrado pelo pátio do usuário (aplicado nos Services e Repositories).

---

## ▶️ Como Executar

1. Clone o projeto:

```bash
git clone https://github.com/Offiline26/MotoSync.git
cd MotoSync

utilizar a branch #pro-gui
```
A aplicação sobe em http://localhost:8081/login

## Web (Thymeleaf):

Home: http://localhost:8081/

Login: http://localhost:8081/login

Cadastro (operador): http://localhost:8081/register

## API: http://localhost:8081/api/**

Login: POST /api/auth/login

Usuários de exemplo (dev):

ADMIN: thiago@email.com / 123456

OPERADOR: lgsreal@gmail.com / 123456

## 🧭 Perfis e UI (resumo)

ADMIN vê/edita tudo (pátios, vagas, motos, leitores, registros).

OPERADOR_PATIO só vê/atua no seu pátio.

As telas Thymeleaf (navbar/footer/head) servem de prova funcional das regras e incluem CSRF.

## 🧰 Troubleshooting

403 / dados “de outro pátio” → verifique o papel do usuário e o patioId associado.

401 → faltou header Authorization.

CSRF em formulários web → certifique-se de incluir o token ${_csrf.parameterName} / ${_csrf.token}.

Oracle não conecta → confira porta/serviço (ex.: XEPDB1) e credenciais.

## 👨‍💻 Autores

Projeto desenvolvido por :
**Thiago Mendes** — RM 555352

**Guilherme Gonçalves** - RM 558475

**Vinicius Banciela** - RM 558117

---
