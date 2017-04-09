#DATABASE UPDATE: 1.4.0 -> 1.5.0

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.bot.butler.servedistance', '5');
UPDATE items_base SET interaction_type = 'fx_box' WHERE item_name LIKE 'fxbox_fx%';

ALTER TABLE `rooms` CHANGE `paper_floor` `paper_floor` VARCHAR(5) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE `rooms` CHANGE `paper_wall` `paper_wall` VARCHAR(5) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE `rooms` CHANGE `paper_landscape` `paper_landscape` VARCHAR(5) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';

ALTER TABLE  `permissions` ADD  `cmd_transform` ENUM(  '0',  '1',  '2' ) NOT NULL DEFAULT  '0' AFTER  `cmd_trash`;
ALTER IGNORE TABLE  `pet_actions` ADD PRIMARY KEY (  `pet_type` );
ALTER TABLE  `pet_actions` CHANGE  `pet_type`  `pet_type` INT( 2 ) NOT NULL AUTO_INCREMENT;
ALTER TABLE  `pet_actions` AUTO_INCREMENT=0;
SET SESSION sql_mode='NO_AUTO_VALUE_ON_ZERO';
INSERT IGNORE INTO `pet_actions` (`pet_type`) VALUES (0), (1), (2), (3), (4), (5), (6), (7), (8), (9), (10), (11), (12), (13), (14), (15), (16), (17), (18), (19), (20), (21), (22), (23), (24), (25), (26), (27), (28), (29), (30), (31), (32), (33), (34), (35);
UPDATE  `pet_actions` SET  `pet_name` =  'Dog' WHERE  `pet_actions`.`pet_type` = 0;
UPDATE  `pet_actions` SET  `pet_name` =  'Cat' WHERE  `pet_actions`.`pet_type` = 1;
UPDATE  `pet_actions` SET  `pet_name` =  'Crocodile' WHERE  `pet_actions`.`pet_type` = 2;
UPDATE  `pet_actions` SET  `pet_name` =  'Terrier' WHERE  `pet_actions`.`pet_type` = 3;
UPDATE  `pet_actions` SET  `pet_name` =  'Bear' WHERE  `pet_actions`.`pet_type` = 4;
UPDATE  `pet_actions` SET  `pet_name` =  'Pig' WHERE  `pet_actions`.`pet_type` = 5;
UPDATE  `pet_actions` SET  `pet_name` =  'Lion' WHERE  `pet_actions`.`pet_type` = 6;
UPDATE  `pet_actions` SET  `pet_name` =  'Rhino' WHERE  `pet_actions`.`pet_type` = 7;
UPDATE  `pet_actions` SET  `pet_name` =  'Spider' WHERE  `pet_actions`.`pet_type` = 8;
UPDATE  `pet_actions` SET  `pet_name` =  'Turtle' WHERE  `pet_actions`.`pet_type` = 9;
UPDATE  `pet_actions` SET  `pet_name` =  'Chicken' WHERE  `pet_actions`.`pet_type` = 10;
UPDATE  `pet_actions` SET  `pet_name` =  'Frog' WHERE  `pet_actions`.`pet_type` = 11;
UPDATE  `pet_actions` SET  `pet_name` =  'Dragon' WHERE  `pet_actions`.`pet_type` = 12;
UPDATE  `pet_actions` SET  `pet_name` =  '' WHERE  `pet_actions`.`pet_type` = 13;
UPDATE  `pet_actions` SET  `pet_name` =  'Monkey' WHERE  `pet_actions`.`pet_type` = 14;
UPDATE  `pet_actions` SET  `pet_name` =  'Horse' WHERE  `pet_actions`.`pet_type` = 15;
UPDATE  `pet_actions` SET  `pet_name` =  'Monsterplant' WHERE  `pet_actions`.`pet_type` = 16;
UPDATE  `pet_actions` SET  `pet_name` =  'Bunny' WHERE  `pet_actions`.`pet_type` = 17;
UPDATE  `pet_actions` SET  `pet_name` =  'Evil Bunny' WHERE  `pet_actions`.`pet_type` = 18;
UPDATE  `pet_actions` SET  `pet_name` =  'Bored Bunny' WHERE  `pet_actions`.`pet_type` = 19;
UPDATE  `pet_actions` SET  `pet_name` =  'Love Bunny' WHERE  `pet_actions`.`pet_type` = 20;
UPDATE  `pet_actions` SET  `pet_name` =  'Wise Pidgeon' WHERE  `pet_actions`.`pet_type` = 21;
UPDATE  `pet_actions` SET  `pet_name` =  'Cunning Pidgeon' WHERE  `pet_actions`.`pet_type` = 22;
UPDATE  `pet_actions` SET  `pet_name` =  'Evil Monkey' WHERE  `pet_actions`.`pet_type` = 23;
UPDATE  `pet_actions` SET  `pet_name` =  'Baby Bear' WHERE  `pet_actions`.`pet_type` = 24;
UPDATE  `pet_actions` SET  `pet_name` =  'Baby Terrier' WHERE  `pet_actions`.`pet_type` = 25;
UPDATE  `pet_actions` SET  `pet_name` =  'Gnome' WHERE  `pet_actions`.`pet_type` = 26;
UPDATE  `pet_actions` SET  `pet_name` =  'Leprechaun' WHERE  `pet_actions`.`pet_type` = 27;
UPDATE  `pet_actions` SET  `pet_name` =  'Baby Cat' WHERE  `pet_actions`.`pet_type` = 28;
UPDATE  `pet_actions` SET  `pet_name` =  'Baby Dog' WHERE  `pet_actions`.`pet_type` = 29;
UPDATE  `pet_actions` SET  `pet_name` =  'Baby Pig' WHERE  `pet_actions`.`pet_type` = 30;
UPDATE  `pet_actions` SET  `pet_name` =  'Haloompa' WHERE  `pet_actions`.`pet_type` = 31;
UPDATE  `pet_actions` SET  `pet_name` =  'Fools' WHERE  `pet_actions`.`pet_type` = 32;
UPDATE  `pet_actions` SET  `pet_name` =  'Pterodactyl' WHERE  `pet_actions`.`pet_type` = 33;
UPDATE  `pet_actions` SET  `pet_name` =  'Velociraptor' WHERE  `pet_actions`.`pet_type` = 34;
UPDATE  `pet_actions` SET  `pet_name` =  'Cow' WHERE  `pet_actions`.`pet_type` = 35;

