package com.example.natsdemo;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Consumer;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api")
public class NatsController {

    private final NatsProducer natsProducer;

    // http://localhost:8082/api/connect
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/connect")
    public void connect() {
        natsProducer.openConnection();
        natsProducer.pushAsync("com.tradeshare", "test");
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/send")
    public void sendMsg() {
        natsProducer.pushStream("com.demo-sub", UUID.randomUUID().toString());
    }
}
