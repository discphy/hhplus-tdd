package io.hhplus.tdd.point.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChargePointTest {

    @DisplayName("충전 포인트를 생성한다.")
    @Test
    void createChargePoint() {
        // given
        long value = 100_000;
        Amount amount = Amount.of(value);

        // when
        Point chargePoint = ChargePoint.of(amount);

        // then
        assertThat(chargePoint.getAmount()).isEqualTo(value);
        assertThat(chargePoint.getTransactionType()).isEqualTo(TransactionType.CHARGE);
    }

}