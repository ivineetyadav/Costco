package za.co.costcomining.iot.service;

import za.co.costcomining.common.entity.Contract;
import za.co.costcomining.common.entity.Device;

import java.time.LocalDate;
import java.util.Optional;

public interface DeviceQueryService {
    Optional<Device> findActiveDevice(String deviceSerial);
    Optional<Contract> findActiveContract(String machineId, LocalDate date);
    void updateLastSeen(String deviceId);
    void saveTelemetry(za.co.costcomining.common.entity.Telemetry telemetry);
}
