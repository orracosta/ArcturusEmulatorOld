ALTER TABLE  `permissions` ADD  `cmd_empty_bots` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '1' AFTER  `cmd_empty`;
ALTER TABLE  `permissions` ADD  `cmd_empty_pets` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '1' AFTER  `cmd_empty_bots`;

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
    ('commands.succes.cmd_empty_bots.cleared', 'Bots inventory cleared!'),
    ('commands.succes.cmd_empty_bots.verify', 'This will remove all bots from your inventory. Type :emptybots %generic.yes% to continue!'),
    ('commands.keys.cmd_empty_bots', 'emptybots;empty_bots;deletebots'),
    ('commands.description.cmd_empty_bots', ':emptybots'),
    ('commands.succes.cmd_empty_pets.cleared', 'Pets inventory cleared!'),
    ('commands.succes.cmd_empty_pets.verify', 'This will remove all pets from your inventory. Type :emptypets %generic.yes% to continue!'),
    ('commands.keys.cmd_empty_pets', 'emptypets;empty_pets;deletepets'),
    ('commands.description.cmd_empty_pets', ':emptypets');

CREATE TABLE `users_effects` (
    `user_id` INT NOT NULL,
    `effect` INT(5) NOT NULL,
    `duration` INT NOT NULL DEFAULT '86400',
    `activation_timestamp` INT NOT NULL DEFAULT '-1',
    `total` INT(2) NOT NULL DEFAULT '1',
    UNIQUE(user_id, effect)
) ENGINE = MyISAM;

ALTER TABLE  `users_settings` CHANGE  `talent_track_citizenship_level`  `talent_track_citizenship_level` INT( 2 ) NOT NULL DEFAULT  '-1',
CHANGE  `talent_track_helpers_level`  `talent_track_helpers_level` INT( 2 ) NOT NULL DEFAULT  '-1';
UPDATE users_settings SET talent_track_citizenship_level = -1;
UPDATE users_settings SET talent_track_helpers_level = -1;
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.room.tags.staff', 'staff;official;habbo');
ALTER TABLE  `permissions` ADD  `acc_room_staff_tags` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';

CREATE TABLE  `catalog_club_offers` (
    `id` INT NOT NULL DEFAULT NULL AUTO_INCREMENT PRIMARY KEY ,
    `enabled` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '1',
    `name` VARCHAR( 35 ) NOT NULL ,
    `days` INT( 7 ) NOT NULL ,
    `credits` INT( 5 ) NOT NULL DEFAULT  '10',
    `points` INT( 5 ) NOT NULL DEFAULT  '0',
    `points_type` INT( 3 ) NOT NULL DEFAULT  '0',
    `type` ENUM(  'HC',  'VIP' ) NOT NULL DEFAULT  'HC',
    `deal` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0'
) ENGINE = MYISAM ;

INSERT INTO `catalog_club_offers` (`id`, `enabled`, `name`, `days`, `credits`, `points`, `points_type`, `type`, `deal`) VALUES
    (NULL, '1', 'HABBO_CLUB_7_DAY', '7', '5', '0', '0', 'VIP', '0'),
    (NULL, '1', 'HABBO_CLUB_1_MONTH', '31', '20', '0', '0', 'VIP', '0');

UPDATE catalog_pages SET page_layout = 'vip_buy' WHERE page_layout = 'club_buy';