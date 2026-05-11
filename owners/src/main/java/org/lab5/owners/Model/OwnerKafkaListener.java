package org.lab5.owners.Model;

import jakarta.persistence.EntityNotFoundException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.lab5.common.Dto.OwnerDto;
import org.lab5.common.Kafka.KafkaRequest;
import org.lab5.common.Kafka.KafkaResponse;
import org.lab5.owners.Service.Implementations.OwnerServiceImpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OwnerKafkaListener {
    private final OwnerServiceImpl ownerService;
    private final KafkaTemplate<String, OwnerDto> ownerKafkaTemplate;
    private final KafkaTemplate<String, List<OwnerDto>> ownerListKafkaTemplate;
    private final KafkaCommandDispatcher dispatcher;
    private final KafkaTemplate<String, KafkaResponse<?>> kafkaTemplate;


    public OwnerKafkaListener(OwnerServiceImpl ownerService,
                              KafkaTemplate<String, OwnerDto> ownerKafkaTemplate,
                              KafkaTemplate<String, List<OwnerDto>> ownerListKafkaTemplate,
                              KafkaCommandDispatcher dispatcher,
                              KafkaTemplate<String, KafkaResponse<?>> kafkaTemplate) {
        this.ownerService = ownerService;
        this.ownerKafkaTemplate = ownerKafkaTemplate;
        this.ownerListKafkaTemplate = ownerListKafkaTemplate;
        this.dispatcher = dispatcher;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = "owners-request",
            groupId = "owners-group",
            containerFactory = "kafkaRequestListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, KafkaRequest<?>> record) {

        KafkaRequest<?> request = record.value();

        KafkaResponse<?> response = dispatcher.dispatch(request);

        var header = record.headers().lastHeader(KafkaHeaders.CORRELATION_ID);
        if (header == null) {
            throw new IllegalStateException("Missing correlation ID in request header");
        }
        byte[] correlationId = header.value();

        ProducerRecord<String, KafkaResponse<?>> producerRecord = new ProducerRecord<>(
                "owners-responses",
                null,
                response
        );
        producerRecord.headers().add("correlationId", correlationId);

        kafkaTemplate.send(producerRecord);
    }

    @KafkaListener(
            topics = "owners-create",
            groupId = "owners-group",
            containerFactory = "ownerDtoContainerFactory"
    )
    public void listenCreate(OwnerDto ownerDto) {
        ownerService.createOwner(ownerDto);
    }

    @KafkaListener(
            topics = "owners-update",
            groupId = "owners-group",
            containerFactory = "ownerDtoContainerFactory"
    )
    public void listenUpdate(OwnerDto ownerDto) {
        ownerService.updateOwner(ownerDto);
    }

    @KafkaListener(
            topics = "owners-delete",
            groupId = "owners-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void listenDelete(ConsumerRecord<String, String> record) {
        Long id = Long.parseLong(record.value());
        ownerService.deleteOwner(id);
    }

    @KafkaListener(
            topics = "owners-getById",
            groupId = "owners-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void listenGetById(ConsumerRecord<String, String> consumerRecord) {
        Long id = Long.parseLong(consumerRecord.value());
        OwnerDto owner = ownerService.getOwnerById(id);

        var header = consumerRecord.headers().lastHeader("correlationId");
        if (header == null) throw new IllegalStateException("Missing correlation ID");


        Message<OwnerDto> message = MessageBuilder.withPayload(owner)
                .setHeader(KafkaHeaders.TOPIC, "owners-getById-response")
                .setHeader(KafkaHeaders.KEY, consumerRecord.key())
                .setHeader("correlationId", header.value())
                .build();

        ownerKafkaTemplate.send(message);
    }

    @KafkaListener(
            topics = "owners-getAll",
            groupId = "owners-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void listenGetAll(ConsumerRecord<String, String> consumerRecord) {
        var header = consumerRecord.headers().lastHeader("correlationId");
        if (header == null) throw new IllegalStateException("Missing correlation ID");

        List<OwnerDto> owners = ownerService.getAllOwners();

        Message<List<OwnerDto>> message = MessageBuilder.withPayload(owners)
                .setHeader(KafkaHeaders.TOPIC, "owners-getAll-response")
                .setHeader(KafkaHeaders.KEY, consumerRecord.key())
                .setHeader("correlationId", header.value())
                .build();

        ownerListKafkaTemplate.send(message);
    }

    @KafkaListener(
            topics = "owners-getByUsername",
            groupId = "owners-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void listenGetByUsername(ConsumerRecord<String, String> consumerRecord) {
        String rawUsername = consumerRecord.value();
        String username = rawUsername.trim().replace("\"", "");

        OwnerDto owner = ownerService.getOwnerByUsername(username);

        var header = consumerRecord.headers().lastHeader("correlationId");
        if (header == null) throw new IllegalStateException("Missing correlation ID");

        Message<OwnerDto> message = MessageBuilder.withPayload(owner)
                .setHeader(KafkaHeaders.TOPIC, "owners-getByUsername-response")
                .setHeader(KafkaHeaders.KEY, consumerRecord.key())
                .setHeader("correlationId", header.value())
                .build();

        ownerKafkaTemplate.send(message);
    }

}
