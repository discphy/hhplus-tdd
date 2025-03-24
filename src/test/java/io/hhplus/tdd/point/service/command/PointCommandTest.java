package io.hhplus.tdd.point.service.command;

import io.hhplus.tdd.point.model.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointCommandTest {

    @DisplayName("충전에 필요한 객체시 금액은 0보다 커야한다.")
    @Test
    void createChargePointCommandWithInvalidAmount() {
        // given
        long userId = 1L;
        long amount = 0L;

        // when & then
        assertThatThrownBy(() -> ChargePointCommand.of(userId, amount))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("금액은 0보다 커야합니다.");
    }

    @DisplayName("사용자 ID와 금액으로 충전에 필요한 객체를 생성한다.")
    @Test
    void createChargePointCommand() {
        // given
        long userId = 1L;
        long amount = 100_000L;

        // when
        PointCommand command = ChargePointCommand.of(userId, amount);

        // then
        assertThat(command.getAmount()).isGreaterThan(0);
        assertThat(command.getType()).isEqualTo(TransactionType.CHARGE);
    }

    @DisplayName("사용에 필요한 객체시 금액은 0보다 커야한다.")
    @Test
    void createUsePointCommandWithInvalidAmount() {
        // given
        long userId = 1L;
        long amount = 0L;

        // when & then
        assertThatThrownBy(() -> UsePointCommand.of(userId, amount))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("금액은 0보다 커야합니다.");
    }

    @DisplayName("사용자 ID와 금액으로 사용에 필요한 객체를 생성한다.")
    @Test
    void createUsePointCommand() {
        // given
        long userId = 1L;
        long amount = 100_000L;

        // when
        PointCommand command = UsePointCommand.of(userId, amount);

        // then
        assertThat(command.getAmount()).isLessThan(0);
        assertThat(command.getType()).isEqualTo(TransactionType.USE);
    }

}