package org.lab5.owners.Service.Implementations;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.lab5.common.Dto.OwnerDto;
import org.lab5.common.Model.Entities.Cat;
import org.lab5.common.Model.Entities.Owner;
import org.lab5.common.Repository.CatRepository;
import org.lab5.common.Repository.OwnerRepository;
import org.lab5.owners.Mapper.OwnerMapper;
import org.lab5.owners.Service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OwnerServiceImpl implements OwnerService {
    private final OwnerRepository ownerRepository;
    private final CatRepository catRepository;
    private final OwnerMapper ownerMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public OwnerServiceImpl(OwnerRepository ownerRepository, CatRepository catRepository, OwnerMapper ownerMapper, PasswordEncoder passwordEncoder) {
        this.ownerRepository = ownerRepository;
        this.catRepository = catRepository;
        this.ownerMapper = ownerMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OwnerDto createOwner(OwnerDto ownerDto) {
        List<Cat> cats = getCatsFromIds(ownerDto.getCatIds());
        Owner owner = ownerMapper.toEntity(ownerDto, cats);

        owner.setPassword(passwordEncoder.encode(ownerDto.getPassword()));

        return ownerMapper.toDto(ownerRepository.save(owner));
    }

    @Override
    public OwnerDto updateOwner(OwnerDto ownerDto) {
        Owner oldOwner = ownerRepository.findById(ownerDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Owner not found with ID: " + ownerDto.getId()));

        List<Cat> cats = null;
        if (ownerDto.getCatIds() != null && !ownerDto.getCatIds().isEmpty()) {
            cats = getCatsFromIds(ownerDto.getCatIds());
        } else {
            cats = oldOwner.getCats();
        }

        if (ownerDto.getRole() != null) oldOwner.setRole(ownerDto.getRole());
        if (ownerDto.getUsername() != null) oldOwner.setUsername(ownerDto.getUsername());
        if (ownerDto.getDateOfBirth() != null) oldOwner.setDateOfBirth(ownerDto.getDateOfBirth());

        oldOwner.setCats(cats);

        return ownerMapper.toDto(ownerRepository.save(oldOwner));
    }

    @Override
    public void deleteOwner(Long id) {

        if (!ownerRepository.existsById(id)) {
            throw new EntityNotFoundException("Owner not found with ID: " + id);
        }

        ownerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public OwnerDto getOwnerById(Long id) {
        return ownerRepository.findById(id)
                .map(ownerMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found with ID: " + id));
    }

    @Override
    @Transactional
    public List<OwnerDto> getAllOwners() {
        return ownerRepository.findAll()
                .stream()
                .map(ownerMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<Cat> getCatsFromIds(List<Long> catIds) {
        if (catIds == null) return Collections.emptyList();

        return catIds.stream()
                .map(id -> catRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Cat not found with ID: " + id)))
                .collect(Collectors.toList());
    }

    @Transactional
    public OwnerDto getOwnerByUsername(String username) throws EntityNotFoundException {
        return ownerRepository.findOwnerByUsername(username)
                .map(ownerMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found with name: " + username));
    }

    public List<OwnerDto> getOwnersByDateOfBirth(LocalDate dateOfBirth) {
        return ownerRepository.findAllByDateOfBirth(dateOfBirth)
                .stream()
                .map(ownerMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<OwnerDto> getOwnersByPage(Pageable pageable) {
        return ownerRepository.findAll(pageable)
                .map(ownerMapper::toDto);
    }
}
