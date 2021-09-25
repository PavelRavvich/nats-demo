package com.example.natsdemo;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamManagement;
import io.nats.client.Nats;
import io.nats.client.api.PublishAck;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;
import io.nats.client.api.StreamInfo;
import io.nats.client.support.JsonUtils;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class NatsProducer {

    private Connection connection;

    private AtomicInteger inc = new AtomicInteger(0);

    @SneakyThrows
    public void openConnection() {
        if (Objects.isNull(connection)) {
            connection = Nats.connect();
        }
    }

    @SneakyThrows
    void push(@NotNull String sub, @NotNull String msg) {
        String message = String.format("%s %s", msg, inc.incrementAndGet());
        connection.publish(sub, message.getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    void pushAsync(@NotNull String sub, @NotNull String msg) {
        JetStream stream = connection.jetStream();

        String message = String.format("%s %s", msg, inc.incrementAndGet());
        CompletableFuture<PublishAck> future = stream
                .publishAsync(sub, message.getBytes(StandardCharsets.UTF_8));

        future.thenApplyAsync(ack -> {
            System.out.println(ack);
            return ack;
        });
    }

    @PreDestroy
    @SneakyThrows
    void closeConnection() {
        if (Objects.nonNull(connection)) {
            connection.close();
        }
    }

    //--------------------- TODO --------------------------------//
    @SneakyThrows
    public void openStream(String name, String sub) {
        JetStreamManagement jsm = connection.jetStreamManagement();
        StreamConfiguration conf = StreamConfiguration.builder()
                .name(name)
                .subjects(sub)
                .storageType(StorageType.Memory)
                .build();
        StreamInfo streamInfo = jsm.addStream(conf);
        JsonUtils.printFormatted(streamInfo);
    }
    @SneakyThrows
    public void pushStream(@NotNull String sub, @NotNull String msg) {
        JetStream stream = connection.jetStream();
        PublishAck ack = stream.publish(sub, msg.getBytes());
        JsonUtils.printFormatted(ack);
    }
    //--------------------- TODO --------------------------------//
}
