package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.entity.persistence.UserPointWriter;
import io.hhplus.tdd.point.service.command.ChargePointCommand;
import io.hhplus.tdd.point.service.command.PointCommand;
import io.hhplus.tdd.point.service.command.UsePointCommand;
import io.hhplus.tdd.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class PointServiceSyncTest extends IntegrationTestSupport {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserPointWriter userPointWriter;

    @BeforeEach
    void setUp() {
        userPointWriter.updatedPoint(ANY_USER_ID, 0L);
    }

    @DisplayName("[동시성] 포인트 충전 최대 금액은 1,000만원 까지다.")
    @Test
    void syncChargePointWithGreaterThenMaxPoint() throws InterruptedException {
        // given
        int threadCount = 12;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        PointCommand command = ChargePointCommand.of(ANY_USER_ID, 1_000_000L);

        List<Exception> exceptions = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.updatePoint(command);
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // when
        UserPoint userPoint = pointService.readPoint(ANY_USER_ID);

        // then
        assertThat(userPoint.point()).isEqualTo(10_000_000L);
        assertThat(exceptions).hasSize(2)
            .allSatisfy(e -> assertThat(e)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 잔고는 10,000,000 포인트 입니다.")
            );
    }

    @DisplayName("[동시성] 포인트를 충전한다.")
    @Test
    void syncChargePoint() throws InterruptedException {
        // given
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        PointCommand command = ChargePointCommand.of(ANY_USER_ID, 1_000_000L);

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
        UserPoint userPoint = pointService.readPoint(ANY_USER_ID);

        // then
        assertThat(userPoint.point()).isEqualTo(5_000_000L);
    }

    @DisplayName("[동시성] 포인트 사용 시 잔고는 충분해야한다.")
    @Test
    void syncUsePointWithLessThenMinPoint() throws InterruptedException {
        // given
        int threadCount = 15;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        PointCommand command = UsePointCommand.of(ANY_USER_ID, 1_000L);

        List<Exception> exceptions = new ArrayList<>();

        userPointWriter.updatedPoint(ANY_USER_ID, 10_000L);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.updatePoint(command);
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // when
        UserPoint userPoint = pointService.readPoint(ANY_USER_ID);

        // then
        assertThat(userPoint.point()).isZero();
        assertThat(exceptions).hasSize(5)
            .allSatisfy(e -> assertThat(e)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔고가 부족합니다.")
            );
    }

    @DisplayName("[동시성] 포인트를 사용한다.")
    @Test
    void syncUsePoint() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        PointCommand command = UsePointCommand.of(ANY_USER_ID, 10_000L);

        userPointWriter.updatedPoint(ANY_USER_ID, 1_000_000L);

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
        UserPoint userPoint = pointService.readPoint(ANY_USER_ID);

        // then
        assertThat(userPoint.point()).isEqualTo(900_000L);
    }
}