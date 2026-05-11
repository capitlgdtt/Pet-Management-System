package org.lab5.cats.Model;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.lab5.cats.Service.Implementations.CatServiceImpl;
import org.lab5.common.Dto.CatDto;
import org.lab5.common.Dto.CatOwnerCheckRequest;
import org.lab5.common.Dto.LocalDateDto;
import org.lab5.common.Kafka.KafkaRequest;
import org.lab5.common.Kafka.KafkaResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CatKafkaListener {
    private final CatServiceImpl catService;
    private final KafkaTemplate<String, CatDto> catDtoKafkaTemplate;
    private final KafkaTemplate<String, List<CatDto>> catListKafkaTemplate;
    private final KafkaTemplate<String, Boolean> booleanKafkaTemplate;
    private final KafkaCommandDispatcher dispatcher;

    public CatKafkaListener(
            CatServiceImpl catService,
            KafkaTemplate<String, CatDto> catDtoKafkaTemplate,
            KafkaTemplate<String, List<CatDto>> catListKafkaTemplate,
            KafkaTemplate<String, Boolean> booleanKafkaTemplate,
            KafkaCommandDispatcher dispatcher
    ) {
        this.catService = catService;
        this.catDtoKafkaTemplate = catDtoKafkaTemplate;
        this.catListKafkaTemplate = catListKafkaTemplate;
        this.booleanKafkaTemplate = booleanKafkaTemplate;
        this.dispatcher = dispatcher;
    }

    @KafkaListener(topics = "cats-requests", groupId = "cats-group")
    public KafkaResponse<?> listen(KafkaRequest<?> request) {
        return dispatcher.dispatch(request);
    }

    @KafkaListener(
            topics = "cats-create",
            groupId = "cats-group",
            containerFactory = "catDtoKafkaListenerContainerFactory"
    )
    public void listenCreate(CatDto catDto) {
        catService.createCat(catDto);
    }

    @KafkaListener(
            topics = "cats-update",
            groupId = "cats-group",
            containerFactory = "catDtoKafkaListenerContainerFactory"
    )
    public void listenUpdate(CatDto catDto) {
        catService.updateCat(catDto);
    }

    @KafkaListener(
            topics = "cats-delete",
            groupId = "cats-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void listenDelete(ConsumerRecord<String, String> record) {
        Long id = Long.parseLong(record.value());
        catService.deleteCat(id);
    }

    @KafkaListener(
            topics = "cats-getById",
            groupId = "cats-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void listenGetById(ConsumerRecord<String, String> record) {
        Long catId = Long.parseLong(record.value());
        CatDto cat = catService.getCatById(catId);

        var header = record.headers().lastHeader("correlationId");
        if (header == null) throw new IllegalStateException("Missing correlation ID");

        Message<CatDto> message = MessageBuilder.withPayload(cat)
                .setHeader(KafkaHeaders.TOPIC, "cats-getById-response")
                .setHeader(KafkaHeaders.KEY, record.key())
                .setHeader("correlationId", header.value())
                .build();

        catDtoKafkaTemplate.send(message);
    }

    @KafkaListener(
            topics = "cats-getAll",
            groupId = "cats-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void listenGetAll(ConsumerRecord<String, String> record) {
        List<CatDto> cats = catService.getAllCats();

        var header = record.headers().lastHeader("correlationId");
        if (header == null) throw new IllegalStateException("Missing correlation ID");

        Message<List<CatDto>> message = MessageBuilder.withPayload(cats)
                .setHeader(KafkaHeaders.TOPIC, "cats-getAll-response")
                .setHeader(KafkaHeaders.KEY, record.key())
                .setHeader("correlationId", header.value())
                .build();

        catListKafkaTemplate.send(message);
    }

    @KafkaListener(
            topics = "cats-getByOwner",
            groupId = "cats-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void listenGetByOwner(ConsumerRecord<String, String> record) {
        Long ownerId = Long.parseLong(record.value());
        List<CatDto> cats = catService.getCatsByOwnerId(ownerId);

        var header = record.headers().lastHeader("correlationId");
        if (header == null) throw new IllegalStateException("Missing correlation ID");

        Message<List<CatDto>> message = MessageBuilder.withPayload(cats)
                .setHeader(KafkaHeaders.TOPIC, "cats-getByOwner-response")
                .setHeader(KafkaHeaders.KEY, record.key())
                .setHeader("correlationId", header.value())
                .build();

        catListKafkaTemplate.send(message);
    }

    @KafkaListener(
            topics = "cats-getByName",
            groupId = "cats-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void listenGetByName(ConsumerRecord<String, String> record) {
        String name = record.value().trim().replace("\"", "");
        List<CatDto> cats = catService.getCatsByName(name);

        var header = record.headers().lastHeader("correlationId");
        if (header == null) throw new IllegalStateException("Missing correlation ID");

        Message<List<CatDto>> message = MessageBuilder.withPayload(cats)
                .setHeader(KafkaHeaders.TOPIC, "cats-getByName-response")
                .setHeader(KafkaHeaders.KEY, record.key())
                .setHeader("correlationId", header.value())
                .build();

        catListKafkaTemplate.send(message);
    }

    @KafkaListener(
            topics = "cats-getByBreed",
            groupId = "cats-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void listenGetByBreed(ConsumerRecord<String, String> record) {
        String breed = record.value().trim().replace("\"", "");
        List<CatDto> cats = catService.getCatsByBreed(breed);

        var header = record.headers().lastHeader("correlationId");
        if (header == null) throw new IllegalStateException("Missing correlation ID");

        Message<List<CatDto>> message = MessageBuilder.withPayload(cats)
                .setHeader(KafkaHeaders.TOPIC, "cats-getByBreed-response")
                .setHeader(KafkaHeaders.KEY, record.key())
                .setHeader("correlationId", header.value())
                .build();

        catListKafkaTemplate.send(message);
    }

    @KafkaListener(
            topics = "cats-getByColor",
            groupId = "cats-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void listenGetByColor(ConsumerRecord<String, String> record) {
        String color = record.value().trim().replace("\"", "");
        List<CatDto> cats = catService.getCatsByColor(color);

        var header = record.headers().lastHeader("correlationId");
        if (header == null) throw new IllegalStateException("Missing correlation ID");

        Message<List<CatDto>> message = MessageBuilder.withPayload(cats)
                .setHeader(KafkaHeaders.TOPIC, "cats-getByColor-response")
                .setHeader(KafkaHeaders.KEY, record.key())
                .setHeader("correlationId", header.value())
                .build();

        catListKafkaTemplate.send(message);
    }

    @KafkaListener(
            topics = "cats-getByDateOfBirth",
            groupId = "cats-group",
            containerFactory = "localDateKafkaListenerContainerFactory"
    )
    public void listenGetByDateOfBirth(ConsumerRecord<String, LocalDateDto> record) {
        List<CatDto> cats = catService.getCatsByDateOfBirth(record.value());

        var header = record.headers().lastHeader("correlationId");
        if (header == null) throw new IllegalStateException("Missing correlation ID");

        Message<List<CatDto>> message = MessageBuilder.withPayload(cats)
                .setHeader(KafkaHeaders.TOPIC, "cats-getByDateOfBirth-response")
                .setHeader(KafkaHeaders.KEY, record.key())
                .setHeader("correlationId", header.value())
                .build();

        catListKafkaTemplate.send(message);
    }

    @KafkaListener(
            topics = "cats-getByTailLength",
            groupId = "cats-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void listenGetByTailLength(ConsumerRecord<String, String> record) {
        Integer tailLength = Integer.parseInt(record.value().trim().replace("\"", ""));
        List<CatDto> cats = catService.getCatsByTailLength(tailLength);

        var header = record.headers().lastHeader("correlationId");
        if (header == null) throw new IllegalStateException("Missing correlation ID");

        Message<List<CatDto>> message = MessageBuilder.withPayload(cats)
                .setHeader(KafkaHeaders.TOPIC, "cats-getByTailLength-response")
                .setHeader(KafkaHeaders.KEY, record.key())
                .setHeader("correlationId", header.value())
                .build();

        catListKafkaTemplate.send(message);
    }

    @KafkaListener(
            topics = "cats-isOwnerOfCat",
            groupId = "cats-group",
            containerFactory = "catOwnerCheckKafkaListenerContainerFactory"
    )
    public void listenIsOwnerOfCat(ConsumerRecord<String, CatOwnerCheckRequest> record) {
        CatOwnerCheckRequest request = record.value();
        boolean result = catService.isOwnerOfCat(request.getCatId(), request.getOwnerId());

        var header = record.headers().lastHeader("correlationId");
        if (header == null) throw new IllegalStateException("Missing correlation ID");

        Message<Boolean> message = MessageBuilder.withPayload(result)
                .setHeader(KafkaHeaders.TOPIC, "cats-isOwnerOfCat-response")
                .setHeader(KafkaHeaders.KEY, record.key())
                .setHeader("correlationId", header.value())
                .build();

        booleanKafkaTemplate.send(message);
    }
}
