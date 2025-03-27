package io.hhplus.tdd.point.entity.persistence;

import io.hhplus.tdd.point.entity.UserPoint;

public interface UserPointWriter {

    UserPoint updatedPoint(long userId, long amount);
}
