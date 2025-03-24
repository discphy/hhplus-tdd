package io.hhplus.tdd.point.entity.persistence;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class UserPointReaderTest extends IntegrationTestSupport {

    @Autowired
    private UserPointReader userPointReader;

    @Autowired
    private UserPointTable userPointTable;

    @DisplayName("사용자 ID로 포인트를 조회한다.")
    @Test
    void findByUserId() {
        // given
        long userId = 1L;
        long amount = 100_000L;

        userPointTable.insertOrUpdate(userId, amount);

        // when
        UserPoint findUserPoint = userPointReader.findByUserId(userId);

        // then
        assertThat(findUserPoint.id()).isEqualTo(userId);
        assertThat(findUserPoint.point()).isEqualTo(amount);
    }
}