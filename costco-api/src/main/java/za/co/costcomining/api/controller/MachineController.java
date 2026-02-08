package za.co.costcomining.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.co.costcomining.api.service.MachineService;
import za.co.costcomining.common.dto.MachineDto;

import java.util.List;

@RestController
@RequestMapping("/api/machines")
@RequiredArgsConstructor
public class MachineController {

    private final MachineService machineService;

    @GetMapping
    public ResponseEntity<List<MachineDto>> findAll() {
        return ResponseEntity.ok(machineService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MachineDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(machineService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MachineDto> create(@RequestBody MachineDto dto) {
        return ResponseEntity.ok(machineService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MachineDto> update(@PathVariable String id, @RequestBody MachineDto dto) {
        return ResponseEntity.ok(machineService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        machineService.delete(id);
        return ResponseEntity.ok().build();
    }
}
