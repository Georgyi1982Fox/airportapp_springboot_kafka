package com.it.jet.office.provider;

import com.it.jet.common.bean.Board;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Slf4j
@Getter
@Component
public class BoardProvider {

    private final List<Board> boards = new ArrayList<>();
    private Optional<Board> getBoard(String boardName){
        return boards.stream()
                .filter(board -> board.getName().equals(boardName))
                .findFirst();
    }
    private void addBoard(Board board){
        Optional<Board> optionalBoard = getBoard(board.getName());
        if(optionalBoard.isPresent()){
            int ind = boards.indexOf(optionalBoard.get());
            boards.set(ind, board);
        }else {
            boards.add(board);
        }
    }

}
