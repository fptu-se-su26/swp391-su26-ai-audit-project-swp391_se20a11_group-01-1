package com.rms.restaurant_management_system.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rms.restaurant_management_system.controller.ApiExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ApiExceptionHandlerTest {
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        ApiErrorWriter writer = new ApiErrorWriter(mapper);
        mvc = MockMvcBuilders.standaloneSetup(new ErrorProbeController())
                .setControllerAdvice(new ApiExceptionHandler(writer))
                .addFilters(new CorrelationIdFilter())
                .build();
    }

    @Test
    void typedNotFoundUsesStableSchemaAndClientCorrelationId() throws Exception {
        mvc.perform(get("/probe/not-found").header(CorrelationIdFilter.HEADER, "client-request-42"))
                .andExpect(status().isNotFound())
                .andExpect(header().string(CorrelationIdFilter.HEADER, "client-request-42"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/probe/not-found"))
                .andExpect(jsonPath("$.correlationId").value("client-request-42"));
    }

    @Test
    void malformedJsonReturnsSafeBadRequest() throws Exception {
        mvc.perform(post("/probe/body").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"))
                .andExpect(header().exists(CorrelationIdFilter.HEADER));
    }

    @Test
    void unsupportedMethodReturns405() throws Exception {
        mvc.perform(post("/probe/not-found"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
    }

    @Test
    void unexpectedExceptionReturns500WithoutInternalMessage() throws Exception {
        mvc.perform(get("/probe/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(content().string(not(containsString("database-password-leak"))));
    }

    @RestController
    @RequestMapping("/probe")
    static class ErrorProbeController {
        @GetMapping("/not-found")
        String notFound() {
            throw new ResourceNotFoundException("Không tìm thấy dữ liệu thử nghiệm");
        }

        @PostMapping("/body")
        Object body(@RequestBody ProbeBody body) {
            return body;
        }

        @GetMapping("/unexpected")
        String unexpected() {
            throw new RuntimeException("database-password-leak");
        }
    }

    record ProbeBody(String value) {
    }
}
