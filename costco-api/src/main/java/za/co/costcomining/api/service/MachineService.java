package za.co.costcomining.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.costcomining.api.repository.MachineRepository;
import za.co.costcomining.api.security.SecurityUtils;
import za.co.costcomining.common.dto.MachineDto;
import za.co.costcomining.common.entity.Machine;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MachineService {

    private final MachineRepository machineRepository;

    public List<MachineDto> findAll() {
        List<Machine> machines;
        if (SecurityUtils.isVendor()) {
            machines = machineRepository.findByActiveVendor(SecurityUtils.getCurrentVendorId());
        } else {
            machines = machineRepository.findAll();
        }
        return machines.stream().map(this::toDto).toList();
    }

    public MachineDto findById(String id) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + id));
        return toDto(machine);
    }

    @Transactional
    public MachineDto create(MachineDto dto) {
        Machine machine = Machine.builder()
                .name(dto.getName())
                .type(dto.getType())
                .model(dto.getModel())
                .serialNumber(dto.getSerialNumber())
                .powerRating(dto.getPowerRating())
                .status(dto.getStatus() != null ? dto.getStatus() : "AVAILABLE")
                .build();
        return toDto(machineRepository.save(machine));
    }

    @Transactional
    public MachineDto update(String id, MachineDto dto) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + id));
        if (dto.getName() != null) machine.setName(dto.getName());
        if (dto.getType() != null) machine.setType(dto.getType());
        if (dto.getModel() != null) machine.setModel(dto.getModel());
        if (dto.getSerialNumber() != null) machine.setSerialNumber(dto.getSerialNumber());
        if (dto.getPowerRating() != null) machine.setPowerRating(dto.getPowerRating());
        if (dto.getStatus() != null) machine.setStatus(dto.getStatus());
        return toDto(machineRepository.save(machine));
    }

    @Transactional
    public void delete(String id) {
        machineRepository.deleteById(id);
    }

    private MachineDto toDto(Machine m) {
        return MachineDto.builder()
                .id(m.getId())
                .name(m.getName())
                .type(m.getType())
                .model(m.getModel())
                .serialNumber(m.getSerialNumber())
                .powerRating(m.getPowerRating())
                .status(m.getStatus())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
