package io.hhplus.tdd.point.entity.persistence;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.hhplus.tdd.point.model.TransactionType.CHARGE;
import static io.hhplus.tdd.point.model.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;

class PointHistoryReaderTest extends IntegrationTestSupport {

    @Autowired
    private PointHistoryReader pointHistoryReader;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @DisplayName("사용자 ID로 포인트 내역을 최신순으로 가져온다.")
    @Test
    void findAllByUserIdOrderByUpdateMillisDesc() {
        // given
        long userId = 1L;
        pointHistoryTable.insert(userId, 100_000L, CHARGE, currentTimeMillis());
        pointHistoryTable.insert(userId, -10_000L, USE, currentTimeMillis());

        // when
        List<PointHistory> histories = pointHistoryReader.findAllByUserIdOrderByUpdateMillisDesc(userId);

        // then
        PointHistory firstHistory = histories.get(0);
        assertThat(firstHistory.userId()).isEqualTo(userId);
        assertThat(firstHistory.amount()).isEqualTo(-10_000L);
        assertThat(firstHistory.type()).isEqualTo(USE);

        PointHistory secondHistory = histories.get(1);
        assertThat(secondHistory.userId()).isEqualTo(userId);
        assertThat(secondHistory.amount()).isEqualTo(100_000L);
        assertThat(secondHistory.type()).isEqualTo(CHARGE);
    }

}