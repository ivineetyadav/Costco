package za.co.costcomining.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.co.costcomining.api.service.DeviceService;
import za.co.costcomining.common.dto.DeviceDto;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public ResponseEntity<List<DeviceDto>> findAll() {
        return ResponseEntity.ok(deviceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(deviceService.findById(id));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceDto> register(@RequestBody DeviceDto dto) {
        return ResponseEntity.ok(deviceService.register(dto));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceDto> approve(@PathVariable String id) {
        return ResponseEntity.ok(deviceService.approve(id));
    }

    @PutMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceDto> suspend(@PathVariable String id) {
        return ResponseEntity.ok(deviceService.suspend(id));
    }

    @GetMapping("/{id}/health")
    public ResponseEntity<Map<String, Object>> health(@PathVariable String id) {
        return ResponseEntity.ok(deviceService.getHealth(id));
    }
}
