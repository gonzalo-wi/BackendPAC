package com.el_jumillano.pac.integrations.health;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetIntegrationStatusUseCase {

    private final IntegrationStatusRepositoryAdapter repository;

    public List<IntegrationStatus> execute() {
        return repository.findAll();
    }
}
