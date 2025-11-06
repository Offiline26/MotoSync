package br.com.fiap.apisecurity.mapper;

import br.com.fiap.apisecurity.dto.PatioDTO;
import br.com.fiap.apisecurity.model.Patio;

import java.util.List;
import java.util.stream.Collectors;

public final class PatioMapper {

    private PatioMapper() {}

    public static PatioDTO toDto(Patio patio) {
        if (patio == null) return null;
        return new PatioDTO(
                patio.getId(),
                patio.getNome(),
                patio.getRua(),
                patio.getNumero(),
                patio.getBairro(),
                patio.getCidade(),
                patio.getEstado(),
                patio.getPais()
        );
    }

    public static Patio toEntity(PatioDTO dto) {
        if (dto == null) return null;
        Patio patio = new Patio();
        patio.setId(dto.getId());
        patio.setNome(dto.getNome());
        patio.setRua(dto.getRua());
        patio.setNumero(dto.getNumero());
        patio.setBairro(dto.getBairro());
        patio.setCidade(dto.getCidade());
        patio.setEstado(dto.getEstado());
        patio.setPais(dto.getPais());
        return patio;
    }

    public static void apply(PatioDTO dto, Patio target) {
        if (dto == null || target == null) return;

        if (dto.getNome()   != null) target.setNome(dto.getNome());
        if (dto.getRua()    != null) target.setRua(dto.getRua());
        if (dto.getNumero() != null) target.setNumero(dto.getNumero());
        if (dto.getBairro() != null) target.setBairro(dto.getBairro());
        if (dto.getCidade() != null) target.setCidade(dto.getCidade());
        if (dto.getEstado() != null) target.setEstado(dto.getEstado());
        if (dto.getPais()   != null) target.setPais(dto.getPais());
    }

    public static List<PatioDTO> toDtoList(List<Patio> patios) {
        return patios.stream()
                .map(PatioMapper::toDto)
                .collect(Collectors.toList());
    }
}


