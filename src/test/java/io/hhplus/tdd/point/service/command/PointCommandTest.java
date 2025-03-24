package io.hhplus.tdd.point.service.command;

import io.hhplus.tdd.support.FixtureTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.hhplus.tdd.point.model.TransactionType.CHARGE;
import static io.hhplus.tdd.point.model.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointCommandTest extends FixtureTestSupport {

    @DisplayName("충전에 필요한 객체시 금액은 0보다 커야한다.")
    @Test
    void createChargePointCommandWithInvalidAmount() {
        // given
        long amount = 0L;

        // when & then
        assertThatThrownBy(() -> ChargePointCommand.of(ANY_USER_ID, amount))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("금액은 0보다 커야합니다.");
    }

    @DisplayName("사용자 ID와 금액으로 충전에 필요한 객체를 생성한다.")
    @Test
    void createChargePointCommand() {
        // given
        long amount = 100_000L;

        // when
        PointCommand command = ChargePointCommand.of(ANY_USER_ID, amount);

        // then
        assertThat(command.getAmount()).isGreaterThan(0);
        assertThat(command.getType()).isEqualTo(CHARGE);
    }

    @DisplayName("사용에 필요한 객체시 금액은 0보다 커야한다.")
    @Test
    void createUsePointCommandWithInvalidAmount() {
        // given
        long amount = 0L;

        // when & then
        assertThatThrownBy(() -> UsePointCommand.of(ANY_USER_ID, amount))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("금액은 0보다 커야합니다.");
    }

    @DisplayName("사용자 ID와 금액으로 사용에 필요한 객체를 생성한다.")
    @Test
    void createUsePointCommand() {
        // given
        long amount = 100_000L;

        // when
        PointCommand command = UsePointCommand.of(ANY_USER_ID, amount);

        // then
        assertThat(command.getAmount()).isLessThan(0);
        assertThat(command.getType()).isEqualTo(USE);
    }

}