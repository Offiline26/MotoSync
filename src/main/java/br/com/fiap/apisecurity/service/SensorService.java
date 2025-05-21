package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.dto.SensorDTO;
import br.com.fiap.apisecurity.mapper.SensorMapper;
import br.com.fiap.apisecurity.model.Sensor;
import br.com.fiap.apisecurity.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;

    @Autowired
    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    // Create
    @Transactional
    @CachePut(value = "sensores", key = "#result.id")
    public SensorDTO createSensor(SensorDTO sensorDTO) {
        Sensor sensor = SensorMapper.toEntity(sensorDTO);
        return SensorMapper.toDto(sensorRepository.save(sensor));
    }

    // Read by ID
    @Cacheable(value = "sensores", key = "#id")
    public SensorDTO readSensorById(UUID id) {
        Sensor sensor = sensorRepository.findById(id).orElse(null);
        return SensorMapper.toDto(sensor);
    }

    // Read by código único
    @Cacheable(value = "sensores", key = "#codigoUnico")
    public SensorDTO readByCodigoUnico(String codigoUnico) {
        Sensor sensor = sensorRepository.findByCodigoUnico(codigoUnico).orElse(null);
        return SensorMapper.toDto(sensor);
    }

    // Read all
    @Cacheable(value = "sensores", key = "#pageable")
    public Page<SensorDTO> readAllSensores(Pageable pageable) {
        Page<Sensor> page = sensorRepository.findAll(pageable);
        List<SensorDTO> dtoList = page.getContent()
                .stream()
                .map(SensorMapper::toDto)
                .toList();
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    // Update
    @Transactional
    @CachePut(value = "sensores", key = "#result.id")
    public SensorDTO updateSensor(UUID id, SensorDTO sensorDTO) {
        Optional<Sensor> optionalSensor = sensorRepository.findById(id);
        if (optionalSensor.isEmpty()) {
            return null;
        }
        Sensor sensor = SensorMapper.toEntity(sensorDTO);
        sensor.setId(id);
        return SensorMapper.toDto(sensorRepository.save(sensor));
    }

    // Delete
    @Transactional
    @CacheEvict(value = "sensores", key = "#id")
    public void deleteSensor(UUID id) {
        sensorRepository.deleteById(id);
    }
}


