package za.co.costcomining.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.co.costcomining.api.service.ContractService;
import za.co.costcomining.common.dto.ContractDto;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @GetMapping
    public ResponseEntity<List<ContractDto>> findAll() {
        return ResponseEntity.ok(contractService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(contractService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContractDto> create(@RequestBody ContractDto dto) {
        return ResponseEntity.ok(contractService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContractDto> update(@PathVariable String id, @RequestBody ContractDto dto) {
        return ResponseEntity.ok(contractService.update(id, dto));
    }
}
