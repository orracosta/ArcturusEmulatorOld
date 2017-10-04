ALTER TABLE  `support_tickets` ADD  `category` INT( 3 ) NOT NULL DEFAULT  '0' AFTER  `issue`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('supporttools.not_ticket_owner', 'You are not the moderator currently handling the ticket.');
ALTER TABLE  `support_cfh_topics` ADD  `default_sanction` INT( 3 ) NOT NULL DEFAULT  '0' AFTER  `auto_reply`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.success.cmd_setmax', 'Success! Maximum users in this room changed to %value%.');

CREATE TABLE  `calendar_rewards` (
    `id` INT( 11 ) NOT NULL AUTO_INCREMENT ,
    `name` VARCHAR( 128 ) NOT NULL ,
    `custom_image` VARCHAR( 128 ) NOT NULL ,
    `credits` INT( 11 ) NOT NULL DEFAULT  '0',
    `points` INT( 11 ) NOT NULL DEFAULT  '0',
    `points_type` INT( 3 ) NOT NULL DEFAULT  '0',
    `badge` VARCHAR( 25 ) NOT NULL DEFAULT  '',
    `catalog_item_id` INT( 11 ) NOT NULL DEFAULT  '0',
PRIMARY KEY (  `id` )
) ENGINE = MYISAM ;

CREATE TABLE  `calendar_rewards_claimed` (
    `user_id` INT NOT NULL ,
    `reward_id` INT NOT NULL ,
    `timestamp` INT NOT NULL
) ENGINE = MYISAM ;

ALTER TABLE  `permissions` ADD  `cmd_changename` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_bundle`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.description.cmd_changename', ':changename'), ('commands.keys.cmd_changename', 'changename;flagme;change_name;namechange');
ALTER TABLE  `users_settings` ADD  `allow_name_change` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';

CREATE TABLE  `namechange_log` (
    `user_id` INT( 11 ) NOT NULL ,
    `old_name` VARCHAR( 32 ) NOT NULL ,
    `new_name` VARCHAR( 32 ) NOT NULL ,
    `timestamp` INT( 11 ) NOT NULL
) ENGINE = MYISAM ;

INSERT INTO `emulator_settings` (`key`, `value`) VALUES
('basejump.url', 'http://localhost/game/BaseJump.swf'),
('basejump.assets.url', 'http://localhost/gamecenter/gamecenter_basejump/BasicAssets.swf');

DROP INDEX `user_id` ON users_recipes;
ALTER TABLE  `users_recipes` ADD UNIQUE KEY (  `user_id`, `recipe` );

DELETE FROM emulator_settings WHERE `key` LIKE 'hotel.max.bots.inventory';
UPDATE  `emulator_settings` SET  `key` =  'hotel.inventory.max.items' WHERE  `emulator_settings`.`key` =  'inventory.max.items';
INSERT INTO  `emulator_texts` (`key` , `value` ) VALUES
('commands.error.cmd_credits.user_not_found',  'Could net send %amount% credits to %user%. %user% does not exist.');

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.marketplace.currency', '0');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_pull.invalid', 'You cannot pull %username% to there.');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_push.invalid', 'You cannot push %username% to there.');
ALTER TABLE  `marketplace_items` ADD  `sold_timestamp` INT( 11 ) NOT NULL DEFAULT  '0' AFTER  `timestamp`;
UPDATE permissions SET acc_debug = '0';

ALTER TABLE  `permissions` ADD  `level` INT( 2 ) NOT NULL DEFAULT  '1' AFTER  `rank_name`;
UPDATE permissions SET `level` = `id`;
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('networking.tcp.proxy', '0');

CREATE TABLE `room_trax_playlist` (
`room_id` INT NOT NULL ,
`item_id` INT NOT NULL ,
INDEX (  `room_id` )
) ENGINE = INNODB ;

ALTER TABLE  `rooms` ADD  `jukebox_active` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';