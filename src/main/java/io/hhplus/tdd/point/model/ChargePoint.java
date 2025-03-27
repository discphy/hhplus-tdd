package io.hhplus.tdd.point.model;

public class ChargePoint implements Point {

    private final Amount amount;

    private ChargePoint(Amount amount) {
        this.amount = amount;
    }

    public static ChargePoint of(Amount amount) {
        return new ChargePoint(amount);
    }

    @Override
    public long getAmount() {
        return amount.getValue();
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.CHARGE;
    }
}
