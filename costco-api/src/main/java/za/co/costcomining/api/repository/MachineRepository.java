package za.co.costcomining.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.co.costcomining.common.entity.Machine;

import java.util.List;
import java.util.Optional;

public interface MachineRepository extends JpaRepository<Machine, String> {
    Optional<Machine> findBySerialNumber(String serialNumber);
    List<Machine> findByStatus(String status);
    long countByStatus(String status);

    @Query("SELECT m FROM Machine m JOIN Contract c ON c.machineId = m.id WHERE c.vendorId = :vendorId AND c.status = 'ACTIVE'")
    List<Machine> findByActiveVendor(String vendorId);
}
