package io.hhplus.tdd.fake;

import io.hhplus.tdd.fake.support.FakePointService;
import io.hhplus.tdd.fake.support.FakeUserPointLockProvider;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.service.command.ChargePointCommand;
import io.hhplus.tdd.point.service.command.PointCommand;
import io.hhplus.tdd.support.FixtureTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class FakePointWithoutLockServiceTest extends FixtureTestSupport {

    private FakePointService pointService;

    @BeforeEach
    void setUp() {
        pointService = new FakePointService(new FakeUserPointLockProvider());
    }

    @DisplayName("동시성 이슈가 생기면 포인트는 덮어쓴다.")
    @Test
    void overwriteChargeUpdatePoint() throws InterruptedException {
        // given
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        PointCommand command = ChargePointCommand.of(ANY_USER_ID, 50_000L);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.updatePoint(command);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // when
        UserPoint userPoint = pointService.readPoint(ANY_USER_ID);

        // then
        assertThat(userPoint.point()).isEqualTo(50_000L);
    }
}