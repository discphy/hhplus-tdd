package io.hhplus.tdd.point.model;

public class Amount {

    private final long value;

    private Amount(long value) {
        validate(value);
        this.value = value;
    }

    public static Amount of(long amount) {
        return new Amount(amount);
    }

    private void validate(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야합니다.");
        }
    }

    public long getValue() {
        return value;
    }
}
