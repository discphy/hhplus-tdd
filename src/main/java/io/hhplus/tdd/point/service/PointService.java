package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.service.command.ChargePointCommand;
import io.hhplus.tdd.point.service.command.PointCommand;
import io.hhplus.tdd.point.service.command.UsePointCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint chargePoint(PointCommand command) {
        UserPoint userPoint = readPoint(command.getUserId());

        UserPoint chargePoint = updateUserPoint(userPoint, command);
        addPointHistory(command);

        return chargePoint;
    }

    public UserPoint usePoint(PointCommand command) {
        UserPoint userPoint = readPoint(command.getUserId());

        UserPoint usePoint = updateUserPoint(userPoint, command);
        addPointHistory(command);

        return usePoint;
    }

    public UserPoint readPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> readPointHistories(long userId) {
        return pointHistoryTable.selectAllByUserId(userId).stream()
            .sorted(Comparator.comparing(PointHistory::updateMillis).reversed())
            .toList();
    }

    private UserPoint updateUserPoint(UserPoint userPoint, PointCommand command) {
        long updatePoint = userPoint.addPoint(command.getAmount());
        return userPointTable.insertOrUpdate(command.getUserId(), updatePoint);
    }

    private void addPointHistory(PointCommand command) {
        pointHistoryTable.insert(command.getUserId(),
            command.getAmount(),
            command.getType(),
            System.currentTimeMillis());
    }
}
