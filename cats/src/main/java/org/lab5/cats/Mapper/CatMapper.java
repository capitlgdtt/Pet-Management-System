package org.lab5.cats.Mapper;

import org.lab5.common.Dto.CatDto;
import org.lab5.common.Model.Entities.Cat;
import org.lab5.common.Model.Entities.Owner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CatMapper {
    public CatDto toDto(Cat cat) {
        CatDto dto = new CatDto();
        dto.setId(cat.getId());
        dto.setName(cat.getName());
        dto.setDateOfBirth(cat.getDateOfBirth());
        dto.setBreed(cat.getBreed());
        dto.setColor(cat.getColor());
        dto.setTailLength(cat.getTailLength());
        dto.setOwnerId(cat.getOwner() != null
                ? cat.getOwner().getId()
                : null);
        dto.setFriendIds(cat.getFriends() != null
                ? cat.getFriends().stream().map(Cat::getId).toList()
                : null);
        return dto;
    }

    public Cat toEntity(CatDto dto, Owner owner, List<Cat> friends) {
        Cat cat = new Cat();
        cat.setName(dto.getName());
        cat.setDateOfBirth(dto.getDateOfBirth());
        cat.setBreed(dto.getBreed());
        cat.setColor(dto.getColor());
        cat.setTailLength(dto.getTailLength());
        cat.setOwner(owner);
        cat.setFriends(friends);
        return cat;
    }
}
