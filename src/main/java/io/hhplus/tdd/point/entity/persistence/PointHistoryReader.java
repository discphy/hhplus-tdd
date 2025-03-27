package io.hhplus.tdd.point.entity.persistence;

import io.hhplus.tdd.point.entity.PointHistory;

import java.util.List;

public interface PointHistoryReader {

    List<PointHistory> findAllByUserIdOrderByUpdateMillisDesc(long userId);
}
