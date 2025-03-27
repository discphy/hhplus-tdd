package io.hhplus.tdd.point.model;

public class UsePoint implements Point {

    private final Amount amount;

    private UsePoint(Amount amount) {
        this.amount = amount;
    }

    public static Point of(Amount amount) {
        return new UsePoint(amount);
    }

    @Override
    public long getAmount() {
        return - amount.getValue();
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.USE;
    }
}
