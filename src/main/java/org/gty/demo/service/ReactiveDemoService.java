package org.gty.demo.service;

import org.apache.commons.lang3.SerializationUtils;
import org.gty.demo.constant.SystemConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AsyncAmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.util.Objects;

@Service
public class ReactiveDemoService {

    private static final Logger log = LoggerFactory.getLogger(ReactiveDemoService.class);
    private static final String MESSAGE = "Hello, World";
    private static final String GLOBAL_COUNTER = "global-counter";

    private final AsyncAmqpTemplate asyncAmqpTemplate;
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    @Autowired
    @SuppressWarnings("unchecked")
    public ReactiveDemoService(@Nonnull AsyncAmqpTemplate asyncAmqpTemplate,
                               @Nonnull KafkaTemplate kafkaTemplate,
                               @Nonnull ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
        this.asyncAmqpTemplate = Objects.requireNonNull(asyncAmqpTemplate,
                "asyncAmqpTemplate must not be null");

        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate, "kafkaTemplate must not be null");

        this.reactiveStringRedisTemplate = Objects.requireNonNull(reactiveStringRedisTemplate,
                "reactiveStringRedisTemplate must not be null");
    }

    public Mono<Void> demo() {
        return Mono.when(incrementRedisCounter(), sendMessageToKafka(), sendMessageToRabbit());
    }

    private Mono<?> incrementRedisCounter() {
        var opsForValue = reactiveStringRedisTemplate.opsForValue();

        return Mono.just(0)
                .map(String::valueOf)
                .flatMap(val -> opsForValue.setIfAbsent(GLOBAL_COUNTER, val))
                .filter(val -> val)
                .map(val -> 0L)
                .switchIfEmpty(opsForValue.increment(GLOBAL_COUNTER))
                .doOnSuccess(val -> log.debug("[Redis] global counter = {}", val));
    }

    private Mono<Void> sendMessageToRabbit() {
        return Mono
                .<Void>fromRunnable(() -> asyncAmqpTemplate.convertSendAndReceive("demo-queue", MESSAGE))
                .doOnSuccess(ignored -> log.debug("[AMQP] --- MESSAGE sent to rabbit"))
                .subscribeOn(SystemConstants.defaultReactorScheduler());
    }

    private Mono<Void> sendMessageToKafka() {
        return Mono
                .<Void>fromRunnable(() -> kafkaTemplate.send("demo-topic",
                        SerializationUtils.serialize(MESSAGE)).addCallback(result -> log.debug("[Kafka] --- MESSAGE sent to kafka"),
                        failure -> log.warn("sending MESSAGE to kafka failed")))
                .subscribeOn(SystemConstants.defaultReactorScheduler());
    }
}
