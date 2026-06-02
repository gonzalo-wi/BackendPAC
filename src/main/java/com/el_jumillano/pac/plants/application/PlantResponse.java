package com.el_jumillano.pac.plants.application;

import com.el_jumillano.pac.plants.domain.PlantCode;

public record PlantResponse(Long id, String name, PlantCode code) {}
