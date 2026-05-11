package org.lab5.common.Repository;

import org.lab5.common.Model.Entities.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    List<Owner> findAllByDateOfBirth(LocalDate dateOfBirth);
    Optional<Owner> findOwnerByUsername(String username);
    Boolean existsByUsername(String username);
}
