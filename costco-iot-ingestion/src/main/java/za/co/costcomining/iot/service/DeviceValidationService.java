package za.co.costcomining.iot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.costcomining.common.entity.Contract;
import za.co.costcomining.common.entity.Device;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceValidationService {

    private final DeviceQueryService deviceQueryService;

    public ValidationResult validate(String deviceSerial, String machineId) {
        // Layer 3, Check 1: Device exists and is ACTIVE
        Optional<Device> deviceOpt = deviceQueryService.findActiveDevice(deviceSerial);
        if (deviceOpt.isEmpty()) {
            log.warn("SECURITY: Rejected telemetry from unknown/inactive device: {}", deviceSerial);
            return ValidationResult.rejected("Device not found or not active: " + deviceSerial);
        }

        Device device = deviceOpt.get();

        // Layer 3, Check 2: Device-Machine mapping matches
        if (device.getMachineId() != null && !device.getMachineId().equals(machineId)) {
            log.warn("SECURITY: Device-machine mismatch. Device {} claims machine {} but is mapped to {}",
                    deviceSerial, machineId, device.getMachineId());
            return ValidationResult.rejected("Device-machine mismatch");
        }

        // Layer 3, Check 3: Machine has an active contract
        Optional<Contract> contractOpt = deviceQueryService.findActiveContract(
                device.getMachineId() != null ? device.getMachineId() : machineId,
                LocalDate.now());

        if (contractOpt.isEmpty()) {
            log.warn("Unbilled usage detected: Device {} on machine {} has no active contract",
                    deviceSerial, machineId);
            // Still accept the telemetry but flag it
            return ValidationResult.accepted(device, null, true);
        }

        return ValidationResult.accepted(device, contractOpt.get(), false);
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class ValidationResult {
        private final boolean accepted;
        private final String rejectionReason;
        private final Device device;
        private final Contract contract;
        private final boolean unbilledUsage;

        public static ValidationResult rejected(String reason) {
            return new ValidationResult(false, reason, null, null, false);
        }

        public static ValidationResult accepted(Device device, Contract contract, boolean unbilled) {
            return new ValidationResult(true, null, device, contract, unbilled);
        }
    }
}
