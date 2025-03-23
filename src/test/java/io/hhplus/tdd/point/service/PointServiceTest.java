package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.model.TransactionType;
import io.hhplus.tdd.point.service.command.ChargePointCommand;
import io.hhplus.tdd.point.service.command.PointCommand;
import io.hhplus.tdd.point.service.command.UsePointCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class PointServiceTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserPointTable userPointTable;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @DisplayName("포인트 충전 최대 금액은 1,000만원 까지다.")
    @Test
    void chargePointWithGreaterThenMaxPoint() {
        // given
        long userId = 1L;
        long amount = 1L;
        userPointTable.insertOrUpdate(userId, 10_000_000L);

        PointCommand command = ChargePointCommand.of(userId, amount);

        // when & then
        assertThatThrownBy(() -> pointService.processPoint(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 잔고는 10,000,000 포인트 입니다.");
    }

    @DisplayName("포인트를 충전한다.")
    @Test
    void chargePoint() {
        // given
        long userId = 1L;
        long amount = 500_000L;
        userPointTable.insertOrUpdate(userId, 10_000L);

        PointCommand command = ChargePointCommand.of(userId, amount);

        // when
        UserPoint userPoint = pointService.processPoint(command);

        // then
        assertThat(userPoint.id()).isEqualTo(userId);
        assertThat(userPoint.point()).isEqualTo(10_000L + amount);
    }

    @DisplayName("포인트 충전 시, 포인트 내역을 저장한다.")
    @Test
    void chargePointWithSavePointHistory() {
        // given
        long userId = 1L;
        long amount = 500_000L;

        PointCommand command = ChargePointCommand.of(userId, amount);
        pointService.processPoint(command);

        // when
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(userId);

        // then
        PointHistory pointHistory = pointHistories.get(pointHistories.size() - 1);
        assertThat(pointHistory.userId()).isEqualTo(userId);
        assertThat(pointHistory.amount()).isEqualTo(amount);
        assertThat(pointHistory.type()).isEqualTo(TransactionType.CHARGE);
    }

    @DisplayName("포인트 사용 시 잔고는 충분해야한다.")
    @Test
    void usePointWithLessThenMinPoint() {
        // given
        long userId = 1L;
        long amount = 10_001;
        userPointTable.insertOrUpdate(userId, 10_000L);

        PointCommand command = UsePointCommand.of(userId, amount);

        // when & then
        assertThatThrownBy(() -> pointService.processPoint(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔고가 부족합니다.");
    }

    @DisplayName("포인트를 사용한다.")
    @Test
    void usePoint() {
        // given
        long userId = 1L;
        long amount = 1_500L;
        userPointTable.insertOrUpdate(userId, 10_000L);

        PointCommand command = UsePointCommand.of(userId, amount);

        // when
        UserPoint userPoint = pointService.processPoint(command);

        // then
        assertThat(userPoint.id()).isEqualTo(userId);
        assertThat(userPoint.point()).isEqualTo(10_000L - amount);
    }

    @DisplayName("포인트 사용 시, 포인트 내역을 저장한다.")
    @Test
    void usePointWithSavePointHistory() {
        // given
        long userId = 1L;
        long amount = 1_500L;
        userPointTable.insertOrUpdate(userId, 10_000L);

        PointCommand command = UsePointCommand.of(userId, amount);
        pointService.processPoint(command);

        // when
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(userId);

        // then
        PointHistory pointHistory = pointHistories.get(pointHistories.size() - 1);
        assertThat(pointHistory.userId()).isEqualTo(userId);
        assertThat(pointHistory.amount()).isEqualTo(-amount);
        assertThat(pointHistory.type()).isEqualTo(TransactionType.USE);
    }

    @DisplayName("사용자 ID로 포인트를 조회한다.")
    @Test
    void readPoint() {
        // given
        long userId = 1L;
        userPointTable.insertOrUpdate(userId, 10_000L);

        // when
        UserPoint point = pointService.readPoint(userId);

        // then
        assertThat(point.id()).isEqualTo(userId);
        assertThat(point.point()).isEqualTo(10_000L);
    }

    @DisplayName("사용자 ID로 포인트 내역을 조회한다.")
    @Test
    void readPointHistories() {
        // given
        long userId = 1L;
        pointHistoryTable.insert(userId, 100_000L, TransactionType.CHARGE, System.currentTimeMillis());
        pointHistoryTable.insert(userId, -50_000L, TransactionType.USE, System.currentTimeMillis());

        // when
        List<PointHistory> histories = pointService.readPointHistories(userId);

        // then
        PointHistory usePointHistory = histories.get(0);
        assertThat(usePointHistory.userId()).isEqualTo(userId);
        assertThat(usePointHistory.amount()).isEqualTo(-50_000L);
        assertThat(usePointHistory.type()).isEqualTo(TransactionType.USE);

        PointHistory chargePointHistory = histories.get(1);
        assertThat(chargePointHistory.userId()).isEqualTo(userId);
        assertThat(chargePointHistory.amount()).isEqualTo(100_000L);
        assertThat(chargePointHistory.type()).isEqualTo(TransactionType.CHARGE);
    }

}