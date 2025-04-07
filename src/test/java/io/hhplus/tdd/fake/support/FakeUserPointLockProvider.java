package io.hhplus.tdd.fake.support;

import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.lock.LockProvider;

import java.util.function.Supplier;

public class FakeUserPointLockProvider implements LockProvider<UserPoint> {

    @Override
    public UserPoint lock(Long id, Supplier<UserPoint> supplier) {
        return supplier.get();
    }
}
