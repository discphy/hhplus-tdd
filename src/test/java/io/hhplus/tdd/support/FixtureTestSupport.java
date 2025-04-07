package io.hhplus.tdd.support;

public abstract class FixtureTestSupport {

    protected static final long ANY_USER_ID = 1L;

    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
