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

    image_url VARCHAR(500)
);