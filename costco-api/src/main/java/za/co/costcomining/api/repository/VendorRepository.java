package za.co.costcomining.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.costcomining.common.entity.Vendor;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, String> {
    Optional<Vendor> findByEmail(String email);
    Optional<Vendor> findByCompanyName(String companyName);
}
