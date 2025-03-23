package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.service.command.ChargePointCommand;
import io.hhplus.tdd.point.service.command.UsePointCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/point")
public class PointController {

    private final PointService pointService;

    @GetMapping("{id}")
    public UserPoint point(@PathVariable("id") long id) {
        return pointService.readPoint(id);
    }

    @GetMapping("{id}/histories")
    public List<PointHistory> history(@PathVariable("id") long id) {
        return pointService.readPointHistories(id);
    }

    @PatchMapping("{id}/charge")
    public UserPoint charge(@PathVariable("id") long id, @RequestBody long amount) {
        return pointService.chargePoint(ChargePointCommand.of(id, amount));
    }

    @PatchMapping("{id}/use")
    public UserPoint use(@PathVariable("id") long id, @RequestBody long amount) {
        return pointService.usePoint(UsePointCommand.of(id, amount));
    }
}
