CREATE TABLE User (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Email VARCHAR(150) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Role ENUM('ADMIN', 'CUSTOMER') NOT NULL
);
CREATE TABLE Author (
    AuthorID INT AUTO_INCREMENT PRIMARY KEY,
    AuthorName VARCHAR(150) NOT NULL
);
CREATE TABLE Category (
    CategoryID INT AUTO_INCREMENT PRIMARY KEY,
    CategoryName VARCHAR(100) NOT NULL UNIQUE
);
CREATE TABLE Book (
    BookID INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(200) NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    StockQuantity INT NOT NULL,
    AuthorID INT NOT NULL,
    CategoryID INT NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_book_author
        FOREIGN KEY (AuthorID) REFERENCES Author(AuthorID),

    CONSTRAINT fk_book_category
        FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID)
);

CREATE TABLE Inventory (
    InventoryID INT AUTO_INCREMENT PRIMARY KEY,
    BookID INT NOT NULL UNIQUE,
    Quantity INT NOT NULL,

    CONSTRAINT fk_inventory_book
        FOREIGN KEY (BookID) REFERENCES Book(BookID)
        ON DELETE CASCADE
);

CREATE TABLE Orders (
    OrderID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    OrderDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    TotalAmount DECIMAL(10,2) NOT NULL,
    Status ENUM('PENDING', 'SHIPPED', 'DELIVERED', 'CANCELLED') NOT NULL,

    CONSTRAINT fk_order_user
        FOREIGN KEY (UserID) REFERENCES User(UserID)
);

CREATE TABLE OrderItem (
    OrderItemID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT NOT NULL,
    BookID INT NOT NULL,
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_orderitem_order
        FOREIGN KEY (OrderID) REFERENCES Orders(OrderID)
        ON DELETE CASCADE,

    CONSTRAINT fk_orderitem_book
        FOREIGN KEY (BookID) REFERENCES Book(BookID)
);

CREATE TABLE Review (
    ReviewID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    BookID INT NOT NULL,
    Rating INT CHECK (Rating BETWEEN 1 AND 5),
    Comments TEXT,

    CONSTRAINT fk_review_user
        FOREIGN KEY (UserID) REFERENCES User(UserID)
        ON DELETE CASCADE,

    CONSTRAINT fk_review_book
        FOREIGN KEY (BookID) REFERENCES Book(BookID)
        ON DELETE CASCADE,

    CONSTRAINT uq_user_book_review UNIQUE (UserID, BookID)
);

INSERT INTO Author (AuthorName) VALUES
('Robert Martin'),
('Joshua Bloch'),
('Martin Fowler'),
('Kathy Sierra');

INSERT INTO Category (CategoryName) VALUES
('Programming'),
('Software Engineering'),
('Databases'),
('Web Development');

INSERT INTO Book (Title, Price, StockQuantity, AuthorID, CategoryID)
VALUES
('Clean Code', 499.00, 50, 1, 2),
('Effective Java', 599.00, 40, 2, 1),
('Refactoring', 699.00, 30, 3, 2),
('Head First Java', 450.00, 60, 4, 1);

