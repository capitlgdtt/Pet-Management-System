package org.lab5.owners.Mapper;


import org.lab5.common.Dto.OwnerDto;
import org.lab5.common.Model.Entities.Cat;
import org.lab5.common.Model.Entities.Owner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OwnerMapper {
    public OwnerDto toDto(Owner owner) {
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setId(owner.getId());
        ownerDto.setUsername(owner.getUsername());
        ownerDto.setPassword(owner.getPassword());
        ownerDto.setRole(owner.getRole());
        ownerDto.setDateOfBirth(owner.getDateOfBirth());
        ownerDto.setCatIds(owner.getCats() != null
                ? owner.getCats().stream().map(Cat::getId).toList()
                : null);
        return ownerDto;
    }

    public Owner toEntity(OwnerDto ownerDto, List<Cat> cats) {
        Owner owner = new Owner();
        owner.setUsername(ownerDto.getUsername());
        owner.setPassword(ownerDto.getPassword());
        owner.setRole(ownerDto.getRole());
        owner.setDateOfBirth(ownerDto.getDateOfBirth());
        owner.setCats(cats);
        return owner;
    }
}
