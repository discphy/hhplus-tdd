package io.hhplus.tdd.fake;

import io.hhplus.tdd.fake.support.FakePointService;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.lock.UserPointLockProvider;
import io.hhplus.tdd.point.service.command.ChargePointCommand;
import io.hhplus.tdd.point.service.command.PointCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class FakePointLockServiceTest {

    private FakePointService pointService;

    @BeforeEach
    void setUp() {
        pointService = new FakePointService(new UserPointLockProvider());
    }

    @DisplayName("Lock을 사용하여 동시성 문제를 해결하여 순차적으로 포인트를 충전한다.")
    @Test
    void overwriteChargeUpdatePoint() throws InterruptedException {
        // given
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        PointCommand command = ChargePointCommand.of(1L, 50_000L);

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

        // when
        UserPoint userPoint = pointService.readPoint(1L);

        // then
        assertThat(userPoint.point()).isEqualTo(100_000L);
    }
}