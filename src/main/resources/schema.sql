CREATE DATABASE homecraft;
USE homecraft;

CREATE TABLE admin (
                       id INT NOT NULL AUTO_INCREMENT,
                       email NVARCHAR(150) NOT NULL,
                       password_hash NVARCHAR(255) NOT NULL,
                       PRIMARY KEY (id)
);

INSERT INTO admin (email, password_hash)
VALUES (
           'admin1@gmail.com',
           '$2a$12$RlddfEUQbKIFxRduAFoZO79WCk.R9xU8bWWMPF3xyhTqU1ym'
       );

DROP TABLE IF EXISTS meetup_list;
DROP TABLE IF EXISTS meetup;
DROP TABLE IF EXISTS workshop_cust;
DROP TABLE IF EXISTS workshop;
DROP TABLE IF EXISTS cust_request;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS seller_rejected;
DROP TABLE IF EXISTS seller;
DROP TABLE IF EXISTS customer;

CREATE TABLE customer (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          profile_image VARCHAR(255) NOT NULL,
                          phone VARCHAR(10) NOT NULL,
                          location VARCHAR(100) NOT NULL,
                          interests VARCHAR(500) NOT NULL,
                          email VARCHAR(100) NOT NULL UNIQUE,
                          password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE seller (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        profile_image VARCHAR(255) NOT NULL,
                        phone VARCHAR(10) NOT NULL,
                        location VARCHAR(100) NOT NULL,
                        craft VARCHAR(500) NOT NULL,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        password_hash VARCHAR(255) NOT NULL,
                        license_number VARCHAR(50) NOT NULL,
                        aadhar_image VARCHAR(255) NOT NULL,
                        is_verified BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE seller_rejected (
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 email VARCHAR(100) NOT NULL UNIQUE,
                                 password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE product (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         seller_id INT NOT NULL,
                         name VARCHAR(150) NOT NULL,
                         description VARCHAR(500),
                         image_path VARCHAR(255) NOT NULL,
                         category VARCHAR(50) NOT NULL,
                         type VARCHAR(100) NOT NULL,
                         price DECIMAL(10,2) NOT NULL,
                         customizations VARCHAR(255),
                         location VARCHAR(100) NOT NULL,
                         FOREIGN KEY (seller_id) REFERENCES seller(id) ON DELETE CASCADE
);

CREATE TABLE orders (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        product_id INT NOT NULL,
                        customer_id INT NOT NULL,
                        seller_id INT NOT NULL,
                        name VARCHAR(100) NOT NULL,
                        price DECIMAL(10,2) NOT NULL,
                        quantity INT NOT NULL CHECK (quantity >= 1),
                        customizations VARCHAR(255),
                        status VARCHAR(20) NOT NULL DEFAULT 'Pending',
                        FOREIGN KEY (product_id) REFERENCES product(id),
                        FOREIGN KEY (customer_id) REFERENCES customer(id),
                        FOREIGN KEY (seller_id) REFERENCES seller(id)
);

CREATE TABLE cust_request (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              orders_id INT NOT NULL,
                              product_id INT NOT NULL,
                              customer_id INT NOT NULL,
                              seller_id INT NOT NULL,
                              info TEXT NOT NULL,
                              CONSTRAINT fk_cust_request_order
                                  FOREIGN KEY (orders_id)
                                      REFERENCES orders(id)
                                      ON DELETE CASCADE,
                              CONSTRAINT fk_cust_request_product
                                  FOREIGN KEY (product_id)
                                      REFERENCES product(id)
                                      ON DELETE CASCADE,
                              CONSTRAINT fk_cust_request_customer
                                  FOREIGN KEY (customer_id)
                                      REFERENCES customer(id)
                                      ON DELETE CASCADE,
                              CONSTRAINT fk_cust_request_seller
                                  FOREIGN KEY (seller_id)
                                      REFERENCES seller(id)
                                      ON DELETE CASCADE
);

CREATE TABLE workshop (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          seller_id INT NOT NULL,
                          title VARCHAR(150) NOT NULL,
                          description TEXT,
                          topics VARCHAR(255) NOT NULL,
                          location VARCHAR(100) NOT NULL,
                          date_time DATETIME NOT NULL,
                          duration_minutes INT NOT NULL,
                          is_paid BOOLEAN NOT NULL DEFAULT FALSE,
                          price DECIMAL(10,2) DEFAULT NULL,
                          status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                          created_at DATETIME NOT NULL,
                          CONSTRAINT fk_workshop_seller
                              FOREIGN KEY (seller_id)
                                  REFERENCES seller(id)
                                  ON DELETE CASCADE
);

CREATE TABLE workshop_cust (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               workshop_id INT NOT NULL,
                               seller_id INT NOT NULL,
                               cust_list TEXT NOT NULL,
                               CONSTRAINT fk_wc_workshop
                                   FOREIGN KEY (workshop_id)
                                       REFERENCES workshop(id)
                                       ON DELETE CASCADE,
                               CONSTRAINT fk_wc_seller
                                   FOREIGN KEY (seller_id)
                                       REFERENCES seller(id)
                                       ON DELETE CASCADE
);

CREATE TABLE meetup (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(150) NOT NULL,
                        description VARCHAR(1000) NOT NULL,
                        topics VARCHAR(500) NOT NULL,
                        location VARCHAR(100) NOT NULL,
                        date_time DATETIME NOT NULL,
                        created_by_type VARCHAR(20) NOT NULL,
                        created_by_id INT NOT NULL,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE meetup_list (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             meetup_id INT NOT NULL,
                             creator_type VARCHAR(20) NOT NULL,
                             creator_id INT NOT NULL,
                             attendees VARCHAR(2000) NOT NULL,
                             CONSTRAINT fk_meetup_list_meetup
                                 FOREIGN KEY (meetup_id)
                                     REFERENCES meetup(id)
                                     ON DELETE CASCADE
);