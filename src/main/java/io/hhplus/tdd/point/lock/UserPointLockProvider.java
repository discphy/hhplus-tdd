package io.hhplus.tdd.point.lock;

import io.hhplus.tdd.point.entity.UserPoint;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Component
public class UserPointLockProvider implements LockProvider<UserPoint> {

    private final Map<Long, ReentrantLock> map = new ConcurrentHashMap<>();

    @Override
    public UserPoint lock(Long id, Supplier<UserPoint> supplier) {
        ReentrantLock reentrantLock = map.computeIfAbsent(id, k -> new ReentrantLock(true));

        reentrantLock.lock();
        try {
            return supplier.get();
        } finally {
            reentrantLock.unlock();
        }
    }
}
