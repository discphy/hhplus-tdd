package io.hhplus.tdd.point.service.command;

import io.hhplus.tdd.point.model.Amount;
import io.hhplus.tdd.point.model.ChargePoint;
import io.hhplus.tdd.point.model.Point;
import io.hhplus.tdd.point.model.TransactionType;

public class ChargePointCommand implements PointCommand {

    private final long userId;
    private final Point point;

    private ChargePointCommand(long userId, long amount) {
        this.userId = userId;
        this.point = ChargePoint.of(Amount.of(amount));
    }

    public static ChargePointCommand of(long userId, long amount) {
        return new ChargePointCommand(userId, amount);
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
