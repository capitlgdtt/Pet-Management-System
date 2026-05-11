package org.lab5.apiGateway.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.lab5.common.Dto.CatDto;
import org.lab5.common.Dto.LocalDateDto;
import org.lab5.common.Dto.CatOwnerCheckRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

@Service
public class KafkaCatService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Map<String, CompletableFuture<Object>> pendingRequests = new ConcurrentHashMap<>();

    private static final String GROUP_ID = "api-gateway-cats-group";

    @Autowired
    public KafkaCatService(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    private <T> T sendAndReceive(String requestTopic, Object message, String responseTopic, Class<T> responseType) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Object> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        ProducerRecord<String, Object> record = new ProducerRecord<>(requestTopic, message);
        record.headers().add("correlationId", correlationId.getBytes(StandardCharsets.UTF_8));
        record.headers().add("replyTo", responseTopic.getBytes(StandardCharsets.UTF_8));

        kafkaTemplate.send(record);

        try {
            Object response = future.get(30, TimeUnit.SECONDS);
            return objectMapper.convertValue(response, responseType);
        } catch (Exception e) {
            pendingRequests.remove(correlationId);
            throw new RuntimeException("Kafka request timeout or error", e);
        }
    }

    private void completeRequest(ConsumerRecord<String, ?> record) {
        Headers headers = record.headers();
        byte[] correlationIdBytes = headers.lastHeader("correlationId").value();
        if (correlationIdBytes == null) return;

        String correlationId = new String(correlationIdBytes, StandardCharsets.UTF_8);
        CompletableFuture<Object> future = pendingRequests.remove(correlationId);
        if (future != null) {
            future.complete(record.value());
        }
    }

    @KafkaListener(
            topics = "cats-getById-response",
            groupId = GROUP_ID,
            containerFactory = "catKafkaListenerContainerFactory"
    )
    public void handleGetByIdResponse(ConsumerRecord<String, CatDto> record) {
        completeRequest(record);
    }

    @KafkaListener(
            topics = "cats-getAll-response",
            groupId = GROUP_ID,
            containerFactory = "catListKafkaListenerContainerFactory"
    )
    public void handleGetAllResponse(ConsumerRecord<String, List<CatDto>> record) {
        completeRequest(record);
    }

    @KafkaListener(
            topics = "cats-getByOwner-response",
            groupId = GROUP_ID,
            containerFactory = "catListKafkaListenerContainerFactory"
    )
    public void handleGetByOwnerResponse(ConsumerRecord<String, List<CatDto>> record) {
        completeRequest(record);
    }

    @KafkaListener(
            topics = "cats-getByName-response",
            groupId = GROUP_ID,
            containerFactory = "catListKafkaListenerContainerFactory"
    )
    public void handleGetByNameResponse(ConsumerRecord<String, List<CatDto>> record) {
        completeRequest(record);
    }

    @KafkaListener(
            topics = "cats-getByBreed-response",
            groupId = GROUP_ID,
            containerFactory = "catListKafkaListenerContainerFactory"
            )
    public void handleGetByBreedResponse(ConsumerRecord<String, List<CatDto>> record) {
        completeRequest(record);
    }

    @KafkaListener(
            topics = "cats-getByColor-response",
            groupId = GROUP_ID,
            containerFactory = "catListKafkaListenerContainerFactory"
    )
    public void handleGetByColorResponse(ConsumerRecord<String, List<CatDto>> record) {
        completeRequest(record);
    }

    @KafkaListener(
            topics = "cats-getByDateOfBirth-response",
            groupId = GROUP_ID,
            containerFactory = "catListKafkaListenerContainerFactory"
    )
    public void handleGetByDateOfBirthResponse(ConsumerRecord<String, List<CatDto>> record) {
        completeRequest(record);
    }

    @KafkaListener(
            topics = "cats-getByTailLength-response",
            groupId = GROUP_ID,
            containerFactory = "catListKafkaListenerContainerFactory"
    )
    public void handleGetByTailLengthResponse(ConsumerRecord<String, List<CatDto>> record) {
        completeRequest(record);
    }

    @KafkaListener(
            topics = "cats-isOwnerOfCat-response",
            groupId = GROUP_ID,
            containerFactory = "boolKafkaListenerContainerFactory"
    )
    public void handleIsOwnerOfCatResponse(ConsumerRecord<String, Boolean> record) {
        completeRequest(record);
    }

    public void createCat(CatDto catDto) {
        kafkaTemplate.send("cats-create", catDto);
    }

    public void updateCat(CatDto catDto) {
        kafkaTemplate.send("cats-update", catDto);
    }

    public void deleteCat(Long catId) {
        kafkaTemplate.send("cats-delete", catId);
    }

    public CatDto getCatById(Long id) {
        return sendAndReceive("cats-getById", id, "cats-getById-response", CatDto.class);
    }

    public List<CatDto> getAllCats() {
        String requestId = UUID.randomUUID().toString();
        return sendAndReceive("cats-getAll", requestId, "cats-getAll-response", List.class);
    }

    public List<CatDto> getCatsByOwnerId(Long ownerId) {
        return sendAndReceive("cats-getByOwner", ownerId, "cats-getByOwner-response", List.class);
    }

    public List<CatDto> getCatsByName(String name) {
        return sendAndReceive("cats-getByName", name, "cats-getByName-response", List.class);
    }

    public List<CatDto> getCatsByBreed(String breed) {
        return sendAndReceive("cats-getByBreed", breed, "cats-getByBreed-response", List.class);
    }

    public List<CatDto> getCatsByColor(String color) {
        return sendAndReceive("cats-getByColor", color, "cats-getByColor-response", List.class);
    }

    public List<CatDto> getCatsByDateOfBirth(LocalDateDto date) {
        return sendAndReceive("cats-getByDateOfBirth", date, "cats-getByDateOfBirth-response", List.class);
    }

    public List<CatDto> getCatsByTailLength(Integer length) {
        return sendAndReceive("cats-getByTailLength", length, "cats-getByTailLength-response", List.class);
    }

    public boolean isOwnerOfCat(Long catId, Long ownerId) {
        CatOwnerCheckRequest request = new CatOwnerCheckRequest(catId, ownerId);
        return sendAndReceive("cats-isOwnerOfCat", request, "cats-isOwnerOfCat-response", Boolean.class);
    }
}
