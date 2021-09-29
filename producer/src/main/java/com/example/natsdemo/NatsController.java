package com.example.natsdemo;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.function.Consumer;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api")
public class NatsController {

    private final NatsProducer natsProducer;

    // http://localhost:8082/api/connect/c23fd3d6-ea4e-4f6a-becf-86a5f42964c6
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/connect/{topic}")
    public void connect(@PathVariable(name = "topic") String topic) {

        natsProducer.openConnection();
        String msg = "{\"p\":[{\"s\":\"1000SHIBUSDT\",\"l\":0.251868,\"m\":\"BINANCE_FUTURES\"}]}";
        natsProducer.pushAsync(topic, msg);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/send")
    public void sendMsg() {
        natsProducer.pushStream("com.demo-sub", UUID.randomUUID().toString());
    }
}
