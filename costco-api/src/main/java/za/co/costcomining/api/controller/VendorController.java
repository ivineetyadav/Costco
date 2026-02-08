package za.co.costcomining.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.co.costcomining.api.service.VendorService;
import za.co.costcomining.common.dto.VendorDto;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @GetMapping
    public ResponseEntity<List<VendorDto>> findAll() {
        return ResponseEntity.ok(vendorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(vendorService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VendorDto> create(@RequestBody VendorDto dto) {
        return ResponseEntity.ok(vendorService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VendorDto> update(@PathVariable String id, @RequestBody VendorDto dto) {
        return ResponseEntity.ok(vendorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        vendorService.delete(id);
        return ResponseEntity.ok().build();
    }
}
