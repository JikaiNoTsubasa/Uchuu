GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, INDEX, DROP, ALTER, CREATE TEMPORARY TABLES, LOCK TABLES ON xxx.* TO 'xxx'@'localhost';

create table user(
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    user_name varchar(255) not null,
    user_password varchar(1000) not null
);