INSERT INTO `pet_breeds` (`race`, `color_one`, `color_two`, `has_color_one`, `has_color_two`) VALUES ('26', '0', '0', '0', '0'), ('27', '0', '0', '0', '0');

ALTER IGNORE TABLE `emulator_texts` ADD PRIMARY KEY(`key`);
INSERT IGNORE INTO `emulator_texts` (`key`, `value`) VALUES
    ('commands.generic.cmd_transform.title', 'The following pets are available:'),
    ('commands.generic.cmd_transform.line', '%name%'),
    ('commands.description.cmd_transform', ':transform <name> <race> <color>'),
    ('commands.keys.cmd_transform', 'transform;becomepet'),
    ('commands.description.cmd_connect_camera', ':connectcamera'),
    ('commands.description.cmd_roompoints', ':roompoints <amount>'),
    ('commands.description.cmd_setmax', ':setmax <amount>'),
    ('commands.description.cmd_staffalert', ':sa <message>');

ALTER TABLE  `permissions` CHANGE  `cmd_bots`  `cmd_bots` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '1';
ALTER TABLE  `permissions` CHANGE  `cmd_control`  `cmd_control` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '0';
ALTER TABLE  `permissions` CHANGE  `cmd_coords`  `cmd_coords` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '2';
ALTER TABLE  `permissions` CHANGE  `cmd_danceall`  `cmd_danceall` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '0';
ALTER TABLE  `permissions` CHANGE  `cmd_diagonal`  `cmd_diagonal` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '1';
ALTER TABLE  `permissions` CHANGE  `cmd_ejectall`  `cmd_ejectall` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '2';
ALTER TABLE  `permissions` CHANGE  `cmd_enable`  `cmd_enable` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '1';
ALTER TABLE  `permissions` CHANGE  `cmd_fastwalk`  `cmd_fastwalk` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '0';
ALTER TABLE  `permissions` CHANGE  `cmd_freeze_bots`  `cmd_freeze_bots` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '1';
ALTER TABLE  `permissions` CHANGE  `cmd_hand_item`  `cmd_hand_item` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '1';
ALTER TABLE  `permissions` CHANGE  `cmd_kickall`  `cmd_kickall` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '2';
ALTER TABLE  `permissions` CHANGE  `cmd_moonwalk`  `cmd_moonwalk` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '0';
ALTER TABLE  `permissions` CHANGE  `cmd_multi`  `cmd_multi` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '0';
ALTER TABLE  `permissions` CHANGE  `cmd_pet_info`  `cmd_pet_info` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '2';
ALTER TABLE  `permissions` CHANGE  `cmd_pull`  `cmd_pull` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '0';
ALTER TABLE  `permissions` CHANGE  `cmd_push` `cmd_push` ENUM('0','1','2') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE  `permissions` CHANGE  `cmd_roomalert` `cmd_roomalert` ENUM('0','1','2') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE  `permissions` CHANGE  `cmd_roomeffect` `cmd_roomeffect` ENUM('0','1','2') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE  `permissions` CHANGE  `cmd_roomitem` `cmd_roomitem` ENUM('0','1','2') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE  `permissions` CHANGE  `cmd_say` `cmd_say` ENUM('0','1','2') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE  `permissions` CHANGE  `cmd_say_all` `cmd_say_all` ENUM('0','1','2') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE  `permissions` CHANGE  `cmd_setmax` `cmd_setmax` ENUM('0','1','2') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE  `permissions` CHANGE  `cmd_setspeed` `cmd_setspeed` ENUM('0','1','2') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '1';
ALTER TABLE  `permissions` CHANGE  `cmd_shout` `cmd_shout` ENUM('0','1','2') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE  `permissions` CHANGE  `cmd_shout_all` `cmd_shout_all` ENUM('0','1','2') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE  `permissions` CHANGE  `cmd_sitdown` `cmd_sitdown` ENUM('0','1','2') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE  `permissions` CHANGE  `cmd_superpull` `cmd_superpull` ENUM('0','1','2') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE  `permissions` CHANGE  `cmd_teleport` `cmd_teleport` ENUM('0','1','2') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE  `permissions` CHANGE  `cmd_unload` `cmd_unload` ENUM('0','1','2') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE  `permissions` ADD  `cmd_wordquiz` ENUM('0','1','2') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';
ALTER TABLE  `permissions` CHANGE  `acc_unlimited_bots`  `acc_unlimited_bots` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '0' COMMENT  'Overrides the bot restriction to the inventory and room.';
ALTER TABLE  `permissions` CHANGE  `acc_unlimited_pets`  `acc_unlimited_pets` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '0' COMMENT  'Overrides the pet restriction to the inventory and room.';

DROP TABLE support_chatlogs;

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('generic.gift.received.anonymous', 'You''ve received a gift!'), ('generic.gift.received', '%username% gave you a gift!');

ALTER TABLE  `emulator_settings` CHANGE  `value`  `value` VARCHAR( 300 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL;

#When changing the query make sure the look, username, id and hof_points are available.
INSERT INTO `emulator_settings` (`key`,`value`) VALUES ('hotelview.halloffame.query',  'SELECT users.look, users.username, users.id, users_settings.hof_points FROM users_settings INNER JOIN users ON users_settings.user_id = users.id WHERE hof_points > 0 ORDER BY hof_points DESC, users.id ASC LIMIT 10');
#END DATABASE UPDATE: 1.4.0 -> 1.5.0