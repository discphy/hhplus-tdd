package io.hhplus.tdd.database;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.hhplus.tdd.point.model.TransactionType.CHARGE;
import static io.hhplus.tdd.point.model.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class PointHistoryTableTest extends IntegrationTestSupport {

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @DisplayName("충전 포인트 내역을 등록한다.")
    @Test
    void insertWithChargePoint() {
        // given
        long amount = 100_000L;

        // when
        PointHistory savePointHistory = pointHistoryTable.insert(ANY_USER_ID, amount, CHARGE, currentTimeMillis());

        // then
        assertThat(savePointHistory.userId()).isEqualTo(ANY_USER_ID);
        assertThat(savePointHistory.amount()).isEqualTo(amount);
        assertThat(savePointHistory.type()).isEqualTo(CHARGE);
    }

    @DisplayName("사용 포인트 내역을 등록한다.")
    @Test
    void insertWithUsePoint() {
        // given
        long amount = -100_000L;

        // when
        PointHistory savePointHistory = pointHistoryTable.insert(ANY_USER_ID, amount, USE, currentTimeMillis());

        // then
        assertThat(savePointHistory.userId()).isEqualTo(ANY_USER_ID);
        assertThat(savePointHistory.amount()).isEqualTo(amount);
        assertThat(savePointHistory.type()).isEqualTo(USE);
    }

    @DisplayName("사용자 ID로 포인트 내역을 조회한다.")
    @Test
    void selectAllByUserId() {
        // given
        long userId = 1L;

        pointHistoryTable.insert(userId, 100_000L, CHARGE, currentTimeMillis());
        pointHistoryTable.insert(userId, -50_000L, USE, currentTimeMillis());

        // when
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(userId);

        // then
        assertThat(pointHistories)
            .extracting("userId", "amount", "type")
            .contains(
                tuple(userId, 100_000L, CHARGE),
                tuple(userId, -50_000L, USE)
            );
    }
}