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
    quest_xp int,
    quest_repeatable boolean default true
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

create table quest_done(
   uq_user int,
   uq_quest int,
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

create table building(
    building_id INT AUTO_INCREMENT PRIMARY KEY,
    building_name varchar(255) not null
)ENGINE=InnoDB;

create table building_recipe(
    recipe_building int,
    recipe_item int,
    recipe_amount int,
    foreign key (recipe_building) references building(building_id),
    foreign key (recipe_item) references item(item_id)
)ENGINE=InnoDB;

create table building_production(
    prod_building int,
    prod_item int,
    prod_amount int,
    prod_level int,
    foreign key (prod_building) references building(building_id),
    foreign key (prod_item) references item(item_id)
)ENGINE=InnoDB;

create table user_building(
    ub_user int,
    ub_building int,
    ub_level int,
    foreign key (ub_user) references user(user_id),
    foreign key (ub_building) references building(building_id)
)ENGINE=InnoDB;

insert into item(item_name, item_value, item_description, item_img)
values
    ('Bois', 1.0, 'Un morceau de bois', 'includes/img/ico_item_wood.png'),
    ('Pierre',1.0,'Un morceau de pierre'),
    ('Charbon',1.2,'Un morceau de chabon'),
    ('Fer Brute',1.5,'Un morceau de fer brute'),
    ('Cuivre Brute',1.6,'Un morceau de cuivre brute'),
    ('Or Brute',2.0,'Un morceau d''or brute');

insert into quest(quest_name, quest_description, quest_level, quest_duration_min, quest_xp)
values
    ('Couper du bois dans la forêt', 'Vous allez couper du bois en forêt', 0, 1, 5),
    ('Ramasser du bois et des pierres', 'Vous allez ramasser du bois et des pierres en forêt', 1, 2, 6),
    ('Mine Niveau I', 'Vous allez à la mine au premier sous sol', 2, 5, 10),
    ('Mine Niveau II', 'Vous allez à la mine au deuxième sous sol', 3, 8, 40),
    ('Mine Niveau III', 'Vous allez à la mine au troisième sous sol', 4, 12, 50);

insert into quest_drop(drop_item, drop_quest, drop_amount_min, drop_amount_max, drop_chance)
values
    ((select item_id from item where item_name='Bois'),(select quest_id from quest where quest_name='Couper du bois dans la forêt'),1,5,90),

    ((select item_id from item where item_name='Bois'),(select quest_id from quest where quest_name='Ramasser du bois et des pierres'),1,3,80),
    ((select item_id from item where item_name='Pierre'),(select quest_id from quest where quest_name='Ramasser du bois et des pierres'),2,5,80),

    ((select item_id from item where item_name='Pierre'),(select quest_id from quest where quest_name='Mine Niveau I'),5,10,80),
    ((select item_id from item where item_name='Charbon'),(select quest_id from quest where quest_name='Mine Niveau I'),1,5,10),
    ((select item_id from item where item_name='Fer Brute'),(select quest_id from quest where quest_name='Mine Niveau I'),1,3,1),
    ((select item_id from item where item_name='Cuivre Brute'),(select quest_id from quest where quest_name='Mine Niveau I'),1,1,0.2),

    ((select item_id from item where item_name='Pierre'),(select quest_id from quest where quest_name='Mine Niveau II'),10,20,80),
    ((select item_id from item where item_name='Charbon'),(select quest_id from quest where quest_name='Mine Niveau II'),5,10,30),
    ((select item_id from item where item_name='Fer Brute'),(select quest_id from quest where quest_name='Mine Niveau II'),2,6,5),
    ((select item_id from item where item_name='Cuivre Brute'),(select quest_id from quest where quest_name='Mine Niveau II'),2,6,5),
    ((select item_id from item where item_name='Or Brute'),(select quest_id from quest where quest_name='Mine Niveau II'),1,5,1),

    ((select item_id from item where item_name='Pierre'),(select quest_id from quest where quest_name='Mine Niveau III'),15,20,80),
    ((select item_id from item where item_name='Charbon'),(select quest_id from quest where quest_name='Mine Niveau III'),10,15,50),
    ((select item_id from item where item_name='Fer Brute'),(select quest_id from quest where quest_name='Mine Niveau III'),8,12,30),
    ((select item_id from item where item_name='Cuivre Brute'),(select quest_id from quest where quest_name='Mine Niveau III'),11,12,20),
    ((select item_id from item where item_name='Or Brute'),(select quest_id from quest where quest_name='Mine Niveau III'),4,8,5);