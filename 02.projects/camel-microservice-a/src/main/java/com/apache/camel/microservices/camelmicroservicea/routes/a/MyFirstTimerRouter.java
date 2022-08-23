package com.apache.camel.microservices.camelmicroservicea.routes.a;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyFirstTimerRouter extends RouteBuilder {
    private final GetCurrentTimeBean getCurrentTimeBean;
    private final LoggingComponent loggingComponent;

    public MyFirstTimerRouter(GetCurrentTimeBean getCurrentTimeBean, LoggingComponent loggingComponent) {
        this.getCurrentTimeBean = getCurrentTimeBean;
        this.loggingComponent = loggingComponent;
    }

    @Override
    public void configure() throws Exception {

        /**
         * The usual flow for Apache Camel is:
         *
         * 1. Pick up a message from the queue
         * 2. Make the surrounding transformation or processing
         *  2.1. Processing: To do some operation or something which does not make a change on the body of the message itself
         *  2.2. Transformation: You are doing anything that changes the body of the message
         * 3. You would send it out to a database or another queue
         */
        from("timer:MyFirstTimerRouter")
                .log("${body}") // expected result: null - It is for check the transformation or processing that generate changes
                .transform().constant("My constant message")
                .log("${body}") // expected result: My constant message - It is for check the transformation or processing that generate changes
                //.transform().constant("Time now is: " + LocalDate.now()) // Only create once time the message because it is a constant
                //.bean("getCurrentTimeBean") // That's the one bad practice that we're following in here is we're having this name dependent on the name of this class
                .bean(getCurrentTimeBean, "getCurrentTime") // You need to specify the name of the method when the bean having two or more methods
                .log("${body}") // expected result: Time now is: AAAA-mm-ddTHH:MM:ss.SSSSSSSSS - It is for check the transformation or processing that generate changes
                .bean(loggingComponent)
                .log("${body}") // expected result: Time now is: AAAA-mm-ddTHH:MM:ss.SSS - It is for check the transformation or processing that generate changes
                .process(new SimpleLoggingProcessor()) // In addition to bean, you can also do processing of a message by creating processes
                .to("log:MyFirstTimerRouter");
                //.log("log:MyFirstTimerRouter"); // That is the same that above line
    }
}

@Component
class GetCurrentTimeBean {
    public String getCurrentTime() {
        return "Time now is: " + LocalDateTime.now();
    }
}

@Component
class LoggingComponent {
    private Logger logger = LoggerFactory.getLogger(LoggingComponent.class);
    public void log(String message) {
        logger.info("LoggingComponent processing result: {}", message);
    }
}

class SimpleLoggingProcessor implements Processor {
    private Logger logger = LoggerFactory.getLogger(SimpleLoggingProcessor.class);
    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("SimpleLoggingProcessor processing result: {}", exchange.getIn().getBody(String.class));
    }
}