package za.co.costcomining.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.co.costcomining.common.entity.Contract;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, String> {
    List<Contract> findByVendorId(String vendorId);
    List<Contract> findByMachineId(String machineId);
    List<Contract> findByStatus(String status);

    @Query("SELECT c FROM Contract c WHERE c.machineId = :machineId AND c.status = 'ACTIVE' AND c.startDate <= :date AND (c.endDate IS NULL OR c.endDate >= :date)")
    Optional<Contract> findActiveContractForMachine(String machineId, LocalDate date);

    long countByStatus(String status);
}
