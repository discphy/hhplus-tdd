package io.hhplus.tdd.point.entity.persistence;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.model.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PointHistoryReaderTest {

    @Autowired
    private PointHistoryReader pointHistoryReader;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @DisplayName("포인트 내역을 최신순으로 가져온다.")
    @Test
    void findAllByUserIdOrderByUpdateMillisDesc() {
        // given
        pointHistoryTable.insert(1L, 100_000L, TransactionType.CHARGE, System.currentTimeMillis());
        pointHistoryTable.insert(1L, -10_000L, TransactionType.USE, System.currentTimeMillis());

        // when
        List<PointHistory> histories = pointHistoryReader.findAllByUserIdOrderByUpdateMillisDesc(1L);

        // then
        PointHistory firstHistory = histories.get(0);
        assertThat(firstHistory.userId()).isEqualTo(1L);
        assertThat(firstHistory.amount()).isEqualTo(-10_000L);
        assertThat(firstHistory.type()).isEqualTo(TransactionType.USE);

        PointHistory secondHistory = histories.get(1);
        assertThat(secondHistory.userId()).isEqualTo(1L);
        assertThat(secondHistory.amount()).isEqualTo(100_000L);
        assertThat(secondHistory.type()).isEqualTo(TransactionType.CHARGE);
    }

}