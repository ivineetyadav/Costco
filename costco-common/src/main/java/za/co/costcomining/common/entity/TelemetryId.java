package za.co.costcomining.common.entity;

import lombok.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class TelemetryId implements Serializable {
    private String id;
    private OffsetDateTime timestamp;
}
