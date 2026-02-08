package za.co.costcomining.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.costcomining.common.entity.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, String> {
    Optional<Device> findByDeviceSerial(String deviceSerial);
    Optional<Device> findByIotThingName(String iotThingName);
    Optional<Device> findByDeviceSerialAndStatus(String deviceSerial, String status);
    List<Device> findByMachineId(String machineId);
    List<Device> findByStatus(String status);
}
