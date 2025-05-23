package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.entity.persistence.PointHistoryReader;
import io.hhplus.tdd.point.entity.persistence.PointHistoryWriter;
import io.hhplus.tdd.point.entity.persistence.UserPointWriter;
import io.hhplus.tdd.point.service.command.ChargePointCommand;
import io.hhplus.tdd.point.service.command.PointCommand;
import io.hhplus.tdd.point.service.command.UsePointCommand;
import io.hhplus.tdd.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.hhplus.tdd.point.model.TransactionType.CHARGE;
import static io.hhplus.tdd.point.model.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointServiceTest extends IntegrationTestSupport {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserPointWriter userPointWriter;

    @Autowired
    private PointHistoryReader pointHistoryReader;

    @Autowired
    private PointHistoryWriter pointHistoryWriter;

    @DisplayName("포인트 충전 최대 금액은 1,000만원 까지다.")
    @Test
    void chargePointWithGreaterThenMaxPoint() {
        // given
        long amount = 1L;
        userPointWriter.updatedPoint(ANY_USER_ID, 10_000_000L);

        PointCommand command = ChargePointCommand.of(ANY_USER_ID, amount);

        // when & then
        assertThatThrownBy(() -> pointService.updatePoint(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 잔고는 10,000,000 포인트 입니다.");
    }

    @DisplayName("포인트를 충전한다.")
    @Test
    void chargePoint() {
        // given
        long amount = 500_000L;
        userPointWriter.updatedPoint(ANY_USER_ID, 10_000L);

        PointCommand command = ChargePointCommand.of(ANY_USER_ID, amount);

        // when
        UserPoint userPoint = pointService.updatePoint(command);

        // then
        assertThat(userPoint.id()).isEqualTo(ANY_USER_ID);
        assertThat(userPoint.point()).isEqualTo(10_000L + amount);
    }

    @DisplayName("포인트 충전 시, 포인트 내역을 저장한다.")
    @Test
    void chargePointWithSavePointHistory() {
        // given
        long amount = 500_000L;

        PointCommand command = ChargePointCommand.of(ANY_USER_ID, amount);
        pointService.updatePoint(command);

        // when
        List<PointHistory> pointHistories = pointHistoryReader.findAllByUserIdOrderByUpdateMillisDesc(ANY_USER_ID);

        // then
        PointHistory pointHistory = pointHistories.get(0);
        assertThat(pointHistory.userId()).isEqualTo(ANY_USER_ID);
        assertThat(pointHistory.amount()).isEqualTo(amount);
        assertThat(pointHistory.type()).isEqualTo(CHARGE);
    }

    @DisplayName("포인트 사용 시 잔고는 충분해야한다.")
    @Test
    void usePointWithLessThenMinPoint() {
        // given
        long amount = 10_001L;
        userPointWriter.updatedPoint(ANY_USER_ID, 10_000L);

        PointCommand command = UsePointCommand.of(ANY_USER_ID, amount);

        // when & then
        assertThatThrownBy(() -> pointService.updatePoint(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔고가 부족합니다.");
    }

    @DisplayName("포인트를 사용한다.")
    @Test
    void usePoint() {
        // given
        long amount = 1_500L;
        userPointWriter.updatedPoint(ANY_USER_ID, 10_000L);

        PointCommand command = UsePointCommand.of(ANY_USER_ID, amount);

        // when
        UserPoint userPoint = pointService.updatePoint(command);

        // then
        assertThat(userPoint.id()).isEqualTo(ANY_USER_ID);
        assertThat(userPoint.point()).isEqualTo(10_000L - amount);
    }

    @DisplayName("포인트 사용 시, 포인트 내역을 저장한다.")
    @Test
    void usePointWithSavePointHistory() {
        // given
        long amount = 1_500L;
        userPointWriter.updatedPoint(ANY_USER_ID, 10_000L);

        PointCommand command = UsePointCommand.of(ANY_USER_ID, amount);
        pointService.updatePoint(command);

        // when
        List<PointHistory> pointHistories = pointHistoryReader.findAllByUserIdOrderByUpdateMillisDesc(ANY_USER_ID);

        // then
        PointHistory pointHistory = pointHistories.get(0);
        assertThat(pointHistory.userId()).isEqualTo(ANY_USER_ID);
        assertThat(pointHistory.amount()).isEqualTo(-amount);
        assertThat(pointHistory.type()).isEqualTo(USE);
    }

    @DisplayName("사용자 ID로 포인트를 조회한다.")
    @Test
    void readPoint() {
        // given
        long userId = 1L;
        userPointWriter.updatedPoint(userId, 10_000L);

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
        ChargePointCommand chargePointCommand = ChargePointCommand.of(userId, 100_000L);
        UsePointCommand usePointCommand = UsePointCommand.of(userId, 50_000L);

        pointHistoryWriter.save(chargePointCommand);
        pointHistoryWriter.save(usePointCommand);

        // when
        List<PointHistory> histories = pointService.readPointHistories(userId);

        // then
        PointHistory usePointHistory = histories.get(0);
        assertThat(usePointHistory.userId()).isEqualTo(userId);
        assertThat(usePointHistory.amount()).isEqualTo(-50_000L);
        assertThat(usePointHistory.type()).isEqualTo(USE);

        PointHistory chargePointHistory = histories.get(1);
        assertThat(chargePointHistory.userId()).isEqualTo(userId);
        assertThat(chargePointHistory.amount()).isEqualTo(100_000L);
        assertThat(chargePointHistory.type()).isEqualTo(CHARGE);
    }

}