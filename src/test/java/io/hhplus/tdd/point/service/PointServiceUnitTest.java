package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.entity.persistence.PointHistoryReader;
import io.hhplus.tdd.point.entity.persistence.PointHistoryWriter;
import io.hhplus.tdd.point.entity.persistence.UserPointReader;
import io.hhplus.tdd.point.entity.persistence.UserPointWriter;
import io.hhplus.tdd.point.lock.LockProvider;
import io.hhplus.tdd.point.service.command.ChargePointCommand;
import io.hhplus.tdd.point.service.command.PointCommand;
import io.hhplus.tdd.point.service.command.UsePointCommand;
import io.hhplus.tdd.support.FixtureTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Supplier;

import static io.hhplus.tdd.point.model.TransactionType.CHARGE;
import static io.hhplus.tdd.point.model.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PointServiceUnitTest extends FixtureTestSupport {

    @InjectMocks
    private PointService pointService;

    @Mock
    private UserPointReader userPointReader;

    @Mock
    private UserPointWriter userPointWriter;

    @Mock
    private PointHistoryReader pointHistoryReader;

    @Mock
    private PointHistoryWriter pointHistoryWriter;

    @Mock
    private LockProvider<UserPoint> lockProvider;

    @DisplayName("포인트 충전 최대 금액은 1,000만원 까지다.")
    @Test
    void chargePointWithGreaterThenMaxPoint() {
        // given
        PointCommand command = ChargePointCommand.of(ANY_USER_ID, 1L);

        given(lockProvider.lock(anyLong(), any())).willAnswer(this::stubLockProvider);

        given(userPointReader.findByUserId(anyLong()))
            .willReturn(new UserPoint(ANY_USER_ID, 10_000_000L, 1L));

        given(userPointWriter.updatedPoint(anyLong(), anyLong()))
            .willThrow(new IllegalArgumentException("최대 잔고는 10,000,000 포인트 입니다."));

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
        PointCommand command = ChargePointCommand.of(ANY_USER_ID, amount);

        given(lockProvider.lock(anyLong(), any())).willAnswer(this::stubLockProvider);

        given(userPointReader.findByUserId(any()))
            .willReturn(new UserPoint(ANY_USER_ID, 10_000L, 1L));

        given(userPointWriter.updatedPoint(anyLong(), anyLong()))
            .willReturn(new UserPoint(ANY_USER_ID, 10_000L + amount, 1L));

        // when
        UserPoint userPoint = pointService.updatePoint(command);

        // then
        assertThat(userPoint.id()).isEqualTo(ANY_USER_ID);
        assertThat(userPoint.point()).isEqualTo(10_000L + amount);
    }

    @DisplayName("포인트 사용 시 잔고는 충분해야한다.")
    @Test
    void usePointWithLessThenMinPoint() {
        // given
        PointCommand command = UsePointCommand.of(ANY_USER_ID, 10_001L);

        given(lockProvider.lock(anyLong(), any())).willAnswer(this::stubLockProvider);

        given(userPointReader.findByUserId(any()))
            .willReturn(new UserPoint(ANY_USER_ID, 10_000L, 1L));

        given(userPointWriter.updatedPoint(anyLong(), anyLong()))
            .willThrow(new IllegalArgumentException("잔고가 부족합니다."));

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
        PointCommand command = UsePointCommand.of(ANY_USER_ID, amount);

        given(lockProvider.lock(anyLong(), any())).willAnswer(this::stubLockProvider);

        given(userPointReader.findByUserId(any()))
            .willReturn(new UserPoint(ANY_USER_ID, 10_000L, 1L));

        given(userPointWriter.updatedPoint(anyLong(), anyLong()))
            .willReturn(new UserPoint(ANY_USER_ID, 10_000L - amount, 1L));

        // when
        UserPoint userPoint = pointService.updatePoint(command);

        // then
        assertThat(userPoint.id()).isEqualTo(ANY_USER_ID);
        assertThat(userPoint.point()).isEqualTo(10_000L - amount);
    }

    @DisplayName("사용자 ID로 포인트를 조회한다.")
    @Test
    void readPoint() {
        // given
        long userId = 1L;

        given(lockProvider.lock(anyLong(), any())).willAnswer(this::stubLockProvider);

        given(userPointReader.findByUserId(userId))
            .willReturn(new UserPoint(1L, 10_000L, 1L));

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
        List<PointHistory> expectedHistories = List.of(
            new PointHistory(1L, userId, -50_000L, USE, 2L),
            new PointHistory(2L, userId, 100_000L, CHARGE, 1L)
        );

        given(pointHistoryReader.findAllByUserIdOrderByUpdateMillisDesc(anyLong()))
            .willReturn(expectedHistories);

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

    private UserPoint stubLockProvider(InvocationOnMock invocation) {
        Supplier<UserPoint> supplier = invocation.getArgument(1);
        return supplier.get();
    }
}