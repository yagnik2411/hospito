package com.yagnik.hospito.common.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic appointmentEventsTopic() {
        return TopicBuilder.name("appointment-events")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic billingEventsTopic() {
        return TopicBuilder.name("billing-events")
                .partitions(1)
                .replicas(1)
                .build();
    }
}