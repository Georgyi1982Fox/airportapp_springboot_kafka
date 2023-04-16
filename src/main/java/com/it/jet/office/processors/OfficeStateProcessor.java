package com.it.jet.office.processors;

import com.it.jet.common.messages.AirportStateMessage;
import com.it.jet.common.messages.OfficeStateMessage;
import com.it.jet.common.processor.MessageConverter;
import com.it.jet.common.processor.MessageProcessor;
import com.it.jet.office.provider.AirportsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
@Slf4j
@Component("OFFICE_STATE")
@RequiredArgsConstructor
public class OfficeStateProcessor implements MessageProcessor<OfficeStateMessage> {
    private final MessageConverter  messageConverter;
    private final AirportsProvider airportsProvider;
    private final KafkaTemplate<String,String> kafkaTemplate;
    @Override
    public void process(String jsonMessage) {
        airportsProvider.getPorts().forEach(airport -> {
            kafkaTemplate.sendDefault(messageConverter.toJson(new AirportStateMessage(airport)));
        });
    }
}
