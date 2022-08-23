package com.apache.camel.microservices.camelmicroservicea.routes.b;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyFileRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("file:01.files/")
                .log("${body}")
                .to("file:01.files/outbox");
                //.routeId("myRoute")
                //.to("file:outbox")
                //.to("file:outbox");
    }
}
