package io.hhplus.tdd.database;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.model.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
class PointHistoryTableTest {

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @DisplayName("충전 포인트 내역을 등록한다.")
    @Test
    void insertWithChargePoint() {
        // given
        long userId = 1L;
        long amount = 100_000L;

        // when
        PointHistory savePointHistory = pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, System.currentTimeMillis());

        // then
        assertThat(savePointHistory.userId()).isEqualTo(userId);
        assertThat(savePointHistory.amount()).isEqualTo(amount);
        assertThat(savePointHistory.type()).isEqualTo(TransactionType.CHARGE);
    }

    @DisplayName("사용 포인트 내역을 등록한다.")
    @Test
    void insertWithUsePoint() {
        // given
        long userId = 1L;
        long amount = -100_000L;

        // when
        PointHistory savePointHistory = pointHistoryTable.insert(userId, amount, TransactionType.USE, System.currentTimeMillis());

        // then
        assertThat(savePointHistory.userId()).isEqualTo(userId);
        assertThat(savePointHistory.amount()).isEqualTo(amount);
        assertThat(savePointHistory.type()).isEqualTo(TransactionType.USE);
    }

    @DisplayName("사용자 ID로 포인트 내역을 조회한다.")
    @Test
    void selectAllByUserId() {
        // given
        long userId = 1L;

        pointHistoryTable.insert(userId, 100_000L, TransactionType.CHARGE, System.currentTimeMillis());
        pointHistoryTable.insert(userId, -50_000L, TransactionType.USE, System.currentTimeMillis());

        // when
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(userId);

        // then
        assertThat(pointHistories)
            .extracting("userId", "amount", "type")
            .contains(
                tuple(userId, 100_000L, TransactionType.CHARGE),
                tuple(userId, -50_000L, TransactionType.USE)
            );
    }
}