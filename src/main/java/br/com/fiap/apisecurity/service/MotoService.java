package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.controller.usuario.Authz;
import br.com.fiap.apisecurity.dto.MotoDTO;
import br.com.fiap.apisecurity.mapper.MotoMapper;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.model.Vaga;
import br.com.fiap.apisecurity.model.enums.StatusMoto;
import br.com.fiap.apisecurity.model.enums.StatusVaga;
import br.com.fiap.apisecurity.repository.MotoRepository;
import br.com.fiap.apisecurity.repository.VagaRepository;
import br.com.fiap.apisecurity.service.notificacao.ExpoNotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MotoService {

    private final MotoRepository motoRepository;
    private final VagaRepository vagaRepository;
    private final ExpoNotificationService expoNotificationService;
    private final Authz authz;

    @Autowired
    public MotoService(MotoRepository motoRepository,
                       VagaRepository vagaRepository,
                       Authz authz,
                       ExpoNotificationService expoNotificationService) {
        this.motoRepository = motoRepository;
        this.vagaRepository = vagaRepository;
        this.authz = authz;
        this.expoNotificationService = expoNotificationService;
    }

    @Transactional
    @Caching(
            put = { @CachePut(cacheNames="motosById", key="#result.id") },
            evict = {
                    @CacheEvict(cacheNames="motosList", allEntries=true),
                    @CacheEvict(cacheNames="motosListAtivas", allEntries=true)
            }
    )
    public MotoDTO createMoto(MotoDTO dto) {

        // 🔐 Regras de autorização continuam iguais
        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();

            if (dto.getVagaId() == null) {
                throw new SecurityException("Operador só pode criar moto vinculada a Vaga do seu pátio.");
            }

            Vaga vaga = vagaRepository.findById(dto.getVagaId())
                    .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada: " + dto.getVagaId()));

            if (vaga.getPatio() == null || !userPatio.equals(vaga.getPatio().getId())) {
                throw new SecurityException("Operador só pode criar moto em vaga do próprio pátio.");
            }
        }

        // 🎯 Converte o DTO em entidade
        Moto moto = MotoMapper.toEntity(dto);

        // Garante status inicial
        moto.setStatus(StatusMoto.DISPONIVEL); // evita null e padroniza

        Vaga vaga = null;

        // ⚙️ Se vier vagaId, valida e deixa a vaga carregada
        if (dto.getVagaId() != null) { // usar dto, não moto, é mais explícito
            vaga = vagaRepository.findById(dto.getVagaId())
                    .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada: " + dto.getVagaId()));

            if (vaga.getStatus() == StatusVaga.OCUPADA) {
                throw new IllegalStateException("Vaga já está ocupada.");
            }

            // Se Moto tiver relação com Pátio, pode garantir consistência aqui:
            // if (moto.getPatio() == null) {
            //     moto.setPatio(vaga.getPatio()); // mantém moto no mesmo pátio da vaga
            // }
        }

        // ✅ 1) SALVA A MOTO PRIMEIRO
        Moto saved = motoRepository.save(moto); // agora é entidade managed/persistida

        // ✅ 2) Depois vincula na vaga e salva a vaga
        if (vaga != null) {
            vaga.setMoto(saved);                // agora aponta para Moto persistida → evita TransientObjectException
            vaga.setStatus(StatusVaga.OCUPADA);
            vagaRepository.save(vaga);

            // Notificação de ocupação / verificar se pátio ficou cheio, etc.
            expoNotificationService.checkEmptyParkSendAlert(vaga.getPatio().getId());
        }

        return MotoMapper.toDto(saved);
    }


    @Transactional(readOnly = true)
    @Cacheable(cacheNames="motosById", key="#id")
    public MotoDTO readMotoById(UUID id) {
        Moto moto = readMotoByIdEntity(id);
        return MotoMapper.toDto(moto);
    }

    @Transactional(readOnly = true)
    public Moto readMotoByIdEntity(UUID id) {
        Moto moto = motoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada: " + id));

        if (authz.isAdmin()) return moto;

        UUID userPatio = authz.currentUserPatioIdOrThrow();
        UUID vagaId = moto.getVagaId();
        if (vagaId == null) throw new SecurityException("Moto sem vaga atual não é visível ao operador.");
        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new EntityNotFoundException("Vaga da moto não encontrada: " + vagaId));
        if (vaga.getPatio() == null || !userPatio.equals(vaga.getPatio().getId())) {
            throw new SecurityException("Acesso negado: moto pertence a outro pátio.");
        }
        return moto;
    }

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = "motosList",
            key       = "(#pageable != null && #pageable.isPaged()) ? \n" +
                    "        (#pageable.pageNumber + ':' + #pageable.pageSize + ':' + (#pageable.sort != null ? #pageable.sort : 'UNSORTED')) \n" +
                    "        : 'UNPAGED'",   // usa o próprio Pageable (tem equals/hashCode)
            condition = "@authz.isAdmin()"
    )
    public Page<MotoDTO> readAllMotos(Pageable pageable) {
        if (authz.isAdmin()) {
            Page<Moto> page = motoRepository.findAll(pageable);
            Map<UUID, String> idToIdent = loadVagaIdentificacao(page.getContent());
            List<MotoDTO> content = page.getContent()
                    .stream()
                    .map(m -> MotoMapper.toDto(m, idToIdent.get(m.getVagaId())))
                    .toList();
            return new PageImpl<>(content, pageable, page.getTotalElements());
        }

        UUID patioId = authz.currentUserPatioIdOrThrow();
        List<UUID> vagaIds = vagaRepository.findAllIdsByPatioId(patioId);
        if (vagaIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<Moto> page = motoRepository.findAllByVagaIdIn(vagaIds, pageable);
        Map<UUID, String> idToIdent = loadVagaIdentificacao(page.getContent());
        List<MotoDTO> content = page.getContent()
                .stream()
                .map(m -> MotoMapper.toDto(m, idToIdent.get(m.getVagaId())))
                .toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Transactional
    @Caching(
            put = { @CachePut(cacheNames="motosById", key="#result.id") },
            evict = {
                    @CacheEvict(cacheNames="motosList", allEntries=true),
                    @CacheEvict(cacheNames="motosListAtivas", allEntries=true)
            }
    )
    public MotoDTO updateMoto(UUID id, MotoDTO dto) {

        // 1) Carrega a moto persistida
        Moto moto = motoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada: " + id));

        // 2) Regras de segurança para operador
        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrNull();

            // Confere se a vaga atual da moto pertence ao pátio do operador
            UUID vagaAtualId = moto.getVagaId();
            if (vagaAtualId != null) {
                Vaga vagaAtual = vagaRepository.findById(vagaAtualId)
                        .orElseThrow(() -> new EntityNotFoundException("Vaga atual não encontrada: " + vagaAtualId));
                if (vagaAtual.getPatio() == null || !userPatio.equals(vagaAtual.getPatio().getId())) {
                    throw new SecurityException("Operador não pode atualizar moto de outro pátio.");
                }
            }

            // Se foi informada nova vaga, confere se também é do mesmo pátio
            if (dto.getVagaId() != null) {
                Vaga nova = vagaRepository.findById(dto.getVagaId())
                        .orElseThrow(() -> new EntityNotFoundException("Vaga informada não encontrada: " + dto.getVagaId()));
                if (nova.getPatio() == null || !userPatio.equals(nova.getPatio().getId())) {
                    throw new SecurityException("Operador só pode mover moto dentro do próprio pátio.");
                }
            }
        }

        // 3) Ajusta vínculo de vaga (libera antiga, ocupa nova, dispara notificações)
        ajustarVagasSeNecessario(moto, dto.getVagaId());

        // 4) Atualiza dados simples da moto
        if (dto.getPlaca() != null) {
            moto.setPlaca(dto.getPlaca().trim().toUpperCase());
        }
        if (dto.getStatus() != null) {
            moto.setStatus(dto.getStatus());
        }

        Moto saved = motoRepository.save(moto);
        return MotoMapper.toDto(saved);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames="motosById", key="#id"),
            @CacheEvict(cacheNames="motosList", allEntries=true),
            @CacheEvict(cacheNames="motosListAtivas", allEntries=true)
    })
    public void inativarMoto(UUID id) {

        Moto moto = readMotoByIdEntity(id);
        moto.setStatus(StatusMoto.INATIVADA);

        // 👉 Use a associação em vez de vagaId “solta”
        Vaga vaga = null;
        if (moto.getVagaId() != null) {
            vaga = vagaRepository.findById(moto.getVagaId()).orElse(null);
        }
        // se Moto tiver: private Vaga vaga;
        if (vaga != null) {
            Patio patio = vaga.getPatio();   // precisamos disso pro alerta

            vaga.setMoto(null);
            vaga.setStatus(StatusVaga.LIVRE);
            vagaRepository.save(vaga);

            moto.setVagaId(null);              // quebra a associação dos dois lados

            // 👉 Agora sim, ID do PÁTIO
            if (patio != null) {
                expoNotificationService.checkEmptyParkSendAlert(patio.getId());
            }
        }

        motoRepository.save(moto);
    }

    @Transactional(readOnly = true)
    public Moto readByPlaca(String placa) {
        Optional<Moto> opt = motoRepository.findByPlaca(placa);
        Moto moto = opt.orElseThrow(() -> new EntityNotFoundException("Moto não encontrada pela placa: " + placa));

        if (authz.isAdmin()) return moto;

        UUID userPatio = authz.currentUserPatioIdOrThrow();
        UUID vagaId = moto.getVagaId();
        if (vagaId == null) throw new SecurityException("Moto sem vaga atual não é visível ao operador.");
        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new EntityNotFoundException("Vaga da moto não encontrada: " + vagaId));
        if (vaga.getPatio() == null || !userPatio.equals(vaga.getPatio().getId())) {
            throw new SecurityException("Acesso negado: moto pertence a outro pátio.");
        }
        return moto;
    }

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames="motosListAtivas",
            key="(#pageable != null && #pageable.isPaged()) ? \n" +
                    "        (#pageable.pageNumber + ':' + #pageable.pageSize + ':' + (#pageable.sort != null ? #pageable.sort : 'UNSORTED')) \n" +
                    "        : 'UNPAGED'",
            condition="@authz.isAdmin()"
    )
    public Page<MotoDTO> readAllMotosAtivas(Pageable pageable) {
        if (authz.isAdmin()) {
            Page<Moto> page = motoRepository.findAllByStatus(StatusMoto.DISPONIVEL, pageable);
            Map<UUID, String> idToIdent = loadVagaIdentificacao(page.getContent());
            List<MotoDTO> content = page.getContent()
                    .stream()
                    .map(m -> MotoMapper.toDto(m, idToIdent.get(m.getVagaId())))
                    .toList();
            return new PageImpl<>(content, pageable, page.getTotalElements());
        }

        UUID patioId = authz.currentUserPatioIdOrThrow();
        List<UUID> vagaIds = vagaRepository.findAllIdsByPatioId(patioId);
        if (vagaIds.isEmpty()) return Page.empty(pageable);

        Page<Moto> page = motoRepository.findAllByStatusAndVagaIdIn(StatusMoto.DISPONIVEL, vagaIds, pageable);
        Map<UUID, String> idToIdent = loadVagaIdentificacao(page.getContent());
        List<MotoDTO> content = page.getContent()
                .stream()
                .map(m -> MotoMapper.toDto(m, idToIdent.get(m.getVagaId())))
                .toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public MotoDTO readByPlacaDto(String placa) {
        return MotoMapper.toDto(readByPlaca(placa));
    }

    private Map<UUID, String> loadVagaIdentificacao(List<Moto> motos) {
        Set<UUID> vagaIds = motos.stream()
                .map(Moto::getVagaId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (vagaIds.isEmpty()) return Collections.emptyMap();
        return vagaRepository.findIdentificacoesByIds(vagaIds);
    }

    private void ajustarVagasSeNecessario(Moto moto, UUID novaVagaId) {

        UUID vagaAntigaId = moto.getVagaId();

        // Se nada mudou, não faz nada
        if (Objects.equals(vagaAntigaId, novaVagaId)) return;

        // 1) Libera a vaga antiga, se existir
        if (vagaAntigaId != null) {
            Vaga antiga = vagaRepository.findById(vagaAntigaId)
                    .orElseThrow(() -> new EntityNotFoundException("Vaga antiga não encontrada: " + vagaAntigaId));

            Patio patioAntigo = antiga.getPatio();          // guarda o pátio pra notificação

            antiga.setMoto(null);                           // tira o vínculo Moto <- Vaga
            antiga.setStatus(StatusVaga.LIVRE);             // marca como livre
            vagaRepository.save(antiga);

            if (patioAntigo != null) {
                // notifica usando o ID do PÁTIO (não da vaga)
                expoNotificationService.checkEmptyParkSendAlert(patioAntigo.getId());
            }
        }

        // 2) Se tem nova vaga → ocupa
        if (novaVagaId != null) {
            Vaga nova = vagaRepository.findById(novaVagaId)
                    .orElseThrow(() -> new EntityNotFoundException("Vaga nova não encontrada: " + novaVagaId));

            // Se já tiver outra moto lá, impede conflito
            if (nova.getMoto() != null && !nova.getMoto().getId().equals(moto.getId())) {
                throw new IllegalStateException("Vaga já está ocupada por outra moto.");
            }

            nova.setMoto(moto);                              // Vaga passa a apontar pra essa moto
            nova.setStatus(StatusVaga.OCUPADA);
            vagaRepository.save(nova);

            moto.setVagaId(novaVagaId);                      // Moto guarda o id da nova vaga

            if (nova.getPatio() != null) {
                expoNotificationService.checkEmptyParkSendAlert(nova.getPatio().getId());
            }
        } else {
            // Sem nova vaga → moto fica sem vaga
            moto.setVagaId(null);
        }
    }

}



