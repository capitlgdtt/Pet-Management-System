package org.lab5.owners.Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.lab5.common.Dto.OwnerDto;
import org.lab5.common.Kafka.KafkaRequest;
import org.lab5.common.Kafka.KafkaResponse;
import org.lab5.owners.Service.Implementations.OwnerServiceImpl;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaCommandDispatcher {
    private final OwnerServiceImpl ownerService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, KafkaRequestHandler> handlers = new HashMap<>();

    @PostConstruct
    public void init() {
        handlers.put("getAllOwners", payload -> {
            List<OwnerDto> owners = ownerService.getAllOwners();
            return new KafkaResponse<>("OK", owners);
        });

        handlers.put("createOwner", payload -> {
            OwnerDto dto = convert(payload, OwnerDto.class);
            return new KafkaResponse<>("OK", ownerService.createOwner(dto));
        });

        handlers.put("updateOwner", payload -> {
            OwnerDto dto = convert(payload, OwnerDto.class);
            return new KafkaResponse<>("OK", ownerService.updateOwner(dto));
        });

        handlers.put("deleteOwner", payload -> {
            Long id = convert(payload, Long.class);
            ownerService.deleteOwner(id);
            return new KafkaResponse<>("OK", null);
        });

        handlers.put("getOwnerById", payload -> {
            Long id = convert(payload, Long.class);
            return new KafkaResponse<>("OK", ownerService.getOwnerById(id));
        });

        handlers.put("getOwnerByUsername", payload -> {
            String username = convert(payload, String.class);
            return new KafkaResponse<>("OK", ownerService.getOwnerByUsername(username));
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
