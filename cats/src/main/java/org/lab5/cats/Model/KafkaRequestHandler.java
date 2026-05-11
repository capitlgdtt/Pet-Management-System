package org.lab5.cats.Model;

import org.lab5.common.Kafka.KafkaResponse;

public interface KafkaRequestHandler {
    KafkaResponse<?> handle(Object payload);
}
