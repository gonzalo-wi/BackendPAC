package com.el_jumillano.pac.plants.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plant {

    private Long id;
    private String name;
    private PlantCode code;
}
