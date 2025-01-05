package com.smoke.weatherapp.exception;

import com.smoke.weatherapp.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest
@Import(GlobalExceptionHandler.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testHandleIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/temperature")
                        .param("latitude", "1000")
                        .param("longitude", "1000"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Constants.ERROR_INVALID_COORDINATES));
    }

    @Test
    public void testHandleRuntimeException() throws Exception {
        mockMvc.perform(get("/temperature")
                        .param("latitude", "40.7128")
                        .param("longitude", "-74.0060"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(Constants.ERROR_OPEN_METEO_API_FAILURE));
    }
}