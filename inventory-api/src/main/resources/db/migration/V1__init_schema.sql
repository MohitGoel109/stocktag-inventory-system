-- ============================================================
-- Inventory Management System — Initial Schema
-- ============================================================

CREATE TABLE app_user (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(200)        NOT NULL,
    email           VARCHAR(200)        NOT NULL UNIQUE,
    password_hash   VARCHAR(255)        NOT NULL,
    role            VARCHAR(20)         NOT NULL,            -- ADMIN | STAFF
    mobile_number   VARCHAR(50),
    address         VARCHAR(200),
    status          VARCHAR(20)         NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE | INACTIVE
    created_at      TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_role CHECK (role IN ('ADMIN','STAFF')),
    CONSTRAINT chk_user_status CHECK (status IN ('ACTIVE','INACTIVE'))
);

CREATE TABLE category (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(200)        NOT NULL UNIQUE,
    created_at      TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE product (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(200)        NOT NULL,
    quantity        INT                 NOT NULL DEFAULT 0,
    price           DECIMAL(12,2)       NOT NULL DEFAULT 0,
    description     VARCHAR(500),
    category_id     BIGINT              NOT NULL,
    created_at      TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT chk_product_quantity CHECK (quantity >= 0),
    CONSTRAINT chk_product_price CHECK (price >= 0)
);

CREATE TABLE customer (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(200)        NOT NULL,
    mobile_number   VARCHAR(50),
    email           VARCHAR(200),
    created_at      TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_code      VARCHAR(50)         NOT NULL UNIQUE,
    customer_id     BIGINT              NOT NULL,
    order_date      TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_paid      DECIMAL(12,2)       NOT NULL DEFAULT 0,
    created_by      BIGINT,
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    CONSTRAINT fk_order_user FOREIGN KEY (created_by) REFERENCES app_user(id)
);

-- Line items per order — the original system never stored these individually,
-- which made it impossible to see what products were in a past order.
CREATE TABLE order_item (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id        BIGINT              NOT NULL,
    product_id      BIGINT              NOT NULL,
    quantity        INT                 NOT NULL,
    unit_price      DECIMAL(12,2)       NOT NULL,
    CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_orderitem_product FOREIGN KEY (product_id) REFERENCES product(id),
    CONSTRAINT chk_orderitem_quantity CHECK (quantity > 0)
);

CREATE INDEX idx_product_category ON product(category_id);
CREATE INDEX idx_order_customer ON orders(customer_id);
CREATE INDEX idx_orderitem_order ON order_item(order_id);
CREATE INDEX idx_orderitem_product ON order_item(product_id);
