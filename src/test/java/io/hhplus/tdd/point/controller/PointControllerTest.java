package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.model.TransactionType;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    @DisplayName("포인트 잔고를 조회한다.")
    @Test
    void point() throws Exception {
        // given
        UserPoint response = new UserPoint(1L, 100_000L, System.currentTimeMillis());

        given(pointService.readPoint(anyLong()))
            .willReturn(response);

        // when & then
        mockMvc.perform(
                get("/point/{id}", 1L)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.point").value(100_000L))
            .andExpect(jsonPath("$.updateMillis").exists());
    }

    @DisplayName("포인트 내역을 조회한다.")
    @Test
    void history() throws Exception {
        // given
        List<PointHistory> response = List.of(
            new PointHistory(3L, 1L, 100_000L, TransactionType.CHARGE, System.currentTimeMillis()),
            new PointHistory(2L, 1L, -50_000L, TransactionType.USE, System.currentTimeMillis()),
            new PointHistory(1L, 1L, 10_000L, TransactionType.CHARGE, System.currentTimeMillis())
        );

        given(pointService.readPointHistories(anyLong()))
            .willReturn(response);

        // when & then
        mockMvc.perform(
                get("/point/{id}/histories", 1L)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[*].id").exists())
            .andExpect(jsonPath("$[*].userId").isNotEmpty())
            .andExpect(jsonPath("$[*].amount").isNotEmpty())
            .andExpect(jsonPath("$[*].type").isNotEmpty());
    }

    @DisplayName("포인트 충전은 0보다 큰 금액이여야 한다.")
    @Test
    void chargeWithInvalidAmount() throws Exception {
        // given
        long amount = -10_000L;

        // when & then
        mockMvc.perform(
                patch("/point/{id}/charge", 1L)
                    .content(String.valueOf(amount))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("금액은 0보다 커야합니다."));
    }

    @DisplayName("포인트를 충전한다.")
    @Test
    void charge() throws Exception {
        // given
        UserPoint response = new UserPoint(1L, 100_000L, System.currentTimeMillis());

        given(pointService.chargePoint(any()))
            .willReturn(response);

        // when & then
        mockMvc.perform(
                patch("/point/{id}/charge", 1L)
                    .content(String.valueOf(100_000L))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.point").value(100_000L))
            .andExpect(jsonPath("$.updateMillis").exists());
    }

    @DisplayName("포인트 사용은 0보다 큰 금액이여야 한다.")
    @Test
    void useWithInvalidAmount() throws Exception {
        // given
        long amount = -10_000L;

        // when & then
        mockMvc.perform(
                patch("/point/{id}/use", 1L)
                    .content(String.valueOf(amount))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("금액은 0보다 커야합니다."));
    }

    @DisplayName("포인트를 사용한다.")
    @Test
    void use() throws Exception {
        // given
        UserPoint response = new UserPoint(1L, 1_000L, System.currentTimeMillis());

        given(pointService.usePoint(any()))
            .willReturn(response);

        // when & then
        mockMvc.perform(
                patch("/point/{id}/use", 1L)
                    .content(String.valueOf(90_000L))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.point").value(1_000L))
            .andExpect(jsonPath("$.updateMillis").exists());
    }

}