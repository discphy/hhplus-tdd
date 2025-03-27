package io.hhplus.tdd.fake.support;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.lock.LockProvider;
import io.hhplus.tdd.point.service.command.PointCommand;

public class FakePointService {

    private final LockProvider<UserPoint> lockProvider;
    private final UserPointTable userPointTable = new UserPointTable();
    private final PointHistoryTable pointHistoryTable = new PointHistoryTable();

    public FakePointService(LockProvider<UserPoint> lockProvider) {
        this.lockProvider = lockProvider;
    }

    public UserPoint updatePoint(PointCommand command) {
        return lockProvider.lock(command.getUserId(), () -> {
            UserPoint userPoint = userPointTable.selectById(command.getUserId());
            long updateAmount = userPoint.addAmount(command.getAmount());

            // 의도적으로 sleep을 줘서 동시성 이슈 발생
            sleep(300);
            UserPoint updatedPoint = userPointTable.insertOrUpdate(command.getUserId(), updateAmount);
            pointHistoryTable.insert(command.getUserId(), command.getAmount(), command.getType(), System.currentTimeMillis());

            return updatedPoint;
        });
    }

    public UserPoint readPoint(long userId) {
        return lockProvider.lock(userId, () -> userPointTable.selectById(userId));
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
