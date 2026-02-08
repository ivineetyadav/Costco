package za.co.costcomining.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.costcomining.api.repository.ContractRepository;
import za.co.costcomining.api.repository.DeviceRepository;
import za.co.costcomining.api.repository.TelemetryRepository;
import za.co.costcomining.common.entity.Contract;
import za.co.costcomining.common.entity.Device;
import za.co.costcomining.common.entity.Telemetry;
import za.co.costcomining.iot.service.DeviceQueryService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceQueryServiceImpl implements DeviceQueryService {

    private final DeviceRepository deviceRepository;
    private final ContractRepository contractRepository;
    private final TelemetryRepository telemetryRepository;

    @Override
    public Optional<Device> findActiveDevice(String deviceSerial) {
        return deviceRepository.findByDeviceSerialAndStatus(deviceSerial, "ACTIVE");
    }

    @Override
    public Optional<Contract> findActiveContract(String machineId, LocalDate date) {
        return contractRepository.findActiveContractForMachine(machineId, date);
    }

    @Override
    @Transactional
    public void updateLastSeen(String deviceId) {
        deviceRepository.findById(deviceId).ifPresent(device -> {
            device.setLastSeenAt(LocalDateTime.now());
            deviceRepository.save(device);
        });
    }

    @Override
    @Transactional
    public void saveTelemetry(Telemetry telemetry) {
        telemetryRepository.save(telemetry);
    }
}
