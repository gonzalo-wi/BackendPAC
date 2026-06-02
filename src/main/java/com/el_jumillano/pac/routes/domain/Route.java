package com.el_jumillano.pac.routes.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route {

    private Long id;
    private Integer routeNumber;
    private Long plantId;
    private LocalDate date;
    private RouteStatus status;
}
