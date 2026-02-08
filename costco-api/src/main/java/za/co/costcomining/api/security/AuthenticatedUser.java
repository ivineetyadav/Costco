package za.co.costcomining.api.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedUser {
    private final String userId;
    private final String email;
    private final String role;
    private final String vendorId;
}
