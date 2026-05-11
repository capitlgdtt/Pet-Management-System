package org.lab5.owners.Model;

import org.lab5.common.Kafka.KafkaResponse;

public interface KafkaRequestHandler {
    KafkaResponse<?> handle(Object payload);
}
