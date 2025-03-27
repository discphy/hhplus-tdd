package io.hhplus.tdd.point.entity.persistence;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPointPersistence implements UserPointReader, UserPointWriter {

    private final UserPointTable userPointTable;

    @Override
    public UserPoint findByUserId(Long userId) {
        return userPointTable.selectById(userId);
    }

    @Override
    public UserPoint updatedPoint(long userId, long amount) {
        return userPointTable.insertOrUpdate(userId, amount);
    }
}
