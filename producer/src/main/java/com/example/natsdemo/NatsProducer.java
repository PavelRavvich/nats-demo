package com.example.natsdemo;

import io.nats.client.*;
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

    private final AtomicInteger inc = new AtomicInteger(0);

    @SneakyThrows
    public void openConnection() {
        String url = "nats://localhost:4222";
        if (Objects.isNull(connection)) {
            Options options = new Options.Builder()
                    .server(url)
                    .connectionListener((conn, type) -> {
                                System.out.println(type);
                            }).build();
            connection = Nats.connect(options);
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

        CompletableFuture<PublishAck> future = stream
                .publishAsync(sub, msg.getBytes(StandardCharsets.UTF_8));

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
