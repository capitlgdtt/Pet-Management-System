package org.lab5.cats.Service.Implementations;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.lab5.cats.Mapper.CatMapper;
import org.lab5.cats.Service.CatService;
import org.lab5.common.Dto.CatDto;
import org.lab5.common.Dto.LocalDateDto;
import org.lab5.common.Model.Entities.Cat;
import org.lab5.common.Model.Entities.Owner;
import org.lab5.common.Repository.CatRepository;
import org.lab5.common.Repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatServiceImpl implements CatService {
    private final OwnerRepository ownerRepository;
    private final CatMapper catMapper;
    private final CatRepository catRepository;

    @Autowired
    public CatServiceImpl(OwnerRepository ownerRepository, CatMapper catMapper, CatRepository catRepository) {
        this.ownerRepository = ownerRepository;
        this.catMapper = catMapper;
        this.catRepository = catRepository;
    }
    @Override
    public CatDto createCat(CatDto catDto) {
        Owner owner = ownerRepository.findById(catDto.getOwnerId())
                .orElseThrow(() -> new EntityNotFoundException("Owner not found with ID: " + catDto.getOwnerId()));

        List<Cat> friends = getFriendsFromIds(catDto.getFriendIds());

        Cat cat = catMapper.toEntity(catDto, owner, friends);
        return catMapper.toDto(catRepository.save(cat));
    }

    @Override
    public CatDto updateCat(CatDto catDto) {
        Cat oldCat = catRepository.findById(catDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cat not found with ID: " + catDto.getId()));

        Owner owner = null;
        if (catDto.getOwnerId() != null) {
            owner = ownerRepository.findById(catDto.getOwnerId())
                    .orElseThrow(() -> new EntityNotFoundException("Owner not found with ID: " + catDto.getOwnerId()));
        } else {
            owner = oldCat.getOwner();
        }

        List<Cat> friends = getFriendsFromIds(catDto.getFriendIds());

        if (catDto.getName() != null) oldCat.setName(catDto.getName());
        if (catDto.getDateOfBirth() != null) oldCat.setDateOfBirth(catDto.getDateOfBirth());
        if (catDto.getBreed() != null) oldCat.setBreed(catDto.getBreed());
        if (catDto.getColor() != null) oldCat.setColor(catDto.getColor());

        oldCat.setOwner(owner);
        oldCat.setFriends(friends);

        return catMapper.toDto(catRepository.save(oldCat));
    }

    @Override
    public void deleteCat(Long id) {
        if (!catRepository.existsById(id)) {
            throw new EntityNotFoundException("Cat not found with ID: " + id);
        }

        catRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CatDto getCatById(Long id) {
        return catRepository.findById(id)
                .map(catMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Cat not found with ID: " + id));
    }

    @Override
    @Transactional
    public List<CatDto> getAllCats() {
        return catRepository.findAll()
                .stream()
                .map(catMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<Cat> getFriendsFromIds(List<Long> friendIds) {
        if (friendIds == null) return Collections.emptyList();

        return friendIds.stream()
                .map(friendId -> catRepository.findById(friendId)
                        .orElseThrow(() -> new EntityNotFoundException("Friend cat not found with ID: " + friendId)))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<CatDto> getCatsByName(String name) {
        return catRepository.findCatByName(name).stream()
                .map(catMapper::toDto)
                .toList();
    }

    @Transactional
    public List<CatDto> getCatsByBreed(String breed) {
        return catRepository.findAllByBreed(breed).stream()
                .map(catMapper::toDto)
                .toList();
    }

    @Transactional
    public List<CatDto> getCatsByColor(String color) {
        return catRepository.findAllByColor(color).stream()
                .map(catMapper::toDto)
                .toList();
    }

    @Transactional
    public List<CatDto> getCatsByDateOfBirth(LocalDateDto dateOfBirth) {
        return catRepository.findAllByDateOfBirth(dateOfBirth.toLocalDate()).stream()
                .map(catMapper::toDto)
                .toList();
    }

    @Transactional
    public List<CatDto> getCatsByOwnerId(Long ownerId) {
        return catRepository.findAllByOwnerId(ownerId).stream()
                .map(catMapper::toDto)
                .toList();
    }

    public Page<CatDto> getCatsByPage(Pageable pageable) {
        return catRepository.findAll(pageable)
                .map(catMapper::toDto);
    }

    public Page<CatDto> getCatsByColorAndBreed(String color, String breed, Pageable pageable) {
        Page<Cat> cats = catRepository.findByColorAndBreed(color, breed, pageable);
        return cats.map(catMapper::toDto);
    }

    public Page<CatDto> getCatsByColorFilter(String color, Pageable pageable) {
        Page<Cat> cats = catRepository.findByColor(color, pageable);
        return cats.map(catMapper::toDto);
    }

    public Page<CatDto> getCatsByBreedFilter(String breed, Pageable pageable) {
        Page<Cat> cats = catRepository.findByBreed(breed, pageable);
        return cats.map(catMapper::toDto);
    }

    @Transactional
    public List<CatDto> getCatsByTailLength(Integer length) {
        return catRepository.findAllByTailLength(length).stream()
                .map(catMapper::toDto)
                .toList();
    }

    public Page<CatDto> getCatsByTailLengthFilter(Integer length, Pageable pageable) {
        Page<Cat> cats = catRepository.findByTailLength(length, pageable);
        return cats.map(catMapper::toDto);
    }

    public boolean isOwnerOfCat(Long catId, Long userId) {
        return catRepository.findById(catId)
                .map(cat -> cat.getOwner() != null && cat.getOwner().getId().equals(userId))
                .orElse(false);
    }
}
