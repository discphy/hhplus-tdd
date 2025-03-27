package io.hhplus.tdd.point.service.command;

import io.hhplus.tdd.point.model.Amount;
import io.hhplus.tdd.point.model.Point;
import io.hhplus.tdd.point.model.TransactionType;
import io.hhplus.tdd.point.model.UsePoint;

public class UsePointCommand implements PointCommand {

    private final long userId;
    private final Point point;

    private UsePointCommand(long userId, long amount) {
        this.userId = userId;
        this.point = UsePoint.of(Amount.of(amount));
    }

    public static UsePointCommand of(long userId, long amount) {
        return new UsePointCommand(userId, amount);
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public long getAmount() {
        return point.getAmount();
    }

    @Override
    public TransactionType getType() {
        return point.getTransactionType();
    }
}
