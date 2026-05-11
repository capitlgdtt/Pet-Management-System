package org.lab5.apiGateway.Config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.lab5.common.Model.Entities.Owner;
import org.lab5.common.Model.Entities.Role;
import org.lab5.common.Repository.OwnerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer {

    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (ownerRepository.findOwnerByUsername("base-admin").isEmpty()) {
            Owner admin = new Owner();
            admin.setUsername("base-admin");
            admin.setPassword(passwordEncoder.encode("base-password"));
            admin.setRole(Role.ROLE_ADMIN);
            ownerRepository.save(admin);
            System.out.println("Admin user created: base-admin / base-password");
        }
    }
}
