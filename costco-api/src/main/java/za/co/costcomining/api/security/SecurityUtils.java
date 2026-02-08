package za.co.costcomining.api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static AuthenticatedUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthenticatedUser) {
            return (AuthenticatedUser) auth.getPrincipal();
        }
        throw new IllegalStateException("No authenticated user found");
    }

    public static String getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    public static String getCurrentVendorId() {
        return getCurrentUser().getVendorId();
    }

    public static boolean isVendor() {
        return "VENDOR".equals(getCurrentUser().getRole());
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(getCurrentUser().getRole());
    }
}
