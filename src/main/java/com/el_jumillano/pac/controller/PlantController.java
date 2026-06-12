package com.el_jumillano.pac.controller;

import com.el_jumillano.pac.plants.application.CloseAllUseCase;
import com.el_jumillano.pac.plants.application.GetPlantsUseCase;
import com.el_jumillano.pac.plants.application.GetPlantStatsUseCase;
import com.el_jumillano.pac.plants.application.PlantResponse;
import com.el_jumillano.pac.plants.application.PlantStatsResponse;
import com.el_jumillano.pac.plants.application.ProcessAllUseCase;
import com.el_jumillano.pac.plants.application.RefreshPlantUseCase;
import com.el_jumillano.pac.reconciliation.application.ReconciliationResponseMapper;
import com.el_jumillano.pac.reconciliation.application.ReconciliationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Plants")
@RestController
@RequestMapping("/api/plants")
@RequiredArgsConstructor
public class PlantController {

    private final GetPlantsUseCase getPlantsUseCase;
    private final RefreshPlantUseCase refreshUseCase;
    private final ProcessAllUseCase processAllUseCase;
    private final CloseAllUseCase closeAllUseCase;
    private final GetPlantStatsUseCase statsUseCase;
    private final ReconciliationResponseMapper reconciliationMapper;

    @GetMapping
    public List<PlantResponse> list() {
        return getPlantsUseCase.execute().stream()
                .map(p -> new PlantResponse(p.getId(), p.getName(), p.getCode()))
                .toList();
    }


    @PostMapping("/{plantId}/refresh")
    public List<ReconciliationResponse> refresh(
            @PathVariable Long plantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return refreshUseCase.execute(plantId, date, userId)
                .stream().map(reconciliationMapper::toResponse).toList();
    }


    @PostMapping("/{plantId}/process-all")
    public List<ReconciliationResponse> processAll(
            @PathVariable Long plantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return processAllUseCase.execute(plantId, date, userId)
                .stream().map(reconciliationMapper::toResponse).toList();
    }


    @PostMapping("/{plantId}/close-all")
    public List<ReconciliationResponse> closeAll(
            @PathVariable Long plantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return closeAllUseCase.execute(plantId, date, userId)
                .stream().map(reconciliationMapper::toResponse).toList();
    }


    @GetMapping("/stats")
    public List<PlantStatsResponse> allStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return statsUseCase.getAllPlants(date);
    }

    
    @GetMapping("/{plantId}/stats")
    public PlantStatsResponse stats(
            @PathVariable Long plantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return statsUseCase.getByPlant(plantId, date);
    }
}
