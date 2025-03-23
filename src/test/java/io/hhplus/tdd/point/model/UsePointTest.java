package io.hhplus.tdd.point.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsePointTest {

    @DisplayName("사용 포인트를 생성한다.")
    @Test
    void createUsePoint() {
        // given
        long value = 100_000;
        Amount amount = Amount.of(value);

        // when
        Point usePoint = UsePoint.of(amount);

        // then
        assertThat(usePoint.getAmount()).isEqualTo(-value);
        assertThat(usePoint.getTransactionType()).isEqualTo(TransactionType.USE);
    }
}