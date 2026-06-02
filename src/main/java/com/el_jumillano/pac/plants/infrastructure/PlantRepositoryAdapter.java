package com.el_jumillano.pac.plants.infrastructure;

import com.el_jumillano.pac.plants.domain.Plant;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlantRepositoryAdapter {

    private final PlantJpaRepository jpaRepository;
    private final PlantMapper mapper;

    public Plant findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("Plant", id));
    }

    public List<Plant> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }
}
