package za.co.costcomining.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.costcomining.iot.service.DeviceMessageParser;
import za.co.costcomining.iot.service.DeviceQueryService;
import za.co.costcomining.iot.service.DeviceValidationService;

import java.util.Map;

@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
@Slf4j
public class TelemetryController {

    private final DeviceMessageParser messageParser;
    private final DeviceValidationService validationService;
    private final DeviceQueryService deviceQueryService;

    @PostMapping("/ingest")
    public ResponseEntity<?> ingestTelemetry(@RequestBody String rawMessage) {
        try {
            DeviceMessageParser.TelemetryMessage telemetryMsg = messageParser.parse(rawMessage);

            DeviceValidationService.ValidationResult result =
                    validationService.validate(telemetryMsg.getDeviceId(), telemetryMsg.getMachineId());

            if (!result.isAccepted()) {
                log.warn("Rejected telemetry: {}", result.getRejectionReason());
                // Still save for local dev - just log the warning
            }

            deviceQueryService.saveTelemetry(telemetryMsg.getTelemetry());

            if (result.isAccepted() && result.getDevice() != null) {
                deviceQueryService.updateLastSeen(result.getDevice().getId());
            }

            log.info("Ingested telemetry for device: {}, machine: {}",
                    telemetryMsg.getDeviceId(), telemetryMsg.getMachineId());

            return ResponseEntity.ok(Map.of("status", "accepted"));
        } catch (Exception e) {
            log.error("Failed to ingest telemetry", e);
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
