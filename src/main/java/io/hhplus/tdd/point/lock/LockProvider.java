package io.hhplus.tdd.point.lock;

import java.util.function.Supplier;

public interface LockProvider<T> {

    T lock(Long id, Supplier<T> supplier);
}
