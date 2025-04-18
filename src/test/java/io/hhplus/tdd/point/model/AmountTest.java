package io.hhplus.tdd.point.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AmountTest {

    @DisplayName("금액은 0보다 커야한다.")
    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void ofIfNotGreaterThenZero(long value) {
        // when & then
        assertThatThrownBy(() -> Amount.of(value))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("금액은 0보다 커야합니다.");
    }

    @DisplayName("금액을 생성한다.")
    @ParameterizedTest
    @ValueSource(longs = {1L, 10_000L, 1_000_000L})
    void of(long value) {
        // given
        Amount amount = Amount.of(value);

        // when
        long result = amount.getValue();

        // then
        assertThat(result).isEqualTo(value);
    }
}