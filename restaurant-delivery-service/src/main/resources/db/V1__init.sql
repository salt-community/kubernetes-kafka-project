create extension if not exists pgcrypto;

create table if not exists delivery (
  id uuid primary key default gen_random_uuid(),
  order_id uuid not null,
  driver_id varchar(255),
  status varchar(32) not null,
  assigned_at timestamp,
  completed_at timestamp
);

create index if not exists idx_delivery_order on delivery(order_id);
