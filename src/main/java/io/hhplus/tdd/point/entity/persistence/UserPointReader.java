package io.hhplus.tdd.point.entity.persistence;

import io.hhplus.tdd.point.entity.UserPoint;

public interface UserPointReader {

    UserPoint findByUserId(Long userId);
}
