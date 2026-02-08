package za.co.costcomining.iot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import za.co.costcomining.common.entity.Telemetry;
import za.co.costcomining.common.util.UlidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeviceMessageParser {

    private final ObjectMapper objectMapper;

    public TelemetryMessage parse(String rawMessage) {
        try {
            JsonNode root = objectMapper.readTree(rawMessage);

            TelemetryMessage msg = new TelemetryMessage();
            msg.setDeviceId(getTextSafe(root, "deviceId"));
            msg.setMachineId(getTextSafe(root, "machineId"));
            msg.setRawJson(rawMessage);

            Telemetry telemetry = Telemetry.builder()
                    .id(UlidGenerator.generate())
                    .deviceId(msg.getDeviceId())
                    .machineId(msg.getMachineId())
                    .timestamp(parseTimestamp(root))
                    .engineRunning(getBooleanSafe(root, "engineRunning"))
                    .engineHours(getDecimalSafe(root, "engineHours"))
                    .fuelLevel(getDecimalSafe(root, "fuelLevel"))
                    .locationLat(getDecimalSafe(root, "locationLat"))
                    .locationLng(getDecimalSafe(root, "locationLng"))
                    .payloadJson(rawMessage)
                    .receivedAt(OffsetDateTime.now())
                    .build();

            msg.setTelemetry(telemetry);
            return msg;
        } catch (Exception e) {
            log.error("Failed to parse telemetry message: {}", rawMessage, e);
            throw new IllegalArgumentException("Invalid telemetry message format", e);
        }
    }

    private OffsetDateTime parseTimestamp(JsonNode root) {
        JsonNode tsNode = root.get("timestamp");
        if (tsNode != null && !tsNode.isNull()) {
            try {
                return OffsetDateTime.parse(tsNode.asText(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            } catch (Exception e) {
                // Try epoch millis
                try {
                    long millis = tsNode.asLong();
                    return OffsetDateTime.now().withNano(0); // fallback
                } catch (Exception ignored) {}
            }
        }
        return OffsetDateTime.now();
    }

    private String getTextSafe(JsonNode node, String field) {
        JsonNode child = node.get(field);
        return child != null && !child.isNull() ? child.asText() : null;
    }

    private Boolean getBooleanSafe(JsonNode node, String field) {
        JsonNode child = node.get(field);
        return child != null && !child.isNull() ? child.asBoolean() : null;
    }

    private BigDecimal getDecimalSafe(JsonNode node, String field) {
        JsonNode child = node.get(field);
        return child != null && !child.isNull() ? new BigDecimal(child.asText()) : null;
    }

    @lombok.Getter
    @lombok.Setter
    public static class TelemetryMessage {
        private String deviceId;
        private String machineId;
        private String rawJson;
        private Telemetry telemetry;
    }
}
