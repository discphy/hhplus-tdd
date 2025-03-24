package io.hhplus.tdd.support;

import io.hhplus.tdd.point.controller.PointController;
import io.hhplus.tdd.point.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(controllers = {
    PointController.class,
})
public abstract class ControllerTestSupport extends FixtureTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected PointService pointService;

}
