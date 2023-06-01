GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, INDEX, DROP, ALTER, CREATE TEMPORARY TABLES, LOCK TABLES ON xxx.* TO 'xxx'@'localhost';

create table user(
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    user_name varchar(255) not null,
    user_password varchar(1000) not null,
    user_level int default 0,
    user_xp int default 0
)ENGINE=InnoDB;

create table item(
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name varchar(255) not null,
    item_description text not null,
    item_value float,
    item_img varchar(255) default 'includes/img/ico_item_unknown.png'
)ENGINE=InnoDB;

create table quest(
    quest_id INT AUTO_INCREMENT PRIMARY KEY,
    quest_name varchar(255) not null,
    quest_description text not null,
    quest_level int,
    quest_duration_min int,
    quest_xp int
)ENGINE=InnoDB;

create table quest_drop(
    drop_id INT AUTO_INCREMENT PRIMARY KEY,
    drop_item int,
    drop_quest int,
    drop_amount_min int,
    drop_amount_max int,
    drop_chance float,
    foreign key (drop_item) references item(item_id),
    foreign key (drop_quest) references quest(quest_id)
)ENGINE=InnoDB;

create table user_quest(
    uq_user int,
    uq_quest int,
    uq_order int,
    uq_start_time timestamp default CURRENT_TIMESTAMP,
    uq_end_time timestamp default CURRENT_TIMESTAMP,
    primary key (uq_user, uq_quest),
    foreign key (uq_user) references user(user_id),
    foreign key (uq_quest) references quest(quest_id)
)ENGINE=InnoDB;

create table inventory(
    inv_id INT AUTO_INCREMENT PRIMARY KEY,
    inv_user int,
    inv_item int,
    inv_amount int,
    foreign key (inv_user) references user(user_id),
    foreign key (inv_item) references item(item_id)
)ENGINE=InnoDB;

insert into item(item_name, item_value, item_description)
values
    ('Bois', 1.0, 'Un morceau de bois');

insert into quest(quest_name, quest_description, quest_level, quest_duration_min, quest_xp)
values
    ('Couper du bois dans la forêt', 'Vous allez couper du bois en forêt', 0, '5', 5);

insert into quest_drop(drop_item, drop_quest, drop_amount_min, drop_amount_max, drop_chance)
values
    ((select item_id from item where item_name='Bois'),(select quest_id from quest where quest_name='Couper du bois dans la forêt'),1,5,90);