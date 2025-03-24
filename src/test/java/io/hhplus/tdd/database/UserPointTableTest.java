package io.hhplus.tdd.database;

import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserPointTableTest extends IntegrationTestSupport {

    @Autowired
    private UserPointTable userPointTable;

    @DisplayName("포인트 잔고가 부족하다.")
    @Test
    void insertOrUpdateWithInsufficientPoint() {
        // given
        long amount = -1;

        // when & then
        assertThatThrownBy(() -> userPointTable.insertOrUpdate(ANY_USER_ID, amount))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔고가 부족합니다.");
    }

    @DisplayName("포인트 최대 잔고를 넘기면 안된다.")
    @Test
    void insertOrUpdateWithExceedMaxPoint() {
        // given
        long amount = 10_000_001L;

        // when & then
        assertThatThrownBy(() -> userPointTable.insertOrUpdate(ANY_USER_ID, amount))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 잔고는 10,000,000 포인트 입니다.");
    }

    @DisplayName("포인트 잔고를 등록 및 수정 한다.")
    @Test
    void insertOrUpdateForChargePoint() {
        // given
        long amount = 100_000L;

        // when
        UserPoint userPoint = userPointTable.insertOrUpdate(ANY_USER_ID, amount);

        // then
        assertThat(userPoint.id()).isEqualTo(ANY_USER_ID);
        assertThat(userPoint.point()).isEqualTo(amount);
    }

    @DisplayName("포인트 잔고를 사용자 ID로 조회한다.")
    @Test
    void selectById() {
        // given
        long userId = 1L;
        long amount = 100_000L;

        userPointTable.insertOrUpdate(userId, amount);

        // when
        UserPoint userPoint = userPointTable.selectById(userId);

        // then
        assertThat(userPoint.id()).isEqualTo(userId);
        assertThat(userPoint.point()).isEqualTo(amount);
    }
}