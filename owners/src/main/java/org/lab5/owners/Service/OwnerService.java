package org.lab5.owners.Service;

import org.lab5.common.Dto.OwnerDto;

import java.util.List;

public interface OwnerService {
    OwnerDto createOwner(OwnerDto ownerDto);
    OwnerDto updateOwner(OwnerDto ownerDto);
    void deleteOwner(Long id);
    OwnerDto getOwnerById(Long id);
    List<OwnerDto> getAllOwners();
}
