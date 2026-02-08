package za.co.costcomining.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.costcomining.api.repository.DeviceRepository;
import za.co.costcomining.api.repository.MachineRepository;
import za.co.costcomining.api.repository.TelemetryRepository;
import za.co.costcomining.api.security.SecurityUtils;
import za.co.costcomining.common.dto.DeviceDto;
import za.co.costcomining.common.entity.Device;
import za.co.costcomining.common.entity.Telemetry;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final MachineRepository machineRepository;
    private final TelemetryRepository telemetryRepository;

    public List<DeviceDto> findAll() {
        return deviceRepository.findAll().stream().map(this::toDto).toList();
    }

    public DeviceDto findById(String id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + id));
        return toDto(device);
    }

    @Transactional
    public DeviceDto register(DeviceDto dto) {
        if (dto.getMachineId() != null) {
            machineRepository.findById(dto.getMachineId())
                    .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + dto.getMachineId()));
        }

        Device device = Device.builder()
                .machineId(dto.getMachineId())
                .deviceSerial(dto.getDeviceSerial())
                .iotThingName(dto.getIotThingName())
                .status("PENDING")
                .build();
        return toDto(deviceRepository.save(device));
    }

    @Transactional
    public DeviceDto approve(String id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + id));
        device.setStatus("ACTIVE");
        device.setApprovedBy(SecurityUtils.getCurrentUserId());
        device.setApprovedAt(LocalDateTime.now());
        return toDto(deviceRepository.save(device));
    }

    @Transactional
    public DeviceDto suspend(String id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + id));
        device.setStatus("SUSPENDED");
        return toDto(deviceRepository.save(device));
    }

    public Map<String, Object> getHealth(String id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + id));

        long messageCount = telemetryRepository.countByDeviceId(id);
        Telemetry latest = telemetryRepository.findLatestByDeviceId(id);

        Map<String, Object> health = new HashMap<>();
        health.put("deviceId", device.getId());
        health.put("status", device.getStatus());
        health.put("lastSeenAt", device.getLastSeenAt());
        health.put("totalMessages", messageCount);
        health.put("latestTelemetry", latest != null ? latest.getTimestamp() : null);
        return health;
    }

    private DeviceDto toDto(Device d) {
        String machineName = d.getMachineId() != null ?
                machineRepository.findById(d.getMachineId()).map(m -> m.getName()).orElse(null) : null;

        return DeviceDto.builder()
                .id(d.getId())
                .machineId(d.getMachineId())
                .machineName(machineName)
                .deviceSerial(d.getDeviceSerial())
                .iotThingName(d.getIotThingName())
                .status(d.getStatus())
                .approvedBy(d.getApprovedBy())
                .approvedAt(d.getApprovedAt())
                .lastSeenAt(d.getLastSeenAt())
                .createdAt(d.getCreatedAt())
                .build();
    }
}
