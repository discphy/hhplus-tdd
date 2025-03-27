package io.hhplus.tdd.point.entity.persistence;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.service.command.PointCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Comparator.comparing;

@Component
@RequiredArgsConstructor
public class PointHistoryPersistence implements PointHistoryReader, PointHistoryWriter {

    private final PointHistoryTable pointHistoryTable;

    @Override
    public List<PointHistory> findAllByUserIdOrderByUpdateMillisDesc(long userId) {
        return pointHistoryTable.selectAllByUserId(userId).stream()
            .sorted(comparing(PointHistory::updateMillis).reversed())
            .toList();
    }

    @Override
    public PointHistory save(PointCommand command) {
        return pointHistoryTable.insert(command.getUserId(),
            command.getAmount(),
            command.getType(),
            System.currentTimeMillis());
    }
}
