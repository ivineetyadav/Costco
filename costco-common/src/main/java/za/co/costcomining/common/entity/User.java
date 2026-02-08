package za.co.costcomining.common.entity;

import jakarta.persistence.*;
import lombok.*;
import za.co.costcomining.common.util.UlidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(length = 26)
    private String id;

    @Column(unique = true, nullable = false, length = 200)
    private String email;

    @Column(name = "full_name", length = 200)
    private String fullName;

    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(name = "vendor_id", length = 26)
    private String vendorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", insertable = false, updatable = false)
    private Vendor vendor;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UlidGenerator.generate();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
