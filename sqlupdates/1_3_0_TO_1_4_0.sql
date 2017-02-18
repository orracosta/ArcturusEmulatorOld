
ALTER TABLE  `users_pets` ADD  `mp_nose_color` TINYINT( 2 ) NOT NULL DEFAULT  '0' AFTER  `mp_nose`;
ALTER TABLE  `users_pets` ADD  `mp_eyes_color` TINYINT( 2 ) NOT NULL DEFAULT  '0' AFTER  `mp_eyes`;
ALTER TABLE  `users_pets` ADD  `mp_mouth_color` TINYINT( 2 ) NOT NULL DEFAULT  '0' AFTER  `mp_mouth`;
ALTER TABLE  `users_pets` ADD  `mp_death_timestamp` INT NOT NULL DEFAULT  '0' AFTER  `mp_mouth_color`;
ALTER TABLE  `users_pets` ADD  `mp_breedable` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';
ALTER TABLE  `users_pets` ADD  `mp_allow_breed` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('monsterplant.seed.item_id', '1337');
UPDATE emulator_settings SET `value` = (SELECT id FROM items_base WHERE item_name LIKE 'mnstr_seed') WHERE `key` LIKE 'monsterplant.seed.item_id';
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