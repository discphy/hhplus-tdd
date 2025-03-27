package io.hhplus.tdd.point.entity.persistence;

import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class UserPointWriterTest extends IntegrationTestSupport {

    @Autowired
    private UserPointWriter userPointWriter;

    @DisplayName("포인트를 수정한다.")
    @Test
    void updatedChargePoint() {
        // given
        long amount = 10_000L;

        // when
        UserPoint userPoint = userPointWriter.updatedPoint(ANY_USER_ID, amount);

        // then
        assertThat(userPoint.id()).isEqualTo(ANY_USER_ID);
        assertThat(userPoint.point()).isEqualTo(amount);
    }
}