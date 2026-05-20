CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,

    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20) NOT NULL,

    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,

    role VARCHAR(20) NOT NULL,

    enabled BOOLEAN DEFAULT FALSE,

    balance DECIMAL(10,2) DEFAULT 0
);

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(255) NOT NULL,

    description TEXT,

    price DECIMAL(10,2) NOT NULL,

    quantity INTEGER NOT NULL,

    image_url VARCHAR(500),

    seller_id BIGINT
);

ALTER TABLE products
ADD COLUMN IF NOT EXISTS seller_id BIGINT;

CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS product_categories (
    product_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,

    PRIMARY KEY (product_id, category_id),

    CONSTRAINT fk_product_categories_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_product_categories_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON DELETE CASCADE
);