-- Sample items
INSERT INTO item (id, item_id, quantity, price) VALUES
                                                    ('11111111-1111-1111-1111-111111111111', 101, 2, 49.90),
                                                    ('22222222-2222-2222-2222-222222222222', 202, 1, 129.00);

-- Payment: PENDING, amount = 49.90*2 + 129.00 = 228.80
INSERT INTO payment (id, order_id, amount, provider_payment_id, status, created_at, failure_reason)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
     'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
     228.80,
     NULL,
     'PENDING',
     '2025-09-10T08:30:00Z',
     NULL);

-- Link items to the payment
INSERT INTO payment_item (payment_id, item_id) VALUES
                                                   ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111'),
                                                   ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222');

INSERT INTO item (id, item_id, quantity, price) VALUES
                                                    ('33333333-3333-3333-3333-333333333333', 303, 3, 19.95),
                                                    ('44444444-4444-4444-4444-444444444444', 404, 5, 9.99);

-- amount = 19.95*3 + 9.99*5 = 59.85 + 49.95 = 109.80
INSERT INTO payment (id, order_id, amount, provider_payment_id, status, created_at, failure_reason)
VALUES
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
     'cccccccc-cccc-cccc-cccc-cccccccccccc',
     109.80,
     NULL,
     'PENDING',
     '2025-09-10T09:00:00Z',
     NULL);

INSERT INTO payment_item (payment_id, item_id) VALUES
                                                   ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '33333333-3333-3333-3333-333333333333'),
                                                   ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '44444444-4444-4444-4444-444444444444');

INSERT INTO item (id, item_id, quantity, price) VALUES
                                                    ('55555555-5555-5555-5555-555555555555', 505, 1, 499.00),
                                                    ('66666666-6666-6666-6666-666666666666', 606, 2, 149.50);

-- amount = 499.00*1 + 149.50*2 = 499.00 + 299.00 = 798.00
INSERT INTO payment (id, order_id, amount, provider_payment_id, status, created_at, failure_reason)
VALUES
    ('cccccccc-cccc-cccc-cccc-cccccccccccc',
     'dddddddd-dddd-dddd-dddd-dddddddddddd',
     798.00,
     NULL,
     'PENDING',
     '2025-09-10T09:30:00Z',
     NULL);

INSERT INTO payment_item (payment_id, item_id) VALUES
                                                   ('cccccccc-cccc-cccc-cccc-cccccccccccc', '55555555-5555-5555-5555-555555555555'),
                                                   ('cccccccc-cccc-cccc-cccc-cccccccccccc', '66666666-6666-6666-6666-666666666666');
