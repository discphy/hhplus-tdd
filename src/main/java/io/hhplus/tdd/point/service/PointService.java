package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.entity.persistence.PointHistoryReader;
import io.hhplus.tdd.point.entity.persistence.PointHistoryWriter;
import io.hhplus.tdd.point.entity.persistence.UserPointReader;
import io.hhplus.tdd.point.entity.persistence.UserPointWriter;
import io.hhplus.tdd.point.service.command.PointCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryReader pointHistoryReader;
    private final PointHistoryWriter pointHistoryWriter;
    private final UserPointReader userPointReader;
    private final UserPointWriter userPointWriter;

    public UserPoint updatePoint(PointCommand command) {
        UserPoint userPoint = readPoint(command.getUserId());
        long updateAmount = userPoint.addAmount(command.getAmount());

        UserPoint updatedPoint = userPointWriter.updatedPoint(command.getUserId(), updateAmount);
        pointHistoryWriter.save(command);

        return updatedPoint;
    }

    public UserPoint readPoint(long userId) {
        return userPointReader.findByUserId(userId);
    }

    public List<PointHistory> readPointHistories(long userId) {
        return pointHistoryReader.findAllByUserIdOrderByUpdateMillisDesc(userId);
    }
}
