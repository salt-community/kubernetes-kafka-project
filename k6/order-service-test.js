import { check, sleep } from 'k6';
import { Writer, Connection, SchemaRegistry } from 'k6/x/kafka';

// Connect to Kafka (exposed via localhost)
const writer = new Writer({
    brokers: ['localhost:9092'],
    topic: 'order.created', // Your actual topic name
    autoCreateTopic: true,
});

export const options = {
    stages: [
        { duration: '30s', target: 50 },
    ],
};

export default function () {
    // The message consumers expect
    const messages = [
        {
            key: JSON.stringify({ orderId: "123" }),
            value: JSON.stringify({
                orderId: "123",
                status: "CREATED",
                totalPrice: 150.00
            }),
        },
    ];

    // Send the message
    const error = writer.produce({ messages: messages });

    check(error, {
        'is sent successfully': (err) => err == undefined,
    });

    sleep(1);
}

export function teardown() {
    writer.close();
}