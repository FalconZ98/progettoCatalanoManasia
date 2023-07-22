DROP DATABASE IF EXISTS mancat;
CREATE DATABASE IF NOT EXISTS mancat;
USE mancat;
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    username VARCHAR(45) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    store_id INT,
    enabled TINYINT(1) NOT NULL DEFAULT 1
);
CREATE TABLE IF NOT EXISTS store (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(16) NOT NULL
);
CREATE TABLE IF NOT EXISTS credit_card (
    id INT PRIMARY KEY AUTO_INCREMENT,
    balance DECIMAL(10, 2) NOT NULL,
    number CHAR(255) NOT NULL,
    enabled TINYINT(1) NOT NULL DEFAULT 0,
    owner_id INT,
    store_id INT
);
CREATE TABLE IF NOT EXISTS users_registered_stores (
    user_id INT,
    store_id INT,
    PRIMARY KEY (user_id, store_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (store_id) REFERENCES store(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transaction (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(255) NOT NULL,
    time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    credit_card_id INT,
    amount INT
);

INSERT INTO store(id, name) VALUES (1, 'Test Store');
INSERT INTO roles(id, name) VALUES (1,'ROLE_ADMIN'),(2, 'ROLE_MERCHANT'),(3, 'ROLE_CUSTOMER');
INSERT INTO users(id, email, username, first_name, last_name, password, store_id, role_id, enabled)
VALUES (1, 'admin@mancat.it','admin','admin','test','$2a$10$AWppFeS60D..f/4PpOF/juj0CLu1sWgQ/TrifH69SLY4OT197nMF6', null, 1,1),
(2, 'merchant@mancat.it','merchant','merchant','test','$2a$10$AWppFeS60D..f/4PpOF/juj0CLu1sWgQ/TrifH69SLY4OT197nMF6', 1, 2,1),
(3, 'customer@mancat.it','customer','customer','test','$2a$10$AWppFeS60D..f/4PpOF/juj0CLu1sWgQ/TrifH69SLY4OT197nMF6', 1, 3,1);
INSERT INTO credit_card(balance, number, owner_id, store_id) VALUES (1000, '5000123456780001', null, 1);
INSERT INTO users_registered_stores(user_id, store_id) VALUES(3, 1);
ALTER TABLE credit_card ADD CONSTRAINT fk_credit_card_owner_id FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE, ADD CONSTRAINT fk_credit_card_store_id FOREIGN KEY (store_id) REFERENCES store(id) ON DELETE CASCADE;
ALTER TABLE users ADD CONSTRAINT fk_user_role_id FOREIGN KEY (role_id) REFERENCES roles(id), ADD CONSTRAINT fk_user_store_id FOREIGN KEY (store_id) REFERENCES store(id) ON DELETE CASCADE;
ALTER TABLE transaction ADD CONSTRAINT fk_transaction_credit_card_id FOREIGN KEY (credit_card_id) REFERENCES credit_card(id) ON DELETE CASCADE;