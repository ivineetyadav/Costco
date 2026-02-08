package za.co.costcomining.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.costcomining.api.repository.ContractRepository;
import za.co.costcomining.api.repository.MachineRepository;
import za.co.costcomining.api.repository.VendorRepository;
import za.co.costcomining.api.security.SecurityUtils;
import za.co.costcomining.common.dto.ContractDto;
import za.co.costcomining.common.entity.Contract;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final MachineRepository machineRepository;
    private final VendorRepository vendorRepository;

    public List<ContractDto> findAll() {
        List<Contract> contracts;
        if (SecurityUtils.isVendor()) {
            contracts = contractRepository.findByVendorId(SecurityUtils.getCurrentVendorId());
        } else {
            contracts = contractRepository.findAll();
        }
        return contracts.stream().map(this::toDto).toList();
    }

    public ContractDto findById(String id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + id));
        return toDto(contract);
    }

    @Transactional
    public ContractDto create(ContractDto dto) {
        machineRepository.findById(dto.getMachineId())
                .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + dto.getMachineId()));
        vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found: " + dto.getVendorId()));

        Contract contract = Contract.builder()
                .machineId(dto.getMachineId())
                .vendorId(dto.getVendorId())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .ratePerHour(dto.getRatePerHour())
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "ZAR")
                .status("ACTIVE")
                .build();

        return toDto(contractRepository.save(contract));
    }

    @Transactional
    public ContractDto update(String id, ContractDto dto) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + id));
        if (dto.getStartDate() != null) contract.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) contract.setEndDate(dto.getEndDate());
        if (dto.getRatePerHour() != null) contract.setRatePerHour(dto.getRatePerHour());
        if (dto.getStatus() != null) contract.setStatus(dto.getStatus());
        return toDto(contractRepository.save(contract));
    }

    private ContractDto toDto(Contract c) {
        String machineName = machineRepository.findById(c.getMachineId())
                .map(m -> m.getName()).orElse(null);
        String vendorName = vendorRepository.findById(c.getVendorId())
                .map(v -> v.getCompanyName()).orElse(null);

        return ContractDto.builder()
                .id(c.getId())
                .machineId(c.getMachineId())
                .machineName(machineName)
                .vendorId(c.getVendorId())
                .vendorName(vendorName)
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .ratePerHour(c.getRatePerHour())
                .currency(c.getCurrency())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
