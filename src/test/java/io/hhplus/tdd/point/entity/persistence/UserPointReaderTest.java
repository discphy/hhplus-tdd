package io.hhplus.tdd.point.entity.persistence;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserPointReaderTest {

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