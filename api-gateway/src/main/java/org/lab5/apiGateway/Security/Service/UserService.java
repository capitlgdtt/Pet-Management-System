package org.lab5.apiGateway.Security.Service;

import org.lab5.apiGateway.Security.Model.UserDetailsImpl;
import org.lab5.common.Model.Entities.Owner;
import org.lab5.common.Model.Entities.Role;
import org.lab5.common.Repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final OwnerRepository ownerRepository;

    @Autowired
    public UserService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Owner owner = ownerRepository.findOwnerByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Owner not found: " + username));

        return new UserDetailsImpl(owner);
    }

    public void setAdminForUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Owner owner = ownerRepository.findOwnerByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Owner not found: " + username));

        owner.setRole(Role.ROLE_ADMIN);
        ownerRepository.save(owner);
    }
}
