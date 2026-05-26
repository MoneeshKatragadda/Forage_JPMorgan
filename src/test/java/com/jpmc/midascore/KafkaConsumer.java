package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class KafkaConsumer {
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "kafka-group")
    public void listen(String transaction){
        System.out.println("Consumer is ready...");
        System.out.println("Transaction received: " + transaction);
    }
}
