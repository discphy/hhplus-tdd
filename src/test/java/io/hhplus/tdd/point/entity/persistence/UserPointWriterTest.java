package io.hhplus.tdd.point.entity.persistence;

import io.hhplus.tdd.point.entity.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserPointWriterTest {

    @Autowired
    private UserPointWriter userPointWriter;

    @DisplayName("포인트를 수정한다.")
    @Test
    void updatedChargePoint() {
        // given
        long userId = 1L;
        long amount = 10_000L;

        // when
        UserPoint userPoint = userPointWriter.updatedPoint(userId, amount);

        // then
        assertThat(userPoint.id()).isEqualTo(userId);
        assertThat(userPoint.point()).isEqualTo(amount);
    }
}