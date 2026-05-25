SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS library_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_db;

DROP TABLE IF EXISTS payment_record;
DROP TABLE IF EXISTS fine_record;
DROP TABLE IF EXISTS borrow_record;
DROP TABLE IF EXISTS book_copy;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS library_card;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    role VARCHAR(20) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_no VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    max_borrow_count INT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS library_card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_no VARCHAR(20) NOT NULL UNIQUE,
    student_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    issue_date DATE NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    publisher VARCHAR(100) NOT NULL,
    summary TEXT,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS book_copy (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    barcode VARCHAR(50) NOT NULL UNIQUE,
    location VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES book(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS borrow_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id BIGINT NOT NULL,
    copy_id BIGINT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    is_overdue TINYINT DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (card_id) REFERENCES library_card(id),
    FOREIGN KEY (copy_id) REFERENCES book_copy(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS fine_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    borrow_record_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    days INT NOT NULL,
    is_paid TINYINT DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student(id),
    FOREIGN KEY (borrow_record_id) REFERENCES borrow_record(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS payment_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    operator_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student(id),
    FOREIGN KEY (operator_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_student_no ON student(student_no);
CREATE INDEX idx_card_no ON library_card(card_no);
CREATE INDEX idx_card_student_id ON library_card(student_id);
CREATE INDEX idx_book_isbn ON book(isbn);
CREATE INDEX idx_book_title_author ON book(title, author);
CREATE INDEX idx_book_copy_barcode ON book_copy(barcode);
CREATE INDEX idx_book_copy_book_id ON book_copy(book_id);
CREATE INDEX idx_borrow_card_id ON borrow_record(card_id);
CREATE INDEX idx_borrow_return_date ON borrow_record(return_date);

INSERT INTO sys_user (username, password, role, create_time) VALUES
('office', '123456', 'OFFICE', NOW()),
('circulation', '123456', 'CIRCULATION', NOW()),
('acquisition', '123456', 'ACQUISITION', NOW());

INSERT INTO student (student_no, name, type, max_borrow_count, create_time) VALUES
('2021001', 'Zhang San', 'UNDERGRADUATE', 5, NOW()),
('2021002', 'Li Si', 'UNDERGRADUATE', 5, NOW()),
('2021003', 'Wang Wu', 'GRADUATE', 10, NOW()),
('2021004', 'Zhao Liu', 'GRADUATE', 10, NOW()),
('2020001', 'Qian Qi', 'TEACHER', 15, NOW()),
('2020002', 'Sun Ba', 'TEACHER', 15, NOW()),
('2021005', 'Zhou Jiu', 'UNDERGRADUATE', 5, NOW()),
('2021006', 'Wu Shi', 'GRADUATE', 10, NOW()),
('2021007', 'Zheng 11', 'UNDERGRADUATE', 5, NOW()),
('2021008', 'Wang Xiaoming', 'GRADUATE', 10, NOW());

INSERT INTO library_card (card_no, student_id, status, issue_date, create_time) VALUES
('C001', 1, 'NORMAL', '2021-09-01', NOW()),
('C002', 2, 'NORMAL', '2021-09-01', NOW()),
('C003', 3, 'NORMAL', '2021-09-01', NOW()),
('C004', 4, 'NORMAL', '2021-09-01', NOW()),
('C005', 5, 'NORMAL', '2021-09-01', NOW()),
('C006', 6, 'NORMAL', '2021-09-01', NOW()),
('C007', 7, 'NORMAL', '2021-09-01', NOW()),
('C008', 8, 'NORMAL', '2021-09-01', NOW()),
('C009', 9, 'NORMAL', '2021-09-01', NOW()),
('C010', 10, 'NORMAL', '2021-09-01', NOW());

INSERT INTO book (isbn, title, author, publisher, summary, create_time) VALUES
('9787010042507', 'Hong Lou Meng', 'Cao Xueqin', 'Renmin Wenxue', 'Classic Chinese novel', NOW()),
('9787020002207', 'San Guo Yan Yi', 'Luo Guanzhong', 'Renmin Wenxue', 'Classic Chinese novel', NOW()),
('9787020008037', 'Shui Hu Zhuan', 'Shi Nai\'an', 'Renmin Wenxue', 'Classic Chinese novel', NOW()),
('9787020008723', 'Xi You Ji', 'Wu Chengen', 'Renmin Wenxue', 'Classic Chinese novel', NOW()),
('9787302105633', 'Thinking in Java', 'Bruce Eckel', 'Machinery Industry', 'Java programming', NOW()),
('9787111213826', 'Computer Systems', 'Bryant', 'Machinery Industry', 'Computer systems', NOW()),
('9787111075547', 'Introduction to Algorithms', 'Cormen', 'Machinery Industry', 'Algorithms', NOW()),
('9787115116224', 'JavaScript Advanced', 'Zakas', 'People Posts', 'JavaScript', NOW()),
('9787115216349', 'Design Patterns', 'Gamma', 'People Posts', 'Design patterns', NOW()),
('9787115175593', 'Head First Java', 'Sierra', 'China Power', 'Java learning', NOW());

INSERT INTO book_copy (book_id, barcode, location, status, create_time) VALUES
(1, '97870100425070001', 'Zone A - Shelf 1', 'AVAILABLE', NOW()),
(1, '97870100425070002', 'Zone A - Shelf 1', 'AVAILABLE', NOW()),
(1, '97870100425070003', 'Zone A - Shelf 1', 'BORROWED', NOW()),
(2, '97870200022070001', 'Zone A - Shelf 2', 'AVAILABLE', NOW()),
(2, '97870200022070002', 'Zone A - Shelf 2', 'AVAILABLE', NOW()),
(3, '97870200080370001', 'Zone B - Shelf 1', 'AVAILABLE', NOW()),
(3, '97870200080370002', 'Zone B - Shelf 1', 'BORROWED', NOW()),
(4, '97870200087230001', 'Zone B - Shelf 2', 'AVAILABLE', NOW()),
(4, '97870200087230002', 'Zone B - Shelf 2', 'AVAILABLE', NOW()),
(4, '97870200087230003', 'Zone B - Shelf 2', 'AVAILABLE', NOW()),
(5, '97873021056330001', 'Zone C - Shelf 1', 'AVAILABLE', NOW()),
(5, '97873021056330002', 'Zone C - Shelf 1', 'BORROWED', NOW()),
(6, '97871112138260001', 'Zone C - Shelf 2', 'AVAILABLE', NOW()),
(6, '97871112138260002', 'Zone C - Shelf 2', 'AVAILABLE', NOW()),
(7, '97871110755470001', 'Zone D - Shelf 1', 'AVAILABLE', NOW()),
(7, '97871110755470002', 'Zone D - Shelf 1', 'AVAILABLE', NOW()),
(8, '97871151162240001', 'Zone D - Shelf 2', 'AVAILABLE', NOW()),
(8, '97871151162240002', 'Zone D - Shelf 2', 'AVAILABLE', NOW()),
(9, '97871152163490001', 'Zone E - Shelf 1', 'AVAILABLE', NOW()),
(10, '97871151755930001', 'Zone E - Shelf 2', 'AVAILABLE', NOW()),
(10, '97871151755930002', 'Zone E - Shelf 2', 'AVAILABLE', NOW());

INSERT INTO borrow_record (card_id, copy_id, borrow_date, due_date, return_date, is_overdue) VALUES
(1, 3, '2024-01-10', '2024-02-10', NULL, 1),
(1, 7, '2024-01-15', '2024-02-15', NULL, 1),
(2, 12, '2024-01-20', '2024-02-20', NULL, 0),
(3, 3, '2024-02-01', '2024-03-01', NULL, 0),
(4, 7, '2024-02-05', '2024-03-05', NULL, 0);

INSERT INTO fine_record (student_id, borrow_record_id, amount, days, is_paid) VALUES
(1, 1, 15.00, 150, 0),
(1, 2, 5.00, 50, 0);

SET FOREIGN_KEY_CHECKS = 1;
