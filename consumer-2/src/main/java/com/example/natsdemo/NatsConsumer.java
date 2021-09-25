package com.example.natsdemo;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

@Service
public class NatsConsumer {
    private Connection connection;

    @PostConstruct
    public void init() {
      openConnection();
      subscribe("com.tradeshare", new Consumer<String>() {
          @Override
          public void accept(String s) {
              System.out.println(s);
          }
      });
    }

    @SneakyThrows
    public void openConnection() {
        connection = Nats.connect();
    }

    public Dispatcher subscribe(@NotNull String sub, Consumer<String> consumer) {
        return connection
                .createDispatcher(msg -> {
                    consumer.accept(new String(msg.getData(), StandardCharsets.UTF_8));
                    msg.ack();
                })
                .subscribe(sub);
    }

    @PreDestroy
    @SneakyThrows
    void closeConnection() {
        if (Objects.nonNull(connection)) {
            connection.close();
        }
    }
}
