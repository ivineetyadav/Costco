package za.co.costcomining.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.co.costcomining.common.entity.Telemetry;
import za.co.costcomining.common.entity.TelemetryId;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public interface TelemetryRepository extends JpaRepository<Telemetry, TelemetryId> {

    @Query("SELECT t FROM Telemetry t WHERE t.machineId = :machineId AND t.timestamp BETWEEN :start AND :end ORDER BY t.timestamp")
    List<Telemetry> findByMachineIdAndTimestampBetween(String machineId, OffsetDateTime start, OffsetDateTime end);

    @Query("SELECT t FROM Telemetry t WHERE t.deviceId = :deviceId ORDER BY t.timestamp DESC LIMIT 1")
    Telemetry findLatestByDeviceId(String deviceId);

    @Query("SELECT COUNT(t) FROM Telemetry t WHERE t.deviceId = :deviceId")
    long countByDeviceId(String deviceId);

    @Query("SELECT COALESCE(SUM(CASE WHEN t.engineRunning = true THEN 1 ELSE 0 END) * 0.5 / 60.0, 0) FROM Telemetry t WHERE t.machineId = :machineId AND t.timestamp BETWEEN :start AND :end")
    BigDecimal calculateEngineHours(String machineId, OffsetDateTime start, OffsetDateTime end);
}
