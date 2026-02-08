package za.co.costcomining.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.costcomining.api.repository.VendorRepository;
import za.co.costcomining.common.dto.VendorDto;
import za.co.costcomining.common.entity.Vendor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;

    public List<VendorDto> findAll() {
        return vendorRepository.findAll().stream().map(this::toDto).toList();
    }

    public VendorDto findById(String id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found: " + id));
        return toDto(vendor);
    }

    @Transactional
    public VendorDto create(VendorDto dto) {
        Vendor vendor = Vendor.builder()
                .companyName(dto.getCompanyName())
                .contactName(dto.getContactName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .build();
        return toDto(vendorRepository.save(vendor));
    }

    @Transactional
    public VendorDto update(String id, VendorDto dto) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found: " + id));
        if (dto.getCompanyName() != null) vendor.setCompanyName(dto.getCompanyName());
        if (dto.getContactName() != null) vendor.setContactName(dto.getContactName());
        if (dto.getEmail() != null) vendor.setEmail(dto.getEmail());
        if (dto.getPhone() != null) vendor.setPhone(dto.getPhone());
        if (dto.getAddress() != null) vendor.setAddress(dto.getAddress());
        return toDto(vendorRepository.save(vendor));
    }

    @Transactional
    public void delete(String id) {
        vendorRepository.deleteById(id);
    }

    private VendorDto toDto(Vendor v) {
        return VendorDto.builder()
                .id(v.getId())
                .companyName(v.getCompanyName())
                .contactName(v.getContactName())
                .email(v.getEmail())
                .phone(v.getPhone())
                .address(v.getAddress())
                .createdAt(v.getCreatedAt())
                .build();
    }
}
