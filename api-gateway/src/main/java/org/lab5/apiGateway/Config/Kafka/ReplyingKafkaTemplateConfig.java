package org.lab5.apiGateway.Config.Kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.lab5.common.Kafka.KafkaRequest;
import org.lab5.common.Kafka.KafkaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReplyingKafkaTemplateConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> commonProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    private Map<String, Object> commonConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, KafkaRequest<?>> catsProducerFactory() {
        return new DefaultKafkaProducerFactory<>(commonProducerConfigs());
    }

    @Bean
    public ConsumerFactory<String, KafkaResponse> catsConsumerFactory() {
        JsonDeserializer<KafkaResponse> valueDeserializer = new JsonDeserializer<>(KafkaResponse.class);
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(
                commonConsumerConfigs(),
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, KafkaResponse> catsReplyContainer() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaResponse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(catsConsumerFactory());
        ConcurrentMessageListenerContainer<String, KafkaResponse> container = factory.createContainer("cats-responses");
        container.getContainerProperties().setGroupId("apiGateway-cats-group");
        return container;
    }

    @Bean
    public ReplyingKafkaTemplate<String, KafkaRequest<?>, KafkaResponse> catsKafkaTemplate() {
        return new ReplyingKafkaTemplate<>(catsProducerFactory(), catsReplyContainer());
    }

    @Bean
    public ProducerFactory<String, KafkaRequest<?>> ownersProducerFactory() {
        return new DefaultKafkaProducerFactory<>(commonProducerConfigs());
    }

    @Bean
    public ConsumerFactory<String, KafkaResponse> ownersConsumerFactory() {
        JsonDeserializer<KafkaResponse> valueDeserializer = new JsonDeserializer<>(KafkaResponse.class);
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(
                commonConsumerConfigs(),
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, KafkaResponse> ownersReplyContainer() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaResponse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(ownersConsumerFactory());
        ConcurrentMessageListenerContainer<String, KafkaResponse> container = factory.createContainer("owners-responses");
        container.getContainerProperties().setGroupId("apiGateway-owners-group");
        return container;
    }

    @Bean
    public ReplyingKafkaTemplate<String, KafkaRequest<?>, KafkaResponse> ownersKafkaTemplate() {
        return new ReplyingKafkaTemplate<>(ownersProducerFactory(), ownersReplyContainer());
    }
}
