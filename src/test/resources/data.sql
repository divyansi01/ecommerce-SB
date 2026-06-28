-- Test data automatically inserted before tests run
INSERT INTO products (id, name, price, stock_quantity, category, is_active, created_at, updated_at)
VALUES
(1, 'Laptop', 999.99, 50, 'Electronics', true, NOW(), NOW()),
(2, 'Mouse', 29.99, 200, 'Electronics', true, NOW(), NOW()),
(3, 'Keyboard', 79.99, 100, 'Electronics', true, NOW(), NOW()),
(4, 'Book', 19.99, 500, 'Books', true, NOW(), NOW());

INSERT INTO users (id, username, email, password, role, created_at, updated_at)
VALUES
(1, 'admin', 'admin@test.com', '$2a$10$encoded', 'ADMIN', NOW(), NOW()),
(2, 'user1', 'user1@test.com', '$2a$10$encoded', 'USER', NOW(), NOW());

------
ALTER TABLE products ALTER COLUMN id RESTART WITH 5;
ALTER TABLE users ALTER COLUMN id RESTART WITH 3;