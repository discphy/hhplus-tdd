package io.hhplus.tdd.point.entity.persistence;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.service.command.PointCommand;

public interface PointHistoryWriter {

    PointHistory save(PointCommand command);
}
