package io.hhplus.tdd.point.entity.persistence;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.service.command.ChargePointCommand;
import io.hhplus.tdd.point.service.command.PointCommand;
import io.hhplus.tdd.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class PointHistoryWriterTest extends IntegrationTestSupport {

    @Autowired
    private PointHistoryWriter pointHistoryWriter;

    @DisplayName("포인트 내역을 저장한다.")
    @Test
    void save() {
        // given
        PointCommand command = ChargePointCommand.of(ANY_USER_ID, 100_000L);

        // when
        PointHistory savePointHistory = pointHistoryWriter.save(command);

        // then
        assertThat(savePointHistory.userId()).isEqualTo(command.getUserId());
        assertThat(savePointHistory.amount()).isEqualTo(command.getAmount());
        assertThat(savePointHistory.type()).isEqualTo(command.getType());
    }

}