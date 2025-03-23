package io.hhplus.tdd.point.service.command;

import io.hhplus.tdd.point.model.TransactionType;

public interface PointCommand {

    long getUserId();

    long getAmount();

    TransactionType getType();
}
