package org.lab5.cats.Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.lab5.cats.Service.Implementations.CatServiceImpl;
import org.lab5.common.Dto.CatDto;
import org.lab5.common.Dto.CatOwnerCheckRequest;
import org.lab5.common.Dto.LocalDateDto;
import org.lab5.common.Kafka.KafkaRequest;
import org.lab5.common.Kafka.KafkaResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaCommandDispatcher {
    private final CatServiceImpl catService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, KafkaRequestHandler> handlers = new HashMap<>();

    @PostConstruct
    public void init() {
        handlers.put("getAllCats", payload -> {
            var cats = catService.getAllCats();
            return new KafkaResponse<>("OK", cats);
        });

        handlers.put("createCat", payload -> {
            CatDto dto = convert(payload, CatDto.class);
            var cat = catService.createCat(dto);
            return new KafkaResponse<>("OK", cat);
        });

        handlers.put("updateCat", payload -> {
            CatDto dto = convert(payload, CatDto.class);
            return new KafkaResponse<>("OK", catService.updateCat(dto));
        });

        handlers.put("deleteCat", payload -> {
            Long id = convert(payload, Long.class);
            catService.deleteCat(id);
            return new KafkaResponse<>("OK", null);
        });

        handlers.put("getCatById", payload -> {
            Long id = convert(payload, Long.class);
            return new KafkaResponse<>("OK", catService.getCatById(id));
        });

        handlers.put("getCatsByOwner", payload -> {
            Long ownerId = convert(payload, Long.class);
            List<CatDto> cats = catService.getCatsByOwnerId(ownerId);
            return new KafkaResponse<>("OK", cats);
        });

        handlers.put("getCatsByName", payload -> {
            String name = convert(payload, String.class);
            List<CatDto> cats = catService.getCatsByName(name);
            return new KafkaResponse<>("OK", cats);
        });

        handlers.put("getCatsByBreed", payload -> {
            String breed = convert(payload, String.class);
            List<CatDto> cats = catService.getCatsByBreed(breed);
            return new KafkaResponse<>("OK", cats);
        });

        handlers.put("getCatsByColor", payload -> {
            String color = convert(payload, String.class);
            List<CatDto> cats = catService.getCatsByColor(color);
            return new KafkaResponse<>("OK", cats);
        });

        handlers.put("getCatsByDateOfBirth", payload -> {
            LocalDateDto date = convert(payload, LocalDateDto.class);
            List<CatDto> cats = catService.getCatsByDateOfBirth(date);
            return new KafkaResponse<>("OK", cats);
        });

        handlers.put("getCatsByTailLength", payload -> {
            Integer length = convert(payload, Integer.class);
            List<CatDto> cats = catService.getCatsByTailLength(length);
            return new KafkaResponse<>("OK", cats);
        });

        handlers.put("isOwnerOfCat", payload -> {
            CatOwnerCheckRequest req = convert(payload, CatOwnerCheckRequest.class);
            boolean result = catService.isOwnerOfCat(req.getCatId(), req.getOwnerId());
            return new KafkaResponse<>("OK", result);
        });
    }

    public KafkaResponse<?> dispatch(KafkaRequest<?> request) {
        KafkaRequestHandler handler = handlers.get(request.getCommand());
        if (handler == null) {
            return new KafkaResponse<>("ERROR", null, "Unknown command: " + request.getCommand());
        }
        try {
            return handler.handle(request.getPayload());
        } catch (Exception e) {
            return new KafkaResponse<>("ERROR", null, e.getMessage());
        }
    }

    private <T> T convert(Object payload, Class<T> clazz) {
        return objectMapper.convertValue(payload, clazz);
    }
}
