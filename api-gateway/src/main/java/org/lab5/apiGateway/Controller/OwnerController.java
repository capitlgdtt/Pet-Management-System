package org.lab5.apiGateway.Controller;

import jakarta.persistence.EntityNotFoundException;
import org.lab5.apiGateway.Service.KafkaOwnerService;
import org.lab5.common.Dto.OwnerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owners")
public class OwnerController {
    private final KafkaOwnerService ownerService;

    @Autowired
    public OwnerController(KafkaOwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> createOwner(@RequestBody OwnerDto ownerDto) {
        ownerService.createOwner(ownerDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.owner.id")
    @PutMapping("/{id}")
    public ResponseEntity<OwnerDto> updateOwner(@PathVariable Long id, @RequestBody OwnerDto ownerDto, Authentication authentication) {
        ownerDto.setId(id);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && ownerDto.getRole() != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }

        try {
            ownerService.updateOwner(ownerDto);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.owner.id")
    @DeleteMapping("/{id}")
    public ResponseEntity<OwnerDto> deleteOwner(@PathVariable Long id) {
        try {
            ownerService.deleteOwner(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.owner.id")
    @GetMapping("/{id}")
    public ResponseEntity<OwnerDto> getOwnerById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ownerService.getOwnerById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    @GetMapping("/username")
    public ResponseEntity<OwnerDto> getOwnerByName(@RequestParam String username) {
        try {
            return ResponseEntity.ok(ownerService.getOwnerByUsername(username));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<OwnerDto>> getAllOwners() {
        List<OwnerDto> owners = ownerService.getAllOwners();
        if (owners.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(owners);
    }
}
