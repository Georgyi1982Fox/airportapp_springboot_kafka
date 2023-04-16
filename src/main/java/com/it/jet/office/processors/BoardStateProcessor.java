package com.it.jet.office.processors;

import com.it.jet.common.bean.Airport;
import com.it.jet.common.bean.Board;
import com.it.jet.common.bean.Route;
import com.it.jet.common.messages.AirportStateMessage;
import com.it.jet.common.messages.BoardStateMessage;
import com.it.jet.common.processor.MessageConverter;
import com.it.jet.common.processor.MessageProcessor;
import com.it.jet.office.provider.AirportsProvider;
import com.it.jet.office.provider.BoardProvider;
import com.it.jet.office.service.WaitingRoutesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component("BOARD_STATE")
@RequiredArgsConstructor
public class BoardStateProcessor implements MessageProcessor<BoardStateMessage> {
    private final MessageConverter messageConverter;
    private final WaitingRoutesService waitingRoutesService;
    private final BoardProvider boardProvider;
    private final AirportsProvider airportsProvider;
    private final KafkaTemplate<String,String> kafkaTemplate;
    @Override
    public void process(String jsonMessage) {
        BoardStateMessage message = messageConverter.extractMessage(jsonMessage,BoardStateMessage.class);
        Board board = message.getBoard();
        Optional<Board> previous = boardProvider.getBoard(board.getName());
        Airport airport = airportsProvider.getAirport(board.getLocation());

        boardProvider.addBoard(board);

        if(previous.isPresent() && board.hasRoute() && !previous.get().hasRoute()){
            Route route = board.getRoute();
            waitingRoutesService.remove(route);
        }

        if(previous.isEmpty() || !board.isBusy() && previous.get().isBusy()){
            airport.addBoard(board.getName());
            kafkaTemplate.sendDefault(messageConverter.toJson(new AirportStateMessage(airport)));
        }
    }
}
