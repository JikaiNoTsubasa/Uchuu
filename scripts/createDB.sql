GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, INDEX, DROP, ALTER, CREATE TEMPORARY TABLES, LOCK TABLES ON xxx.* TO 'xxx'@'localhost';

create table user(
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    user_name varchar(255) not null,
    user_password varchar(1000) not null,
    user_level int default 0,
    user_xp int default 0,
    user_admin boolean default false
)ENGINE=InnoDB;

create table item(
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name varchar(255) not null,
    item_description text not null,
    item_value float,
    item_img varchar(255) default 'includes/img/ico_item_unknown.png'
)ENGINE=InnoDB;

create table building(
    building_id INT AUTO_INCREMENT PRIMARY KEY,
    building_name varchar(255) not null
)ENGINE=InnoDB;

create table quest(
    quest_id INT AUTO_INCREMENT PRIMARY KEY,
    quest_name varchar(255) not null,
    quest_description text not null,
    quest_level int,
    quest_duration_min int,
    quest_xp int,
    quest_repeatable boolean default true,
    quest_plan int null default null,
    foreign key (quest_plan) references building(building_id)
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
   qd_user int,
   qd_quest int,
   qd_start_time timestamp default CURRENT_TIMESTAMP,
   qd_end_time timestamp default CURRENT_TIMESTAMP,
   primary key (qd_user, qd_quest),
   foreign key (qd_user) references user(user_id),
   foreign key (qd_quest) references quest(quest_id)
)ENGINE=InnoDB;

create table inventory(
    inv_id INT AUTO_INCREMENT PRIMARY KEY,
    inv_user int,
    inv_item int,
    inv_amount int,
    foreign key (inv_user) references user(user_id),
    foreign key (inv_item) references item(item_id)
)ENGINE=InnoDB;

create table recipe(
    recipe_id int primary key,
    recipe_name varchar(255) null default 'Recipe'
)ENGINE=InnoDB;

create table recipe_item(
   ri_id int primary key,
   ri_recipe int,
   ri_amount int,
   ri_item int,
   foreign key (ri_recipe) references recipe(recipe_id),
   foreign key (ri_item) references item(item_id)
)ENGINE=InnoDB;

create table building_level(
    bl_building int,
    bl_level int,
    bl_recipe int,
    foreign key (bl_recipe) references recipe(recipe_id)
)ENGINE=InnoDB;

create table user_building(
    ub_user int,
    ub_building int,
    ub_level int default 0,
    foreign key (ub_user) references user(user_id),
    foreign key (ub_building) references building(building_id)
)ENGINE=InnoDB;

insert into building(building_id, building_name) VALUES (1, 'Maison');

insert into item(item_id, item_name, item_value, item_description, item_img)
values
    (1,'Bois', 1.0, 'Un morceau de bois', 'includes/img/ico_item_wood.png'),
    (2,'Pierre',1.0,'Un morceau de pierre', 'includes/img/ico_item_unknown.png'),
    (3,'Charbon',1.2,'Un morceau de chabon', 'includes/img/ico_item_unknown.png'),
    (4,'Fer Brute',1.5,'Un morceau de fer brute', 'includes/img/ico_item_unknown.png'),
    (5,'Cuivre Brute',1.6,'Un morceau de cuivre brute', 'includes/img/ico_item_unknown.png'),
    (6,'Or Brute',2.0,'Un morceau d''or brute', 'includes/img/ico_item_unknown.png');

insert into recipe(recipe_id, recipe_name)
VALUES
    (1,'Recette Maison lvl 0');

insert into recipe_item(ri_id, ri_recipe, ri_amount, ri_item)
VALUES
    (1,1,50,1),
    (2,1,50,2);

insert into building_level(bl_building, bl_level, bl_recipe)
values
    (1,0,1);

insert into quest(quest_id, quest_name, quest_description, quest_level, quest_duration_min, quest_xp, quest_repeatable, quest_plan)
values
    (1,'Couper du bois dans la forêt', 'Vous allez couper du bois en forêt.', 0, 1, 5, true, null),
    (2,'Ramasser du bois et des pierres', 'Vous allez ramasser du bois et des pierres en forêt.', 1, 2, 6, true, null),
    (3,'Mine Niveau I', 'Vous allez à la mine au premier sous sol.', 2, 5, 10, true, null),
    (4,'Mine Niveau II', 'Vous allez à la mine au deuxième sous sol.', 3, 8, 40, true, null),
    (5,'Mine Niveau III', 'Vous allez à la mine au troisième sous sol.', 4, 12, 50, true, null),
    (6,'Apprentissage: Maison', 'Vous apprennez a construire une maison. Une fois cette quête réalisé, vous pourrez construire des maisons.', 5, 30, 100, false, 1);

insert into quest_drop(drop_item, drop_quest, drop_amount_min, drop_amount_max, drop_chance)
values
    (1,1,1,5,90),

    (1,2,1,3,80),
    (2,2,2,5,80),

    (2,3,5,10,80),
    (3,3,1,5,10),
    (4,3,1,3,1),
    (5,3,1,1,0.2),

    (2,4,10,20,80),
    (3,4,5,10,30),
    (4,4,2,6,5),
    (5,4,2,6,5),
    (6,4,1,5,1),

    (2,5,15,20,80),
    (3,5,10,15,50),
    (4,5,8,12,30),
    (5,5,11,12,20),
    (6,5,4,8,5);

