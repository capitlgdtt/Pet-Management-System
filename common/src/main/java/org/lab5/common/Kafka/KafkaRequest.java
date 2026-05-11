package org.lab5.common.Kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaRequest<T> {
    private String command;
    private T payload;
    private Map<String, Object> metadata;
}
