package io.hhplus.tdd.point.entity;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    private static final long MIN_POINT = 0;
    private static final long MAX_POINT = 10_000_000;

    public UserPoint {
        if (point < MIN_POINT) {
            throw new IllegalArgumentException("잔고가 부족합니다.");
        }

        if (point > MAX_POINT) {
            throw new IllegalArgumentException("최대 잔고는 10,000,000 포인트 입니다.");
        }
    }

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public long addAmount(long amount) {
        return point + amount;
    }
}
