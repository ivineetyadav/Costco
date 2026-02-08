package za.co.costcomining.common.entity;

import jakarta.persistence.*;
import lombok.*;
import za.co.costcomining.common.util.UlidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "vendors")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Vendor {

    @Id
    @Column(length = 26)
    private String id;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "contact_name", length = 200)
    private String contactName;

    @Column(length = 200)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UlidGenerator.generate();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
