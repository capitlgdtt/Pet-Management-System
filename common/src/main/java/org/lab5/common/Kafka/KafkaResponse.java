package org.lab5.common.Kafka;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class KafkaResponse<T> {
    private T payload;
    private String status;
    private String errorMessage;

    public KafkaResponse() {}

    public KafkaResponse(String status, T payload) {
        this.status = status;
        this.payload = payload;
        this.errorMessage = null;
    }
}
