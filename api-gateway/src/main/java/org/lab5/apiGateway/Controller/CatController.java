package org.lab5.apiGateway.Controller;


import jakarta.persistence.EntityNotFoundException;
import org.lab5.apiGateway.Security.Model.UserDetailsImpl;
import org.lab5.apiGateway.Service.KafkaCatService;
import org.lab5.common.Dto.CatDto;
import org.lab5.common.Dto.LocalDateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cats")
public class CatController {
    private final KafkaCatService catService;

    @Autowired
    public CatController(KafkaCatService catService) {
        this.catService = catService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CatDto> createCat(@RequestBody CatDto catDto, Authentication authentication) {
        try {
            if (authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"))) {
                Long currentUserId = ((UserDetailsImpl) authentication.getPrincipal()).getOwner().getId();
                catDto.setOwnerId(currentUserId);
            } else if (authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                if (catDto.getOwnerId() == null) {
                    throw new IllegalArgumentException("Admin must specify ownerId");
                }
            }

            catService.createCat(catDto);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatDto> updateCat(@PathVariable Long id, @RequestBody CatDto catDto, Authentication authentication) {
        Long currentUserId = ((UserDetailsImpl) authentication.getPrincipal()).getOwner().getId();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            boolean isOwner = catService.isOwnerOfCat(id, currentUserId);
            if (!isOwner) {
                return ResponseEntity.status(403).build();
            }
        }

        catDto.setId(id);
        try {
            catService.updateCat(catDto);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCat(@PathVariable Long id, Authentication authentication) {
        Long currentUserId = ((UserDetailsImpl) authentication.getPrincipal()).getOwner().getId();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            boolean isOwner = catService.isOwnerOfCat(id, currentUserId);
            if (!isOwner) {
                return ResponseEntity.status(403).build();
            }
        }

        try {
            catService.deleteCat(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CatDto> getCatById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(catService.getCatById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<CatDto>> getCatsByOwner(@PathVariable Long ownerId) {
        List<CatDto> cats = catService.getCatsByOwnerId(ownerId);
        if (cats.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cats);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/name")
    public ResponseEntity<List<CatDto>> getCatsByName(@RequestParam String name) {
        List<CatDto> cats = catService.getCatsByName(name);
        return cats.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(cats);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/breed")
    public ResponseEntity<List<CatDto>> getCatsByBreed(@RequestParam String breed) {
        List<CatDto> cats = catService.getCatsByBreed(breed);
        return cats.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(cats);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/color")
    public ResponseEntity<List<CatDto>> getCatsByColor(@RequestParam String color) {
        List<CatDto> cats = catService.getCatsByColor(color);
        return cats.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(cats);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/dob")
    public ResponseEntity<List<CatDto>> getCatsByDateOfBirth(@RequestParam LocalDate date) {
        LocalDateDto dto = new LocalDateDto(date);
        List<CatDto> cats = catService.getCatsByDateOfBirth(dto);
        return cats.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(cats);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<CatDto>> getAllCats() {
        List<CatDto> cats = catService.getAllCats();
        if (cats.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cats);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/tail-length")
    public ResponseEntity<List<CatDto>> getCatsByTailLength(@RequestParam Integer length) {
        List<CatDto> cats = catService.getCatsByTailLength(length);
        return cats.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(cats);
    }

}