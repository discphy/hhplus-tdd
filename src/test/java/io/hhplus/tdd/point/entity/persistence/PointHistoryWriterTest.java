package io.hhplus.tdd.point.entity.persistence;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.service.command.ChargePointCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PointHistoryWriterTest {

    @Autowired
    private PointHistoryWriter pointHistoryWriter;

    @DisplayName("포인트 내역을 저장한다.")
    @Test
    void save() {
        // given
        ChargePointCommand command = ChargePointCommand.of(1L, 100_000L);

        // when
        PointHistory savePointHistory = pointHistoryWriter.save(command);

        // then
        assertThat(savePointHistory.userId()).isEqualTo(command.getUserId());
        assertThat(savePointHistory.amount()).isEqualTo(command.getAmount());
        assertThat(savePointHistory.type()).isEqualTo(command.getType());
    }

}