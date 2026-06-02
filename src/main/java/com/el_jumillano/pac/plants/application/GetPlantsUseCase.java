package com.el_jumillano.pac.plants.application;

import com.el_jumillano.pac.plants.domain.Plant;
import com.el_jumillano.pac.plants.infrastructure.PlantRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPlantsUseCase {

    private final PlantRepositoryAdapter repository;

    public List<Plant> execute() {
        return repository.findAll();
    }

    public Plant getById(Long id) {
        return repository.findById(id);
    }
}
