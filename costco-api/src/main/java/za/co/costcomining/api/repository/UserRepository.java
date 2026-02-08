package za.co.costcomining.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.costcomining.common.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
    List<User> findByRole(String role);
    List<User> findByVendorId(String vendorId);
    boolean existsByEmail(String email);
}
