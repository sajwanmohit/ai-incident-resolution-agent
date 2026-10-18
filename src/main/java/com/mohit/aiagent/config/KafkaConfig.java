package com.mohit.aiagent.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:incident-resolution-group}")
    private String consumerGroupId;

    @Value("${KAFKA_SECURITY_PROTOCOL:}")
    private String kafkaSecurityProtocol;

    @Value("${KAFKA_SASL_MECHANISM:}")
    private String kafkaSaslMechanism;

    @Value("${KAFKA_USERNAME:}")
    private String kafkaUsername;

    @Value("${KAFKA_PASSWORD:}")
    private String kafkaPassword;

    @Value("${KAFKA_SSL_CA_LOCATION:}")
    private String kafkaSslCaLocation;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        log.info("Creating Kafka producer factory with bootstrap servers: {}", bootstrapServers);
        Map<String, Object> props = new HashMap<>();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        applyAivenSecurity(props);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        log.info("Creating KafkaTemplate bean");
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        log.info("Creating Kafka consumer factory for group: {}", consumerGroupId);
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        applyAivenSecurity(props);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        log.info("Creating Kafka listener container factory");
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public NewTopic incidentEventsTopic(@Value("${app.kafka.topic:incident-events}") String topicName,
                                        @Value("${app.kafka.partitions:2}") int partitions,
                                        @Value("${app.kafka.replication-factor:2}") short replicationFactor) {
        log.info("Ensuring Kafka topic '{}' exists with partitions={} replicationFactor={}", topicName, partitions, replicationFactor);
        return new NewTopic(topicName, partitions, replicationFactor);
    }

    private void applyAivenSecurity(Map<String, Object> props) {
        boolean isLocalKafka = bootstrapServers == null || bootstrapServers.startsWith("localhost") || bootstrapServers.startsWith("127.0.0.1");
        boolean hasExplicitSecurityConfig = StringUtils.hasText(kafkaSecurityProtocol)
                || StringUtils.hasText(kafkaSaslMechanism)
                || StringUtils.hasText(kafkaUsername)
                || StringUtils.hasText(kafkaPassword)
                || StringUtils.hasText(kafkaSslCaLocation);

        if (isLocalKafka || !hasExplicitSecurityConfig) {
            return;
        }

        if (StringUtils.hasText(kafkaSecurityProtocol)) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaSecurityProtocol);
        }
        if (StringUtils.hasText(kafkaSaslMechanism)) {
            props.put(SaslConfigs.SASL_MECHANISM, kafkaSaslMechanism);
        }
        if (StringUtils.hasText(kafkaUsername) && StringUtils.hasText(kafkaPassword)) {
            String jaasConfig = String.format(
                    "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",
                    kafkaUsername,
                    kafkaPassword
            );
            props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);
        }
        if (StringUtils.hasText(kafkaSslCaLocation)) {
            props.put("ssl.ca.location", kafkaSslCaLocation);
        }

        log.info("Configured Kafka SASL/SSL settings for non-local bootstrap server: {}", bootstrapServers);
    }
}
