# 🍽️ 📝 Restaurant Order Service 🍔 🍕

*"A microservice simulating restaurant communication through Apache Kafka"*

## Introduction

The Order Service is part of an upskilling project focused on learning and applying Apache Kafka in a microservice
architecture.
It implements event-driven communication patterns such as the Outbox Pattern and Idempotent Consumers to ensure reliable
message delivery and processing across services.

The other 3 microservices:

- [Payment Service](https://github.com/salt-community/restaurant-payment-service) 💳
- [Kitchen Service](https://github.com/salt-community/restaurant-kitchen-service) 🧑‍🍳
- [Delivery Service](https://github.com/salt-community/restaurant-delivery-service) 🚚

Additionally, we have a [frontend](https://github.com/salt-community/restaurant-frontend-simulation) to visualize the backend flow.

To simulate kafka communication scroll down for further instructions.

## Producing events

The Order Service produces two Kafka topics. Events are versioned to allow backward-compatible evolution:
The eventId are both included in the headers and in the message itself.

- `order.created.v1`
- `order.canceled.v1`

## Consuming events

Since the Order Service is responsible for initiating and maintaining the lifecycle of an order, it listens to topics
that indicate failure or cancellation from other services:

- `payment.failed`
- `kitchen.canceled`

## Outbox Pattern

The Outbox Pattern ensures reliable event publishing by persisting messages before sending them to Kafka.

In this application, the pattern is implemented as follows:

- `Outbox Entity`: Stores event details including payload, status, and last error (if any) and can be used as a form of
  DLQ.

- `Outbox Service`: Handles event creation and transactional persistence.

- `Outbox Worker`: A scheduled job that fetches up to 100 pending events, asynchronously publishes them to Kafka and
  updates their
  status.

Events are written to the outbox table in parallel with:

- Creating a new order.

- Consuming a canceled or failed event from another service.

- Receiving an API call to cancel an order.

## Idempotency

To prevent duplicate processing, idempotency is enforced using a consumed_event table.
Each incoming message has a unique eventId. The consumer checks if this eventId has already been processed:

- If yes → return without doing anything.

- If no → process the message and mark it as consumed.

## REST API

The service exposes a REST controller for managing orders:

📖 [Swagger UI](http://localhost:8080/swagger-ui/index.html) 📖

- `POST /orders` → Creates a new order. This will:

    - Persist the order in the database.
    - Write an order.created.v1 event to the outbox.
    - The Outbox Worker later publishes this event to Kafka.

- `GET /orders` → Retrieve all orders no matter status.

- `GET /orders/{id}` → Retrieve an order by ID.

- `DELETE /orders/cancel/{id}` → Cancel an order (publishes an order.canceled.v1 event).

## Getting started

### Prerequisites

- Docker, Postman, IntelliJ or other IDE
- [Import Postman Collection](ORDER-SERVICE.postman_collection.json)

This guide is isolated for trying out the order-service.
To simultaneously run all 4 microservice documentation will come later on...

1. Clone this repository
2. Request the `.env` file and put it in the project
3. Make sure your docker desktop app is alive and run these commands to spin up all necessary services:

```bash
docker run -d --name broker -p 9092:9092 apache/kafka:latest
docker compose -f docker-compose.yml up -d
```

4. Run the application in your IDE.

### Create an order

1. Post following body to `http://localhost:8081/api/orders` to receive the `orderId`.

```bash
{
  "items": [
    {
      "itemId": 1,
      "quantity": 2,
      "price": 59.0
    },
    {
      "itemId": 1,
      "quantity": 2,
      "price": 19.0
    },
    {
      "itemId": 2,
      "quantity": 4,
      "price": 14.0
    }
  ],
  "totalPrice": 212
}

```

2. Inside your Broker navigate into

```bash 
cd opt/kafka/bin
```

3. Consume order.created.v1 event

```bash 
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic order.created.v1 --from-beginning
```

### Cancel an order

#### 1. To Produce an order.canceled.v1 event either:

- Send a DeleteMapping to `http://localhost:8081/api/orders/{orderId}` to cancel the order and trigger the
  order.canceled.v1 event.

Alternatively, manually produce an event inside the broker:

```bash 
cd opt/kafka/bin
```

```bash 
./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic order.canceled.v1
```

Sending this with correct filled out fields:

```bash
{"eventId": "uuid-here", "orderId": "uuid-here", "orderStatus": "CANCELED"}
```

#### 2. Listening/consume order.canceled.v1 event

```bash 
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic order.canceled.v1 --from-beginning
```
