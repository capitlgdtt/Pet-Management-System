package org.lab5.cats.Service;

import org.lab5.common.Dto.CatDto;

import java.util.List;

public interface CatService {
    CatDto createCat(CatDto catDto);
    CatDto updateCat(CatDto catDto);
    void deleteCat(Long id);
    CatDto getCatById(Long id);
    List<CatDto> getAllCats();
}
