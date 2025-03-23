package io.hhplus.tdd.point.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserPointTest {

    @DisplayName("포인트 잔고는 최소 잔고보다 작을 수 없다.")
    @Test
    void cannotLessThanMinPoint() {
        // given
        long userId = 1L;
        long point = -1L;
        long updateMillis = System.currentTimeMillis();

        // when & then
        assertThatThrownBy(() -> new UserPoint(userId, point, updateMillis))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔고가 부족합니다.");
    }

    @DisplayName("포인트 잔고는 최대 잔고 보다 클 수 없다.")
    @Test
    void cannotGreaterThenMaxPoint() {
        // given
        long userId = 1L;
        long point = 10_000_001L;
        long updateMillis = System.currentTimeMillis();

        // when & then
        assertThatThrownBy(() -> new UserPoint(userId, point, updateMillis))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 잔고는 10,000,000 포인트 입니다.");
    }

    @DisplayName("포인트 잔고에 충전/사용할 포인트를 더한다.")
    @Test
    void addAmount() {
        // given
        long userId = 1L;
        long point = 100_000L;
        long updateMillis = System.currentTimeMillis();

        UserPoint userPoint = new UserPoint(userId, point, updateMillis);

        // when
        long addPoint = userPoint.addAmount(100_000L);

        // then
        assertThat(addPoint).isEqualTo(200_000L);
    }
}