package org.lab5.apiGateway.Config.Kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.BooleanDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.lab5.common.Dto.CatDto;
import org.lab5.common.Dto.OwnerDto;
import org.lab5.common.Kafka.KafkaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, OwnerDto> ownerDtoConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "org.lab5.common.Dto.OwnerDto");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OwnerDto> ownerKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OwnerDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(ownerDtoConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, List<OwnerDto>> ownerDtoListConsumerFactory() {
        JsonDeserializer<List<OwnerDto>> deserializer = new JsonDeserializer<>(new TypeReference<>() {});
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, List<OwnerDto>> ownerListKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, List<OwnerDto>> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(ownerDtoListConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, KafkaResponse<?>> consumerFactory() {
        JsonDeserializer<KafkaResponse<?>> deserializer = new JsonDeserializer<>(KafkaResponse.class);
        deserializer.addTrustedPackages("*");
        deserializer.setRemoveTypeHeaders(false);
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "api-gateway-owners-group");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaResponse<?>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaResponse<?>> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, CatDto> catDtoConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "org.lab5.common.Dto.CatDto");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CatDto> catKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CatDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(catDtoConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, List<CatDto>> catDtoListConsumerFactory() {
        JsonDeserializer<List<CatDto>> deserializer = new JsonDeserializer<>(new TypeReference<>() {});
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, List<CatDto>> catListKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, List<CatDto>> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(catDtoListConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Boolean> boolConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, BooleanDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Boolean> boolKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Boolean> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(boolConsumerFactory());
        return factory;
    }

}
