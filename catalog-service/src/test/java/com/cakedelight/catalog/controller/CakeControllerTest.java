package com.cakedelight.catalog.controller;

import com.cakedelight.catalog.entity.Cake;
import com.cakedelight.catalog.exception.CakeNotFoundException;
import com.cakedelight.catalog.service.CakeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CakeController.class)
class CakeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CakeService cakeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void postCakes_rejectsMissingName() throws Exception {
        var payload = new Cake();
        payload.setPrice(BigDecimal.valueOf(10));

        mockMvc.perform(post("/cakes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postCakes_rejectsNonPositivePrice() throws Exception {
        var payload = new Cake();
        payload.setName("Test Cake");
        payload.setPrice(BigDecimal.ZERO);

        mockMvc.perform(post("/cakes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCake_whenNotFound_returns404() throws Exception {
        when(cakeService.findById(1L)).thenThrow(new CakeNotFoundException(1L));

        mockMvc.perform(get("/cakes/1"))
                .andExpect(status().isNotFound());
    }
}
