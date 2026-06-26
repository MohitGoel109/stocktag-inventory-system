package com.inventory.api.config;

import com.inventory.api.entity.AppUser;
import com.inventory.api.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates a default ADMIN account on first boot if no users exist yet,
 * so a fresh clone of this project is usable immediately without manually
 * inserting a bcrypt hash into the database.
 *
 * The default password is intentionally NOT hardcoded as a hash in source —
 * it's read from an environment variable (or falls back to a clearly-labeled
 * default) and hashed at startup, then the password is never persisted as plaintext.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin-email:admin@inventory.local}")
    private String defaultAdminEmail;

    @Value("${app.bootstrap.admin-password:Admin@12345}")
    private String defaultAdminPassword;

    @Override
    public void run(String... args) {
        if (appUserRepository.count() > 0) {
            return; // Already initialized.
        }

        AppUser admin = AppUser.builder()
                .name("System Administrator")
                .email(defaultAdminEmail)
                .passwordHash(passwordEncoder.encode(defaultAdminPassword))
                .role(AppUser.Role.ADMIN)
                .status(AppUser.UserStatus.ACTIVE)
                .build();

        appUserRepository.save(admin);

        System.out.println("============================================================");
        System.out.println(" First run detected — created default admin account:");
        System.out.println("   Email:    " + defaultAdminEmail);
        System.out.println("   Password: " + defaultAdminPassword);
        System.out.println(" Please log in and change this password immediately,");
        System.out.println(" or set APP_BOOTSTRAP_ADMIN_PASSWORD before first run.");
        System.out.println("============================================================");
    }
}
