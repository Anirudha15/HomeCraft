
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'HomeCraftDB')
BEGIN
    CREATE DATABASE HomeCraftDB;
END
GO
USE HomeCraftDB;
GO

IF OBJECT_ID('dbo.admin', 'U') IS NOT NULL DROP TABLE dbo.admin;
CREATE TABLE admin (
                       id INT IDENTITY(1,1) PRIMARY KEY,
                       email NVARCHAR(150) NOT NULL,
                       password_hash NVARCHAR(255) NOT NULL
);

INSERT INTO admin (email, password_hash)
VALUES (
           'admin@gmail.com',
           '$2a$11$ExampleHashForAdmin123ThisWouldNeedRealBcryptGeneration' 
       );


DROP TABLE IF EXISTS dbo.cust_response;
DROP TABLE IF EXISTS dbo.cust_request;
DROP TABLE IF EXISTS dbo.orders;
DROP TABLE IF EXISTS dbo.product;
DROP TABLE IF EXISTS dbo.workshop;
DROP TABLE IF EXISTS dbo.meetup;
DROP TABLE IF EXISTS dbo.seller_rejected;
DROP TABLE IF EXISTS dbo.seller;
DROP TABLE IF EXISTS dbo.customer;
GO

CREATE TABLE dbo.customer (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    profile_image VARCHAR(255) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    location VARCHAR(100) NOT NULL,
    interests VARCHAR(500) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);
GO

CREATE TABLE dbo.seller (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    profile_image VARCHAR(255) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    location VARCHAR(100) NOT NULL,
    craft VARCHAR(500) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    license_number VARCHAR(50) NOT NULL,
    aadhar_image VARCHAR(255) NOT NULL,
    is_verified BIT NOT NULL DEFAULT 0
);
GO

CREATE TABLE dbo.seller_rejected (
    id INT IDENTITY(1,1) PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);
GO

CREATE TABLE dbo.product (
    id INT IDENTITY(1,1) PRIMARY KEY,
    seller_id INT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    image_path VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    type VARCHAR(100) NOT NULL, 
    price DECIMAL(10,2) NOT NULL,
    customizations VARCHAR(255),
    location VARCHAR(100) NOT NULL,

    CONSTRAINT FK_product_seller
        FOREIGN KEY (seller_id)
        REFERENCES dbo.seller(id)
        ON DELETE CASCADE
);
GO

CREATE TABLE dbo.orders (
    id INT IDENTITY(1,1) PRIMARY KEY,

    product_id INT NOT NULL,
    customer_id INT NOT NULL,
    seller_id INT NOT NULL,

    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 1),
    customizations VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'Pending',

    CONSTRAINT FK_orders_product FOREIGN KEY (product_id) REFERENCES dbo.product(id),
    CONSTRAINT FK_orders_customer FOREIGN KEY (customer_id) REFERENCES dbo.customer(id),
    CONSTRAINT FK_orders_seller FOREIGN KEY (seller_id) REFERENCES dbo.seller(id)
);
GO

CREATE TABLE dbo.cust_request (
    id INT IDENTITY(1,1) PRIMARY KEY,

    orders_id INT NOT NULL,
    product_id INT NOT NULL,
    customer_id INT NOT NULL,
    seller_id INT NOT NULL,

    info TEXT NOT NULL,

    CONSTRAINT FK_request_order FOREIGN KEY (orders_id) REFERENCES dbo.orders(id) ON DELETE CASCADE,
    CONSTRAINT FK_request_product FOREIGN KEY (product_id) REFERENCES dbo.product(id),
    CONSTRAINT FK_request_customer FOREIGN KEY (customer_id) REFERENCES dbo.customer(id),
    CONSTRAINT FK_request_seller FOREIGN KEY (seller_id) REFERENCES dbo.seller(id)
);
GO

CREATE TABLE dbo.cust_response (
    id INT IDENTITY(1,1) PRIMARY KEY,
    request_id INT NOT NULL,
    response_text TEXT NOT NULL,
    CONSTRAINT FK_cust_response_request
        FOREIGN KEY (request_id)
        REFERENCES dbo.cust_request(id)
        ON DELETE CASCADE
);
GO

CREATE TABLE dbo.workshop (
    id INT IDENTITY(1,1) PRIMARY KEY,
    seller_id INT NOT NULL,
    topic VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    venue VARCHAR(100),
    timing DATETIME,
    capacity INT,
    is_paid BIT,
    CONSTRAINT FK_workshop_seller
        FOREIGN KEY (seller_id)
        REFERENCES dbo.seller(id)
        ON DELETE CASCADE
);
GO

CREATE TABLE dbo.meetup (
    id INT IDENTITY(1,1) PRIMARY KEY,
    theme VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    timing DATETIME NOT NULL
);
GO

SELECT TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
ORDER BY TABLE_NAME; 
