package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.service.command.PointCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint processPoint(PointCommand command) {
        UserPoint userPoint = readPoint(command.getUserId());
        long updateAmount = userPoint.addAmount(command.getAmount());

        UserPoint processPoint = updateUserPoint(command.getUserId(), updateAmount);
        addPointHistory(command);

        return processPoint;
    }

    public UserPoint readPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> readPointHistories(long userId) {
        return pointHistoryTable.selectAllByUserId(userId).stream()
            .sorted(Comparator.comparing(PointHistory::updateMillis).reversed())
            .toList();
    }

    private UserPoint updateUserPoint(long userId, long updateAmount) {
        return userPointTable.insertOrUpdate(userId, updateAmount);
    }

    private void addPointHistory(PointCommand command) {
        pointHistoryTable.insert(command.getUserId(),
            command.getAmount(),
            command.getType(),
            System.currentTimeMillis());
    }
}
