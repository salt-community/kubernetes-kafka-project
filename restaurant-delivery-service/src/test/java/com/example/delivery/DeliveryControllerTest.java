package com.example.delivery;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.delivery.api.DeliveryController;
import com.example.delivery.service.DeliveryAssignmentService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DeliveryController.class)
class DeliveryControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    DeliveryAssignmentService service;

    @Test
    void completeReturns202() throws Exception {
        UUID id = UUID.randomUUID();
        mvc
            .perform(post("/api/deliveries/{orderId}/complete", id))
            .andExpect(status().isAccepted());
        verify(service).complete(id);
    }

    @Test
    void assignReturns202() throws Exception {
        UUID id = UUID.randomUUID();
        mvc.perform(post("/api/deliveries/{orderId}/assign", id)).andExpect(status().isAccepted());
        verify(service).assignDriverFor(id);
    }
}
