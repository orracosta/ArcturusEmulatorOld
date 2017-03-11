#DATABASE UPDATE: 1.3.0 -> 1.4.0

ALTER TABLE  `users_pets` ADD  `mp_nose_color` TINYINT( 2 ) NOT NULL DEFAULT  '0' AFTER  `mp_nose`;
ALTER TABLE  `users_pets` ADD  `mp_eyes_color` TINYINT( 2 ) NOT NULL DEFAULT  '0' AFTER  `mp_eyes`;
ALTER TABLE  `users_pets` ADD  `mp_mouth_color` TINYINT( 2 ) NOT NULL DEFAULT  '0' AFTER  `mp_mouth`;
ALTER TABLE  `users_pets` ADD  `mp_death_timestamp` INT NOT NULL DEFAULT  '0' AFTER  `mp_mouth_color`;
ALTER TABLE  `users_pets` ADD  `mp_breedable` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';
ALTER TABLE  `users_pets` ADD  `mp_allow_breed` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';

#This configuration option defines the monsterplant seed item id.
#Note that it will automatically be set if there is an mnstr_seed item in the items_base table.
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('monsterplant.seed.item_id', '1337');
UPDATE emulator_settings SET `value` = (SELECT id FROM items_base WHERE item_name LIKE 'mnstr_seed') WHERE `key` LIKE 'monsterplant.seed.item_id';

#This configuration option defines the rare monsterplant seed item id.
#Note that it will automatically be set if there is an mnstr_seed_rare item in the items_base table.
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('monsterplant.seed_rare.item_id', '1337');
UPDATE emulator_settings SET `value` = (SELECT id FROM items_base WHERE item_name LIKE 'mnstr_seed_rare') WHERE `key` LIKE 'monsterplant.seed_rare.item_id';

UPDATE `items_base` SET `interaction_type` = 'monsterplant_seed' WHERE `items_base`.`item_name` LIKE 'mnstr_seed%';

INSERT INTO `pet_actions` (`pet_type`, `pet_name`, `happy_actions`, `tired_actions`, `random_actions`) VALUES ('0', '', '', '', '');

ALTER TABLE  `permissions` CHANGE  `cmd_word_quiz`  `cmd_word_quiz` ENUM(  '0',  '1',  '2' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '0';
ALTER TABLE  `permissions` ADD  `cmd_roomalert` ENUM(  '0',  '1',  '2' ) NOT NULL DEFAULT  '0' AFTER  `cmd_redeem`;

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
    ('commands.description.cmd_roomalert', ':roomalert <message>'),
    ('commands.error.cmd_roomalert.empty', 'Please specify an message!'),
    ('commands.keys.cmd_roomalert', 'roomalert;room_alert;ra');

ALTER TABLE  `users_pets` ADD  `gnome_data` VARCHAR( 80 ) NOT NULL DEFAULT  '';

#This configuration option defines whether players will get kicked from public rooms when they walk on the door tile.
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.room.public.doortile.kick', '0'), ('info.shown', '1');;

INSERT INTO `pet_actions` (`pet_type`, `pet_name`, `happy_actions`, `tired_actions`, `random_actions`) VALUES ('33', '', '', '', ''), ('34', '', '', '', '');
INSERT INTO `pet_breeds` (`race`, `color_one`, `color_two`, `has_color_one`, `has_color_two`) VALUES
    ('33', '0', '0', '1', '0'),
    ('33', '1', '1', '1', '0'),
    ('33', '2', '2', '1', '0'),
    ('33', '3', '3', '1', '0'),
    ('33', '4', '4', '1', '0'),
    ('33', '5', '5', '1', '0'),
    ('33', '6', '6', '1', '0'),
    ('33', '7', '7', '1', '0'),
    ('33', '8', '8', '1', '0'),
    ('33', '9', '9', '1', '0'),
    ('33', '10', '10', '1', '0'),
    ('33', '11', '11', '1', '0'),
    ('33', '12', '12', '1', '0'),
    ('33', '13', '13', '1', '0'),
    ('33', '14', '14', '1', '0'),
    ('33', '15', '15', '1', '0'),
    ('33', '16', '16', '1', '0'),
    ('33', '17', '17', '1', '0'),
    ('33', '18', '18', '1', '0'),
    ('33', '19', '19', '1', '0'),
    ('33', '20', '20', '1', '0');

INSERT INTO `pet_breeds` (`race`, `color_one`, `color_two`, `has_color_one`, `has_color_two`) VALUES
    ('34', '0', '0', '1', '0'),
    ('34', '1', '1', '1', '0'),
    ('34', '2', '2', '1', '0'),
    ('34', '3', '3', '1', '0'),
    ('34', '4', '4', '1', '0'),
    ('34', '5', '5', '1', '0'),
    ('34', '6', '6', '1', '0'),
    ('34', '7', '7', '1', '0'),
    ('34', '8', '8', '1', '0'),
    ('34', '9', '9', '1', '0'),
    ('34', '10', '10', '1', '0'),
    ('34', '11', '11', '1', '0'),
    ('34', '12', '12', '1', '0'),
    ('34', '13', '13', '1', '0'),
    ('34', '14', '14', '1', '0'),
    ('34', '15', '15', '1', '0'),
    ('34', '16', '16', '1', '0'),
    ('34', '17', '17', '1', '0'),
    ('34', '18', '18', '1', '0'),
    ('34', '19', '19', '1', '0');

DELETE FROM emulator_texts WHERE `key` LIKE 'commands.keys.cmd_roompoints';
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_roompoints', 'roompoints;room_points');

DROP TABLE camera_web;
CREATE TABLE `camera_web` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `timestamp` INT NOT NULL,
    `url` VARCHAR(128) NOT NULL DEFAULT '',
    PRIMARY KEY (`id`),
    INDEX (`user_id`),
    UNIQUE (`id`)
) ENGINE = INNODB;

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('camera.publish.delay', '180');

#END DATABASE UPDATE: 1.3.0 -> 1.4.0