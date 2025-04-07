package io.hhplus.tdd.point.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.hhplus.tdd.point.model.TransactionType.CHARGE;
import static io.hhplus.tdd.point.model.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;

class PointTest {

    @DisplayName("충전 포인트를 생성한다.")
    @Test
    void createChargePoint() {
        // given
        long value = 100_000L;
        Amount amount = Amount.of(value);

        // when
        Point chargePoint = ChargePoint.of(amount);

        // then
        assertThat(chargePoint.getAmount()).isEqualTo(value);
        assertThat(chargePoint.getTransactionType()).isEqualTo(CHARGE);
    }

    @DisplayName("사용 포인트를 생성한다.")
    @Test
    void createUsePoint() {
        // given
        long value = 100_000L;
        Amount amount = Amount.of(value);

        // when
        Point usePoint = UsePoint.of(amount);

        // then
        assertThat(usePoint.getAmount()).isEqualTo(-value);
        assertThat(usePoint.getTransactionType()).isEqualTo(USE);
    }
}