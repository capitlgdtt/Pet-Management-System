package org.lab5.apiGateway.Service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.lab5.common.Dto.OwnerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class KafkaOwnerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTemplate<String, OwnerDto> ownerDtoKafkaTemplate;

    private final Map<String, CompletableFuture<OwnerDto>> pendingOwnerRequests = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<List<OwnerDto>>> pendingOwnerListRequests = new ConcurrentHashMap<>();

    private static final String GROUP_ID = "api-gateway-owners-group";

    @Autowired
    public KafkaOwnerService(KafkaTemplate<String, Object> kafkaTemplate,
                             KafkaTemplate<String, OwnerDto> ownerDtoKafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.ownerDtoKafkaTemplate = ownerDtoKafkaTemplate;
    }

    public void createOwner(OwnerDto ownerDto) {
        kafkaTemplate.send("owners-create", ownerDto);
    }

    public void updateOwner(OwnerDto ownerDto) {
        kafkaTemplate.send("owners-update", ownerDto);
    }

    public void deleteOwner(Long ownerId) {
        kafkaTemplate.send("owners-delete", ownerId);
    }

    public OwnerDto getOwnerById(Long id) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<OwnerDto> future = new CompletableFuture<>();
        pendingOwnerRequests.put(correlationId, future);

        ProducerRecord<String, Object> record = new ProducerRecord<>("owners-getById", id);
        record.headers().add("correlationId", correlationId.getBytes(StandardCharsets.UTF_8));
        record.headers().add("replyTo", "owners-getById-response".getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(record);

        try {
            return future.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            pendingOwnerRequests.remove(correlationId);
            throw new RuntimeException("Kafka getOwnerById timeout or error", e);
        }
    }

    public List<OwnerDto> getAllOwners() {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<List<OwnerDto>> future = new CompletableFuture<>();
        pendingOwnerListRequests.put(correlationId, future);

        ProducerRecord<String, Object> record = new ProducerRecord<>("owners-getAll", correlationId);
        record.headers().add("correlationId", correlationId.getBytes(StandardCharsets.UTF_8));
        record.headers().add("replyTo", "owners-getAll-response".getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(record);

        try {
            return future.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            pendingOwnerListRequests.remove(correlationId);
            throw new RuntimeException("Kafka getAllOwners timeout or error", e);
        }
    }

    public OwnerDto getOwnerByUsername(String username) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<OwnerDto> future = new CompletableFuture<>();
        pendingOwnerRequests.put(correlationId, future);

        ProducerRecord<String, Object> record = new ProducerRecord<>("owners-getByUsername", username);
        record.headers().add("correlationId", correlationId.getBytes(StandardCharsets.UTF_8));
        record.headers().add("replyTo", "owners-getByUsername-response".getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(record);

        try {
            return future.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            pendingOwnerRequests.remove(correlationId);
            throw new RuntimeException("Kafka getOwnerByUsername timeout or error", e);
        }
    }

    @KafkaListener(
            topics = "owners-getById-response",
            groupId = GROUP_ID,
            containerFactory = "ownerKafkaListenerContainerFactory"
    )
    public void handleGetByIdResponse(ConsumerRecord<String, OwnerDto> record) {
        completeOwnerRequest(record);
    }

    @KafkaListener(
            topics = "owners-getAll-response",
            groupId = GROUP_ID,
            containerFactory = "ownerListKafkaListenerContainerFactory"
    )
    public void handleGetAllResponse(ConsumerRecord<String, List<OwnerDto>> record) {
        completeOwnerListRequest(record);
    }

    @KafkaListener(
            topics = "owners-getByUsername-response",
            groupId = GROUP_ID,
            containerFactory = "ownerKafkaListenerContainerFactory"
    )
    public void handleGetByUsernameResponse(ConsumerRecord<String, OwnerDto> record) {
        completeOwnerRequest(record);
    }

    private void completeOwnerRequest(ConsumerRecord<String, OwnerDto> record) {
        String correlationId = extractCorrelationId(record.headers());
        if (correlationId == null) return;

        CompletableFuture<OwnerDto> future = pendingOwnerRequests.remove(correlationId);
        if (future != null) {
            OwnerDto owner = record.value();
            if (owner != null) {
                future.complete(owner);
            } else {
                future.completeExceptionally(new NoSuchElementException("Owner not found"));
            }
        }
    }

    private void completeOwnerListRequest(ConsumerRecord<String, List<OwnerDto>> record) {
        String correlationId = extractCorrelationId(record.headers());
        if (correlationId == null) return;

        CompletableFuture<List<OwnerDto>> future = pendingOwnerListRequests.remove(correlationId);
        if (future != null) {
            List<OwnerDto> owners = record.value();
            if (owners != null) {
                future.complete(owners);
            } else {
                future.completeExceptionally(new IllegalStateException("Owner list is null"));
            }
        }
    }

    private String extractCorrelationId(Headers headers) {
        if (headers == null) return null;
        var header = headers.lastHeader("correlationId");
        if (header == null) return null;
        return new String(header.value(), StandardCharsets.UTF_8);
    }

}
