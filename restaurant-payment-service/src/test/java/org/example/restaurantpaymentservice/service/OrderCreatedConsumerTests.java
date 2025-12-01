package org.example.restaurantpaymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.restaurantpaymentservice.evenDto.OrderMessageDto;
import org.example.restaurantpaymentservice.idempotent.ConsumedEvent;
import org.example.restaurantpaymentservice.idempotent.ConsumedEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCreatedConsumerTests {

    @Mock
    private PaymentService paymentService;

    @Mock
    private ConsumedEventRepository consumedEventRepository;

    private OrderCreatedConsumer consumer;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        consumer = new OrderCreatedConsumer(mapper, paymentService, consumedEventRepository);
    }

    @Test
    void listen_newEvent_persistsAndProcesses() {
        // given
        UUID eventId = UUID.randomUUID();
        String json = """
            {
              "eventId":"%s",
              "orderId":"4f4a7f7a-1c74-4c1f-b0fa-5e1b0a5bb111",
              "orderStatus":"PLACED",
              "createdAt":"2025-09-10T07:45:00Z",
              "totalPrice":228.8,
              "items":[
                 {"id":"11111111-1111-1111-1111-111111111111","itemId":101,"quantity":2,"price":49.9},
                 {"id":"22222222-2222-2222-2222-222222222222","itemId":202,"quantity":1,"price":129.0}
              ]
            }
            """.formatted(eventId);

        ConsumerRecord<String,String> record =
                new ConsumerRecord<>("order.created.v1", 0, 0L, eventId.toString(), json);

        when(consumedEventRepository.existsById(eventId)).thenReturn(false);

        // when
        consumer.listen(record);

        // then
        // saved idempotency record
        ArgumentCaptor<ConsumedEvent> consumedCaptor = ArgumentCaptor.forClass(ConsumedEvent.class);
        verify(consumedEventRepository).save(consumedCaptor.capture());
        ConsumedEvent saved = consumedCaptor.getValue();
        // exact eventId should be stored
        org.junit.jupiter.api.Assertions.assertEquals(eventId, saved.getEventId());

        // payment created from the parsed order
        verify(paymentService, times(1)).createPendingFromOrder(any(OrderMessageDto.class));

        // existsById was checked
        verify(consumedEventRepository).existsById(eventId);
    }

    @Test
    void listen_duplicateEvent_skipsProcessing() {
        // given
        UUID eventId = UUID.randomUUID();
        String json = """
            {"eventId":"%s","orderId":"4f4a7f7a-1c74-4c1f-b0fa-5e1b0a5bb111","orderStatus":"PLACED","createdAt":"2025-09-10T07:45:00Z","totalPrice":10.0,"items":[]}
            """.formatted(eventId);

        ConsumerRecord<String,String> record =
                new ConsumerRecord<>("order.created.v1", 0, 1L, eventId.toString(), json);

        when(consumedEventRepository.existsById(eventId)).thenReturn(true);

        // when
        consumer.listen(record);

        // then
        verify(consumedEventRepository, never()).save(any());
        verify(paymentService, never()).createPendingFromOrder(any());
    }

    @Test
    void listen_badJson_logsAndDoesNothing() {
        // given: invalid JSON (missing closing brace, for example)
        String badJson = """
            {"eventId":"%s","orderId":"4f4a7f7a-1c74-4c1f-b0fa-5e1b0a5bb111","orderStatus":"PLACED"
            """.formatted(UUID.randomUUID());
        ConsumerRecord<String,String> record =
                new ConsumerRecord<>("order.created.v1", 0, 2L, null, badJson);

        // when
        consumer.listen(record);

        // then
        verify(consumedEventRepository, never()).existsById(any());
        verify(consumedEventRepository, never()).save(any());
        verify(paymentService, never()).createPendingFromOrder(any());
    }
}
