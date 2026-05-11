package org.lab5.common.Repository;

import org.lab5.common.Model.Entities.Cat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CatRepository extends JpaRepository<Cat, Long> {
    Optional<Cat> findCatByName(String name);
    List<Cat> findAllByOwnerId(Long id);
    List<Cat> findAllByBreed(String breed);
    List<Cat> findAllByColor(String color);
    List<Cat> findAllByDateOfBirth(LocalDate dateOfBirth);
    Page<Cat> findByColorAndBreed(String color, String breed, Pageable pageable);
    Page<Cat> findByBreed(String breed, Pageable pageable);
    Page<Cat> findByColor(String color, Pageable pageable);
    List<Cat> findAllByTailLength(Integer tailLength);
    Page<Cat> findByTailLength(Integer tailLength, Pageable pageable);
}
