CREATE TABLE orders (
    id               UUID PRIMARY KEY,
    customer_id      VARCHAR(64)              NOT NULL,
    delivery_address VARCHAR(255)             NOT NULL,
    status           VARCHAR(20)              NOT NULL,
    total_amount     NUMERIC(12, 2)           NOT NULL,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    version          BIGINT                   NOT NULL
);

CREATE INDEX ix_orders_customer_id ON orders (customer_id);

CREATE TABLE order_items (
    order_id   UUID           NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    sku        VARCHAR(64)    NOT NULL,
    quantity   INTEGER        NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL
);

CREATE INDEX ix_order_items_order_id ON order_items (order_id);
